package com.example.contagiapp.data.amici;

import android.content.Intent;
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



        RecyclerView recyclerView =  view.findViewById(R.id.recyclerView);

        String mailUtenteLoggato;

        //Otteniamo la lista della mail degli amici
        db.collection("Utenti")
                .document("abbbbaaaa@gmail.com").get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        ArrayList<String> listaMail = (ArrayList<String>) document.get("amici");
                        Log.d("lista", String.valueOf(listaMail));
                    }
                });



        ArrayList<Utente> aaaaaa = new ArrayList<Utente>();
        Utente a1 = new Utente();
        Utente a2 = new Utente();

        a1.setMail("chiusura@gmail.com");
        //Todo: devo ottenere questi dati dal db. Vedere come esempio
        Utente u1 = new Utente();
        u1.setNome("Pinco");
        u1.setCognome("Pallino");

        Utente u2 = new Utente();
        u2.setNome("Roberto");
        u2.setCognome("Pillo");

        amici = new ArrayList<Utente>();
        amici.add(a1);
        amici.add(u2);



        UserAdapter adapter = new UserAdapter(amici);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, true));


      getUtente("dani@gmail.com");
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

    public void getUtente(String mailAmico){ //ricordati che deve essere Utente e non void
        //final Utente user = null;
        db.collection("Utenti")
                .whereEqualTo(String.valueOf(getId()), mailAmico) //SICURAMENTE QUESTO NON VA BENE
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                Log.d("data", data);
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String nome = documentSnapshot.getString("nome");
                    String cognome = documentSnapshot.getString("cognome");
                    String mail = documentSnapshot.getString("mail");
                    data = data + "NOME: " + nome + " COGNOME: " + cognome + "\n";
                }

                Log.d("booooooooooo", data);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Failure", String.valueOf(e));
            }
        });


        //return user;
    }


}
