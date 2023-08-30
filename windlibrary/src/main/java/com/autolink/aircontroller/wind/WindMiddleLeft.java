package com.autolink.aircontroller.wind;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.autolink.aircontroller.MiddleLeftDataUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class WindMiddleLeft extends BaseWind {

    TextureCube myCube;
    Bitmap[] bitmaps;
    private final int BITMAP_SIZE = 120;
    int FRAME_TIME = 5;
    private int time = 0;

    private final int MAX_STEP = 200;
    private final int STEP_AUTO_UP = 2;
    private final int STEP_AUTO_DOWN = 1;
    private final int STEP_CUSTOM = 0;
    private int step;
    int step_mode = STEP_AUTO_UP;
    private float x = 0.0f;

    private boolean plus = true;

    private final float MAX_SWING_ANGLE = 50f;
    private final float MIN_SWING_ANGLE = 0f;
    private final float SWING_ANGLE = MIN_SWING_ANGLE - MAX_SWING_ANGLE;

    private boolean swing = false;

    private float down_x, down_y;
    private float down_horizontal_angle, down_vertical_angle;

    private final float[] BOX_ONE = new float[]{
            //1
            -0.8f, -1.0f,//左下
            -0.8f, 1.0f,//左上
            0.8f, -1.0f,//右下
            0.8f, 1.0f,//右上
    };

    private final float[] BOX_TWO = new float[]{
            //1
            -1.8f, -1.0f,//左下
            -1.0f, 1.0f,//左上
            0.0f, -1.0f,//右下
            1.0f, 1.0f,//右上
    };

    private float[] boxs;

    private float[] boxs_src;
    private WindRenderListener windRendererCallBack;

    public WindMiddleLeft(Context c) {
        bitmaps = new Bitmap[BITMAP_SIZE];
        for (int i = 0; i < BITMAP_SIZE; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(c.getResources(), MiddleLeftDataUtil.bitmapIds[i]);
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
    public  void drawWind(GL10 gl) {
        myCube.drawWind(gl);
    }

    @Override
    public   void initTextureCube(GL10 gl) {
        myCube.init(gl);
    }

    @Override
    public  void LoadTextures(GL10 gl) {
        myCube.LoadTextures(gl);
    }

    @Override
    public  void setWindLevel(int level) {

    }

    @Override
    public  void swingWind(boolean on) {
        swing = on;
    }

    @Override
    public  void horizontalWind(int step) {

    }

    @Override
    public  void verticalWind(int step) {

    }

    @Override
    public  void touchDown(float x, float y) {
        down_x = x;
        down_y = y;
        down_horizontal_angle = this.step;
        down_vertical_angle = myCube.xrot;
    }

    @Override
    public  void touchMove(float x, float y) {
        if (swing) {
            return;
        }
        float angleX = (down_x - x) + down_horizontal_angle;
        float angleY = (down_y - y) / 3 + down_vertical_angle;

        if (angleX >= MAX_STEP) {
            this.step = MAX_STEP;
        } else if (angleX <= 0) {
            this.step = 0;
        } else {
            this.step = (int) angleX;
        }
        if (angleY >= MAX_SWING_ANGLE) {
            myCube.xrot = MAX_SWING_ANGLE;
        } else if (angleY <= MIN_SWING_ANGLE) {
            myCube.xrot = MIN_SWING_ANGLE;
        } else {
            myCube.xrot = (int) angleY;
        }
        callBack();
    }

    @Override
    public void registerListener(WindRenderListener windRendererCallBack) {
        this.windRendererCallBack = windRendererCallBack;
    }

    @Override
    public void unRegisterListener() {
        this.windRendererCallBack = null;

    }

    @Override
    public void setWindStepInfo(float xStep, float yStep) {
        step = (int) xStep;
        myCube.xrot = yStep;
    }

    @Override
    public float[] getWindStepInfo() {
        float[] windStepInfo = new float[2];
        windStepInfo[0] = step;
        windStepInfo[1] = myCube.xrot;
        return windStepInfo;
    }

    private void callBack() {
        if (windRendererCallBack != null) {
            windRendererCallBack.onGestureCallBack(step, myCube.xrot);
        }
    }

    class TextureCube {
        Bitmap[] mbitmaps;
        int[] textures;
        float xrot = 0.0f;
        float yrot = 0.0f;
        float zrot = 0.0f;
        int frame = 0;

        FloatBuffer textureBuffer;
        FloatBuffer cubeBuff;

        public TextureCube(Bitmap[] bitmaps) {
            mbitmaps = bitmaps;
        }

        public void init(GL10 gl) {
            cubeBuff = makeFloatBuffer(boxs);
            textureBuffer = makeFloatBuffer(MiddleLeftDataUtil.textureCoordinates);
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
            if (swing) {
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
            }
            for (int n = 0; n < boxs.length; n++) {
                cubeBuff.put(n, boxs[n] + (((boxs_src[n] - boxs[n]) / MAX_STEP) * step));
            }
            gl.glVertexPointer(2, GL10.GL_FLOAT, 0, cubeBuff);

            //gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

            gl.glRotatef(xrot, 1, 0, 0);  //旋转 x
            //gl.glRotatef(zrot, 0, 0, 1f);  //旋转 z
            // gl.glTranslatef(-2.5f, 0f, 0f);//先将wind移动到左侧位置
            //gl.glRotatef(yrot, 0, 1f, 0);  // 进行 y坐标 旋转 y
            gl.glTranslatef(0.3f, -0.8f, 0f); // 再次将wind移动改变旋转轴
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[frame]);
            gl.glNormal3f(MiddleLeftDataUtil.normals[0][0], MiddleLeftDataUtil.normals[0][1], MiddleLeftDataUtil.normals[0][2]);

            //xrot += 0.5f;
            //yrot += 0.5f;
            //zrot += 0.5f;
//            if (plus) {
//                if (xrot <= MAX_SWING_ANGLE) {
//                    xrot += 0.5f;
//                } else {
//                    plus = false;
//                }
//            } else {
//                if (xrot >= MIN_SWING_ANGLE) {
//                    xrot -= 0.5f;
//                } else {
//                    plus = true;
//                }
//            }
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
