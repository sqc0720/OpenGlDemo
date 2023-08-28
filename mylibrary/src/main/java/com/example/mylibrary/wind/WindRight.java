package com.example.mylibrary.wind;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.example.mylibrary.RightDataUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class WindRight extends BaseWind {

    TextureCube myCube;
    Bitmap[] bitmaps;
    private final int BITMAP_SIZE = 120;
    int FRAME_TIME = 5;
    private int time = 0;

    private final int MAX_STEP = 200;
    private final int STEP_AUTO_UP = 2;
    private final int STEP_AUTO_DOWN = 1;
    private final int STEP_CUSTOM = 0;
    private int step = 100;
    int step_mode = STEP_CUSTOM;
    private float x = 0.0f;

    private final float MAX_SWING_ANGLE = 290f;
    private final float MIN_SWING_ANGLE = 360f;
    private final float SWING_ANGLE = MIN_SWING_ANGLE - MAX_SWING_ANGLE;
    private boolean plus = true;
    private boolean swing = false;

    private float down_x, down_y;
    private float down_horizontal_angle, down_vertical_angle;

    private final float[] BOX_ONE = new float[]{
            //1
            -1.0f, -1.0f,//左下
            -1.0f, 1.0f,//左上
            1.0f, -1.8f,//右下
            1.0f, 0.4f,//右上
    };

    private final float[] BOX_TWO = new float[]{
            //1
            -1.0f, -1.0f,//左下
            -1.0f, 1.0f,//左上
            1.0f, -0.4f,//右下
            1.0f, 1.8f,//右上
    };

    private float[] boxs;

    private float[] boxs_src;
    private WindRenderListener windRendererCallBack;

    public WindRight(Context c) {
        bitmaps = new Bitmap[BITMAP_SIZE];
        for (int i = 0; i < BITMAP_SIZE; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(c.getResources(), RightDataUtil.bitmapIds[i]);
        }
        myCube = new TextureCube(bitmaps);
        boxs = new float[BITMAP_SIZE * 8];
        boxs_src = new float[BITMAP_SIZE * 8];
        for (int n = 0; n < BITMAP_SIZE * 8; n++) {
            boxs[n] = BOX_ONE[n % 8];
            boxs_src[n] = BOX_TWO[n % 8];
        }
    }

    @Override
    public void drawWind(GL10 gl) {
        myCube.drawWind(gl);
    }

    @Override
    public void initTextureCube(GL10 gl) {
        myCube.init(gl);
    }

    @Override
    public void LoadTextures(GL10 gl) {
        myCube.LoadTextures(gl);
    }

    @Override
    public void setWindLevel(int level) {
        FRAME_TIME = level;
    }

    @Override
    public void swingWind(boolean on) {
        swing = on;
    }

    @Override
    public void horizontalWind(int angle) {
        if (swing) {
            return;
        }
        if (angle <= MIN_SWING_ANGLE && angle >= MAX_SWING_ANGLE) {
            myCube.yrot = angle;
        }
    }

    @Override
    public void verticalWind(int angle) {
        if (swing) {
            return;
        }
        if (angle <= MAX_STEP && angle >= 0) {
            step = angle;
        }
    }

    @Override
    public void touchDown(float x, float y) {
        down_x = x;
        down_y = y;
        down_horizontal_angle = myCube.yrot;
        down_vertical_angle = this.step;
    }

    @Override
    public void touchMove(float x, float y) {
        if (swing) {
            return;
        }
        float angleX = (x - down_x) / 3 + down_horizontal_angle;
        float angleY = (down_y - y) + down_vertical_angle;

        if (angleX >= MIN_SWING_ANGLE) {
            myCube.yrot = MIN_SWING_ANGLE;
        } else if (angleX <= MAX_SWING_ANGLE) {
            myCube.yrot = MAX_SWING_ANGLE;
        } else {
            myCube.yrot = angleX;
        }
        if (angleY >= MAX_STEP) {
            step = MAX_STEP;
        } else if (angleY <= 0) {
            step = 0;
        } else {
            step = (int) angleY;
        }
        windRendererCallBack.onGestureCallBack(step, myCube.yrot);
        callBack();
    }
    private void callBack() {
        if (windRendererCallBack != null) {
            windRendererCallBack.onGestureCallBack(step, myCube.yrot);
        }
    }
    @Override
    public void registerListener(WindRenderListener windRendererCallBack) {
        this.windRendererCallBack = windRendererCallBack;
    }

    @Override
    public void unRegisterListener() {
        this.windRendererCallBack = null;

    }

    class TextureCube {
        Bitmap[] mbitmaps;
        int[] textures;
        float xrot = 0.0f;
        float yrot = 360.0f;
        float zrot = 0.0f;
        int frame = 0;

        FloatBuffer textureBuffer;
        FloatBuffer cubeBuff;

        public TextureCube(Bitmap[] bitmaps) {
            mbitmaps = bitmaps;
        }

        public void init(GL10 gl) {
            cubeBuff = makeFloatBuffer(boxs);
            textureBuffer = makeFloatBuffer(RightDataUtil.textureCoordinates);
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

        public void drawWind(GL10 gl) {
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

            //gl.glRotatef(xrot, 1, 0, 0);  //旋转 x
            //gl.glRotatef(zrot, 0, 0, 1f);  //旋转 z
            // gl.glTranslatef(-2.5f, 0f, 0f);//先将wind移动到左侧位置
            gl.glRotatef(yrot, 0, 1, 0);  // 进行 y坐标 旋转 y
            gl.glTranslatef(0.8f, 0f, 0f); // 再次将wind移动改变旋转轴
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[frame]);
            gl.glNormal3f(RightDataUtil.normals[0][0], RightDataUtil.normals[0][1], RightDataUtil.normals[0][2]);

            //xrot += 0.5f;
            //zrot += 0.5f;
            if (swing) {
                if (plus) {
                    if (yrot < MIN_SWING_ANGLE) {
                        yrot += 0.5f;
                    } else {
                        plus = false;
                    }
                } else {
                    if (yrot >= MAX_SWING_ANGLE) {
                        yrot -= 0.5f;
                    } else {
                        plus = true;
                    }
                }
            }
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
