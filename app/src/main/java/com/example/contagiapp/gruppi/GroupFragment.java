package com.example.contagiapp.gruppi;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    TextInputEditText editText;

    ArrayList<Gruppo> listaGruppi;
    ArrayList<Gruppo> listaGruppiPartecipante;
    ArrayList<String> listaIdGruppiCreati = new ArrayList<String>();
    ArrayList<String> listaIdGruppiPartecipante = new ArrayList<String>();

    RecyclerView rvGruppiCreati;
    RecyclerView rvGruppiPartecipante;
    TextView tvTuoiGruppi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_group, container, false);

        tvTuoiGruppi = view.findViewById(R.id.tvTuoiGruppi);
        rvGruppiCreati = view.findViewById(R.id.rvGruppiCreati);
        rvGruppiPartecipante = view.findViewById(R.id.rvGruppiPartecipante);
        caricaGruppi();


        crea_gruppo = view.findViewById(R.id.FAB_groups);
        crea_gruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreaGruppoActivity.class);
                startActivity(intent);
            }
        });

        editText = view.findViewById(R.id.search_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //da inserire metodo per la ricerca
                    return true;
                }

                return false;
            }
        });




        return view;
    }


    private void caricaGruppi() {
        listaGruppi = new ArrayList<Gruppo>();
        listaGruppiPartecipante = new ArrayList<Gruppo>();
        String mailAdmin = getMailUtenteLoggato();


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
                    listaGruppi.add(gruppo);
                    Log.d("Lista Gruppi", String.valueOf(listaGruppi));
                    Log.d("Lista_ID", String.valueOf(listaIdGruppiCreati));

                }
                if(listaGruppi.isEmpty()){
                    tvTuoiGruppi.setText("Non hai ancora nessun gruppo. Crea subito uno");
                }
                GruppoAdapter adapter = new GruppoAdapter(listaGruppi);
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
        }); //toDo onFailure

        db.collection("Gruppo").whereArrayContains("partecipanti", getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);

                            Log.d("gruppo.getPartecipanti", String.valueOf(gruppo.getPartecipanti()));
                            gruppo.setPartecipanti(gruppo.getPartecipanti());
                            gruppo.aggiornaNroPartecipanti(gruppo.getPartecipanti());

                            String id = documentSnapshot.getId();
                            listaIdGruppiPartecipante.add(id);
                            listaGruppiPartecipante.add(gruppo);
                            Log.d("ListaGruppiPartecipante", String.valueOf(listaGruppiPartecipante));
                            Log.d("Lista_ID", String.valueOf(listaIdGruppiCreati));

                            if(!listaGruppiPartecipante.isEmpty()){
                                tvTuoiGruppi.setText("I tuoi gruppi");
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
                    }
                });


    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
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
