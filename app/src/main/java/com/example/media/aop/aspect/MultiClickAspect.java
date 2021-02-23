package com.example.media.aop.aspect;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    public void aroundMultiClickMethod(ProceedingJoinPoint joinpoint) throws Throwable {
        Log.e(TAG, "点击前");
        final Context context = ((Context) joinpoint.getThis());
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastClickTime > TIME_INTERVAL) {
            joinpoint.proceed();
            Log.e(TAG, "点击后");
            mLastClickTime = currentTimeMillis;
        } else {
            Toast.makeText(context, "点击间隔太短", Toast.LENGTH_SHORT).show();
        }
    }
}
