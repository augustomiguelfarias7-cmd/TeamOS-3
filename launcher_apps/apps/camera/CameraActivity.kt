package com.teamos.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamos.launcher.R

/**
 * App Câmera nativo - TeamOS 3.0
 * Visual: One UI 8 + Android 16
 */
class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Preview da câmera + captura de foto/vídeo
        // Usa CameraX (recomendado no Android moderno)
    }
}
