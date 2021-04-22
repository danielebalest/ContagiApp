package com.example.contagiapp.eventi;

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
import android.widget.TextView;

import com.example.contagiapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    public EventsFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FloatingActionButton new_event;
    TextInputEditText editText;
    RecyclerView rvEventi;
    ArrayList<Evento> listaEventi = new ArrayList<Evento>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_events, container, false);

        rvEventi = view.findViewById(R.id.rvEventi);
        caricaEventi();


        new_event = view.findViewById(R.id.floating_action_button);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsActivity.class);
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

    private void caricaEventi(){
        listaEventi = new ArrayList<Evento>();
        db.collection("Eventi").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Evento evento = documentSnapshot.toObject(Evento.class);
                            listaEventi.add(evento);
                        }
                        EventAdapter adapter = new EventAdapter(listaEventi);
                        rvEventi.setAdapter(adapter);
                        rvEventi.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                }); //toDo onFailure
    }
}
