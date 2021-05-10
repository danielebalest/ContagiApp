package com.example.contagiapp.data.amici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
* questa activity permette la visualizzazione del profilo dell'utente selezionato dalla ListView di addFriends
* */

public class ProfiloUtentiActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgUtenti";
    TextView textViewNomeCognome;
    TextView textViewCitta;
    TextView textViewAge;
    ImageView imageViewProfiloUtente;
    MaterialButton btnRichiesta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utenti);

        textViewNomeCognome = findViewById(R.id.textViewNomeCognome);
        textViewCitta = findViewById(R.id.textViewCitta);
        textViewAge = findViewById(R.id.textViewAge);
        imageViewProfiloUtente = findViewById(R.id.imageViewProfiloUtente);
        btnRichiesta = findViewById(R.id.btnRichiesta);

        final Bundle extras = getIntent().getExtras();
        if(extras.getString("id") != null) {
            final String idUtenteSelezionato = extras.getString("id");
            final String amico = extras.getString("amico");
            Log.d("idUtenteSelezionato:", String.valueOf(idUtenteSelezionato));

            db.collection("Utenti")
                    .document(idUtenteSelezionato)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                final Utente user = documentSnapshot.toObject(Utente.class);
                                String nome = user.getNome();
                                String cognome = user.getCognome();
                                String citta = user.getCitta();
                                int age = user.getAge();
                                textViewNomeCognome.setText(nome + " " + cognome);
                                textViewCitta.setText(citta);
                                textViewAge.setText(String.valueOf(age));
                                caricaImgDaStorage(storageRef, storageDirectory, idUtenteSelezionato, imageViewProfiloUtente);

                                if(amico.equals("no")) {
                                    btnRichiesta.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String profiloLoggato = getMailUtenteLoggato();

                                            user.addRichiesta(profiloLoggato);

                                            db.collection("Utenti").document(idUtenteSelezionato)
                                                    .update("richiesteRicevute", user.getRichiesteRicevute());

                                            btnRichiesta.setText("Richiesta inviata");
                                            btnRichiesta.setClickable(false);
                                        }
                                    });
                                } else {
                                    btnRichiesta.setText("Rimuovi amico");

                                    btnRichiesta.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            rimuoviAmico(idUtenteSelezionato, extras.getString("mailLoggato"));
                                            //TODO finish();
                                        }
                                    });
                                }

                                //aggiornaRichieste(idUtenteSelezionato, profiloLoggato, user);

                            } else {
                                Toast.makeText(ProfiloUtentiActivity.this, "Documents does not exist", Toast.LENGTH_SHORT);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfiloUtentiActivity.this, "Errore", Toast.LENGTH_SHORT);
                }
            });
        }
    }


    private void aggiornaRichieste(String mailDestinatario, String mailMittente, Utente utente) {

    }



    private void caricaImgDaStorage(StorageReference storageRef, String directory, String idImmagine, final ImageView imageView){
        storageRef.child(directory + "/" + idImmagine).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        //TODO capire il funzionamento
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

    private void rimuoviAmico(final String idUtenteSelezionato, final String mail) {
        db.collection("Utenti").document(mail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> am = (ArrayList<String>) documentSnapshot.get("amici");
                am.remove(idUtenteSelezionato);
                db.collection("Utenti").document(mail).update("amici", am);
            }
        });

        db.collection("Utenti").document(idUtenteSelezionato).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> am = (ArrayList<String>) documentSnapshot.get("amici");
                am.remove(mail);
                db.collection("Utenti").document(idUtenteSelezionato).update("amici", am);
            }
        });
    }

}