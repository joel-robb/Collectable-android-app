package com.example.collectable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    //variables
    public final int splashDelay = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //no night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //create and set the timer
        Timer RunSplash = new Timer();

        //give timer task
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                //close splash screen


                //launch second screen
                //Intent Service --> create an object of second screen
                Intent intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);

                finish();
            }
        };
        //Start Timer
        RunSplash.schedule(ShowSplash, splashDelay);
    }
}