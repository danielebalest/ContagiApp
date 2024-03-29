package com.batsoftware.contagiapp.gruppi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.batsoftware.contagiapp.Database;
import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.UserAdapter;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProfiloGruppoFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgGruppi";
    private List<Utente> listaPartecipanti = new ArrayList<Utente>();
    int nStato = 0;
    LinearLayout status;
    private TextView admin;
    private RecyclerView rvPartecipanti;
    TextView tvStatusDescr;

    ColorStateList red = ColorStateList.valueOf(Color.parseColor("#FF0000"));
    ColorStateList orange = ColorStateList.valueOf(Color.parseColor("#F4511E"));
    ColorStateList yellow = ColorStateList.valueOf(Color.parseColor("#FFF8F405"));
    ColorStateList green = ColorStateList.valueOf(Color.parseColor("#FF43A047"));

    private String descStatoVerde;
    private String descStatoGiallo;
    private String descStatoArancione;
    private String descStatoRosso;

    public ProfiloGruppoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view =  inflater.inflate(R.layout.fragment_profilo_gruppo, container, false);

        descStatoVerde = getString(R.string.DescrStatoGruppoVerde);
        descStatoGiallo = getString(R.string.DescrStatoGruppoGiallo);
        descStatoArancione = getString(R.string.DescrStatoGruppoArancione);
        descStatoRosso = getString(R.string.DescrStatoGruppoRosso);

        status = view.findViewById(R.id.groupStatusCircle);
        tvStatusDescr = view.findViewById(R.id.tvStatusDescription);
        admin = view.findViewById(R.id.tvAdmin);

        Bundle bundle = getArguments();
        final String idGruppo = bundle.getString("idGruppo");
        Log.d("idGruppo", String.valueOf(idGruppo));

        caricaGruppo(idGruppo, view);

        //QUA
        Database db = new Database();
        db.getStatoGruppo(new Database.StatoGruppo() {
            @Override
            public String setIdGruppo() {
                return idGruppo;
            }

            @Override
            public void getStato(String stato) {
                Log.d("stato", stato);
            }
        });



        MaterialButton btnAbbandonaGruppo = view.findViewById(R.id.btnAbbandonaGruppo);
        btnAbbandonaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage(R.string.are_you_sure_you_want_to_quit);

                builder1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        abbandonaGruppo(idGruppo, getMailUtenteLoggato());
                    }
                });

                builder1.setNegativeButton(
                        R.string.no,
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

                    final Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                    String nome = gruppo.getNomeGruppo();
                    String descrizione = gruppo.getDescrizione();
                    final ArrayList<String> mailPartecipanti = gruppo.getPartecipanti();
                    gruppo.aggiornaNroPartecipanti(mailPartecipanti);
                    int nroPartecipanti = gruppo.getNroPartecipanti();

                    Log.d("nomeGruppo", String.valueOf(nome));

                    TextView tvNomeGruppo = view.findViewById(R.id.tvNomeProfiloGruppo);
                    TextView tvDescGruppo = view.findViewById(R.id.tvDescrProfiloGruppo);
                    final TextView tvNroPartecipanti = view.findViewById(R.id.tvNumPartecipantiProfiloGruppo);
                    final TextView admin = view.findViewById(R.id.tvAdmin);

                    tvNomeGruppo.setText(nome);
                    tvDescGruppo.setText(descrizione);
                    tvNroPartecipanti.setText(getContext().getText(R.string.participants) + "(" + String.valueOf(nroPartecipanti) + ")");

                    rvPartecipanti = view.findViewById(R.id.rvPartecipantiProfiloGruppo);
                    Log.d("mailPartecipanti.size()", String.valueOf(mailPartecipanti.size()));

                    ImageView imageViewProfiloGruppo = view.findViewById(R.id.imgProfiloGruppo);
                    caricaImgDaStorage(storageRef, storageDirectory, idGruppo, imageViewProfiloGruppo);

                    aggiornaNrPartecipanti(mailPartecipanti.size(), gruppo.getIdGruppo());
                    //ciclo l'elenco dei partecipanti per poter ottenere
                    for(int i = 0; i < mailPartecipanti.size(); i++){
                        final int finalI = i + 1;
                        db.collection("Utenti")
                                .document(mailPartecipanti.get(i))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Utente user = documentSnapshot.toObject(Utente.class);

                                        if(user.getMailPath().equals(gruppo.getAdmin())) {
                                            admin.setText(user.getCognome()+" "+user.getNome());
                                        }
                                        Log.d("dataNascita", String.valueOf(user.getDataNascita()));

                                        listaPartecipanti.add(user);
                                        Log.d("listaPartecipantiFOR", String.valueOf(listaPartecipanti)); //qui è visibile, ma è nel for

                                        if(finalI == mailPartecipanti.size()) {
                                            caricaPartecipanti(listaPartecipanti);

                                            nStato = calcolaNuovoStatoGruppo(listaPartecipanti);
                                            Log.d("nStato", String.valueOf(nStato));
                                            impostaStatoGruppo(nStato, idGruppo, db);
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void aggiornaNrPartecipanti(int num, String idGruppo) {
        db.collection("Gruppo").document(idGruppo).update("nroPartecipanti", num);
    }

    private void caricaPartecipanti(List<Utente> list) {
        UserAdapter adapter = new UserAdapter(list);
        rvPartecipanti.setAdapter(adapter);
        rvPartecipanti.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private int calcolaNuovoStatoGruppo(List<Utente> listaPartecipanti){
        int nuovoStato = 0;
        ArrayList <Integer> listaStati = new ArrayList<Integer> ();

        for(int i = 0; i < listaPartecipanti.size(); i ++){
            listaStati.add(listaPartecipanti.get(i).statoToNumber());
        }


        nuovoStato = Collections.max(listaStati);

        return nuovoStato;
    }

    private void impostaStatoGruppo(int nStato, String idGruppo, FirebaseFirestore db) {
        switch (nStato){
            case 1:
                status.setBackgroundTintList(green);
                tvStatusDescr.setText(descStatoVerde);
                aggiornaStatoGruppo(idGruppo, "verde", db);
                break;
            case 2:
                status.setBackgroundTintList(yellow);
                tvStatusDescr.setText(descStatoGiallo);
                aggiornaStatoGruppo(idGruppo, "giallo", db);
                break;
            case 3:
                status.setBackgroundTintList(orange);
                tvStatusDescr.setText(descStatoArancione);
                aggiornaStatoGruppo(idGruppo, "arancione", db);
                break;
            case 4:
                status.setBackgroundTintList(red);
                tvStatusDescr.setText(descStatoRosso);
                aggiornaStatoGruppo(idGruppo, "rosso", db);
                break;
            default:
                break;
        }
    }

    private void aggiornaStatoGruppo (String idGruppo, String nuovoStatoGruppo,  FirebaseFirestore db){
        db.collection("Gruppo")
                .document(idGruppo)
                .update("statoGruppo", nuovoStatoGruppo);
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getActivity().getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }
}