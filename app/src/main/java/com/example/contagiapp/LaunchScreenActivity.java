package com.example.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class LaunchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                /*per andare alla welcomeActivity*/
                Intent welcomeIntent = new Intent(LaunchScreenActivity.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
        },
            1000);  //la Launch Screen rimarrà visibile per 1 secondi

    }
}
