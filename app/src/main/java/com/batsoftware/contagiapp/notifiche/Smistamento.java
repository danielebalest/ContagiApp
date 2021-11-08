package com.batsoftware.contagiapp.notifiche;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.batsoftware.contagiapp.eventi.EliminazionePartecipazioneEvento;

public class Smistamento extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id = getIntent().getExtras().getString("id");

        Bundle bundle = new Bundle();

        Fragment fragment1 = new EliminazionePartecipazioneEvento();
        bundle.putBoolean("partenza", false);
        bundle.putString("idEvento", id);

        fragment1.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment1).commit();
    }
}
