package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.contagiapp.R;

public class CreaGruppoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);
    }

    public void AddImgGruoup(View view) {
        Intent imgIntent = new Intent(this, AddImgGruppoActivity.class);
        startActivity(imgIntent);
    }
}