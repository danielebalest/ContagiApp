package com.example.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.contagiapp.BuildConfig;
import com.example.contagiapp.R;
import com.example.contagiapp.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ProfiloActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final String TAG = "ProfiloActivity";
    private Button certificato;
    private ListView listViewProfilo;
    private Button logout;
    private Button modifica;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Utente utente;
    //private ImageView imgCertificato;
    private ImageView imgViewProfiloUtente;
    String imageFileName;
    String currentPhotoPath;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgUtenti";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo);
        //imgCertificato= findViewById(R.id.immaginecertificato);
        imgViewProfiloUtente = findViewById(R.id.imgProfilo);
        listViewProfilo = (ListView) findViewById(R.id.list_profilo);
        final ArrayList<String> arrayListProfilo = new ArrayList<>();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");

        //TODO verificare il controllo
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);

            riempiListView(arrayListProfilo);

            caricaImgDaStorage(storageRef, storageDirectory, utente.getMail(), imgViewProfiloUtente);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
            String username = prefs1.getString("mail", "no");
            Log.d("username", String.valueOf(username));


            db.collection("Utenti")
                    .document(username)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            utente = new Utente();
                            utente = documentSnapshot.toObject(Utente.class);
                            utente.setNome(documentSnapshot.getString("nome"));
                            utente.setCognome(documentSnapshot.getString("cognome"));
                            utente.setMail(documentSnapshot.getString("mail"));
                            utente.setDataNascita(documentSnapshot.getString("dataNascita"));
                            utente.setGenere(documentSnapshot.getString("genere"));
                            utente.setNazione(documentSnapshot.getString("nazione"));
                            utente.setRegione(documentSnapshot.getString("regione"));
                            utente.setProvince(documentSnapshot.getString("province"));
                            utente.setCitta(documentSnapshot.getString("citta"));
                            utente.setTelefono(documentSnapshot.getString("telefono"));
                            utente.setStato(documentSnapshot.getString("stato"));

                            riempiListView(arrayListProfilo);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Documento non esiste");
                }
            });
        }



        //arrayListProfilo.add("Propic"+ utente.getPropic());



        modifica = (Button) findViewById(R.id.modifica_dati);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mod = new Intent(ProfiloActivity.this, ModificaUtenteActivity.class);
                startActivity(mod);
                finish();
            }
        });

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplicationContext ().getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                Intent welcome = new Intent(ProfiloActivity.this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            }
        });

        /*certificato = (Button) findViewById(R.id.certificato);
        certificato.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               dispatchTakePictureIntent(photoIntent);
            }
        });*/
    }


    private void riempiListView(ArrayList<String> arrayListProfilo){
        String stato = null;
        if(utente.getStato().equals("rosso")) stato="POSITIVO";
        if(utente.getStato().equals("verde")) stato="NEGATIVO";
        if(utente.getStato().equals("arancione")) stato="contatto con un POSITIVO";
        if(utente.getStato().equals("giallo")) stato="INCERTO";

        arrayListProfilo.add("Stato: "+stato);
        arrayListProfilo.add("Nome: "+utente.getNome());
        arrayListProfilo.add("Cognome: "+utente.getCognome());
        arrayListProfilo.add("Mail: "+utente.getMail());
        arrayListProfilo.add("Data di Nascita: "+utente.getDataNascita());
        arrayListProfilo.add("Genere: "+utente.getGenere());
        arrayListProfilo.add("Nazione di residenza: "+utente.getNazione());
        arrayListProfilo.add("Regione di residenza: "+utente.getRegione());
        arrayListProfilo.add("Provincia di residenza: "+utente.getProvince());
        arrayListProfilo.add("Città di residenza: "+utente.getCitta());
        arrayListProfilo.add("Telefono: "+utente.getTelefono());

        caricaImgDaStorage(storageRef, storageDirectory, utente.getMail(), imgViewProfiloUtente);
        Log.d("arrayListProfilo", String.valueOf(arrayListProfilo));
        ArrayAdapter arrayAdapter = new ArrayAdapter(ProfiloActivity.this, R.layout.support_simple_spinner_dropdown_item, arrayListProfilo);
        listViewProfilo.setAdapter(arrayAdapter);
    }

    private void caricaImgDaStorage(StorageReference storageRef, String directory, String idImmagine, final ImageView imageView){
        storageRef.child(directory + "/" + idImmagine).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });
    }

   /* private void dispatchTakePictureIntent(@NotNull Intent takePictureIntent) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK ){
            Bitmap bitmap= BitmapFactory.decodeFile(currentPhotoPath);
            imgCertificato.setImageBitmap(bitmap);
            imgCertificato.setRotation(90);
        }
    }

    @NotNull
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }*/
}