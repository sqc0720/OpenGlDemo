package com.app.opengldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mylibrary.WindView;

public class DrawAnimation extends Activity {
    WindView myView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_main);
        myView = findViewById(R.id.my_gl);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        myView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.onPause();
    }

    int level = 0;

    public void windSpeed(View v) {
        if (level < 7) {
            level++;
        } else {
            level = 0;
        }
        myView.setWindLevel(level);
    }

    int mode = 0;

    public void windbai(View v) {
        if (mode == 0) {
            mode = 1;
        } else {
            mode = 0;
        }
        myView.setMode(mode);

    }

}
