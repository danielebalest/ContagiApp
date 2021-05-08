package com.example.contagiapp.gruppi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.AddUserAdapter;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.data.amici.FriendsFragment;
import com.example.contagiapp.data.amici.ProfiloUtentiActivity;
import com.example.contagiapp.eventi.NewEventsActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;

public class InvitaAmiciGruppoActivity extends AppCompatActivity {

    private Button btn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> idList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invita_amici_gruppo);

        Bundle extras = getIntent().getExtras();
        final String idGruppo = extras.getString("idGruppo");

        btn = findViewById(R.id.btnTermina);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(InvitaAmiciGruppoActivity.this, MainActivity.class);
                startActivity(mainIntent);
                //todo: devo tornare al fragment GroupFragment

            }
        });


        final RecyclerView rvInvitaAmici = findViewById(R.id.rvInvitaAmici);
        String mailAdmin = getMailUtenteLoggato();
        db.collection("Utenti")
                .document(mailAdmin).get()
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                        final ArrayList<String> amiciNonPartecipanti = (ArrayList<String>) document.get("amici");
                        if(amiciNonPartecipanti.isEmpty()){
                            TextView tvInvitaAmici = findViewById(R.id.tvInvitaAmici);
                            tvInvitaAmici.setText("Non hai ancora nessun amico");
                        }
                        Log.d("lista", String.valueOf(amiciNonPartecipanti));

                        //recupero listaPartecipanti
                        db.collection("Gruppo").document(idGruppo)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                                ArrayList<String> listaPartecipanti = gruppo.getPartecipanti();
                                Log.d("listaPartecipanti", String.valueOf(listaPartecipanti));

                                for(int i=0; i<listaPartecipanti.size(); i++){
                                    amiciNonPartecipanti.remove(listaPartecipanti.get(i));  //rimuovo gli amici che già partecipano al gruppo
                                }

                                getFriends(amiciNonPartecipanti, idGruppo, rvInvitaAmici);
                            }
                        });

                    }
                });
    }





    private String getMailUtenteLoggato(){
        Gson gson = new Gson();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String json = prefs.getString("utente", "no");
        String mailUtenteLoggato;

        if(!json.equals("no")) {
            Utente utente = gson.fromJson(json, Utente.class);
            mailUtenteLoggato = utente.getMail();
            Log.d("mailutenteLoggato", mailUtenteLoggato);
        } else {
            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("LoginTemporaneo",Context.MODE_PRIVATE);
            mailUtenteLoggato = prefs1.getString("mail", "no");
            Log.d("mail", mailUtenteLoggato);
        }
        return mailUtenteLoggato;
    }



    public void getFriends(ArrayList<String> listaAmici, final String idGruppo, final RecyclerView recyclerView){
        /*
        metodo che svolge le seguenti operazioni:
         1)date in input le mail degli amici ottiene, per ciascuno, i seguenti dati dal database: nome, cognome, mail
         2)crea per ognuno un nuovo tipo Utente che aggiunge ad una lista
         3) passa la lista all'adapter del recycler View che poi permetterà la visualizzazione della lista di CardView degli amici sull'app
         */

        Log.d("idGruppo2", String.valueOf(idGruppo));

        final ArrayList<Utente> amici = new ArrayList<Utente>();
        for(int i=0; i < listaAmici.size(); i++){
            db.collection("Utenti")
                    .document(listaAmici.get(i))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Utente user = new Utente();
                            user.setNome(documentSnapshot.getString("nome"));
                            user.setCognome(documentSnapshot.getString("cognome"));
                            user.setMail(documentSnapshot.getString("mail"));
                            user.setDataNascita(documentSnapshot.getString("dataNascita"));
                            Log.d("Nome utente", String.valueOf(user.getNome()));
                            Log.d("dataNascita", String.valueOf(user.getDataNascita()));

                            amici.add(user);
                            Log.d("amiciSize", String.valueOf(amici.size()));



                            String id = user.getMail();
                            idList.add(id);

                            AddUserAdapter adapter = new AddUserAdapter(amici, idGruppo);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                            recyclerView.addOnItemTouchListener(new InvitaAmiciGruppoActivity.RecyclerTouchListener(InvitaAmiciGruppoActivity.this, recyclerView, new InvitaAmiciGruppoActivity.RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position) {


                                }

                                @Override
                                public void onLongClick(View view, int position) {
                                    String idUtenteSelezionato = idList.get(position);
                                    Log.i("idList1: ", idUtenteSelezionato);
                                }

                            }));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error", "errore");
                }
            });
        }





    }



    //per il click
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private InvitaAmiciGruppoActivity.RecyclerTouchListener.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final InvitaAmiciGruppoActivity.RecyclerTouchListener.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface ClickListener {
            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }

    }

}