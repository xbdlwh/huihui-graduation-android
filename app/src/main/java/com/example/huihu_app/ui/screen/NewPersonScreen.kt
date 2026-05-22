package com.example.huihu_app.ui.screen

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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.FoodLoadingCard
import com.example.huihu_app.ui.components.FoodReactionBar
import com.example.huihu_app.ui.components.SwipeFoodCard
import com.example.huihu_app.ui.viewModel.NewPersonViewModel
import kotlin.uuid.Uuid

@Composable
fun NewPersonScreen(
    token: String,
    viewModel: NewPersonViewModel = viewModel(factory = AppViewModelProvider.FACTORY, key = Math.random().toString())
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        viewModel.start(token)
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
                text = "建立你的口味画像",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "第 ${if (uiState.currentRound == 0) 1 else uiState.currentRound} / 2 轮",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                        Text("重试")
                    }
                }

                uiState.cards.isNotEmpty() -> {
                    val topCard = uiState.cards.first()
                    key(topCard.id) {
                        SwipeFoodCard(
                            food = topCard,
                            onSwipeRight = { viewModel.onLike() },
                            onSwipeLeft = { viewModel.onSkip() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    FoodReactionBar(
                        onSkip = { viewModel.onSkip() },
                        onDislike = { viewModel.onDislike() },
                        onLike = { viewModel.onLike() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.isLoading && uiState.cards.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (uiState.isCompleted) {
                Text(
                    text = "口味画像已完成。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
