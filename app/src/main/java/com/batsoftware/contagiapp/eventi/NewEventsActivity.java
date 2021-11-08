package com.batsoftware.contagiapp.eventi;

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
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import dizionarioPerCitta.Cities;
import dizionarioPerCitta.Province;
import dizionarioPerCitta.Regions;
import es.dmoral.toasty.Toasty;


public class NewEventsActivity extends AppCompatActivity {

    MapView mapView;
    EditText editTextIndirizzo;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final String TAG = "NewEventsFragment";

    private boolean caricato;
    private Button creaEvento;
    private TextView dataEvento;
    private TextClock orarioEvento;
    private DatePickerDialog.OnDateSetListener dataDellEvento;
    private TimePickerDialog.OnTimeSetListener orarioDellEvento;

    private String idEvento;
    private boolean cond;
    private Evento evento = new Evento();

    private EditText nome;
    private EditText descrizione;
    private EditText numeroMaxP;
    private TextInputEditText data;
    private TextClock orario;
    private EditText indirizzo;

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
    ArrayAdapter<String> adapterCitta = null;

    public static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private final static String storageDirectory = "eventi";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventiCollection = db.collection("Eventi");
    String documentId = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        caricato = false;

        nome = findViewById(R.id.editTextNomeEvento);
        descrizione = findViewById(R.id.editTextDescrEvento);
        numeroMaxP = findViewById(R.id.editTextNumMaxPartecipanti);
        data = findViewById(R.id.dataEvento);
        orario= findViewById(R.id.orarioEvento);
        indirizzo = findViewById(R.id.editTextIndirizzo);

        autoCompleteRegion = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextRegioneEvento);
        autoCompleteProvincia = findViewById(R.id.autoCompleteTextProvinciaEvento);
        autoCompleteCity = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextCittaEvento);
        layoutRegion = findViewById(R.id.textInputRegioneEventoLayout);
        layoutProvince = findViewById(R.id.textInputProvinciaEventoLayout);
        layoutCity = findViewById(R.id.textInputCittaLayoutEvento);


        Bundle bundle = getIntent().getExtras();
        cond = bundle.getBoolean("scelta");
        if(cond) {

            idEvento = bundle.getString("idEvento");
            TextView modifica = findViewById(R.id.textView2);
            modifica.setText(getText(R.string.modifica_evento));

            db.collection("Eventi")
                    .document(idEvento)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            evento = documentSnapshot.toObject(Evento.class);

                            caricato = true;
                            MaterialButton btnImg = findViewById(R.id.btnAddImgEvento);
                            btnImg.setVisibility(View.INVISIBLE);

                            nome.setText(evento.getNome());
                            descrizione.setText(evento.getDescrizione());
                            numeroMaxP.setText(Integer.toString(evento.getNumeroMaxPartecipanti()));
                            autoCompleteRegion.setText(evento.getRegione());
                            autoCompleteProvincia.setText(evento.getProvincia());
                            autoCompleteCity.setText(evento.getCitta());
                            indirizzo.setText(evento.getIndirizzo());
                            data.setText(evento.getData());
                            orario.setText(evento.getOrario());

                            creaEvento.setText(getText(R.string.modify_event));
                        }
                    });
        }

        ArrayAdapter<String> adapterRegione = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Regions.all_regions);


        autoCompleteRegion.setAdapter(adapterRegione);
        layoutProvince.setEnabled(false);
        layoutCity.setEnabled(false);


        autoCompleteRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Regione selezionata", autoCompleteRegion.getText().toString());
                regioneSelezionata = autoCompleteRegion.getText().toString();
                layoutProvince.setEnabled(true);
                adapterProvincia = new ArrayAdapter<String>(NewEventsActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        Province.map.get(autoCompleteRegion.getText().toString()));
                autoCompleteProvincia.setAdapter(adapterProvincia);

                autoCompleteProvincia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Provincia selezionata", autoCompleteProvincia.getText().toString());
                        provinciaSelezionata = autoCompleteProvincia.getText().toString();
                        layoutCity.setEnabled(true);
                        adapterCitta = new ArrayAdapter<String>(NewEventsActivity.this,
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
                layoutCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("provincia", String.valueOf(provinciaSelezionata));
                layoutCity.setEnabled(false);
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
                layoutProvince.setEnabled(false);
                autoCompleteProvincia.setText(null);
                layoutCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("regione", String.valueOf(regioneSelezionata));
                layoutProvince.setEnabled(false);
                autoCompleteProvincia.setText(null);
                layoutCity.setEnabled(false);
                autoCompleteCity.setText(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextIndirizzo = findViewById(R.id.editTextIndirizzo);

        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);

        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventToDb();
            }
        });

        //Date Picker
        orarioEvento=(TextClock) findViewById(R.id.orarioEvento);
        orarioEvento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                int hour;
                int minute;
                if(cond) {
                    String ora = evento.getOrario();
                    hour = Integer.parseInt(ora.substring(0,2));
                    minute = Integer.parseInt(ora.substring(3,5));
                } else {
                    hour = cal.get(Calendar.HOUR);
                    minute = cal.get(Calendar.MINUTE);
                }

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
                int year;
                int month;
                int day;

                if(cond) {
                    String data = evento.getData();
                    day = Integer.parseInt(data.substring(0,2));
                    month = Integer.parseInt(data.substring(3,5)) - 1;
                    year = Integer.parseInt(data.substring(6,10));
                } else {
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                }

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
                if(month <= 9) {
                    if(dayOfMonth <= 9) {
                        date = "0" + dayOfMonth + "/0" + month + "/" + year;
                    } else date = dayOfMonth + "/0" + month + "/" + year;
                } else {
                    if(dayOfMonth <= 9) {
                        date = "0" + dayOfMonth + "/" + month + "/" + year;
                    } else date = dayOfMonth + "/" + month + "/" + year;
                }

                dataEvento.setText(date);
            }
        };

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
        if(cittaSelezionata == null && !autoCompleteCity.getText().toString().isEmpty()) {
                cittaSelezionata = autoCompleteCity.getText().toString();
                regioneSelezionata = autoCompleteRegion.getText().toString();
                provinciaSelezionata = autoCompleteProvincia.getText().toString();
        }

        if(controlloEditText(nome.getText().toString(), numeroMaxP.getText().toString(), descrizione.getText().toString(), cittaSelezionata, indirizzo.getText().toString())){
            String mail = getMailUtenteLoggato();
            evento.setAdmin(mail);
            evento.setNome(nome.getText().toString());
            evento.setDescrizione(descrizione.getText().toString());
            evento.setNumeroMaxPartecipanti(Integer.parseInt(numeroMaxP.getText().toString()));
            evento.setData(data.getText().toString()); //da vedere controllo
            evento.setOrario(orario.getText().toString());
            evento.setRegione(regioneSelezionata);
            evento.setProvincia(provinciaSelezionata);
            evento.setCitta(cittaSelezionata);
            evento.setIndirizzo(indirizzo.getText().toString());
            evento.setStatoRosso(false);

            ArrayList<String> partecipanti = new ArrayList<String>(); //inizializzo un array vuoto
            evento.setPartecipanti(partecipanti);


            Log.d("getIndirizzo", String.valueOf(evento.getIndirizzo()));
            Log.d("getData", String.valueOf(evento.getData()));
            Log.d("getOrario", String.valueOf(evento.getOrario()));


            if(dataOraValide(evento)){
                if(caricato) {

                    db.collection("Eventi").add(evento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            documentId = documentReference.getId();
                            evento.setIdEvento(documentId);
                            db.collection("Eventi").document(documentId).update("idEvento", documentId);

                            String imgPath = documentId;

                            if(cond) {
                                db.collection("Eventi").document(idEvento).delete();
                                Toast.makeText(NewEventsActivity.this, "Evento modificato", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                db.collection("Eventi").document(documentId).update("pathImg", imgPath);
                                uploadImage();
                                Toast.makeText(NewEventsActivity.this, "Evento aggiunto", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
                } else Toasty.warning(NewEventsActivity.this, "Immagine non iserita", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(NewEventsActivity.this, "Dati inseriti non corretti", Toast.LENGTH_LONG).show();
        }


    }

    private Boolean dataOraValide(Evento evento){

        boolean validita = false;

        try {
            Date dataEvento = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(evento.getData()+" "+evento.getOrario());
            Date dataAttuale = new Date(System.currentTimeMillis());

            //3600000 = 1 ora
            if(dataEvento.getTime() - dataAttuale.getTime() < 3600000) {
                TextInputLayout dataa = findViewById(R.id.data_evento);
                dataa.setError(getString(R.string.tra_1_ora));
            } else validita = true;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return validita;
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
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
        caricato = true;
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
        TextInputLayout textInputLayoutCitta = findViewById(R.id.textInputCittaLayoutEvento);
        TextInputLayout textInputLayoutIndirizzo = findViewById(R.id.textInputIndirizzo);
        TextInputLayout textInputLayoutRegione = findViewById(R.id.textInputRegioneEventoLayout);
        TextInputLayout textInputLayoutProvincia = findViewById(R.id.textInputProvinciaEventoLayout);

        if ((!nomeEvento.isEmpty()) && (!numMaxPartecipanti.isEmpty()) && (!descrEvento.isEmpty()) && (citta != null) && (!indirizzo.isEmpty())) { //se sono tutti validi
            textInputLayoutNome.setErrorEnabled(false);
            textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
            textInputLayoutDescrEvento.setErrorEnabled(false);
            textInputLayoutCitta.setErrorEnabled(false);
            textInputLayoutIndirizzo.setErrorEnabled(false);
            isValid = true;

        } else {
            if (nomeEvento.isEmpty() && numMaxPartecipanti.isEmpty() && descrEvento.isEmpty() && citta == null && indirizzo.isEmpty()) { //se sono tutti vuoti
                Toasty.warning(NewEventsActivity.this, getText(R.string.fill_in_all_fields), Toast.LENGTH_SHORT).show();

                textInputLayoutNome.setError(getText(R.string.enter_event_name));
                textInputLayoutNumMaxPartecipanti.setError(getText(R.string.enter_group_description));
                textInputLayoutDescrEvento.setError(getText(R.string.enter_event_description));
                textInputLayoutCitta.setError(getText(R.string.enter_event_city));
                textInputLayoutIndirizzo.setError(getText(R.string.enter_event_address));
            } else {

                //se solo uno tra tutti è vuoto
                if (indirizzo.isEmpty()) {
                    textInputLayoutIndirizzo.setError(getText(R.string.enter_event_address));
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                }

                if (citta == null) {
                    textInputLayoutCitta.setError(getText(R.string.enter_event_city));
                    textInputLayoutRegione.setError(getText(R.string.inserisci_regione));
                    textInputLayoutProvincia.setError(getText(R.string.inserisci_provincia));
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (descrEvento.isEmpty()) {
                    textInputLayoutDescrEvento.setError(getText(R.string.enter_event_description));
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNumMaxPartecipanti.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (numMaxPartecipanti.isEmpty()) {
                    textInputLayoutNumMaxPartecipanti.setError(getText(R.string.enter_event_max_participants_number));
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutDescrEvento.setErrorEnabled(false);
                    textInputLayoutCitta.setErrorEnabled(false);
                    textInputLayoutIndirizzo.setErrorEnabled(false);
                }

                if (nomeEvento.isEmpty()) {
                    textInputLayoutNome.setError(getText(R.string.enter_group_name));
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
        pd.setMessage(getText(R.string.loading));
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
                            Toast.makeText(NewEventsActivity.this, getText(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toast.makeText(NewEventsActivity.this, getText(R.string.image_not_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
