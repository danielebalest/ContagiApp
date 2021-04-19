package com.example.contagiapp.gruppi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.AddUserAdapter;
import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

public class InvitaAmiciGruppoActivity extends AppCompatActivity {

    private Button btnCreaGruppo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference gruppoCollection = db.collection("Gruppo");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_amici_gruppo);

        btnCreaGruppo = findViewById(R.id.btnCreaGruppo);

        final RecyclerView rvInvitaAmici = findViewById(R.id.rvInvitaAmici);
        String mailAdmin = getMailUtenteLoggato();
        db.collection("Utenti")
                .document(mailAdmin).get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        ArrayList<String> listaMail = (ArrayList<String>) document.get("amici");
                        if(listaMail.isEmpty()){
                            //tvListaMail.setText("Non hai ancora nessun amico");
                        }
                        Log.d("lista", String.valueOf(listaMail));
                        getFriends(listaMail, rvInvitaAmici);
                    }
                });
    }


    public void addGroupToDb(View view) {
        String mailAdmin = getMailUtenteLoggato();


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

    public void getFriends(ArrayList<String> listaAmici, final RecyclerView recyclerView){
        /*
        metodo che svolge le seguenti operazioni:
         1)date in input le mail degli amici ottiene, per ciascuno, i seguenti dati dal database: nome, cognome, mail
         2)crea per ognuno un nuovo tipo Utente che aggiunge ad una lista
         3) passa la lista all'adapter del recycler View che poi permetterà la visualizzazione della lista di CardView degli amici sull'app
         */
        final ArrayList<Utente> amici = new ArrayList<Utente>();
        for(int i=0; i < listaAmici.size(); i++){
            db.collection("Utenti")
                    .document(listaAmici.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Utente user = new Utente();
                            user.setNome(documentSnapshot.getString("nome"));
                            user.setCognome(documentSnapshot.getString("cognome"));
                            user.setMail(documentSnapshot.getString("mail"));
                            Log.d("Nome utente", String.valueOf(user.getNome()));


                            amici.add(user);
                            Log.d("amiciSize", String.valueOf(amici.size()));
                            AddUserAdapter adapter = new AddUserAdapter(amici);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", "errore");
                }
            });
        }
    }


}