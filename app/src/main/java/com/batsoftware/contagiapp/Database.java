package com.batsoftware.contagiapp;

import android.util.Log;

import com.batsoftware.contagiapp.gruppi.Gruppo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    public Database(){

    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface StatoGruppo{
        String setIdGruppo();
        void getStato(String stato);
    }
    public void getStatoGruppo(final StatoGruppo mydb){

        db.collection("Gruppo")
                .document(mydb.setIdGruppo())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Gruppo g = documentSnapshot.toObject(Gruppo.class);
                        Log.d("Oggetto", g.toString());
                        mydb.getStato(g.getNomeGruppo());
                        Log.d("getStatoGruppo", String.valueOf(g.getStatoGruppo()));
                        String stato = g.getStatoGruppo();


                        //Log.d("getStatoGruppo", String.valueOf(g.getStatoGruppo().getClass()));

                    }
                });
    }

}
