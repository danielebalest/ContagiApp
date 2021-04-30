package com.example.contagiapp.eventi;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.ProfiloUtentiActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;


public class ProfiloEventoFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final static String storageDirectory = "eventi";

    public ProfiloEventoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_profilo_evento, container, false);

        Bundle bundle = getArguments();
        final String idEvento = bundle.getString("idEvento");
        Log.d("idEvento", String.valueOf(idEvento));

        caricaEvento(idEvento, view);


        final ImageView img = view.findViewById(R.id.imgProfiloEvento);


        db.collection("Eventi").document(idEvento)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                //recupero l'immagine dallo storage
                Log.d("eventi/idEvento","eventi/"+idEvento);

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

                    Evento evento = documentSnapshot.toObject(Evento.class);
                    String nome = evento.getNome();
                    String descrizione = evento.getDescrizione();
                    String data = evento.getData();
                    String orario = evento.getOrario();
                    String indirizzo = evento.getIndirizzo();
                    String citta = evento.getCitta();

                    TextView tvNomeEvento = view.findViewById(R.id.tvNomeEvento2);
                    TextView tvDescrEvento = view.findViewById(R.id.tvDescrEvento2);
                    TextView tvDataEvento = view.findViewById(R.id.tvDataEvento);
                    TextView tvOrarioEvento = view.findViewById(R.id.tvOrarioEvento);
                    TextView tvIndirizzoEvento = view.findViewById(R.id.tvIndirizzoEvento);
                    TextView tvCittaEvento = view.findViewById(R.id.tvCittaEvento);

                    tvNomeEvento.setText(nome);
                    tvDescrEvento.setText(descrizione);
                    tvDataEvento.setText(data);
                    tvOrarioEvento.setText(orario);
                    tvIndirizzoEvento.setText(indirizzo);
                    tvCittaEvento.setText(citta);

                } else {
                    Toast.makeText(getContext(), "Documents does not exist", Toast.LENGTH_SHORT);
                }
            }
        });
    }
}