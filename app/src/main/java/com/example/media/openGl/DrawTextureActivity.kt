package com.example.media.openGl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.media.openGl.texture.TextureGLSurfaceView

class DrawTextureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textureGLSurfaceView = TextureGLSurfaceView(this)
        setContentView(textureGLSurfaceView)
    }
}