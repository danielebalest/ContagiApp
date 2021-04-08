package com.example.contagiapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contagiapp.eventi.NewEventsFragment;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.android.material.switchmaterial.SwitchMaterial;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    AlertDialog dialog;
    AlertDialog.Builder builder;
    String [] items = {"Italiano", "English"};


    public SettingFragment() {
        // Required empty public constructor
    }


    //da implemetare il cambio di lingua
    public void setAppLocate(String localeCode){
        Resources res = getResources();
    }

    public SwitchMaterial switchMaterial;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_setting, container, false);


        //builder = new AlertDialog.Builder(SettingFragment.super);
        builder.setTitle("Seleziona la lingua");

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        dialog.show();

        switchMaterial = view.findViewById(R.id.SwitchDarkTheme);
        /*implementare cambio tema quando lo switch cambia stato
        switchMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchMaterial.isChecked()){
                    //implementare tema scuro
                }else {
                    //tema  chiaro
                }
            }
        });*/

        return view;
    }
}
