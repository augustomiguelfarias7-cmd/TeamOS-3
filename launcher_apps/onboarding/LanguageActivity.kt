package com.teamos.launcher.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamos.launcher.R
import com.google.android.material.button.MaterialButton

/**
 * Seleção de idioma - TeamOS 3.0
 * Idiomas: en-US, pt-BR, pt-PT, ja, en-GB, fr, es, hi, ko, en-AU, ar
 */
class LanguageActivity : AppCompatActivity() {

    private val languages = listOf(
        LanguageItem("en-US", "English (United States)", "English"),
        LanguageItem("pt-BR", "Português (Brasil)", "Português"),
        LanguageItem("pt-PT", "Português (Portugal)", "Português"),
        LanguageItem("ja", "日本語", "Japanese"),
        LanguageItem("en-GB", "English (United Kingdom)", "English"),
        LanguageItem("fr", "Français", "French"),
        LanguageItem("es", "Español", "Spanish"),
        LanguageItem("hi", "हिन्दी", "Hindi"),
        LanguageItem("ko", "한국어", "Korean"),
        LanguageItem("en-AU", "English (Australia)", "English"),
        LanguageItem("ar", "العربية", "Arabic")
    )

    private var selectedCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val recycler = findViewById<RecyclerView>(R.id.language_list)
        val btnContinue = findViewById<MaterialButton>(R.id.btn_continue)
        btnContinue.isEnabled = false

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = LanguageAdapter(languages) { item ->
            selectedCode = item.code
            btnContinue.isEnabled = true
            // Salvar preferência de idioma
            getSharedPreferences("teamos_setup", MODE_PRIVATE)
                .edit()
                .putString("language", item.code)
                .apply()
        }

        btnContinue.setOnClickListener {
            startActivity(Intent(this, WifiActivity::class.java))
            finish()
        }
    }
}

data class LanguageItem(val code: String, val nativeName: String, val englishName: String)
