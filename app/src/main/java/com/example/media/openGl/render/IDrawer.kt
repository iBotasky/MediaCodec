package com.example.media.openGl.render

interface IDrawer {


    fun onCreate(textureId: Int)

    fun onDrawFrame()

    fun setVideoSize(width: Int, height: Int)

    fun setPlayerSize(width: Int, height: Int)

    fun release()
}