package com.example.contagiapp.data.amici;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contagiapp.R;
import com.example.contagiapp.gruppi.GroupSearch;
import com.google.android.material.textfield.TextInputEditText;

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
    TextInputEditText editText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        listView = (ListView)view.findViewById(R.id.list_view_friends);

        ArrayList<String> arrayListFriend = new ArrayList<>();

        arrayListFriend.add("Utente1");
        arrayListFriend.add("Utente2");
        arrayListFriend.add("Utente3");
        arrayListFriend.add("Utente4");
        arrayListFriend.add("Utente5");
        arrayListFriend.add("Utente6");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayListFriend);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FriendProfile.class);
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


}
