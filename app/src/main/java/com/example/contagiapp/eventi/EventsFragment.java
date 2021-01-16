package com.example.contagiapp.eventi;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contagiapp.R;

public class EventsFragment extends Fragment {

    public class ciao extends AppCompatActivity {
        private Button new_event;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_events);

            //implementazione OnClick
            new_event = (Button) findViewById(R.id.nuovo_evento);
            new_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openNuovoEvento();
                }
            });

        }

        public void openNuovoEvento() {
            Intent newEvents = new Intent(this, NewEventsFragment.class);
            startActivity(newEvents);
        }
    }
}
