package com.batsoftware.contagiapp.gruppi;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RimuoviPartecipanteAdapter extends RecyclerView.Adapter<RimuoviPartecipanteAdapter.ViewHolder>{

    private List<Utente> mUsers;
    private ArrayList<Utente> utenti = new ArrayList<Utente>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    String idGruppo;






    public RimuoviPartecipanteAdapter(List<Utente> users, String idGruppo){
        mUsers = users;
        this.idGruppo = idGruppo;
    }



    @NonNull
    @Override
    public RimuoviPartecipanteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.item_partecipanti, parent, false);

        RimuoviPartecipanteAdapter.ViewHolder viewHolder = new RimuoviPartecipanteAdapter.ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RimuoviPartecipanteAdapter.ViewHolder holder, int position) {
        final Utente user = mUsers.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCognome = holder.cognomeTextView;
        final ImageView imageViewUser = holder.imgUtente;
        final String idUtente = user.getMail();
        final CheckBox checkBox = holder.checkBox;

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


        db.collection("Gruppo").document(idGruppo)
                .get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        final ArrayList<String> listaPartecipanti = (ArrayList<String>) document.get("partecipanti");


                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if(isChecked){
                                listaPartecipanti.add(idUtente);
                                db.collection("Gruppo").document(idGruppo).update("partecipanti", listaPartecipanti);
                                Log.d("listaPartecipanti", String.valueOf(String.valueOf(listaPartecipanti) + "  Utente:"  +  String.valueOf(user.getMailPath())));
                            }
                            if(!isChecked){
                                if(user.getMail() !=null){

                                    listaPartecipanti.remove(idUtente);
                                    db.collection("Gruppo").document(idGruppo).update("partecipanti", listaPartecipanti);
                                    Log.d("listaInvitiRemove", String.valueOf(String.valueOf(listaPartecipanti)));
                                }
                            }

                        });

                    }
                });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nomeTextView;
        public TextView cognomeTextView;
        public ImageView imgUtente;
        MaterialCheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTextView =  itemView.findViewById(R.id.tvNamePartecipante);
            cognomeTextView = itemView.findViewById(R.id.tvSurnamePartecipante);
            imgUtente = itemView.findViewById(R.id.imgPartecipante);
            checkBox = itemView.findViewById(R.id.checkBoxRimoviPartecipante);

        }


    }

}
