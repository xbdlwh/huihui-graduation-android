package com.example.huihu_app.ui.viewModel

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.RestaurantFoodSimple
import com.example.huihu_app.data.model.SimpleRestaurant
import com.example.huihu_app.data.model.SuggestionType
import com.example.huihu_app.data.repository.RestaurantRepository
import com.example.huihu_app.data.repository.SuggestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class AddSuggestionUiState(
    val type: SuggestionType = SuggestionType.ADD_FOOD,
    val content: String = "",
    val restaurants: List<SimpleRestaurant> = emptyList(),
    val foods: List<RestaurantFoodSimple> = emptyList(),
    val selectedRestaurantId: Int? = null,
    val selectedFoodId: Int? = null,
    val selectedImages: List<SelectedImage> = emptyList(),
    val isLoadingRestaurants: Boolean = false,
    val isLoadingFoods: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val created: Boolean = false
) {
    val isUploadingImages: Boolean
        get() = selectedImages.any { it.isUploading }

    val hasPendingUpload: Boolean
        get() = selectedImages.any { !it.isUploading && it.uploadedUrl == null }
}

class AddSuggestionViewModel(
    private val suggestionRepository: SuggestionRepository,
    private val restaurantRepository: RestaurantRepository,
    private val topicRepository: com.example.huihu_app.data.repository.TopicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddSuggestionUiState())
    val uiState = _uiState.asStateFlow()

    fun updateType(type: SuggestionType) {
        _uiState.update { state ->
            when (type) {
                SuggestionType.ADD_FOOD -> state.copy(
                    type = type,
                    selectedRestaurantId = null,
                    selectedFoodId = null,
                    foods = emptyList(),
                    error = null
                )
                SuggestionType.UPDATE_FOOD -> state.copy(
                    type = type,
                    error = null
                )
                SuggestionType.OTHER -> state.copy(
                    type = type,
                    error = null
                )
            }
        }

        if (type != SuggestionType.ADD_FOOD) {
            loadRestaurants()
            val restaurantId = _uiState.value.selectedRestaurantId
            if (restaurantId != null && _uiState.value.foods.isEmpty()) {
                loadFoods(restaurantId)
            }
        }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun loadRestaurants() {
        val state = _uiState.value
        if (state.isLoadingRestaurants || state.restaurants.isNotEmpty()) return

        _uiState.update { it.copy(isLoadingRestaurants = true, error = null) }
        viewModelScope.launch {
            val response = restaurantRepository.simpleRestaurants()
            if (!response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        isLoadingRestaurants = false,
                        error = response.message
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    restaurants = response.data.orEmpty(),
                    isLoadingRestaurants = false
                )
            }
        }
    }

    fun selectRestaurant(restaurantId: Int?) {
        val previous = _uiState.value.selectedRestaurantId
        if (previous == restaurantId) return

        _uiState.update {
            it.copy(
                selectedRestaurantId = restaurantId,
                selectedFoodId = null,
                foods = if (restaurantId == null) emptyList() else it.foods
            )
        }

        if (restaurantId != null) {
            loadFoods(restaurantId)
        }
    }

    fun selectFood(foodId: Int?) {
        _uiState.update { it.copy(selectedFoodId = foodId) }
    }

    fun loadFoods(restaurantId: Int) {
        if (_uiState.value.isLoadingFoods) return
        _uiState.update {
            it.copy(
                isLoadingFoods = true,
                foods = emptyList(),
                selectedFoodId = null,
                error = null
            )
        }

        viewModelScope.launch {
            val response = restaurantRepository.restaurantFoods(restaurantId)
            if (!response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        isLoadingFoods = false,
                        error = response.message
                    )
                }
                return@launch
            }
            _uiState.update {
                it.copy(
                    foods = response.data.orEmpty(),
                    isLoadingFoods = false
                )
            }
        }
    }

    fun onImagesPicked(contentResolver: ContentResolver, uris: List<Uri>) {
        if (uris.isEmpty()) return
        val current = _uiState.value.selectedImages
        val existing = current.map { it.uri.toString() }.toSet()
        val newUris = uris.filterNot { it.toString() in existing }
        if (newUris.isEmpty()) return

        _uiState.update { state ->
            state.copy(
                selectedImages = state.selectedImages + newUris.map { SelectedImage(it, isUploading = true) },
                error = null
            )
        }

        viewModelScope.launch {
            val parts = newUris.mapNotNull { uriToPart(contentResolver, it) }
            if (parts.isEmpty()) {
                markUploadFailed(newUris, "Failed to read selected images.")
                return@launch
            }

            val uploadRes = topicRepository.uploadImages(parts)
            if (!uploadRes.isSuccess()) {
                markUploadFailed(newUris, uploadRes.message)
                return@launch
            }

            val urls = uploadRes.data ?: emptyList()
            _uiState.update { state ->
                val updated = state.selectedImages.map { image ->
                    val idx = newUris.indexOfFirst { it.toString() == image.uri.toString() }
                    if (idx == -1) {
                        image
                    } else {
                        image.copy(
                            isUploading = false,
                            uploadedUrl = urls.getOrNull(idx)
                        )
                    }
                }
                val hasMissing = updated.any { !it.isUploading && it.uploadedUrl == null }
                state.copy(
                    selectedImages = updated,
                    error = if (hasMissing) "Some images failed to upload. Please reselect or remove them." else null
                )
            }
        }
    }

    fun removeImage(uri: Uri) {
        _uiState.update {
            it.copy(selectedImages = it.selectedImages.filterNot { image -> image.uri == uri })
        }
    }

    fun submit(token: String) {
        val state = _uiState.value
        if (state.isSubmitting) return
        if (state.content.isBlank()) {
            _uiState.update { it.copy(error = "Content is required.") }
            return
        }
        if (state.isUploadingImages) {
            _uiState.update { it.copy(error = "Please wait for image uploads to finish.") }
            return
        }
        if (state.hasPendingUpload) {
            _uiState.update { it.copy(error = "Some images are not uploaded yet. Remove them or reselect.") }
            return
        }
        if (state.type == SuggestionType.UPDATE_FOOD) {
            if (state.selectedRestaurantId == null || state.selectedFoodId == null) {
                _uiState.update { it.copy(error = "Please select restaurant and food for UPDATE_FOOD.") }
                return
            }
        }

        val restaurantId = when (state.type) {
            SuggestionType.ADD_FOOD -> null
            SuggestionType.UPDATE_FOOD -> state.selectedRestaurantId
            SuggestionType.OTHER -> state.selectedRestaurantId
        }
        val foodId = when (state.type) {
            SuggestionType.ADD_FOOD -> null
            SuggestionType.UPDATE_FOOD -> state.selectedFoodId
            SuggestionType.OTHER -> state.selectedFoodId
        }

        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val response = suggestionRepository.createSuggestion(
                token = token,
                content = state.content,
                images = state.selectedImages.mapNotNull { it.uploadedUrl },
                type = state.type.name,
                foodId = foodId,
                restaurantId = restaurantId
            )

            if (!response.isSuccess()) {
                _uiState.update { it.copy(isSubmitting = false, error = response.message) }
                return@launch
            }

            _uiState.update { it.copy(isSubmitting = false, created = true, error = null) }
        }
    }

    fun clearCreatedFlag() {
        _uiState.update { it.copy(created = false) }
    }

    private fun markUploadFailed(failedUris: List<Uri>, message: String) {
        _uiState.update { state ->
            state.copy(
                selectedImages = state.selectedImages.map { image ->
                    if (failedUris.any { it.toString() == image.uri.toString() }) {
                        image.copy(isUploading = false, uploadedUrl = null)
                    } else {
                        image
                    }
                },
                error = message
            )
        }
    }

    private fun uriToPart(contentResolver: ContentResolver, uri: Uri): MultipartBody.Part? {
        val mimeType = contentResolver.getType(uri) ?: "image/*"
        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
        } ?: "upload_${System.currentTimeMillis()}.jpg"

        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("files", fileName, requestBody)
    }
}
