package com.example.contagiapp.gruppi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;


public class ProfiloGruppoAdminFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgGruppi";
    private ArrayList<Utente> listaPartecipanti = new ArrayList<Utente>();
    LinearLayout status;
    int nStato = 0;
    private RecyclerView rvPartecipanti;

    ColorStateList red = ColorStateList.valueOf(Color.parseColor("#FF0000"));
    ColorStateList orange = ColorStateList.valueOf(Color.parseColor("#F4511E"));
    ColorStateList yellow = ColorStateList.valueOf(Color.parseColor("#FFF8F405"));
    ColorStateList green = ColorStateList.valueOf(Color.parseColor("#FF43A047"));




    public ProfiloGruppoAdminFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view =  inflater.inflate(R.layout.fragment_profilo_gruppo_admin, container, false);

        Bundle bundle = getArguments();
        final String idGruppo = bundle.getString("idGruppo");
        Log.d("idGruppo", String.valueOf(idGruppo));


        caricaGruppo(idGruppo, view);
        Log.d("listaPartecipONCREATE", String.valueOf(listaPartecipanti));

        MaterialButton btnInvita = view.findViewById(R.id.btnAdminInvitaAmici);
        MaterialButton btnEliminaGruppo = view.findViewById(R.id.btnEliminaGruppo);
        MaterialButton btnModificaGruppo = view.findViewById(R.id.btnModificaGruppo);
        status = view.findViewById(R.id.statusCircle);

        btnInvita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent invitaIntent = new Intent(getActivity(), InvitaAmiciGruppoActivity.class);
                invitaIntent.putExtra("idGruppo", idGruppo);
                //invitaIntent.putExtra("listaMailPartecipanti", mailPartecipanti);
                startActivity(invitaIntent);
            }
        });



        btnEliminaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Sicuro di eliminare il gruppo?");
                builder1.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        eliminaGruppo(idGruppo);

                        Bundle bundle = new Bundle();
                        GroupFragment fragment = new GroupFragment();
                        fragment.setArguments(bundle);

                        //richiamo il fragment
                        FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                        fr.replace(R.id.container,fragment);
                        fr.addToBackStack(null); //serve per tornare al fragment precedente
                        fr.commit();

                    }
                });

                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        btnModificaGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModificaGruppoFragment fragment = new ModificaGruppoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("idGruppo", idGruppo);

                fragment.setArguments(bundle);

                //richiamo il fragment
                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.addToBackStack(null); //serve per tornare al fragment precedente
                fr.commit();
            }
        });

        return view;
    }



    private void eliminaGruppo(String idGruppo){
        db.collection("Gruppo")
                .document(idGruppo)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("document", "DocumentSnapshot successfully deleted!");
                        Toasty.success(getActivity(), "Gruppo eliminato", Toast.LENGTH_SHORT).show();



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("document", "Error deleting document", e);
                        Toasty.error(getActivity(), "Gruppo non eliminato", Toast.LENGTH_SHORT).show();
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
                    final ArrayList<String> mailPartecipanti = gruppo.getPartecipanti();
                    gruppo.aggiornaNroPartecipanti(mailPartecipanti);
                    int nroPartecipanti = gruppo.getNroPartecipanti();

                    Log.d("nomeGruppo", String.valueOf(nome));

                    TextView tvNomeGruppo = view.findViewById(R.id.tvNomeProfiloGruppoAdmin);
                    TextView tvDescGruppo = view.findViewById(R.id.tvDescrProfiloGruppoAdmin);
                    final TextView tvNroPartecipanti = view.findViewById(R.id.tvNumPartecipantiProfiloGruppoAdmin);

                    ImageView imageViewProfiloGruppo = view.findViewById(R.id.imgProfiloGruppoAdmin);
                    caricaImgDaStorage(storageRef, storageDirectory, idGruppo, imageViewProfiloGruppo);

                    tvNomeGruppo.setText(nome);
                    tvDescGruppo.setText(descrizione);
                    tvNroPartecipanti.setText("Partecipanti" + "(" + String.valueOf(nroPartecipanti) + ")");

                    rvPartecipanti = view.findViewById(R.id.rvPartecipantiProfiloGruppoAdmin);
                    Log.d("mailPartecipanti.size()", String.valueOf(mailPartecipanti.size()));

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
                                        Log.d("dataNascita", String.valueOf(user.getDataNascita()));

                                        listaPartecipanti.add(user);
                                        Log.d("listaPartecipantiFOR", String.valueOf(listaPartecipanti)); //qui è visibile, ma è nel for

                                        if(finalI == mailPartecipanti.size()) {
                                            caricaPartecipanti(listaPartecipanti);

                                            //todo: richiamare il metodo per lo stato del gruppo
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

    private void caricaPartecipanti(List<Utente> list) {
        UserAdapter adapter = new UserAdapter(list);
        rvPartecipanti.setAdapter(adapter);
        rvPartecipanti.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void aggiornaNrPartecipanti(int num, String idGruppo) {
        db.collection("Gruppo").document(idGruppo).update("nroPartecipanti", num);
    }

    private void impostaStatoGruppo(int nStato, String idGruppo, FirebaseFirestore db) {
        switch (nStato){
            case 1:
                status.setBackgroundTintList(green);
                aggiornaStatoGruppo(idGruppo, "verde", db);
                break;
            case 2:
                status.setBackgroundTintList(yellow);
                aggiornaStatoGruppo(idGruppo, "giallo", db);
                break;
            case 3:
                status.setBackgroundTintList(orange);
                aggiornaStatoGruppo(idGruppo, "arancione", db);
                break;
            case 4:
                status.setBackgroundTintList(red);
                aggiornaStatoGruppo(idGruppo, "red", db);
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

    private int calcolaNuovoStatoGruppo(ArrayList<Utente> listaPartecipanti){
        int nuovoStato = 0;
        ArrayList <Integer> listaStati = new ArrayList<Integer> ();

        for(int i = 0; i < listaPartecipanti.size(); i ++){
            listaStati.add(listaPartecipanti.get(i).statoToNumber());
        }


        nuovoStato = Collections.max(listaStati);

        return nuovoStato;
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
}