package com.example.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.example.contagiapp.utente.ProfiloActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    //per aggiungere menu sulla actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment SettingFragment= new SettingFragment();
        if(id == R.id.nav_setting){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, SettingFragment).commit();
        }

        if(id == R.id.nav_account){
            Intent profiloIntent = new Intent(this, ProfiloActivity.class);
            startActivity(profiloIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //Per ottenere la barra di navigazione in basso
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {

                    Fragment fragment=null;

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
            };


}
