 package com.example.contagiapp.data.amici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
* questa activity permette la visualizzazione degli utenti della piattaforma.
* E' possibile cliccarci sopra e passare alla  ProfiloUtentiActivity per visulizzare il profilo
 */


public class AddFriendsActivity extends AppCompatActivity  {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView textViewData;
    private ListView listView;
    private RecyclerView recyclerView;

    ArrayList<String> list = new ArrayList<String>();   //lista che conterrà le informazioni principali degli utenti
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti
    ArrayAdapter<String> arrayAdapter;

    ArrayList<Utente> utenti = new ArrayList<Utente>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        textViewData = findViewById(R.id.TextViewAllUser);

        loadUser();
    }


    public void loadUser() {
        Map<String, Object> user = new HashMap<>();
        //listView = findViewById(R.id.ListViewAllUser);
        recyclerView = findViewById(R.id.rvUtenti);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        db.collection("Utenti").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Utente user = documentSnapshot.toObject(Utente.class);
                    String nome = user.getNome();
                    String cognome = user.getCognome();

                    String id = documentSnapshot.getId();
                    idList.add(id);
                    list.add(nome + " " + cognome);
                    utenti.add(user);

                }

                UserAdapter adapter = new UserAdapter(utenti);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(AddFriendsActivity.this, LinearLayoutManager.VERTICAL, true));
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AddFriendsActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String idUtenteSelezionato = idList.get(position);  //finalmente cristo
                        Log.i("idList: ", idUtenteSelezionato);
                        Toast.makeText(getApplicationContext(), idUtenteSelezionato, Toast.LENGTH_LONG).show();

                        Intent profiloIntent = new Intent(AddFriendsActivity.this, ProfiloUtentiActivity.class );
                        profiloIntent.putExtra("id", idUtenteSelezionato);
                        startActivity(profiloIntent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));




            }
        });//Todo: onFailure
    }



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