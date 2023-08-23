package com.app.opengldemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

//你可以通过设置GLSurfaceView.RENDERMODE_WHEN_DIRTY来让你的GLSurfaceView监听到数据变化的时候再去刷新，
// 即修改GLSurfaceView的渲染模式。这个设置可以防止重绘GLSurfaceView，直到你调用了requestRender()，这个设置在默写层面上来说，对你的APP是更有好处的
public class WindView extends GLSurfaceView {

    private final String TAG = WindView.class.getSimpleName();

    private WindRenderer mRenderer;


    public WindView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        //setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRenderer = new WindRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context
        //setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRenderer = new WindRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("zsktest", "ACTION_MOVE ->" + event.getX());
            mRenderer.setZRot(event.getX() / 2);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {

        }
        return true;
    }

    public void setWindLevel(int level) {
        if (level > 7 || level < 0) {
            Log.d(TAG, "level data error! level->" + level);
            return;
        }
        mRenderer.setFrameTime(7 - level);
    }

    public void setMode(int mode) {
        if (mode < 0 || mode > 2) {
            Log.d(TAG, "mode data error! mode->" + mode);
            return;
        }
        mRenderer.setStepMode(mode);
    }
}