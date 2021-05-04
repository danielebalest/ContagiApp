package com.example.contagiapp.gruppi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.eventi.NewEventsActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AddImgGruppoActivity extends AppCompatActivity {

    private final static int PICK_IMAGE = 1;
    private Uri imageUri;
    private MaterialButton btnImgCopertina;
    private ImageView imageViewCopertina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Intent invitaIntent = new Intent(this, InvitaAmiciGruppoActivity.class);
                invitaIntent.putExtra("nomeGruppo", nomeGruppo);
                invitaIntent.putExtra("descrGruppo", descrGruppo);
                invitaIntent.putExtra("imageUri", imageUri.toString());
                startActivity(invitaIntent);
            }else  Toast.makeText(getApplicationContext(), "Inserire immagine", Toast.LENGTH_SHORT).show();



        }else Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_SHORT).show();

    }


    private void selectImage(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);
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