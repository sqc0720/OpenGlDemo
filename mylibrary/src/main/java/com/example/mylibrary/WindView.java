package com.example.mylibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.mylibrary.wind.WindRenderListener;

//你可以通过设置GLSurfaceView.RENDERMODE_WHEN_DIRTY来让你的GLSurfaceView监听到数据变化的时候再去刷新，
// 即修改GLSurfaceView的渲染模式。这个设置可以防止重绘GLSurfaceView，直到你调用了requestRender()，这个设置在默写层面上来说，对你的APP是更有好处的
public class WindView extends GLSurfaceView {

    private final String TAG = WindView.class.getSimpleName();

    private WindRenderer mRenderer;
    private GestureCallBack gestureCallBack;

    public WindView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        //setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mRenderer = new WindRenderer(context, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    public void registerGestureListener(GestureCallBack gestureCallBack) {
        this.gestureCallBack = gestureCallBack;
    }

    public void unRegisterGestureListener() {
        gestureCallBack = null;
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context
        //setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.hvacIcon);
        int hvacIcon_position = array.getInt(R.styleable.hvacIcon_position, -1);

        Log.d(TAG, "value--->" + hvacIcon_position);
        mRenderer = new WindRenderer(context, hvacIcon_position);
        mRenderer.registerWindRenderer(windRenderListener);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setAlpha(0.1f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "ACTION_DOWN x->" + event.getX() + ",y->" + event.getY());
            mRenderer.touchDown(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "ACTION_MOVE x->" + event.getX() + ",y->" + event.getY());
            mRenderer.touchMove(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "ACTION_UP ->" + event.getX());
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRenderer.unRegisterListener();
    }

    public void setWindLevel(int level) {
        if (level > 7 || level < 0) {
            Log.d(TAG, "level data error! level->" + level);
            return;
        }
        mRenderer.setFrameTime(8 - level);
    }

    public void setSwing(boolean swing) {
        mRenderer.setSwing(swing);
    }

    private WindRenderListener windRenderListener = new WindRenderListener() {
        @Override
        public void onGestureCallBack(float xStep, float yStep) {
            Log.d(TAG, "onGestureCallBack() called with: xStep = [" + xStep + "], yStep = [" + yStep + "]");
            if (gestureCallBack != null) {
                gestureCallBack.onGestureCallBack(xStep, yStep);
            }
        }
    };

    public interface GestureCallBack {
        void onGestureCallBack(float xStep, float yStep);
    }
}