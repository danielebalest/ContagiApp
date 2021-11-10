package com.batsoftware.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.batsoftware.contagiapp.amici.FriendsFragment;
import com.batsoftware.contagiapp.eventi.EventsFragment;
import com.batsoftware.contagiapp.gruppi.GroupFragment;
import com.batsoftware.contagiapp.impostazioni.SettingActivity;
import com.batsoftware.contagiapp.notifiche.Notifiche;
import com.batsoftware.contagiapp.notifiche.NotifyFragment;
import com.batsoftware.contagiapp.utente.ProfiloActivity;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String stato;
    Fragment fragment=null;
    Bundle b = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        new Notifiche(this);

        // Tiene lo schermo acceso
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        }


        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

    }


    //per aggiungere menu sulla actionBar
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final Drawable draw = menu.getItem(0).getIcon();
        draw.mutate();

        draw.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        Utente utente;
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
            stato = utente.getStato();

            if(stato.equals("rosso")) draw.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            if(stato.equals("verde")) draw.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            if(stato.equals("arancione")) draw.setColorFilter(Color.rgb(255, 165, 0), PorterDuff.Mode.SRC_IN);
            if(stato.equals("giallo")) draw.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);

            db.collection("Utenti")
                    .document(mailUtenteLoggato)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            stato = documentSnapshot.getString("stato");

                            if(stato.equals("rosso")) draw.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                            if(stato.equals("verde")) draw.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                            if(stato.equals("arancione")) draw.setColorFilter(Color.rgb(255, 165, 0), PorterDuff.Mode.SRC_IN);
                            if(stato.equals("giallo")) draw.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
                        }
                    });
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.nav_setting){
            Intent settingIntent = new Intent(this, SettingActivity.class);
            startActivity(settingIntent);
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
