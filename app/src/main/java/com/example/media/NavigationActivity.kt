package com.example.media

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initView()
    }

    private fun initView() {
        decodeVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DecodeActivity::class.java))
        }

        decodeEncodeVideo.setOnClickListener {

        }

        muxerVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, ExtractorActivity::class.java))
        }

        openGlDrawTriangle.setOnClickListener {
            startActivity(Intent(NavigationActivity@this, DrawTriangleActivity::class.java))
        }
    }
}