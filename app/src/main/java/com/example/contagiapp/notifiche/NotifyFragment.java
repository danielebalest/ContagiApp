package com.example.contagiapp.notifiche;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.eventi.EliminazionePartecipazioneEvento;
import com.example.contagiapp.eventi.Evento;
import com.example.contagiapp.eventi.ProfiloEventoAdminFragment;
import com.example.contagiapp.gruppi.Gruppo;
import com.example.contagiapp.impostazioni.EventiPartecipatoAdapter;
import com.example.contagiapp.impostazioni.EventsPartecipatoPositivo;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti
    private RecyclerView rvEventiACuiPartecipo;
    private RecyclerView rvEventiRossi;
    private ArrayList<Evento> listaEventi = new ArrayList<Evento>();
    private Evento ev;
    private Utente utente;
    private final List<Evento> ev1 = new ArrayList<>();
    private boolean contro = false;
    //private List<Evento> eventi = new ArrayList<>();

    public NotifyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_notify, container, false);

        RecyclerView rvNoEventiPartecipazione = view.findViewById(R.id.rvNoPartecipazioneEvento);
        rvEventiRossi = view.findViewById(R.id.rvEventiRossi);
        final RecyclerView recyclerViewRichieste =  view.findViewById(R.id.rvRichieste);
        final RecyclerView recyclerViewInviti =  view.findViewById(R.id.rvInviti);
        rvEventiACuiPartecipo = view.findViewById(R.id.rvEventiACuiPartecipo);

        String mailUtenteLoggato = getMailUtenteLoggato();
        //Otteniamo la lista della mail degli amici
        db.collection("Utenti")
                .document(mailUtenteLoggato)
                .get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        ArrayList<String> listaMailRichieste = (ArrayList<String>) document.get("richiesteRicevute");
                        ArrayList<String> listaInviti = (ArrayList<String>) document.get("invitiRicevuti");

                        Log.d("listaMailRichieste", String.valueOf(listaMailRichieste));
                        Log.d("listaInviti", String.valueOf(listaInviti));
                        getRichieste(listaMailRichieste, recyclerViewRichieste);
                        getInviti(listaInviti, recyclerViewInviti);

                        utente = document.toObject(Utente.class);
                    }
                });

        caricaEventi(rvEventiACuiPartecipo);
        caricaEventiRossi(rvEventiRossi);
        caricaEventiNoPartecipazione(rvNoEventiPartecipazione);

        return view;
    }

    public void caricaEventiNoPartecipazione(final RecyclerView rvEventiACuiNonPartecipo) {
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("eventi", Context.MODE_PRIVATE);
        String json = pref.getString("id", "no");

        if(!json.equals("no")) {
            Gson gson = new Gson();
            final ArrayList<String> eventi;
            eventi = gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());

            if(eventi.size() != 0) {
                for(int i = 0; i < eventi.size(); i++) {
                    String id = eventi.get(i);

                    db.collection("Eventi")
                            .document(id)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    ev1.add(documentSnapshot.toObject(Evento.class));
                                    Log.d("EVENTI:::",documentSnapshot.toObject(Evento.class).getIdEvento());

                                    if(ev1.size() == eventi.size()) {
                                        EventoNoPartecipazioneAdapter adapter = new EventoNoPartecipazioneAdapter(ev1, getActivity().getApplicationContext());

                                        rvEventiACuiNonPartecipo.setAdapter(adapter);
                                        rvEventiACuiNonPartecipo.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    }
                                }
                            });
                }
            }
        }
    }

    public void caricaEventiRossi(final RecyclerView rvEventi) {

        final ArrayList<Evento> eventi = new ArrayList<>();

        db.collection("Eventi")
                .whereArrayContains("partecipanti", getMailUtenteLoggato())
                .whereEqualTo("statoRosso",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ev = document.toObject(Evento.class);

                                try {
                                    Date dataRosso = new SimpleDateFormat("dd/MM/yyyy").parse(ev.getDataRosso());
                                    Date dataAttuale = new Date(System.currentTimeMillis());

                                    //864000000 millisecondi = 10 giorni
                                    if(dataAttuale.getTime() - dataRosso.getTime() <= 864000000) {
                                        eventi.add(ev);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            EventoRossoAdapter adapter = new EventoRossoAdapter(getContext(), eventi, getMailUtenteLoggato(), utente);

                            rvEventi.setAdapter(adapter);
                            rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                    }
                });
    }

    public void getRichieste(ArrayList<String> listaRichieste, final RecyclerView recyclerView){
        /*
        metodo che svolge le seguenti operazioni:
         1)date in input le mail degli amici ottiene, per ciascuno, i seguenti dati dal database: nome, cognome, mail
         2)crea per ognuno un nuovo tipo Utente che aggiunge ad una lista
         3) passa la lista all'adapter del recycler View che poi permetterà la visualizzazione della lista di CardView degli amici sull'app
         */

        final ArrayList<Utente> utenti = new ArrayList<Utente>();
        for(int i=0; i < listaRichieste.size(); i++){
            db.collection("Utenti")
                    .document(listaRichieste.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Utente user = new Utente();
                            user.setNome(documentSnapshot.getString("nome"));
                            user.setCognome(documentSnapshot.getString("cognome"));
                            user.setMail(documentSnapshot.getString("mail"));
                            user.setDataNascita(documentSnapshot.getString("dataNascita"));
                            user.setCitta(documentSnapshot.getString("citta"));
                            user.setAmici((ArrayList<String>) documentSnapshot.get("amici"));
                            user.setRichiesteRicevute((ArrayList<String>) documentSnapshot.get("richiesteRicevute"));

                            Log.d("amici", String.valueOf(user.getAmici()));


                            utenti.add(user);
                            Log.d("richiesteSize", String.valueOf(utenti.size()));

                            db.collection("Utenti").document(getMailUtenteLoggato())
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = (DocumentSnapshot) task.getResult();

                                }
                            });

                            //
                            //apro il documento dell'utente loggato
                            //nell'adapter vengono aggiornati gli amici al click del bottone Accetta
                            db.collection("Utenti").document(getMailUtenteLoggato())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                final Utente utenteLoggato = documentSnapshot.toObject(Utente.class);
                                                RichiesteAdapter adapter = new RichiesteAdapter(utenti, getMailUtenteLoggato(), utenteLoggato);
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                            }else {
                                                Toast.makeText(getActivity(), "Documents does not exist", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });

                            //RichiesteAdapter adapter = new RichiesteAdapter(utenti, getMailUtenteLoggato(), utenteLoggato);

                            String id = user.getMail();
                            idList.add(id);



/*
                            recyclerView.addOnItemTouchListener(new NotifyFragment.RecyclerTouchListener(getActivity(), recyclerView, new NotifyFragment.RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    String idUtenteSelezionato = idList.get(position);
                                    Log.i("idList: ", idUtenteSelezionato);

                                    Intent profiloIntent = new Intent(getActivity(), ProfiloUtentiActivity.class);
                                    profiloIntent.putExtra("id", idUtenteSelezionato);
                                    startActivity(profiloIntent);
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }

                            }));
                            */

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", "errore");
                }


            });


        }

    }

    public void getInviti(final ArrayList<String> listaInviti, final RecyclerView recyclerView){


        final ArrayList<Gruppo> gruppi = new ArrayList<Gruppo>();
        for(int i=0; i < listaInviti.size(); i++){
            db.collection("Gruppo")
                    .document(listaInviti.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Gruppo gruppo = new Gruppo();
                            gruppo.setNomeGruppo(documentSnapshot.getString("nomeGruppo"));
                            gruppo.setAdmin(documentSnapshot.getString("admin"));
                            gruppo.setDescrizione(documentSnapshot.getString("descrizione"));
                            gruppo.setIdGruppo(documentSnapshot.getString("idGruppo"));
                            //gruppo.setNroPartecipanti((Integer) documentSnapshot.get("nroPartecipanti"));
                            gruppo.setPartecipanti((ArrayList<String>) documentSnapshot.get("partecipanti"));
                            Log.d("setPartecipanti", String.valueOf((ArrayList<String>) documentSnapshot.get("partecipanti")));

                            Log.d("idGruppo", String.valueOf(documentSnapshot.getString("idGruppo")));


                            gruppi.add(gruppo);
                            Log.d("gruppi.size()", String.valueOf(gruppi.size()));



                            db.collection("Utenti").document(getMailUtenteLoggato())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()){
                                                final Utente utenteLoggato = documentSnapshot.toObject(Utente.class);
                                                utenteLoggato.setInvitiRicevuti(listaInviti);
                                                InvitiAdapter adapter = new InvitiAdapter(gruppi, getMailUtenteLoggato(), utenteLoggato);
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                            }else {
                                                Toast.makeText(getActivity(), "Documents does not exist", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", "errore");
                }


            });


        }

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

    private void caricaEventi(final RecyclerView rvEventi){

        listaEventi = new ArrayList<Evento>();


        db.collection("Eventi")
                //.whereArrayContains("partecipanti", getMailUtenteLoggato())
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Evento evento = documentSnapshot.toObject(Evento.class);

                            try {
                                Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(evento.getData());
                                Date dataAttuale = new Date(System.currentTimeMillis());

                                if(dataEvento.compareTo(dataAttuale) >= 0
                                        && (evento.getPartecipanti().contains(getMailUtenteLoggato()) ||
                                        evento.getAdmin().equals(getMailUtenteLoggato()))) {

                                    listaEventi.add(evento);

                                    String id = documentSnapshot.getId();
                                    idList.add(id);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        EventPartecipanteAdapter adapter = new EventPartecipanteAdapter(listaEventi);

                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));


                        rvEventi.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvEventi, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Evento evento = listaEventi.get(position);
                                if(evento.getAdmin().equals(getMailUtenteLoggato())) {
                                    ProfiloEventoAdminFragment fragment = new ProfiloEventoAdminFragment();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("idEvento", idList.get(position));

                                    fragment.setArguments(bundle);

                                    //richiamo il fragment

                                    FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                                    fr.replace(R.id.container,fragment);
                                    fr.addToBackStack(null); //serve per tornare al fragment precedente
                                    fr.commit();
                                }

                                if(evento.getPartecipanti().contains(getMailUtenteLoggato())) {
                                    EliminazionePartecipazioneEvento fragment = new EliminazionePartecipazioneEvento();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("idEvento", idList.get(position));
                                    bundle.putBoolean("partenza", true);

                                    fragment.setArguments(bundle);

                                    //richiamo il fragment

                                    FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                                    fr.replace(R.id.container,fragment);
                                    fr.addToBackStack(null); //serve per tornare al fragment precedente
                                    fr.commit();
                                }
                            }
                            @Override
                            public void onLongClick(View view, int position) {

                            }

                        }));
                    }
                }); //toDo onFailure
    }


}

//TODO vedere se la partecipazione all'evento la vede solo chi si è iscritto all'evento come gruppo o tutto il gruppo
//per il click
class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private GestureDetector gestureDetector;
    private RecyclerTouchListener.ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerTouchListener.ClickListener clickListener) {
        this.clickListener = (ClickListener) clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());

        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}