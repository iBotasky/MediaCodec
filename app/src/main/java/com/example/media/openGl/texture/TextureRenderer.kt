package com.example.media.openGl.texture

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.media.R
import com.example.media.openGl.utils.Constants
import com.example.media.openGl.utils.ShaderHelper
import com.example.media.openGl.utils.TextureHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TextureRenderer(val mContext: Context) : GLSurfaceView.Renderer {
    companion object {
        const val U_VIEW_MATRIX = "u_ViewMatrix"
        const val U_MODEL_MATRIX = "u_ModelMatrix"
        const val U_PROJECTION_MATRIX = "u_ProjectionMatrix"
        const val A_POSITION = "aPosition"
        const val A_TEXTURE_COORDINATE = "aCoordinate"
        const val U_TEXTURE_UNIT = "uTexture"
    }

    private var uModeMatrixAttr: Int = 0
    private var uViewMatrixAttr: Int = 0
    private var uProjectionMatrixAttr: Int = 0
    private var aPositionAttr: Int = 0
    private var aTextureCoordinateAttr: Int = 0
    private var uTextureUnitAttr: Int = 0
    private var mTextureId: Int = 0

    protected var modelMatrix = FloatArray(16)
    protected var viewMatrix = FloatArray(16)
    protected var projectionMatrix = FloatArray(16)
    protected var mvpMatrix = FloatArray(16)

    private val vertexArrayData = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )

    private val textureArrayData = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

    private val verticesShader =
        """
            attribute vec4 aPosition; 
            attribute vec2 aCoordinate;
            varying vec2 vCoordinate;
            void main() {
                vCoordinate = aCoordinate ;
                gl_Position = aPosition;
            }
        """.trimIndent()
    // 图片纹理Shader
    private val fragmentShader =
        """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2 vCoordinate;
            void main(){
                gl_FragColor = texture2D(uTexture,vCoordinate);
            }
        """.trimIndent()

    private val videoFragmentShader =
        //#extension GL_OES_EGL_image_external : require 拓展纹理，YUV->RGB
        """
            #extension GL_OES_EGL_image_external : require
            uniform sampler2D uTexture;
            varying vec2 vCoordinate;
            void main(){
                gl_FragColor = texture2D(uTexture, vCoordinate);
            }
        """.trimIndent()



    private val vertexFloatBuffer = ByteBuffer
        .allocateDirect(vertexArrayData.size * Constants.BYTES_PRE_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexArrayData)
        .position(0)

    private val textureFloatBuffer = ByteBuffer
        .allocateDirect(textureArrayData.size * Constants.BYTES_PRE_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(textureArrayData)
        .position(0)

    private var mProgram: Int = -1

    override fun onDrawFrame(gl: GL10?) {
        Log.e("TextureRender", " onDrawFrame")
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)



        GLES20.glVertexAttribPointer(aPositionAttr, 2, GLES20.GL_FLOAT, false, 0, vertexFloatBuffer)
        GLES20.glEnableVertexAttribArray(aPositionAttr)
        GLES20.glVertexAttribPointer(
            aTextureCoordinateAttr,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            textureFloatBuffer
        )
        GLES20.glEnableVertexAttribArray(aTextureCoordinateAttr)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.e("TextureRender", " onSurfaceChange")
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = ShaderHelper.buildProgram(verticesShader, fragmentShader)
        Log.e("TextureRender", " onSurfaceCreated:$mProgram")
        glClearColor(.5f, .5f, .5f, 1f)
        // 获取各个参数句柄
        aPositionAttr = GLES20.glGetAttribLocation(mProgram, A_POSITION)

        aTextureCoordinateAttr = GLES20.glGetAttribLocation(mProgram, A_TEXTURE_COORDINATE)
        uTextureUnitAttr = GLES20.glGetUniformLocation(mProgram, U_TEXTURE_UNIT)

        mTextureId = TextureHelper.loadTexture(mContext, R.drawable.yellow)

        GLES20.glUniform1i(uTextureUnitAttr, 0)
//        val intBuffer = IntBuffer.allocate(1)
//        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuffer)

        GLES20.glUseProgram(mProgram)
    }

}