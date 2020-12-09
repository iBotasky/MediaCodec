package com.example.media.openGl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.media.openGl.Shapes.Triangle
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MGLRenderer : GLSurfaceView.Renderer {
    companion object {
        const val TAG = "GLSurfaceRenderer"
    }

    private val triangle = Triangle()

    // 每次重新绘制视图时调用。
    override fun onDrawFrame(gl: GL10?) {
        Log.e(TAG, "onDrawFrame")
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        triangle.draw()
    }

    // 当视图的几何图形发生变化（例如当设备的屏幕方向发生变化）时调用
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.e(TAG, "onSurfaceChange width :$width  height:$height")
        GLES20.glViewport(0, 0, width, height)
    }

    //  调用一次以设置视图的 OpenGL ES 环境。
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.e(TAG, "onSurfaceCreate")
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
    }

}