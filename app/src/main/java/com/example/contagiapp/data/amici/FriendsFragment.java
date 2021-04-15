package com.example.contagiapp.data.amici;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.contagiapp.utente.Utente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

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

    ArrayList<Utente> utenti;

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



        RecyclerView recyclerView =  view.findViewById(R.id.recyclerView);

        Utente u1 = new Utente();
        u1.setNome("Pinco");
        u1.setCognome("Pallino");

        Utente u2 = new Utente();
        u2.setNome("Roberto");
        u2.setCognome("Pillo");


        //Todo: devo ottenere questi dati dal db. Vedere come esempio
        utenti = new ArrayList<>();
        utenti.add(u1);
        utenti.add(u2);
/*
        UserAdapter adapter = new UserAdapter(utenti);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, true));

*/
      return view;

    }



    //Crasha porca puttana
    public void visualizzaMieiAmici(View view){
        //textViewFriends = (TextView) view.findViewById(R.id.textViewAmici);
        Map<String, Object> amici = new HashMap<>();
       /*db.collection("Amici").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String mail = documentSnapshot.getString("amici");
                    data = data + mail + "\n";
                }
                //textViewFriends.setText(data);
            }
        });*/

    }

    public void addFriends(){
        Intent addFriendsIntent = new Intent(getActivity(), AddFriendsActivity.class);
        startActivity(addFriendsIntent);

    }


}
