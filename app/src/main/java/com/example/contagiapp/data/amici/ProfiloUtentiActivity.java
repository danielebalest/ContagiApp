package com.example.contagiapp.data.amici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.squareup.picasso.Picasso;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utenti);

        textViewNomeCognome = findViewById(R.id.textViewNomeCognome);
        textViewCitta = findViewById(R.id.textViewCitta);
        textViewAge = findViewById(R.id.textViewAge);
        imageViewProfiloUtente = findViewById(R.id.imageViewProfiloUtente);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String idUtenteSelezionato = extras.getString("id");
            Log.d("idUtenteSelezionato:", String.valueOf(idUtenteSelezionato));

            db.collection("Utenti")
                    .document(idUtenteSelezionato)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                Utente user = documentSnapshot.toObject(Utente.class);
                                String nome = user.getNome();
                                String cognome = user.getCognome();
                                String citta = user.getCitta();
                                int age = user.getAge();
                                textViewNomeCognome.setText(nome + " " + cognome);
                                textViewCitta.setText(citta);
                                textViewAge.setText(String.valueOf(age));

                                caricaImgDaStorage(storageRef, storageDirectory, idUtenteSelezionato, imageViewProfiloUtente);
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

    public void inviaRichiesta(View view) {
        //Todo: implementare richieste di amicizia
        MaterialButton b = findViewById(R.id.btnRichiesta);

    }
}