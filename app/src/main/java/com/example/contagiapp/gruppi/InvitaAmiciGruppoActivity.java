package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InvitaAmiciGruppoActivity extends AppCompatActivity {

    private Button btnCreaGruppo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference gruppoCollection = db.collection("Gruppo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_amici_gruppo);

        btnCreaGruppo = findViewById(R.id.btnCreaGruppo);



    }

    public void addGroupToDb(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nomeGruppo = extras.getString("nomeGruppo");
            String descrGruppo = extras.getString("descrGruppo");

            Gruppo gruppo = new Gruppo();
            gruppo.setNomeGruppo(nomeGruppo);
            gruppo.setDescrizione(descrGruppo);
            gruppoCollection.add(gruppo);
        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_LONG).show();





    }
}