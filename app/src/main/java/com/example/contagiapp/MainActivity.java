package com.example.contagiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.example.contagiapp.impostazioni.SettingActivity;
import com.example.contagiapp.notifiche.NotifyFragment;
import com.example.contagiapp.utente.ProfiloActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String stato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkMyPermission();
        checkMyBL();

        // Tiene lo schermo acceso
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Toast.makeText(this,"schifoso il signore",Toast.LENGTH_SHORT).show();
        }

         */

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });

    }


    //per bluetooth
    private void checkMyBL(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},PERMISSION_CODE
                        );
            }
        }

    }

    //per gps
    private void checkMyPermission(){
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Toast.makeText(MainActivity.this, "Permesso gps concesso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    //per aggiungere menu sulla actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");
        Utente utente;

        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            stato = utente.getStato();
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
                            stato = documentSnapshot.getString("stato");
                        }
                    });
        }

        Drawable draw = menu.getItem(0).getIcon();
        draw.mutate();

        draw.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        if(stato.equals("rosso")) draw.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        if(stato.equals("verde")) draw.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        if(stato.equals("arancione")) draw.setColorFilter(Color.rgb(255, 165, 0), PorterDuff.Mode.SRC_IN);
        if(stato.equals("giallo")) draw.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.nav_setting){
            //getSupportFragmentManager().beginTransaction().replace(R.id.container, SettingFragment).commit();
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
