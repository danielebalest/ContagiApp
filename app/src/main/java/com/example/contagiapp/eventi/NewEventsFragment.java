package com.example.contagiapp.eventi;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewEventsFragment extends AppCompatActivity {

    Button creaEvento;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);

        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
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

    private void openMainActivity() {
        Map<String, Object> evento = new HashMap<>();

        TextView nome = (TextView) findViewById(R.id.textNameEvent);
        evento.put("nome", nome.getText().toString());

        TextView numeroP = (TextView) findViewById(R.id.editTextNumber);
        evento.put("num_partecipanti", numeroP.getText().toString());

        //data da vedere dalla registrazione
//TODO inserire i dati dell'utente che crea l'evento
        TextView descrizione = (TextView) findViewById(R.id.editTextTextMultiLine);
        evento.put("descrizione", descrizione.getText().toString());

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        evento.put("nazione", nazione.getSelectedItem().toString());

        TextView citta = (TextView) findViewById(R.id.editCittaResidenza);
        evento.put("citta", citta.getText().toString());

        db.collection("Eventi").add(evento);
        Toast.makeText(this, "Evento aggiunto", Toast.LENGTH_SHORT).show();

        //Tornare indietro
        this.finish();

    }






};
