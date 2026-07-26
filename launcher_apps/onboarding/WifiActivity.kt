package com.teamos.launcher.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R
import com.teamos.launcher.LauncherActivity
import com.google.android.material.button.MaterialButton

/**
 * Configuração de Wi-Fi na primeira inicialização
 * TeamOS 3.0 - estilo One UI 8 + Android 16
 */
class WifiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        findViewById<MaterialButton>(R.id.btn_skip).setOnClickListener {
            finishSetup()
        }

        findViewById<MaterialButton>(R.id.btn_continue).setOnClickListener {
            // Aqui entraria a lógica real de conexão Wi-Fi
            finishSetup()
        }
    }

    private fun finishSetup() {
        getSharedPreferences("teamos_setup", MODE_PRIVATE)
            .edit()
            .putBoolean("setup_complete", true)
            .apply()

        startActivity(Intent(this, LauncherActivity::class.java))
        finish()
    }
}
