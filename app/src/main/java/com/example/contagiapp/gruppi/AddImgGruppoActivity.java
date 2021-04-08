package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.contagiapp.R;

public class AddImgGruppoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_img_gruppo);
    }

    public void InvitaAmici(View view) {
    }

    public void invitaAmici(View view) {
        Intent invitaIntent = new Intent(this, InvitaAmiciGruppoActivity.class);
        startActivity(invitaIntent);
    }
}