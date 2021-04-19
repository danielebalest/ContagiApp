package com.example.contagiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contagiapp.utente.Utente;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.ViewHolder>{

    private List<Utente> mUsers;
    private ArrayList<Utente> utenti = new ArrayList<Utente>();


    public AddUserAdapter(List<Utente> users){
        mUsers = users;
    }

    @NonNull
    @Override
    public AddUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View usertView = inflater.inflate(R.layout.add_user_row, parent, false);

        AddUserAdapter.ViewHolder viewHolder = new AddUserAdapter.ViewHolder(usertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddUserAdapter.ViewHolder holder, int position) {
        Utente user = mUsers.get(position);
        TextView textViewNome = holder.nomeTextView;
        TextView textViewCognome = holder.cognomeTextView;
        MaterialButton btnAddUser = holder.btnAddUser;

        textViewNome.setText(user.getNome());
        textViewCognome.setText(user.getCognome());
        btnAddUser.setText(R.string.invite);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeTextView;
        public TextView cognomeTextView;
        public MaterialButton btnAddUser;
        UserAdapter.OnUserListener onUserListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.onUserListener = onUserListener;
            nomeTextView =  itemView.findViewById(R.id.tvNameUser);
            cognomeTextView = itemView.findViewById(R.id.tvSurnameUser);
            btnAddUser = itemView.findViewById(R.id.btnAddUser);
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
