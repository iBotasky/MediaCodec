package com.example.media.openGl.render

interface IDrawer {


    fun onCreate(textureId: Int)

    fun onChange(width: Int, height: Int)

    fun onDrawFrame()

    fun release()
}