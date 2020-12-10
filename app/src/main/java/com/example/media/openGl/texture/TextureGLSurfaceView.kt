package com.example.media.openGl.texture

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureGLSurfaceView(context: Context): GLSurfaceView(context) {
    init {
        // 穿件一个OpenGl2.0的上下文
        setEGLContextClientVersion(2)
        setRenderer(object : GLSurfaceView.Renderer{
            override fun onDrawFrame(gl: GL10?) {
                TODO("Not yet implemented")
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                TODO("Not yet implemented")
            }

            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                TODO("Not yet implemented")
            }

        })

        // 设置只有在数据发生变化才会绘制视图
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}