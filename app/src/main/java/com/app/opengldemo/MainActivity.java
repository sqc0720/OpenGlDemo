package com.app.opengldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.autolink.airwind.WindView;

public class MainActivity extends Activity {
    WindView myView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_main);
        setWindowConfig();
        myView = findViewById(R.id.left);
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
        myView.setSwing(mode==1);

    }

}
