package com.example.despensacuartel.data.model

import androidx.compose.ui.graphics.Color

data class InventoryItem(
    val id: String = "",
    val nombre: String = "",
    val categoriaID: String = "",
    val cantidadActual: Int = 0,
    val cantidadMaxima: Int = 10,
    val unidad: String = "",
    val actualizadoPor: String = "",
    val fechaActualizacion: Long = System.currentTimeMillis()
) {
    val fillPercentage: Float
        get() = if (cantidadMaxima > 0) cantidadActual.toFloat() / cantidadMaxima else 0f
}

enum class Category(val id: String, val displayName: String, val angle: Float, val emoji: String) {
    FRUTAS("frutas", "Frutas", 270f, "🍌"),
    CARNES("carnes", "Carnes", 315f, "🥩"),
    PAN("pan", "Pan", 0f, "🍞"),
    CAFE("cafe", "Café", 45f, "☕"),
    LACTEOS("lacteos", "Lácteos", 90f, "🥛"),
    MEDICAMENTOS("medicamentos", "Medicamentos", 135f, "💊"),
    CERVEZA("cerveza", "Cerveza", 180f, "🍺"),
    VERDURAS("verduras", "Verduras", 225f, "🥕");

    companion object {
        fun fromId(id: String): Category? = entries.find { it.id == id }
    }
}

sealed class SectionColor {
    data object Empty : SectionColor()
    data object VeryLow : SectionColor()
    data object Low : SectionColor()
    data object Medium : SectionColor()
    data object Full : SectionColor()

    fun toColors(): List<Color> = when (this) {
        is SectionColor.Empty -> listOf(Color(0xFF3D3D3D), Color(0xFF3D3D3D), Color(0xFF3D3D3D), Color(0xFF3D3D3D))
        is SectionColor.VeryLow -> listOf(Color(0xFFE24A4A), Color(0xFFE24A4A), Color(0xFFE24A4A), Color(0xFFE24A4A))
        is SectionColor.Low -> listOf(Color(0xFFFF9800), Color(0xFFFF9800), Color(0xFFFF9800), Color(0xFFE24A4A))
        is SectionColor.Medium -> listOf(Color(0xFFFFEB3B), Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFFE24A4A))
        is SectionColor.Full -> listOf(Color(0xFF4AE24A), Color(0xFF4AE24A), Color(0xFF4AE24A), Color(0xFF4AE24A))
    }

    companion object {
        fun fromFillPercentage(percentage: Float): SectionColor = when {
            percentage <= 0f -> Empty
            percentage < 0.2f -> VeryLow
            percentage < 0.4f -> Low
            percentage < 0.7f -> Medium
            else -> Full
        }
    }
}

data class CategorySummary(
    val category: Category,
    val items: List<InventoryItem>,
    val totalQuantity: Int,
    val maxCapacity: Int,
    val fillPercentage: Float,
    val sectionColor: SectionColor
) {
    companion object {
        fun fromItems(category: Category, items: List<InventoryItem>): CategorySummary {
            val totalQuantity = items.sumOf { it.cantidadActual }
            val maxCapacity = items.sumOf { it.cantidadMaxima }
            val fillPercentage = if (maxCapacity > 0) totalQuantity.toFloat() / maxCapacity else 0f
            return CategorySummary(
                category = category,
                items = items,
                totalQuantity = totalQuantity,
                maxCapacity = maxCapacity,
                fillPercentage = fillPercentage,
                sectionColor = SectionColor.fromFillPercentage(fillPercentage)
            )
        }
    }
}
