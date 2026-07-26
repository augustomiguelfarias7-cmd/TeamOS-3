package com.teamos.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R

/**
 * Navegador próprio do TeamOS 3.0
 * Baseado em WebView (motor Chromium embutido no Android)
 * Com suporte a abas
 *
 * Visual: One UI 8 + Android 16
 */
class TeamOSBrowserActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val tabs = mutableListOf<String>()
    private var currentTab = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teamos_browser)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Aba inicial
        openTab("https://www.mozilla.org")
    }

    fun openTab(url: String) {
        tabs.add(url)
        currentTab = tabs.lastIndex
        webView.loadUrl(url)
    }

    fun switchTab(index: Int) {
        if (index in tabs.indices) {
            currentTab = index
            webView.loadUrl(tabs[index])
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
