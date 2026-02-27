package com.example.huihu_app.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.FoodLoadingCard
import com.example.huihu_app.ui.components.TodayFoodActionBar
import com.example.huihu_app.ui.components.TodayFoodCard
import com.example.huihu_app.ui.components.TodayFoodNextAction
import com.example.huihu_app.ui.viewModel.FoodRecommendationViewModel

@Composable
fun FoodRecommendationScreen(
    token: String,
    viewModel: FoodRecommendationViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        viewModel.load(token)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
//        if (uiState.isRefilling) {
//            Text(
//                text = "正在刷新推荐...",
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }

        when {
            uiState.isLoading && uiState.currentFood == null -> {
                FoodLoadingCard(modifier = Modifier.fillMaxWidth())
            }

            uiState.error != null && uiState.currentFood == null -> {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { viewModel.retry() }) {
                    Text("重试")
                }
            }

            uiState.currentFood == null -> {
                Text(
                    text = "当前暂无推荐。",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { viewModel.load(token = token, force = true) }) {
                    Text("刷新推荐")
                }
            }

            else -> {
                val currentFood = uiState.currentFood!!
                val isAccepted = uiState.acceptedFoodId == currentFood.id
                TodayFoodCard(
                    food = currentFood,
                    isCelebrating = isAccepted,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isAccepted) {
                    TodayFoodNextAction(
                        onNextFood = { viewModel.nextFood() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TodayFoodActionBar(
                        onThatsIt = { viewModel.onThatsIt() },
                        onChangeIt = { viewModel.onChangeIt() },
                        onDontLikeIt = { viewModel.onDontLikeIt() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (uiState.feedbackMessage != null) {
            Text(
                text = uiState.feedbackMessage!!,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

//        Text(
//            text = "缓存菜品：${uiState.cachedCount}",
//            style = MaterialTheme.typography.labelMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )

        if (uiState.error != null && uiState.currentFood != null) {
            Text(
                text = "同步网络异常：${uiState.error}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
//
//        if (uiState.isLoading && uiState.currentFood != null) {
//            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//        }
    }
}
