package com.example.media.openGl.texture

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
        const val A_POSITION = "a_Position"
        const val A_TEXTURE_COORDINATE = "a_TextureCoordinates"
        const val U_TEXTURE_UNIT = "u_TextureUnit"
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
        0.5f, 0f,
        0f, 1.0f,
        1.0f, 1.0f
    )

    private val textureArrayData = floatArrayOf(
        0.5f, 0f,
        0f, 1.0f,
        1.0f, 1.0f
    )

    private val verticesShader =
        """
            attribute vec4 a_Position; 
            attribute vec2 a_TextureCoordinates;
            varying vec2 v_TextureCoordinates; 
            uniform mat4 u_ModelMatrix;
            uniform mat4 u_ViewMatrix;
            uniform mat4 u_ProjectionMatrix;
            uniform mat4 u_Matrix;
            void main() {
                v_TextureCoordinates = a_TextureCoordinates ;
                gl_Position = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix * a_Position;
            }
        """.trimIndent()

    private val fragmentShader =
        """
            precision mediump float;
            uniform sampler2D u_TextureUnit;
            varying vec2 v_TextureCoordinates;
            void main(){

                gl_FragColor = texture2D(u_TextureUnit,v_TextureCoordinates);
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
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUniformMatrix4fv(uModeMatrixAttr, 1, false, modelMatrix, 0)
        GLES20.glUniformMatrix4fv(uViewMatrixAttr, 1, false, viewMatrix, 0)
        GLES20.glUniformMatrix4fv(uProjectionMatrixAttr, 1, false, projectionMatrix, 0)


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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio =
            if (width > height) width.toFloat() / height.toFloat() else height.toFloat() / width.toFloat()
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = ShaderHelper.buildProgram(verticesShader, fragmentShader)
        glClearColor(.5f, .5f, .5f, 1f)
        // 获取各个参数句柄
        aPositionAttr = GLES20.glGetAttribLocation(mProgram, A_POSITION)
        uModeMatrixAttr = GLES20.glGetUniformLocation(mProgram, U_MODEL_MATRIX)
        uViewMatrixAttr = GLES20.glGetUniformLocation(mProgram, U_VIEW_MATRIX)
        uProjectionMatrixAttr = GLES20.glGetUniformLocation(mProgram, U_PROJECTION_MATRIX)

        aTextureCoordinateAttr = GLES20.glGetAttribLocation(mProgram, A_TEXTURE_COORDINATE)
        uTextureUnitAttr = GLES20.glGetUniformLocation(mProgram, U_TEXTURE_UNIT)

        mTextureId = TextureHelper.loadTexture(mContext, R.mipmap.ic_launcher)

        GLES20.glUniform1i(uTextureUnitAttr, 0)
        val intBuffer = IntBuffer.allocate(1)
        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuffer)
        Matrix.setIdentityM(modelMatrix, 0)

//        Matrix.translateM(modelMatrix, 0, 0f, 0.5f, 0f)

        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
    }

}