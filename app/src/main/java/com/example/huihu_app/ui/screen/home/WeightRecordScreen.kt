package com.example.huihu_app.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.CalorieGoalViewModel
import com.example.huihu_app.ui.viewModel.MealRecordViewModel

@Composable
fun WeightRecordScreen(
    token: String,
    onSetCalorieGoal: () -> Unit,
    calorieGoalViewModel: CalorieGoalViewModel = viewModel(factory = AppViewModelProvider.FACTORY),
    mealRecordViewModel: MealRecordViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val calorieGoalState by calorieGoalViewModel.uiState.collectAsState()
    val mealRecordState by mealRecordViewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        calorieGoalViewModel.loadCalorieGoal(token)
        mealRecordViewModel.loadMealRecords(token)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (calorieGoalState.isLoading || mealRecordState.isLoading) {
            CircularProgressIndicator()
        }

        // Header with calorie goal info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "每日目标",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${calorieGoalState.calorieGoal?.daily_calorie_goal?.toInt() ?: "--"} kcal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onSetCalorieGoal) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "修改目标"
                )
            }
        }

        // Today's calorie consumption card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "今日摄入",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${mealRecordState.totalCaloriesToday.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (mealRecordState.totalCaloriesToday > (calorieGoalState.calorieGoal?.daily_calorie_goal ?: Double.MAX_VALUE)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "/ ${calorieGoalState.calorieGoal?.daily_calorie_goal?.toInt() ?: 0} kcal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val goal = calorieGoalState.calorieGoal?.daily_calorie_goal ?: 1.0
                val consumed = mealRecordState.totalCaloriesToday
                val progress = (consumed / goal).coerceIn(0.0, 1.0).toFloat()
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                    ) {
                        val consumedWidth = progress.coerceAtMost(1f)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(consumedWidth)
                                .height(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (consumed > goal) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {}
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "剩余 ${(goal - consumed).coerceAtLeast(0.0).toInt()} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (calorieGoalState.calorieGoal == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSetCalorieGoal),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "您还没有设置每日卡路里目标",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onSetCalorieGoal) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("设置目标")
                    }
                }
            }
        }

        if (calorieGoalState.error != null || mealRecordState.error != null) {
            Text(
                text = calorieGoalState.error ?: mealRecordState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}