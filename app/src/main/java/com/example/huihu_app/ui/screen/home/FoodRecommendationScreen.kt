package com.example.huihu_app.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.huihu_app.data.model.FoodComment
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.AddCommentButton
import com.example.huihu_app.ui.components.AddToRecordButton
import com.example.huihu_app.ui.components.FoodLoadingCard
import com.example.huihu_app.ui.components.TodayFoodActionBar
import com.example.huihu_app.ui.components.TodayFoodCard
import com.example.huihu_app.ui.components.TodayFoodNextAction
import com.example.huihu_app.ui.viewModel.FoodRecommendationViewModel

@Composable
fun FoodRecommendationScreen(
    token: String,
    isRandomMode: Boolean,
    onFoodClick: (Int) -> Unit = {},
    onAddToRecord: (Int, String) -> Unit = { _, _ -> },
    viewModel: FoodRecommendationViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        viewModel.load(token)
    }
    LaunchedEffect(isRandomMode) {
        viewModel.onRandomModeChanged(isRandomMode)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Food section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            onFoodClick = onFoodClick,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (isAccepted) {
                            TodayFoodNextAction(
                                onNextFood = { viewModel.nextFood() },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                            AddCommentButton(
                                onSubmit = { comment -> viewModel.addComment(token, comment) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            AddToRecordButton(
                                onAddToRecord = { mealType ->
                                    uiState.currentFood?.id?.let { foodId ->
                                        onAddToRecord(foodId, mealType)
                                    }
                                },
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

                if (uiState.error != null && uiState.currentFood != null) {
                    Text(
                        text = "同步网络异常：${uiState.error}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
// 10 15 12 25 20 15
        // Comments section
        if (uiState.currentFood != null) {
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "评论",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (uiState.isLoadingComments) {
                item {
                    CircularProgressIndicator()
                }
            } else if (uiState.comments.isEmpty()) {
                item {
                    Text(
                        text = "暂无评论",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.comments) { comment ->
                    CommentItem(
                        comment = comment,
                        onThumbClick = { viewModel.toggleCommentThumb(token, comment.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: FoodComment,
    onThumbClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.create_time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.clickable(onClick = onThumbClick),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = null,
                        tint = if (comment.thumbed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = "${comment.thumb_count}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
