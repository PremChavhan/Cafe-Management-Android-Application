package com.android.eshoppingmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class activity_splash extends AppCompatActivity {


    private static int splashTimeOut=5000;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView=findViewById(R.id.splashimage);
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(activity_splash.this, login_activity.class);
                startActivity(i);
                finish();
            }
        }, 3000);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        imageView.startAnimation(myanim);
    }
}
