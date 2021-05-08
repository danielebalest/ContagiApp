package com.example.contagiapp.gruppi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contagiapp.AddUserAdapter;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ModificaGruppoFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgGruppi";

    private Uri imageUri;
    ImageView imageViewModificaCopertina;

    String idGruppo;

    private final static int PICK_IMAGE = 1;
    final ArrayList<Utente> listaPartecipanti = new ArrayList<Utente>();

    public ModificaGruppoFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view;
        view = inflater.inflate(R.layout.fragment_modifica_gruppo, container, false);

        final EditText nome = view.findViewById(R.id.editTextModificaNomeGruppo);
        final EditText descr = view.findViewById(R.id.editTextModificaDescrGruppo);
        imageViewModificaCopertina = view.findViewById(R.id.imageViewModificaCopertinaGruppo);

        Bundle bundle = getArguments();
        idGruppo = bundle.getString("idGruppo");
        Log.d("idGruppo ModificaFrag", String.valueOf(idGruppo));


        //carica il gruppo
        db.collection("Gruppo")
                .document(idGruppo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                        Log.d("gruppo.getNomeGruppo()", gruppo.getNomeGruppo());

                        nome.setText(gruppo.getNomeGruppo());
                        descr.setText(gruppo.getDescrizione());
                    }
                });


        MaterialButton btnSalvaModifiche = view.findViewById(R.id.btnSalvaModifiche);
        btnSalvaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifica(idGruppo, nome, descr, view);
                uploadImage(idGruppo);
            }
        });


        caricaImgDaStorage(storageRef, storageDirectory, idGruppo, imageViewModificaCopertina);

        imageViewModificaCopertina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        final RecyclerView rvRimuoviPartecipante = view.findViewById(R.id.rvModificaGruppo);


        //recupero listaPartecipanti
        db.collection("Gruppo").document(idGruppo)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                final ArrayList<String> listaMailPartecipanti = gruppo.getPartecipanti();
                Log.d("listaMailPartecipanti", String.valueOf(listaMailPartecipanti));


                for (int i = 0; i < listaMailPartecipanti.size(); i++) {
                    db.collection("Utenti")
                            .document(listaMailPartecipanti.get(i))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Utente user = new Utente();
                                    user.setNome(documentSnapshot.getString("nome"));
                                    user.setCognome(documentSnapshot.getString("cognome"));
                                    user.setMail(documentSnapshot.getString("mail"));
                                    user.setDataNascita(documentSnapshot.getString("dataNascita"));
                                    Log.d("Nome utente", String.valueOf(user.getNome()));
                                    Log.d("dataNascita", String.valueOf(user.getDataNascita()));


                                    listaPartecipanti.add(user);
                                    Log.d("amiciSize", String.valueOf(listaPartecipanti.size()));

                                    RimuoviPartecipanteAdapter adapter = new RimuoviPartecipanteAdapter(listaPartecipanti, idGruppo);
                                    rvRimuoviPartecipante.setAdapter(adapter);
                                    rvRimuoviPartecipante.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                }
                            });

                }

                Log.d("listaPartecipanti", String.valueOf(listaPartecipanti));



            }
        });




        return view;
    }

    private void modifica(String idGruppo, EditText nome, EditText descr, View view){

        if(controlloEditText(nome.getText().toString(), descr.getText().toString(), view)){
            db.collection("Gruppo")
                    .document(idGruppo)
                    .update("nomeGruppo", nome.getText().toString(), "descrizione", descr.getText().toString());
            Toasty.success(getActivity(), "Gruppo modificato", Toast.LENGTH_SHORT).show();

            ProfiloGruppoAdminFragment fragment = new ProfiloGruppoAdminFragment();

            Bundle bundle = new Bundle();
            bundle.putString("idGruppo", idGruppo);

            fragment.setArguments(bundle);
            //richiamo il fragment
            FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.container,fragment);
            fr.addToBackStack(null); //serve per tornare al fragment precedente
            fr.commit();
        }

    }

    public boolean controlloEditText(String nomeGruppo, String descrGruppo, View view) {
        TextInputLayout textInputLayoutNome = view.findViewById(R.id.TextLayoutModificaNomeGruppo);
        TextInputLayout textInputLayoutDesc = view.findViewById(R.id.TextLayoutModificaDescrGruppo);
        boolean isValid = false;

        if ((!nomeGruppo.isEmpty()) && (!descrGruppo.isEmpty())) {
            textInputLayoutNome.setErrorEnabled(false);
            textInputLayoutDesc.setErrorEnabled(false);
            isValid = true;

        } else {
            if (nomeGruppo.isEmpty() && descrGruppo.isEmpty()) {
                Toasty.warning(getActivity(), "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutNome.setError("Inserisci nome del gruppo");

                Toasty.warning(getActivity(), "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
            } else {

                if (nomeGruppo.isEmpty()) {
                    Toasty.warning(getActivity(), "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutNome.setError("Inserisci nome del gruppo");
                    textInputLayoutDesc.setErrorEnabled(false);


                }
                if (descrGruppo.isEmpty()) {
                    Toasty.warning(getActivity(), "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
                    textInputLayoutNome.setErrorEnabled(false);
                }
            }
        }
        return isValid;
    }

    private void selectImage(){
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == -1){
            imageUri = data.getData();
            Log.d("imageUri", String.valueOf(imageUri));

            ImageView imageView= getView().findViewById(R.id.imageViewModificaCopertinaGruppo);
            Picasso.get().load(imageUri).into(imageView); //mette l'immagine nell'ImageView di questa activity

            imageViewModificaCopertina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

        }

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

    private void uploadImage(String documentId){
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Caricamento");
        pd.show();


        //Log.d("documentId2", documentId);
        //Log.d("uri", imageUri.toString());
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
                            Toasty.success(getActivity(), "immagine caricata", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Toasty.error(getActivity(), "immagine non caricata", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            pd.dismiss();
            Toast.makeText(getActivity(), "Errore", Toast.LENGTH_SHORT).show();
            Log.e("Errore", "imageUri o documentId nulli");
            Log.d("documentId2", String.valueOf(documentId));
        }

    }
}