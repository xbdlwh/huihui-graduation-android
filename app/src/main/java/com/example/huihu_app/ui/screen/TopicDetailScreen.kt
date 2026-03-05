package com.example.huihu_app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.huihu_app.AppContainer
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.TopicDetailViewModel
import com.example.huihu_app.ui.viewModel.TopicLikeUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    token: String,
    topic: Topic,
    onBack: () -> Unit,
    onWriteComment: (Int) -> Unit,
    onOpenTopicDetail: (Topic) -> Unit,
    onOpenImagePreview: (List<String>, Int) -> Unit,
    viewModel: TopicDetailViewModel = viewModel(
        key = "topic_detail_${topic.id}",
        factory = AppViewModelProvider.FACTORY
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(topic.id) {
        viewModel.loadComments(token = token, topicId = topic.id)
    }

    LaunchedEffect(uiState.message) {
        val message = uiState.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.consumeMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("帖子详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onWriteComment(topic.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "写评论"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val topLikeUi = uiState.likeOverrides[topic.id]
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                TopicCard(
                    topic = topic,
                    likeUi = topLikeUi,
                    likeActionInFlight = topic.id in uiState.inFlightTopicIds,
                    onToggleLike = { viewModel.onToggleLike(token, topic) },
                    onCommentClick = { onWriteComment(topic.id) },
                    onTopicClick = null,
                    showTitle = true,
                    onOpenImagePreview = onOpenImagePreview
                )
            }
            item {
                Text(
                    text = "评论（${uiState.comments.size}）",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (uiState.isLoading && uiState.comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.error != null && uiState.comments.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "加载评论失败",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadComments(token, topic.id) }) {
                            Text("重试")
                        }
                    }
                }
            } else {
                items(uiState.comments, key = { it.id }) { comment ->
                    TopicCard(
                        topic = comment,
                        likeUi = uiState.likeOverrides[comment.id],
                        likeActionInFlight = comment.id in uiState.inFlightTopicIds,
                        onToggleLike = { viewModel.onToggleLike(token, comment) },
                        onCommentClick = { onWriteComment(comment.id) },
                        onTopicClick = { onOpenTopicDetail(comment) },
                        showTitle = false,
                        onOpenImagePreview = onOpenImagePreview
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicCard(
    topic: Topic,
    likeUi: TopicLikeUi?,
    likeActionInFlight: Boolean,
    onToggleLike: () -> Unit,
    onCommentClick: () -> Unit,
    onTopicClick: (() -> Unit)?,
    showTitle: Boolean,
    onOpenImagePreview: (List<String>, Int) -> Unit
) {
    val liked = likeUi?.liked ?: topic.liked
    val likeCount = likeUi?.likeCount ?: topic.like_count

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onTopicClick != null) Modifier.clickable(onClick = onTopicClick)
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            TopicProfileAvatar(topic.user_info?.profile, topic.user_info?.name)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = topic.user_info?.name ?: "用户 ${topic.user_id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (showTitle) {
//                    Text(
//                        text = topic.title,
//                        style = MaterialTheme.typography.titleMedium
//                    )
                }
                Text(
                    text = topic.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val images = topic.images.orEmpty()
                if (images.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(images.size) { imageIndex ->
                            val imageUrl = images[imageIndex]
                            AsyncImage(
                                model = imageUrl.toAbsoluteImageUrl(),
                                contentDescription = topic.title,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onOpenImagePreview(images, imageIndex) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Text(
                    text = topic.create_at,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TopicActionRow(
                    liked = liked,
                    likeCount = likeCount,
                    commentCount = topic.comment_count,
                    likeActionInFlight = likeActionInFlight,
                    onToggleLike = onToggleLike,
                    onCommentClick = onCommentClick
                )
            }
        }
    }
}

@Composable
private fun TopicActionRow(
    liked: Boolean,
    likeCount: Int,
    commentCount: Int,
    likeActionInFlight: Boolean,
    onToggleLike: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onToggleLike,
            enabled = !likeActionInFlight,
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

        OutlinedButton(
            onClick = onCommentClick,
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
}

@Composable
private fun TopicProfileAvatar(profileUrl: String?, username: String?) {
    if (!profileUrl.isNullOrBlank()) {
        AsyncImage(
            model = profileUrl.toAbsoluteImageUrl(),
            contentDescription = username ?: "头像",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .alpha(0.2f)
        )
    }
}

private fun String.toAbsoluteImageUrl(): String {
    if (startsWith("http://") || startsWith("https://")) return this
    val host = AppContainer.BASE_URL.trimEnd('/')
    val path = if (startsWith("/")) this else "/$this"
    return host + path
}
