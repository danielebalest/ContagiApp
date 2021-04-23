package com.example.contagiapp.eventi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.example.contagiapp.data.amici.ProfiloUtentiActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfiloEventoFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfiloEventoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_profilo_evento, container, false);

        Bundle bundle = getArguments();
        String idEvento = bundle.getString("idEvento");
        Log.d("idEvento", String.valueOf(idEvento));

        caricaEvento(idEvento, view);

        // Inflate the layout for this fragment
        return view;
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