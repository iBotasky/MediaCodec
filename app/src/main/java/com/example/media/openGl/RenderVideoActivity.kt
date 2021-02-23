package com.example.media.openGl

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.media.R
import com.example.media.databinding.ActivityRenderVideoBinding
import com.example.media.openGl.render.DefaultRender
import com.example.media.openGl.video.VideoDrawer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RenderVideoActivity : AppCompatActivity() {
    companion object {
        const val TAG = "RenderVideoActivity"
    }

    private lateinit var binding: ActivityRenderVideoBinding
    private var mVideoTrack: Int = -1
    private var mVideoFormat: MediaFormat? = null

    private var isRunning = false   // 用来做整个流程的运行判断，当最后一帧渲染结束即为false
    private var isEOS =
        false       // 用来判定QueueInput是否END_OF_STREAM, 已经END_OF_STREAM则循环就不需要在去queueInput, 直接去获取解码后的Output渲染

    // App运行后直接在手机上给权限即可，不添加权限申请了
    val ORIGINAL_VIDEO = "${Environment.getExternalStorageDirectory().absolutePath}/1.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenderVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val drawer = VideoDrawer()
        binding.glSurfaceView.setEGLContextClientVersion(2)
        val renderer = DefaultRender()
        renderer.addDrawer(drawer)
        binding.glSurfaceView.setRenderer(renderer)
        binding.glSurfaceView.renderMode = RENDERMODE_WHEN_DIRTY

        binding.startRender.setOnClickListener {
            drawer.mSurfaceTexture?.let {
                Log.e(TAG, "onStartRender")
                lifecycleScope.launch {
                    binding.startRender.isEnabled = false
                    withContext(Dispatchers.Default) {
                        startDecodeRender(Surface(it))
                    }
                    binding.startRender.isEnabled = true
                }
            }
        }
    }


    private fun startDecodeRender(surface: Surface) {
        Log.e(TAG, "===============初始化解编码器===============")
        val mExtractor = MediaExtractor()
        mExtractor.setDataSource(ORIGINAL_VIDEO)
        // 遍历提取器的轨道，获取格式

        for (i in 0 until mExtractor.trackCount) {
            val mediaFormat = mExtractor.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime != null && mime.startsWith("video/")) {
                mVideoTrack = i
                mVideoFormat = mediaFormat
                break
            }
        }
        if (mVideoTrack >= 0) {
            mExtractor.selectTrack(mVideoTrack)
        }
        // 创建解码器
        val mime = mExtractor.getTrackFormat(mVideoTrack).getString(MediaFormat.KEY_MIME)
        val mVideoDecoder = MediaCodec.createDecoderByType(mime!!)
//        mVideoDecoder.configure(mVideoFormat, Surface(textureView.surfaceTexture), null, 0);
        mVideoDecoder.configure(mVideoFormat, surface, null, 0)
        mVideoDecoder.start()

        // 开始编码并渲染
        var curSampleFlags = -1
        var curSampleTime = -1L
        var totalSize = 0L
        mVideoDecoder.apply {
            isRunning = true
            isEOS = false
            //选择要解析的轨道
            mExtractor.selectTrack(mVideoTrack);
            try {
                // 用来存放每次decode帧的bufferInfo
                val info = MediaCodec.BufferInfo()
                while (isRunning) {
                    Log.e(TAG, "===============Running===============")
                    /**
                     * 延迟 TIME_US 等待拿到空的 input buffer下标，单位为 us
                     * -1 表示一直等待，知道拿到数据，0 表示立即返回
                     */
                    if (!isEOS) {
                        val inputBufferId = this.dequeueInputBuffer(1000)
                        Log.e(TAG, "getInputId:$inputBufferId")
                        if (inputBufferId > 0) {
                            // 拿到可用的Buffer缓存区
                            val inputBuffer = this.getInputBuffer(inputBufferId)
                            inputBuffer?.let { b ->
                                var size = 0
                                //先清空数据
                                b.clear()

                                //读取当前帧的数据
                                val buffercount: Int = mExtractor.readSampleData(b, 0)
                                if (buffercount < 0) {
                                    size = -1
                                } else {
                                    //记录当前时间戳
                                    curSampleTime = mExtractor.sampleTime
                                    //记录当前帧的标志位
                                    curSampleFlags = mExtractor.sampleFlags
                                    //进入下一帧
                                    mExtractor.advance()
                                    size = buffercount
                                }
                                if (size >= 0) {
                                    this.queueInputBuffer(
                                        inputBufferId,
                                        0,
                                        size,
//                                        0, 0
                                        mExtractor.sampleTime,
                                        mExtractor.sampleFlags
                                    )
                                    Log.e(TAG, "queueInput:$size")
                                } else {
                                    this.queueInputBuffer(
                                        inputBufferId,
                                        0,
                                        0,
                                        0,
                                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    )
                                    isEOS = true
                                    Log.e(
                                        TAG,
                                        "===============End of stream==============="
                                    )
                                }
                            }
                        }
                    }
                    //等到拿到输出的buffer下标,并且把当前帧信息放入info
                    val outputId: Int = this.dequeueOutputBuffer(info, 1000)
                    totalSize += info.size
                    Log.e(TAG, "outputId:$outputId size:${info.size}")
                    when (outputId) {
                        MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED ->
                            Log.e(TAG, "STATE INFO_OUTPUT_BUFFERS_CHANGED")
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED ->
                            Log.d(TAG, "STATE New format " + this.outputFormat);
                        MediaCodec.INFO_TRY_AGAIN_LATER ->
                            Log.d(TAG, "STATE dequeueOutputBuffer timed out!");
                        else -> {
                            if (outputId >= 0) {
                                //释放buffer，并渲染到 Surface 中
                                this.releaseOutputBuffer(outputId, true)
                            }
                        }
                    }

                    // 在所有解码后的帧都被渲染后，就可以停止播放了
                    Log.e(
                        TAG,
                        "flags:${info.flags}  result:${info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM}"
                    )
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM !== 0) {
                        isRunning = false
                        Log.e(
                            TAG,
                            "OutputBuffer BUFFER_FLAG_END_OF_STREAM TotalSize:$totalSize"
                        )
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isRunning = false
                try {
                    mVideoDecoder.stop()
                    mVideoDecoder.release()
                    mExtractor.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}