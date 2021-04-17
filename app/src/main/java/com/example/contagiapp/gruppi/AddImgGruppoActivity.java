package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.contagiapp.R;

public class AddImgGruppoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_img_gruppo);
    }



    public void invitaAmici(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nomeGruppo = extras.getString("nomeGruppo");
            String descrGruppo = extras.getString("descrGruppo");

            Intent invitaIntent = new Intent(this, InvitaAmiciGruppoActivity.class);
            invitaIntent.putExtra("nomeGruppo", nomeGruppo);
            invitaIntent.putExtra("descrGruppo", descrGruppo);
            startActivity(invitaIntent);

        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_LONG).show();

    }


    public void selectImg(View view) {

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");

        startActivity(pickIntent);
    }
}