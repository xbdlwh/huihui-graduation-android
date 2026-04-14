package com.example.huihu_app.ui.screen

import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.huihu_app.AppContainer.Companion.BASE_URL_BACK_END

@Composable
fun FoodTrackScreen(token: String) {
    val encodedToken = Uri.encode(token)
    WebPage(
        url = "$BASE_URL_BACK_END/foodtrack?token=$encodedToken",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun WebPage(modifier: Modifier = Modifier, url: String) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { webView },
        update = {
            it.loadUrl(url)
        }
    )
}
