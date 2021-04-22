package com.example.contagiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.gruppi.Gruppo;
import com.example.contagiapp.utente.Utente;

import java.util.ArrayList;

public class GruppoAdapter extends RecyclerView.Adapter<GruppoAdapter.ViewHolder> {
    private ArrayList<Gruppo> listaGruppi;

    public GruppoAdapter(ArrayList< Gruppo> gruppi){
        listaGruppi = gruppi;
    }


    @NonNull
    @Override
    public GruppoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View usertView = inflater.inflate(R.layout.item_gruppo, parent, false);

        GruppoAdapter.ViewHolder viewHolder = new GruppoAdapter.ViewHolder(usertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gruppo gruppo = listaGruppi.get(position);
        TextView textViewNomeGruppo = holder.nomeGruppoTextView;

        textViewNomeGruppo.setText(gruppo.getNomeGruppo());
    }

    @Override
    public int getItemCount() {
        return listaGruppi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeGruppoTextView;
        UserAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeGruppoTextView =  itemView.findViewById(R.id.tvNameGroup);

        }

        @Override
        public void onClick(View v) {
            onUserListener.onItemClick(getAdapterPosition());
        }
    }
}