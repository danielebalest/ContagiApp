package com.example.contagiapp.data.amici;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.gruppi.CreaGruppoActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }



    private Button visualizza_profilo;
    ListView listView;
    TextInputEditText editText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView textViewFriends;
    private FloatingActionButton aggiungi_amici;
    private RecyclerView recyclerView;

    ArrayList<Utente> amici;
    ArrayList<String> listaAmici = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_friends, container, false);




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
                        ArrayList<String> listaMail = (ArrayList<String>) document.get("amici");
                        if(listaMail.isEmpty()){
                            tvListaMail.setText("Non hai ancora nessun amico");
                        }
                        Log.d("lista", String.valueOf(listaMail));
                        getFriends(listaMail);
                    }
                });
        return view;
    }



    public void addFriends(){
        Intent addFriendsIntent = new Intent(getActivity(), AddFriendsActivity.class);
        startActivity(addFriendsIntent);
    }

    public void getFriends(ArrayList<String> listaAmici){
        /*
        metodo che svolge le seguenti operazioni:
         1)date in input le mail degli amici ottiene, per ciascuno, i seguenti dati dal database: nome, cognome, mail
         2)crea per ognuno un nuovo tipo Utente che aggiunge ad una lista
         3) passa la lista all'adapter del recycler View che poi permetterà la visualizzazione della lista di CardView degli amici sull'app
         */
        amici = new ArrayList<Utente>();
        for(int i=0; i < listaAmici.size(); i++){
            db.collection("Utenti")
                    .document(listaAmici.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Utente user = new Utente();
                            user.setNome(documentSnapshot.getString("nome"));
                            user.setCognome(documentSnapshot.getString("cognome"));
                            user.setMail(documentSnapshot.getString("mail"));
                            Log.d("Nome utente", String.valueOf(user.getNome()));


                            amici.add(user);
                            Log.d("amiciSize", String.valueOf(amici.size()));
                            UserAdapter adapter = new UserAdapter(amici);

                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, true));
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
}
