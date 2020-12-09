package com.example.media.openGl.Shapes

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.FloatBuffer

abstract class RendererAbs : GLSurfaceView.Renderer {
    protected var program = -1
    protected var vPosition = -1
    protected var uColor = -1


    /**
     * 加载定制的Shader方法
     * [shaderType] shader类型 GLES20.GL_VERTEX_SHADER GLES20.GL_FRAGMENT_SHADER
     * [sourceCode] shader的脚本
     */
    protected fun loadShader(shaderType: Int, sourceCode: String): Int {
        // 创建一个脚本
        var shader = GLES20.glCreateShader(shaderType)
        if (shader != 0) {
            // 加载Shader源代码
            GLES20.glShaderSource(shader, sourceCode)
            // 编译Shader
            GLES20.glCompileShader(shader)
            // 存放变异成功的Shader的数量数组
            val compiled = IntArray(1)
            // 获取Shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    /**
     * 创建Shader程序
     */
    protected fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }
        // 创建一个GL程序
        var program = GLES20.glCreateProgram()
        if (program != 0){
            // 如果创建成功加入顶点着色器跟片元着色器
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, pixelShader)
            // 链接程序
            GLES20.glLinkProgram(program)
            // 存放链接成功的Program数量
            val linkState = IntArray(1);
            // 若链接失败则报错并删除程序
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkState, 0)
            if (linkState[0] != GLES20.GL_TRUE){
                Log.e("ES20_ERROR", "Could not link program: ")
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }


    abstract fun getVertices(): FloatBuffer



}