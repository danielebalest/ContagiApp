package com.example.contagiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.eventi.NewEventsActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;

import static com.example.contagiapp.R.color.cardview_shadow_end_color;
import static com.example.contagiapp.R.color.quantum_yellow;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout status;
    private MaterialButton btnSearchEvents;
    private  MaterialButton btnCreateEvents;

    ColorStateList red = ColorStateList.valueOf(Color.parseColor("#FF0000"));
    ColorStateList yellow = ColorStateList.valueOf(Color.parseColor("#FFF8F405"));
    ColorStateList green = ColorStateList.valueOf(Color.parseColor("#FF43A047"));

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        btnCreateEvents = view.findViewById(R.id.btnCreateEvent);
        btnSearchEvents = view.findViewById(R.id.btnSearchEvent);
        status = view.findViewById(R.id.statusCircle2);


        btnCreateEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsActivity.class);
                startActivity(intent);
            }
        });

        btnSearchEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //richiamo il fragment
                Fragment fragment = new EventsFragment();
                FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.container,fragment);
                fr.addToBackStack(null); //serve per tornare al fragment precedente
                fr.commit();
            }
        });

        db.collection("Utenti")
                .document(getMailUtenteLoggato())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Utente utente = documentSnapshot.toObject(Utente.class);
                        String stato = utente.getStato();
                        switch (stato){
                            case "rosso" : status.setBackgroundTintList(red);
                            break;

                            case "verde" : status.setBackgroundTintList(green);
                            break;

                            case "giallo" : status.setBackgroundTintList(yellow);
                            break;

                            default:
                                //toDo: inserire errore per lo stato
                        }

                    }
                });

        return view;
    }

    private String getMailUtenteLoggato(){
        Utente utente;
        Gson gson = new Gson();
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;
        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
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

