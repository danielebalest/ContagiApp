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

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
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

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class SegnalaNegativita extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private MaterialButton btnAddImgNegativita;
    private MaterialButton btnAddPdfNegativita;
    private MaterialButton completaSegnalazione;
    DatePickerDialog datePickerDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnala_negativita);

        final EditText editTextDataNegativita = findViewById(R.id.editTextDataNegativita);
        final TextInputLayout textInputLayoutData = findViewById(R.id.textInputLayoutDataNegativita);





        //Todo: inserire data OK
        //Todo: controllo DA SISTEMARE
        //Todo: completa segnalazione: devo controllare ancora la data: se data 


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

                                int annoInserito = cal.get(Calendar.YEAR);
                                int giornoDellAnnoInserito = cal.get(Calendar.DAY_OF_YEAR);


                                Log.d("YEAR", String.valueOf(cal.get(Calendar.YEAR)));
                                Log.d("giornoDellAnno", String.valueOf(cal.get(Calendar.DAY_OF_YEAR)));
                                controlloData(editTextDataNegativita, textInputLayoutData, giornoDellAnnoInserito, annoInserito);
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

                    Intent i = new Intent(SegnalaNegativita.this, SettingActivity.class);
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

    private void controlloData(EditText editTextData, TextInputLayout textInputLayoutData, int dayOfYear, int anno){
        /*
        * regole. La data del tampone negativo deve:
        * 1) essere inferiore o pari alla data attuale OK
        * 2) essere superiore alla data di segnalazione del tampone positivo + 10 giorni se l'utente è stato contagiato
        * 3) se sono passati più di 10 giorni dal tampone negativo lo stato torna in giallo
        * */

        String data = editTextData.getText().toString();

        //todo: controllo da sistemare. Deve tenere conto anche dell'anno
        Calendar cal = Calendar.getInstance();
        int dayOfYearToday = cal.get(Calendar.DAY_OF_YEAR);
        int annoAttuale = cal.get(Calendar.YEAR);

        if(editTextData == null){
            textInputLayoutData.setError("inserisci data");
        }else{
            textInputLayoutData.setErrorEnabled(false);

            if(anno < annoAttuale){
                textInputLayoutData.setError("E' trascorso molto tempo");
            }else {
                if((dayOfYear - dayOfYearToday) > 0){
                    textInputLayoutData.setError("Data successiva a quella di oggi");
                }else if ((dayOfYearToday - dayOfYear) > 5){
                    textInputLayoutData.setError("E' trascorso molto tempo");
                    Toasty.warning(SegnalaNegativita.this, "E' trascorso molto tempo", Toast.LENGTH_SHORT).show();
                }else
                    textInputLayoutData.setErrorEnabled(false);
            }
        }
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