package com.example.despensacuartel.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.despensacuartel.data.model.Category

object CategoryIcons {
    private val icons: Map<Category, ImageVector> = mapOf(
        Category.FRUTAS to Icons.Default.Eco,
        Category.CARNES to Icons.Default.Restaurant,
        Category.PAN to Icons.Default.BakeryDining,
        Category.CAFE to Icons.Default.Coffee,
        Category.LACTEOS to Icons.Default.LocalDrink,
        Category.MEDICAMENTOS to Icons.Default.Medication,
        Category.CERVEZA to Icons.Default.SportsBar,
        Category.VERDURAS to Icons.Default.Grass
    )

    fun getIcon(category: Category): ImageVector? = icons[category]

    fun getIconOrEmoji(category: Category): Any = icons[category] ?: category.emoji
}
