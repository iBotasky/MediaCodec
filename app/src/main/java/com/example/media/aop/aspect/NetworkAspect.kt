package com.example.media.aop.aspect

import android.content.Context
import android.widget.Toast
import com.example.media.util.NetworkUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut


@Aspect
class NetworkAspect {
    companion object {
        private const val TAG = "NetworkAspect"
    }


    @Pointcut("execution(@com.example.media.aop.annotations.NetworkCheck * * (..))")
    fun networkCheck() {
    }

    @Around("networkCheck()")
    fun aroundNetworkCheckMethod(proceedingJoinPoint: ProceedingJoinPoint) {
        val context = proceedingJoinPoint.`this` as Context
        if (NetworkUtils.isNetworkAvailable(context)){
            proceedingJoinPoint.proceed()
        }else{
            Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show()
        }
    }
}