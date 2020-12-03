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
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
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
            val t = Thread()
            t.run {
                initMediaCodeces()
                decode()
            }
            t.start()
        }
    }

    private fun initMediaCodeces() {
        Log.e("VideoTrack", "===============初始化解编码器===============")
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
        mExtractor!!.advance()
        mVideoDecoder?.apply {
            isRunning = true
            try {
                var info = MediaCodec.BufferInfo()
                while (isRunning) {
                    Log.e("VideoTrack", "===============Running===============")
                    /**
                     * 延迟 TIME_US 等待拿到空的 input buffer下标，单位为 us
                     * -1 表示一直等待，知道拿到数据，0 表示立即返回
                     */
                    val inputBufferId = this.dequeueInputBuffer(1000)
                    if (inputBufferId > 0) {
                        // 拿到可用的Buffer缓存区
                        val inputBuffer = this.getInputBuffer(inputBufferId)
                        inputBuffer?.let { b ->
                            val size = mExtractor!!.readSampleData(b, 0)
                            mExtractor!!.advance()
                            if (size >= 0) {
                                this.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    size,
                                    mExtractor!!.sampleTime,
                                    mExtractor!!.sampleFlags
                                )
                                Log.e("VideoTrack", "===============on Size===============")
                            } else {
                                this.queueInputBuffer(
                                    inputBufferId,
                                    0,
                                    0,
                                    0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                )
                                isRunning = false
                                Log.e("VideoTrack", "===============End of stream===============")
                            }
                        }
                    }
                    //等到拿到输出的buffer下标
                    val outputId: Int = this.dequeueOutputBuffer(info, 1000)

                    if (outputId >= 0) {
                        //释放buffer，并渲染到 Surface 中
                        this.releaseOutputBuffer(outputId, true)
                    }

                    // 在所有解码后的帧都被渲染后，就可以停止播放了
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM !== 0) {
                        Log.e(
                            "VideoDecoder",
                            "zsr OutputBuffer BUFFER_FLAG_END_OF_STREAM"
                        )
                        break
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
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