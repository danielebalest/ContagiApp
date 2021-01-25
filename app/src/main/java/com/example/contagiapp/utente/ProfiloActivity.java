package com.example.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfiloActivity extends AppCompatActivity {
    private static final int PHOTO_REQUEST_CODE = 0;
    private Button certificato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);

        certificato = (Button) findViewById(R.id.certificato);
        certificato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
            }
        });

        //Inizializza e assegna varibaile
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //imposta nav_events selezionata
        bottomNavigationView.setSelectedItemId(R.id.nav_events);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                Fragment fragment = null;
                switch (menuitem.getItemId())
                {
                    case R.id.nav_group:
                        fragment = new GroupFragment();
                        break;

                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.nav_notify:
                        fragment = new NotifyFragment();
                        break;

                    case R.id.nav_events:
                        fragment = new EventsFragment();
                        break;

                    case R.id.nav_friends:
                        fragment = new FriendsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                return true;
            }
        });

    }
}