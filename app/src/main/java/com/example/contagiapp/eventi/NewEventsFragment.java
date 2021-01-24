package com.example.contagiapp.eventi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.contagiapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewEventsFragment extends AppCompatActivity {

    Button creaEvento;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);

        // collegamento button registrati con la mainActivity
        creaEvento = (Button) findViewById(R.id.buttonCreaEvento);
        creaEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {

    }
}