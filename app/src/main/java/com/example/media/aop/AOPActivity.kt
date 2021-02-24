package com.example.media.aop

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.media.aop.annotations.MultiClickCheck
import com.example.media.aop.annotations.NetworkCheck
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


        binding.btnMultiCheck.setOnClickListener {
            onMultiCheckClick()
        }

        binding.btnNetworkCheck.setOnClickListener {
            onNetworkCheck()
        }
    }

    @MultiClickCheck
    private fun onMultiCheckClick() {
        Log.e(TAG, "点击了")
        Toast.makeText(this@AOPActivity, "点击成功", Toast.LENGTH_SHORT).show()
    }

    @NetworkCheck
    private fun onNetworkCheck() {
        Toast.makeText(this@AOPActivity, "链接成功", Toast.LENGTH_SHORT).show()
    }
}