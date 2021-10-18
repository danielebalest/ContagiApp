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
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.eventi.EventsFragment;
import com.example.contagiapp.eventi.NewEventsActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static com.example.contagiapp.R.color.cardview_shadow_end_color;
import static com.example.contagiapp.R.color.quantum_yellow;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayout status;
    private MaterialButton btnSearchEvents;
    private  MaterialButton btnCreateEvents;
    private TextView tvStatusDescr;
    private String statoUtente;

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
        tvStatusDescr = view.findViewById(R.id.tvStatusDescription);


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

        boolean caricato;
        do {
            try {
                caricato = false;
                String mail = getMailUtenteLoggato();
                db.collection("Utenti")
                        .document(getMailUtenteLoggato())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Utente utente = documentSnapshot.toObject(Utente.class);
                                Log.d("getMailUtenteLoggato", getMailUtenteLoggato());
                                String stato = utente.getStato();
                                String dataStato = utente.getDataPositivita();

                                if(!statoUtente.equals(stato)) setStato(stato, dataStato);


                                switch (stato){//TODO modificare i messaggi in tvStatusDEscr
                                    case "rosso" : status.setBackgroundTintList(red);
                                        tvStatusDescr.setText(getString(R.string.DescrStatoRosso));
                                        break;

                                    case "verde" : status.setBackgroundTintList(green);
                                        tvStatusDescr.setText(getString(R.string.DescrStatoVerde));
                                        break;

                                    case "giallo" : status.setBackgroundTintList(yellow);
                                        tvStatusDescr.setText(getString(R.string.DescrStatoGiallo));
                                        break;

                                    case "arancione" :
                                        try {
                                            Date dataPositivita = new SimpleDateFormat("dd/MM/yyyy").parse(dataStato);
                                            Date dataAttuale = new Date(System.currentTimeMillis());

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            String stringDataAttuale = sdf.format(dataAttuale);

                                            //864000000 millisecondi = 10 giorni
                                            if(dataAttuale.getTime() - dataPositivita.getTime() >= 864000000) {
                                                db.collection("Utenti").document(getMailUtenteLoggato()).update("stato", "giallo", "dataPositivita", stringDataAttuale);
                                                status.setBackgroundTintList(yellow);
                                                setStato("giallo", stringDataAttuale);
                                            } else status.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 165, 0)));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    default:
                                        //toDo: inserire errore per lo stato
                                }

                            }
                        });

            } catch (NullPointerException e) {
                caricato = true;
            }
        }while(caricato);
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
            statoUtente = utente.getStato();
        } else {
            SharedPreferences prefs1 = getActivity().getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);

            db.collection("Utenti")
                    .document(mailUtenteLoggato)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    statoUtente = documentSnapshot.getString("stato");
                }
            });
        }
        return mailUtenteLoggato;
    }

    private void setStato(String stato, String dataPos) {
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("utente", "no");
        Utente utente;

        if(!json.equals("no")) {
            utente = gson.fromJson(json, Utente.class);
            utente.setStato(stato);
            utente.setDataPositivita(dataPos);

            SharedPreferences.Editor editor = prefs.edit();
            String json1 = gson.toJson(utente);
            editor.putString("utente", json1);
            editor.apply();
        }

        Toasty.success(getContext(), "Stato aggiornato", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getActivity(),MainActivity.class);
        startActivity(i);
    }
}

