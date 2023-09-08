package com.app.opengldemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.autolink.airwind.WindView;

public class MainActivity extends Activity {
    WindView right, left, middleRight, middleLeft;
    RelativeLayout rl_whole;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_main);
        setWindowConfig();
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        middleLeft = findViewById(R.id.middle_left);
        middleRight = findViewById(R.id.middle_right);
        rl_whole = findViewById(R.id.rl_whole);
    }

    protected void setWindowConfig() {
        /*SubClass implement*/
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        left.onResume();
        right.onResume();
        middleLeft.onResume();
        middleRight.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        left.onPause();
        right.onPause();
        middleLeft.onPause();
        middleRight.onPause();
    }

    int level = 0;

    public void windSpeed(View v) {
        if (level < 7) {
            level++;
        } else {
            level = 0;
        }
        left.setWindLevel(level);
    }

    int mode = 0;

    public void windbai(View v) {
        if (mode == 0) {
            mode = 1;
        } else {
            mode = 0;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                Log.d("shao", "Night mode is active, we're using day theme");
                Resources res1 = getResources();
                int id = res1.getIdentifier("bg", "mipmap", getPackageName());
                Log.d("shao", "onConfigurationChanged() called with: id = [" + id + "]");
                rl_whole.setBackgroundResource(id);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                Log.d("shao", "Night mode is active, we're using dark theme");
                Resources res = getResources();
                int id1 = res.getIdentifier("bg_night", "mipmap", getPackageName());
                Log.d("shao", "onConfigurationChanged() called with: id1 = [" + id1 + "]");
                rl_whole.setBackgroundResource(id1);
                break;
        }

    }
}
