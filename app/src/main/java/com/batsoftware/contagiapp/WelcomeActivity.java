package com.batsoftware.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.batsoftware.contagiapp.registrazione.RegistrationActivity;
import com.batsoftware.contagiapp.ui.login.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private Button loginButton;
    private Button registrationButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.NoActionBar);

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
        finish();
    }

    public void openRegistration(){
        Intent registrationIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registrationIntent);
        finish();
    }
}