package com.myroutine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.local.entity.MeasureType
import com.myroutine.app.ui.theme.MetricTextStyle

internal fun formatMeasure(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

@Composable
fun TableContent(
    exercises: List<Exercise>,
    onDelete: (Exercise) -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onUpdate: (Exercise) -> Unit,
    onEdit: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current
    val fallbackRowHeightPx = with(LocalDensity.current) { 120.dp.toPx() }

    LazyColumn(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        itemsIndexed(
            items = exercises,
            key = { _, item -> item.id }
        ) { index, exercise ->

            val dismissState = rememberSwipeToDismissBoxState(
                positionalThreshold = { totalDistance ->
                    totalDistance * 0.5f
                }
            )

            LaunchedEffect(dismissState.currentValue) {
                if (
                    dismissState.currentValue == SwipeToDismissBoxValue.EndToStart ||
                    dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd
                ) {
                    onDelete(exercise)
                }
            }

            // Alto real de la fila, para que el arrastre mueva de a un ejercicio
            var rowHeightPx by remember { mutableIntStateOf(0) }

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val alignment = when (dismissState.dismissDirection) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        else -> Alignment.CenterEnd
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.errorContainer),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .size(26.dp)
                        )
                    }
                }
            ) {
                var weightText by remember(exercise.id) {
                    mutableStateOf(formatMeasure(exercise.measureValue))
                }
                var repsText by remember(exercise.id) {
                    mutableStateOf(exercise.reps.toString())
                }
                var setsText by remember(exercise.id) {
                    mutableStateOf(exercise.sets.toString())
                }
                var isWeightFocused by remember { mutableStateOf(false) }
                var isRepsFocused by remember { mutableStateOf(false) }
                var isSetsFocused by remember { mutableStateOf(false) }

                // Reps y series también pueden cambiar desde el modal de edición
                LaunchedEffect(exercise.reps, isRepsFocused) {
                    if (!isRepsFocused) repsText = (exercise.reps ?: 0).toString()
                }
                LaunchedEffect(exercise.sets, isSetsFocused) {
                    if (!isSetsFocused) setsText = exercise.sets.toString()
                }

                LaunchedEffect(exercise.measureValue, isWeightFocused) {
                    if (!isWeightFocused) {
                        val formatted = formatMeasure(exercise.measureValue)
                        if (formatted != weightText) {
                            weightText = formatted
                        }
                    }
                }

                val metricTextStyle = MetricTextStyle.copy(color = MaterialTheme.colorScheme.onBackground)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .onSizeChanged { rowHeightPx = it.height }
                ) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClickLabel = "Editar ejercicio") { onEdit(exercise) }
                                    .padding(vertical = 4.dp)
                            )

                            IconButton(
                                onClick = { onEdit(exercise) },
                                modifier = Modifier
                                    .offset(x = 10.dp)
                                    .size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar ${exercise.name}",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .offset(x = 10.dp)
                                    .size(44.dp)
                                    .pointerInput(exercises) {
                                        var totalDrag = 0f
                                        var currentIndex = index

                                        detectDragGestures(
                                            onDragStart = {
                                                totalDrag = 0f
                                                currentIndex = index
                                            },
                                            onDragEnd = { totalDrag = 0f },
                                            onDragCancel = { totalDrag = 0f },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                totalDrag += dragAmount.y

                                                val threshold = rowHeightPx
                                                    .takeIf { it > 0 }
                                                    ?.toFloat()
                                                    ?: fallbackRowHeightPx

                                                if (kotlin.math.abs(totalDrag) > threshold) {
                                                    val direction = if (totalDrag > 0) 1 else -1
                                                    val newIndex = (currentIndex + direction)
                                                        .coerceIn(0, exercises.lastIndex)

                                                    if (newIndex != currentIndex) {
                                                        onMove(currentIndex, newIndex)
                                                        currentIndex = newIndex
                                                    }

                                                    totalDrag = 0f
                                                }
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = "Reordenar ${exercise.name}",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricField(
                                label = when (exercise.measureType) {
                                    MeasureType.WEIGHT -> "CARGA"
                                    MeasureType.TIME -> "TIEMPO"
                                },
                                focused = isWeightFocused,
                                modifier = Modifier.weight(1f)
                            ) {
                                BasicTextField(
                                    value = weightText,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                            weightText = newValue
                                            val parsed = newValue.toDoubleOrNull()

                                            when {
                                                newValue.isEmpty() -> onUpdate(exercise.copy(measureValue = 0.0))
                                                parsed != null -> onUpdate(exercise.copy(measureValue = parsed))
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    textStyle = metricTextStyle,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { isWeightFocused = it.isFocused },
                                    visualTransformation = unitSuffix(
                                        unit = when (exercise.measureType) {
                                            MeasureType.WEIGHT -> "kg"
                                            MeasureType.TIME -> "s"
                                        },
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                            fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ),
                                    decorationBox = { innerTextField ->
                                        Box {
                                            if (weightText.isEmpty()) {
                                                Text(
                                                    text = "0",
                                                    style = MetricTextStyle,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }

                            MetricField(
                                label = "REPS",
                                focused = isRepsFocused,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (exercise.failure) {
                                    Text(
                                        text = "Fallo",
                                        style = metricTextStyle
                                    )
                                } else {
                                    BasicTextField(
                                        value = repsText,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                                repsText = newValue
                                                onUpdate(exercise.copy(reps = newValue.toIntOrNull() ?: 0))
                                            }
                                        },
                                        textStyle = metricTextStyle,
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused && !isRepsFocused) {
                                                    repsText = ""
                                                }
                                                if (!focusState.isFocused && repsText.isEmpty()) {
                                                    repsText = exercise.reps.toString()
                                                }
                                                isRepsFocused = focusState.isFocused
                                            }
                                    )
                                }
                            }

                            MetricField(
                                label = "SERIES",
                                focused = isSetsFocused,
                                modifier = Modifier.weight(1f)
                            ) {
                                BasicTextField(
                                    value = setsText,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                            setsText = newValue
                                            onUpdate(exercise.copy(sets = newValue.toIntOrNull() ?: 0))
                                        }
                                    },
                                    textStyle = metricTextStyle,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused && !isSetsFocused) {
                                                setsText = ""
                                            }
                                            if (!focusState.isFocused && setsText.isEmpty()) {
                                                setsText = exercise.sets.toString()
                                            }
                                            isSetsFocused = focusState.isFocused
                                        }
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

// Muestra la unidad pegada al número sin que forme parte del valor editable
private fun unitSuffix(unit: String, style: SpanStyle) = VisualTransformation { text ->
    if (text.isEmpty()) {
        TransformedText(text, OffsetMapping.Identity)
    } else {
        val transformed = buildAnnotatedString {
            append(text)
            append(" ")
            withStyle(style) { append(unit) }
        }
        TransformedText(
            transformed,
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int) = offset
                override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(text.length)
            }
        )
    }
}

@Composable
private fun MetricField(
    label: String,
    focused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.5.dp,
                color = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                shape = shape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}
