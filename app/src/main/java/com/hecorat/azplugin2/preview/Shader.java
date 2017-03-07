package com.hecorat.azplugin2.preview;

/**
 * Created by bkmsx on 2/14/2017.
 */

interface Shader {
    String VERTEX_CODE = "uniform mat4 u_MVPMatrix;" +
            "uniform mat4 u_TextureMatrix;" +
            "attribute vec4 a_VertexCoords;" +
            "attribute vec4 a_TextureCoords;" +
            "varying vec2 v_TextureCoords;" +
            "void main() {" +
            "  v_TextureCoords = (u_TextureMatrix * a_TextureCoords).xy;" +
            "  gl_Position = u_MVPMatrix * a_VertexCoords;" +
            "}";
    String FRAGMENT_CODE = "#extension GL_OES_EGL_image_external : require \n" +
            "precision mediump float;" +
            "uniform samplerExternalOES u_Texture;" +
            "varying vec2 v_TextureCoords;" +
            "void main() {" +
            "   gl_FragColor = texture2D(u_Texture, v_TextureCoords);" +
            "}";
}
