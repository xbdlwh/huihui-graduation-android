package com.example.huihu_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.huihu_app.data.model.Food

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeFoodCard(
    food: Food,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onSwipeRight()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeLeft()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val isLike = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            val background = if (isLike) Color(0xFFD5F5E3) else Color(0xFFFDE2E1)
            val icon = if (isLike) Icons.Filled.ThumbUp else Icons.Filled.Close
            val alignment = if (isLike) Alignment.CenterStart else Alignment.CenterEnd

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(background)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = null)
            }
        }
    ) {
        FoodCardContent(food = food)
    }
}

@Composable
fun FoodCardContent(
    food: Food,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = food.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FoodReactionBar(
    onSkip: () -> Unit,
    onDislike: () -> Unit,
    onLike: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onSkip,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.Close, contentDescription = null)
            Text(" Skip")
        }
        OutlinedButton(
            onClick = onDislike,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.ThumbDown, contentDescription = null)
            Text(" Dislike")
        }
        Button(
            onClick = onLike,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.ThumbUp, contentDescription = null)
            Text(" Like")
        }
    }
}

@Composable
fun FoodLoadingCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .height(22.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}

@Composable
fun TodayFoodCard(
    food: Food,
    isCelebrating: Boolean,
    modifier: Modifier = Modifier
) {
    val pulse = if (isCelebrating) {
        val transition = rememberInfiniteTransition(label = "gold_pulse")
        val animated by transition.animateFloat(
            initialValue = 0.35f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(950),
                repeatMode = RepeatMode.Reverse
            ),
            label = "gold_alpha"
        )
        animated
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isCelebrating) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFFFFD54F).copy(alpha = pulse),
                        shape = RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(2.dp)
    ) {
        FoodCardContent(food = food)
        if (isCelebrating) {
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text("Today's pick") },
                leadingIcon = { Icon(Icons.Filled.AutoAwesome, contentDescription = null) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun TodayFoodActionBar(
    onThatsIt: () -> Unit,
    onChangeIt: () -> Unit,
    onDontLikeIt: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onThatsIt,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("That's it")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onChangeIt,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Change it")
            }
            OutlinedButton(
                onClick = onDontLikeIt,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("Don't like it")
            }
        }
    }
}
