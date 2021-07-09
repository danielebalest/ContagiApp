package com.example.contagiapp.eventi;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.UserAdapter;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfiloEventoAdminFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "eventi";
    public Evento evento;
    RecyclerView rvPartecipantiProfiloEventoAdmin;

    public ProfiloEventoAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profilo_evento_admin, container, false);

        Bundle bundle = getArguments();
        final String idEvento = bundle.getString("idEvento");

        caricaEvento(idEvento, view);
        caricaPartecipanti(idEvento);

        final ImageView img = view.findViewById(R.id.imgProfiloEventoAdmin);
        rvPartecipantiProfiloEventoAdmin = view.findViewById(R.id.rvPartecipantiProfiloEventoAdmin);

        db.collection("Eventi").document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                //recupero l'immagine dallo storage
                Log.d("eventi/idEvento","eventi/" + idEvento);

                caricaImgDaStorage(storageRef, storageDirectory, idEvento, img );

            }
        });


        return view;
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

    private void caricaEvento(String idEvento, final View view){
        db.collection("Eventi")
                .document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    evento = documentSnapshot.toObject(Evento.class);
                    String nome = evento.getNome();
                    String descrizione = evento.getDescrizione();
                    String data = evento.getData();
                    String orario = evento.getOrario();
                    String indirizzo = evento.getIndirizzo();
                    String citta = evento.getCitta();

                    TextView tvNomeEvento = view.findViewById(R.id.tvNomeEventoAdmin);
                    TextView tvDescrEvento = view.findViewById(R.id.tvDescrEventoAdmin);
                    TextView tvDataEvento = view.findViewById(R.id.tvDataEventoAdmin);
                    TextView tvOrarioEvento = view.findViewById(R.id.tvOrarioEventoAdmin);
                    TextView tvIndirizzoEvento = view.findViewById(R.id.tvIndirizzoEventoAdmin);
                    TextView tvCittaEvento = view.findViewById(R.id.tvCittaEventoAdmin);

                    tvNomeEvento.setText(nome);
                    tvDescrEvento.setText(descrizione);
                    tvDataEvento.setText(data);
                    tvOrarioEvento.setText(orario);
                    tvIndirizzoEvento.setText(indirizzo);
                    tvCittaEvento.setText(citta);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void caricaPartecipanti(String idEvento){
        db.collection("Eventi")
                .document(idEvento)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Evento evento = documentSnapshot.toObject(Evento.class);

                        ArrayList<String> listaPartecipanti = evento.getPartecipanti();
                        Log.d("listaPartecipanti", String.valueOf(listaPartecipanti));

                        //recuperare dalle mail l'oggetto utente
                        final ArrayList<Utente> listaUtenti = new ArrayList<Utente>();

                        for(int i=0; i < listaPartecipanti.size(); i++){
                            db.collection("Utenti")
                                    .whereEqualTo("mail", listaPartecipanti)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            
                                        }
                                    });

                        }
                        Log.d("listaUtenti2", String.valueOf(listaUtenti));

                        //recyclerView
                        //UserAdapter adapter = new UserAdapter(listaPartecipanti);


                    }
                });

    }

    //toDo: devo fare RW per visualizzare


}