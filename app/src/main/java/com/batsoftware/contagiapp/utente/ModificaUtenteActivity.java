package com.batsoftware.contagiapp.utente;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.batsoftware.contagiapp.R;
//import com.example.contagiapp.registrazione.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dizionarioPerCitta.Cities;
import dizionarioPerCitta.Province;
import dizionarioPerCitta.Regions;

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

    private AutoCompleteTextView autoCompleteRegion;
    private AutoCompleteTextView autoCompleteProvincia;
    private AutoCompleteTextView autoCompleteCity;
    private TextInputLayout layoutRegion;
    private TextInputLayout layoutProvince;
    private TextInputLayout layoutCity;


    String regioneSelezionata = null;
    String provinciaSelezionata = null;
    String cittaSelezionata = null;
    ArrayAdapter<String> adapterProvincia = null;
    ArrayAdapter<String> adapterRegione = null;
    ArrayAdapter<String> adapterCitta = null;

    private Utente utente = new Utente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_dati_utente);

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

        autoCompleteRegion = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextRegioneModActivity);
        autoCompleteProvincia = findViewById(R.id.autoCompleteTextProvinciaModActivity);
        autoCompleteCity = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextCittaModActivity);
        layoutRegion = findViewById(R.id.textInputRegioneLayoutModActivity);
        layoutProvince = findViewById(R.id.textInputProvinciaLayoutModActivity);
        layoutCity = findViewById(R.id.textInputCittaLayoutModActivity);

        adapterRegione = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Regions.all_regions);


        autoCompleteRegion.setAdapter(adapterRegione);
        autoCompleteProvincia.setEnabled(false);
        autoCompleteCity.setEnabled(false);


        autoCompleteRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Regione selezionata", autoCompleteRegion.getText().toString());
                regioneSelezionata = autoCompleteRegion.getText().toString();
                autoCompleteProvincia.setEnabled(true);
                adapterProvincia = new ArrayAdapter<String>(ModificaUtenteActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        Province.map.get(autoCompleteRegion.getText().toString()));
                autoCompleteProvincia.setAdapter(adapterProvincia);

                autoCompleteProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Provincia selezionata", autoCompleteProvincia.getText().toString());
                        provinciaSelezionata = autoCompleteProvincia.getText().toString();
                        autoCompleteCity.setEnabled(true);
                        adapterCitta = new ArrayAdapter<String>(ModificaUtenteActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                Cities.mapPerProvincia.get(autoCompleteProvincia.getText().toString()));

                        autoCompleteCity.setAdapter(adapterCitta);

                        autoCompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Log.d("Citta selezionata", autoCompleteCity.getText().toString());
                                cittaSelezionata = autoCompleteCity.getText().toString();
                            }
                        });
                    }
                });


            }

        });


        autoCompleteProvincia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("provincia", String.valueOf(provinciaSelezionata));
                autoCompleteCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("provincia", String.valueOf(provinciaSelezionata));
                autoCompleteCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteRegion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("regione", String.valueOf(regioneSelezionata));
                autoCompleteProvincia.setEnabled(false);
                autoCompleteProvincia.setText(null);
                autoCompleteCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("regione", String.valueOf(regioneSelezionata));
                autoCompleteProvincia.setEnabled(false);
                autoCompleteProvincia.setText(null);
                autoCompleteCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);

            db.collection("Utenti").whereEqualTo("mail", mailUtenteLoggato).get()
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
        autoCompleteRegion.setText(utente.getRegione());
        autoCompleteProvincia.setText(utente.getProvince());
        autoCompleteCity.setText(utente.getCitta());
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


        // collegamento button registrati con la mainActivity
        modifica = (Button) findViewById(R.id.modificaDati);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (controlli_TextInput(nome, nomeLayout, cognome, cognomeLayout, mail, mailLayout, data, dataLayout, phone, phoneLayout, psw1, psw1Layout, psw2, psw2Layout,
                        layoutRegion, autoCompleteRegion, layoutProvince, autoCompleteProvincia, layoutCity, autoCompleteCity)) {

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
                        layoutRegion.setError("Inserisci regione");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci regione", Toast.LENGTH_SHORT).show();
                        break;

                    case 5:
                        layoutProvince.setError("Inserisci provincia");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci provincia", Toast.LENGTH_SHORT).show();
                        break;

                    case 6:
                        layoutCity.setError("Inserisci citta");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci citta", Toast.LENGTH_SHORT).show();
                        break;

                    case 7:
                        mailLayout.setError("Inserisci mail");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci mail", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        phoneLayout.setError("Inserisci cellulare");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci cellulare", Toast.LENGTH_SHORT).show();
                        break;
                    case 9:
                        psw1Layout.setError("Inserisci password");
                        Toast.makeText(ModificaUtenteActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                        break;
                    case 10:
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
                                    TextInputEditText psw2, TextInputLayout psw2Layout,
                                    TextInputLayout regioneLayout, AutoCompleteTextView tvRegione,
                                    TextInputLayout provinciaLayout, AutoCompleteTextView tvProvincia,
                                    TextInputLayout cittaLayout,  AutoCompleteTextView tvCitta) {

        if (isEmpty(name)) {
            return 1;
        } else nomeLayout.setError(null);

        if (isEmpty(surname)) {
            return 2;
        } else cognomeLayout.setError(null);

        if (isEmpty(birth)) {
            return 3;
        } else dataLayout.setError(null);

        if (tvRegione.getText().toString() == null) {
            return 4;
        } else regioneLayout.setError(null);

        if (tvProvincia.getText().toString() == null) {
            return 5;
        } else provinciaLayout.setError(null);

        if (tvCitta.getText().toString() == null) {
            return 6;
        } else cittaLayout.setError(null);
        if (isEmpty(mail)) {
            return 7;
        } else mailLayout.setError(null);

        if (isEmpty(phone)) {
            return 8;
        } else phoneLayout.setError(null);

        if (isEmpty(psw1)) {
            return 9;
        } else psw1Layout.setError(null);

        if (isEmpty(psw2)) {
            return 10;
        } else psw2Layout.setError(null);


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


        user.put("regione", regioneSelezionata);
        utente.setRegione(regioneSelezionata);


        user.put("province", provinciaSelezionata);
        utente.setProvince(provinciaSelezionata);

        user.put("citta", cittaSelezionata);
        utente.setCitta(cittaSelezionata);

        EditText telefono = (EditText) findViewById(R.id.editTextPhone);
        String cell = telefono.getText().toString();
        user.put("telefono", cell);
        utente.setTelefono(cell);

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