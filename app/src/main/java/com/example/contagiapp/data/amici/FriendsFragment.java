package com.example.contagiapp.data.amici;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.contagiapp.R;
import com.example.contagiapp.gruppi.GroupSearch;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    public FriendsFragment() {
        // Required empty public constructor
    }



    private Button visualizza_profilo;
    ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = (ListView)view.findViewById(R.id.list_view);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Utente1");
        arrayList.add("Utente2");
        arrayList.add("Utente3");
        arrayList.add("Utente4");
        arrayList.add("Utente5");
        arrayList.add("Utente6");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayList);
        listView.setAdapter(arrayAdapter);


        /*visualizza_profilo = view.findViewById(R.id.Visualizza_profilo);
        visualizza_profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendProfile.class);
                startActivity(intent);



            }
        });*/

      return view;
    }


}
