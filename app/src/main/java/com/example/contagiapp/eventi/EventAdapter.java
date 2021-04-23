package com.example.contagiapp.eventi;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;

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
        TextView textViewNomeEvento = holder.nomeEventoTextViewItemEvent;
        TextView textViewNumPartecipanti = holder.numeroPartecipantiItemEvent;
        TextView textViewPostiDisponibili = holder.postiDisponibiliItemEvent;
        TextView textViewDataEvento = holder.dataItemEvent;
        TextView textViewOrarioEvento = holder.orarioItemEvent;
        TextView textViewCittaEvento = holder.cittaItemEvent;


        evento.setNumeroPostiDisponibili(evento.getNumeroMaxPartecipanti(), evento.getNumPartecipanti());
        textViewNomeEvento.setText(evento.getNome());
        textViewNumPartecipanti.setText(evento.getNumPartecipanti() + " partecipanti"); //todo: inserire R.string.participants
        textViewPostiDisponibili.setText(String.valueOf(evento.getNumeroPostiDisponibili()) + " disponibili"); //todo: inserire R.string.available
        textViewDataEvento.setText(evento.getData());
        textViewOrarioEvento.setText(evento.getOrario());
        textViewCittaEvento.setText(evento.getCitta());

        if(evento.getNumeroPostiDisponibili() == 0){
            textViewPostiDisponibili.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeEventoTextViewItemEvent, numeroPartecipantiItemEvent, postiDisponibiliItemEvent, dataItemEvent, orarioItemEvent, cittaItemEvent;
        EventAdapter.OnEventListener onEventListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEventoTextViewItemEvent = itemView.findViewById(R.id.tvNameItemEvent);
            numeroPartecipantiItemEvent = itemView.findViewById(R.id.tvNumPartecipantiItemEvent);
            postiDisponibiliItemEvent = itemView.findViewById(R.id.tvPostiDisponibiliitemEvent);
            dataItemEvent = itemView.findViewById(R.id.tvDataItemEvent);
            orarioItemEvent = itemView.findViewById(R.id.tvOraItemEvent);
            cittaItemEvent = itemView.findViewById(R.id.tvCittaItemEvent);

        }


        @Override
        public void onClick(View v) {
            onEventListener.onItemClick(getAdapterPosition());
            }
        }

        public interface OnEventListener{
            void onItemClick(int position);
        }

}
