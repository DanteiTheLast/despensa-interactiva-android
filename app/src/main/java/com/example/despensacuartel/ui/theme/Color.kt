package com.example.despensacuartel.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // ========== LIGHT MODE ==========
    
    // Status colors (para la rueda) - Vibrantes y modernos
    val StatusFull = Color(0xFF10B981)        // Emerald
    val StatusMedium = Color(0xFFF59E0B)       // Amber cálido
    val StatusLow = Color(0xFFF97316)          // Naranja vibrante
    val StatusVeryLow = Color(0xFFEF4444)       // Rojo coral
    val StatusEmpty = Color(0xFF6B7280)         // Gris elegante

    // Glow colors (versiones más claras para efectos)
    val StatusFullGlow = Color(0xFF34D399)
    val StatusMediumGlow = Color(0xFFFBBF24)
    val StatusLowGlow = Color(0xFFFB923C)
    val StatusVeryLowGlow = Color(0xFFF87171)
    val StatusEmptyGlow = Color(0xFF9CA3AF)

    // Light Background - Fresh Green Palette
    val Background = Color(0xFFF0FDF4)         // Verde muy claro
    val BackgroundEnd = Color(0xFFECFDF5)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFE2E8F0)
    val OnSurface = Color(0xFF0F172A)           // Slate 900
    val OnSurfaceVariant = Color(0xFF64748B)     // Slate 500

    // Primary - Fresh Green (food/pantry theme)
    val Primary = Color(0xFF059669)             // Emerald 600
    val PrimaryContainer = Color(0xFFA7F3D0)    // Emerald 200
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFF064E3B)   // Emerald 900

    // Secondary - Warm Amber
    val Secondary = Color(0xFFD97706)           // Amber 600
    val SecondaryContainer = Color(0xFFFDE68A)  // Amber 200
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFF78350F) // Amber 900

    // Buttons
    val ButtonPositive = Color(0xFF10B981)      // Emerald
    val ButtonPositiveContainer = Color(0xFFD1FAE5)
    val ButtonNegative = Color(0xFFDC2626)       // Red
    val ButtonNegativeContainer = Color(0xFFFEE2E2)

    // Progress Track
    val ProgressTrack = Color(0xFFE2E8F0)       // Slate 200

    // Card
    val CardBackground = Color(0xFFFFFFFF)
    
    // Outline
    val Outline = Color(0xFFCBD5E1)             // Slate 300
    val OutlineDark = Color(0xFF94A3B8)          // Slate 400
    val OutlineVariant = Color(0xFFF1F5F9)       // Slate 100

    // Error
    val Error = Color(0xFFDC2626)               // Red 600
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFEE2E2)

    // ========== DARK MODE ==========
    
    // Dark Backgrounds
    val DarkBackground = Color(0xFF052E16)       // Emerald 950
    val DarkSurface = Color(0xFF0F172A)          // Slate 900
    val DarkSurfaceVariant = Color(0xFF1E293B)   // Slate 800
    val DarkOnSurface = Color(0xFFE2E8F0)         // Slate 200
    val DarkOnSurfaceVariant = Color(0xFF94A3B8) // Slate 400

    // Dark Primary (más brillante para contraste)
    val DarkPrimary = Color(0xFF34D399)           // Emerald 400
    val DarkPrimaryContainer = Color(0xFF064E3B) // Emerald 900
    val DarkOnPrimary = Color(0xFF003322)
    val DarkOnPrimaryContainer = Color(0xFFA7F3D0) // Emerald 200

    // Dark Secondary
    val DarkSecondary = Color(0xFFFBBF24)        // Amber 400
    val DarkSecondaryContainer = Color(0xFF78350F) // Amber 900
    val DarkOnSecondary = Color(0xFF422006)
    val DarkOnSecondaryContainer = Color(0xFFFDE68A) // Amber 200

    // Dark Error
    val DarkError = Color(0xFFF87171)            // Red 400
    val DarkOnError = Color(0xFF450A0A)          // Red 950
    val DarkErrorContainer = Color(0xFF7F1D1D)   // Red 900

    // Dark Outline
    val DarkOutline = Color(0xFF334155)          // Slate 700
    val DarkOutlineVariant = Color(0xFF1E293B)   // Slate 800

    // Dark Card
    val DarkCardBackground = Color(0xFF1E293B)   // Slate 800

    // Dark Status colors (para la rueda)
    val DarkStatusFull = Color(0xFF34D399)       // Emerald 400
    val DarkStatusMedium = Color(0xFFFBBF24)     // Amber 400
    val DarkStatusLow = Color(0xFFFB923C)        // Orange 400
    val DarkStatusVeryLow = Color(0xFFF87171)   // Red 400
    val DarkStatusEmpty = Color(0xFF64748B)      // Slate 500
}
