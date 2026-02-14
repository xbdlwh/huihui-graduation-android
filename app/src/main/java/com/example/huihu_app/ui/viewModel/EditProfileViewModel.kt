package com.example.huihu_app.ui.viewModel

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class EditProfileUiState(
    val username: String = "",
    val profileUrl: String? = null,
    val profilePreviewUri: Uri? = null,
    val isLoading: Boolean = false,
    val isUploadingProfile: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val topicRepository: TopicRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMe(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val response = authRepository.me(token)
            if (!response.isSuccess()) {
                _uiState.update { it.copy(isLoading = false, error = response.message) }
                return@launch
            }
            val user = response.data
            _uiState.update {
                it.copy(
                    username = user?.name.orEmpty(),
                    profileUrl = user?.profile,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun uploadProfile(contentResolver: ContentResolver, uri: Uri) {
        _uiState.update {
            it.copy(
                profilePreviewUri = uri,
                isUploadingProfile = true,
                error = null
            )
        }
        viewModelScope.launch {
            val part = uriToPart(contentResolver, uri)
            if (part == null) {
                _uiState.update {
                    it.copy(
                        isUploadingProfile = false,
                        error = "Failed to read image."
                    )
                }
                return@launch
            }

            val response = topicRepository.uploadImages(listOf(part))
            if (!response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        isUploadingProfile = false,
                        error = response.message
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    profileUrl = response.data?.firstOrNull(),
                    isUploadingProfile = false,
                    error = null
                )
            }
        }
    }

    fun save(token: String) {
        if (_uiState.value.isSaving || _uiState.value.isUploadingProfile) return
        if (_uiState.value.username.isBlank()) {
            _uiState.update { it.copy(error = "Username is required.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val response = authRepository.update(
                token = token,
                username = _uiState.value.username,
                profile = _uiState.value.profileUrl
            )
            if (!response.isSuccess()) {
                _uiState.update { it.copy(isSaving = false, error = response.message) }
                return@launch
            }
            _uiState.update { it.copy(isSaving = false, saved = true, error = null) }
        }
    }

    fun clearSavedFlag() {
        _uiState.update { it.copy(saved = false) }
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
