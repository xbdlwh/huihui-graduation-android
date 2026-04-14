package com.example.huihu_app.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WeightRecordScreen() {
    var initialWeight by rememberSaveable { mutableStateOf(120f) }
    var targetWeight by rememberSaveable { mutableStateOf(100f) }
    var currentWeight by rememberSaveable { mutableStateOf(112f) }
    val lostWeight = (initialWeight - currentWeight).coerceAtLeast(0f)
    val weightTrend = remember {
        mutableStateListOf(120f, 118f, 117f, 115f, 114f, 113f, 112f)
    }
    var showPlanSheet by rememberSaveable { mutableStateOf(false) }
    var showRecordSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            WeightPlanCard(
                initialWeight = initialWeight,
                targetWeight = targetWeight,
                lostWeight = lostWeight,
                onOpenPlanSettings = { showPlanSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            WeightRecordCard(
                currentWeight = currentWeight,
                weights = weightTrend.toList(),
                onRecordWeight = { showRecordSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }
    }

    if (showPlanSheet) {
        PlanSettingsSheet(
            currentWeight = currentWeight,
            targetWeight = targetWeight,
            onDismiss = { showPlanSheet = false },
            onSave = { updatedCurrentWeight, updatedTargetWeight ->
                currentWeight = updatedCurrentWeight
                targetWeight = updatedTargetWeight
                if (weightTrend.isEmpty()) {
                    weightTrend.add(updatedCurrentWeight)
                } else {
                    weightTrend[weightTrend.lastIndex] = updatedCurrentWeight
                }
                showPlanSheet = false
            }
        )
    }

    if (showRecordSheet) {
        RecordWeightSheet(
            currentWeight = currentWeight,
            onDismiss = { showRecordSheet = false },
            onSave = { newWeight ->
                currentWeight = newWeight
                weightTrend.add(newWeight)
                showRecordSheet = false
            }
        )
    }
}

@Composable
private fun WeightPlanCard(
    initialWeight: Float,
    targetWeight: Float,
    lostWeight: Float,
    onOpenPlanSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalToLose = (initialWeight - targetWeight).coerceAtLeast(1f)
    val progress = (lostWeight / totalToLose).coerceIn(0f, 1f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "体重记录方案",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onOpenPlanSettings) {
                    Text("方案")
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "方案设置"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                WeightArcProgress(
                    progress = progress,
                    lostWeight = lostWeight,
                    modifier = Modifier.size(152.dp)
                )

                WeightInfo(
                    title = "初始体重",
                    weight = "${initialWeight.toInt()}斤",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp)
                )

                WeightInfo(
                    title = "目标体重",
                    weight = "${targetWeight.toInt()}斤",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun WeightRecordCard(
    currentWeight: Float,
    weights: List<Float>,
    onRecordWeight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "体重记录",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "当前体重",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${currentWeight.toInt()}斤",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = onRecordWeight,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("记录今天体重")
                }
            }

            WeightLineChart(
                weights = weights,
                modifier = Modifier
                    .weight(1.1f)
                    .height(110.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordWeightSheet(
    currentWeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float) -> Unit
) {
    var weightInput by rememberSaveable { mutableStateOf(currentWeight.formatWeightInput()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "记录体重",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "填写今天的体重后，会同步更新下方折线图和上方已减斤数。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = weightInput,
                onValueChange = {
                    weightInput = it.filter { char -> char.isDigit() || char == '.' }
                },
                label = { Text("今天体重(斤)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    onSave(weightInput.toFloatOrNull() ?: currentWeight)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("保存记录")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanSettingsSheet(
    currentWeight: Float,
    targetWeight: Float,
    onDismiss: () -> Unit,
    onSave: (Float, Float) -> Unit
) {
    var gender by rememberSaveable { mutableStateOf("男") }
    var height by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf(currentWeight.formatWeightInput()) }
    var targetWeightInput by rememberSaveable { mutableStateOf(targetWeight.formatWeightInput()) }
    val genderOptions = listOf("男", "女")

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "方案设置",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "完善基础信息后，可以更方便地规划你的体重管理目标。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "基础资料",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "性别",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            genderOptions.forEach { option ->
                                FilterChip(
                                    selected = gender == option,
                                    onClick = { gender = option },
                                    label = { Text(option) }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it.filter(Char::isDigit) },
                            label = { Text("身高(cm)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it.filter(Char::isDigit) },
                            label = { Text("年龄") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("当前体重(斤)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetWeightInput,
                        onValueChange = {
                            targetWeightInput = it.filter { char -> char.isDigit() || char == '.' }
                        },
                        label = { Text("目标体重(斤)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "填写建议",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "先填写基础信息，后续可以再补目标体重和阶段计划。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 2.dp))

            Button(
                onClick = {
                    val updatedCurrentWeight = weight.toFloatOrNull() ?: currentWeight
                    val updatedTargetWeight = targetWeightInput.toFloatOrNull() ?: targetWeight
                    onSave(updatedCurrentWeight, updatedTargetWeight)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("保存方案")
            }
        }
    }
}

private fun Float.formatWeightInput(): String {
    return if (this % 1f == 0f) this.toInt().toString() else this.toString()
}

@Composable
private fun WeightArcProgress(
    progress: Float,
    lostWeight: Float,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
    val progressColor = Color(0xFF38A169)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(
                strokeWidth / 2,
                strokeWidth / 2
            )

            drawArc(
                color = trackColor,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = progressColor,
                startAngle = 150f,
                sweepAngle = 240f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "已减",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${lostWeight.toInt()}斤",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun WeightLineChart(
    weights: List<Float>,
    modifier: Modifier = Modifier
) {
    val safeWeights = if (weights.size >= 2) weights else listOf(0f, 0f)
    val maxWeight = safeWeights.maxOrNull() ?: 0f
    val minWeight = safeWeights.minOrNull() ?: 0f
    val range = (maxWeight - minWeight).takeIf { it > 0f } ?: 1f
    val lineColor = Color(0xFF38A169)
    val pointColor = Color(0xFF2F855A)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height
        val horizontalStep = chartWidth / (safeWeights.size - 1).coerceAtLeast(1)

        repeat(3) { index ->
            val y = chartHeight * (index + 1) / 4f
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(chartWidth, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val points = safeWeights.mapIndexed { index, value ->
            val x = horizontalStep * index
            val normalized = (value - minWeight) / range
            val y = chartHeight - (normalized * (chartHeight - 10.dp.toPx())) - 5.dp.toPx()
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        points.forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 4.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun WeightInfo(
    title: String,
    weight: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = weight,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
