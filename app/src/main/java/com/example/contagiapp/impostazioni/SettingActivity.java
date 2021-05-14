package com.example.contagiapp.impostazioni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.google.android.material.button.MaterialButton;

public class SettingActivity extends AppCompatActivity {

    private MaterialButton btnSegnalaPositivita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnSegnalaPositivita = findViewById(R.id.btnSegnalaPositività);

        btnSegnalaPositivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SegnalaPositivitaActivity.class);
                startActivity(intent);
            }
        });

    }
}