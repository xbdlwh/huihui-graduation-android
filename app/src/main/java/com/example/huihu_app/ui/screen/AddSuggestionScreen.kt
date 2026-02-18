package com.example.huihu_app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.data.model.RestaurantFoodSimple
import com.example.huihu_app.data.model.SimpleRestaurant
import com.example.huihu_app.data.model.SuggestionType
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.TopicImageUploadSection
import com.example.huihu_app.ui.viewModel.AddSuggestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSuggestionScreen(
    token: String,
    onBack: () -> Unit,
    viewModel: AddSuggestionViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.created) {
        if (uiState.created) {
            viewModel.clearCreatedFlag()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Suggestion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Type", style = MaterialTheme.typography.labelLarge)
            SuggestionTypeSelector(
                selected = uiState.type,
                onSelect = viewModel::updateType
            )

            OutlinedTextField(
                value = uiState.content,
                onValueChange = viewModel::updateContent,
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            val showRestaurantSelector = uiState.type != SuggestionType.ADD_FOOD
            val showFoodSelector = when (uiState.type) {
                SuggestionType.UPDATE_FOOD -> true
                SuggestionType.OTHER -> uiState.selectedRestaurantId != null
                SuggestionType.ADD_FOOD -> false
            }

            if (showRestaurantSelector) {
                RestaurantSelector(
                    restaurants = uiState.restaurants,
                    selectedRestaurantId = uiState.selectedRestaurantId,
                    isLoading = uiState.isLoadingRestaurants,
                    onSelectRestaurant = viewModel::selectRestaurant
                )
            }

            if (showFoodSelector) {
                FoodSelector(
                    foods = uiState.foods,
                    selectedFoodId = uiState.selectedFoodId,
                    isLoading = uiState.isLoadingFoods,
                    onSelectFood = viewModel::selectFood
                )
            }

            TopicImageUploadSection(
                selectedImages = uiState.selectedImages,
                onPickImages = { uris -> viewModel.onImagesPicked(context.contentResolver, uris) },
                onRemoveImage = viewModel::removeImage,
                isUploadingImages = uiState.isUploadingImages,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.submit(token) },
                enabled = !uiState.isSubmitting && !uiState.isUploadingImages,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Suggestion")
                }
            }
        }
    }
}

@Composable
private fun SuggestionTypeSelector(
    selected: SuggestionType,
    onSelect: (SuggestionType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SuggestionType.entries.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(type) },
                label = { Text(type.name) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestaurantSelector(
    restaurants: List<SimpleRestaurant>,
    selectedRestaurantId: Int?,
    isLoading: Boolean,
    onSelectRestaurant: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = restaurants.firstOrNull { it.id == selectedRestaurantId }?.name ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Restaurant") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (isLoading) {
                DropdownMenuItem(
                    text = { Text("Loading...") },
                    onClick = {}
                )
            } else {
                if (selectedRestaurantId != null) {
                    DropdownMenuItem(
                        text = { Text("Clear") },
                        onClick = {
                            onSelectRestaurant(null)
                            expanded = false
                        }
                    )
                }
                restaurants.forEach { restaurant ->
                    DropdownMenuItem(
                        text = { Text(restaurant.name) },
                        onClick = {
                            onSelectRestaurant(restaurant.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSelector(
    foods: List<RestaurantFoodSimple>,
    selectedFoodId: Int?,
    isLoading: Boolean,
    onSelectFood: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = foods.firstOrNull { it.id == selectedFoodId }?.name ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Food") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (isLoading) {
                DropdownMenuItem(
                    text = { Text("Loading...") },
                    onClick = {}
                )
            } else {
                if (selectedFoodId != null) {
                    DropdownMenuItem(
                        text = { Text("Clear") },
                        onClick = {
                            onSelectFood(null)
                            expanded = false
                        }
                    )
                }
                foods.forEach { food ->
                    DropdownMenuItem(
                        text = { Text(food.name) },
                        onClick = {
                            onSelectFood(food.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
