package com.example.media.openGl.shapes

import android.opengl.GLES20

class TriangleRenderer : RendererAbs() {

    init {
        vertices = floatArrayOf(
            0.0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
        )

        fragmentShader =
            """
            precision mediump float;         
            uniform vec4 uColor;             
            void main(){                     
                gl_FragColor = uColor;        
            }
        """


        verticesShader =
            """
            attribute vec4 vPosition;
            uniform mat4 vMatrix;
            void main(){                         
                gl_Position = vMatrix * vPosition;
            }
        """

        drawMode = GLES20.GL_TRIANGLE_STRIP
        drawFirst = 0
        drawCount = 3

    }

}