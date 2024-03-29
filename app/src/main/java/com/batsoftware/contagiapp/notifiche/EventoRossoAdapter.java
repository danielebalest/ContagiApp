package com.batsoftware.contagiapp.notifiche;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.eventi.Evento;
import com.batsoftware.contagiapp.impostazioni.EventiPartecipatoAdapter;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class EventoRossoAdapter extends RecyclerView.Adapter<EventoRossoAdapter.ViewHolder> {
    private Context context;
    private List<Evento> eventi;
    private String mailUtenteLoggato;
    private Utente utente;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public EventoRossoAdapter(Context context, List<Evento> eventi, String mailUtenteLoggato, Utente utente) {
        this.context = context;
        this.eventi = eventi;
        this.mailUtenteLoggato = mailUtenteLoggato;
        this.utente = utente;
    }

    @NonNull
    @Override
    public EventoRossoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventsView = inflater.inflate(R.layout.item_partecipato_evento, parent, false);

        EventoRossoAdapter.ViewHolder viewHolder = new EventoRossoAdapter.ViewHolder(eventsView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull EventoRossoAdapter.ViewHolder holder, final int position) {

        final Evento event = eventi.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCitta = holder.cittaTextView;
        TextView textViewData = holder.dataTextView;
        final ImageView imageViewEvent = holder.imgEvento;
        final MaterialButton btnAccetta = holder.btnAccetta;
        final MaterialButton btnRifiuta = holder.btnRifiuta;
        final String idEvento = event.getIdEvento();

        textViewNome.setText(event.getNome());
        textViewCitta.setText(event.getCitta());
        textViewData.setText(event.getData()+"  "+event.getOrario());
        Log.d("user.getNome()", String.valueOf(event.getNome()));

        //recupero l'immagine dallo storage
        Log.d("eventi/idEvento","eventi/"+event.getPathImg());
        storageRef.child("eventi/"+event.getPathImg()).getDownloadUrl()
                .addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String sUrl = uri.toString(); //otteniamo il token del'immagine
                        Log.d("OnSuccess", "");
                        Log.d("sUrl", sUrl);
                        Picasso.get().load(sUrl).into(imageViewEvent);
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("OnFailure Exception", String.valueOf(e));
                    }
                });

        btnAccetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnAccetta.setText(context.getText(R.string.take_part));
                btnRifiuta.setText(context.getText(R.string.no));
                btnAccetta.setClickable(false);
                btnRifiuta.setClickable(true);

                if(utente.getStato().equals("verde") || utente.getStato().equals("giallo")) {
                    Date dataAttuale = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String stringDataAttuale = sdf.format(dataAttuale);


                    db.collection("Utenti").document(mailUtenteLoggato)
                            .update("stato", "arancione", "dataPositivita", stringDataAttuale);

                    Toasty.success(context, context.getText(R.string.you_attended_red_event), Toast.LENGTH_LONG).show();
                }

                List<String> partecipanti = event.getPartecipanti();
                partecipanti.remove(mailUtenteLoggato);
                db.collection("Eventi").document(event.getIdEvento()).update("partecipanti", partecipanti);
            }
        });

        btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRifiuta.setText(context.getText(R.string.did_not_partecipated));
                btnAccetta.setText(context.getText(R.string.yes));
                btnAccetta.setClickable(true);
                btnRifiuta.setClickable(false);

                List<String> partecipanti = event.getPartecipanti();
                partecipanti.remove(mailUtenteLoggato);
                db.collection("Eventi").document(event.getIdEvento()).update("partecipanti", partecipanti);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeTextView;
        public TextView cittaTextView;
        public TextView dataTextView;
        public ImageView imgEvento;
        public MaterialButton btnAccetta;
        public MaterialButton btnRifiuta;
        EventiPartecipatoAdapter.OnEventListener onEventListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onEventListener = onEventListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameEventRichiesta);
            cittaTextView = itemView.findViewById(R.id.tvCittaEventoRichiesta);
            imgEvento = itemView.findViewById(R.id.imgEventoRichiesta);
            dataTextView = itemView.findViewById(R.id.tvDataEventoRichiesta);
            btnAccetta = itemView.findViewById(R.id.btnAccetta);
            btnRifiuta = itemView.findViewById(R.id.btnRifiuta);
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