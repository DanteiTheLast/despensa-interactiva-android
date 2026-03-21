package com.example.despensacuartel.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import com.example.despensacuartel.ui.theme.AppColors

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val velocityX: Float,
    val velocityY: Float
)

@Composable
fun SuccessAnimation(
    isVisible: Boolean,
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val successColor = if (isDarkTheme) AppColors.SuccessGreenDark else AppColors.SuccessGreen
    val errorColor = if (isDarkTheme) AppColors.ErrorRedDark else AppColors.ErrorRed
    val baseColor = if (isSuccess) successColor else errorColor
    val backgroundColor = baseColor.copy(alpha = 0.15f)

    val confettiColors = listOf(
        AppColors.SuccessGreen,
        AppColors.StatusMedium,
        AppColors.Primary,
        AppColors.Secondary,
        AppColors.SuccessGreenDark,
        AppColors.DarkStatusMedium
    )

    val scale = remember { Animatable(0f) }
    val confettiProgress = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }

    val confettiParticles = remember {
        List(60) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -Random.nextFloat() * 0.5f,
                color = confettiColors.random(),
                size = Random.nextFloat() * 10 + 5,
                velocityX = (Random.nextFloat() - 0.5f) * 0.8f,
                velocityY = Random.nextFloat() * 0.6f + 0.4f
            )
        }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            iconScale.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
            scale.animateTo(1f, tween(300))
            confettiProgress.animateTo(1f, tween(2000))
            kotlinx.coroutines.delay(500)
            confettiProgress.animateTo(0f, tween(300))
            kotlinx.coroutines.delay(300)
            onDismiss()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (confettiProgress.value > 0f) {
                confettiParticles.forEach { particle ->
                    val progress = confettiProgress.value
                    val currentX = (particle.x + particle.velocityX * progress) * size.width
                    val currentY = ((particle.y + particle.velocityY * progress) % 1.5f) * size.height

                    drawCircle(
                        color = particle.color.copy(alpha = (1f - progress * 0.5f).coerceIn(0f, 1f)),
                        radius = particle.size,
                        center = Offset(currentX, currentY)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(if (isSuccess) scale.value * iconScale.value else scale.value * pulseScale)
                .clip(CircleShape)
                .background(baseColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isSuccess) "Éxito" else "Error",
                tint = Color.White,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}