package com.example.contagiapp.eventi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private ArrayList<Evento> listaEventi;

    public EventAdapter(ArrayList<Evento> listaEventi){
        this.listaEventi = listaEventi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Evento evento = listaEventi.get(position);
        TextView textViewNomeEvento = holder.nomeEventoTextView;
        TextView textViewNumPartecipanti = holder.numeroPartecipanti;
        TextView textViewPostiDisponibili = holder.postiDisponibili;

        textViewNomeEvento.setText(evento.getNome());
        textViewNumPartecipanti.setText(evento.getNumPartecipanti() + R.string.participants);
        textViewPostiDisponibili.setText(evento.getNumeroPostiDisponibili() + R.string.available);
    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeEventoTextView;
        public TextView numeroPartecipanti;
        public TextView postiDisponibili;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEventoTextView = itemView.findViewById(R.id.tvNameEvent);
            numeroPartecipanti = itemView.findViewById(R.id.tvNumPartecipanti);
            postiDisponibili = itemView.findViewById(R.id.tvPostiDisponibili);

        }


        @Override
        public void onClick(View v) {

        }

    }
}
