package com.example.contagiapp.data.amici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.service.autofill.AutofillService;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
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


public class AddFriendsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView textViewData;
    private ListView listView;

    ArrayList<String> list = new ArrayList<String>();   //lista che conterrà le informazioni principali degli utenti
    ArrayList<String> idList = new ArrayList<String>(); //lista che conterrà gli id cioè le mail degli utenti
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        textViewData = findViewById(R.id.TextViewAllUser);

        loadUser2();
        visualizzaProfilo(listView);
    }


    public void loadUser2() {
        Map<String, Object> user = new HashMap<>();
        listView = findViewById(R.id.ListViewAllUser);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);

        db.collection("Utenti").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Utente user = documentSnapshot.toObject(Utente.class);
                    String nome = user.getNome();
                    String cognome = user.getCognome();

                    /*
                    String nome = documentSnapshot.getString("nome");
                    String cognome = documentSnapshot.getString("cognome");
                    */


                    String id = documentSnapshot.getId();
                    idList.add(id);
                    list.add(nome + " " + cognome);

                }

                listView.setAdapter(arrayAdapter);
            }
        });//Todo: onFailure
    }



    public void visualizzaProfilo(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val =  parent.getAdapter().getItem(position).toString(); //recucupera contenuto della listView

                String idUtenteSelezionato = idList.get(position);  //finalmente cristo
                Log.i("idList: ", idUtenteSelezionato);
                Toast.makeText(getApplicationContext(), idUtenteSelezionato, Toast.LENGTH_LONG).show();


                Intent profiloIntent = new Intent(AddFriendsActivity.this, ProfiloUtentiActivity.class );
                profiloIntent.putExtra("id", idUtenteSelezionato);
                startActivity(profiloIntent);
            }
        });
    }




}