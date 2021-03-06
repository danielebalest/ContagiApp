package com.example.contagiapp.eventi;

import android.app.AuthenticationRequiredException;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.NotifyFragment;
import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.gruppi.GroupFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewEventsFragment extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;

    EditText editTextLuogo;
    TextView textViewLuogo;


    private static final String TAG = "NewEventsFragment";
    private Button creaEvento;
    private TextView dataEvento;
    private TextClock orarioEvento;
    private DatePickerDialog.OnDateSetListener dataDellEvento;
    private TimePickerDialog.OnTimeSetListener orarioDellEvento;







    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int anno = 0, mese = 0, giorno = 0, ora=0, minuti=0, oraapp=0, minapp=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        editTextLuogo = findViewById(R.id.editLuogoEvento);
        textViewLuogo = findViewById(R.id.text_viewLuogo);



        //inizializza l' SDK


        Places.initialize(getApplicationContext(), "AIzaSyDaZTesWrbtKYHmXv8grh73xk3kjMBzMT4");
        editTextLuogo.setFocusable(false);
        editTextLuogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(NewEventsFragment.this);

                startActivityForResult(intent, 100);
            }
        });





        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);

        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        //Date Picker
        orarioEvento=(TextClock) findViewById(R.id.orarioEvento);
        orarioEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog dialog;
                dialog = new TimePickerDialog(NewEventsFragment.this, android.R.style.Theme_Material_InputMethod, orarioDellEvento,hour,minute,true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                dialog.show();

            }
        });




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

        dataDellEvento = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String date = null;
                Log.d(TAG, "onDateSet: date: " + dayOfMonth + "/" + month + "/" + year);
                if(month<=9) {
                    date = dayOfMonth + "/0" + month + "/" + year;
                }else
                    date = dayOfMonth + "/" + month + "/" + year;

                dataEvento.setText(date);
            }
        };
        //orario visualizzato come cristo comanda

        orarioDellEvento = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                boolean condorario= false;
                boolean condminuto= false;

                if(hour<=9){
                    condorario=true;
                }
                if(minute<=9){
                    condminuto=true;
                }

                String time=null;
                Log.d(TAG,"onTimeSet: time: " +hour + ":" + minute);
                if(condorario && condminuto){
                    time= "0" + hour + ":0" + minute;
                }else if (condorario){
                    time= "0"+ hour + ":" + minute;
                }else if(condminuto){
                    time= + hour + ":0" + minute;
                }else time= + hour + ":" + minute;

                orarioEvento.setText(time);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            //quando ha successo
            //inizializza place
            Place place = Autocomplete.getPlaceFromIntent(data);

            editTextLuogo.setText(place.getAddress());

            textViewLuogo.setText(String.format("Nome luogo :   %s", place.getName()));
        }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openMainActivity() {
        Map<String, Object> evento = new HashMap<>();

        TextView nome = (TextView) findViewById(R.id.textNameEvent);
        evento.put("nome", nome.getText().toString());

        TextView numeroP = (TextView) findViewById(R.id.editTextNumber);
        evento.put("num_partecipanti", numeroP.getText().toString());

        TextView data = (TextView) findViewById(R.id.dataEvento);
        String appoggio= data.getText().toString();

        TextClock orario= (TextClock) findViewById(R.id.orarioEvento);
        String appoggio1=orario.getText().toString();

        TextView descrizione = (TextView) findViewById(R.id.editTextTextMultiLine);
        evento.put("descrizione", descrizione.getText().toString());

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        evento.put("nazione", nazione.getSelectedItem().toString());

        TextView citta = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("citta", citta.getText().toString());

        TextView luogo = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("luogo", luogo.getText().toString());
        controllodata(evento,appoggio,appoggio1);


        //Tornare indietro
        this.finish();

    }

    void controllodata(Map<String, Object> evento, String appoggio, String appoggio1){
        Calendar cal = Calendar.getInstance();
        boolean condevento=false;
        boolean condorario2= true;
        int l = appoggio.length();
        int l1= appoggio1.length();
        System.out.println("la lunghezza è stocazzooooo "+ l1);
        System.out.println("l'orario scelto è "+ appoggio1);
        switch (l) {
            case 9:
                anno = Integer.valueOf(appoggio.substring(l - 4, l));
                mese = Integer.valueOf(appoggio.substring(l - 7, l - 5));
                giorno = Integer.valueOf(appoggio.charAt(0)) - 48;
                break;
            case 10:
                anno = Integer.valueOf(appoggio.substring(l - 4, l));
                mese = Integer.valueOf(appoggio.substring(l - 7, l - 5));
                giorno = Integer.valueOf(appoggio.substring(l - 10, l - 8));
                break;
        }
        minapp = Integer.valueOf(appoggio1.substring(3,5));
        oraapp = Integer.valueOf(appoggio1.substring(0,2));

        System.out.println("orario scelto "+ oraapp);
        System.out.println("minuti scelti "+ minapp);
        if (anno >= cal.get(Calendar.YEAR)) {
            if ((mese-1) >= cal.get(Calendar.MONTH)) {
                if (giorno >= cal.get(Calendar.DAY_OF_MONTH))
                    if(oraapp>= (cal.get(Calendar.HOUR_OF_DAY)+1)) {
                        evento.put("data", appoggio);
                        evento.put("orario evento", appoggio1);
                        condorario2=false;
                    }
            }else condevento= true;
        }else condevento= true;

        if(condevento){
            Toast.makeText(this, "data non valida",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }else if(condorario2) {
            Toast.makeText(this, "orario non valido",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }else{
            db.collection("Eventi").add(evento);
            Toast.makeText(this, "Evento aggiunto", Toast.LENGTH_SHORT).show();
            finish();
            //provaaaaaaaa
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
};
