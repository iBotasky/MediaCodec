package com.example.media.openGl.render

interface IDrawer {


    fun onCreate()

    fun onChange(width: Int, height: Int)

    fun onDrawFrame()
}