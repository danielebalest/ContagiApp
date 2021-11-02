package com.batsoftware.contagiapp.impostazioni;

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
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventiPartecipatoAdapter extends RecyclerView.Adapter<EventiPartecipatoAdapter.ViewHolder> {
    private List<Evento> mEvents;
    private String mailUtenteLoggato;
    private Utente utenteLoggato;
    private String dataRosso;
    private List<Boolean> cond = new ArrayList<>();
    private Context context;

    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventiPartecipatoAdapter(Context context, List<Evento> eventi, String mailUtenteLoggato, Utente utenteLoggato, String dataRosso){
        mEvents = eventi;
        this.mailUtenteLoggato = mailUtenteLoggato;
        this.utenteLoggato = utenteLoggato;
        this.dataRosso = dataRosso;
        this.context = context;

        for(int i = 0; i <= mEvents.size(); i++) {
            cond.add(false);
        }
    }

    public List<Boolean> getCond() {
        return cond;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventsView = inflater.inflate(R.layout.item_partecipato_evento, parent, false);

        EventiPartecipatoAdapter.ViewHolder viewHolder = new EventiPartecipatoAdapter.ViewHolder(eventsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventiPartecipatoAdapter.ViewHolder holder, final int position) {

        final Evento event = mEvents.get(position);
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
        Log.d("eventi/idEvento","eventi/"+idEvento);
        storageRef.child("eventi/"+idEvento).getDownloadUrl()
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
                //avvisare tutti i partecipanti a quell'evento e se hanno partecipato diventano arancioni e dopo 10 giorni gialli
                //se non hanno partecipato non succede niente

                btnAccetta.setText("Partecipato");
                btnRifiuta.setText("no");
                btnAccetta.setClickable(false);
                btnRifiuta.setClickable(true);
                cond.set(position, true);
                db.collection("Eventi").document(idEvento).update("statoRosso", true, "dataRosso", dataRosso);
            }
        });

        btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Eventi").document(idEvento).update("statoRosso", false, "dataRosso", null);
                btnRifiuta.setText("No partecipato");
                btnAccetta.setText("si");
                btnAccetta.setClickable(true);
                btnRifiuta.setClickable(false);
                cond.set(position, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
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