package com.batsoftware.contagiapp.gruppi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class AddImgGruppoActivity extends AppCompatActivity {

    private final static int PICK_IMAGE = 1;
    private Uri imageUri;
    private MaterialButton btnImgCopertina;
    private ImageView imageViewCopertina;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference gruppoCollection = db.collection("Gruppo");
    String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("sonoQUI", "AddImgGruppoActivity");

        setContentView(R.layout.activity_add_img_gruppo);
        btnImgCopertina = findViewById(R.id.btnAddImgCopertina);
        btnImgCopertina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }


    public void invitaAmici(View view) {
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nomeGruppo = extras.getString("nomeGruppo");
            String descrGruppo = extras.getString("descrGruppo");

            if(imageUri != null){
                addGroupToDb(nomeGruppo, descrGruppo);

            }else Toasty.warning(getApplicationContext(), "Inserire immagine", Toast.LENGTH_SHORT).show();



        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_SHORT).show();

    }

    public void addGroupToDb(String nomeGruppo, String descrGruppo) {
        String mailAdmin = getMailUtenteLoggato();

            ArrayList<String> listaMailPartecipanti = new ArrayList<String>();
            listaMailPartecipanti.add(getMailUtenteLoggato());

            final Gruppo gruppo = new Gruppo();
            gruppo.setAdmin(mailAdmin);
            gruppo.setNomeGruppo(nomeGruppo);
            gruppo.setDescrizione(descrGruppo);
            gruppo.setPartecipanti(listaMailPartecipanti);
            gruppo.setStatoGruppo("giallo");
            gruppo.setNroPartecipanti(1);
            gruppoCollection.add(gruppo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    documentId = documentReference.getId();
                    gruppo.setIdGruppo(documentId);
                    Log.d("documentId", String.valueOf(documentId));
                    Log.d("getIdGruppo", String.valueOf(gruppo.getIdGruppo()));
                    db.collection("Gruppo").document(documentId).update("idGruppo", documentId);
                    Toasty.success(AddImgGruppoActivity.this, getString(R.string.group_create), Toast.LENGTH_LONG).show();
                    uploadImage(documentId);

                    Intent invitaIntent = new Intent(AddImgGruppoActivity.this, InvitaAmiciGruppoActivity.class);
                    invitaIntent.putExtra("idGruppo", documentId);
                    startActivity(invitaIntent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.error(AddImgGruppoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
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

    private void selectImage(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);
    }

    private void uploadImage(String documentId){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getText(R.string.loading));
        pd.show();

        if((imageUri != null) && (documentId != null)){
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("imgGruppi").child(documentId);

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("downloadUrl", url);
                            pd.dismiss();
                            Toasty.success(AddImgGruppoActivity.this, getText(R.string.image_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toasty.error(AddImgGruppoActivity.this, getText(R.string.image_not_uploaded), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(AddImgGruppoActivity.this, getText(R.string.ERROR), Toast.LENGTH_SHORT).show();
            Log.e("Errore", "imageUri o documentId nulli");
            Log.d("documentId2", String.valueOf(documentId));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            Log.d("imageUri", String.valueOf(imageUri));

            ImageView imageView= findViewById(R.id.imageViewCopertinaGruppo);
            Picasso.get().load(imageUri).into(imageView); //mette l'immagine nell'ImageView di questa activity
            btnImgCopertina.setVisibility(View.GONE);

            imageViewCopertina = findViewById(R.id.imageViewCopertinaGruppo);
            imageViewCopertina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });
        }

    }


}