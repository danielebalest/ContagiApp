package com.example.contagiapp.gruppi;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_profile);

        //Inizializza e assegna varibaile
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        //imposta nav_events selezionata
        bottomNavigationView.setSelectedItemId(R.id.nav_group);

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
