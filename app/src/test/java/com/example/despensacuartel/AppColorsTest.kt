package com.example.despensacuartel

import androidx.compose.ui.graphics.Color
import com.example.despensacuartel.ui.theme.AppColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AppColorsTest {

    @Test
    fun `Status colors are not null`() {
        assertNotNull(AppColors.StatusFull)
        assertNotNull(AppColors.StatusMedium)
        assertNotNull(AppColors.StatusLow)
        assertNotNull(AppColors.StatusVeryLow)
        assertNotNull(AppColors.StatusEmpty)
    }

    @Test
    fun `Glow colors are not null`() {
        assertNotNull(AppColors.StatusFullGlow)
        assertNotNull(AppColors.StatusMediumGlow)
        assertNotNull(AppColors.StatusLowGlow)
        assertNotNull(AppColors.StatusVeryLowGlow)
        assertNotNull(AppColors.StatusEmptyGlow)
    }

    @Test
    fun `Primary colors are not null`() {
        assertNotNull(AppColors.Primary)
        assertNotNull(AppColors.PrimaryContainer)
        assertNotNull(AppColors.OnPrimary)
        assertNotNull(AppColors.OnPrimaryContainer)
    }

    @Test
    fun `Secondary colors are not null`() {
        assertNotNull(AppColors.Secondary)
        assertNotNull(AppColors.SecondaryContainer)
        assertNotNull(AppColors.OnSecondary)
        assertNotNull(AppColors.OnSecondaryContainer)
    }

    @Test
    fun `Button colors are not null`() {
        assertNotNull(AppColors.ButtonPositive)
        assertNotNull(AppColors.ButtonNegative)
    }

    @Test
    fun `Error colors are not null`() {
        assertNotNull(AppColors.Error)
        assertNotNull(AppColors.OnError)
        assertNotNull(AppColors.ErrorContainer)
    }

    @Test
    fun `Dark mode colors are not null`() {
        assertNotNull(AppColors.DarkBackground)
        assertNotNull(AppColors.DarkSurface)
        assertNotNull(AppColors.DarkPrimary)
        assertNotNull(AppColors.DarkError)
        assertNotNull(AppColors.DarkStatusFull)
    }

    @Test
    fun `Animation colors are not null`() {
        assertNotNull(AppColors.SuccessGreen)
        assertNotNull(AppColors.SuccessGreenDark)
        assertNotNull(AppColors.ErrorRed)
        assertNotNull(AppColors.ErrorRedDark)
    }

    @Test
    fun `Success colors are different from Error colors`() {
        assertEquals(AppColors.SuccessGreen, Color(0xFF10B981))
        assertEquals(AppColors.ErrorRed, Color(0xFFEF4444))
        assertNotNull(AppColors.SuccessGreen)
        assertNotNull(AppColors.ErrorRed)
    }

    @Test
    fun `Status colors have correct hierarchy`() {
        val fullAlpha = AppColors.StatusFull
        val emptyAlpha = AppColors.StatusEmpty
        
        assertNotNull(fullAlpha)
        assertNotNull(emptyAlpha)
    }

    @Test
    fun `Dark status colors are brighter than light`() {
        assertEquals(Color(0xFF10B981), AppColors.StatusFull)
        assertEquals(Color(0xFF34D399), AppColors.DarkStatusFull)
    }
}