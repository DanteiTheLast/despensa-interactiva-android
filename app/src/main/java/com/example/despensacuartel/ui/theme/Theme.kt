package com.example.despensacuartel.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimary = AppColors.OnPrimary,
    background = AppColors.Background,
    surface = AppColors.Surface,
    onBackground = AppColors.OnSurface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.Surface,
    onSurfaceVariant = AppColors.OnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimary = AppColors.OnPrimary,
    background = AppColors.OnSurface,
    surface = AppColors.OnSurface,
    onBackground = AppColors.Background,
    onSurface = AppColors.Background,
    onSurfaceVariant = AppColors.Background
)

@Composable
fun DespensaCuartelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
