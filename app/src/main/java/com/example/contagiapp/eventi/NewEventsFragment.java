package com.example.contagiapp.eventi;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewEventsFragment extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "NewEventsFragment";
    private Button creaEvento;
    private TextView dataEvento;
    private DatePickerDialog.OnDateSetListener dataDellEvento;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        //per la mappa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview);if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }//fine mappa

        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);

        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        //Date Picker
        dataEvento = (TextView) findViewById(R.id.dataEvento);
        dataEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        NewEventsFragment.this,
                        android.R.style.Theme_Material_InputMethod,
                        dataDellEvento,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                dialog.show();
            }
        });

        dataDellEvento = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Log.d(TAG, "onDateSet: date: " + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth + "/" + month+1 + "/" + year;
                dataEvento.setText(date);
            }
        };

    }

    private void openMainActivity() {
        Map<String, Object> evento = new HashMap<>();

        TextView nome = (TextView) findViewById(R.id.textNameEvent);
        evento.put("nome", nome.getText().toString());

        TextView numeroP = (TextView) findViewById(R.id.editTextNumber);
        evento.put("num_partecipanti", numeroP.getText().toString());

        TextView data = (TextView) findViewById(R.id.dataEvento);
        evento.put("data", data.getText().toString());

        TextView descrizione = (TextView) findViewById(R.id.editTextTextMultiLine);
        evento.put("descrizione", descrizione.getText().toString());

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        evento.put("nazione", nazione.getSelectedItem().toString());

        TextView citta = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("citta", citta.getText().toString());

        TextView luogo = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("luogo", luogo.getText().toString());

        db.collection("Eventi").add(evento);
        Toast.makeText(this, "Evento aggiunto", Toast.LENGTH_SHORT).show();

        //Tornare indietro
        this.finish();

    }//TODO vedere se la mappa funziona altrimenti toglierla

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

