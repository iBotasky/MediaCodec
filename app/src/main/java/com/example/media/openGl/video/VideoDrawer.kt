package com.example.media.openGl.video

import com.example.media.openGl.render.IDrawer
import com.example.media.openGl.utils.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VideoDrawer : IDrawer {
    companion object {
        const val VERTEX_SHADER =
            """
            
        """
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

    override fun onCreate() {
        initPos()
    }

    override fun onChange(width: Int, height: Int) {

    }

    override fun onDrawFrame() {

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
}