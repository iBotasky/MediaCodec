package com.example.media.openGl.shapes

import android.opengl.GLES20

class LineRenderer : RendererAbs() {

    init {
        vertices = floatArrayOf(
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            -0.5f, -0.5f
        )

        verticesShader =
            """
            attribute vec2 vPosition;
            void main(){
                gl_Position = vec4(vPosition,0,1);
            }
            """

        fragmentShader =
            """
            precision mediump float;
            uniform vec4 uColor;
            void main(){
                gl_FragColor = uColor;
            }
            """

        drawMode = GLES20.GL_TRIANGLE_FAN
        drawFirst = 0
        drawCount = 6
    }


}