package com.example.contagiapp.data.amici;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/*
* questa activity permette la visualizzazione del profilo dell'utente selezionato dalla ListView di addFriends
* */

public class ProfiloUtentiActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView textViewNomeCognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utenti);

        textViewNomeCognome = findViewById(R.id.textViewNomeCognome);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String idUtenteSelezionato = extras.getString("id");
            Toast.makeText(getApplicationContext(), "Profilo Utente: " + idUtenteSelezionato, Toast.LENGTH_LONG).show();

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

                                textViewNomeCognome.setText(nome + " " + cognome);
                            } else {
                                Toast.makeText(ProfiloUtentiActivity.this, "Documents does not exist", Toast.LENGTH_SHORT);
                            }
                        }
                    });

        }


    }
}