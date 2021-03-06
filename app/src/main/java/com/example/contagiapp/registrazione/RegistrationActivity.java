package com.example.contagiapp.registrazione;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.contagiapp.BuildConfig;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog.OnDateSetListener dataDiNascita;
    private static final String TAG = "RegistrationActivity";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;

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

    //per la foto
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private ImageView immagine;
    String imageFileName;
    static String currentPhotoPath;
    private Utente utente = new Utente();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        immagine= findViewById(R.id.propic);
        storage= FirebaseStorage.getInstance();
        storageReference= storage.getReferenceFromUrl("gs://contagiapp-c5306.appspot.com/");

        //Spinner per nazioni
        Spinner spinnerNazioni = findViewById(R.id.spinnerNazioni);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.nazioni, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerNazioni.setAdapter(adapter);
        spinnerNazioni.setOnItemSelectedListener(this);

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

        // collegamento button registrati con la mainActivity
        Button signUpButton = (Button) findViewById(R.id.modificaDati);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (controlli_TextInput(nome, nomeLayout, cognome, cognomeLayout, mail, mailLayout, data, dataLayout, phone, phoneLayout, psw1, psw1Layout, psw2, psw2Layout)){

                    case 1:
                        nomeLayout.setError("Inserisci nome");
                        Toast.makeText(RegistrationActivity.this, "Inserisci nome", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        cognomeLayout.setError("Inserisci cognome");
                        Toast.makeText(RegistrationActivity.this, "Inserisci cognome", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        dataLayout.setError("Inserisci data di nascita");
                        Toast.makeText(RegistrationActivity.this, "Inserisci data di nascita", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        mailLayout.setError("Inserisci mail");
                        Toast.makeText(RegistrationActivity.this, "Inserisci mail", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        phoneLayout.setError("Inserisci cellulare");
                        Toast.makeText(RegistrationActivity.this, "Inserisci cellulare", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        psw1Layout.setError("Inserisci password");
                        Toast.makeText(RegistrationActivity.this, "Inserisci Password", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        psw2Layout.setError("Inserisci password");
                        Toast.makeText(RegistrationActivity.this, "Inserisci nome", Toast.LENGTH_SHORT).show();
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
        data.setOnClickListener(new View.OnClickListener() {
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

                data.setText(date);
            }
        };
        Button scattafoto = findViewById(R.id.scattafoto);
        scattafoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dispatchTakePictureIntent(photoIntent);
            }
        });
    }
    private void dispatchTakePictureIntent(@NotNull Intent takePictureIntent) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            filePath = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @NotNull
    private File createImageFile() throws IOException {
        // Create an image file name
       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "PROPIC";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            immagine.setImageBitmap(bitmap);
            immagine.setRotation(90);
        }
    }

    private void uploadImage(String mail) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("files/"+mail+".jpg");

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private int controlli_TextInput(TextInputEditText name, TextInputLayout nomeLayout, TextInputEditText surname, TextInputLayout cognomeLayout, TextInputEditText mail, TextInputLayout mailLayout,
                                    TextInputEditText birth, TextInputLayout dataLayout, TextInputEditText phone, TextInputLayout phoneLayout, TextInputEditText psw1, TextInputLayout psw1Layout,
                                    TextInputEditText psw2, TextInputLayout psw2Layout){

        if(isEmpty(name)){
            return 1;
        }else nomeLayout.setError(null);

        if(isEmpty(surname)){
            return 2;
        }else cognomeLayout.setError(null);

        if(isEmpty(birth)){
            return 3;
        }else dataLayout.setError(null);

        if(isEmpty(mail)){
            return 4;
        }else mailLayout.setError(null);

        if(isEmpty(phone)){
            return 5;
        }else phoneLayout.setError(null);

        if(isEmpty(psw1)){
            return 6;
        }else psw1Layout.setError(null);

        if(isEmpty(psw2)){
            return 7;
        }else psw2Layout.setError(null);

        return 0;
    }

    private boolean isEmpty(TextInputEditText etText) {
        return etText.getText().toString().length() <= 0;
    }

    public void openMainActivity(){
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
        finishAfterTransition();
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
        } else {
            if(conddata && condemail) {
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
                        user1.put("mailPath", email);
                        utente.setPassword(psw1);
                        utente.setMail(email);
                        db.collection("Utenti").document(email).set(user1);

                        uploadImage(utente.getMail());
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(utente);
                        editor.putString("utente", json);
                        editor.apply ();


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
}
