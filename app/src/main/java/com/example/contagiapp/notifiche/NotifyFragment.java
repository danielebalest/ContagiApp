package com.example.contagiapp.notifiche;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.data.amici.ProfiloUtentiActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti

    public NotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_notify, container, false);

        final RecyclerView recyclerView =  view.findViewById(R.id.rvNotify);


        String mailUtenteLoggato = getMailUtenteLoggato();
        //Otteniamo la lista della mail degli amici
        db.collection("Utenti")
                .document(mailUtenteLoggato).get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        ArrayList<String> listaMail = (ArrayList<String>) document.get("richiesteRicevute");

                        Log.d("lista", String.valueOf(listaMail));
                        getRichieste(listaMail, recyclerView);
                    }
                });
        return view;
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
                            Log.d("Nome utente", String.valueOf(user.getNome()));


                            utenti.add(user);
                            Log.d("richiesteSize", String.valueOf(utenti.size()));
                            RichiesteAdapter adapter = new RichiesteAdapter(utenti);

                            String id = user.getMail();
                            idList.add(id);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private NotifyFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final NotifyFragment.RecyclerTouchListener.ClickListener clickListener) {
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
