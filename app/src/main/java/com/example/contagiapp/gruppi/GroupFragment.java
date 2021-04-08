package com.example.contagiapp.gruppi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.service.autofill.OnClickAction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contagiapp.R;
import com.example.contagiapp.eventi.NewEventsFragment;
import com.example.contagiapp.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    public GroupFragment() {

    }

    private FloatingActionButton crea_gruppo;
    private Button visualizza_gruppo;
    ListView listViewGruppi;
    TextInputEditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_group, container, false);
        listViewGruppi = (ListView)view.findViewById(R.id.list_groups);
        ArrayList<String> arrayListGruppi = new ArrayList<>();

        arrayListGruppi.add("Gruppo1");
        arrayListGruppi.add("Gruppo2");
        arrayListGruppi.add("Gruppo3");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arrayListGruppi);
        listViewGruppi.setAdapter(arrayAdapter);

        listViewGruppi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GroupSearch.class);
                startActivity(intent);
            }
        });

        crea_gruppo = view.findViewById(R.id.FAB_groups);
        crea_gruppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreaGruppoActivity.class);
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
