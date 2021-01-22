package com.example.contagiapp.eventi;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.contagiapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventsFragment extends Fragment {

    public EventsFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton new_event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_events, container, false);

        new_event = view.findViewById(R.id.floating_action_button);
        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewEventsFragment.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
