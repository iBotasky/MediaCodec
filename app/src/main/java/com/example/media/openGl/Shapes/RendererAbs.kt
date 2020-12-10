package com.example.media.openGl.Shapes

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class RendererAbs : GLSurfaceView.Renderer {
    protected var program = -1
    protected var vPosition = -1
    protected var uColor = -1
    protected var vMatrix = -1

    protected var vertices: FloatArray? = null
    protected var verticesShader: String? = null
    protected var fragmentShader: String? = null

    protected var drawMode = GLES20.GL_TRIANGLE_STRIP
    protected var drawFirst: Int = 0
    protected var drawCount: Int = 0

    // 投影矩阵
    protected var mProjectionMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

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
        if (program != 0) {
            // 如果创建成功加入顶点着色器跟片元着色器
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, pixelShader)
            // 链接程序
            GLES20.glLinkProgram(program)
            // 存放链接成功的Program数量
            val linkState = IntArray(1);
            // 若链接失败则报错并删除程序
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkState, 0)
            if (linkState[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ")
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }


    protected fun getVerticesBuffer(): FloatBuffer? {
        vertices?.apply {
            // 创建顶点坐标数据缓冲
            // vertices.length*4是因为一个float占四个字节
            val vbb = ByteBuffer.allocateDirect(this.size * 4)
            vbb.order(ByteOrder.nativeOrder()) //设置字节顺序

            val vertexBuf = vbb.asFloatBuffer() //转换为Float型缓冲

            vertexBuf.put(this) //向缓冲区中放入顶点坐标数据

            vertexBuf.position(0) //设置缓冲区起始位置

            return vertexBuf
        }
        return null
    }


    /**
     * 当GLSurfaceView中的Surface被创建的时候(界面显示)回调此方法，一般在这里做一些初始化
     * @param gl10 1.0版本的OpenGL对象，这里用于兼容老版本，用处不大
     * @param eglConfig egl的配置信息(GLSurfaceView会自动创建egl，这里可以先忽略)
     */
    /**
     * program的初始化要放在onSurfaceCreated后
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        if (verticesShader == null || fragmentShader == null) {
            Log.e("RenderAbs", "verticesShader or fragmentShader is null")
        }
        // 初始化着色器
        // 基于顶点着色器与片元着色器创建程序,
        program = createProgram(verticesShader!!, fragmentShader!!)
        // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        uColor = GLES20.glGetUniformLocation(program, "uColor")
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(.5f, .5f, .5f, 1.0f)
    }

    /**
     * 当GLSurfaceView中的Surface被改变的时候回调此方法(一般是大小变化)
     * @param gl10 同onSurfaceCreated()
     * @param width Surface的宽度
     * @param height Surface的高度
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // 设置绘图的窗口(可以理解成在画布上划出一块区域来画图)
        GLES20.glViewport(0, 0, width, height)

        // 计算投影矩阵
        val aspectRatio = if (width > height) {
            width / height.toFloat()
        } else {
            height / width.toFloat()
        }

        if (width > height) {
            Matrix.orthoM(mProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }

    }

    /**
     * 当Surface需要绘制的时候回调此方法
     * 根据GLSurfaceView.setRenderMode()设置的渲染模式不同回调的策略也不同：
     * GLSurfaceView.RENDERMODE_CONTINUOUSLY : 固定一秒回调60次(60fps)
     * GLSurfaceView.RENDERMODE_WHEN_DIRTY   : 当调用GLSurfaceView.requestRender()之后回调一次
     * @param gl10 同onSurfaceCreated()
     */
    override fun onDrawFrame(gl: GL10?) {
        // 获取图形的顶点坐标
        val vertices = getVerticesBuffer()
        if (vertices == null) {
            Log.e("GLSurfaceView", " getVerticesBuffer is null")
        }
        // 清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        // 使用某套shader程序
        GLES20.glUseProgram(program)

        // 为画笔指定顶点位置数据(vPosition)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices)

        // 为矩阵设置数据(vMatrix)
        if (vMatrix != -1) {
            GLES20.glUniformMatrix4fv(vMatrix, 1, false, mProjectionMatrix, 0)
        }

        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(vPosition)

        // 设置属性uColor(颜色 索引,R,G,B,A)
        GLES20.glUniform4f(uColor, 0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
        // 绘制
        GLES20.glDrawArrays(drawMode, drawFirst, drawCount)
    }

}