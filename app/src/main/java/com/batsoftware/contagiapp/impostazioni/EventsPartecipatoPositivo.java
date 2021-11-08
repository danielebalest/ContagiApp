package com.batsoftware.contagiapp.impostazioni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.batsoftware.contagiapp.MainActivity;
import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.eventi.Evento;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsPartecipatoPositivo extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "EventsPartecipatoP";
    private String dataRosso;
    private Button btnFine;
    private EventiPartecipatoAdapter adapter;
    private TextView noEventi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi_partecipato_positivo);

        btnFine = findViewById(R.id.btnFine);
        noEventi = findViewById(R.id.textView4);

        final List<Evento> eventi = new ArrayList<>();
        final RecyclerView recyclerView =  findViewById(R.id.rvRichiesteEventi);

        db.collection("Eventi")
                .whereArrayContains("partecipanti", getMailUtenteLoggato())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Evento ev = document.toObject(Evento.class);

                                //se l'evento è accaduto 10 giorni fa
                                try {
                                    Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(ev.getData());
                                    Intent intent = getIntent();
                                    dataRosso = intent.getStringExtra("dataRosso");
                                    Date dataRosso2 = new SimpleDateFormat("dd/MM/yyyy").parse(dataRosso);

                                    //864000000 millisecondi = 10 giorni
                                    if(dataRosso2.getTime() - dataEvento.getTime() <= 864000000) {
                                        eventi.add(ev);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(eventi.isEmpty()) noEventi.setVisibility(View.VISIBLE);
                            //if(eventi.isEmpty()) finish();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Utenti").document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            final Utente utenteLoggato = documentSnapshot.toObject(Utente.class);
                            adapter = new EventiPartecipatoAdapter(getApplicationContext(), eventi, getMailUtenteLoggato(), utenteLoggato, dataRosso);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(EventsPartecipatoPositivo.this));
                        }else {
                            Toast.makeText(EventsPartecipatoPositivo.this, "Documents does not exist", Toast.LENGTH_SHORT);
                        }
                    }
                });

        btnFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!eventi.isEmpty()) {
                    if(adapter.getCond().contains(false)) {
                        Toast.makeText(EventsPartecipatoPositivo.this, getText(R.string.complete_event_report), Toast.LENGTH_LONG).show();
                    } else home();
                } else home();
            }
        });
    }


    private void home() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }
}