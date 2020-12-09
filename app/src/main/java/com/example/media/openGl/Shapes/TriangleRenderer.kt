package com.example.media.openGl.Shapes

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRenderer : RendererAbs() {
    // 顶点着色器的脚本
    private val verticesShader =
        """
            attribute vec2 vPosition;            
            void main(){                         
                gl_Position = vec4(vPosition,0,1);
            }
        """

    // 片元着色器的脚本
    private val fragmentShader =
        """
            precision mediump float;         
            uniform vec4 uColor;             
            void main(){                     
                gl_FragColor = uColor;        
            }
        """

    override fun getVertices(): FloatBuffer {
        val vertices = floatArrayOf(
            0.0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        )

        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个float占四个字节

        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder()) //设置字节顺序

        val vertexBuf = vbb.asFloatBuffer() //转换为Float型缓冲

        vertexBuf.put(vertices) //向缓冲区中放入顶点坐标数据

        vertexBuf.position(0) //设置缓冲区起始位置

        return vertexBuf
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
        // 初始化着色器
        // 基于顶点着色器与片元着色器创建程序,
        program = createProgram(verticesShader, fragmentShader)
        // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        uColor = GLES20.glGetUniformLocation(program, "uColor")

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
        GLES20.glClearColor(1.0f, 1f, 0f, 1.0f)
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
        val vertices = getVertices()
        // 清屏
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        // 使用某套shader程序
        GLES20.glUseProgram(program)

        // 为画笔指定顶点位置数据(vPosition)
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertices)

        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(vPosition)

        // 设置属性uColor(颜色 索引,R,G,B,A)
        GLES20.glUniform4f(uColor, 0.0f, 1.0f, 0.0f, 1.0f)
        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3)
    }


}