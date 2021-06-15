package com.example.contagiapp.impostazioni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.gruppi.Gruppo;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private MaterialButton btnSegnalaPositivita;
    private MaterialButton btnSegnalaNegativita;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        btnSegnalaPositivita = findViewById(R.id.btnSegnalaPositività);
        btnSegnalaNegativita = findViewById(R.id.btnSegnalaNegatività);

        db.collection("Utenti").
                document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Utente utente = documentSnapshot.toObject(Utente.class);

                String id = documentSnapshot.getId();

                String dataPositività = utente.getDataPositivita();


                try {
                    Date dataPositività1=new SimpleDateFormat("dd/MM/yyyy").parse(dataPositività);
                    Log.d("date1", String.valueOf(dataPositività1));


                    Date dataAttuale = new Date(System.currentTimeMillis());
                    Log.d("date", String.valueOf(dataAttuale));

                    dataPositività1.getMonth();
                    dataAttuale.getMonth();
                    int differenzaDiData = Math.abs(dataPositività1.getDate() - dataAttuale.getDate()); //todo: da sistemare se i mesi sono diversi
                    Log.d("differenzaDiData", String.valueOf(differenzaDiData));

                    if(differenzaDiData < 10){
                        btnSegnalaNegativita.setClickable(false);
                        btnSegnalaNegativita.setVisibility(View.INVISIBLE);
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }




            }
        });


        btnSegnalaPositivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SegnalaPositivitaActivity.class);
                startActivity(intent);
            }
        });

        btnSegnalaNegativita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SegnalaNegativita.class);
                startActivity(intent);
            }
        });

    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }
}