 package com.batsoftware.contagiapp.amici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.UserAdapter;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Collections;
 /*
* questa activity permette la visualizzazione degli utenti della piattaforma.
* E' possibile cliccarci sopra e passare alla  ProfiloUtentiActivity per visulizzare il profilo
 */


public class AddFriendsActivity extends AppCompatActivity  {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    String mailUtenteLoggato;
    ArrayList<String> idList = new ArrayList<>(); //lista che conterrà gli id cioè le mail degli utenti
    ArrayList<Utente> utenti = new ArrayList<>();
    ArrayList<String> amiciLoggato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);


        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        db.setFirestoreSettings(settings);

        mailUtenteLoggato = getMailUtenteLoggato();

        Bundle extras = getIntent().getExtras();
        if(extras.getString("aggiungere").equals("si")) {
            ArrayList<String> listaMailAmici;

            if(extras.getStringArrayList("listaMailAmici") == null) {
                listaMailAmici = new ArrayList<>();
            } else listaMailAmici = extras.getStringArrayList("listaMailAmici");

            Log.d("mailUtenteLoggato8", String.valueOf(mailUtenteLoggato));

            listaMailAmici.add(mailUtenteLoggato);
            listaMailAmici.removeAll(Collections.singleton(null));
            Log.d("listaMailAmici1", String.valueOf(listaMailAmici));
            caricaUtentiNonAmici(listaMailAmici, mailUtenteLoggato);
        } else {
            caricaAmiciCercati(extras);
        }
    }

    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMailPath();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }

    public void caricaUtentiNonAmici(ArrayList<String> listaAmici, final String mailUtenteLoggato) {
        utenti.clear();
        idList.clear();

        recyclerView = findViewById(R.id.rvUtenti);

        db.collection("Utenti")
                .whereNotIn(FieldPath.documentId(), listaAmici)
                //.whereNotEqualTo(FieldPath.documentId(), a.get(1))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Utente user = documentSnapshot.toObject(Utente.class);

                    String id = documentSnapshot.getId();
                    idList.add(id);
                    utenti.add(user);
                }

                Log.d("AllUsers", String.valueOf(utenti));

                UserAdapter adapter = new UserAdapter(utenti);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AddFriendsActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String idUtenteSelezionato = idList.get(position);
                        Log.i("idList: ", idUtenteSelezionato);


                        Intent profiloIntent = new Intent(AddFriendsActivity.this, ProfiloUtentiActivity.class );
                        profiloIntent.putExtra("id", idUtenteSelezionato);
                        profiloIntent.putExtra("mailUtenteLoggato", mailUtenteLoggato);
                        profiloIntent.putExtra( "amico", "no");
                        startActivity(profiloIntent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));
            }
        });
    }

    public void caricaAmiciCercati(final Bundle extras) {
        recyclerView = findViewById(R.id.rvUtenti);
        final String cerca = extras.getString("cerca");
        final String mail = extras.getString("mail");

        db.collection("Utenti").document(mail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                amiciLoggato = (ArrayList<String>) documentSnapshot.get("amici");
            }
        });

        db.collection("Utenti").whereNotEqualTo("mailPath", mail).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Utente user = documentSnapshot.toObject(Utente.class);
                            String id = documentSnapshot.getId();

                            String trov = user.getNome() + " " + user.getCognome();
                            if (trov.toLowerCase().contains(cerca.toLowerCase())) {
                                idList.add(id);
                                utenti.add(user);
                            }
                        }

                        UserAdapter adapter = new UserAdapter(utenti);

                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AddFriendsActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                final String idUtenteSelezionato = idList.get(position);  //finalmente cristo
                                Log.i("idList: ", idUtenteSelezionato);

                                final Intent profiloIntent = new Intent(AddFriendsActivity.this, ProfiloUtentiActivity.class );
                                profiloIntent.putExtra("id", idUtenteSelezionato);
                                profiloIntent.putExtra("mailUtenteLoggato", mail);

                                if(amiciLoggato.contains(idUtenteSelezionato)) {
                                    profiloIntent.putExtra( "amico", "si");
                                } else {
                                    profiloIntent.putExtra( "amico", "no");
                                }

                                startActivity(profiloIntent);
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }

                        }));
                    }
                });
    }

    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
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