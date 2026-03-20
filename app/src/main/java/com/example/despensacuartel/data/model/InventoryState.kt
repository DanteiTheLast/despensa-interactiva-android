package com.example.despensacuartel.data.model

import androidx.compose.ui.graphics.Color

data class InventoryItem(
    val id: String = "",
    val nombre: String = "",
    val categoriaID: String = "",
    val cantidadActual: Int = 0,
    val cantidadMaxima: Int = 10,
    val unidad: String = "",
    val tipoIcon: String = "default",
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
        is SectionColor.Empty -> listOf(
            Color(0xFF4B5563),  // Gris elegante
            Color(0xFF6B7280),
            Color(0xFF4B5563),
            Color(0xFF4B5563)
        )
        is SectionColor.VeryLow -> listOf(
            Color(0xFFEF4444),  // Rojo coral vibrante
            Color(0xFFDC2626),
            Color(0xFFEF4444),
            Color(0xFFEF4444)
        )
        is SectionColor.Low -> listOf(
            Color(0xFFF97316),  // Naranja vibrante
            Color(0xFFEA580C),
            Color(0xFFEF4444),  // Rojo en la punta
            Color(0xFFF97316)
        )
        is SectionColor.Medium -> listOf(
            Color(0xFFF59E0B),  // Amber cálido
            Color(0xFFD97706),
            Color(0xFFF97316), // Naranja en la punta
            Color(0xFFF59E0B)
        )
        is SectionColor.Full -> listOf(
            Color(0xFF10B981),  // Emerald brillante
            Color(0xFF059669),
            Color(0xFF10B981),
            Color(0xFF10B981)
        )
    }

    fun toPrimaryColor(): Color = when (this) {
        is SectionColor.Empty -> Color(0xFF4B5563)
        is SectionColor.VeryLow -> Color(0xFFEF4444)
        is SectionColor.Low -> Color(0xFFF97316)
        is SectionColor.Medium -> Color(0xFFF59E0B)
        is SectionColor.Full -> Color(0xFF10B981)
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
