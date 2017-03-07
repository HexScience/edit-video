package com.hecorat.azplugin2.preview;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by bkmsx on 2/14/2017.
 */

class CustomRenderer implements GLSurfaceView.Renderer {
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private FloatBuffer vertexBuffer, textureBuffer;
    private float[] mvpMatrix = new float[16], textureMatrix = new float[16];
    private int programHandle;
    private boolean stop;

    private OnSurfaceTextureListener callback;
    private SurfaceTexture surfaceTexture;

    CustomRenderer(OnSurfaceTextureListener listener) {
        callback = listener;
        stop = false;
    }

    void reset() {
        stop = false;
    }

    void stop() {
        stop = true;
    }

    private void setupVertex() {
        float[] vertexCoords = new float[]{
                -1, 1, 0,
                -1, -1, 0,
                1, 1, 0,
                1, 1, 0,
                -1, -1, 0,
                1, -1, 0
        };

        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertexCoords).position(0);
    }

    void setupVideoSize(float left, float right, float bottom, float top) {
        float[] textureCoords = new float[] {
                left, top,
                left, bottom,
                right, top,
                right, top,
                left, bottom,
                right, bottom
        };

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureCoords).position(0);
    }

    private void setupTexture() {
        setupVideoSize(0, 1, 0, 1);
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0]);

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textures[0]);
        callback.onSurfaceTextureCreated(surfaceTexture);
    }

    private void setupProgram() {
        String vertexCode = Shader.VERTEX_CODE;
        String fragmentCode = Shader.FRAGMENT_CODE;
        programHandle = GLES20.glCreateProgram();
        int vertexShader = getShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
        GLES20.glAttachShader(programHandle, vertexShader);
        GLES20.glAttachShader(programHandle, fragmentShader);
        GLES20.glLinkProgram(programHandle);
    }

    private int getShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        setupVertex();
        setupTexture();
        setupProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(textureMatrix);

        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (stop) {
            return;
        }

        GLES20.glUseProgram(programHandle);

        int vertexHandle = GLES20.glGetAttribLocation(programHandle, "a_VertexCoords");
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int textureHandle = GLES20.glGetAttribLocation(programHandle, "a_TextureCoords");
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        Matrix.setIdentityM(mvpMatrix, 0);
        int mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        int textureMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_TextureMatrix");
        GLES20.glUniformMatrix4fv(textureMatrixHandle, 1, false, textureMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(textureHandle);
    }

    interface OnSurfaceTextureListener {
        void onSurfaceTextureCreated(SurfaceTexture surfaceTexture);
    }
}
