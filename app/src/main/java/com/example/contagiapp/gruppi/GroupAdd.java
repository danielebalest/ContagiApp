package com.example.contagiapp.gruppi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.WelcomeActivity;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupAdd extends AppCompatActivity {
    private  BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_add);

        //BOTTON NAVIGATION
        //Inizializza e assegna varibaile
        bottomNavigationView = findViewById(R.id.bottomNav);

        //imposta nav_events selezionata
        bottomNavigationView.setSelectedItemId(R.id.nav_group);


        final Intent intent = new Intent(this, WelcomeActivity.class);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                Fragment fragment = null;

                switch (menuitem.getItemId())
                {
                    case R.id.nav_group:
                        startActivity(intent);
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
