package com.example.contagiapp.eventi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.contagiapp.R;
import com.example.contagiapp.ui.login.LoginViewModel;

public class NewEventsFragment extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_events);
        /*loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login2);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);*/

    }
}