package com.example.contagiapp.gruppi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.contagiapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import es.dmoral.toasty.Toasty;
import androidx.core.widget.ContentLoadingProgressBar;

public class CreaGruppoActivity extends AppCompatActivity {

    TextInputEditText editTextNomeGruppo;
    TextInputEditText editTextDescrGruppo;
    MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_gruppo);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImgGruoup();
            }
        });

    }

    public void addImgGruoup() {
        editTextNomeGruppo = findViewById(R.id.editTextNomeGruppo);
        editTextDescrGruppo = findViewById(R.id.editTextDescrGruppo);
        String nomeGruppo = editTextNomeGruppo.getText().toString();
        String descrGruppo = editTextDescrGruppo.getText().toString();

        controlloEditText(nomeGruppo, descrGruppo);
    }

    public void controlloEditText(String nomeGruppo, String descrGruppo) {
        TextInputLayout textInputLayoutNome = findViewById(R.id.TextLayoutNomeGruppo);
        TextInputLayout textInputLayoutDesc = findViewById(R.id.TextLayoutDescrGruppo);

        if ((!nomeGruppo.isEmpty()) && (!descrGruppo.isEmpty())) {
            textInputLayoutNome.setErrorEnabled(false);
            textInputLayoutDesc.setErrorEnabled(false);

            //Apro activity AddImgGruppoActivity
            Intent imgIntent = new Intent(CreaGruppoActivity.this, AddImgGruppoActivity.class);
            imgIntent.putExtra("nomeGruppo", nomeGruppo);
            imgIntent.putExtra("descrGruppo", descrGruppo);
            Log.d("sonoQUI", "prima dello start");
            startActivity(imgIntent);
        } else {
            if (nomeGruppo.isEmpty() && descrGruppo.isEmpty()) {
                Toasty.warning(CreaGruppoActivity.this, "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutNome.setError("Inserisci nome del gruppo");

                Toasty.warning(CreaGruppoActivity.this, "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
            } else {

                if (nomeGruppo.isEmpty()) {
                    Toasty.warning(CreaGruppoActivity.this, "Inserisci nome del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutNome.setError("Inserisci nome del gruppo");
                    textInputLayoutDesc.setErrorEnabled(false);


                }
                if (descrGruppo.isEmpty()) {
                    Toasty.warning(CreaGruppoActivity.this, "Inserisci descrizione del gruppo", Toast.LENGTH_SHORT).show();
                    textInputLayoutDesc.setError("Inserisci descrizione del gruppo");
                    textInputLayoutNome.setErrorEnabled(false);
                }
            }
        }
    }
}