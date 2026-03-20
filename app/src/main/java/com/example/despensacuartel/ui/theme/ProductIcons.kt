package com.example.despensacuartel.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.ui.graphics.vector.ImageVector

data class ProductIconItem(
    val id: String,
    val emoji: String,
    val vector: ImageVector,
    val displayName: String
)

object ProductIcons {
    val availableIcons: List<ProductIconItem> = listOf(
        ProductIconItem(
            id = "eco",
            emoji = "🌿",
            vector = Icons.Default.Eco,
            displayName = "Frutas"
        ),
        ProductIconItem(
            id = "restaurant",
            emoji = "🍖",
            vector = Icons.Default.Restaurant,
            displayName = "Carnes"
        ),
        ProductIconItem(
            id = "bakery",
            emoji = "🍞",
            vector = Icons.Default.BakeryDining,
            displayName = "Pan"
        ),
        ProductIconItem(
            id = "coffee",
            emoji = "☕",
            vector = Icons.Default.Coffee,
            displayName = "Café"
        ),
        ProductIconItem(
            id = "drink",
            emoji = "🥛",
            vector = Icons.Default.LocalDrink,
            displayName = "Lácteos"
        ),
        ProductIconItem(
            id = "medication",
            emoji = "💊",
            vector = Icons.Default.Medication,
            displayName = "Medicinas"
        ),
        ProductIconItem(
            id = "beer",
            emoji = "🍺",
            vector = Icons.Default.SportsBar,
            displayName = "Cerveza"
        ),
        ProductIconItem(
            id = "grass",
            emoji = "🌱",
            vector = Icons.Default.Grass,
            displayName = "Verduras"
        ),
        ProductIconItem(
            id = "canned",
            emoji = "🥫",
            vector = Icons.Default.Inventory2,
            displayName = "Enlatados"
        )
    )

    private val iconsById: Map<String, ProductIconItem> = availableIcons.associateBy { it.id }

    fun getIconById(id: String): ProductIconItem? = iconsById[id]

    fun getVectorById(id: String): ImageVector? = iconsById[id]?.vector

    fun getEmojiById(id: String): String? = iconsById[id]?.emoji
}