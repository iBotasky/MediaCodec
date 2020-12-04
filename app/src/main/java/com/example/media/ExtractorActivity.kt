package com.example.media

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_extractor.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class ExtractorActivity : AppCompatActivity() {
    val videoOutPath =
        "${Environment.getExternalStorageDirectory().absolutePath}/extractor_video.mp4"
    val audioOutPath =
        "${Environment.getExternalStorageDirectory().absolutePath}/extractor_audio.mp3"
    val sourcePath = "${Environment.getExternalStorageDirectory().absolutePath}/1.mp4"

    companion object {
        const val TAG = "Extractor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extractor)
        startExtractor.setOnClickListener {
            lifecycleScope.launch {
                it.isEnabled = false
                (it as Button).text = "分离中"
                withContext(Dispatchers.Default) {
                    extractorVideo()
                }
                Toast.makeText(this@ExtractorActivity, "分离视频完成", Toast.LENGTH_SHORT).show()
                it.text = "开始分离音视频"
                it.isEnabled = true
            }
        }
    }

    private fun extractorVideo() {
        // 创建分离器
        var videoTrack = -1
        var audioTrack = -1
        var videoMediaFormat: MediaFormat? = null
        var audioMediaFormat: MediaFormat? = null
        val mExtractor = MediaExtractor()
        mExtractor.setDataSource(sourcePath)
        for (i in 0 until mExtractor.trackCount) {
            val mediaFormat = mExtractor.getTrackFormat(i)
            val mime = mediaFormat.getString(MediaFormat.KEY_MIME)
            if (mime != null) {
                if (mime.startsWith("video/")) {
                    videoTrack = i
                    videoMediaFormat = mediaFormat
                    Log.e(
                        TAG,
                        "videoWidth:${videoMediaFormat.getInteger(MediaFormat.KEY_WIDTH)} " +
                                "videoHeight:${videoMediaFormat.getInteger(MediaFormat.KEY_HEIGHT)} " +
                                "videoDuration:${videoMediaFormat.getLong(MediaFormat.KEY_DURATION)}"
                    )
                } else if (mime.startsWith("audio/")) {
                    audioTrack = i
                    audioMediaFormat = mediaFormat
                }
            }
        }

        // 创建Muxer合成器，合成音频/视频/音视频
        if (videoMediaFormat != null) {
            val mMuxer = MediaMuxer(videoOutPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            mMuxer.addTrack(videoMediaFormat)
            mExtractor.selectTrack(videoTrack)

            mMuxer.start()

            val buffer = ByteBuffer.allocate(500 * 1024)
            val info = MediaCodec.BufferInfo()
            var videoSize = 0

            var curSampleTime = 0L
            var curSampleFlags = 0
            var isEOS = false
            while (!isEOS) {
                buffer.clear()
                videoSize = mExtractor.readSampleData(buffer, 0)
                if (videoSize < 0) {
                    isEOS = true
                    break
                }

                curSampleFlags = mExtractor.sampleFlags
                curSampleTime = mExtractor.sampleTime

                mExtractor.advance()

                info.offset = 0
                info.size = videoSize
                info.flags = curSampleFlags
                info.presentationTimeUs = curSampleTime
                mMuxer.writeSampleData(videoTrack, buffer, info)
            }
            mExtractor.release()
            mMuxer.stop()
            mMuxer.release()
        }
    }

}