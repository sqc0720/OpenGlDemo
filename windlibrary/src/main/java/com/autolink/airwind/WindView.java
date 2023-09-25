package com.autolink.airwind;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.autolink.airwind.view.WindRenderListener;

import java.util.Arrays;

//你可以通过设置GLSurfaceView.RENDERMODE_WHEN_DIRTY来让你的GLSurfaceView监听到数据变化的时候再去刷新，
// 即修改GLSurfaceView的渲染模式。这个设置可以防止重绘GLSurfaceView，直到你调用了requestRender()，这个设置在默写层面上来说，对你的APP是更有好处的
public class WindView extends GLSurfaceView {

    private final String TAG = WindView.class.getSimpleName();

    private WindRenderer mRenderer;
    private GestureCallBack gestureCallBack;
    private boolean mEnable = true;

    private boolean open = true;

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
        mRenderer.openRender(open);
        mRenderer.registerWindRenderer(windRenderListener);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setAlpha(0.1f);
    }

    private static final int MAX_DISTANCE_FOR_CLICK = 50;
    private static final int MAX_INTERVAL_FOR_CLICK = 150;
    private static final int MAX_DOUBLE_CLICK_INTERVAL = 500;
    private int mDownX = 0;
    private int mDownY = 0;
    private int mTempX = 0;
    private int mTempY = 0;
    private boolean mIsWaitUpEvent = false;
    private boolean mIsWaitDoubleClick = false;
    private final Runnable mTimerForUpEvent = new Runnable() {
        public void run() {
            if (mIsWaitUpEvent) {
                Log.d(TAG, "The mTimerForUpEvent has executed, so set the mIsWaitUpEvent as false");
                mIsWaitUpEvent = false;
            } else {
                Log.d(TAG, "The mTimerForUpEvent has executed, mIsWaitUpEvent is false,so do nothing");
            }
        }
    };
    private final Runnable mTimerForSecondClick = new Runnable() {
        @Override
        public void run() {
            if (mIsWaitDoubleClick) {
                Log.d(TAG, "The mTimerForSecondClick has executed,so as a singleClick");
                mIsWaitDoubleClick = false;
                // at here can do something for singleClick!!
            } else {
                Log.d(TAG, "The mTimerForSecondClick has executed, the doubleclick has executed ,so do thing");
            }
        }
    };

    private void onSingleClick() {
        Log.d(TAG, "single click here");
        if (mIsWaitDoubleClick) {
            onDoubleClick();
            mIsWaitDoubleClick = false;
            removeCallbacks(mTimerForSecondClick);
        } else {
            mIsWaitDoubleClick = true;
            postDelayed(mTimerForSecondClick, MAX_DOUBLE_CLICK_INTERVAL);
        }
    }

    private void onDoubleClick() {
        Log.d(TAG, "double click here");
        if (gestureCallBack != null) {
            gestureCallBack.onDoubleClick();
        }
    }

    private void touchEvent(MotionEvent event) {
        if (!mIsWaitUpEvent && event.getAction() != MotionEvent.ACTION_DOWN) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mIsWaitUpEvent = true;
                postDelayed(mTimerForUpEvent, MAX_INTERVAL_FOR_CLICK);
                break;
            case MotionEvent.ACTION_MOVE:
                mTempX = (int) event.getX();
                mTempY = (int) event.getY();
                if (Math.abs(mTempX - mDownX) > MAX_DISTANCE_FOR_CLICK
                        || Math.abs(mTempY - mDownY) > MAX_DISTANCE_FOR_CLICK) {
                    mIsWaitUpEvent = false;
                    removeCallbacks(mTimerForUpEvent);
                    Log.d(TAG, "The move distance too far:cancel the click");
                }
                break;
            case MotionEvent.ACTION_UP:
                mTempX = (int) event.getX();
                mTempY = (int) event.getY();
                if (Math.abs(mTempX - mDownX) > MAX_DISTANCE_FOR_CLICK
                        || Math.abs(mTempY - mDownY) > MAX_DISTANCE_FOR_CLICK) {
                    mIsWaitUpEvent = false;
                    removeCallbacks(mTimerForUpEvent);
                    Log.d(TAG, "The touch down and up distance too far:cancel the click");
                    break;
                } else {
                    mIsWaitUpEvent = false;
                    removeCallbacks(mTimerForUpEvent);
                    onSingleClick();
                    return;
                }
            case MotionEvent.ACTION_CANCEL:
                mIsWaitUpEvent = false;
                removeCallbacks(mTimerForUpEvent);
                Log.d(TAG, "The touch cancel state:cancel the click");
                break;
            default:
                Log.d(TAG, "irrelevant MotionEvent state:" + event.getAction());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchEvent(event);
        if (mEnable && open) {
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

    public void setTouchEnable(boolean enable) {
        mEnable = enable;
    }

    private void smoothWindStep(float xStep, float yStep) {
        mRenderer.smoothWindStep(xStep, yStep);
    }

    public void setWindStepInfo(float xStep, float yStep) {
        mRenderer.setWindStepInfo(xStep, 100 - yStep);
    }

    public float[] getWindStepInfo() {
        float[] finalInfo = mRenderer.getWindStepInfo();
        finalInfo[1] = 100 - finalInfo[1];
        Log.d(TAG, "getWindStepInfo finalInfo: " + Arrays.toString(finalInfo));
        return finalInfo;
    }

    public void openWind(boolean open) {
        this.open = open;
        if (mRenderer != null) {
            mRenderer.openRender(open);
        }
    }

    private WindRenderListener windRenderListener = new WindRenderListener() {
        @Override
        public void onGestureCallBack(float xStep, float yStep) {
            Log.d(TAG, "onGestureCallBack() called with: xStep = [" + xStep + "], yStep = [" + (100 - yStep) + "]");
            if (gestureCallBack != null) {
                gestureCallBack.onGestureCallBack(xStep, 100 - yStep);
            }
        }
    };

    public interface GestureCallBack {
        void onGestureCallBack(float xStep, float yStep);

        void onDoubleClick();
    }

    private void setSwing(boolean swing) {
        mRenderer.setSwing(swing);
    }
}