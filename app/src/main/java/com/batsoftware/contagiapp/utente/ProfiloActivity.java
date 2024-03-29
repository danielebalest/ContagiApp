package com.batsoftware.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.WelcomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfiloActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final String TAG = "ProfiloActivity";
    private ListView listViewProfilo;
    private Button logout;
    private Button modifica;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Utente utente;
    private ImageView imgViewProfiloUtente;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgUtenti";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }

        imgViewProfiloUtente = findViewById(R.id.imgProfilo);
        listViewProfilo = (ListView) findViewById(R.id.list_profilo);
        final ArrayList<String> arrayListProfilo = new ArrayList<>();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");

        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            riempiListView(arrayListProfilo);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
            String username = prefs1.getString("mail", "no");
            Log.d("username", String.valueOf(username));

            db.collection("Utenti")
                    .document(username)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            utente = documentSnapshot.toObject(Utente.class);
                            riempiListView(arrayListProfilo);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Documento non esiste");
                }
            });
        }

        modifica = findViewById(R.id.modifica_dati);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mod = new Intent(ProfiloActivity.this, ModificaUtenteActivity.class);
                startActivity(mod);
                finish();
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplicationContext ().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent welcome = new Intent(ProfiloActivity.this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            }
        });

    }


    private void riempiListView(ArrayList<String> arrayListProfilo){
        String stato = null;
        if(utente.getStato().equals("rosso")) stato=getString(R.string.positive);
        if(utente.getStato().equals("verde")) stato=getString(R.string.negative);
        if(utente.getStato().equals("arancione")) stato=getString(R.string.contact_with_a_positive);
        if(utente.getStato().equals("giallo")) stato=getString(R.string.uncertain);

        arrayListProfilo.add(getString(R.string.state2dots) +stato);
        arrayListProfilo.add(getString(R.string.name2dots)+utente.getNome());
        arrayListProfilo.add(getString(R.string.surname2dots)+utente.getCognome());
        arrayListProfilo.add(getString(R.string.mail2dots)+utente.getMail());
        arrayListProfilo.add(getString(R.string.date_of_birth2dots)+utente.getDataNascita());
        arrayListProfilo.add(getString(R.string.gender2dots)+utente.getGenere());
        arrayListProfilo.add(getString(R.string.region_of_residence2dots)+utente.getRegione());
        arrayListProfilo.add(getString(R.string.province_of_residence2dots)+utente.getProvince());
        arrayListProfilo.add(getString(R.string.city_of_residence2dots)+utente.getCitta());
        arrayListProfilo.add(getString(R.string.phone2dots)+utente.getTelefono());

        caricaImgDaStorage(storageRef, storageDirectory, utente.getMailPath(), imgViewProfiloUtente);
        Log.d("arrayListProfilo", String.valueOf(arrayListProfilo));
        ArrayAdapter arrayAdapter = new ArrayAdapter(ProfiloActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayListProfilo);
        listViewProfilo.setAdapter(arrayAdapter);
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

}