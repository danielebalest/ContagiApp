package com.example.contagiapp.eventi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.contagiapp.R;
import com.example.contagiapp.notifiche.NotifyFragment;
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

public class EliminazionePartecipazioneEvento extends Fragment {

    private static final String TAG = "EliminazionePartecipazioneEvento";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "eventi";
    private Button btnElimina;
    public Evento evento;

    public EliminazionePartecipazioneEvento() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_rimuovi_partecipazione_evento, container, false);

        Bundle bundle = getArguments();
        final String idEvento = bundle.getString("idEvento");
        final boolean partenza = bundle.getBoolean("partenza");
        Log.d("idEvento", String.valueOf(idEvento));

        caricaEvento(idEvento, view);
        btnElimina = view.findViewById(R.id.btnRimuoviPartecipazione);

        btnElimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> partecipanti = evento.getPartecipanti();
                partecipanti.remove(getMailUtenteLoggato());

                db.collection("Eventi").document(idEvento).update("partecipanti", partecipanti);

                Fragment fragment = null;
                if(partenza)  fragment = new NotifyFragment();
                else fragment = new EventsFragment();

                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.addToBackStack(null); //serve per tornare al fragment precedente
                fr.commit();
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
                    String citta = evento.getCitta();
                    int numMax = evento.getNumeroMaxPartecipanti();
                    int numPartecipanti = evento.getPartecipanti().size();
                    int numDisponibili = numMax - numPartecipanti;

                    TextView tvNomeEvento = view.findViewById(R.id.tvNomeEvento2);
                    TextView tvDescrEvento = view.findViewById(R.id.tvDescrEvento2);
                    TextView tvDataEvento = view.findViewById(R.id.tvDataEvento);
                    TextView tvOrarioEvento = view.findViewById(R.id.tvOrarioEvento);
                    TextView tvIndirizzoEvento = view.findViewById(R.id.tvIndirizzoEvento);
                    TextView tvCittaEvento = view.findViewById(R.id.tvCittaEvento);
                    TextView numMaxPartecipanti = view.findViewById(R.id.num_partecipanti_max);
                    TextView numDispono = view.findViewById(R.id.posti_disponibili);
                    TextView numParteci = view.findViewById(R.id.num_partecipanti);

                    tvNomeEvento.setText("Nome evento: "+nome);
                    tvDescrEvento.setText(descrizione);
                    tvDataEvento.setText(data);
                    tvOrarioEvento.setText(orario);
                    tvIndirizzoEvento.setText("Indirizzo: "+indirizzo);
                    tvCittaEvento.setText("Città: "+citta);
                    numMaxPartecipanti.setText("Numero massimo di partecipanti:  "+numMax);
                    numDispono.setText("Numero posti disponibili:   "+numDisponibili);
                    numParteci.setText("Numero di iscritti all'evento:   "+numPartecipanti);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}