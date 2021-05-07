package com.example.contagiapp.gruppi;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.contagiapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class ModificaGruppoFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ModificaGruppoFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view;
        view = inflater.inflate(R.layout.fragment_modifica_gruppo, container, false);

        final EditText nome = view.findViewById(R.id.editTextModificaNomeGruppo);
        final EditText descr = view.findViewById(R.id.editTextModificaDescrGruppo);

        Bundle bundle = getArguments();
        final String idGruppo = bundle.getString("idGruppo");
        Log.d("idGruppo ModificaFrag", String.valueOf(idGruppo));


        //carica il gruppo
        db.collection("Gruppo")
                .document(idGruppo)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Gruppo gruppo = documentSnapshot.toObject(Gruppo.class);
                        Log.d("gruppo.getNomeGruppo()", gruppo.getNomeGruppo());

                        nome.setText(gruppo.getNomeGruppo());
                        descr.setText(gruppo.getDescrizione());
                    }
                });


        MaterialButton btnSalvaModifiche = view.findViewById(R.id.btnSalvaModifiche);
        btnSalvaModifiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifica(idGruppo, nome, descr, view);
            }
        });


        return view;
    }

    private void modifica(String idGruppo, EditText nome, EditText descr, View view){

        if(controlloEditText(nome.getText().toString(), descr.getText().toString(), view)){
            db.collection("Gruppo")
                    .document(idGruppo)
                    .update("nomeGruppo", nome.getText().toString(), "descrizione", descr.getText().toString());
            Toasty.success(getActivity(), "Gruppo modificato", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean controlloEditText(String nomeGruppo, String descrGruppo, View view) {
        TextInputLayout textInputLayoutNome = view.findViewById(R.id.TextLayoutModificaNomeGruppo);
        TextInputLayout textInputLayoutDesc = view.findViewById(R.id.TextLayoutModificaDescrGruppo);
        boolean isValid = false;

        if ((!nomeGruppo.isEmpty()) && (!descrGruppo.isEmpty())) {
            textInputLayoutNome.setErrorEnabled(false);
            textInputLayoutDesc.setErrorEnabled(false);
            isValid = true;

        } else {
            if (nomeGruppo.isEmpty() && descrGruppo.isEmpty()) {
                Toasty.warning(getActivity(), "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutNome.setError("Inserisci nome del gruppo");

                Toasty.warning(getActivity(), "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
            } else {

                if (nomeGruppo.isEmpty()) {
                    Toasty.warning(getActivity(), "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutNome.setError("Inserisci nome del gruppo");
                    textInputLayoutDesc.setErrorEnabled(false);


                }
                if (descrGruppo.isEmpty()) {
                    Toasty.warning(getActivity(), "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
                    textInputLayoutNome.setErrorEnabled(false);
                }
            }
        }
        return isValid;
    }

}