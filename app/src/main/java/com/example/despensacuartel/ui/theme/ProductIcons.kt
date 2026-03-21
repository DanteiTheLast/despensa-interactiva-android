package com.example.despensacuartel.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.despensacuartel.data.model.Category

data class ProductIconItem(
    val id: String,
    val emoji: String,
    val vector: ImageVector,
    val displayName: String
)

object ProductIcons {
    private val categoryIconIds: Map<Category, String> = mapOf(
        Category.FRUTAS to "frutas",
        Category.CARNES to "carnes",
        Category.PAN to "pan",
        Category.CAFE to "cafe",
        Category.LACTEOS to "lacteos",
        Category.MEDICAMENTOS to "medicamentos",
        Category.CERVEZA to "cerveza",
        Category.VERDURAS to "verduras"
    )

    val availableIcons: List<ProductIconItem> = buildList {
        Category.entries.forEach { category ->
            CategoryIcons.getIcon(category)?.let { vector ->
                add(
                    ProductIconItem(
                        id = categoryIconIds[category] ?: category.id,
                        emoji = category.emoji,
                        vector = vector,
                        displayName = category.displayName
                    )
                )
            }
        }
        add(
            ProductIconItem(
                id = "canned",
                emoji = "🥫",
                vector = Icons.Default.Inventory2,
                displayName = "Enlatados"
            )
        )
    }

    private val iconsById: Map<String, ProductIconItem> = availableIcons.associateBy { it.id }

    fun getIconById(id: String): ProductIconItem? = iconsById[id]

    fun getVectorById(id: String): ImageVector? = iconsById[id]?.vector

    fun getEmojiById(id: String): String? = iconsById[id]?.emoji
}