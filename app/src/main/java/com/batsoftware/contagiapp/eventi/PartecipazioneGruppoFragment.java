package com.batsoftware.contagiapp.eventi;

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

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.gruppi.Gruppo;
import com.batsoftware.contagiapp.gruppi.GruppoAdapter;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class PartecipazioneGruppoFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Utente utente;
    RecyclerView rvGruppiCreati;
    ArrayList<Gruppo> listaGruppiCreati;
    ArrayList<Gruppo> listaGruppiPartecipante;
    ArrayList<String> listaIdGruppiCreati = new ArrayList<String>();
    ArrayList<String> listaGruppi = new ArrayList<String>();
    String idEvento;
    ArrayList<String> listaPartecipantiEvento;
    int numPostiDisponibili;

    public PartecipazioneGruppoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_partecipazione_gruppo, container, false);
        rvGruppiCreati = view.findViewById(R.id.rvGruppiCreati);

        Bundle bundle = getArguments();
        idEvento = bundle.getString("idEvento");
        Log.d("bundle", String.valueOf(idEvento));


        db.collection("Eventi")
                .document(idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Evento evento = documentSnapshot.toObject(Evento.class);

                        numPostiDisponibili = evento.getNumeroMaxPartecipanti() - evento.getPartecipanti().size();

                        if(documentSnapshot.exists()){
                            listaGruppi = evento.getGruppiPartecipanti();
                            listaPartecipantiEvento = evento.getPartecipanti();
                            Log.d("listaPartecipantiEvento", String.valueOf(listaPartecipantiEvento));
                        }

                    }
                });

        caricaGruppi();
        // Inflate the layout for this fragment
        return view;
    }

    private void tornareIndietro(int i) {

        Fragment fragment = null;

        switch (i) {
            case 1:
                fragment = new EliminazionePartecipazioneEvento();
                break;
            case 2:
                fragment = new ProfiloEventoFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("idEvento", idEvento);
        bundle.putBoolean("partenza", false);

        fragment.setArguments(bundle);

        //richiamo il fragment

        FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.container,fragment);
        fr.addToBackStack(null); //serve per tornare al fragment precedente
        fr.commit();
    }


    private void caricaGruppi() {
        listaGruppiCreati = new ArrayList<Gruppo>();
        listaGruppiPartecipante = new ArrayList<Gruppo>();
        String mailAdmin = getMailUtenteLoggato();


        db.collection("Gruppo")
                .whereEqualTo("admin", mailAdmin)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);

                            String statoGruppo = String.valueOf(gruppo.getStatoGruppo());
                            if(gruppo.getNroPartecipanti() <= numPostiDisponibili &&
                                    (statoGruppo.equals("verde") || statoGruppo.equals("giallo"))) {
                                String id = documentSnapshot.getId();

                                listaIdGruppiCreati.add(id);
                                listaGruppiCreati.add(gruppo);
                            }
                        }


                        if(listaGruppiCreati.isEmpty()){
                            Toast.makeText(getContext(),getText(R.string.no_group_msg),Toast.LENGTH_LONG).show();
                        }

                        if(listaIdGruppiCreati.size()!=0) {
                            GruppoAdapter adapter = new GruppoAdapter(listaGruppiCreati);
                            rvGruppiCreati.setAdapter(adapter);
                            rvGruppiCreati.setLayoutManager(new LinearLayoutManager(getActivity()));
                            rvGruppiCreati.addOnItemTouchListener(new PartecipazioneGruppoFragment.RecyclerTouchListener(getActivity(), rvGruppiCreati, new PartecipazioneGruppoFragment.RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Log.i("lista1: ", String.valueOf(listaIdGruppiCreati));
                                    final String idGruppoSelezionato = listaIdGruppiCreati.get(position);
                                    Log.i("idList: ", idGruppoSelezionato);


                                    if(!listaGruppi.contains(idGruppoSelezionato)){
                                        listaGruppi.add(idGruppoSelezionato);
                                        Toasty.success(getActivity(), getText(R.string.registration_completed), Toast.LENGTH_SHORT).show();

                                        db.collection("Gruppo")
                                                .document(idGruppoSelezionato)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        //recupero tutti i partecipanti al gruppo
                                                        //aggiungo questi alla lista dei partecipanti all'evento (metterndoci i controlli se quell'email già esiste)

                                                        if(documentSnapshot.exists()){
                                                            Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                                                            final ArrayList<String> listaPartecipantiGruppo = gruppo.getPartecipanti();
                                                            Log.d("listaPartecipantiGruppo", String.valueOf(listaPartecipantiGruppo));

                                                            //aggiunta alla lista dei partecipanti all'evento i partecipanti del gruppo

                                                            for(int i = 0; i < listaPartecipantiGruppo.size(); i++) {
                                                                boolean cond = true;
                                                                for(int j = 0; j < listaPartecipantiEvento.size(); j++) {
                                                                    if(listaPartecipantiGruppo.get(i).equals(listaPartecipantiEvento.get(j))) {
                                                                        cond = false;
                                                                    }
                                                                }

                                                                if(cond) {
                                                                    listaPartecipantiEvento.add(listaPartecipantiGruppo.get(i));
                                                                }
                                                            }

                                                            db.collection("Eventi")
                                                                    .document(idEvento)
                                                                    .update("partecipanti", listaPartecipantiEvento);
                                                            tornareIndietro(1);
                                                        }
                                                    }
                                                });

                                        db.collection("Eventi")
                                                .document(idEvento)
                                                .update("gruppiPartecipanti", listaGruppi);
                                    }else Toasty.warning(getActivity(), getText(R.string.group_already_registered), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onLongClick(View view, int position) {

                                }

                            }));
                        } else {
                            tornareIndietro(2);
                            Toast.makeText(getContext().getApplicationContext(), getText(R.string.groups_you_created_exceed_maximum), Toast.LENGTH_LONG).show();
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
        private PartecipazioneGruppoFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PartecipazioneGruppoFragment.RecyclerTouchListener.ClickListener clickListener) {
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