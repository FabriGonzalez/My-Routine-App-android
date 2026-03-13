package com.myroutine.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import com.myroutine.app.data.local.entity.Exercise
import com.myroutine.app.data.local.entity.MeasureType

private fun formatMeasure(value: Double): String {
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
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current
    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            },
        contentPadding = PaddingValues(vertical = 8.dp)
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

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val alignment = when (dismissState.dismissDirection) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        else -> Alignment.CenterEnd
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = alignment
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar",
                                tint = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            ) {
                Column {
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

                    LaunchedEffect(exercise.measureValue) {
                        if (!isWeightFocused) {
                            val formatted = formatMeasure(exercise.measureValue)
                            if (formatted != weightText) {
                                weightText = formatted
                            }
                        }
                    }

                    LaunchedEffect(isWeightFocused) {
                        if (!isWeightFocused) {
                            val formatted = formatMeasure(exercise.measureValue)
                            if (formatted != weightText) {
                                weightText = formatted
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exercise.name,
                            modifier = Modifier.weight(2f),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (isWeightFocused)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            val unit = when (exercise.measureType) {
                                MeasureType.WEIGHT -> "kg"
                                MeasureType.TIME -> "s"
                            }

                            BasicTextField(
                                value = weightText,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        weightText = newValue
                                        val parsed = newValue.toDoubleOrNull()

                                        when {
                                            newValue.isEmpty() -> {
                                                onUpdate(exercise.copy(measureValue = 0.0))
                                            }
                                            parsed != null -> {
                                                onUpdate(exercise.copy(measureValue = parsed))
                                            }
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        isWeightFocused = focusState.isFocused
                                    },
                                decorationBox = { innerTextField ->

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (weightText.isEmpty()) {
                                                Text(
                                                    text = "0",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                                )
                                            }

                                            innerTextField()
                                        }

                                        Spacer(Modifier.width(4.dp))

                                        Text(
                                            text = unit,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (isRepsFocused) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (exercise.failure) {
                                Text(
                                    text = "Fallo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                BasicTextField(
                                    value = repsText,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                            repsText = newValue
                                            val newReps = newValue.toIntOrNull() ?: 0
                                            onUpdate(exercise.copy(reps = newReps))
                                        }
                                    },
                                    textStyle = TextStyle(
                                        textAlign = TextAlign.Center,
                                        color = if (isRepsFocused) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onBackground,
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                    ),
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
                                        },
                                    decorationBox = { innerTextField ->
                                        Box(contentAlignment = Alignment.Center) {
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (isSetsFocused) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                        ) {
                            BasicTextField(
                                value = setsText,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                        setsText = newValue
                                        val newSets = newValue.toIntOrNull() ?: 0
                                        onUpdate(exercise.copy(sets = newSets))
                                    }
                                },
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = if (isSetsFocused) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                ),
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
                                    },
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.Center) {
                                        innerTextField()
                                    }
                                }
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Reordenar",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier
                                .width(48.dp)
                                .size(28.dp)
                                .pointerInput(exercises) {
                                    var totalDrag by mutableStateOf(0f)
                                    var currentIndex = index

                                    detectDragGestures(
                                        onDragStart = {
                                            draggedItemIndex = index
                                            totalDrag = 0f
                                            currentIndex = index
                                        },
                                        onDragEnd = {
                                            draggedItemIndex = null
                                            totalDrag = 0f
                                        },
                                        onDragCancel = {
                                            draggedItemIndex = null
                                            totalDrag = 0f
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()

                                            totalDrag += dragAmount.y

                                            val itemHeight = 90f

                                            if (kotlin.math.abs(totalDrag) > itemHeight) {
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
                                }
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
