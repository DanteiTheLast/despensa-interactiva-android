package com.example.despensacuartel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddProductFormState(
    val nombre: String = "",
    val categoriaID: String = "",
    val cantidadMaxima: String = "",
    val unidad: String = "pcs",
    val tipoIcon: String = "eco",
    val nombreError: String? = null,
    val cantidadMaxError: String? = null,
    val isSaving: Boolean = false,
    val saveResult: SaveResult = SaveResult.Idle
)

sealed class SaveResult {
    data object Idle : SaveResult()
    data object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class AddProductViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(AddProductFormState())
    val formState: StateFlow<AddProductFormState> = _formState.asStateFlow()

    fun updateNombre(value: String) {
        _formState.update { it.copy(nombre = value, nombreError = null) }
    }

    fun updateCategoria(value: String) {
        _formState.update { it.copy(categoriaID = value) }
    }

    fun updateCantidadMax(value: String) {
        _formState.update { it.copy(cantidadMaxima = value, cantidadMaxError = null) }
    }

    fun updateUnidad(value: String) {
        _formState.update { it.copy(unidad = value) }
    }

    fun updateTipoIcon(value: String) {
        _formState.update { it.copy(tipoIcon = value) }
    }

    fun saveProduct() {
        val current = _formState.value

        val validationResult = validateForm(current)
        _formState.update { validationResult.state }

        if (validationResult.hasError) {
            _formState.update { it.copy(saveResult = SaveResult.Error("Corrige los errores")) }
            return
        }

        val cantidadMax = validationResult.cantidadMax
        
        _formState.update { it.copy(isSaving = true, saveResult = SaveResult.Idle) }
        
        viewModelScope.launch {
            try {
                val newItem = InventoryItem(
                    nombre = current.nombre.trim(),
                    categoriaID = current.categoriaID,
                    cantidadActual = 0,
                    cantidadMaxima = cantidadMax!!,
                    unidad = current.unidad,
                    tipoIcon = current.tipoIcon,
                    actualizadoPor = "local",
                    fechaActualizacion = System.currentTimeMillis()
                )
                
                val success = repository.addItemToFirestore(newItem)
                if (success) {
                    _formState.update { 
                        it.copy(
                            isSaving = false,
                            saveResult = SaveResult.Success
                        )
                    }
                } else {
                    _formState.update { 
                        it.copy(
                            isSaving = false,
                            saveResult = SaveResult.Error("Error al guardar")
                        )
                    }
                }
            } catch (e: Exception) {
                _formState.update { 
                    it.copy(
                        isSaving = false,
                        saveResult = SaveResult.Error(e.message ?: "Error desconocido")
                    )
                }
            }
        }
    }

    fun resetForm() {
        _formState.update { AddProductFormState() }
    }

    fun clearSaveResult() {
        _formState.update { it.copy(saveResult = SaveResult.Idle) }
    }

    private fun validateForm(state: AddProductFormState): ValidationResult {
        var newState = state
        var hasError = false

        if (state.nombre.isBlank()) {
            newState = newState.copy(nombreError = "El nombre es requerido")
            hasError = true
        }

        val cantidadMax = state.cantidadMaxima.toIntOrNull()
        if (cantidadMax == null || cantidadMax <= 0) {
            newState = newState.copy(cantidadMaxError = "Debe ser mayor a 0")
            hasError = true
        }

        return ValidationResult(hasError, newState, cantidadMax ?: 0)
    }

    private data class ValidationResult(
        val hasError: Boolean,
        val state: AddProductFormState,
        val cantidadMax: Int
    )

    class Factory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddProductViewModel(repository) as T
        }
    }
}
