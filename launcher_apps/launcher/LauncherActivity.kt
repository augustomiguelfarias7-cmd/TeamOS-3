package com.teamos.launcher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R

/**
 * Launcher principal do TeamOS 3.0
 * Visual: Android 16 Material 3 Expressive + One UI 8
 *
 * Contém:
 * - Grade de apps
 * - Dock
 * - Quick Settings Panel (swipe down)
 */
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // Quick Settings será controlado via SystemUI / WindowInsets
        // ou um painel customizado com GestureDetector
    }
}
