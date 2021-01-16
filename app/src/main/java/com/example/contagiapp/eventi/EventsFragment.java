package com.example.contagiapp.eventi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.R;

public class EventsFragment extends FragmentActivity {

    public EventsFragment() {
        // Required empty public constructor
    }

    private Button new_event;

    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_events, container, false);

        //new_event = (Button) view.findViewById(R.id.nuovo_evento);

        /*new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view = inflater.inflate(R.layout.fragment_events, container, false);
                //openNuovoEvento();
            }
        });*/

        return view;
        //return inflater.inflate(R.layout.fragment_notify, container, false);
    }





    /*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_events);

        //implementazione OnClick
        new_event = (Button) getView.findViewById(R.id.nuovo_evento);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNuovoEvento();
            }
        });

    }*/
//public class ciao extends AppCompatActivity {
    public void openNuovoEvento(View v) {
        NewEventsFragment new1 = new NewEventsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new NewEventsFragment()).commit();
        //new1.onCreate(nuovoEvento);
        /*Intent newEvents = new Intent(getActivity(), NewEventsFragment.class);
        startActivity(newEvents);*/
        //NewEventsFragment view = new NewEventsFragment();
        //getSupportFragmentManager().beginTransaction().replace(R.id.container, new EventsFragment()).commit();
        /*LayoutInflater inflater = null;
        ViewGroup container = null;
        View view = inflater.inflate(R.layout.fragment_new_events, container, false);*/
    }//}
}
