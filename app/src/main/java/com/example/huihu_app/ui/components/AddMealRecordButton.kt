package com.example.huihu_app.ui.components

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.huihu_app.data.repository.MealRecordRepository
import com.example.huihu_app.data.repository.TopicRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealRecordButton(
    onCreateRecord: (String, Double) -> Unit,
    topicRepository: TopicRepository? = null,
    mealRecordRepository: MealRecordRepository? = null,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    ExtendedFloatingActionButton(
        onClick = { showSheet = true },
        modifier = modifier
    ) {
        Icon(Icons.Default.Fastfood, contentDescription = null)
        Text("添加饮食记录")
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            AddMealRecordContent(
                onDismiss = { showSheet = false },
                onCreateRecord = { mealType, calories ->
                    onCreateRecord(mealType, calories)
                    showSheet = false
                },
                topicRepository = topicRepository,
                mealRecordRepository = mealRecordRepository
            )
        }
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

@Composable
fun AddMealRecordContent(
    onDismiss: () -> Unit,
    onCreateRecord: (String, Double) -> Unit,
    topicRepository: TopicRepository? = null,
    mealRecordRepository: MealRecordRepository? = null
) {
    var inputMode by remember { mutableStateOf("manual") }
    val mealTypes = listOf("breakfast", "lunch", "dinner", "snack")
    var selectedMealType by remember { mutableStateOf("lunch") }
    var caloriesText by remember { mutableStateOf("") }
    var isRecognizing by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedKcal by remember { mutableStateOf<Double?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    )
    { uri ->
        if (uri != null) {
            selectedImageUri = uri
            isRecognizing = true
            errorMsg = null
            scope.launch {
                val part = uriToPart(context.contentResolver, uri)
                if (part == null) {
                    errorMsg = "读取图片失败"
                    isRecognizing = false
                    return@launch
                }
                val response = topicRepository?.uploadImages(listOf(part))
                if (response?.isSuccess() == true) {
                    val imageUrl = response.data?.firstOrNull()
                    if (imageUrl != null) {
                        val encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                        try {
                            val recognizeResponse = mealRecordRepository?.recognizeFood("$encodedUrl")
                            if (recognizeResponse?.isSuccess() == true) {
                                recognizedKcal = recognizeResponse.data
                                caloriesText = recognizedKcal?.toInt()?.toString() ?: ""
                            } else {
                                errorMsg = recognizeResponse?.message ?: "识别失败"
                            }
                        } catch (e: Exception) {
                            errorMsg = e.message ?: "识别失败"
                        }
                    }
                } else {
                    errorMsg = response?.message ?: "上传图片失败"
                }
                isRecognizing = false
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .height(400.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    )
    {
        Text(
            text = "添加饮食记录",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            FilterChip(
                selected = inputMode == "manual",
                onClick = { inputMode = "manual" },
                label = { Text("手动输入") }
            )
            FilterChip(
                selected = inputMode == "image",
                onClick = { inputMode = "image" },
                label = { Text("图片识别") }
            )
        }

        if (inputMode == "manual") {
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
                            .clickable { selectedMealType = mealType },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
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

            OutlinedTextField(
                value = caloriesText,
                onValueChange = { caloriesText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("卡路里 (kcal)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else
        {
            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "选择的食物图片",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {
                            selectedImageUri = null
                            recognizedKcal = null
                            caloriesText = ""
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "移除图片",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
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
                    text = errorMsg ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (recognizedKcal != null || caloriesText.isNotEmpty()) {
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("识别结果 (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

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
                            .clickable { selectedMealType = mealType },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
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

        val calories = caloriesText.toDoubleOrNull() ?: 0.0
        Button(
            onClick = {
                if (calories > 0) {
                    onCreateRecord(selectedMealType, calories)
                }
            },
            enabled = calories > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("创建记录")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
