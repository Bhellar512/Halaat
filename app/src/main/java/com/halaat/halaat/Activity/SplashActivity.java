package com.halaat.halaat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import com.halaat.halaat.R;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {

    final int TIME_INTERVAL_TO_CHECK = 500;// in millis
    final int MIN_TIME_INTERVAL_FOR_SPLASH = 2500; // in millis
    boolean workComplete = false;
    Timer checkWorkTimer;
    Runnable backgroundWork = new Runnable() {

        @Override
        public void run() {

            // This area can be used in Splash to do tasks that do not delay
            // Splash as well as do not extend its time if there processing time
            // is less than splash
            // Check Internet Connection and meet necessary conditions
            // to start the app. E.g. Disk Checks.
            // Check preferences and availability of certain data.
            workComplete = true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    public void onResume() {
        super.onResume();

        launchTimerAndTask();
    }

    private void launchTimerAndTask() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showMainActivity();
            }
        }, MIN_TIME_INTERVAL_FOR_SPLASH);
        // Launch timer to test image changing and background threads work
       /* checkWorkTimer = new Timer();
        checkWorkTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (workComplete) {
                    initNextActivity();
                }
            }

        }, MIN_TIME_INTERVAL_FOR_SPLASH, TIME_INTERVAL_TO_CHECK);

        new Thread(backgroundWork).start();*/
    }

    private void initNextActivity() {
        checkWorkTimer.cancel();
        showMainActivity();

    }

    private void showMainActivity() {
        Intent i = new Intent(this, SignIn.class);
        startActivity(i);
        finish();
    }
}