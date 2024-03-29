package com.batsoftware.contagiapp.eventi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    public EventsFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton new_event;
    String mailUtenteLoggato;
    TextInputEditText editText;
    RecyclerView rvEventi;
    ArrayList<Evento> listaEventi = new ArrayList<Evento>();
    ArrayList<Evento> listaEventiCreati = new ArrayList<Evento>();
    ArrayList<Evento> listaEventiIscritto = new ArrayList<Evento>();
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli eventi

    ArrayList<String> listaNomiEventi = new ArrayList<String>();
    ArrayList<String> listaNomiEventiCitta = new ArrayList<String>();
    ArrayList<Evento> listaEventiTrovati = new ArrayList<Evento>();
    ArrayList<String> listaIdEventi = new ArrayList<String>();


    private boolean switchiscritto = false;
    private boolean switchcreato = false;

    private int ruolo;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_events, container, false);
        mailUtenteLoggato = getMailUtenteLoggato();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);

        final Switch iscritto = view.findViewById(R.id.switch1);
        final Switch creati = view.findViewById(R.id.switch2);
        rvEventi = view.findViewById(R.id.rvEventi);

        Log.d("listaEventiOnCreate", String.valueOf(listaEventi));
        EventAdapter adapterVuoto = new EventAdapter(listaEventi, getContext());
        rvEventi.setAdapter(adapterVuoto);


        caricaEventi();

        rvEventi.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvEventi, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                String idEventoSelezionato = idList.get(position);
                Log.i("idList: ", idEventoSelezionato);

                Fragment fragment;

                Bundle bundle = new Bundle();

                switch (ruolo){
                    case 0:
                        fragment = new ProfiloEventoFragment();
                        break;
                    case 1:
                        fragment = new EliminazionePartecipazioneEvento();
                        bundle.putBoolean("partenza", false);
                        break;
                    case 2:
                        fragment = new ProfiloEventoAdminFragment();
                        break;
                    default:
                        Log.e("ERROR", "SWITCH");
                        fragment = new ProfiloEventoFragment();
                }


                bundle.putString("idEvento", idEventoSelezionato);

                fragment.setArguments(bundle);

                //richiamo il fragment
                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.addToBackStack(null); //serve per tornare al fragment precedente
                fr.commit();

                iscritto.setChecked(false);
                creati.setChecked(false);

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        iscritto.setOnCheckedChangeListener(this);
        creati.setOnCheckedChangeListener(this);

        new_event = view.findViewById(R.id.floating_action_button);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsActivity.class);
                intent.putExtra("scelta", false);
                startActivity(intent);
            }
        });


        //RICERCA
        editText = view.findViewById(R.id.search_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        listaEventi.clear();
                        listaEventiIscritto.clear();
                        listaEventiCreati.clear();

                        listaEventiTrovati.clear();
                        listaIdEventi.clear();


                        ArrayList<String> nomeEventiTrovati;
                        ArrayList<String> cittaEventiTrovati;



                        nomeEventiTrovati = ricerca(editText.getText().toString(), listaNomiEventi);
                        cittaEventiTrovati= ricerca(editText.getText().toString(), listaNomiEventiCitta);

                        Log.d("listaInCuiCercare", String.valueOf(listaNomiEventi));
                        Log.d("eventiTrovati", String.valueOf(nomeEventiTrovati));
                        Log.d("listaInCuiCercareCitta", String.valueOf(listaNomiEventiCitta));
                        Log.d("eventiCittaTrovati", String.valueOf(cittaEventiTrovati));



                        //ottengo l'id dell'evento Trovato
                        for(int i = 0; i < nomeEventiTrovati.size(); i++){
                            db.collection("Eventi")
                                    .whereEqualTo("nome", nomeEventiTrovati.get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Evento eventoTrovato = document.toObject(Evento.class);

                                                listaEventiTrovati.add(eventoTrovato);
                                                listaIdEventi.add(eventoTrovato.getIdEvento());
                                            }
                                            Log.d("listaEventiTrovati", String.valueOf(listaEventiTrovati));
                                            Log.d("listaGruppiCreati_ID", String.valueOf(listaIdEventi));

                                            EventAdapter adapter = new EventAdapter(listaEventiTrovati, getContext());
                                            rvEventi.setAdapter(adapter);
                                            rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        }
                                    });
                        }
                        for(int i = 0; i < cittaEventiTrovati.size(); i++){
                            db.collection("Eventi")
                                    .whereEqualTo("citta", cittaEventiTrovati.get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Evento eventoTrovato = document.toObject(Evento.class);

                                                listaEventiTrovati.add(eventoTrovato);
                                                listaIdEventi.add(eventoTrovato.getIdEvento());
                                            }
                                            Log.d("listaEventiTrovati", String.valueOf(listaEventiTrovati));
                                            Log.d("listaGruppiCreati_ID", String.valueOf(listaIdEventi));

                                            EventAdapter adapter = new EventAdapter(listaEventiTrovati, getContext());
                                            rvEventi.setAdapter(adapter);
                                            rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        }
                                    });
                        }

                        hideSoftKeyboard(getActivity()); //nascode la tastiera dopo aver cliccato il tasto cerca nella tastiera
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void caricaEventiCreati(){
        idList.clear();
        listaEventiCreati.clear();
        listaNomiEventi.clear();
        listaNomiEventiCitta.clear();



        Log.d("sonocaricaEventiCreati", "caricaEventiCreati");

        db.collection("Eventi")
                .whereEqualTo("admin", mailUtenteLoggato)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                            String idEvento = documentSnapshot.getId();
                            Log.d("idEventoCreato", idEvento);


                            idList.add(idEvento);

                            Log.d("idListCreatiOutOfIF", String.valueOf(idList));

                            Evento evento = documentSnapshot.toObject(Evento.class);


                            try {
                                Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(evento.getData());
                                Date dataAttuale = new Date(System.currentTimeMillis());

                                if(dataEvento.compareTo(dataAttuale) >= 0 && ! listaEventiCreati.contains(evento.getIdEvento())){
                                    listaEventiCreati.add(evento);

                                    if(!listaNomiEventi.contains(evento.getNome())){
                                        listaNomiEventi.add(evento.getNome());
                                        Log.d("1listaNomiEventi", String.valueOf(listaNomiEventi));
                                    }
                                    if(!listaNomiEventiCitta.contains(evento.getCitta())){
                                        listaNomiEventiCitta.add(evento.getCitta());
                                        Log.d("2listaNomiEventi", String.valueOf(listaNomiEventiCitta));
                                    }

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                        Log.d("listaEventiCreatiOUTFor", String.valueOf(listaEventiCreati));

                        EventAdapter adapter = new EventAdapter(listaEventiCreati, getContext());
                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));

                        ruolo = 2;
                    }
                });

    }

    private void caricaEventi(){
        listaEventi.clear();
        idList.clear();

        Log.d("caricaEventi", "sonoCaricaEventi");

        db.collection("Eventi")
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
                                        && !evento.getAdmin().equals(mailUtenteLoggato)
                                        && !evento.getPartecipanti().contains(mailUtenteLoggato)) {

                                    Log.d("idList", String.valueOf(idList));

                                    listaEventi.add(evento);
                                    String id = documentSnapshot.getId();
                                    idList.add(id);

                                    if(!listaNomiEventi.contains(evento.getNome())){
                                        listaNomiEventi.add(evento.getNome());
                                    }

                                    if(!listaNomiEventiCitta.contains(evento.getCitta())){
                                        listaNomiEventiCitta.add(evento.getCitta());
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("listaEventiOutFor", String.valueOf(listaEventi));
                        EventAdapter adapter = new EventAdapter(listaEventi, getContext());

                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                        ruolo = 0;



                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("document", "Error document", e);
            }
        });

    }

    private void caricaEventiIscritto(){
        listaEventiIscritto.clear();
        idList.clear();
        listaNomiEventi.clear();
        listaNomiEventiCitta.clear();


        db.collection("Eventi")
                .whereArrayContains("partecipanti", mailUtenteLoggato)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Evento evento = documentSnapshot.toObject(Evento.class);

                            try {
                                Date dataEvento = new SimpleDateFormat("dd/MM/yyyy").parse(evento.getData());
                                Date dataAttuale = new Date(System.currentTimeMillis());

                                if(dataEvento.compareTo(dataAttuale) >= 0 && !evento.getAdmin().equals(mailUtenteLoggato)) {

                                    Log.d("idListIscritto", String.valueOf(idList));
                                    listaEventiIscritto.add(evento);
                                    Log.d("listaEventiIscritto", String.valueOf(listaEventiIscritto));
                                    String id = documentSnapshot.getId();
                                    idList.add(id);

                                    if(!listaNomiEventi.contains(evento.getNome())){
                                        listaNomiEventi.add(evento.getNome());
                                    }

                                    if(!listaNomiEventiCitta.contains(evento.getCitta())){
                                        listaNomiEventiCitta.add(evento.getCitta());
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Log.d("listaEventiIscritOutFor", String.valueOf(listaEventiIscritto));
                        EventAdapter adapter = new EventAdapter(listaEventiIscritto, getContext());

                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));



                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("document", "Error document", e);
            }
        });

        Log.d("listaEventiIscrOutAll", String.valueOf(listaEventiIscritto));
        ruolo = 1;
    }

    private ArrayList<String> ricerca(String testoInserito, ArrayList<String> listaInCuiCercare){


        ArrayList<String> elementiTrovati = new ArrayList<String>();
        for(int i=0; i < listaInCuiCercare.size(); i++){
            if(listaInCuiCercare.get(i).toLowerCase().contains(testoInserito.toLowerCase())){
                elementiTrovati.add(listaInCuiCercare.get(i));
            }
        }
        return elementiTrovati;
    }


    //funzione che nasconde la tastiera
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    private String getMailUtenteLoggato(){
        Utente utente;
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getActivity().getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch1:
                switchiscritto = isChecked;

                if(isChecked) {
                    if (switchcreato) {
                        Toast.makeText(getContext(), getContext().getText(R.string.impossible_operation), Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                        switchiscritto = !switchiscritto;
                    } else caricaEventiIscritto();
                }

                if(!isChecked && !switchcreato) caricaEventi();
                break;
            case R.id.switch2:
                switchcreato = isChecked;

                if(isChecked) {
                    if(switchiscritto) {
                        Toast.makeText(getContext(),getContext().getText(R.string.impossible_operation),Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                        switchcreato = !switchcreato;
                    } else caricaEventiCreati();
                }

                if(!isChecked && !switchiscritto) caricaEventi();
                break;
        }
    }

    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private EventsFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final EventsFragment.RecyclerTouchListener.ClickListener clickListener) {
            this.clickListener = clickListener;
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
}
