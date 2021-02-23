package com.example.media

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.media.aop.AOPActivity
import com.example.media.codec.DecodeActivity
import com.example.media.codec.ExtractorActivity
import com.example.media.databinding.ActivityNavigationBinding
import com.example.media.openGl.DrawShapeActivity
import com.example.media.openGl.DrawTextureActivity
import com.example.media.openGl.RenderVideoActivity

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.decodeVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DecodeActivity::class.java))
        }

        binding.decodeEncodeVideo.setOnClickListener {

        }

        binding.muxerVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, ExtractorActivity::class.java))
        }

        binding.openGlDrawShape.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DrawShapeActivity::class.java))
        }

        binding.openGlDrawTexture.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, DrawTextureActivity::class.java))
        }

        binding.openGlRenderVideo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, RenderVideoActivity::class.java))
        }


        binding.aopDemo.setOnClickListener {
            startActivity(Intent(NavigationActivity@ this, AOPActivity::class.java))
        }
    }
}