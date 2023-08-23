package com.example.mylibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class WindRenderer implements GLSurfaceView.Renderer {
    private Context context;

    Bitmap[] bitmaps;
    TextureCube myCube;
    private final int BITMAP_SIZE = 120;
    private int FRAME_TIME = 7;
    private int time = 0;

    private final int MAX_STEP = 200;
    private final int STEP_AUTO_UP = 2;
    private final int STEP_AUTO_DOWN = 1;
    private final int STEP_CUSTOM = 0;
    private int step;
    private int step_mode;
    private float x = 0.0f;

    private boolean plus = true;

    private final float[] BOX_ONE = new float[]{
            //1
            -1.0f, 1.5f,//左上
            -1.0f, -0.8f,//左下
            1.0f, 1.0f,//右上
            1.0f, -1.0f//右下
    };

    private final float[] BOX_TWO = new float[]{
            //1
            -1.0f, 0.8f,//左上
            -1.0f, -1.5f,//左下
            1.0f, 1.0f,//右上
            1.0f, -1.0f//右下
    };
//    private final float[] BOX_ONE = new float[]{
//            //1
//            -0.5f, 1.0f,//左上
//            -0.5f, -0.3f,//左下
//            0.5f, 0.5f,//右上
//            0.5f, -0.5f//右下
//    };
//
//    private final float[] BOX_TWO = new float[]{
//            //1
//            -0.5f, 0.3f,//左上
//            -0.5f, -1.0f,//左下
//            0.5f, 0.5f,//右上
//            0.5f, -0.5f//右下
//    };

    private float[] boxs;

    private float[] boxs_src;

    public WindRenderer(Context c) {
        context = c;
        bitmaps = new Bitmap[BITMAP_SIZE];
        for (int i = 0; i < BITMAP_SIZE; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(c.getResources(), DataUtil.bitmapIds[i]);
        }
        myCube = new TextureCube(bitmaps);
        boxs = new float[BITMAP_SIZE * 8];
        boxs_src = new float[BITMAP_SIZE * 8];
        for (int n = 0; n < BITMAP_SIZE * 8; n++) {
            boxs[n] = BOX_ONE[n % 8];
            boxs_src[n] = BOX_TWO[n % 8];
        }
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        GLU.gluLookAt(gl, 1, 1, 3, 0, 0, 0, 0, 1, 0);
        myCube.draw(gl);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100f);
        myCube.init(gl);
        myCube.LoadTextures(gl);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void setZRot(float rot) {
        myCube.zrot = rot;
    }

    public void setFrameTime(int time) {
        FRAME_TIME = time;
    }

    public void setStepMode(int mode) {
        step_mode = mode;
    }

    class TextureCube {
        Bitmap[] mbitmaps;
        int[] textures;
        float xrot = 0.0f;
        float yrot = 0.0f;
        float zrot = 95f;
        int frame = 0;

        FloatBuffer textureBuffer;
        FloatBuffer cubeBuff;

        public TextureCube(Bitmap[] bitmaps) {
            mbitmaps = bitmaps;
        }

        public void init(GL10 gl) {
            cubeBuff = makeFloatBuffer(boxs);
            textureBuffer = makeFloatBuffer(DataUtil.textureCoordinates);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glClearColor(0f, 0f, 0f, 0f);
            gl.glClearDepthf(1.0f);
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, cubeBuff);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glShadeModel(GL10.GL_SMOOTH);
        }

        public void draw(GL10 gl) {
            if (step_mode == STEP_AUTO_UP) {
                step++;
                if (step >= MAX_STEP) {
                    step_mode = STEP_AUTO_DOWN;
                }
            } else if (step_mode == STEP_AUTO_DOWN) {
                step--;
                if (step <= 0) {
                    step_mode = STEP_AUTO_UP;
                }
            }
            for (int n = 0; n < boxs.length; n++) {
                cubeBuff.put(n, boxs[n] + (((boxs_src[n] - boxs[n]) / MAX_STEP) * step));
            }
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, cubeBuff);

            //gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

            //gl.glRotatef(xrot, 1, 0, 0);  //旋转 x
            gl.glRotatef(yrot, 0, 1, 0);  //旋转 y
            //gl.glRotatef(zrot, 0, 0, 1f);  //旋转 z
            gl.glTranslatef(-1f, 0f, 0f);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[frame]);
//            for (int i = 0; i < 6; i++) {
            gl.glNormal3f(DataUtil.normals[0][0], DataUtil.normals[0][1], DataUtil.normals[0][2]);

//            }
            //xrot += 0.5f;
            if (plus) {
                if (yrot <= 60) {
                    yrot += 0.5f;
                } else {
                    plus = false;
                }
            } else {
                if (yrot > 0) {
                    yrot -= 0.5f;
                } else {
                    plus = true;
                }
            }
            //zrot += 0.5f;
            time++;
            if (time >= FRAME_TIME) {
                time = 0;
                if (frame < textures.length - 1) {
                    frame += 1;
                } else {
                    frame = 0;
                }
            }


            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4 * (frame), 4);
        }

        public FloatBuffer makeFloatBuffer(float[] arr) {
            ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer fb = bb.asFloatBuffer();
            fb.put(arr);
            fb.position(0);
            return fb;
        }

        public void LoadTextures(GL10 gl) {
            textures = new int[mbitmaps.length];
            gl.glGenTextures(mbitmaps.length, textures, 0);
            for (int i = 0; i < textures.length; i++) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mbitmaps[i], 0);
                mbitmaps[i].recycle();
            }
            mbitmaps = null;
        }
    }
}
