package com.example.contagiapp.eventi;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.R;
import com.example.contagiapp.gruppi.AddImgGruppoActivity;
import com.example.contagiapp.gruppi.CreaGruppoActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class NewEventsActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    EditText editTextLuogo;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final String TAG = "NewEventsFragment";

    private Button creaEvento;
    private TextView dataEvento;
    private TextClock orarioEvento;
    private DatePickerDialog.OnDateSetListener dataDellEvento;
    private TimePickerDialog.OnTimeSetListener orarioDellEvento;

    public static final int PICK_IMAGE = 1;
    private Uri imageUri;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventiCollection = db.collection("Eventi");
    String documentId = null;
    private int anno = 0, mese = 0, giorno = 0, ora=0, minuti=0, oraapp=0, minapp=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        editTextLuogo = findViewById(R.id.editTextIndirizzo);


        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);

        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openMainActivity();
                addEventToDb();
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
                dialog = new TimePickerDialog(NewEventsActivity.this, android.R.style.Theme_Material_InputMethod, orarioDellEvento,hour,minute,true);
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
                        NewEventsActivity.this,
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


    private void addEventToDb(){


        EditText nome = findViewById(R.id.editTextNomeEvento);
        EditText descrizione = findViewById(R.id.editTextDescrEvento);
        EditText numeroMaxP = findViewById(R.id.editTextNumMaxPartecipanti);
        TextView data = findViewById(R.id.dataEvento);
        TextClock orario= findViewById(R.id.orarioEvento);
        EditText citta = findViewById(R.id.editTextCitta);
        EditText indirizzo = findViewById(R.id.editTextIndirizzo);

        if(controlloEditText(nome.getText().toString(), numeroMaxP.getText().toString(), descrizione.getText().toString(), citta.getText().toString(), indirizzo.getText().toString())){
            final Evento evento = new Evento();
            evento.setAdmin(getMailUtenteLoggato());
            evento.setNome(nome.getText().toString());
            evento.setDescrizione(descrizione.getText().toString());
            evento.setNumeroMaxPartecipanti(Integer.parseInt(numeroMaxP.getText().toString()));
            evento.setData(data.getText().toString()); //da vedere controllo
            evento.setOrario(orario.getText().toString());
            evento.setCitta(citta.getText().toString());
            evento.setIndirizzo(indirizzo.getText().toString());

            ArrayList<String> partecipanti = new ArrayList<String>(); //inizializzo un array vuoto
            evento.setPartecipanti(partecipanti);


            Log.d("getIndirizzo", String.valueOf(evento.getIndirizzo()));
            Log.d("getData", String.valueOf(evento.getData()));
            Log.d("getOrario", String.valueOf(evento.getOrario()));


            if(dataOraValide(evento, evento.getData(), evento.getOrario())){
                db.collection("Eventi").add(evento)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                documentId = documentReference.getId();
                                evento.setIdEvento(documentId);
                                db.collection("Eventi").document(documentId).update("idEvento", documentId);
                                uploadImage();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                Toast.makeText(this, "Evento aggiunto", Toast.LENGTH_SHORT).show();

                finish();
            }else {
                finish();
                startActivity(getIntent());
            }
        }


    }


    private void openMainActivity() {
        Map<String, Object> evento = new HashMap<>();

        TextView nome = (TextView) findViewById(R.id.editTextNomeEvento);
        evento.put("nome", nome.getText().toString());

        TextView numeroP = (TextView) findViewById(R.id.editTextNumMaxPartecipanti);
        evento.put("num_partecipanti", numeroP.getText().toString());

        TextView data = (TextView) findViewById(R.id.dataEvento);
        String appoggio= data.getText().toString();

        TextClock orario= (TextClock) findViewById(R.id.orarioEvento);
        String appoggio1=orario.getText().toString();

        TextView descrizione = (TextView) findViewById(R.id.editTextDescrEvento);
        evento.put("descrizione", descrizione.getText().toString());

        Spinner nazione = (Spinner) findViewById(R.id.spinnerNazioni);
        evento.put("nazione", nazione.getSelectedItem().toString());

        TextView citta = (TextView) findViewById(R.id.editTextCitta);
        evento.put("citta", citta.getText().toString());

        TextView luogo = (TextView) findViewById(R.id.editTextCitta);

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

    private Boolean dataOraValide(Evento evento, String data, String orario){
        Boolean validita= false;
        Calendar cal = Calendar.getInstance();
        boolean condevento2=false;
        boolean condorario3= true;
        int l = data.length();
        int l1= orario.length();
        System.out.println("la lunghezza è stocazzooooo "+ l1);
        System.out.println("l'orario scelto è "+ orario);
        switch (l) {
            case 9:
                anno = Integer.valueOf(data.substring(l - 4, l));
                mese = Integer.valueOf(data.substring(l - 7, l - 5));
                giorno = Integer.valueOf(data.charAt(0)) - 48;
                break;
            case 10:
                anno = Integer.valueOf(data.substring(l - 4, l));
                mese = Integer.valueOf(data.substring(l - 7, l - 5));
                giorno = Integer.valueOf(data.substring(l - 10, l - 8));
                break;
        }
        minapp = Integer.valueOf(orario.substring(3,5));
        oraapp = Integer.valueOf(orario.substring(0,2));

        System.out.println("l'anno in cui viviamo è "+cal.get(Calendar.YEAR) + (anno >= cal.get(Calendar.YEAR)));
        System.out.println("il mese in cui viviamo è "+cal.get(Calendar.MONTH+1)+ ((mese) >= cal.get(Calendar.MONTH)));
        System.out.println("il giorno in cui viviamo è "+cal.get(Calendar.DAY_OF_MONTH)+ (giorno >= cal.get(Calendar.DAY_OF_MONTH)));
        System.out.println(" condizione orario"+(oraapp>= (cal.get(Calendar.HOUR_OF_DAY))));

        System.out.println("orario secondo sto cristo di server " +(cal.get(Calendar.HOUR_OF_DAY)));
        System.out.println("orario scelto "+ oraapp);
        System.out.println("minuti scelti "+ minapp);
        if (anno >= cal.get(Calendar.YEAR)) {
            if ((mese) >= cal.get(Calendar.MONTH)) {
                if (giorno >= cal.get(Calendar.DAY_OF_MONTH)) {
                    if (giorno == cal.get(Calendar.DAY_OF_MONTH)) {
                        if (oraapp >= (cal.get(Calendar.HOUR_OF_DAY))+1) {
                            System.out.println("arrivo qui");
                            evento.setData(data);
                            evento.setOrario(orario);
                            condorario3 = false;
                            condevento2=true;
                        }
                    } else condevento2 = true;
                } else condevento2 = true;
            } else condevento2 = true;
        } else condevento2 = true;

        if(!condevento2){
            Toast.makeText(this, "data non valida",Toast.LENGTH_SHORT).show();
            validita = false;
        }else if(condorario3) {
            Toast.makeText(this, "orario non valido",Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "l'evento deve essere tra minimo un'ora!",Toast.LENGTH_SHORT).show();
            validita = false;
        }else{
            validita = true;
        }
        return validita;
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        //TODO capire il funzionamento
        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
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


    public void addImgEvent(View view) {
        openImage();
    }


    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            ImageView imageView= findViewById(R.id.immagineEvento);
            Picasso.get().load(imageUri).into(imageView); //mette l'immagine nell'ImageView di questa activity
        }

    }

    public boolean controlloEditText(String nomeEvento, String numMaxPartecipanti, String descrEvento, String citta, String indirizzo) {
        boolean isValid = false;

        TextInputLayout textInputLayoutNome = findViewById(R.id.textInputNomeEventoLayout);
        TextInputLayout textInputLayoutNumMaxPartecipanti = findViewById(R.id.textInputNumMaxPartecipantiLayout);
        TextInputLayout textInputLayoutDescrEvento = findViewById(R.id.textInputDescrEvento);
        TextInputLayout textInputLayoutCitta = findViewById(R.id.textInputCitta);
        TextInputLayout textInputLayoutIndirizzo = findViewById(R.id.textInputIndirizzo);

        if ((!nomeEvento.isEmpty()) && (!numMaxPartecipanti.isEmpty()) && (!descrEvento.isEmpty()) && (!citta.isEmpty()) && (!indirizzo.isEmpty())) { //se sono tutti validi
            textInputLayoutNome.setErrorEnabled(false);
            textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
            textInputLayoutDescrEvento.setErrorEnabled(false);
            textInputLayoutCitta.setErrorEnabled(false);
            textInputLayoutIndirizzo.setErrorEnabled(false);
            isValid = true;

        } else {
            if (nomeEvento.isEmpty() && numMaxPartecipanti.isEmpty() && descrEvento.isEmpty() && citta.isEmpty() && indirizzo.isEmpty()) { //se sono tutti vuoti
                Toasty.warning(NewEventsActivity.this, "Inserisci tutti i campi", Toast.LENGTH_SHORT).show();

                textInputLayoutNome.setError("Inserisci nome dell'evento");
                textInputLayoutNumMaxPartecipanti.setError("Inserisci descrizione del gruppo");
                textInputLayoutDescrEvento.setError("Inserisci descrizione");
                textInputLayoutCitta.setError("Inserisci città");
                textInputLayoutIndirizzo.setError("Inserisci indirizzo");
            } else {

                //se solo uno tra tutti è vuoto
                if (indirizzo.isEmpty()) {
                    textInputLayoutIndirizzo.setError("Inserisci indirizzo");
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                }

                if (citta.isEmpty()) {
                    textInputLayoutCitta.setError("Inserisci città");
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (descrEvento.isEmpty()) {
                    textInputLayoutDescrEvento.setError("Inserisci descrizione");
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (numMaxPartecipanti.isEmpty()) {
                    textInputLayoutNumMaxPartecipanti.setError("Inserisci numero massimo dei partecipanti");
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (nomeEvento.isEmpty()) {
                    textInputLayoutNome.setError("Inserisci nome del gruppo");
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

            }
        }
        return isValid;
    }

    private  void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Caricamento");
        pd.show();

        Log.d("imageUri", String.valueOf(imageUri));
        Log.d("documentID", String.valueOf(documentId));

        if((imageUri != null) && (documentId != null)){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("eventi").child(documentId);

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("downloadUrl", url);
                            //pd.dismiss();
                            Toast.makeText(NewEventsActivity.this, "immagine caricata", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(NewEventsActivity.this, "immagine non caricata", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
};
