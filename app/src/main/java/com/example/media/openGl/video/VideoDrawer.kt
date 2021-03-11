package com.example.media.openGl.video

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.util.Log
import com.example.media.openGl.render.IDrawer
import com.example.media.openGl.utils.Constants
import com.example.media.openGl.utils.ShaderHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VideoDrawer : IDrawer {
    companion object {
        const val TAG = "VideoDrawer"
        private const val A_POSITION = "aPosition";
        private const val A_COORDINATE = "aCoordinate";
        private const val V_COORDINATE = "vCoordinate"
        private const val A_ALPHA = "alpha";
        private const val IN_ALPHA = "inAlpha"
        private const val U_TEXTURE = "uTexture"

        //        const val VERTEX_SHADER =
//            """
//            attribute vec4 ${A_POSITION};
//            precision mediump float;
//            attribute vec2 ${A_COORDINATE};
//            varying vec2 ${V_COORDINATE};
//            attribute float ${A_ALPHA};
//            varying float ${IN_ALPHA};
//            void main(){
//                gl_Position =  ${A_POSITION};
//                $V_COORDINATE = ${A_COORDINATE};
//                $IN_ALPHA = $A_ALPHA;
//            }
//
//        """
        const val VERTEX_SHADER = "attribute vec4 aPosition;" +
                "precision mediump float;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "attribute float alpha;" +
                "varying float inAlpha;" +
                "void main() {" +
                "    gl_Position = aPosition;" +
                "    vCoordinate = aCoordinate;" +
                "    inAlpha = alpha;" +
                "}"

//        const val FRAGMENT_SHADER = """
//            #extension GL_OES_EGL_image_external : require
//            precision mediump float;
//            varying vec2 $V_COORDINATE;
//            varying float $IN_ALPHA;
//            uniform samplerExternalOES $U_TEXTURE;
//            void main() {
//              vec4 color = texture2D($U_TEXTURE, $V_COORDINATE);
//              gl_FragColor = vec4(color.r, color.g, color.b, $IN_ALPHA);
//            }
//        """

        const val FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" + //一定要加换行"\n"，否则会和下一行的precision混在一起，导致编译出错
                    "precision mediump float;" +
                    "varying vec2 vCoordinate;" +
                    "varying float inAlpha;" +
                    "uniform samplerExternalOES uTexture;" +
                    "void main() {" +
                    "  vec4 color = texture2D(uTexture, vCoordinate);" +
                    "  gl_FragColor = vec4(color.r, color.g, color.b, inAlpha);" +
                    "}"
    }

    private val mVertexCoors = floatArrayOf(
        -1f, -1f,
        1f, -1f,
        -1f, 1f,
        1f, 1f
    )

    private val mTextureCoors = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

    private var mWorldWidth = -1
    private var mWorldHeight = -1
    private var mVideoWidth = -1
    private var mVideoHeight = -1


    private var mTextureId = -1
    var mSurfaceTexture: SurfaceTexture? = null

    // OpenGL程序ID
    private var mProgram = -1

    // 变换矩阵句柄
    private var mMatrixHandle = -1

    // 定点坐标句柄
    private var mVertexPosHandle = -1

    // 纹理坐标句柄
    private var mTexturePosHandle = -1

    // 纹理句柄
    private var mTextureHandle = -1

    // 透明句柄
    private var mAlphaHandle = -1

    private lateinit var mVertexBuffer: FloatBuffer
    private lateinit var mTextureBuffer: FloatBuffer

    private var mMatrix: FloatArray? = null
    private var mAlpha = 1f

    override fun onCreate(textureId: Int) {
        Log.e(TAG, "onCreate")
        mTextureId = textureId
        mSurfaceTexture = SurfaceTexture(textureId)

        initPos()
        createProgram()
    }

    override fun onChange(width: Int, height: Int) {
        Log.e(TAG, "onChange")
    }

    override fun onDrawFrame() {
        Log.e(TAG, "onDrawFrame")
        updateTexture()
        // 启用顶点句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandle)
        GLES20.glEnableVertexAttribArray(mTexturePosHandle)

        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPosHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glVertexAttribPointer(
            mTexturePosHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureBuffer
        )
        GLES20.glVertexAttrib1f(mAlphaHandle, mAlpha)

        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }


    private fun updateTexture() {
        mSurfaceTexture?.updateTexImage()
    }

    private fun initPos() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexCoors.size * Constants.BYTES_PRE_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mVertexCoors)
            .position(0) as FloatBuffer

        mTextureBuffer = ByteBuffer.allocateDirect(mTextureCoors.size * Constants.BYTES_PRE_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(mTextureCoors)
            .position(0) as FloatBuffer
    }

    private fun createProgram() {
        // 创建Program
        mProgram = ShaderHelper.buildProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        if (mProgram == -1) {
            Log.e(TAG, "Create Program Failed")
            return
        }
        // 获取句柄
        mVertexPosHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        mTexturePosHandle = GLES20.glGetAttribLocation(mProgram, "aCoordinate")
        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture")
        mAlphaHandle = GLES20.glGetAttribLocation(mProgram, "alpha")

        GLES20.glUseProgram(mProgram)
        //激活指定纹理单元
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(mTextureHandle, 0)
        //配置边缘过渡参数
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandle)
        GLES20.glDisableVertexAttribArray(mTexturePosHandle)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureId), 0)
        GLES20.glDeleteProgram(mProgram)
        mSurfaceTexture?.release()
    }
}