package com.example.despensacuartel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Inventory2
import com.example.despensacuartel.ui.theme.ProductIconItem
import com.example.despensacuartel.ui.theme.ProductIcons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProductIconsTest {

    @Test
    fun `availableIcons contains expected number of icons`() {
        assertEquals(9, ProductIcons.availableIcons.size)
    }

    @Test
    fun `getIconById returns correct icon for category`() {
        val frutasIcon = ProductIcons.getIconById("frutas")
        assertNotNull(frutasIcon)
        assertEquals("Frutas", frutasIcon?.displayName)
        assertEquals("🌿", frutasIcon?.emoji)
    }

    @Test
    fun `getIconById returns correct icon for canned`() {
        val cannedIcon = ProductIcons.getIconById("canned")
        assertNotNull(cannedIcon)
        assertEquals("Enlatados", cannedIcon?.displayName)
        assertEquals("🥫", cannedIcon?.emoji)
    }

    @Test
    fun `getIconById returns null for invalid id`() {
        assertNull(ProductIcons.getIconById("invalid"))
        assertNull(ProductIcons.getIconById(""))
        assertNull(ProductIcons.getIconById("frutas_extra"))
    }

    @Test
    fun `getVectorById returns correct vector`() {
        val vector = ProductIcons.getVectorById("frutas")
        assertNotNull(vector)
        assertEquals(Icons.Default.Eco, vector)
    }

    @Test
    fun `getVectorById returns null for invalid id`() {
        assertNull(ProductIcons.getVectorById("invalid"))
    }

    @Test
    fun `getEmojiById returns correct emoji`() {
        assertEquals("🌿", ProductIcons.getEmojiById("frutas"))
        assertEquals("🥫", ProductIcons.getEmojiById("canned"))
    }

    @Test
    fun `getEmojiById returns null for invalid id`() {
        assertNull(ProductIcons.getEmojiById("invalid"))
    }

    @Test
    fun `all icons have unique ids`() {
        val ids = ProductIcons.availableIcons.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `all icons have non-empty values`() {
        ProductIcons.availableIcons.forEach { icon ->
            assertNotNull(icon.id)
            assertNotNull(icon.emoji)
            assertNotNull(icon.vector)
            assertNotNull(icon.displayName)
            assert(icon.id.isNotEmpty())
            assert(icon.emoji.isNotEmpty())
            assert(icon.displayName.isNotEmpty())
        }
    }

    @Test
    fun `canned icon uses correct vector`() {
        val cannedIcon = ProductIcons.getIconById("canned")
        assertNotNull(cannedIcon)
        assertEquals(Icons.Default.Inventory2, cannedIcon?.vector)
    }
}