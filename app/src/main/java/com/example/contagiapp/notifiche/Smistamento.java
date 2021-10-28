package com.example.contagiapp.notifiche;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.contagiapp.R;
import com.example.contagiapp.eventi.EliminazionePartecipazioneEvento;
import com.example.contagiapp.eventi.ProfiloEventoFragment;
import com.example.contagiapp.gruppi.ProfiloGruppoFragment;

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
        //finish();
    }
}
