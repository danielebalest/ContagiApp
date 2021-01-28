package com.example.contagiapp.registrazione;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView dataNascita;
    private Button signUpButton;
    private DatePickerDialog.OnDateSetListener dataDiNascita;
    private static final String TAG = "RegistrationActivity";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int anno = 0, mese = 0, giorno = 0;

    //Per gli errori
    private TextInputLayout nomeLayout;
    private TextInputLayout cognomeLayout;
    private TextInputLayout phoneLayout;

    private TextInputEditText nome;
    private TextInputEditText cognome;
    private TextInputEditText phone;



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


        nome = (TextInputEditText) findViewById(R.id.editTextName);
        cognome = (TextInputEditText) findViewById(R.id.editTextSurname);
        phone = (TextInputEditText) findViewById(R.id.editTextPhone) ;

        nomeLayout = (TextInputLayout) findViewById(R.id.textInputNameLayout);
        cognomeLayout = (TextInputLayout) findViewById(R.id.textInputSurnameLayout);
        phoneLayout = (TextInputLayout) findViewById(R.id.textInputPhoneLayout);

        // collegamento button registrati con la mainActivity
        signUpButton = (Button) findViewById(R.id.registrati);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                switch (controlli_TextInput(nome, nomeLayout, cognome, cognomeLayout, phone, phoneLayout)){

                    case 1:
                        nomeLayout.setError("Inserisci nome");
                        cognomeLayout.setError(null);
                        phoneLayout.setError(null);
                        break;
                    case 2:
                        cognomeLayout.setError("Inserisci cognome");
                        nomeLayout.setError(null);
                        phoneLayout.setError(null);
                        break;
                    case 3:
                        phoneLayout.setError("Inserisci cellulare");
                        nomeLayout.setError(null);
                        cognomeLayout.setError(null);
                        break;

                    default:
                        addToDb();
                        break;
                }
                //per togliere i controlli togliere il commento alla riga successiva
                //addToDb();

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
                        android.R.style.Theme_Material_InputMethod,
                        dataDiNascita,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
                dialog.show();
            }
        });

        dataDiNascita = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String date = null;
                Log.d(TAG, "onDateSet: date: " + dayOfMonth + "/" + month + "/" + year);
                if(month<=9) {
                     date = dayOfMonth + "/0" + month + "/" + year;
                }else
                    date = dayOfMonth + "/" + month + "/" + year;

                dataNascita.setText(date);
            }
        };
    }

    private int controlli_TextInput(TextInputEditText name, TextInputLayout nomeLayout, TextInputEditText surname, TextInputLayout surnameLayout, TextInputEditText phone, TextInputLayout phoneLayout){

        if(isEmpty(name) == true){
            return 1;
        }

        if(isEmpty(surname) == true){
            return 2;
        }

        if(isEmpty(phone) == true){
            return 3;
        }


        return 0;
    }

    private boolean isEmpty(TextInputEditText etText) {
        if(etText.getText().toString().length() > 0)
            return false;
        return true;
    }



    public void openMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }


    public void addToDb() {
        // Create a new user with a first, middle, and last name
        final Map<String, Object> user = new HashMap<>();

        TextInputEditText name = (TextInputEditText) findViewById(R.id.editTextName);
        String nome = name.getText().toString();
        user.put("nome", nome);

        TextInputEditText surname = (TextInputEditText) findViewById(R.id.editTextSurname);
        String cognome = surname.getText().toString();
        user.put("cognome", cognome);


        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        int Idselezionato = radiogroup.getCheckedRadioButtonId();
        RadioButton radiosex = (RadioButton) findViewById(Idselezionato);
        user.put("genere", radiosex.getText().toString());

        TextView date = (TextView) findViewById(R.id.dataNascita);
        final String appoggio = date.getText().toString();

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        user.put("nazione", nazione.getSelectedItem().toString());

        Spinner regione = (Spinner) findViewById(R.id.spinnerRegione);
        user.put("regione", regione.getSelectedItem().toString());

        Spinner provincia = (Spinner) findViewById(R.id.spinnerProvince);
        user.put("province", provincia.getSelectedItem().toString());

        Spinner citta = (Spinner) findViewById(R.id.spinnerCitta);
        user.put("citta", citta.getSelectedItem().toString());

        EditText telefono = (EditText) findViewById(R.id.editTextPhone);
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

                        controlli(querySnapshots.isEmpty(), user, email, psw1, psw2, appoggio);

                    }
                });
    }

    //Spinner per nazioni
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    //per spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void controlli(boolean cond, Map<String, Object> user1, String email, String psw1, String psw2,String appoggio) {
        Calendar cal = Calendar.getInstance();
        int l = appoggio.length();
        boolean conddata= false;
        boolean condemail= false;
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

        if (anno <= (cal.get(Calendar.YEAR) - 14)) {
            if ((mese-1) <= cal.get(Calendar.MONTH)) {
                if (giorno <= cal.get(Calendar.DAY_OF_MONTH))
                    user1.put("dataNascita", appoggio);
                else conddata= true;
            }else conddata= true;
        }else conddata= true;


        if(!email.isEmpty()){
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            boolean matchFound = m.matches();

            String  expressionPlus="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pPlus = Pattern.compile(expressionPlus, Pattern.CASE_INSENSITIVE);
            Matcher mPlus = pPlus.matcher(email);
            boolean matchFoundPlus = mPlus.matches();
            condemail=(matchFound && matchFoundPlus);
        }
        if(!condemail) {
            Toast.makeText(this, "formato email non valido", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }else
            if(conddata && condemail)
        {
            Toast.makeText(this, "bisogna avere almeno 14 anni per iscriversi", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        } else {
            if (!(psw1.equals(psw2))) {
                Toast.makeText(this, "Le password coincidono", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            } else {
                if (cond) {
                    user1.put("password", psw1);
                    user1.put("mail", email);
                    db.collection("Utenti").add(user1);
                    addToDb();
                } else {
                    Toast.makeText(this, "Mail già esistente", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }
        }
    }
}
