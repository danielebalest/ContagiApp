package com.example.contagiapp.impostazioni;

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

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class SegnalaNegativita extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private MaterialButton btnAddImgNegativita;
    private MaterialButton btnAddPdfNegativita;
    private MaterialButton completaSegnalazione;
    DatePickerDialog datePickerDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Utente utente = new Utente();
    private static final String TAG = "SegnalaNegativita";

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnala_negativita);

        final EditText editTextDataNegativita = findViewById(R.id.editTextDataNegativita);
        final TextInputLayout textInputLayoutData = findViewById(R.id.textInputLayoutDataNegativita);


        editTextDataNegativita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(
                        SegnalaNegativita.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                editTextDataNegativita.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                //int dayOfYear = dayOfMonth + 30 * month;

                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                int giornoDellAnnoInserito = cal.get(Calendar.DAY_OF_YEAR);


                                Log.d("YEAR", String.valueOf(cal.get(Calendar.YEAR)));
                                Log.d("giornoDellAnno", String.valueOf(cal.get(Calendar.DAY_OF_YEAR)));
                                controlloData(editTextDataNegativita, textInputLayoutData, giornoDellAnnoInserito);
                            }
                        },
                        year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });


        btnAddImgNegativita = findViewById(R.id.btnAddImgNegativita);
        btnAddImgNegativita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scegliImmagine();
            }
        });


        btnAddPdfNegativita = findViewById(R.id.btnAddPdfNegativita);
        btnAddPdfNegativita.setOnClickListener(new View.OnClickListener() {
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
                    cambiaStatoUtente("verde");
                    Log.d("data1", editTextDataNegativita.getText().toString());
                    aggiornaDataNegativita(editTextDataNegativita.getText().toString());

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    Gson gson = new Gson();
                    String json = prefs.getString("utente", "no");

                    if(!json.equals("no")) {
                        utente = gson.fromJson(json, Utente.class);
                        utente.setStato("verde");
                        utente.setDataNegativita(editTextDataNegativita.getText().toString());
                        json = gson.toJson(utente);
                        editor.putString("utente", json);
                        editor.commit();
                    }

                    Intent i = new Intent(SegnalaNegativita.this, MainActivity.class);
                    startActivity(i);
                }else
                    Toasty.warning(SegnalaNegativita.this, "Inserisci data e/o certificato", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cambiaStatoUtente(String nuovoStato){
        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .update("stato", nuovoStato);
    }



    private void controlloData(EditText editTextData, final TextInputLayout textInputLayoutData, final int dayOfYearNegativita){
        /*
        * regole. La data del tampone negativo deve:
        * 1) essere inferiore o pari alla data attuale OK
        * 2) essere superiore alla data di segnalazione del tampone positivo + 10 giorni se l'utente è stato contagiato
        * 3) se sono passati più di 10 giorni dal tampone negativo lo stato torna in giallo
        * */

        String data = editTextData.getText().toString();

        try {
            Date dataAttuale = new Date(System.currentTimeMillis());
            Date inserita = new SimpleDateFormat("dd/MM/yyyy").parse(data);

            if(editTextData == null) {
                textInputLayoutData.setError("inserisci data");
            } else {
                if((inserita.getTime() - dataAttuale.getTime()) > 0) {
                    textInputLayoutData.setErrorEnabled(true);
                    textInputLayoutData.setError("Data inserita successiva a quella di oggi");
                } else {
                    if((dataAttuale.getTime() - inserita.getTime()) > 864000000) {
                        textInputLayoutData.setErrorEnabled(true);
                        textInputLayoutData.setError("E' trascorso molto tempo");
                    } else textInputLayoutData.setErrorEnabled(false);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //CONTROLLO SU DATA POSITIVITA'
        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Utente utente = documentSnapshot.toObject(Utente.class);
                        String dataPositivita = utente.getDataPositivita();

                        if(dataPositivita != null){
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date parse = null;
                            try {
                                parse = sdf.parse(dataPositivita);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Calendar c = Calendar.getInstance();
                            c.setTime(parse);


                            int dayOfYearPositivita = c.get(Calendar.DAY_OF_YEAR);


                            Log.d("dayOfYearPositivita", String.valueOf(dayOfYearPositivita));
                            Log.d("dayOfYearNegativita", String.valueOf(dayOfYearNegativita));


                            if((dayOfYearNegativita - dayOfYearPositivita) < 10 && utente.getStato().equals("rosso")){
                                Log.d("differenza", String.valueOf((dayOfYearNegativita - dayOfYearPositivita)));
                                textInputLayoutData.setError("Devono essere trascorsi almeno 10 giorni dalla positività");
                            }
                        }
                    }
                });
    }



    private void aggiornaDataNegativita(String data) {
        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .update("dataNegativita", data);
    }


    private void scegliImmagine(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void scegliDocumento(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            uri = data.getData();
            Log.d("uri", String.valueOf(uri));
            Toasty.success(SegnalaNegativita.this, "Operazione eseguita", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(String documentId){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Caricamento");
        pd.show();


        if((uri != null) && (documentId != null)){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("certificatiNegativita").child(documentId);

            fileRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("downloadUrl", url);
                            pd.dismiss();
                            Toasty.success(SegnalaNegativita.this, "Certificato caricato", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toasty.error(SegnalaNegativita.this, "Certificato non caricato", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(SegnalaNegativita.this, "Errore", Toast.LENGTH_SHORT).show();
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
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }
}