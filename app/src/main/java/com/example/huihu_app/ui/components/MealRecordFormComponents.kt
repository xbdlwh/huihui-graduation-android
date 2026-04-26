package com.example.huihu_app.ui.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// ─── Utility ───────────────────────────────────────────────────────────────────

internal fun uriToPart(contentResolver: ContentResolver, uri: Uri): MultipartBody.Part? {
    val mimeType = contentResolver.getType(uri) ?: "image/*"
    val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
    } ?: "upload_${System.currentTimeMillis()}.jpg"

    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("files", fileName, requestBody)
}

// ─── Mode & Meal Type Selectors ───────────────────────────────────────────────

@Composable
fun InputModeSelector(
    inputMode: String,
    onInputModeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = inputMode == "manual",
            onClick = { onInputModeChange("manual") },
            label = { Text("手动输入") }
        )
        FilterChip(
            selected = inputMode == "image",
            onClick = { onInputModeChange("image") },
            label = { Text("图片识别") }
        )
    }
}

@Composable
fun MealTypeSelector(
    selectedMealType: String,
    onMealTypeChange: (String) -> Unit
) {
    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")

    Text(
        text = "选择餐次",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        mealTypes.forEach { mealType ->
            val isSelected = selectedMealType == mealType
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onMealTypeChange(mealType) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = when (mealType) {
                        "breakfast" -> "早餐"
                        "lunch" -> "午餐"
                        "dinner" -> "晚餐"
                        "snack" -> "零食"
                        else -> mealType
                    },
                    modifier = Modifier.padding(12.dp),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ─── Manual Input ──────────────────────────────────────────────────────────────

@Composable
fun CaloriesTextField(
    caloriesText: String,
    onCaloriesChange: (String) -> Unit,
    label: String = "卡路里 (kcal)"
) {
    OutlinedTextField(
        value = caloriesText,
        onValueChange = { onCaloriesChange(it.filter { c -> c.isDigit() || c == '.' }) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ManualInputSection(
    selectedMealType: String,
    onMealTypeChange: (String) -> Unit,
    caloriesText: String,
    onCaloriesChange: (String) -> Unit
) {
    MealTypeSelector(
        selectedMealType = selectedMealType,
        onMealTypeChange = onMealTypeChange
    )
    CaloriesTextField(
        caloriesText = caloriesText,
        onCaloriesChange = onCaloriesChange
    )
}

// ─── Image Recognition ─────────────────────────────────────────────────────────

@Composable
fun ImagePickerCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击上传食物图片",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImagePreviewSection(
    imageUri: Uri,
    onRemove: () -> Unit,
    recognizedCalories: String? = null,
    recognizedFoodName: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "选择的食物图片",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "移除图片",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        if (recognizedCalories != null && recognizedCalories.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (recognizedFoodName != null) {
                        Text(
                            text = recognizedFoodName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "${recognizedCalories} 千卡",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageRecognitionSection(
    selectedImageUri: Uri?,
    isRecognizing: Boolean,
    errorMsg: String?,
    selectedMealType: String,
    onMealTypeChange: (String) -> Unit,
    caloriesText: String,
    onCaloriesChange: (String) -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    recognizedCalories: String? = null,
    recognizedFoodName: String? = null
) {
    if (selectedImageUri != null) {
        ImagePreviewSection(imageUri = selectedImageUri, onRemove = onRemoveImage, recognizedCalories = recognizedCalories, recognizedFoodName = recognizedFoodName)
    } else {
        ImagePickerCard(onClick = onPickImage)
    }

    if (isRecognizing) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text("正在识别...", style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (errorMsg != null) {
        Text(
            text = errorMsg,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (selectedImageUri != null) {
        MealTypeSelector(
            selectedMealType = selectedMealType,
            onMealTypeChange = onMealTypeChange
        )
    }
}
