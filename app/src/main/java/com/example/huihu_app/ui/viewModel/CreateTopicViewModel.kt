package com.example.huihu_app.ui.viewModel

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val DEFAULT_TOPIC_TITLE = "默认标题"

data class SelectedImage(
    val uri: Uri,
    val uploadedUrl: String? = null,
    val isUploading: Boolean = false
)

data class CreateTopicUiState(
    val title: String = "",
    val content: String = "",
    val selectedImages: List<SelectedImage> = emptyList(),
    val locationText: String = "点击获取位置",
    val isLocating: Boolean = false,
    val isPublic: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val created: Boolean = false
) {
    val isUploadingImages: Boolean
        get() = selectedImages.any { it.isUploading }

    val hasPendingUpload: Boolean
        get() = selectedImages.any { !it.isUploading && it.uploadedUrl == null }
}

class CreateTopicViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTopicUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun updateVisibility(isPublic: Boolean) {
        _uiState.update { it.copy(isPublic = isPublic) }
    }

    fun updateLocationState(isLocating: Boolean) {
        _uiState.update { it.copy(isLocating = isLocating) }
    }

    fun updateLocationText(locationText: String) {
        _uiState.update { it.copy(locationText = locationText, isLocating = false, error = null) }
    }

    fun setLocationError(message: String) {
        _uiState.update { it.copy(isLocating = false, error = message) }
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
                markUploadFailed(newUris, "读取所选图片失败。")
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
                    error = if (hasMissing) "部分图片上传失败，请重新选择或移除。" else null
                )
            }
        }
    }

    fun removeImage(uri: Uri) {
        _uiState.update {
            it.copy(selectedImages = it.selectedImages.filterNot { image -> image.uri == uri })
        }
    }

    fun clearCreatedFlag() {
        _uiState.update { it.copy(created = false) }
    }

    fun submit(token: String, commentToId: Int? = null) {
        val state = _uiState.value
        if (state.isSubmitting) return
        if (state.content.isBlank()) {
            _uiState.update { it.copy(error = "内容不能为空。") }
            return
        }
        if (state.isUploadingImages) {
            _uiState.update { it.copy(error = "请等待图片上传完成。") }
            return
        }
        if (state.hasPendingUpload) {
            _uiState.update { it.copy(error = "部分图片尚未上传，请移除或重新选择。") }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, error = null) }
        val effectiveTitle = state.title.trim().ifBlank { DEFAULT_TOPIC_TITLE }

        viewModelScope.launch {
            val createRes = topicRepository.createTopic(
                token = token,
                title = effectiveTitle,
                content = state.content,
                images = state.selectedImages.mapNotNull { it.uploadedUrl },
                commentToId = commentToId
            )

            if (!createRes.isSuccess()) {
                _uiState.update {
                    it.copy(isSubmitting = false, error = createRes.message)
                }
                return@launch
            }

            _uiState.update {
                CreateTopicUiState(created = true)
            }
        }
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
