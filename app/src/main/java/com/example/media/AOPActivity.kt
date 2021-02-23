package com.example.media

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.media.aop.annotations.MultiClickCheck
import com.example.media.databinding.ActivityAOPBinding

class AOPActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "AOP"
    }


    private lateinit var binding: ActivityAOPBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAOPBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnAop.setOnClickListener {
            onAOPClick()
        }
    }

    @MultiClickCheck
    private fun onAOPClick() {
        Log.e(TAG, "onClick")
    }
}