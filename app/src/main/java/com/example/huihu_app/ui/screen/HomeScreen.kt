package com.example.huihu_app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState.selectedTab) {
                            0 -> "Forum"
                            1 -> "Today's Food!"
                            else -> "Mine"
                        }
                    )
                }
            )
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
                    Icon(Icons.Filled.Add, contentDescription = "Create topic")
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
                0 -> ForumScreen(state = state, token = token)
                1 -> FoodRecommendationScreen(token = token)
                else -> MineScreen(onLogout = viewModel::logout)
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Filled.Forum, contentDescription = "Forum") },
            label = { Text("Forum") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Filled.Restaurant, contentDescription = "Food Recommendation") },
            label = { Text("Food") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Mine") },
            label = { Text("Mine") }
        )
    }
}
