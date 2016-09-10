/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This part of the Wire software uses source coded posted on the StackOverflow site.
 * (http://stackoverflow.com/questions/12519235)
 *
 * That work is licensed under a Creative Commons Attribution-ShareAlike 2.5 Generic License.
 * (http://creativecommons.org/licenses/by-sa/2.5)
 *
 * Contributors on StackOverflow:
 *  - Dan Collins (http://stackoverflow.com/users/2437767)
 *  - psharma (http://stackoverflow.com/users/1703437)
 *  - Joel Teply (http://stackoverflow.com/users/1060382)
 */
package com.waz.zclient.pages.main.profile.camera.colorlens;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import com.waz.zclient.camera.CameraFacing;
import com.waz.zclient.utils.device.DeviceDetector;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class DirectVideo {


    private final static String VERTEX_SHADER_CODE =
        "attribute vec4 position;" +
        "attribute vec2 inputTextureCoordinate;" +
        "varying vec2 textureCoordinate;" +
        "void main()" +
        "{" +
        "gl_Position = position;" +
        "textureCoordinate = inputTextureCoordinate;" +
        "}";

    private final static String FRAGMENT_SHADER_CODE =
        "#extension GL_OES_EGL_image_external : require\n" +
        "precision mediump float;" +
        "varying vec2 textureCoordinate;                            \n" +
        "uniform samplerExternalOES s_texture;               \n" +
        "void main() {" +
        " vec2 textureCoordinate = vec2(textureCoordinate.s, 1.0 - textureCoordinate.t);  \n" + //flip it!
        " vec4 normalColor = texture2D(s_texture, textureCoordinate); " +
        " float gray = (normalColor.r + normalColor.g + normalColor.b) / 3.0; " +
        " vec4 grayColor = vec4(gray, gray, gray, 1); \n" +
        " gl_FragColor = grayColor; \n" +
        "}";

    private ShortBuffer drawListBuffer;
    private final int program;


    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;

    // in counterclockwise order:
    private static final float[] SQUARE_VERTICES_FRONT_FACING = {
        -1.0f, 1.0f,
        -1.0f, -1.0f,
        1.0f, -1.0f,
        1.0f, 1.0f
    };

    // in counterclockwise order:
    private static final float[] SQUARE_VERTICES_FRONT_FACING_NEXUS_6 = {
        1.0f, -1.0f,
        1.0f, 1.0f,
        -1.0f, 1.0f,
        -1.0f, -1.0f
    };

    // in counterclockwise order:
    private static final float[] SQUARE_VERTICES_BACK_FACING = {
        -1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, -1.0f
    };

    // order to draw vertices
    private short[] drawOrder = {
        0, 1, 2, 2, 3, 0
    };

    // in counterclockwise order:
    static float[] textureVertices = {
        1.0f, 0.0f,
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    };

    private static final int VERTEXT_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int texture;

    public DirectVideo(int cameraId) {
        float[] squareVertices = getSquareVerticesForCameraAndDevice(cameraId);

        texture = createTexture();

        ByteBuffer bb = ByteBuffer.allocateDirect(squareVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareVertices);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        FloatBuffer textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

        program = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(program, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(program, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        int positionHandle = GLES20.glGetAttribLocation(program, "position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEXT_STRIDE,
                                     vertexBuffer);

        int textureCoordHandle = GLES20.glGetAttribLocation(program, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(textureCoordHandle);
        GLES20.glVertexAttribPointer(textureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEXT_STRIDE,
                                     textureVerticesBuffer);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);


    }

    public void draw() {
        GLES20.glUseProgram(program);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
    }

    public int getTexture() {
        return texture;
    }

    static private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    static private int createTexture() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                               GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                               GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                               GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                               GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    // Always the same vertex order for back facing cameras
    // Special order for Nexus 6 front-facing cam.
    private float[] getSquareVerticesForCameraAndDevice(int cameraId) {
        if (cameraId == CameraFacing.BACK.facing) {
            return SQUARE_VERTICES_BACK_FACING;
        } else if (DeviceDetector.isNexus6()) {
            return SQUARE_VERTICES_FRONT_FACING_NEXUS_6;
        }
        return SQUARE_VERTICES_FRONT_FACING;
    }
}
