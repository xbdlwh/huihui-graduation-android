package com.example.huihu_app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.huihu_app.AppContainer
import com.example.huihu_app.data.model.Suggestion
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.SuggestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionScreen(
    token: String,
    onBack: () -> Unit,
    onAddSuggestion: () -> Unit,
    viewModel: SuggestionViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(token) {
        viewModel.loadMySuggestions(token)
    }
    DisposableEffect(lifecycleOwner, token) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadMySuggestions(token)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggestion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddSuggestion) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add suggestion"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.suggestions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            uiState.error != null && uiState.suggestions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "Failed to load suggestions",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadMySuggestions(token) }) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.suggestions, key = { it.id }) { suggestion ->
                        SuggestionItem(suggestion)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: Suggestion) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = suggestion.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = suggestion.created_at,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = suggestion.status,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = suggestion.type,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val food = suggestion.food
            val restaurant = suggestion.restaurant
            if (food != null && restaurant != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!food.image.isNullOrBlank()) {
                        AsyncImage(
                            model = food.image.toAbsoluteImageUrl(),
                            contentDescription = food.name,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = food.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
