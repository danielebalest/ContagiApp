package com.example.contagiapp.notifiche;

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

import com.example.contagiapp.R;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RichiesteAdapter extends RecyclerView.Adapter<com.example.contagiapp.notifiche.RichiesteAdapter.ViewHolder>{

    private List<Utente> mUsers;
    private ArrayList<Utente> utenti = new ArrayList<Utente>();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();



    public RichiesteAdapter(List<Utente> users){
        mUsers = users;
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
    public void onBindViewHolder(@NonNull com.example.contagiapp.notifiche.RichiesteAdapter.ViewHolder holder, int position) {
        Utente user = mUsers.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCognome = holder.cognomeTextView;
        TextView textViewAge = holder.ageTextView;
        final ImageView imageViewUser = holder.imgUtente;
        String idUtente = user.getMail();

        textViewNome.setText(user.getNome());
        textViewCognome.setText(user.getCognome());
        //Log.d("user.getAge()", String.valueOf(user.getAge()));
        //textViewAge.setText(user.getAge() + " "  + "anni");

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
        com.example.contagiapp.notifiche.RichiesteAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameUserRichiesta);
            cognomeTextView = itemView.findViewById(R.id.tvSurnameUserRichiesta);
            imgUtente = itemView.findViewById(R.id.imgUserRichiesta);
            ageTextView = itemView.findViewById(R.id.tvAgeUserRichiesta);
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