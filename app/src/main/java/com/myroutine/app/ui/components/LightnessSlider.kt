package com.myroutine.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.toArgb

@Composable
fun LightnessSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    selectedColor: Color,
    borderRadius: Dp = 8.dp,
    wheelRadius: Dp = 10.dp
) {

    var size by remember { mutableStateOf(IntSize.Zero) }

    val paint = Paint()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(borderRadius))
            .onSizeChanged { size = it }
            .pointerInput(Unit) {

                detectHorizontalDragGestures { change, _ ->
                    if (size.width == 0) return@detectHorizontalDragGestures

                    val position = (change.position.x / size.width)
                        .coerceIn(0f, 1f)

                    onValueChange(position)
                }
            }
            .pointerInput(Unit) {

                detectTapGestures { offset ->
                    if (size.width == 0) return@detectTapGestures

                    val position = (offset.x / size.width)
                        .coerceIn(0f, 1f)

                    onValueChange(position)
                }
            }
    ) {

        if (size.width == 0) return@Canvas

        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)

        val saturated = Color(android.graphics.Color.HSVToColor(hsv))

        hsv[1] = 0f
        val pastel = Color(android.graphics.Color.HSVToColor(hsv))

        val padding = 4.dp.toPx()

        drawIntoCanvas { canvas ->

            val shader = LinearGradientShader(
                colors = listOf(saturated, pastel),
                from = Offset(padding, 0f),
                to = Offset(size.width - padding, 0f),
                tileMode = TileMode.Clamp
            )

            paint.shader = shader

            canvas.drawRoundRect(
                left = padding,
                top = padding,
                right = size.width - padding,
                bottom = size.height.toFloat() - padding,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint = paint
            )

            val point =
                padding + (size.width - padding * 2) * value

            val center = Offset(point, size.height / 2f)

            canvas.drawCircle(
                center = center,
                radius = wheelRadius.toPx(),
                paint = Paint().apply { color = Color.White }
            )

            canvas.drawCircle(
                center = center,
                radius = wheelRadius.toPx(),
                paint = Paint().apply {
                    color = Color.Black
                    style = PaintingStyle.Stroke
                    strokeWidth = 3.dp.toPx()
                }
            )
        }
    }
}