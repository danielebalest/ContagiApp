package com.example.contagiapp.eventi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    public Context mContext;
    private ArrayList<Evento> listaEventi;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();



    public EventAdapter(ArrayList<Evento> listaEventi, Context mContext){
        this.listaEventi = listaEventi;
        this.mContext = mContext;
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
        String idEvento = evento.getIdEvento();
        Log.d("idEventoo", String.valueOf(idEvento));

        TextView textViewNomeEvento = holder.nomeEventoTextViewItemEvent;
        TextView textViewNumPartecipanti = holder.numeroPartecipantiItemEvent;
        TextView textViewPostiDisponibili = holder.postiDisponibiliItemEvent;
        TextView textViewDataEvento = holder.dataItemEvent;
        TextView textViewOrarioEvento = holder.orarioItemEvent;
        TextView textViewCittaEvento = holder.cittaItemEvent;
        final ImageView imageViewEvento = holder.imgEvento;



        //evento.setNumeroPostiDisponibili(evento.getNumeroMaxPartecipanti(), evento.getNumPartecipanti());
        textViewNomeEvento.setText(evento.getNome());
        textViewNumPartecipanti.setText(evento.getPartecipanti().size() + mContext.getString(R.string.participants));
        textViewPostiDisponibili.setText(evento.getNumeroMaxPartecipanti() - evento.getPartecipanti().size() + mContext.getString(R.string.available));
        textViewDataEvento.setText(evento.getData());
        textViewOrarioEvento.setText(evento.getOrario());
        textViewCittaEvento.setText("Città: "+evento.getCitta());


        //recupero l'immagine dallo storage
        Log.d("eventi/idEvento","eventi/"+idEvento);
        storageRef.child("eventi/"+idEvento).getDownloadUrl()
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



        if((evento.getNumeroMaxPartecipanti() - evento.getPartecipanti().size()) == 0){
            textViewPostiDisponibili.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return listaEventi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeEventoTextViewItemEvent, numeroPartecipantiItemEvent, postiDisponibiliItemEvent, dataItemEvent, orarioItemEvent, cittaItemEvent;
        public ImageView imgEvento;
        EventAdapter.OnEventListener onEventListener;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEventoTextViewItemEvent = itemView.findViewById(R.id.tvNameItemEvent);
            numeroPartecipantiItemEvent = itemView.findViewById(R.id.tvNumPartecipantiItemEvent);
            postiDisponibiliItemEvent = itemView.findViewById(R.id.tvPostiDisponibiliitemEvent);
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
