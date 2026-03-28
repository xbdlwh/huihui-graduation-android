package com.example.huihu_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
import com.example.huihu_app.data.model.statusText
import com.example.huihu_app.data.model.typeText
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.SuggestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionScreen(
    token: String,
    onBack: () -> Unit,
    onAddSuggestion: () -> Unit,
    onOpenSuggestion: (Suggestion) -> Unit,
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
                title = { Text("建议") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddSuggestion) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "新增建议"
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
                        text = uiState.error ?: "加载建议失败",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadMySuggestions(token) }) {
                        Text("重试")
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
                        SuggestionItem(
                            suggestion = suggestion,
                            onClick = { onOpenSuggestion(suggestion) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: Suggestion,
    onClick: () -> Unit
) {
    if (suggestion.food != null) {
        SuggestionItemWithFood(suggestion = suggestion, onClick = onClick)
    } else {
        SuggestionItemWithoutFood(suggestion = suggestion, onClick = onClick)
    }
}

@Composable
private fun SuggestionItemWithFood(
    suggestion: Suggestion,
    onClick: () -> Unit
) {
    val imageUrl = suggestion.food?.image ?: suggestion.images?.firstOrNull()
    Card(Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(end = 14.dp)
                , horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxHeight()) {
                Box(modifier = Modifier.fillMaxHeight()) {
                    AsyncImage(
                        model = AppContainer.BASE_URL + imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(10f / 14f),
                        contentScale = ContentScale.Crop
                    )
                    Text(suggestion.typeText(), Modifier.padding(4.dp).clip(shape = RoundedCornerShape(5.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(3.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelSmall)
                }
                Column(Modifier
                    .fillMaxHeight().padding(10.dp),
                ) {
                    Text(text =  suggestion.food!!.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight(800), modifier = Modifier.padding(bottom = 10.dp))
                    Text(suggestion.restaurant!!.name,style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 7.dp))
                    Text(suggestion.content,style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 7.dp))
                    Text(suggestion.created_at,style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 7.dp))
                }
            }
            Column(Modifier
                .fillMaxHeight()
                .padding(vertical = 10.dp)) {
                Text(
                    text = suggestion.statusText(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}

@Composable
private fun SuggestionItemWithoutFood(
    suggestion: Suggestion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 108.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (suggestion.type.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f)
                    ) {
                        Text(
                            text = suggestion.typeText(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Text(
                        text = "建议",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = suggestion.statusText(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = suggestion.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = suggestion.created_at,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun String.toAbsoluteImageUrl(): String {
    if (startsWith("http://") || startsWith("https://")) return this
    val host = AppContainer.BASE_URL.trimEnd('/')
    val path = if (startsWith("/")) this else "/$this"
    return host + path
}
