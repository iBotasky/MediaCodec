package com.example.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "VideoTrack"
    }

    // 解码器
    private var mVideoDecoder: MediaCodec? = null

    // 编码器
    private var mVideoEncoder: MediaCodec? = null

    // Muxer
    private var mMediaMuxer: MediaMuxer? = null

    // 提取器
    private var mExtractor: MediaExtractor? = null

    private var mVideoTrack: Int = -1
    private var mVideoFormat: MediaFormat? = null

    private var isRunning = false

    val ORIGINAL_VIDEO = "${Environment.getExternalStorageDirectory().absolutePath}/1.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnVideoTrack.setOnClickListener {
            Thread(Runnable {
                initMediaCodeces()
                decode()
            }).start()
        }
    }

    private fun initMediaCodeces() {
        Log.e(TAG, "===============初始化解编码器===============")
        mExtractor = MediaExtractor()
        mExtractor?.setDataSource(ORIGINAL_VIDEO)
        // 遍历提取器的轨道，获取格式
        mExtractor?.apply {
            for (i in 0 until this.trackCount) {
                val mediaFormat = this.getTrackFormat(i)
                val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
                if (mime != null && mime.startsWith("video/")) {
                    mVideoTrack = i
                    mVideoFormat = mediaFormat
                    break
                }
                if (mVideoTrack >= 0) {
                    this.selectTrack(mVideoTrack)
                }
            }
            // 创建解码器
            val mime = this.getTrackFormat(mVideoTrack).getString(MediaFormat.KEY_MIME)
            mVideoDecoder = MediaCodec.createDecoderByType(mime!!)
            mVideoDecoder!!.configure(mVideoFormat, Surface(mTextureView.surfaceTexture), null, 0);
            mVideoDecoder!!.start()
        }
    }

    private fun decode() {
        var curSampleFlags = -1
        var curSampleTime = -1L
        mVideoDecoder?.apply {
            isRunning = true
            try {
                var info = MediaCodec.BufferInfo()
                while (isRunning) {
                    Log.e(TAG, "===============Running===============")
                    /**
                     * 延迟 TIME_US 等待拿到空的 input buffer下标，单位为 us
                     * -1 表示一直等待，知道拿到数据，0 表示立即返回
                     */
                    val inputBufferId = this.dequeueInputBuffer(1000)
                    Log.e(TAG, "getInputId:$inputBufferId")
                    if (inputBufferId > 0) {
                        // 拿到可用的Buffer缓存区
                        val inputBuffer = this.getInputBuffer(inputBufferId)
                        inputBuffer?.let { b ->
                            var size = 0
                            //先清空数据
                            b.clear()
                            //选择要解析的轨道
                            mExtractor!!.selectTrack(mVideoTrack);
                            //读取当前帧的数据
                            val buffercount: Int = mExtractor!!.readSampleData(b, 0)
                            if (buffercount < 0) {
                                size = -1
                            } else {
                                //记录当前时间戳
                                curSampleTime = mExtractor!!.sampleTime
                                //记录当前帧的标志位
                                curSampleFlags = mExtractor!!.sampleFlags
                                //进入下一帧
                                mExtractor!!.advance()
                                size = buffercount
                            }
                            if (size >= 0) {
                                this.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    size,
                                    0, 0
//                                    mExtractor!!.sampleTime,
//                                    mExtractor!!.sampleFlags
                                )
                                Log.e(TAG, "getSize:$size")
                            } else {
                                this.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    0,
                                    0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                isRunning = false
                                Log.e(TAG, "===============End of stream===============")
                            }
                        }
                    }
                    //等到拿到输出的buffer下标
                    val outputId: Int = this.dequeueOutputBuffer(info, 1000)
//                    Log.e(TAG, "getOutputId:$outputId")
//                    if (outputId >= 0) {
//                        //释放buffer，并渲染到 Surface 中
//                        this.releaseOutputBuffer(outputId, true)
//                    }

                    when (outputId) {
                        MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED ->
                            Log.e(TAG, "INFO_OUTPUT_BUFFERS_CHANGED")
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED ->
                            Log.d(TAG, "New format " + this.outputFormat);
                        MediaCodec.INFO_TRY_AGAIN_LATER ->
                            Log.d(TAG, "dequeueOutputBuffer timed out!");
                        else -> {
                            if (outputId >= 0) {
                                //释放buffer，并渲染到 Surface 中
                                this.releaseOutputBuffer(outputId, true)
                            }
                        }
                    }

                    // 在所有解码后的帧都被渲染后，就可以停止播放了
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        Log.e(
                            "VideoDecoder",
                            "zsr OutputBuffer BUFFER_FLAG_END_OF_STREAM"
                        )
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isRunning = false
                mVideoDecoder?.apply {
                    this.stop()
                    this.release()
                }
                mExtractor?.release()

            }
        }
    }

}