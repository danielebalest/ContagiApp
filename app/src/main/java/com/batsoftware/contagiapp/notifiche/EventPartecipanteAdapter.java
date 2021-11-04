package com.batsoftware.contagiapp.notifiche;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.eventi.Evento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventPartecipanteAdapter extends RecyclerView.Adapter<EventPartecipanteAdapter.ViewHolder>{
    private ArrayList<Evento> listaEventi;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();



    public EventPartecipanteAdapter(ArrayList<Evento> listaEventi){
        this.listaEventi = listaEventi;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.item_promemoria_evento, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Evento evento = listaEventi.get(position);
        String idEvento = evento.getIdEvento();
        Log.d("idEventoo", String.valueOf(idEvento));

        TextView textViewNomeEvento = holder.nomeEventoTextViewItemEvent;
        TextView textViewDataEvento = holder.dataItemEvent;
        TextView textViewOrarioEvento = holder.orarioItemEvent;
        TextView textViewCittaEvento = holder.cittaItemEvent;
        final ImageView imageViewEvento = holder.imgEvento;



        textViewNomeEvento.setText(evento.getNome());

        textViewDataEvento.setText(evento.getData());
        textViewOrarioEvento.setText(evento.getOrario());
        textViewCittaEvento.setText(evento.getCitta());


        //recupero l'immagine dallo storage
        Log.d("eventi/idEvento","eventi/"+evento.getPathImg());
        storageRef.child("eventi/"+evento.getPathImg()).getDownloadUrl()
                .addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("OnSuccess", "");
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageViewEvento);
            }})
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });


    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeEventoTextViewItemEvent, dataItemEvent, orarioItemEvent, cittaItemEvent;
        public ImageView imgEvento;
        EventPartecipanteAdapter.OnEventListener onEventListener;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEventoTextViewItemEvent = itemView.findViewById(R.id.tvNameItemEvent);
            dataItemEvent = itemView.findViewById(R.id.tvDataItemEvent);
            orarioItemEvent = itemView.findViewById(R.id.tvOraItemEvent);
            cittaItemEvent = itemView.findViewById(R.id.tvCittaItemEvent);
            imgEvento = itemView.findViewById(R.id.imgItemEvent);
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
