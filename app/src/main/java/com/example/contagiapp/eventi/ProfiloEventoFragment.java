package com.example.contagiapp.eventi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.gruppi.Gruppo;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class ProfiloEventoFragment extends Fragment {

    private static final String TAG = "ProfiloEventoFragment";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "eventi";
    private Button btnPartecipa;
    private Button btnPartecipaComeGruppo;
    private ImageButton btnShare;
    public Evento evento;
    ArrayList<String> gruppiEvento = new ArrayList<>();


    public ProfiloEventoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_profilo_evento, container, false);

        Bundle bundle = getArguments();
        final String idEvento = bundle.getString("idEvento");
        Log.d("idEvento", String.valueOf(idEvento));

        controlloGruppi(idEvento);

        caricaEvento(idEvento, view);
        btnPartecipa= view.findViewById(R.id.partecipa_evento);
        btnPartecipaComeGruppo = view.findViewById(R.id.partecipa_evento_gruppo);

        final String mailutente= getMailUtenteLoggato();
        final ArrayList<String> partecipanti = new ArrayList<>();
        btnPartecipa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(evento.getAdmin().equals(mailutente)) {
                    Toast.makeText(getContext(), "Hai creato tu questo evento", Toast.LENGTH_SHORT).show();

                }else if(evento.getPartecipanti().contains(mailutente)) {
                    Toast.makeText(getContext(), "Ti sei già iscritto a questo evento", Toast.LENGTH_LONG).show();
                }else if((evento.getNumeroMaxPartecipanti() - evento.getPartecipanti().size()) == 0){
                    Toast.makeText(getContext(),"Non ci sono più posto disponibili",Toast.LENGTH_SHORT).show();
                }
                else {
                    //otteniamo lo stato dell'utente per controllarlo

                    db.collection("Utenti")
                            .document(getMailUtenteLoggato())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Utente utente = documentSnapshot.toObject(Utente.class);
                                    String stato = utente.getStato();

                                    if(stato.equals("giallo") || stato.equals("verde")){
                                        //OK può prenotarsi

                                        ArrayList<String> appoggio= evento.getPartecipanti();
                                        for(int i=0; i<appoggio.size();i++){
                                            partecipanti.add(appoggio.get(i));

                                        }

                                        partecipanti.add(getMailUtenteLoggato());
                                        assert evento != null;
                                        evento.setPartecipanti(partecipanti);

                                        db.collection("Eventi").document(idEvento).update("partecipanti", partecipanti);
                                        Toast.makeText(getContext(), "Iscrizione aggiunta!", Toast.LENGTH_LONG).show();

                                    }else{
                                        //non può prenotarsi
                                        Toasty.warning(getActivity(), "Non puoi prenotarti all'evento").show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error");
                        }
                    });

            }
        }
        });



        btnPartecipaComeGruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PartecipazioneGruppoFragment fragment = new PartecipazioneGruppoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("idEvento", idEvento);

                fragment.setArguments(bundle);

                Log.d("idEv", idEvento);

                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.addToBackStack(null); //serve per tornare al fragment precedente
                fr.commit();

            }
        });

        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setBackgroundColor(Color.TRANSPARENT);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                Log.d("nomeEvento", String.valueOf(evento.getNome()));

                String body = getString(R.string.subMessage1)  + evento.getNome().toUpperCase() + getString(R.string.subMessage2) + evento.getIndirizzo()
                        + getString(R.string.separator) + evento.getCitta() + getString(R.string.separator) + evento.getProvincia() + getString(R.string.separator)
                        + evento.getRegione()
                        +  getString(R.string.subMessage3);

                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });



        final ImageView img = view.findViewById(R.id.imgProfiloEvento);

        db.collection("Eventi").document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //recupero l'immagine dallo storage
                Log.d("eventi/idEvento","eventi/" + idEvento);

                caricaImgDaStorage(storageRef, storageDirectory, idEvento, img );
            }
        });




        return view;
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

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

    private void caricaEvento(String idEvento, final View view){
        db.collection("Eventi")
                .document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    evento = documentSnapshot.toObject(Evento.class);
                    String nome = evento.getNome();
                    String descrizione = evento.getDescrizione();
                    String data = evento.getData();
                    String orario = evento.getOrario();
                    String indirizzo = evento.getIndirizzo();
                    String regione = evento.getRegione();
                    String provincia = evento.getProvincia();
                    String citta = evento.getCitta();
                    int numMax = evento.getNumeroMaxPartecipanti();
                    int numPartecipanti = evento.getPartecipanti().size();
                    int numDisponibili = numMax - numPartecipanti;

                    TextView tvNomeEvento = view.findViewById(R.id.tvNomeEvento2);
                    TextView tvDescrEvento = view.findViewById(R.id.tvDescrEvento2);
                    TextView tvDataEvento = view.findViewById(R.id.tvDataEvento);
                    TextView tvOrarioEvento = view.findViewById(R.id.tvOrarioEvento);
                    TextView tvRegioneEvento = view.findViewById(R.id.tvRegioneEvento);
                    TextView tvProvinciaEvento = view.findViewById(R.id.tvProvinciaEvento);
                    TextView tvCittaEvento = view.findViewById(R.id.tvCittaEvento);
                    TextView tvIndirizzoEvento = view.findViewById(R.id.tvIndirizzoEvento);
                    TextView numMaxPartecipanti = view.findViewById(R.id.num_partecipanti_max);
                    TextView numDispono = view.findViewById(R.id.posti_disponibili);
                    TextView numParteci = view.findViewById(R.id.num_partecipanti);


                    tvNomeEvento.setText("Nome evento: "+nome);
                    tvDescrEvento.setText(descrizione);
                    tvDataEvento.setText(data);
                    tvOrarioEvento.setText(orario);
                    tvRegioneEvento.setText("Regione: " + regione);
                    tvProvinciaEvento.setText("Provincia: " + provincia);
                    tvCittaEvento.setText("Città: " +citta);
                    tvIndirizzoEvento.setText("Indirizzo: "+indirizzo);
                    numMaxPartecipanti.setText("Numero massimo di partecipanti:   "+numMax);
                    numDispono.setText("Numero posti disponibili:   "+numDisponibili);
                    numParteci.setText("Numero di iscritti all'evento:   "+numPartecipanti);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void controlloGruppi(final String idEvento) {
        final ArrayList<Boolean> cond = new ArrayList<>();

        db.collection("Eventi")
                .document(idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Evento evento1 = documentSnapshot.toObject(Evento.class);
                gruppiEvento = evento1.getGruppiPartecipanti();
                final ArrayList<String> partecipanti = evento1.getPartecipanti();

                //Log.d("ANY_TAG", String.valueOf(gruppiEvento));

                for(int i = 0; i < gruppiEvento.size(); i++) {
                    String idGruppo = gruppiEvento.get(i);

                    db.collection("Gruppo")
                            .document(idGruppo)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            //Log.d("ANY_TAG", "entered gruppo");
                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);

                            ArrayList<String> membri = gruppo.getPartecipanti();

                            for(int j = 0; j < membri.size(); j++) {
                                String membro = membri.get(j);
                                if(partecipanti.contains(membro)) {
                                    cond.add(true);
                                } else cond.add(false);
                            }
                        }
                    });

                    if(!cond.contains(true)) {
                        gruppiEvento.remove(idGruppo);
                    }
                }
            }
        });

        db.collection("Eventi").document(idEvento).update("gruppiPartecipanti", gruppiEvento);
    }

    //TODO come chiudere l'attività schiacciando il tasto indietro
}