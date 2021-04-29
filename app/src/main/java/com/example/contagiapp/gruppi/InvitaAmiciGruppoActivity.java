package com.example.contagiapp.gruppi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.contagiapp.AddUserAdapter;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.eventi.NewEventsActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;

public class InvitaAmiciGruppoActivity extends AppCompatActivity {

    private Button btnCreaGruppo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference gruppoCollection = db.collection("Gruppo");
    Uri imageUri;
    String documentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_amici_gruppo);

        btnCreaGruppo = findViewById(R.id.btnCreaGruppo);
        btnCreaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupToDb();
                //todo: devo tornare al fragment GroupFragment
            }
        });

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


    public void addGroupToDb() {
        String mailAdmin = getMailUtenteLoggato();


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nomeGruppo = extras.getString("nomeGruppo");
            String descrGruppo = extras.getString("descrGruppo");

            imageUri = Uri.parse(extras.getString("imageUri"));

            final Gruppo gruppo = new Gruppo();
            gruppo.setAdmin(mailAdmin);
            gruppo.setNomeGruppo(nomeGruppo);
            gruppo.setDescrizione(descrGruppo);
            gruppoCollection.add(gruppo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentId = documentReference.getId();
                    gruppo.setIdGruppo(documentId);
                    Log.d("documentId", String.valueOf(documentId));
                    Log.d("getIdGruppo", String.valueOf(gruppo.getIdGruppo()));
                    db.collection("Gruppo").document(documentId).update("idGruppo", documentId);
                    uploadImage(documentId);
                }
            });
        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_SHORT).show();

       Toast.makeText(getApplicationContext(), "Gruppo creato", Toast.LENGTH_SHORT);

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

    private  void uploadImage(String documentId){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Caricamento");
        pd.show();


        //Log.d("documentId2", documentId);
        //Log.d("uri", imageUri.toString());
        if((imageUri != null) && (documentId != null)){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("imgGruppi").child(documentId);

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("downloadUrl", url);
                            pd.dismiss();
                            Toast.makeText(InvitaAmiciGruppoActivity.this, "immagine caricata", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(InvitaAmiciGruppoActivity.this, "immagine non caricata", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(InvitaAmiciGruppoActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            Log.e("Errore", "imageUri o documentId nulli");
            Log.d("documentId2", String.valueOf(documentId));
        }

    }


}