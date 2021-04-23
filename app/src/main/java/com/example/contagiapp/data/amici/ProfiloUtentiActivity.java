package com.example.contagiapp.data.amici;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
* questa activity permette la visualizzazione del profilo dell'utente selezionato dalla ListView di addFriends
* */

public class ProfiloUtentiActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView textViewNomeCognome;
    TextView textViewCitta;
    TextView textViewAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utenti);

        textViewNomeCognome = findViewById(R.id.textViewNomeCognome);
        textViewCitta = findViewById(R.id.textViewCitta);
        textViewAge = findViewById(R.id.textViewAge);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String idUtenteSelezionato = extras.getString("id");
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
                            } else {
                                Toast.makeText(ProfiloUtentiActivity.this, "Documents does not exist", Toast.LENGTH_SHORT);
                            }
                        }
                    }); //TODO: onFailure



        }



    }



    public void inviaRichiesta(View view) {
        //Todo: implementare richieste di amicizia
        MaterialButton b = findViewById(R.id.btnRichiesta);

    }
}