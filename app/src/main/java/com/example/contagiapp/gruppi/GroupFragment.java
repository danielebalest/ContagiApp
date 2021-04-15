package com.example.contagiapp.gruppi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.contagiapp.GruppoAdapter;
import com.example.contagiapp.R;
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
    TextInputEditText editText;

    ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_group, container, false);

        //costruisci data source
        ArrayList<Gruppo> arrayListGruppi = new ArrayList<Gruppo>();
        //crea un adapter per convertire l'array in view
        GruppoAdapter adapter = new GruppoAdapter(getActivity().getApplicationContext(), arrayListGruppi);
        //collega adapter alla listView
        listView = view.findViewById(R.id.ListViewGroup);
        listView.setAdapter(adapter);


        //popolamento
        Gruppo g1 = new Gruppo();
        Gruppo g2 = new Gruppo();
        Gruppo g3 = new Gruppo();

        g1.setNomeGruppo("g1");
        g1.setDescrizione("d1");
        g2.setNomeGruppo("g2");
        g3.setNomeGruppo("g3");
        adapter.add(g1);
        adapter.add(g2);
        adapter.add(g3);

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
