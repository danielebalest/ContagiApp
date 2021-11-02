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
import com.batsoftware.contagiapp.gruppi.Gruppo;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InvitiAdapter extends RecyclerView.Adapter<InvitiAdapter.ViewHolder>{

    private List<Gruppo> mGruppi;
    private String mailUtenteLoggato;
    private Utente utenteLoggato;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    public InvitiAdapter(List<Gruppo> gruppi, String mailUtenteLoggato, Utente utenteLoggato){
        mGruppi = gruppi;
        this.mailUtenteLoggato = mailUtenteLoggato;
        this.utenteLoggato = utenteLoggato;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_invito, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvitiAdapter.ViewHolder holder, int position) {
        final Gruppo gruppo = mGruppi.get(position);
        TextView textViewNomeGruppo = holder.nomeGruppo;
        TextView textViewPartecipanti = holder.partecipanti;
        final ImageView imageViewUser = holder.imgGruppo;
        final MaterialButton btnAccettaInvito = holder.btnAccettaInvito;
        final MaterialButton btnRifiutaInvito = holder.btnRifiutaInvito;
        final String idGruppo = gruppo.getIdGruppo();


        textViewNomeGruppo.setText(gruppo.getNomeGruppo());
        Log.d("gruppo", String.valueOf(gruppo.getNomeGruppo()));
        //textViewPartecipanti.setText(gruppo.getNroPartecipanti());



        //recupero l'immagine dallo storage
        Log.d("imgGruppi/idGruppo","imgGruppi/"+idGruppo);
        storageRef.child("imgGruppi/"+idGruppo).getDownloadUrl()
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

        btnAccettaInvito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gruppo.addPartecipante(mailUtenteLoggato);

                btnAccettaInvito.setText("Accettato");
                btnAccettaInvito.setClickable(false);
                btnRifiutaInvito.setVisibility(View.GONE);

                int numero = gruppo.getPartecipanti().size() + 1;

                //aggiunge alla lista partecipanti del gruppo la mail dell'utente loggato
                db.collection("Gruppo").document(idGruppo)
                        .update("partecipanti", gruppo.getPartecipanti(), "nroPartecipanti", numero);



                //rimuovere richiesta  per l'utente loggato
                utenteLoggato.rimuoviInvito(idGruppo);
                db.collection("Utenti").document(mailUtenteLoggato)
                        .update("invitiRicevuti", utenteLoggato.getInvitiRicevuti());

            }
        });

        btnRifiutaInvito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRifiutaInvito.setText("Rifiutato");
                btnRifiutaInvito.setClickable(false);
                btnAccettaInvito.setVisibility(View.GONE);

                //rimuovere invito  per l'utente loggato
                utenteLoggato.rimuoviInvito(idGruppo);
                db.collection("Utenti").document(mailUtenteLoggato)
                        .update("invitiRicevuti", utenteLoggato.getInvitiRicevuti());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGruppi.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeGruppo;
        public TextView partecipanti;
        public ImageView imgGruppo;
        public MaterialButton btnAccettaInvito;
        public MaterialButton btnRifiutaInvito;
        InvitiAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeGruppo =  itemView.findViewById(R.id.tvNomeGruppoInvito);
            partecipanti = itemView.findViewById(R.id.tvNumPartecipantiGruppoInvito);
            imgGruppo = itemView.findViewById(R.id.imgGruppoInvito);
            btnAccettaInvito = itemView.findViewById(R.id.btnAccettaInvito);
            btnRifiutaInvito = itemView.findViewById(R.id.btnRifiutaInvito);
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
