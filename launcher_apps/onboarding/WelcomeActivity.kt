package com.teamos.launcher.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R
import com.google.android.material.button.MaterialButton

/**
 * Tela de boas-vindas do TeamOS 3.0
 * Visual: Android 16 Material 3 Expressive + One UI 8
 */
class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<MaterialButton>(R.id.btn_continue).setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
            finish()
        }
    }
}
