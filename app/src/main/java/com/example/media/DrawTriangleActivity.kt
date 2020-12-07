package com.example.media

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.media.Shapes.Triangle
import kotlinx.android.synthetic.main.activity_draw_triangle.*

class DrawTriangleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_triangle)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(Triangle(glSurfaceView))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}