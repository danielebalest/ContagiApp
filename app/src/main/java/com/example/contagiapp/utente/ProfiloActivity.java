package com.example.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ProfiloActivity extends AppCompatActivity {
    private static final int PHOTO_REQUEST_CODE = 0;
    private Button certificato;
    ListView listViewProfilo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        listViewProfilo = (ListView) findViewById(R.id.list_profilo);
        ArrayList<String> arrayListProfilo = new ArrayList<>();

        arrayListProfilo.add("Nome");
        arrayListProfilo.add("Cognome");
        arrayListProfilo.add("Email");
        arrayListProfilo.add("Data di Nascita");
        arrayListProfilo.add("Città di Nascita");
        arrayListProfilo.add("Genere");
        arrayListProfilo.add("Nazione di residenza");
        arrayListProfilo.add("Città di residenza");
        arrayListProfilo.add("Via di residenza");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrayListProfilo);
        listViewProfilo.setAdapter(arrayAdapter);




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