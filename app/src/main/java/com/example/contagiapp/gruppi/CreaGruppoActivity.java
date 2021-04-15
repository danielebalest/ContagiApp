package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.contagiapp.R;
import com.google.android.material.textfield.TextInputEditText;

public class CreaGruppoActivity extends AppCompatActivity {

    TextInputEditText editTextNomeGruppo;
    TextInputEditText editTextDescrGruppo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);
    }

    public void addImgGruoup(View view) {
        editTextNomeGruppo = findViewById(R.id.editTextNomeGruppo);
        editTextDescrGruppo = findViewById(R.id.editTextDescrGruppo);
        String nomeGruppo = editTextNomeGruppo.getText().toString();
        String descrGruppo = editTextDescrGruppo.getText().toString();



        //Apro activity AddImgGruppoActivity
        Intent imgIntent = new Intent(this, AddImgGruppoActivity.class);
        imgIntent.putExtra("nomeGruppo", nomeGruppo);
        imgIntent.putExtra("descrGruppo", descrGruppo);
        startActivity(imgIntent);

    }


}