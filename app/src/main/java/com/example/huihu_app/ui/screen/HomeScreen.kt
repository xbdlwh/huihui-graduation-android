package com.example.huihu_app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.screen.home.FoodRecommendationScreen
import com.example.huihu_app.ui.screen.home.ForumScreen
import com.example.huihu_app.ui.screen.home.MineScreen
import com.example.huihu_app.ui.viewModel.HomeViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    token: String,
    onCreateTopic: () -> Unit,
    onWriteComment: (Int) -> Unit,
    onOpenTopicDetail: (Topic) -> Unit,
    onOpenImagePreview: (List<String>, Int) -> Unit,
    onEditProfile: () -> Unit,
    onFoodLiked: () -> Unit,
    onTopicManage: () -> Unit,
    onSuggestion: () -> Unit,
    onFoodTrack: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettingsSheet by remember { mutableStateOf(false) }

    val state = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (uiState.selectedTab != 2) {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                when (uiState.selectedTab) {
                                    0 -> "论坛"
                                    1 -> "今日吃什么"
                                    else -> "我的"
                                }
                            )
                            if (uiState.selectedTab == 1 && uiState.isRandomMode) {
                                Icon(
                                    imageVector = Icons.Filled.Shuffle,
                                    contentDescription = "随机模式已开启",
                                    modifier = Modifier.padding(start = 6.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        if (uiState.selectedTab == 1) {
                            IconButton(onClick = { showSettingsSheet = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "设置"
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            HomeBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab
            )
        },
        floatingActionButton = {
            if (uiState.selectedTab == 0) {
                FloatingActionButton(onClick = onCreateTopic) {
                    Icon(Icons.Filled.Add, contentDescription = "发帖")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.selectedTab) {
                0 -> ForumScreen(
                    state = state,
                    token = token,
                    onWriteComment = onWriteComment,
                    onOpenTopicDetail = onOpenTopicDetail,
                    onOpenImagePreview = onOpenImagePreview
                )
                1 -> FoodRecommendationScreen(
                    token = token,
                    isRandomMode = uiState.isRandomMode
                )
                else -> MineScreen(
                    token = token,
                    onEditProfile = onEditProfile,
                    onFoodLiked = onFoodLiked,
                    onTopicManage = onTopicManage,
                    onSuggestion = onSuggestion,
                    onFoodTrack = onFoodTrack,
                    onLogout = viewModel::logout
                )
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = false,
            ),

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "推荐设置",
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "随机模式")
                    Switch(
                        checked = uiState.isRandomMode,
                        onCheckedChange = viewModel::setRandomMode
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        windowInsets = WindowInsets(top = 0, bottom = 0)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Filled.Forum, contentDescription = "论坛") },
            label = { Text("论坛") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Filled.Restaurant, contentDescription = "美食推荐") },
            label = { Text("美食") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "我的") },
            label = { Text("我的") }
        )
    }
}
