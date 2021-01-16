package com.example.contagiapp.eventi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.contagiapp.R;

public class NewEventsFragment extends Fragment {

    //private LoginViewModel loginViewModel;
    public void NewEventsFragment(View v) {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);

        return inflater.inflate(R.layout.fragment_new_events, container, false);
        //setContentView(R.layout.fragment_new_events);
        /*loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login2);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);*/

    }
}