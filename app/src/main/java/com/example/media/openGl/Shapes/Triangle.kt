package com.example.media.Shapes

import android.opengl.GLES20
import android.util.Log
import android.view.View
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Triangle(mView: View) : Shape(mView) {
    companion object {
        const val TAG = "TriangleDraw"
        const val COORDE_PER_VERTEX = 3
        val TRIANGLE_COORDS = floatArrayOf(
            0.5f, 0.5f, 0.0f,  // top
            -0.5f, -0.5f, 0.0f,  // bottom left
            0.5f, -0.5f, 0.0f // bottom right
        )
    }

    private val vertexShaderCode = """
        attribute vec4 vPosition;
            void main() {
                gl_Position = vPosition;
            }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
            uniform vec4 vColor;
                void main() {
                    gl_FragColor = vColor;
                }
    """.trimIndent()


    private var mProgram = -1
    private var mPositionHandle = -1
    private var mColorHandle = -1

    private val mViewMatrix = FloatArray(16)
    private lateinit var vertexBuffer: FloatBuffer


    //顶点个数
    private val vertexCount: Int = TRIANGLE_COORDS.size / COORDE_PER_VERTEX

    // 顶点之间的偏移量
    private val vertexStride = COORDE_PER_VERTEX * 4 // 每个定点4个字节

    // RGBA
    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    init {
        Log.e(TAG, "===============onInit")
        val byteBuffer = ByteBuffer.allocateDirect(TRIANGLE_COORDS.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        vertexBuffer = byteBuffer.asFloatBuffer()
        vertexBuffer.put(TRIANGLE_COORDS)
        vertexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()

        // 创建一个空的opengles程序
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)

        GLES20.glLinkProgram(mProgram)
    }


    override fun onDrawFrame(gl: GL10?) {
        Log.e(TAG, "===============onDrawFrame")
        GLES20.glUseProgram(mProgram)
        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")

        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(
            mPositionHandle, COORDE_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        //绘制三角形

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄

        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

}