package com.example.huihu_app.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.FoodLoadingCard
import com.example.huihu_app.ui.components.TodayFoodActionBar
import com.example.huihu_app.ui.components.TodayFoodCard
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

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Today's Food!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (uiState.pendingReactionCount > 0) {
                Text(
                    text = "Syncing feedback... (${uiState.pendingReactionCount})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            when {
                uiState.isLoading && uiState.cards.isEmpty() -> {
                    FoodLoadingCard(modifier = Modifier.fillMaxWidth())
                }

                uiState.error != null && uiState.cards.isEmpty() -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { viewModel.retry() }) {
                        Text("Retry")
                    }
                }

                uiState.cards.isEmpty() -> {
                    Text(
                        text = "No recommendations right now.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = { viewModel.load(token = token, force = true) }) {
                        Text("Refresh recommendations")
                    }
                }

                else -> {
                    val currentFood = uiState.cards.first()
                    TodayFoodCard(
                        food = currentFood,
                        isCelebrating = uiState.acceptedFoodId == currentFood.id,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TodayFoodActionBar(
                        onThatsIt = { viewModel.onThatsIt() },
                        onChangeIt = { viewModel.onChangeIt() },
                        onDontLikeIt = { viewModel.onDontLikeIt() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.feedbackMessage != null) {
                Text(
                    text = uiState.feedbackMessage!!,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.error != null && uiState.cards.isNotEmpty()) {
                Text(
                    text = "Network issue while syncing: ${uiState.error}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.isLoading && uiState.cards.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
