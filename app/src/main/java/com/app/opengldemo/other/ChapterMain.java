package com.app.opengldemo.other;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class ChapterMain extends Activity {
    private GLSurfaceView mGLSurfaceView;
    private GLRender mGLRender;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ʵ����GLSurfaceView
        mGLSurfaceView = new GLSurfaceView(this);

        mGLRender = new GLRender(this);

        // ������Ⱦ��
        mGLSurfaceView.setRenderer(mGLRender);

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("zsktest", "onKeyUp->" + keyCode);
        mGLRender.onKeyUp(keyCode, event);
        return super.onKeyUp(keyCode, event);
    }
}
