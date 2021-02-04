package com.example.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.WelcomeActivity;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ProfiloActivity extends AppCompatActivity {
    private static final int PHOTO_REQUEST_CODE = 0;
    private static final String TAG = "ProfiloActivity";
    private Button certificato;
    private ListView listViewProfilo;
    private Button logout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Utente utente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        listViewProfilo = (ListView) findViewById(R.id.list_profilo);
        ArrayList<String> arrayListProfilo = new ArrayList<>();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");

        //TODO verificare il controllo
        if(json != "no") {
            utente = gson.fromJson(json, Utente.class);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
            String username = prefs1.getString("mail", "no");

            db.collection("Utenti").whereEqualTo("mail", username)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            utente = document.toObject(Utente.class);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

        arrayListProfilo.add("Nome: "+utente.getNome());
        arrayListProfilo.add("Cognome: "+utente.getCognome());
        arrayListProfilo.add("Mail: "+utente.getMail());
        arrayListProfilo.add("Data di Nascita: "+utente.getDataNascita());
        arrayListProfilo.add("Genere: "+utente.getGenere());
        arrayListProfilo.add("Nazione di residenza: "+utente.getNazione());
        arrayListProfilo.add("Regione di residenza: "+utente.getRegione());
        arrayListProfilo.add("Provincia di residenza: "+utente.getProvince());
        arrayListProfilo.add("Città di residenza: "+utente.getCitta());
        arrayListProfilo.add("Telefono: "+utente.getTelefono());

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayListProfilo);
        listViewProfilo.setAdapter(arrayAdapter);

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplicationContext ().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();

                Intent welcome = new Intent(ProfiloActivity.this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            }
        });

        certificato = (Button) findViewById(R.id.certificato);
        certificato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
            }
        });
    }
}