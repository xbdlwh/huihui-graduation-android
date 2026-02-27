package com.example.huihu_app.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.huihu_app.AppContainer
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.MineViewModel

@Composable
fun MineScreen(
    token: String,
    onEditProfile: () -> Unit,
    onFoodLiked: () -> Unit,
    onTopicManage: () -> Unit,
    onSuggestion: () -> Unit,
    onFoodTrack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MineViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(token) {
        viewModel.loadMe(token)
    }
    DisposableEffect(lifecycleOwner, token) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadMe(token)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        UserInfoCard(
            userName = uiState.user?.name,
            profileUrl = uiState.user?.profile,
            isLoading = uiState.isLoading,
            onClick = onEditProfile
        )

        StatsCard(
            decisionSuccessCount = uiState.likeCount,
            decisionFailureCount = uiState.dislikeCount,
            tags = uiState.topTagNames,
            onOpenDecisionSuccess = onFoodLiked
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (uiState.isLoading && uiState.user == null) {
            CircularProgressIndicator()
        }
        ListButtonCard(
            onTopicManage = onTopicManage,
            onSuggestion = onSuggestion,
            onFoodTrack = onFoodTrack,
            onLogout = onLogout
        )
    }
}

@Composable
private fun UserInfoCard(
    userName: String?,
    profileUrl: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (profileUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .alpha(0.2f)
                )
            } else {
                AsyncImage(
                    model = profileUrl.toAbsoluteImageUrl(),
                    contentDescription = userName ?: "头像",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = userName ?: if (isLoading) "加载中..." else "未知用户",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "点击编辑资料",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun String.toAbsoluteImageUrl(): String {
    if (startsWith("http://") || startsWith("https://")) return this
    val host = AppContainer.BASE_URL.trimEnd('/')
    val path = if (startsWith("/")) this else "/$this"
    return host + path
}

@Composable
fun StatsCard(
    decisionSuccessCount: Int,
    decisionFailureCount: Int,
    tags: List<String>,
    onOpenDecisionSuccess: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),  // 圆角
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)  // 白色背景
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // 可点击的灰色文本
                if (decisionSuccessCount > 0) {
                    Row(
                        Modifier
                            .padding(6.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .clickable(onClick = onOpenDecisionSuccess),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "决策成功",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                        Image(Icons.Default.ChevronRight, contentDescription = null, alpha = 0.3f)
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "决策成功",
                    value = "$decisionSuccessCount",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "决策失败",
                    value = "$decisionFailureCount",
                    color = Color.Red
                )
            }

            // 成功率
            if (decisionSuccessCount + decisionFailureCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                val successRate =
                    (decisionSuccessCount.toFloat() / (decisionSuccessCount + decisionFailureCount) * 100).toInt()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "推荐成功率",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$successRate%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
            if (tags.size == 3) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "个人口味偏好",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${tags[0]} ${tags[1]} ${tags[2]}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }

    }
}

/**
 * 统计项
 */
@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ListButtonCard(
    modifier: Modifier = Modifier,
    onTopicManage: () -> Unit,
    onSuggestion: () -> Unit,
    onFoodTrack: () -> Unit,
    onLogout: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),  // 圆角
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)  // 白色背景
    ) {
        ListButton(
            text = "话题",
            icon = { Image(Icons.Filled.Topic, contentDescription = null) },
            onClick = onTopicManage
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(color = MaterialTheme.colorScheme.primary.copy(0.3f))
        )
        ListButton(
            text = "建议",
            icon = { Image(Icons.Filled.AcUnit, contentDescription = null) },
            onClick = onSuggestion
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(color = MaterialTheme.colorScheme.primary.copy(0.3f))
        )
        ListButton(
            text = "食物轨迹",
            icon = { Image(Icons.Filled.TrackChanges, contentDescription = null) },
            onClick = onFoodTrack
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(color = MaterialTheme.colorScheme.primary.copy(0.3f))
        )
        ListButton(
            text = "退出登录",
            icon = { Image(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
            onClick = onLogout
        )
    }
}

@Composable
fun ListButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(start = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row() {
            icon()
            Spacer(Modifier.width(10.dp))
            Text(text)
        }
        Image(Icons.Filled.ChevronRight, contentDescription = null, alpha = 0.3f)
    }
}

@Composable
private fun TasteTagChip(
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}
