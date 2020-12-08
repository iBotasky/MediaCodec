package com.example.media.openGl.Shapes

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.View

abstract class Shape(mView: View) : GLSurfaceView.Renderer {


    public fun loadShader(type: Int, shaderCode: String):Int {
        // 根据type穿件顶点着色器跟片元着色器
        val shader = GLES20.glCreateShader(type)
        // 将资源加入到着色器中，并bianyi
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}