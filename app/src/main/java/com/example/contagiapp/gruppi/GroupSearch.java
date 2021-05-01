package com.example.contagiapp.gruppi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupSearch extends AppCompatActivity {
    private  BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_profile);

        //Inizializza variabile
        bottomNavigationView = findViewById(R.id.bottomNav3);

        //imposta nav_events selezionata
        bottomNavigationView.setSelectedItemId(R.id.nav_group);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        //bottomNavigationView.setSelected();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                    Intent mainIntent = new Intent(GroupSearch.this, MainActivity.class);

                    switch (menuitem.getItemId())
                    {
                        case R.id.nav_group:
                            startActivity(mainIntent);
                            break;

                        case R.id.nav_home:
                            startActivity(mainIntent);
                            break;

                        case R.id.nav_notify:
                            startActivity(mainIntent);
                            break;

                        case R.id.nav_events:
                            startActivity(mainIntent);
                            break;

                        case R.id.nav_friends:
                            startActivity(mainIntent);
                            break;

                    }
                    //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    return true;
                }
            };

}