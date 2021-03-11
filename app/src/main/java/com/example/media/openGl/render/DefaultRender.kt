package com.example.media.openGl.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.media.openGl.utils.OpenGLTools
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


// 默认的渲染器
class DefaultRender : GLSurfaceView.Renderer {
    private val mDrawers = mutableListOf<IDrawer>()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        //开启混合，即半透明
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // 根据Drawers生成对应的纹理数量获取纹理ID
        val textureIds = OpenGLTools.createTextureIds(mDrawers.size)

//        mDrawers.forEachIndexed { index, iDrawer ->
//            iDrawer.onCreate(textureIds[index])
//        }

        for ((index, drawer) in mDrawers.withIndex()) {
            drawer.onCreate(textureIds[index])
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        mDrawers.forEach {
            it.onChange(width, height)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.e("DefaultRenderer", " onDrawFrame")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mDrawers.forEach {
            it.onDrawFrame()
        }
    }


    fun addDrawer(drawer: IDrawer) {
        mDrawers.add(drawer)
    }
}