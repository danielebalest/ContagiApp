package com.batsoftware.contagiapp.gruppi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.TextView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    public GroupFragment() {
    }

    private Utente utente;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton crea_gruppo;
    String mailUtenteLoggato;

    TextInputEditText editTextSearch;

    ArrayList<Gruppo> listaGruppiCreati;
    ArrayList<Gruppo> listaGruppiPartecipante;
    ArrayList<String> listaIdGruppiCreati = new ArrayList<String>();
    ArrayList<String> listaIdGruppiPartecipante = new ArrayList<String>();


    ArrayList<Gruppo> listaGruppiCreatiTrovati = new ArrayList<Gruppo>();
    ArrayList<Gruppo> listaGruppiPartecipanteTrovati = new ArrayList<Gruppo>();
    ArrayList<String> listaNomiGruppiCreati = new ArrayList<String>(); //mi serve per fare la ricerca sul nome del gruppo
    ArrayList<String> listaNomiGruppiPartecipante = new ArrayList<String>(); //mi serve per fare la ricerca sul nome del gruppo

    RecyclerView rvGruppiCreati;
    RecyclerView rvGruppiPartecipante;
    TextView tvTuoiGruppi;
    TextView tvGruppiPartecipante;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_group, container, false);



        mailUtenteLoggato = getMailUtenteLoggato();
        tvTuoiGruppi = view.findViewById(R.id.tvTuoiGruppi);
        tvGruppiPartecipante = view.findViewById(R.id.tvGruppiPartecipante);
        rvGruppiCreati = view.findViewById(R.id.rvGruppiCreati);
        rvGruppiPartecipante = view.findViewById(R.id.rvGruppiPartecipante);
        editTextSearch = view.findViewById(R.id.search_field_group);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);


        Log.d("mailUtLog", mailUtenteLoggato);
        caricaGruppi();

        crea_gruppo = view.findViewById(R.id.FAB_groups);
        crea_gruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreaGruppoActivity.class);
                startActivity(intent);
            }
        });



        return view;
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


    private void caricaGruppi() {
        listaGruppiCreati = new ArrayList<Gruppo>();
        listaGruppiPartecipante = new ArrayList<Gruppo>();

        final String mailAdmin = mailUtenteLoggato;
        Log.d("mailUtLogAd", mailAdmin);


        db.collection("Gruppo").whereEqualTo("admin", mailAdmin)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                    String id = documentSnapshot.getId();


                    gruppo.aggiornaNroPartecipanti(gruppo.getPartecipanti());
                    listaIdGruppiCreati.add(id);

                    if(!listaNomiGruppiCreati.contains(gruppo.getNomeGruppo())){
                        listaNomiGruppiCreati.add(gruppo.getNomeGruppo());
                    }


                    listaGruppiCreati.add(gruppo);
                    Log.d("Lista_Gruppi", String.valueOf(listaGruppiCreati));
                    Log.d("Lista_ID", String.valueOf(listaIdGruppiCreati));
                    Log.d("ListaNomiGruppiCreat", String.valueOf(listaNomiGruppiCreati));

                }

                GruppoAdapter adapter = new GruppoAdapter(listaGruppiCreati);
                rvGruppiCreati.setAdapter(adapter);
                rvGruppiCreati.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvGruppiCreati.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvGruppiCreati, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Log.i("lista1: ", String.valueOf(listaIdGruppiCreati));
                        String idGruppoSelezionato = listaIdGruppiCreati.get(position);
                        Log.i("idList: ", idGruppoSelezionato);


                        ProfiloGruppoAdminFragment fragment = new ProfiloGruppoAdminFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("idGruppo", idGruppoSelezionato);

                        fragment.setArguments(bundle);

                        //richiamo il fragment
                        FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                        fr.replace(R.id.container,fragment);
                        fr.addToBackStack(null); //serve per tornare al fragment precedente
                        fr.commit();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));

            }
        });


        db.collection("Gruppo").whereArrayContains("partecipanti", mailUtenteLoggato)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);

                            if(!gruppo.getAdmin().equals(mailAdmin)) {
                                Log.d("gruppo.getPartecipanti", String.valueOf(gruppo.getPartecipanti()));
                                gruppo.setPartecipanti(gruppo.getPartecipanti());
                                gruppo.aggiornaNroPartecipanti(gruppo.getPartecipanti());

                                String id = documentSnapshot.getId();
                                listaIdGruppiPartecipante.add(id);
                                listaGruppiPartecipante.add(gruppo);


                                Log.d("ListaGruppiPartecipante", String.valueOf(listaGruppiPartecipante));
                                Log.d("Lista_ID", String.valueOf(listaIdGruppiCreati));

                                if(!listaNomiGruppiPartecipante.contains(gruppo.getNomeGruppo())){
                                    listaNomiGruppiPartecipante.add(gruppo.getNomeGruppo());
                                }
                            }


                        }//fine for
                        if(listaGruppiPartecipante.isEmpty()){
                            tvGruppiPartecipante.setVisibility(View.GONE);
                        }

                        if(listaGruppiCreati.isEmpty() && listaGruppiPartecipante.isEmpty()){
                            tvTuoiGruppi.setText(R.string.no_group_msg);
                        }

                        if(listaGruppiCreati.isEmpty() && ! listaGruppiPartecipante.isEmpty()){
                            tvTuoiGruppi.setVisibility(View.GONE);
                        }



                        GruppoAdapter adapter = new GruppoAdapter(listaGruppiPartecipante);
                        rvGruppiPartecipante.setAdapter(adapter);
                        rvGruppiPartecipante.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvGruppiPartecipante.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvGruppiPartecipante, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Log.i("lista2: ", String.valueOf(listaIdGruppiPartecipante));
                                String idGruppoSelezionato = listaIdGruppiPartecipante.get(position);
                                Log.i("idGruppoSelezionato: ", idGruppoSelezionato);

                                ProfiloGruppoFragment fragment = new ProfiloGruppoFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("idGruppo", idGruppoSelezionato);

                                fragment.setArguments(bundle);

                                //richiamo il fragment
                                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                                fr.replace(R.id.container,fragment);
                                fr.addToBackStack(null); //serve per tornare al fragment precedente
                                fr.commit();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }

                        }));

                    }
                });

        //RICERCA
        if(!editTextSearch.toString().isEmpty()){


            editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        listaGruppiCreati.clear();
                        listaGruppiPartecipante.clear();
                        listaGruppiCreatiTrovati.clear();
                        listaGruppiPartecipanteTrovati.clear();
                        listaIdGruppiCreati.clear();
                        listaIdGruppiPartecipante.clear();

                        ArrayList<String> nomeGruppiCreatiTrovati;
                        ArrayList<String> nomeGruppiPartecipantiTrovati;
                        nomeGruppiCreatiTrovati = ricerca(editTextSearch.getText().toString(), listaNomiGruppiCreati);
                        nomeGruppiPartecipantiTrovati = ricerca(editTextSearch.getText().toString(), listaNomiGruppiPartecipante);

                        Log.d("listaInCuiCercare", String.valueOf(listaNomiGruppiCreati));
                        Log.d("gruppiTrovati", String.valueOf(nomeGruppiCreatiTrovati));


                        //ottengo l'id del gruppo creato Trovato
                        for(int i = 0; i < nomeGruppiCreatiTrovati.size(); i++){
                            db.collection("Gruppo")
                                    .whereEqualTo("nomeGruppo", nomeGruppiCreatiTrovati.get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Gruppo gruppoTrovato = document.toObject(Gruppo.class);
                                                gruppoTrovato.aggiornaNroPartecipanti(gruppoTrovato.getPartecipanti());
                                                listaGruppiCreatiTrovati.add(gruppoTrovato);
                                                listaIdGruppiCreati.add(gruppoTrovato.getIdGruppo());
                                            }
                                            Log.d("listaGruppiTrovati", String.valueOf(listaGruppiCreatiTrovati));
                                            Log.d("listaGruppiCreati_ID", String.valueOf(listaIdGruppiCreati));

                                            GruppoAdapter adapter = new GruppoAdapter(listaGruppiCreatiTrovati);
                                            rvGruppiCreati.setAdapter(adapter);
                                            rvGruppiCreati.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        }
                                    });
                        }

                        //ottengo l'id del gruppo Trovato a cui partecipo
                        for(int i = 0; i < nomeGruppiPartecipantiTrovati.size(); i++){
                            db.collection("Gruppo")
                                    .whereEqualTo("nomeGruppo", nomeGruppiPartecipantiTrovati.get(i))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Gruppo gruppoTrovato = document.toObject(Gruppo.class);
                                                gruppoTrovato.aggiornaNroPartecipanti(gruppoTrovato.getPartecipanti());

                                                listaGruppiPartecipanteTrovati.add(gruppoTrovato);
                                                listaIdGruppiPartecipante.add(gruppoTrovato.getIdGruppo());
                                            }
                                            Log.d("listaGruppiPartTrovati", String.valueOf(listaGruppiPartecipanteTrovati));
                                            Log.d("listaGruppiParte_ID", String.valueOf(listaIdGruppiPartecipante));

                                            GruppoAdapter adapter = new GruppoAdapter(listaGruppiPartecipanteTrovati);
                                            rvGruppiPartecipante.setAdapter(adapter);
                                            rvGruppiPartecipante.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        }
                                    });
                        }

                        hideSoftKeyboard(getActivity()); //nascode la tastiera dopo aver cliccato il tasto cerca nella tastiera
                        return true;
                    }
                    return false;
                }
            });
        }
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

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerTouchListener.ClickListener clickListener) {
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
