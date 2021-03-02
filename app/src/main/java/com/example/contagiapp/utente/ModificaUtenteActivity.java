package com.example.contagiapp.utente;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModificaUtenteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextView dataNascita;
    private Button modifica;
    private DatePickerDialog.OnDateSetListener dataDiNascita;
    private static final String TAG = "RegistrationActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int anno = 0, mese = 0, giorno = 0;

    //Per gli errori
    private TextInputLayout nomeLayout;
    private TextInputLayout cognomeLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout mailLayout;
    private TextInputLayout dataLayout;
    private TextInputLayout psw1Layout;
    private TextInputLayout psw2Layout;

    private TextInputEditText nome;
    private TextInputEditText cognome;
    private TextInputEditText phone;
    private TextInputEditText mail;
    private TextInputEditText data;
    private TextInputEditText psw1;
    private TextInputEditText psw2;

    private Utente utente = new Utente();


    //TODO fare le cose scritte sotto:
    //rivedere il fatto di utilizzare le regioni e province (con spinner) quindi se toglierli e mettere solo spinner nazioni e una textbox per la città
    //ricontrollare tutto il codice di questa classe
    //aggiungere una viewbox sopra il sesso



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_dati_utente);

        //Spinner per nazioni
        Spinner spinnerNazioni = findViewById(R.id.spinnerNazioni);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.nazioni, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerNazioni.setAdapter(adapter);
        spinnerNazioni.setOnItemSelectedListener(this);

        Spinner regione = (Spinner) findViewById(R.id.spinnerRegione);
        Spinner provincia = (Spinner) findViewById(R.id.spinnerProvince);
        Spinner citta = (Spinner) findViewById(R.id.spinnerCitta);

        nome = (TextInputEditText) findViewById(R.id.editTextName);
        cognome = (TextInputEditText) findViewById(R.id.editTextSurname);
        phone = (TextInputEditText) findViewById(R.id.editTextPhone);
        data = (TextInputEditText) findViewById(R.id.editTextDataNascita);
        mail = (TextInputEditText) findViewById(R.id.editTextTextEmailAddress);
        psw1 = (TextInputEditText) findViewById(R.id.editTextTextPassword) ;
        psw2 = (TextInputEditText) findViewById(R.id.editTextRepeatPassword);

        nomeLayout = (TextInputLayout) findViewById(R.id.textInputNameLayout);
        cognomeLayout = (TextInputLayout) findViewById(R.id.textInputSurnameLayout);
        phoneLayout = (TextInputLayout) findViewById(R.id.textInputPhoneLayout);
        mailLayout = (TextInputLayout) findViewById(R.id.textInputEmailLayout);
        dataLayout = (TextInputLayout) findViewById(R.id.textInputBirthLayout);
        psw1Layout = (TextInputLayout) findViewById(R.id.textInputPasswordLayout);
        psw2Layout = (TextInputLayout) findViewById(R.id.textInputRepeatPasswordLayout);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");
        utente = gson.fromJson(json, Utente.class);

        SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
        String username = prefs1.getString("mail", "no");

        if(json == "no") {
            db.collection("Utenti").whereEqualTo("mail", utente.getMail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    utente = document.toObject(Utente.class);
                                }
                            }
                        }
                    });
        }

        nome.setText(utente.getNome());
        cognome.setText(utente.getCognome());
        phone.setText(utente.getTelefono());
        data.setText(utente.getDataNascita());
        mail.setText(utente.getMail());
        psw1.setText(utente.getPassword());
        psw2.setText(utente.getPassword());

        RadioButton radioM = (RadioButton) findViewById(R.id.radioButton1);
        RadioButton radioF = (RadioButton) findViewById(R.id.radioButton2);
        if(utente.getGenere().equals("M")) {
            radioM.setChecked(true);
            System.out.println(utente.getGenere() + " M");
            System.out.println(utente.getGenere());
        } else {
            System.out.println(utente.getGenere() + " F");
            System.out.println(utente.getGenere());
            radioF.setChecked(true);
        }

        int num = 0;
        switch(utente.getNazione()) {
            case "Italia":
                num = 0;
                break;
            case "Francia":
                num = 1;
                break;
            case "Spagna":
                num = 2;
                break;
            case "Germania":
                num = 3;
                break;
        }
        spinnerNazioni.setSelection(num);

        switch(utente.getRegione()) {
            case "Puglia":
                num = 0;
                break;
            case "Basilicata":
                num = 1;
                break;
            case "Campagnia":
                num = 2;
                break;
        }
        regione.setSelection(num);

        switch (utente.getProvince()) {
            case "BA":
                num = 0;
                break;
            case "BAT":
                num = 1;
                break;
            case "FG":
                num = 2;
                break;
            case "LE":
                num = 3;
                break;
            case "BR":
                num = 4;
                break;
            case "TA":
                num = 5;
                break;
        }
        provincia.setSelection(num);

        switch(utente.getCitta()) {
            case "Bari":
                num = 0;
                break;
            case "Molfetta":
                num = 1;
                break;
        }
        citta.setSelection(num);

        // collegamento button registrati con la mainActivity
        modifica = (Button) findViewById(R.id.modificaDati);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (controlli_TextInput(nome, nomeLayout, cognome, cognomeLayout, mail, mailLayout, data, dataLayout, phone, phoneLayout, psw1, psw1Layout, psw2, psw2Layout)){

                    case 1:
                        nomeLayout.setError("Inserisci nome");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci nome", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        cognomeLayout.setError("Inserisci cognome");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci cognome", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        dataLayout.setError("Inserisci data di nascita");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci data di nascita", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        mailLayout.setError("Inserisci mail");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci mail", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        phoneLayout.setError("Inserisci cellulare");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci cellulare", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        psw1Layout.setError("Inserisci password");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        psw2Layout.setError("Inserisci password");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci nome", Toast.LENGTH_SHORT).show();
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
        dataNascita = (TextView) findViewById(R.id.editTextDataNascita);
        dataNascita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ModificaUtenteActivity.this,
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

    private int controlli_TextInput(TextInputEditText name, TextInputLayout nomeLayout, TextInputEditText surname, TextInputLayout cognomeLayout, TextInputEditText mail, TextInputLayout mailLayout,
                                    TextInputEditText birth, TextInputLayout dataLayout, TextInputEditText phone, TextInputLayout phoneLayout, TextInputEditText psw1, TextInputLayout psw1Layout,
                                    TextInputEditText psw2, TextInputLayout psw2Layout){

        if(isEmpty(name) == true){
            return 1;
        }else nomeLayout.setError(null);

        if(isEmpty(surname) == true){
            return 2;
        }else cognomeLayout.setError(null);

        if(isEmpty(birth) == true){
            return 3;
        }else dataLayout.setError(null);

        if(isEmpty(mail)  == true){
            return 4;
        }else mailLayout.setError(null);

        if(isEmpty(phone) == true){
            return 5;
        }else phoneLayout.setError(null);

        if(isEmpty(psw1)  == true){
            return 6;
        }else psw1Layout.setError(null);

        if(isEmpty(psw2)  == true){
            return 7;
        }else psw2Layout.setError(null);

        return 0;
    }

    private boolean isEmpty(TextInputEditText etText) {
        if(etText.getText().toString().length() > 0)
            return false;
        return true;
    }

    public void openMainActivity(){
        Intent mainIntent = new Intent(this, ProfiloActivity.class);
        startActivity(mainIntent);
        finish();
    }

    public void addToDb() {
        // Create a new user with a first, middle, and last name
        final Map<String, Object> user = new HashMap<>();

        TextInputEditText name = (TextInputEditText) findViewById(R.id.editTextName);
        String nome = name.getText().toString();
        user.put("nome", nome);
        utente.setNome(nome);

        TextInputEditText surname = (TextInputEditText) findViewById(R.id.editTextSurname);
        String cognome = surname.getText().toString();
        user.put("cognome", cognome);
        utente.setCognome(cognome);

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        int Idselezionato = radiogroup.getCheckedRadioButtonId();
        RadioButton radiosex = (RadioButton) findViewById(Idselezionato);
        user.put("genere", radiosex.getText().toString());
        utente.setGenere(radiosex.getText().toString());

        TextView date = (TextView) findViewById(R.id.editTextDataNascita);
        final String appoggio = date.getText().toString();

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        user.put("nazione", nazione.getSelectedItem().toString());
        utente.setNazione(nazione.getSelectedItem().toString());

        Spinner regione = (Spinner) findViewById(R.id.spinnerRegione);
        user.put("regione", regione.getSelectedItem().toString());
        utente.setRegione(regione.getSelectedItem().toString());

        Spinner provincia = (Spinner) findViewById(R.id.spinnerProvince);
        user.put("province", provincia.getSelectedItem().toString());
        utente.setProvince(provincia.getSelectedItem().toString());

        Spinner citta = (Spinner) findViewById(R.id.spinnerCitta);
        user.put("citta", citta.getSelectedItem().toString());
        utente.setCitta(citta.getSelectedItem().toString());

        EditText telefono = (EditText) findViewById(R.id.editTextPhone);
        user.put("telefono", telefono.getText().toString());
        utente.setTelefono(telefono.getText().toString());

        EditText password = (EditText) findViewById(R.id.editTextTextPassword);
        final String psw1 = password.getText().toString();

        EditText password2 = (EditText) findViewById(R.id.editTextRepeatPassword);
        final String psw2 = password2.getText().toString();

        EditText mail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        final String email = mail.getText().toString();

        db.collection("Utenti").whereEqualTo("mail", email).whereNotIn("telefono", Collections.singletonList(telefono.getText().toString())).get()
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

    private void controlli(boolean cond, Map<String, Object> user1, String email, String psw1, String psw2, String appoggio) {
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

        if (anno == (cal.get(Calendar.YEAR) - 14)) {
            if(mese == cal.get(Calendar.MONTH)) {
                if (giorno > cal.get(Calendar.DAY_OF_MONTH)) conddata = true;
            } else if(mese > cal.get(Calendar.DAY_OF_MONTH)) conddata = true;
        } else if(anno > (cal.get(Calendar.YEAR) - 14)) conddata = true;

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
            mailLayout.setError("Formato email non valido");
        }else

        if(conddata && condemail)
        {
            Toast.makeText(this, "Bisogna avere almeno 14 anni per iscriversi", Toast.LENGTH_SHORT).show();
            dataLayout.setError("Bisogna avere almeno 14 anni per iscriversi");
        } else {
            user1.put("dataNascita", appoggio);
            utente.setDataNascita(appoggio);

            if (!(psw1.equals(psw2))) {
                Toast.makeText(this, "Le password coincidono", Toast.LENGTH_SHORT).show();
                psw1Layout.setError("Le password coincidono");
                psw2Layout.setError("Le password coincidono");
            } else {
                if (cond) {
                    psw1Layout.setError(null);
                    psw2Layout.setError(null);
                    user1.put("password", psw1);
                    user1.put("mail", email);
                    utente.setPassword(psw1);
                    utente.setMail(email);

                    //elimina documento esiste e aggiunge quello nuovo
                    db.collection("Utenti").document(utente.getMailPath()).update(user1);

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    Gson gson = new Gson();
                    String json = prefs.getString("utente", "no");
                    if(json!="no") {
                        json = gson.toJson(utente);
                        editor.putString("utente", json);
                        editor.commit ();
                    } else {
                        SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = prefs1.edit();
                        editor1.putString("mail", email);
                        editor1.commit ();
                    }

                    openMainActivity();
                    finish();
                } else {
                    Toast.makeText(this, "Mail già esistente", Toast.LENGTH_SHORT).show();
                    mailLayout.setError("Mail già esistente");
                }
            }
        }
    }
}