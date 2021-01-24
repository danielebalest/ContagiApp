package com.example.contagiapp.registrazione;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView dataNascita;
    private Button signInButton;
    private DatePickerDialog.OnDateSetListener dataDiNascita;
    private static final String TAG = "RegistrationActivity";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        //Spinner per nazioni
        Spinner spinnerNazioni = findViewById(R.id.spinnerNazioni);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.nazioni, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerNazioni.setAdapter(adapter);
        spinnerNazioni.setOnItemSelectedListener(this);


        // collegamento button registrati con la mainActivity
        signInButton = (Button) findViewById(R.id.registrati);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        //Date Picker
        dataNascita = (TextView) findViewById(R.id.dataNascita);
        dataNascita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistrationActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dataDiNascita,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dataDiNascita = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Log.d(TAG, "onDateSet: date: " + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth + "/" + month+1 + "/" + year;
                dataNascita.setText(date);
            }
        };
    }

    public void openMainActivity(){
        // Create a new user with a first, middle, and last name
        final Map<String, Object> user = new HashMap<>();

        EditText name = (EditText)findViewById(R.id.editTextName);
        String nome = name.getText().toString();
        user.put("nome", nome);

        EditText surname = (EditText)findViewById(R.id.editTextSurname);
        String cognome = surname.getText().toString();
        user.put("cognome", cognome);

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        int Idselezionato = radiogroup.getCheckedRadioButtonId();
        RadioButton radiosex = (RadioButton) findViewById(Idselezionato);
        user.put("genere", radiosex.getText().toString());

        EditText date = (EditText) findViewById(R.id.dataNascita);
        user.put("dataNascita", date.getText().toString());

        Spinner nazione= (Spinner) findViewById(R.id.spinnerNazioni);
        user.put("nazione", nazione.getSelectedItem().toString());

        Spinner regione= (Spinner) findViewById(R.id.spinnerRegione);
        user.put("regione", regione.getSelectedItem().toString());

        Spinner provincia= (Spinner) findViewById(R.id.spinnerProvince);
        user.put("province", provincia.getSelectedItem().toString());

        Spinner citta= (Spinner) findViewById(R.id.spinnerCitta);
        user.put("citta", citta.getSelectedItem().toString());

        EditText telefono = (EditText)findViewById(R.id.editTextPhone);
        user.put("telefono", telefono.getText().toString());

        EditText password = (EditText) findViewById(R.id.editTextTextPassword);
        final String psw1 = password.getText().toString();

        EditText password2 = (EditText) findViewById(R.id.editTextRepeatPassword);
        final String psw2 = password2.getText().toString();

        EditText mail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        final String email = mail.getText().toString();

        db.collection("Utenti").whereEqualTo("mail", email).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshots) {
                        mail_contr(querySnapshots.isEmpty(), user, email, psw1, psw2);
                    }
                });
}
    //Spinner per nazioni
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void mail_contr(boolean cond, Map<String, Object> user1, String email, String psw1, String psw2) {
        if(!(psw1.equals(psw2))){
            Toast.makeText(this, "Le password coincidono", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        } else {
            if (cond) {
                user1.put("password", psw1);
                user1.put("mail", email);
                db.collection("Utenti").add(user1);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                Toast.makeText(this, "Mail già esistente", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        }
    }
}