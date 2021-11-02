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
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
            final ImageView img = findViewById(R.id.imgProfiloEvento);

            db.collection("Eventi")
                    .document(idEvento)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            evento = documentSnapshot.toObject(Evento.class);

                            nome.setText(evento.getNome());
                            descrizione.setText(evento.getDescrizione());
                            numeroMaxP.setText(Integer.toString(evento.getNumeroMaxPartecipanti()));
                            autoCompleteRegion.setText(evento.getRegione());
                            autoCompleteProvincia.setText(evento.getProvincia());
                            autoCompleteCity.setText(evento.getCitta());
                            indirizzo.setText(evento.getIndirizzo());
                            data.setText(evento.getData());
                            orario.setText(evento.getOrario());

                            creaEvento.setText("Modifica Evento");

                            //recupero l'immagine dallo storage
                            //TODO caricare l'immgaine precedente
                            /*Log.d("eventi/idEvento","eventi/"+idEvento);
                            storageRef.child("eventi/"+idEvento).getDownloadUrl()
                                    .addOnSuccessListener( new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String sUrl = uri.toString(); //otteniamo il token del'immagine
                                            Log.d("OnSuccess", "");
                                            Log.d("sUrl", sUrl);
                                            Picasso.get().load(sUrl).into(img);
                                        }})
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("OnFailure Exception", String.valueOf(e));
                                        }
                                    });*/
                        }
                    });
        }

//TODO controllare se la modifica evento funziona e se la chiamata di
// questa pagina funziona senza problemi sia da "profiloEventoAdminFragment"
// che da "EventsFragment" e perchè torna indietro in ProfiloEventoFragment anzichè EventsFragment
// aggiustare i controlli per data e orario e capire perchè quando apro il calendario anzichè 12 mi esce 1

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


        /*mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);*/


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
                    month = Integer.parseInt(data.substring(3,5));
                    year = Integer.parseInt(data.substring(6,10)) - 1;
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

                final Intent parentIntent = NavUtils.getParentActivityIntent(this);

                db.collection("Eventi").add(evento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentId = documentReference.getId();
                        evento.setIdEvento(documentId);
                        db.collection("Eventi").document(documentId).update("idEvento", documentId);
                        uploadImage();

                        if(cond) {
                            db.collection("Eventi").document(idEvento).delete();
                            //db.collection("Eventi").document(idEvento).update(evento);

                            Toast.makeText(NewEventsActivity.this, "Evento modificato", Toast.LENGTH_SHORT).show();

                            /*ProfiloEventoAdminFragment eventoAdmin = new ProfiloEventoAdminFragment();

                            Bundle bun = new Bundle();
                            bun.putString("idEvento", documentId);

                            eventoAdmin.setArguments(bun);
                            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                            fr.replace(R.id.new_event,eventoAdmin);
                            fr.addToBackStack(null); //serve per tornare al fragment precedente
                            fr.commit();*/

//TODO il problema è quando si va da questa activity a profiloEventoAdminFragment
                            finish();
                        } else {
                            Toast.makeText(NewEventsActivity.this, "Evento aggiunto", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                        /*parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        parentIntent.putExtra("idEvento", documentId);
                        startActivity(parentIntent);
                        finish();*/
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
                        /*.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + task.getResult().getId());
                                documentId = task.getResult().getId();
                                evento.setIdEvento(documentId);
                                db.collection("Eventi").document(documentId).update("idEvento", documentId);
                                uploadImage();

                                if(cond) {
                                    db.collection("Eventi").document(idEvento).delete();
                                    //db.collection("Eventi").document(idEvento).update(evento);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });*/
            }else {
                Toast.makeText(NewEventsActivity.this, "Dati inseriti non corretti", Toast.LENGTH_LONG).show();
            }
        }


    }

    private Boolean dataOraValide(Evento evento){

        boolean validita = false;

        try {
            Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(evento.getData());
            Date dataAttuale = new Date(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            String orario = evento.getOrario();
            int minapp = Integer.valueOf(orario.substring(3,5));
            int oraapp = Integer.valueOf(orario.substring(0,2));

            if(dataEvento.compareTo(dataAttuale) >= 0) {
                if(dataEvento.compareTo(dataAttuale) == 0) {
                    if(oraapp > (cal.get(Calendar.HOUR_OF_DAY) + 1) &&
                            minapp <= cal.get(Calendar.MINUTE)) {
                        validita = true;
                    } else Toast.makeText(this, "L'evento deve essere almeno tra un'ora da adesso", Toast.LENGTH_LONG).show();
                } else validita = true;
            } else Toast.makeText(this, "Data inserita non valida", Toast.LENGTH_LONG).show();

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
}
