package com.myroutine.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.*
import androidx.core.graphics.toColorInt

@Composable
fun ColorPickerDialog(
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {

    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(Color.White) }
    var hexInput by remember { mutableStateOf("") }
    var lightness by remember { mutableStateOf(1f) }

    val quickColors = listOf(
        Color(0xFFE57373),
        Color(0xFFFFB74D),
        Color(0xFFFFF176),
        Color(0xFF81C784),
        Color(0xFF64B5F6),
        Color(0xFFBA68C8)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                }
            ) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = {
            Text("Selecciona un color")
        },
        text = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    controller = controller,
                    drawDefaultWheelIndicator = false,
                    drawOnPosSelected = {

                        val point = controller.selectedPoint.value

                        drawCircle(
                            color = Color.White,
                            radius = 10.dp.toPx(),
                            center = Offset(point.x, point.y)
                        )

                        drawCircle(
                            color = Color.Black,
                            radius = 14.dp.toPx(),
                            center = Offset(point.x, point.y),
                            style = Stroke(3.dp.toPx())
                        )
                    },
                    onColorChanged = {
                        selectedColor = it.color

                        val hsv = FloatArray(3)
                        android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)

                        lightness = 1f - hsv[1]
                    }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Brillo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface
                            ),
                            RoundedCornerShape(8.dp)
                        ),
                    controller = controller
                )

                Spacer(Modifier.height(12.dp))

                Column {

                    Text(
                        text = "Claridad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface
                                ),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        LightnessSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp),
                            value = lightness,
                            selectedColor = selectedColor,
                            onValueChange = {

                                lightness = it

                                val hsv = FloatArray(3)
                                android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)

                                hsv[1] = 1f - it

                                val newColor = Color(android.graphics.Color.HSVToColor(hsv))

                                selectedColor = newColor
                                controller.selectByColor(newColor, fromUser = false)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Transparencia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface
                            ),
                            RoundedCornerShape(8.dp)
                        ),
                    controller = controller
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Colores rápidos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    quickColors.forEach { color ->

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(color, CircleShape)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onSurface
                                    ),
                                    CircleShape
                                )
                                .clickable {
                                    selectedColor = color
                                    controller.selectByColor(color, true)
                                }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(selectedColor, CircleShape)
                            .border(
                                BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.onSurface
                                ),
                                CircleShape
                            )
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "#%06X".format(0xFFFFFF and selectedColor.toArgb()),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = {
                        hexInput = it

                        val color = hexToColor(it)
                        if (color != null) {
                            selectedColor = color
                            controller.selectByColor(color, fromUser = false)
                        }
                    },
                    label = { Text("HEX") },
                    placeholder = { Text("#FF0000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    )
}

private fun hexToColor(hex: String): Color? {
    return try {
        val cleanHex = hex.removePrefix("#")
        when (cleanHex.length) {
            6 -> Color("#$cleanHex".toColorInt())
            8 -> Color("#$cleanHex".toColorInt())
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}