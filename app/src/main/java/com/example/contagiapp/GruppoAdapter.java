package com.example.contagiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.contagiapp.gruppi.Gruppo;

import java.util.ArrayList;

public class GruppoAdapter extends ArrayAdapter<Gruppo> {
    public GruppoAdapter(Context context, ArrayList< Gruppo> users){
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Ottieni item dalla posizione
        Gruppo gruppo = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gruppo, parent, false);
        }

        TextView tvNomeGruppo = convertView.findViewById(R.id.tvNome);
        TextView tvDescGruppo = convertView.findViewById(R.id.tvDescr);

        tvNomeGruppo.setText(gruppo.getNomeGruppo());
        tvDescGruppo.setText(gruppo.getDescrizione());

        return convertView;
    }
}
