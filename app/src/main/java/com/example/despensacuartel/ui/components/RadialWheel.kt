package com.example.despensacuartel.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
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
    strokeColor: Color = AppColors.Surface,
    strokeWidth: Float = 3f,
    onSectionClick: (String) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val wheelSizeDp = (screenWidthDp * 0.8f).dp

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "wheelScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val wheelSizePx = with(density) { wheelSizeDp.toPx() }

        val textMeasurer = rememberTextMeasurer()

        Canvas(
            modifier = Modifier
                .fillMaxWidth(wheelSizeDp.value / screenWidthDp)
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { offset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val outerRadius = minOf(centerX, centerY) * 0.9f

                            val dx = offset.x - centerX
                            val dy = offset.y - centerY
                            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                            if (distance <= outerRadius * 1.3f && distance >= outerRadius * 0.1f) {
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                angle = (angle + 360) % 360

                                val clickedCategory = Category.entries.minByOrNull { category ->
                                    val diff = angle - category.angle
                                    val normalizedDiff = ((diff + 180) % 360) - 180
                                    kotlin.math.abs(normalizedDiff)
                                }

                                clickedCategory?.let { onSectionClick(it.id) }
                            }
                        }
                    )
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val outerRadius = minOf(centerX, centerY) * 0.9f

            val sections = Category.entries.size
            val angleStep = 360f / sections

            Category.entries.forEachIndexed { index, category ->
                val sectionMidAngle = category.angle
                val sectionStartAngle = sectionMidAngle - (angleStep / 2)
                val sectionEndAngle = sectionMidAngle + (angleStep / 2)

                val p1 = Offset(centerX, centerY)
                val p2 = getPointOnCircle(centerX, centerY, outerRadius, sectionStartAngle)
                val p3 = getPointOnCircle(centerX, centerY, outerRadius * 1.3f, sectionMidAngle)
                val p4 = getPointOnCircle(centerX, centerY, outerRadius, sectionEndAngle)

                val path = Path().apply {
                    moveTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p3.x, p3.y)
                    lineTo(p4.x, p4.y)
                    close()
                }

                val colors = sectionColors[category] ?: listOf(SectionColor.Empty, SectionColor.Empty, SectionColor.Empty, SectionColor.Empty)
                val colorList = colors.flatMap { it.toColors() }

                val brush = Brush.radialGradient(
                    colors = listOf(
                        colorList.getOrElse(0) { Color.DarkGray },
                        colorList.getOrElse(1) { Color.DarkGray },
                        colorList.getOrElse(2) { Color.DarkGray },
                        colorList.getOrElse(3) { Color.DarkGray }
                    ),
                    center = Offset(centerX, centerY),
                    radius = outerRadius
                )

                drawPath(path = path, brush = brush)
                drawPath(path = path, color = strokeColor, style = Stroke(width = strokeWidth))

                val emojiRadius = outerRadius * 0.65f
                val emojiMidAngle = sectionMidAngle
                val emojiPos = getPointOnCircle(centerX, centerY, emojiRadius, emojiMidAngle)

                val textLayoutResult = textMeasurer.measure(
                    text = category.emoji,
                    style = TextStyle(fontSize = 28.sp)
                )

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        emojiPos.x - textLayoutResult.size.width / 2,
                        emojiPos.y - textLayoutResult.size.height / 2
                    )
                )
            }
        }
    }
}

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
