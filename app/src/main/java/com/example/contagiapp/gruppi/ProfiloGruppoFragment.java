package com.example.contagiapp.gruppi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.eventi.Evento;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class ProfiloGruppoFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgGruppi";
    private ArrayList<Utente> listaPartecipanti = new ArrayList<Utente>();


    public ProfiloGruppoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view =  inflater.inflate(R.layout.fragment_profilo_gruppo, container, false);

        Bundle bundle = getArguments();
        final String idGruppo = bundle.getString("idGruppo");
        Log.d("idGruppo", String.valueOf(idGruppo));

        caricaGruppo(idGruppo, view);

        MaterialButton btnAbbandonaGruppo = view.findViewById(R.id.btnAbbandonaGruppo);
        btnAbbandonaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Sei sicuro di abbandonare il gruppo?");

                builder1.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        abbandonaGruppo(idGruppo, getMailUtenteLoggato());
                    }
                });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        return view;
    }


    private void abbandonaGruppo(final String idGruppo, final String mailUtenteLoggato){
        db.collection("Gruppo")
                .document(idGruppo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                            ArrayList<String> listaMailPartecipanti = gruppo.getPartecipanti();


                            listaMailPartecipanti.remove(mailUtenteLoggato);
                            db.collection("Gruppo").document(idGruppo).update("partecipanti", listaMailPartecipanti);

                            Bundle bundle = new Bundle();
                            GroupFragment fragment = new GroupFragment();
                            fragment.setArguments(bundle);

                            //richiamo il fragment
                            FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                            fr.replace(R.id.container,fragment);
                            fr.addToBackStack(null); //serve per tornare al fragment precedente
                            fr.commit();
                        }
                    }
                });
    }

    private void caricaGruppo(final String idGruppo, final View view){
        db.collection("Gruppo")
                .document(idGruppo)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                    String nome = gruppo.getNomeGruppo();
                    String descrizione = gruppo.getDescrizione();
                    String admin = gruppo.getAdmin();
                    final ArrayList<String> mailPartecipanti = gruppo.getPartecipanti();
                    gruppo.aggiornaNroPartecipanti(mailPartecipanti);
                    int nroPartecipanti = gruppo.getNroPartecipanti();

                    Log.d("nomeGruppo", String.valueOf(nome));

                    TextView tvNomeGruppo = view.findViewById(R.id.tvNomeProfiloGruppo);
                    TextView tvDescGruppo = view.findViewById(R.id.tvDescrProfiloGruppo);
                    TextView tvAdmin = view.findViewById(R.id.tvAdmin);
                    final TextView tvNroPartecipanti = view.findViewById(R.id.tvNumPartecipantiProfiloGruppo);


                    tvNomeGruppo.setText(nome);
                    tvDescGruppo.setText(descrizione);
                    tvNroPartecipanti.setText("Partecipanti" + "(" + String.valueOf(nroPartecipanti) + ")");
                    tvAdmin.setText(admin);




                    final RecyclerView rvPartecipanti = view.findViewById(R.id.rvPartecipantiProfiloGruppo);
                    Log.d("mailPartecipanti.size()", String.valueOf(mailPartecipanti.size()));
                    for(int i=0; i < mailPartecipanti.size(); i++){
                        db.collection("Utenti")
                                .document(mailPartecipanti.get(i))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Utente user = new Utente();
                                        user.setNome(documentSnapshot.getString("nome"));
                                        user.setCognome(documentSnapshot.getString("cognome"));
                                        user.setMail(documentSnapshot.getString("mail"));
                                        user.setDataNascita(documentSnapshot.getString("dataNascita"));
                                        Log.d("dataNascita", String.valueOf(user.getDataNascita()));

                                        listaPartecipanti.add(user);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getActivity(), "Error", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                    Log.d("listaPartecipanti", String.valueOf(listaPartecipanti));
                    UserAdapter adapter = new UserAdapter(listaPartecipanti);
                    rvPartecipanti.setAdapter(adapter);
                    rvPartecipanti.setLayoutManager(new LinearLayoutManager(getActivity()));


                    ImageView imageViewProfiloGruppo = view.findViewById(R.id.imgProfiloGruppo);
                    caricaImgDaStorage(storageRef, storageDirectory, idGruppo, imageViewProfiloGruppo);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT);
                }
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

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        //TODO capire il funzionamento
        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getActivity().getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }
}