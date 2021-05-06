package com.example.contagiapp.gruppi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GruppoAdapter extends RecyclerView.Adapter<GruppoAdapter.ViewHolder> {
    private ArrayList<Gruppo> listaGruppi;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "imgGruppi";

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
        TextView textViewNumPartecipantiGruppo = holder.numPartecipantiGruppo;
        ImageView imageViewGruppo = holder.imgGruppo;

        textViewNomeGruppo.setText(gruppo.getNomeGruppo());
        Log.d("NomeGrup: Partecipanti", String.valueOf(gruppo.getNomeGruppo()) + "   " + String.valueOf(gruppo.getNroPartecipanti()));
        textViewNumPartecipantiGruppo.setText(gruppo.getNroPartecipanti() + " partecipanti");



        String idGruppo = gruppo.getIdGruppo();
        caricaImgDaStorage(storageRef, storageDirectory, idGruppo, imageViewGruppo);

    }



    @Override
    public int getItemCount() {
        return listaGruppi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeGruppoTextView;
        public TextView numPartecipantiGruppo;
        public ImageView imgGruppo;
        UserAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeGruppoTextView =  itemView.findViewById(R.id.tvNameGroup);
            numPartecipantiGruppo = itemView.findViewById(R.id.tvNumPartecipantiGruppo);
            imgGruppo = itemView.findViewById(R.id.imgGruppo);

        }

        @Override
        public void onClick(View v) {
            onUserListener.onItemClick(getAdapterPosition());
        }
    }

    private void caricaImgDaStorage(StorageReference storageRef, String directory, String idImmagine, final ImageView imageView){
        storageRef.child(directory + "/" + idImmagine).getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String sUrl = uri.toString(); //otteniamo il token del'immagine
                Log.d("sUrl", sUrl);
                Picasso.get().load(sUrl).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("OnFailure Exception", String.valueOf(e));
            }
        });
    }
}
