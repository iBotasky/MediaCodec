package com.example.media.openGl

import android.content.Context
import android.opengl.GLSurfaceView
import com.example.media.openGl.Shapes.LineRenderer
import com.example.media.openGl.Shapes.TriangleRenderer

class MGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: GLSurfaceView.Renderer

    init {
        // 穿件一个OpenGl2.0的上下文
        setEGLContextClientVersion(2)

        // 三角形
        renderer = TriangleRenderer()
        // 线
//        renderer = LineRenderer()

        // 设置GLSurface要渲染的Render
        setRenderer(renderer)

        // 设置只有在数据发生变化才会绘制视图
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}