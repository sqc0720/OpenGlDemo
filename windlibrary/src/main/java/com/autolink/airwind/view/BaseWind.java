package com.autolink.airwind.view;

import javax.microedition.khronos.opengles.GL10;

public abstract class BaseWind {

    public abstract void drawWind(GL10 gl);

    public abstract void initTextureCube(GL10 gl);

    public abstract void LoadTextures(GL10 gl);

    public abstract void setWindLevel(int level);

    public abstract void swingWind(boolean on);

    public abstract void horizontalWind(int angle);

    public abstract void verticalWind(int angle);

    public abstract void touchDown(float x, float y);

    public abstract void touchMove(float x, float y);

    public abstract void registerListener(WindRenderListener windRendererCallBack);

    public abstract void unRegisterListener();

    public abstract void setWindStepInfo(float xStep, float yStep);
    public abstract void smoothWindStepInfo(float xStep, float yStep);
    public abstract float[] getWindStepInfo();
}
