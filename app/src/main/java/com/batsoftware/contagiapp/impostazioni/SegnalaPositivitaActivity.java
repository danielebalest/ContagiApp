package com.batsoftware.contagiapp.impostazioni;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class SegnalaPositivitaActivity extends AppCompatActivity {


    private static final int PICK_IMAGE = 1;
    private MaterialButton btnAddImgPositivita;
    private MaterialButton btnAddPdfPositivita;
    private MaterialButton completaSegnalazione;
    DatePickerDialog datePickerDialog;
    private static final String TAG = "SegnalaPositivita";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Utente utente = new Utente();

    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnala_positivita);

        final EditText editTextData = findViewById(R.id.editTextDataPositivita);
        final TextInputLayout textInputLayoutData = findViewById(R.id.textInputLayoutDataPositivita);

        editTextData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);




                datePickerDialog = new DatePickerDialog(
                        SegnalaPositivitaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                                month++;

                                String dataInserita;

                                if (month <= 9) {
                                    dataInserita = dayOfMonth + "/0" + month + "/" + year;
                                } else
                                    dataInserita = dayOfMonth + "/" + month + "/" + year;

                                editTextData.setText(dataInserita);
                                //int dayOfYear = dayOfMonth + 30 * month;

                                Log.d("giornoDellAnno", String.valueOf(cal.get(Calendar.DAY_OF_YEAR)));
                                controlloData(editTextData, textInputLayoutData, dataInserita);
                            }
                        },
                        year, month, dayOfMonth);

                datePickerDialog.show();

            }
        });




        btnAddImgPositivita = findViewById(R.id.btnAddImgPositivita);
        btnAddImgPositivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scegliImmagine();
            }
        });

        btnAddPdfPositivita = findViewById(R.id.btnAddPdfPositivita);
        btnAddPdfPositivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scegliDocumento();
            }
        });


            completaSegnalazione = findViewById(R.id.btnCompletaSegnalazione);
            completaSegnalazione.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(uri != null && !textInputLayoutData.isErrorEnabled()){
                        uploadImage(getMailUtenteLoggato());
                        cambiaStatoUtente("rosso");
                        Log.d("data1", editTextData.getText().toString());
                        aggiornaDataPositivita(editTextData.getText().toString());

                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        Gson gson = new Gson();
                        String json = prefs.getString("utente", "no");

                        if(!json.equals("no")) {
                            utente = gson.fromJson(json, Utente.class);
                            utente.setStato("rosso");
                            utente.setDataPositivita(editTextData.getText().toString());
                            json = gson.toJson(utente);
                            editor.putString("utente", json);
                            editor.commit ();
                        }

                        Intent i = new Intent(SegnalaPositivitaActivity.this, EventsPartecipatoPositivo.class);
                        i.putExtra("dataRosso", editTextData.getText().toString());
                        startActivity(i);
                    }else
                        Toasty.warning(SegnalaPositivitaActivity.this, getText(R.string.enter_date_and_certificate), Toast.LENGTH_SHORT).show();
                }
            });



    }

    private void aggiornaDataPositivita(String data) {
        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .update("dataPositivita", data);
    }

    private void cambiaStatoUtente(String nuovoStato){
        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .update("stato", nuovoStato);
    }


    private void controlloData(EditText editTextData, TextInputLayout textInputLayoutData, String dataInserita){

        try {
            Date dataAttuale = new Date(System.currentTimeMillis());
            Date inserita = new SimpleDateFormat("dd/MM/yyyy").parse(dataInserita);

            if(editTextData == null) {
                textInputLayoutData.setError(getString(R.string.enter_date));
            } else {
                if((inserita.getTime() - dataAttuale.getTime()) > 0) {
                    textInputLayoutData.setErrorEnabled(true);
                    textInputLayoutData.setError(getString(R.string.data_successiva));
                } else {
                    if((dataAttuale.getTime() - inserita.getTime()) > 864000000) {
                        textInputLayoutData.setErrorEnabled(true);
                        textInputLayoutData.setError(getString(R.string.molto_tempo));
                    } else textInputLayoutData.setErrorEnabled(false);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scegliDocumento(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }


    private void scegliImmagine(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            uri = data.getData();
            Log.d("uri", String.valueOf(uri));
            Toasty.success(SegnalaPositivitaActivity.this, getString(R.string.operazione_eseguita), Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(String documentId){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getText(R.string.loading));
        pd.show();


        if((uri != null) && (documentId != null)){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("certificatiPositivita").child(documentId);

            fileRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("downloadUrl", url);
                            pd.dismiss();
                            Toasty.success(SegnalaPositivitaActivity.this, getText(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toasty.error(SegnalaPositivitaActivity.this, getText(R.string.image_not_uploaded), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            finish();
                        }
                    });
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(SegnalaPositivitaActivity.this, "Errore", Toast.LENGTH_SHORT).show();
            Log.e("Errore", "Uri o documentId nulli");
            Log.d("documentId", String.valueOf(documentId));
        }
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
}