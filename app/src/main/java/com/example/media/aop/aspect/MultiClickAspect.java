package com.example.media.aop.aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MultiClickAspect {
    private static final String TAG = "MultiCheckAspect";
    private static final long TIME_INTERVAL = 1000;
    private long mLastClickTime = 0;

    @Pointcut("execution(@com.example.media.aop.annotations.MultiClickCheck * * (..))")
    public void multiClickCheck() {
    }


    @Around("multiClickCheck()")
    public void aroundMethod(ProceedingJoinPoint joinpoint) throws Throwable {
        Log.e(TAG, "点击前");
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastClickTime > TIME_INTERVAL) {
            joinpoint.proceed();
            Log.e(TAG, "点击后");
            mLastClickTime = currentTimeMillis;
        } else {
            Log.e(TAG, "点击间隔太短");
        }
    }
}
