package com.example.huihu_app.ui.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.huihu_app.AppContainer
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.ForumViewModel
import com.example.huihu_app.ui.viewModel.TopicLikeUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    state: LazyListState,
    token: String,
    onWriteComment: (Int) -> Unit,
    onOpenTopicDetail: (Topic) -> Unit,
    viewModel: ForumViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val topics = viewModel.topics(token).collectAsLazyPagingItems()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val isRefreshing = topics.loadState.refresh is LoadState.Loading
    val context = LocalContext.current

    LaunchedEffect(uiState.message) {
        val message = uiState.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeMessage()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { topics.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        if (topics.loadState.refresh is LoadState.Error && topics.itemCount == 0) {
            val error = (topics.loadState.refresh as LoadState.Error).error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error.message ?: "Failed to load topics",
                    color = MaterialTheme.colorScheme.error
                )
                Button(onClick = { topics.retry() }) {
                    Text("Retry")
                }
            }
            return@PullToRefreshBox
        }

        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(topics.itemCount) { index ->
                val topic = topics[index]
                if (topic != null) {
                    val likeUi = uiState.likeOverrides[topic.id]
                    TopicItem(
                        topic = topic,
                        likeUi = likeUi,
                        likeActionInFlight = topic.id in uiState.inFlightTopicIds,
                        onToggleLike = { viewModel.onToggleLike(token, topic) },
                        onWriteComment = { onWriteComment(topic.id) },
                        onOpenTopicDetail = { onOpenTopicDetail(topic) }
                    )
                }
            }

            if (topics.loadState.append is LoadState.Loading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (topics.loadState.append is LoadState.Error) {
                val error = (topics.loadState.append as LoadState.Error).error
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error.message ?: "Failed to load more",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { topics.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicItem(
    topic: Topic,
    likeUi: TopicLikeUi?,
    likeActionInFlight: Boolean,
    onToggleLike: () -> Unit,
    onWriteComment: () -> Unit,
    onOpenTopicDetail: () -> Unit
) {
    val liked = likeUi?.liked ?: topic.liked
    val likeCount = likeUi?.likeCount ?: topic.like_count

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenTopicDetail)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            TopicAuthorAvatar(topic = topic)
            TopicItemContent(
                modifier = Modifier.weight(1f),
                topic = topic,
                liked = liked,
                likeCount = likeCount,
                likeActionInFlight = likeActionInFlight,
                onToggleLike = onToggleLike,
                onWriteComment = onWriteComment
            )
        }
    }
}

@Composable
private fun TopicAuthorAvatar(topic: Topic) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val profileUrl = topic.user_info?.profile
        if (!profileUrl.isNullOrBlank()) {
            AsyncImage(
                model = profileUrl.toAbsoluteImageUrl(),
                contentDescription = topic.user_info?.name ?: "profile",
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                    .alpha(0.2f)
            )
        }
    }
}

@Composable
private fun TopicItemContent(
    modifier: Modifier = Modifier,
    topic: Topic,
    liked: Boolean,
    likeCount: Int,
    likeActionInFlight: Boolean,
    onToggleLike: () -> Unit,
    onWriteComment: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = topic.user_info?.name ?: "User ${topic.user_id}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = topic.title,
            style = MaterialTheme.typography.titleMedium
        )
        TopicTextContent(content = topic.content)
        TopicImagesStrip(images = topic.images.orEmpty(), title = topic.title)
        TopicFooter(
            createdAt = topic.create_at,
            liked = liked,
            likeCount = likeCount,
            commentCount = topic.comment_count,
            likeActionInFlight = likeActionInFlight,
            onToggleLike = onToggleLike,
            onWriteComment = onWriteComment
        )
    }
}

@Composable
private fun TopicTextContent(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun TopicImagesStrip(images: List<String>, title: String) {
    if (images.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        items(images) { rawUrl ->
            AsyncImage(
                model = rawUrl.toAbsoluteImageUrl(),
                contentDescription = title,
                modifier = Modifier
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun TopicFooter(
    createdAt: String,
    liked: Boolean,
    likeCount: Int,
    commentCount: Int,
    likeActionInFlight: Boolean,
    onToggleLike: () -> Unit,
    onWriteComment: () -> Unit
) {
    Text(
        text = createdAt,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopicLikeButton(
            liked = liked,
            likeCount = likeCount,
            enabled = !likeActionInFlight,
            onClick = onToggleLike
        )
        TopicCommentCountButton(
            commentCount = commentCount,
            onClick = onWriteComment
        )
    }
}

@Composable
private fun TopicLikeButton(
    liked: Boolean,
    likeCount: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.height(34.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.height(14.dp)
        )
        Text(
            text = "$likeCount",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun TopicCommentCountButton(commentCount: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(34.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.height(14.dp)
        )
        Text(
            text = "$commentCount",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp
        )
    }
}

private fun String.toAbsoluteImageUrl(): String {
    if (startsWith("http://") || startsWith("https://")) return this
    val host = AppContainer.BASE_URL.trimEnd('/')
    val path = if (startsWith("/")) this else "/$this"
    return host + path
}
