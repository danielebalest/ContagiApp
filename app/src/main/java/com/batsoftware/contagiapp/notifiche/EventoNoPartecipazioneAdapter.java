package com.batsoftware.contagiapp.notifiche;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.batsoftware.contagiapp.impostazioni.EventiPartecipatoAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventoNoPartecipazioneAdapter extends RecyclerView.Adapter<EventoNoPartecipazioneAdapter.ViewHolder> {
    private List<Evento> ev;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public EventoNoPartecipazioneAdapter(List<Evento> ev, Context context) {
        this.ev = ev;
        this.context = context;
    }

    @NonNull
    @Override
    public EventoNoPartecipazioneAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventsView = inflater.inflate(R.layout.item_annulla_partecipazione_evento, parent, false);

        EventoNoPartecipazioneAdapter.ViewHolder viewHolder = new EventoNoPartecipazioneAdapter.ViewHolder(eventsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventoNoPartecipazioneAdapter.ViewHolder holder, final int position) {
        final Evento event = ev.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCitta = holder.cittaTextView;
        TextView textViewData = holder.dataTextView;
        final ImageView imageViewEvent = holder.imgEvento;
        final MaterialButton btnAccetta = holder.btnAccetta;
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

                ArrayList<String> eventi = new ArrayList<>();
                SharedPreferences pref = context.getSharedPreferences("eventi", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = pref.getString("id", "no");
                eventi = gson.fromJson(json, new TypeToken<ArrayList<String>>() {}.getType());

                eventi.remove(event.getIdEvento());

                SharedPreferences.Editor editor = pref.edit();
                json = gson.toJson(eventi);
                editor.putString("id", json);
                editor.commit();

                btnAccetta.setClickable(false);
                btnAccetta.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ev.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeTextView;
        public TextView cittaTextView;
        public TextView dataTextView;
        public ImageView imgEvento;
        public MaterialButton btnAccetta;
        EventiPartecipatoAdapter.OnEventListener onEventListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onEventListener = onEventListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameEventRichiesta);
            cittaTextView = itemView.findViewById(R.id.tvCittaEventoRichiesta);
            imgEvento = itemView.findViewById(R.id.imgEventoRichiesta);
            dataTextView = itemView.findViewById(R.id.tvDataEventoRichiesta);
            btnAccetta = itemView.findViewById(R.id.btnAccetta);
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
