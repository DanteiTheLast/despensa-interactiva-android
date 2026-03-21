package com.example.despensacuartel

import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.CategorySummary
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.model.SectionColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class InventoryStateTest {

    @Test
    fun `Category fromId returns correct category`() {
        assertEquals(Category.FRUTAS, Category.fromId("frutas"))
        assertEquals(Category.CARNES, Category.fromId("carnes"))
        assertEquals(Category.PAN, Category.fromId("pan"))
        assertEquals(Category.CAFE, Category.fromId("cafe"))
        assertEquals(Category.LACTEOS, Category.fromId("lacteos"))
        assertEquals(Category.MEDICAMENTOS, Category.fromId("medicamentos"))
        assertEquals(Category.CERVEZA, Category.fromId("cerveza"))
        assertEquals(Category.VERDURAS, Category.fromId("verduras"))
    }

    @Test
    fun `Category fromId returns null for invalid id`() {
        assertNull(Category.fromId("invalid"))
        assertNull(Category.fromId(""))
        assertNull(Category.fromId("FRUTAS"))
    }

    @Test
    fun `Category fromId is case sensitive`() {
        assertNull(Category.fromId("Frutas"))
        assertNull(Category.fromId("FRUTAS"))
        assertNull(Category.fromId("carnes"))
    }

    @Test
    fun `Category entries contains all 8 categories`() {
        assertEquals(8, Category.entries.size)
    }

    @Test
    fun `InventoryItem fillPercentage calculates correctly`() {
        val item = InventoryItem(
            cantidadActual = 5,
            cantidadMaxima = 10
        )
        assertEquals(0.5f, item.fillPercentage, 0.001f)
    }

    @Test
    fun `InventoryItem fillPercentage returns zero when max is zero`() {
        val item = InventoryItem(
            cantidadActual = 5,
            cantidadMaxima = 0
        )
        assertEquals(0f, item.fillPercentage, 0.001f)
    }

    @Test
    fun `InventoryItem fillPercentage returns zero when empty`() {
        val item = InventoryItem(
            cantidadActual = 0,
            cantidadMaxima = 10
        )
        assertEquals(0f, item.fillPercentage, 0.001f)
    }

    @Test
    fun `InventoryItem fillPercentage returns one when full`() {
        val item = InventoryItem(
            cantidadActual = 10,
            cantidadMaxima = 10
        )
        assertEquals(1f, item.fillPercentage, 0.001f)
    }

    @Test
    fun `SectionColor fromFillPercentage returns Empty for zero`() {
        assertEquals(SectionColor.Empty, SectionColor.fromFillPercentage(0f))
    }

    @Test
    fun `SectionColor fromFillPercentage returns VeryLow for low percentages`() {
        assertEquals(SectionColor.VeryLow, SectionColor.fromFillPercentage(0.1f))
        assertEquals(SectionColor.VeryLow, SectionColor.fromFillPercentage(0.19f))
    }

    @Test
    fun `SectionColor fromFillPercentage returns Low for medium-low percentages`() {
        assertEquals(SectionColor.Low, SectionColor.fromFillPercentage(0.2f))
        assertEquals(SectionColor.Low, SectionColor.fromFillPercentage(0.39f))
    }

    @Test
    fun `SectionColor fromFillPercentage returns Medium for medium percentages`() {
        assertEquals(SectionColor.Medium, SectionColor.fromFillPercentage(0.4f))
        assertEquals(SectionColor.Medium, SectionColor.fromFillPercentage(0.69f))
    }

    @Test
    fun `SectionColor fromFillPercentage returns Full for high percentages`() {
        assertEquals(SectionColor.Full, SectionColor.fromFillPercentage(0.7f))
        assertEquals(SectionColor.Full, SectionColor.fromFillPercentage(1f))
    }

    @Test
    fun `SectionColor toColors returns list of 4 colors`() {
        val colors = SectionColor.Full.toColors()
        assertEquals(4, colors.size)
    }

    @Test
    fun `SectionColor toPrimaryColor returns correct color`() {
        assertEquals(
            androidx.compose.ui.graphics.Color(0xFF10B981),
            SectionColor.Full.toPrimaryColor()
        )
        assertEquals(
            androidx.compose.ui.graphics.Color(0xFFEF4444),
            SectionColor.VeryLow.toPrimaryColor()
        )
    }

    @Test
    fun `CategorySummary fromItems calculates correctly`() {
        val items = listOf(
            InventoryItem(cantidadActual = 5, cantidadMaxima = 10),
            InventoryItem(cantidadActual = 3, cantidadMaxima = 5)
        )
        
        val summary = CategorySummary.fromItems(Category.FRUTAS, items)
        
        assertEquals(Category.FRUTAS, summary.category)
        assertEquals(2, summary.items.size)
        assertEquals(8, summary.totalQuantity)
        assertEquals(15, summary.maxCapacity)
        assertEquals(SectionColor.Medium, summary.sectionColor)
    }

    @Test
    fun `CategorySummary fromItems with empty list`() {
        val summary = CategorySummary.fromItems(Category.FRUTAS, emptyList())
        
        assertEquals(0, summary.totalQuantity)
        assertEquals(0, summary.maxCapacity)
        assertEquals(0f, summary.fillPercentage, 0.001f)
        assertEquals(SectionColor.Empty, summary.sectionColor)
    }

    @Test
    fun `CategorySummary fromItems with all full items`() {
        val items = listOf(
            InventoryItem(cantidadActual = 10, cantidadMaxima = 10),
            InventoryItem(cantidadActual = 5, cantidadMaxima = 5)
        )
        
        val summary = CategorySummary.fromItems(Category.CERVEZA, items)
        
        assertEquals(1f, summary.fillPercentage, 0.001f)
        assertEquals(SectionColor.Full, summary.sectionColor)
    }

    @Test
    fun `CategorySummary fromItems with all empty items`() {
        val items = listOf(
            InventoryItem(cantidadActual = 0, cantidadMaxima = 10),
            InventoryItem(cantidadActual = 0, cantidadMaxima = 5)
        )
        
        val summary = CategorySummary.fromItems(Category.VERDURAS, items)
        
        assertEquals(0f, summary.fillPercentage, 0.001f)
        assertEquals(SectionColor.Empty, summary.sectionColor)
    }
}