package com.batsoftware.contagiapp.amici;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.UserAdapter;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }



    private Button visualizza_profilo;
    TextInputEditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String mailUtenteLoggato;
    private FloatingActionButton aggiungi_amici;
    private RecyclerView recyclerView;
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti
    ArrayList<String> listaMail;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view;
        view = inflater.inflate(R.layout.fragment_friends, container, false);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);


        mailUtenteLoggato = getMailUtenteLoggato();


        editText = view.findViewById(R.id.search_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
                    intent.putExtra("aggiungere", "no");
                    intent.putExtra("cerca", editText.getText().toString());
                    intent.putExtra("mail", mailUtenteLoggato);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        aggiungi_amici = view.findViewById(R.id.FAB_friends);
        aggiungi_amici.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  addFriends();
              }
          });
        recyclerView =  view.findViewById(R.id.recyclerView);
        final TextView tvListaMail = view.findViewById(R.id.tvTuoiAmici);



        String mailUtenteLoggato = getMailUtenteLoggato();

        //Otteniamo la lista della mail degli amici
        db.collection("Utenti")
                .document(mailUtenteLoggato).get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        listaMail = (ArrayList<String>) document.get("amici");
                        if(listaMail.isEmpty()){
                            tvListaMail.setText(getContext().getText(R.string.you_dont_have_any_friends_yet));
                        }
                        Log.d("lista", String.valueOf(listaMail));

                        if(listaMail.isEmpty()) {
                            TextView am = view.findViewById(R.id.tvTuoiAmici);
                            am.setText(getContext().getText(R.string.you_dont_have_any_friends_yet));
                        } else getFriends(listaMail);
                    }
                });
        return view;
    }



    public void addFriends(){
        Intent addFriendsIntent = new Intent(getActivity(), AddFriendsActivity.class);
        addFriendsIntent.putExtra("aggiungere", "si");
        addFriendsIntent.putExtra("listaMailAmici", listaMail);
        addFriendsIntent.putExtra("mailUtenteLoggato", mailUtenteLoggato);
        startActivity(addFriendsIntent);
    }


    public void getFriends(ArrayList<String> listaAmici){
        /*
        metodo che svolge le seguenti operazioni:
         1)date in input le mail degli amici ottiene, per ciascuno, i seguenti dati dal database: nome, cognome, mail
         2)crea per ognuno un nuovo tipo Utente che aggiunge ad una lista
         3) passa la lista all'adapter del recycler View che poi permetterà la visualizzazione della lista di CardView degli amici sull'app
         */
        final ArrayList<Utente> amici = new ArrayList<Utente>();


        db.collection("Utenti")
                .whereIn("mail",listaAmici)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Utente user = documentSnapshot.toObject(Utente.class);

                            String id = documentSnapshot.getId();
                            idList.add(id);
                            amici.add(user);
                        }


                        UserAdapter adapter = new UserAdapter(amici);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                String idUtenteSelezionato = idList.get(position);
                                Log.d("position", String.valueOf(position));
                                //Log.d("idList: ", idUtenteSelezionato);

                                Intent profiloIntent = new Intent(getActivity(), ProfiloUtentiActivity.class);
                                profiloIntent.putExtra("id", idUtenteSelezionato);
                                profiloIntent.putExtra("amico", "si");
                                profiloIntent.putExtra("mailLoggato", mailUtenteLoggato);
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
                Log.d("Error", "Error");
            }
        });
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

    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private FriendsFragment.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendsFragment.RecyclerTouchListener.ClickListener clickListener) {
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

