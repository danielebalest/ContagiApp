package com.batsoftware.contagiapp.utente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.WelcomeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                            utente = documentSnapshot.toObject(Utente.class);
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



        modifica = findViewById(R.id.modifica_dati);
        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mod = new Intent(ProfiloActivity.this, ModificaUtenteActivity.class);
                startActivity(mod);
                finish();
            }
        });

        logout = findViewById(R.id.logout);
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
        if(utente.getStato().equals("rosso")) stato=getString(R.string.positive);
        if(utente.getStato().equals("verde")) stato=getString(R.string.negative);
        if(utente.getStato().equals("arancione")) stato=getString(R.string.contact_with_a_positive);
        if(utente.getStato().equals("giallo")) stato=getString(R.string.uncertain);

        arrayListProfilo.add(getString(R.string.state2dots) +stato);
        arrayListProfilo.add(getString(R.string.name2dots)+utente.getNome());
        arrayListProfilo.add(getString(R.string.surname2dots)+utente.getCognome());
        arrayListProfilo.add(getString(R.string.mail2dots)+utente.getMail());
        arrayListProfilo.add(getString(R.string.date_of_birth2dots)+utente.getDataNascita());
        arrayListProfilo.add(getString(R.string.gender2dots)+utente.getGenere());
        arrayListProfilo.add(getString(R.string.country_of_residence2dots)+utente.getNazione());
        arrayListProfilo.add(getString(R.string.region_of_residence2dots)+utente.getRegione());
        arrayListProfilo.add(getString(R.string.province_of_residence2dots)+utente.getProvince());
        arrayListProfilo.add(getString(R.string.city_of_residence2dots)+utente.getCitta());
        arrayListProfilo.add(getString(R.string.phone2dots)+utente.getTelefono());

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