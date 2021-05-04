package com.example.contagiapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.ViewHolder>{

    private List<Utente> mUsers;
    private ArrayList<Utente> utenti = new ArrayList<Utente>();
    private ArrayList<String> utentiSelezionati;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();





    public AddUserAdapter(List<Utente> users, ArrayList<String> utentiSelezionati){
        mUsers = users;
        this.utentiSelezionati = utentiSelezionati;
    }



    @NonNull
    @Override
    public AddUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.add_user_row, parent, false);

        AddUserAdapter.ViewHolder viewHolder = new AddUserAdapter.ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddUserAdapter.ViewHolder holder, int position) {
        final Utente user = mUsers.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCognome = holder.cognomeTextView;
        final ImageView imageViewUser = holder.imgUtente;
        String idUtente = user.getMail();
        CheckBox checkBox = holder.checkBox;

        textViewNome.setText(user.getNome());
        textViewCognome.setText(user.getCognome());


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

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    utentiSelezionati.add(user.getMail());
                    Log.d("utentiSelezionati", String.valueOf(utentiSelezionati));

                    db.collection("Utenti").document(user.getMail()).update("invitiRicevuti", "GfDqvqvlXwTLJx4yjVLj");
                }
                if(!isChecked){
                    if(user.getMail() !=null){
                        utentiSelezionati.remove(user.getMail());
                        Log.d("utentiSelezionati", String.valueOf(utentiSelezionati));
                    }
                }

            }
        });
    }




    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView nomeTextView;
        public TextView cognomeTextView;
        public ImageView imgUtente;
        MaterialCheckBox checkBox;
        AddUserAdapter.OnUserListener onUserListener;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameAddUser);
            cognomeTextView = itemView.findViewById(R.id.tvSurnameAddUser);
            imgUtente = itemView.findViewById(R.id.imgAddUser);
            checkBox = itemView.findViewById(R.id.checkBoxAddUser);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserListener.onItemClick(getAdapterPosition());
                }
            });

        }


        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
    public interface OnUserListener{
        void onItemClick(int position);


    }
}
