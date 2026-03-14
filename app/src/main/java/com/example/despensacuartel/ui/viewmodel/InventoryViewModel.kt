package com.example.despensacuartel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.CategorySummary
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.model.SectionColor
import com.example.despensacuartel.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val isLoading: Boolean = true,
    val items: List<InventoryItem> = emptyList(),
    val categorySummaries: List<CategorySummary> = emptyList(),
    val error: String? = null
)

class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

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

    fun getSectionColors(): Map<Category, List<SectionColor>> {
        val colors = mutableMapOf<Category, List<SectionColor>>()
        Category.entries.forEach { category ->
            val summary = _uiState.value.categorySummaries.find { it.category == category }
            val color = summary?.sectionColor ?: SectionColor.Empty
            colors[category] = listOf(color, color, color, color)
        }
        return colors
    }

    fun getItemsByCategory(categoriaID: String): List<InventoryItem> {
        return _uiState.value.items.filter { it.categoriaID == categoriaID }
    }

    fun getItemById(itemId: String): InventoryItem? {
        return _uiState.value.items.find { it.id == itemId }
    }

    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        val currentItems = _uiState.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val updatedItem = currentItems[index].copy(
                cantidadActual = newQuantity.coerceIn(0, currentItems[index].cantidadMaxima),
                fechaActualizacion = System.currentTimeMillis()
            )
            currentItems[index] = updatedItem
            
            val summaries = repository.getCategorySummaries(currentItems)
            _uiState.update {
                it.copy(
                    items = currentItems,
                    categorySummaries = summaries
                )
            }
        }
    }

    class Factory(private val useDummyData: Boolean = true) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(InventoryRepository(useDummyData)) as T
        }
    }
}
