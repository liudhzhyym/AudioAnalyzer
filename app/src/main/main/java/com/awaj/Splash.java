package com.awaj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by keshavdulal on 04/08/16.
 */
public class Splash extends AppCompatActivity {
    ImageView splashIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        splashIcon = (ImageView) findViewById(R.id.splashIcon);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, Home.class));
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation splashAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.splash_animation);
        splashIcon.startAnimation(splashAnimation);
    }
}
