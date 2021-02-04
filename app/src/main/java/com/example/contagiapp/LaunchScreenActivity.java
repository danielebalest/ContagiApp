package com.example.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.contagiapp.utente.Utente;
import com.google.gson.Gson;

public class LaunchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                String json = prefs.getString("utente", "no");

                SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
                String json1 = prefs1.getString("mail", "no");

                if(json1 == "no") {
                    SharedPreferences.Editor editor = prefs1.edit();
                    editor.clear();
                    editor.commit();
                }

                if(json != "no") {
                    Intent welcomeIntent = new Intent(LaunchScreenActivity.this, MainActivity.class);
                    startActivity(welcomeIntent);
                    finish();
                } else {
                    Intent welcomeIntent = new Intent(LaunchScreenActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                    finish();
                }
            }
        },
            1000);  //la Launch Screen rimarrà visibile per 1 secondi

    }
}
