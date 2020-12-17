package com.example.media

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.media.codec.DecodeActivity
import com.example.media.codec.ExtractorActivity
import com.example.media.openGl.DrawShapeActivity
import com.example.media.openGl.DrawTextureActivity
import com.example.media.openGl.RenderVideoActivity
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

        openGlDrawShape.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DrawShapeActivity::class.java))
        }

        openGlDrawTexture.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DrawTextureActivity::class.java))
        }

        openGlRenderVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, RenderVideoActivity::class.java))
        }
    }
}