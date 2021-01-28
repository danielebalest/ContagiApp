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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewEventsFragment extends AppCompatActivity {

    private static final String TAG = "NewEventsFragment";
    private Button creaEvento;
    private TextView dataEvento;
    private DatePickerDialog.OnDateSetListener dataDellEvento;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int anno = 0, mese = 0, giorno = 0;

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

    }

    private void openMainActivity() {
        Map<String, Object> evento = new HashMap<>();

        TextView nome = (TextView) findViewById(R.id.textNameEvent);
        evento.put("nome", nome.getText().toString());

        TextView numeroP = (TextView) findViewById(R.id.editTextNumber);
        evento.put("num_partecipanti", numeroP.getText().toString());

        TextView data = (TextView) findViewById(R.id.dataEvento);
        String appoggio= data.getText().toString();

        TextView descrizione = (TextView) findViewById(R.id.editTextTextMultiLine);
        evento.put("descrizione", descrizione.getText().toString());

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        evento.put("nazione", nazione.getSelectedItem().toString());

        TextView citta = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("citta", citta.getText().toString());

        TextView luogo = (TextView) findViewById(R.id.editCittaEvento);
        evento.put("luogo", luogo.getText().toString());
        controllodata(evento,appoggio);


        //Tornare indietro
        this.finish();

    }

    void controllodata(Map<String, Object> evento, String appoggio){
        Calendar cal = Calendar.getInstance();
        boolean condevento=false;
        int l = appoggio.length();
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
        if (anno >= cal.get(Calendar.YEAR)) {
            if ((mese-1) >= cal.get(Calendar.MONTH)) {
                if (giorno >= cal.get(Calendar.DAY_OF_MONTH))
                    evento.put("data", appoggio);
                else condevento= true;
            }else condevento= true;
        }else condevento= true;

        if(condevento){
            Toast.makeText(this, "data non valida",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }else {
            db.collection("Eventi").add(evento);
            Toast.makeText(this, "Evento aggiunto", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
};
