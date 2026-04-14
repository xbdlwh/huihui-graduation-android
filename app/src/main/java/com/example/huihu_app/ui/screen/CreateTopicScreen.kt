package com.example.huihu_app.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.TopicImageUploadSection
import com.example.huihu_app.ui.viewModel.CreateTopicViewModel
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTopicScreen(
    token: String,
    commentToId: Int? = null,
    screenTitle: String = "发布帖子",
    submitButtonText: String = "发布帖子",
    onCreated: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: CreateTopicViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermission) {
            requestCurrentLocation(
                context = context,
                onStart = { viewModel.updateLocationState(true) },
                onSuccess = viewModel::updateLocationText,
                onError = viewModel::setLocationError,
                launchSuspend = { block -> scope.launch { block() } }
            )
        } else {
            viewModel.setLocationError("未授予定位权限。")
        }
    }

    LaunchedEffect(uiState.created) {
        if (uiState.created) {
            viewModel.clearCreatedFlag()
            onCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.content,
                onValueChange = viewModel::updateContent,
                placeholder = {
                    Text(
                        text = "请输入此刻的想法......",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                textStyle = MaterialTheme.typography.titleMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                minLines = 5
            )

            TopicImageUploadSection(
                selectedImages = uiState.selectedImages,
                onPickImages = { uris ->
                    viewModel.onImagesPicked(context.contentResolver, uris)
                },
                onRemoveImage = viewModel::removeImage,
                isUploadingImages = uiState.isUploadingImages,
                showMetaSection = true,
                locationText = uiState.locationText,
                isLocating = uiState.isLocating,
                onLocationClick = {
                    val hasFineLocation = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarseLocation = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasFineLocation || hasCoarseLocation) {
                        requestCurrentLocation(
                            context = context,
                            onStart = { viewModel.updateLocationState(true) },
                            onSuccess = viewModel::updateLocationText,
                            onError = viewModel::setLocationError,
                            launchSuspend = { block -> scope.launch { block() } }
                        )
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                isPublic = uiState.isPublic,
                onVisibilityChange = viewModel::updateVisibility,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.submit(token = token, commentToId = commentToId) },
                enabled = !uiState.isSubmitting && !uiState.isUploadingImages,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(submitButtonText)
                }
            }
        }
    }
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
private fun requestCurrentLocation(
    context: Context,
    onStart: () -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    launchSuspend: ((suspend () -> Unit) -> Unit)
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
        onError("无法获取定位服务。")
        return
    }
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
        !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    ) {
        onError("请先开启定位服务。")
        return
    }

    val provider = when {
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> null
    }
    if (provider == null) {
        onError("无法获取当前位置。")
        return
    }

    onStart()
    LocationManagerCompat.getCurrentLocation(
        locationManager,
        provider,
        CancellationSignal(),
        ContextCompat.getMainExecutor(context)
    ) { location ->
        if (location == null) {
            onError("当前位置获取失败，请稍后重试。")
            return@getCurrentLocation
        }
        launchSuspend {
            onSuccess(resolveLocationText(context, location))
        }
    }
}

private suspend fun resolveLocationText(context: Context, location: Location): String {
    return withContext(Dispatchers.IO) {
        runCatching {
            fetchAmapFormattedAddress(
                longitude = location.longitude,
                latitude = location.latitude
            ).ifBlank {
                "${location.latitude.formatCoord()}, ${location.longitude.formatCoord()}"
            }
        }.getOrElse {
            "${location.latitude.formatCoord()}, ${location.longitude.formatCoord()}"
        }
    }
}

private fun Double.formatCoord(): String = String.format(Locale.US, "%.4f", this)

private fun fetchAmapFormattedAddress(
    longitude: Double,
    latitude: Double
): String {
    val requestUrl = buildString {
        append("https://restapi.amap.com/v3/geocode/regeo")
        append("?output=json")
        append("&location=$longitude,$latitude")
        append("&key=1c4ac2b3fed0b7b5a8e26d2394f155d3")
        append("&radius=1000")
        append("&extensions=all")
    }


    val connection = (URL(requestUrl).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 10_000
        readTimeout = 10_000
    }

    return try {
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        JSONObject(body)
            .optJSONObject("regeocode")
            ?.optString("formatted_address")
            .orEmpty()
    } finally {
        connection.disconnect()
    }
}
private const val TAG = "CreateTopicScreen"