package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

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
        String mailAdmin = getMailAdmin();


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nomeGruppo = extras.getString("nomeGruppo");
            String descrGruppo = extras.getString("descrGruppo");

            Gruppo gruppo = new Gruppo();
            gruppo.setAdmin(mailAdmin);
            gruppo.setNomeGruppo(nomeGruppo);
            gruppo.setDescrizione(descrGruppo);
            gruppoCollection.add(gruppo);
        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_LONG).show();


    }

    private String getMailAdmin(){
        Utente utente;
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailAdmin;
        //TODO capire il funzionamento
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailAdmin = utente.getMail();
            Log.d("mail", mailAdmin);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
            mailAdmin = prefs1.getString("mail", "no");
            Log.d("mail", mailAdmin);
        }
        return mailAdmin;
    }
}