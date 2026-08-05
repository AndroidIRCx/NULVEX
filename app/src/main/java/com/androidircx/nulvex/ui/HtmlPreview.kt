package com.androidircx.nulvex.ui

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Renders a self-contained HTML document in a hardened WebView for note preview.
 *
 * Locked down for a zero-knowledge vault: JavaScript is disabled, all network and file/
 * content access is blocked, every resource request and navigation is intercepted, and the
 * document is loaded with a null base URL. Combined with FLAG_SECURE (set on the Activity)
 * this renders decrypted note HTML without letting it phone home or execute code.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SandboxedHtmlPreview(html: String, modifier: Modifier = Modifier) {
    val blockedResponse = WebResourceResponse("text/plain", "utf-8", null)
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                with(settings) {
                    javaScriptEnabled = false
                    allowFileAccess = false
                    allowContentAccess = false
                    blockNetworkLoads = true
                    blockNetworkImage = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    domStorageEnabled = false
                }
                setBackgroundColor(0x00000000)
                isVerticalScrollBarEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean = true // block all navigation

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse = blockedResponse // block all resource loads
                }
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }
    )
}
