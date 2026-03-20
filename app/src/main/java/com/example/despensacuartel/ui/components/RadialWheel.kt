package com.example.despensacuartel.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.SectionColor
import com.example.despensacuartel.ui.theme.AppColors
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadialWheel(
    modifier: Modifier = Modifier,
    sectionColors: Map<Category, List<SectionColor>> = emptyMap(),
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 4f,
    onSectionClick: (String) -> Unit = {},
    onCenterClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val wheelSizeDp = (screenWidthDp * 0.8f).dp

    var currentPressed by remember { mutableStateOf<Category?>(null) }
    var isCenterPressed by remember { mutableStateOf(false) }
    val centerScale by animateFloatAsState(
        targetValue = if (isCenterPressed) 0.9f else 1f,
        animationSpec = tween(150),
        label = "centerScale"
    )
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(isCenterPressed) {
        if (isCenterPressed) {
            kotlinx.coroutines.delay(150)
            isCenterPressed = false
        }
    }

    // Animación global de escala de la rueda
    val wheelScale by animateFloatAsState(
        targetValue = if (currentPressed != null) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "wheelScale"
    )

    // Calcular estados de cada sección (sin animaciones internas al Canvas)
    val sectionStates = remember(currentPressed) {
        Category.entries.associate { category ->
            category to SectionAnimationState(
                isPressed = category == currentPressed,
                scale = if (category == currentPressed) 0.88f else 1f,
                glow = if (category == currentPressed) 1f else 0f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val textMeasurer = rememberTextMeasurer()

        val boxSize = with(LocalDensity.current) { wheelSizeDp.toPx() }

        val sections = Category.entries.size
        val angleStep = remember { 360f / sections }

        // Cache de paths de secciones
        val sectionPaths = remember(currentPressed, wheelScale, boxSize) {
            val centerX = boxSize / 2f
            val centerY = boxSize / 2f
            val outerRadius = minOf(centerX, centerY) * 0.9f * wheelScale

            Category.entries.associate { category ->
                val state = sectionStates[category]!!
                val scaledOuterRadius = outerRadius * state.scale

                val sectionMidAngle = category.angle
                val sectionStartAngle = sectionMidAngle - (angleStep / 2)
                val sectionEndAngle = sectionMidAngle + (angleStep / 2)

                val p1 = Offset(centerX, centerY)
                val p2 = getPointOnCircle(centerX, centerY, scaledOuterRadius, sectionStartAngle)
                val p3 = getPointOnCircle(centerX, centerY, scaledOuterRadius * 1.3f * state.scale, sectionMidAngle)
                val p4 = getPointOnCircle(centerX, centerY, scaledOuterRadius, sectionEndAngle)

                val path = Path().apply {
                    moveTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p3.x, p3.y)
                    lineTo(p4.x, p4.y)
                    close()
                }
                category to path
            }
        }

        // Cache de brushes radiales por sección
        val categoryBrushes = remember(currentPressed, wheelScale, boxSize) {
            val centerX = boxSize / 2f
            val centerY = boxSize / 2f
            val outerRadius = minOf(centerX, centerY) * 0.9f * wheelScale

            Category.entries.associate { category ->
                val state = sectionStates[category]!!
                val scaledOuterRadius = outerRadius * state.scale

                val colors = sectionColors[category] ?: listOf(SectionColor.Empty, SectionColor.Empty, SectionColor.Empty, SectionColor.Empty)
                val colorList = colors.flatMap { it.toColors() }
                val primaryColor = colorList.getOrElse(0) { Color.DarkGray }

                val brightenedColor = if (state.isPressed) {
                    Color(
                        red = (primaryColor.red * 1.3f).coerceAtMost(1f),
                        green = (primaryColor.green * 1.3f).coerceAtMost(1f),
                        blue = (primaryColor.blue * 1.3f).coerceAtMost(1f),
                        alpha = 1f
                    )
                } else primaryColor

                val glowRadius = if (state.isPressed) scaledOuterRadius * 1.5f else scaledOuterRadius

                val brush = Brush.radialGradient(
                    colors = if (state.isPressed) {
                        listOf(
                            brightenedColor.copy(alpha = 0.9f),
                            brightenedColor.copy(alpha = 0.85f),
                            primaryColor.copy(alpha = 0.9f),
                            primaryColor.copy(alpha = 0.85f)
                        )
                    } else {
                        listOf(
                            primaryColor.copy(alpha = 0.85f),
                            primaryColor.copy(alpha = 0.85f),
                            primaryColor,
                            primaryColor.copy(alpha = 0.95f)
                        )
                    },
                    center = Offset(centerX, centerY),
                    radius = glowRadius
                )
                category to brush
            }
        }

        // Cache de posiciones de iconos
        val iconPositions = remember(wheelScale, boxSize) {
            val centerX = boxSize / 2f
            val centerY = boxSize / 2f
            val outerRadius = minOf(centerX, centerY) * 0.9f * wheelScale

            Category.entries.associate { category ->
                val state = sectionStates[category]!!
                val scaledOuterRadius = outerRadius * state.scale
                val iconRadius = scaledOuterRadius * 0.95f
                val iconPos = getPointOnCircle(centerX, centerY, iconRadius, category.angle)
                category to iconPos
            }
        }

        // Función auxiliar para detectar la sección en una posición
        fun detectSectionAtPosition(x: Float, y: Float, width: Float, height: Float): Category? {
            val centerX = width / 2f
            val centerY = height / 2f
            val outerRadius = minOf(centerX, centerY) * 0.9f

            val dx = x - centerX
            val dy = y - centerY
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

            if (distance <= outerRadius * 1.3f && distance >= outerRadius * 0.1f) {
                val angle = ((Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 360) % 360)

                return Category.entries.minByOrNull { category ->
                    val diff = angle - category.angle
                    val normalizedDiff = ((diff + 180) % 360) - 180
                    kotlin.math.abs(normalizedDiff)
                }
            }
            return null
        }

        fun detectCenterAtPosition(x: Float, y: Float, width: Float, height: Float): Boolean {
            val centerX = width / 2f
            val centerY = height / 2f
            val buttonRadius = minOf(centerX, centerY) * 0.15f
            val dx = x - centerX
            val dy = y - centerY
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
            return distance <= buttonRadius * 1.5f
        }

        Canvas(
            modifier = Modifier
                .size(wheelSizeDp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            val isCenter = detectCenterAtPosition(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                            if (isCenter) {
                                isCenterPressed = true
                            } else {
                                val detected = detectSectionAtPosition(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                                currentPressed = detected
                                detected?.let {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }

                                try {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val position = event.changes.firstOrNull()?.position
                                            if (position != null) {
                                                val movedDetection = detectSectionAtPosition(position.x, position.y, size.width.toFloat(), size.height.toFloat())
                                                if (movedDetection != currentPressed) {
                                                    currentPressed = movedDetection
                                                    movedDetection?.let {
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    }
                                                }
                                            }
                                            if (event.changes.all { it.isConsumed }) break
                                        }
                                    }
                                } catch (e: Exception) {
                                }
                                currentPressed = null
                            }
                        },
                        onTap = { offset ->
                            val isCenter = detectCenterAtPosition(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                            if (isCenter) {
                                onCenterClick()
                            } else {
                                val detected = detectSectionAtPosition(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                                detected?.let { onSectionClick(it.id) }
                            }
                        }
                    )
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val outerRadius = minOf(centerX, centerY) * 0.9f * wheelScale

            Category.entries.forEachIndexed { index, category ->
                val state = sectionStates[category]!!

                val path = sectionPaths[category]!!
                drawPath(path = path, brush = categoryBrushes[category]!!)

                // Glow effect - ahora en toda la sección, no solo contorno
                if (state.glow > 0f) {
                    val colors = sectionColors[category] ?: listOf(SectionColor.Empty, SectionColor.Empty, SectionColor.Empty, SectionColor.Empty)
                    val colorList = colors.flatMap { it.toColors() }
                    val primaryColor = colorList.getOrElse(0) { Color.DarkGray }
                    val brightenedColor = if (state.isPressed) {
                        Color(
                            red = (primaryColor.red * 1.3f).coerceAtMost(1f),
                            green = (primaryColor.green * 1.3f).coerceAtMost(1f),
                            blue = (primaryColor.blue * 1.3f).coerceAtMost(1f),
                            alpha = 1f
                        )
                    } else primaryColor

                    // Capa exterior de glow (más grande)
                    drawPath(
                        path = path,
                        color = brightenedColor.copy(alpha = 0.2f),
                        style = Stroke(width = strokeWidth * 8 * state.glow, cap = StrokeCap.Round)
                    )
                    // Segunda capa de glow
                    drawPath(
                        path = path,
                        color = brightenedColor.copy(alpha = 0.35f),
                        style = Stroke(width = strokeWidth * 5 * state.glow, cap = StrokeCap.Round)
                    )
                    // Tercera capa
                    drawPath(
                        path = path,
                        color = brightenedColor.copy(alpha = 0.5f),
                        style = Stroke(width = strokeWidth * 3 * state.glow, cap = StrokeCap.Round)
                    )
                    // Capa interior brillante
                    drawPath(
                        path = path,
                        color = brightenedColor.copy(alpha = 0.7f),
                        style = Stroke(width = strokeWidth * 1.5f, cap = StrokeCap.Round)
                    )
                }

                // Contorno
                val contourWidth = strokeWidth * 3 + (state.glow * strokeWidth * 0.5f)
                drawPath(
                    path = path,
                    color = if (state.isPressed) AppColors.OutlineDark else strokeColor.copy(alpha = 0.8f),
                    style = Stroke(width = contourWidth, cap = StrokeCap.Round)
                )

                // Icono en la punta (emoji con fallback a CategoryIcons para accesibilidad)
                val iconSize = 40f + (state.glow * 8)
                val iconPos = iconPositions[category]!!

                // Glow efecto para el icono
                if (state.glow > 0f) {
                    val colors = sectionColors[category] ?: listOf(SectionColor.Empty, SectionColor.Empty, SectionColor.Empty, SectionColor.Empty)
                    val colorList = colors.flatMap { it.toColors() }
                    val primaryColor = colorList.getOrElse(0) { Color.DarkGray }
                    val brightenedColor = if (state.isPressed) {
                        Color(
                            red = (primaryColor.red * 1.3f).coerceAtMost(1f),
                            green = (primaryColor.green * 1.3f).coerceAtMost(1f),
                            blue = (primaryColor.blue * 1.3f).coerceAtMost(1f),
                            alpha = 1f
                        )
                    } else primaryColor

                    drawCircle(
                        color = brightenedColor.copy(alpha = 0.3f),
                        radius = iconSize * 0.6f,
                        center = iconPos
                    )
                }

                // Dibujar emoji con mejor contraste
                val textLayoutResult = textMeasurer.measure(
                    text = category.emoji,
                    style = TextStyle(fontSize = iconSize.sp)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        iconPos.x - textLayoutResult.size.width / 2,
                        iconPos.y - textLayoutResult.size.height / 2
                    )
                )
            }

            // Botón central "+"
            val centerXButton = size.width / 2
            val centerYButton = size.height / 2
            val buttonRadius = minOf(centerXButton, centerYButton) * 0.15f * centerScale

            drawCircle(
                color = Color(0x40000000),
                radius = buttonRadius + 4f,
                center = Offset(centerXButton + 2f, centerYButton + 2f)
            )

            drawCircle(
                color = AppColors.Primary,
                radius = buttonRadius,
                center = Offset(centerXButton, centerYButton)
            )

            drawCircle(
                color = Color.Black,
                radius = buttonRadius,
                center = Offset(centerXButton, centerYButton),
                style = Stroke(width = 3f)
            )

            val plusTextLayoutResult = textMeasurer.measure(
                text = "+",
                style = TextStyle(
                    fontSize = (buttonRadius * 0.8f).sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = plusTextLayoutResult,
                topLeft = Offset(
                    centerXButton - plusTextLayoutResult.size.width / 2,
                    centerYButton - plusTextLayoutResult.size.height / 2
                )
            )
        }
    }
}

private data class SectionAnimationState(
    val isPressed: Boolean,
    val scale: Float,
    val glow: Float
)

private fun getPointOnCircle(
    centerX: Float,
    centerY: Float,
    radius: Float,
    angleDegrees: Float
): Offset {
    val angleRadians = Math.toRadians(angleDegrees.toDouble())
    val x = centerX + (radius * cos(angleRadians)).toFloat()
    val y = centerY + (radius * sin(angleRadians)).toFloat()
    return Offset(x, y)
}
