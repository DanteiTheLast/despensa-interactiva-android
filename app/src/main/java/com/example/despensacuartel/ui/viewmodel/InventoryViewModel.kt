package com.example.despensacuartel.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.CategorySummary
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.model.SectionColor
import com.example.despensacuartel.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val items: List<InventoryItem> = emptyList(),
    val categorySummaries: List<CategorySummary> = emptyList(),
    val error: String? = null,
    val syncResult: String? = null
)

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val initialSectionColors: Map<Category, List<SectionColor>> =
        Category.entries.associateWith { listOf(SectionColor.Empty, SectionColor.Empty, SectionColor.Empty, SectionColor.Empty) }

    val sectionColors: StateFlow<Map<Category, List<SectionColor>>>
        get() = _uiState.map { state ->
            Category.entries.associateWith { category ->
                val summary = state.categorySummaries.find { it.category == category }
                val color = summary?.sectionColor ?: SectionColor.Empty
                listOf(color, color, color, color)
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialSectionColors)

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getInventoryStream().collect { items ->
                    val summaries = repository.getCategorySummaries(items)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            categorySummaries = summaries
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun getItemsByCategory(categoriaID: String): List<InventoryItem> {
        return _uiState.value.items.filter { it.categoriaID == categoriaID }
    }

    fun getItemById(itemId: String): InventoryItem? {
        return _uiState.value.items.find { it.id == itemId }
    }

    fun syncToFirestore() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncResult = null) }
            repository.syncToFirestore()
                .onSuccess { count ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncResult = "$count productos sincronizados"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncResult = "Error: ${error.message}"
                        )
                    }
                }
        }
    }

    fun clearSyncResult() {
        _uiState.update { it.copy(syncResult = null) }
    }

    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val index = currentState.items.indexOfFirst { it.id == itemId }
            if (index != -1) {
                val updatedItem = currentState.items[index].copy(
                    cantidadActual = newQuantity.coerceIn(0, currentState.items[index].cantidadMaxima),
                    fechaActualizacion = System.currentTimeMillis()
                )
                val newItems = currentState.items.mapIndexed { i, item -> if (i == index) updatedItem else item }
                val newSummaries = repository.getCategorySummaries(newItems)
                _uiState.update {
                    it.copy(items = newItems, categorySummaries = newSummaries)
                }
                repository.updateItemInFirestore(updatedItem)
                    .onSuccess {
                        Log.d("VIEWMODEL", "Item updated in Firestore")
                    }
                    .onFailure { error ->
                        Log.e("VIEWMODEL", "Error updating Firestore: ${error.message}")
                    }
            }
        }
    }

    class Factory(private val useDummyData: Boolean = false) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(InventoryRepository(useDummyData)) as T
        }
    }
}