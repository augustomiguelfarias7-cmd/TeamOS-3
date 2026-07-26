package com.teamos.store

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R

/**
 * Loja de Apps do TeamOS 3.0
 * Instala "apps" como WebView wrappers de sites
 * (ChatGPT, Gemini, WhatsApp Web, GitHub, etc.)
 *
 * Visual: One UI 8 + Android 16
 */
class StoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        // Lista de apps WebView disponíveis
        // Ao "instalar", cria atalho que abre o site em WebView nativo
    }
}
