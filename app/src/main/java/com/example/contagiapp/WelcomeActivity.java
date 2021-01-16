package com.example.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.contagiapp.ui.login.LoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registrationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //implementazione OnClick
        loginButton = (Button) findViewById(R.id.login);
        registrationButton = (Button) findViewById(R.id.signUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistration();
            }
        });
    }

    public void openLogin(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void openRegistration(){
        Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrationIntent);

    }
}