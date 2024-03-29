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

public class RichiesteAdapter extends RecyclerView.Adapter<RichiesteAdapter.ViewHolder>{

    private List<Utente> mUsers;
    private String mailUtenteLoggato;
    private Utente utenteLoggato;
    private ArrayList<Utente> utenti = new ArrayList<Utente>();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    public RichiesteAdapter(List<Utente> users, String mailUtenteLoggato, Utente utenteLoggato){
        mUsers = users;
        this.mailUtenteLoggato = mailUtenteLoggato;
        this.utenteLoggato = utenteLoggato;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.item_richiesta, parent, false);

        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RichiesteAdapter.ViewHolder holder, int position) {
        final Utente user = mUsers.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCognome = holder.cognomeTextView;
        TextView textViewAge = holder.ageTextView;
        final ImageView imageViewUser = holder.imgUtente;
        final MaterialButton btnAccetta = holder.btnAccetta;
        final MaterialButton btnRifiuta = holder.btnRifiuta;
        final String idUtente = user.getMail();

        Context context= holder.itemView.getContext();


        textViewNome.setText(user.getNome());
        textViewCognome.setText(user.getCognome());
        textViewAge.setText(user.getAge() + " "  + context.getText(R.string.years));
        Log.d("user.getNome()", String.valueOf(user.getNome()));
        Log.d("user.getMailPath", String.valueOf(user.getMail()));

        //recupero l'immagine dallo storage
        Log.d("imgUtenti/idUtente","imgUtenti/"+idUtente);
        storageRef.child("imgUtenti/"+idUtente).getDownloadUrl()
                .addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String sUrl = uri.toString(); //otteniamo il token del'immagine
                        Log.d("OnSuccess", "");
                        Log.d("sUrl", sUrl);
                        Picasso.get().load(sUrl).into(imageViewUser);
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

                user.addAmico(mailUtenteLoggato);

                btnAccetta.setText(context.getText(R.string.accepted));
                btnAccetta.setClickable(false);
                btnRifiuta.setVisibility(View.GONE);

                //aggiunge alla lista amici dell'utente che ha inviato la richiesta, la mail dell'utente loggato
                db.collection("Utenti").document(idUtente)
                        .update("amici", user.getAmici());


                utenteLoggato.addAmico(idUtente);
                //aggiunge alla lista amici dell'utente loggato che ha ricevuto la richiesta, la mail dell'utente che ha inviato la richiesta
                db.collection("Utenti").document(mailUtenteLoggato)
                        .update("amici", utenteLoggato.getAmici());


                //rimuovere richiesta  per l'utente loggato
                utenteLoggato.rimuoviRichiesta(idUtente);
                db.collection("Utenti").document(mailUtenteLoggato)
                        .update("richiesteRicevute", utenteLoggato.getRichiesteRicevute());

            }
        });


        btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRifiuta.setText(context.getText(R.string.declined));
                btnRifiuta.setClickable(false);
                btnAccetta.setVisibility(View.GONE);

                //rimuovere richiesta  per l'utente loggato
                utenteLoggato.rimuoviRichiesta(idUtente);
                db.collection("Utenti").document(mailUtenteLoggato)
                        .update("richiesteRicevute", utenteLoggato.getRichiesteRicevute());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeTextView;
        public TextView cognomeTextView;
        public TextView ageTextView;
        public ImageView imgUtente;
        public MaterialButton btnAccetta;
        public MaterialButton btnRifiuta;
        RichiesteAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameUserRichiesta);
            cognomeTextView = itemView.findViewById(R.id.tvSurnameUserRichiesta);
            imgUtente = itemView.findViewById(R.id.imgUserRichiesta);
            ageTextView = itemView.findViewById(R.id.tvAgeUserRichiesta);
            btnAccetta = itemView.findViewById(R.id.btnAccetta);
            btnRifiuta = itemView.findViewById(R.id.btnRifiuta);
        }

        @Override
        public void onClick(View v) {
            onUserListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnUserListener{
        void onItemClick(int position);
    }



}