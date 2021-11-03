package com.batsoftware.contagiapp.eventi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfiloPartecipanteFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    MaterialButton btnRimuoviPartecipanteEvento;
    TextView tvNomeCognome;
    TextView tvNascita;
    TextView tvCitta;
    ImageView imgPartecipante;

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgUtenti";


    public ProfiloPartecipanteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilo_partecipante, container, false);

        Log.d("doveSiamo", "ProfiloPartecipanteFragment");

        btnRimuoviPartecipanteEvento = view.findViewById(R.id.btnRimuoviPartecipante);
        tvNomeCognome = view.findViewById(R.id.tvNomeCognomePartecipante);
        tvNascita = view.findViewById(R.id.tvNascita);
        tvCitta = view.findViewById(R.id.tvCitta);
        imgPartecipante = view.findViewById(R.id.imageViewProfiloPartecipante);


        Bundle bundle = getArguments();
        final String mailPartecipante = bundle.getString("mailPartecipante");
        final String idEvento = bundle.getString("idEvento");


        caricaImgDaStorage(storageRef, storageDirectory, mailPartecipante, imgPartecipante);
        caricaPartecipante(mailPartecipante);

        btnRimuoviPartecipanteEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage(getText(R.string.are_you_sure_you_want_to_remove_the_reservation));
                builder1.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        rimuoviPartecipante(idEvento, mailPartecipante);

                        Bundle bundle = new Bundle();
                        bundle.putString("idEvento", idEvento);
                        ProfiloEventoAdminFragment fragment = new ProfiloEventoAdminFragment();
                        fragment.setArguments(bundle);

                        //richiamo il fragment
                        FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                        fr.replace(R.id.container,fragment);
                        fr.addToBackStack(null); //serve per tornare al fragment precedente
                        fr.commit();

                    }
                });

                builder1.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();


            }
        });

        return view;
    }

    private void rimuoviPartecipante(final String idEvento, final String mailPartecipante){
        db.collection("Eventi")
                .document(idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Evento evento = documentSnapshot.toObject(Evento.class);

                        ArrayList<String> partecipanti = evento.getPartecipanti();
                        partecipanti.remove(mailPartecipante);

                        //aggiorno la lista partecipanti
                        db.collection("Eventi")
                                .document(idEvento)
                                .update("partecipanti", partecipanti);
                    }
                });

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

    private void caricaPartecipante(String mail){
        db.collection("Utenti")
                .document(mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Utente utente = documentSnapshot.toObject(Utente.class);

                        tvNomeCognome.setText(utente.getNome() + " " + utente.getCognome());
                        tvNascita.setText(utente.getDataNascita());
                        tvCitta.setText(utente.getCitta());
                    }
                });

    }



}