package com.autolink.aircontroller;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.autolink.aircontroller.wind.BaseWind;
import com.autolink.aircontroller.wind.WindFootLeft;
import com.autolink.aircontroller.wind.WindFootRight;
import com.autolink.aircontroller.wind.WindLeft;
import com.autolink.aircontroller.wind.WindMiddleLeft;
import com.autolink.aircontroller.wind.WindMiddleRight;
import com.autolink.aircontroller.wind.WindRenderListener;
import com.autolink.aircontroller.wind.WindRight;

import javax.microedition.khronos.opengles.GL10;

public class WindRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private BaseWind baseWind;
    private int wind;
    WindRendererCallBack baseListener;

    public WindRenderer(Context c, int wind) {
        this.wind = wind;
        context = c;
        if (wind == 3) {
            baseWind = new WindMiddleLeft(c);
        } else if (wind == 8) {
            baseWind = new WindMiddleRight(c);
        } else if (wind == 4) {
            baseWind = new WindRight(c);
        } else if (wind == 0) {
            baseWind = new WindLeft(c);
        } else if (wind == 9) {
            baseWind = new WindFootLeft(c);
        }else if (wind == 10) {
            baseWind = new WindFootRight(c);
        }
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
//        GLU.gluLookAt(gl, 1, 1, 3, 0, 0, 0, 0, 1, 0);
        GLU.gluLookAt(gl, -0.1f, 1f, 3f, 0.0f, 0, 0, 0, 1f, 0);
        baseWind.drawWind(gl);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100f);
        baseWind.initTextureCube(gl);
        baseWind.LoadTextures(gl);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void setHorizontalAngle(int angle) {
        baseWind.horizontalWind(angle);
    }

    public void setVerticalAngle(int angle) {
        baseWind.verticalWind(angle);
    }

    void touchDown(float x, float y) {
        if (wind == 9 || wind == 10) {
            return;
        }
        baseWind.touchDown(x, y);
    }

    void touchMove(float x, float y) {
        if (wind == 9 || wind == 10) {
            return;
        }
        baseWind.touchMove(x, y);
    }

    public void setFrameTime(int time) {
        baseWind.setWindLevel(time);
    }

    public void setSwing(boolean swing) {
        baseWind.swingWind(swing);
    }

    public void registerWindRenderer(WindRenderListener listener) {
        baseWind.registerListener(listener);
    }

    public void unRegisterListener() {
        baseWind.unRegisterListener();
    }

    public void setWindStepInfo(float xStep, float yStep) {
        baseWind.setWindStepInfo(xStep, yStep);
    }

    public float[] getWindStepInfo() {
        return baseWind.getWindStepInfo();
    }

    public interface WindRendererCallBack {
        void onGestureCallBack(float xStep, float yStep);
    }
}
