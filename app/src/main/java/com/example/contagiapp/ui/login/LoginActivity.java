package com.example.contagiapp.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contagiapp.HomeFragment;
import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.registrazione.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG ="LoginActivity";
    private LoginViewModel loginViewModel;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout usernameTextLayout;
    private TextInputLayout passwordTextLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        usernameTextLayout = findViewById(R.id.textFieldUsername);
        passwordTextLayout = findViewById(R.id.textFieldPassword);


        final Button loginButton = findViewById(R.id.login);
        final Button createAccountButton = findViewById(R.id.createAccount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
                /*
                switch (controlli(usernameEditText, passwordEditText)){
                    case 1:
                        usernameTextLayout.setError("Inserisci username");

                    case 2:
                        passwordTextLayout.setError("Inserisci password");
                        break;
                    case 0:
                        openMain();
                }

                 */


            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistration();
            }
        });
    }

    private int controlli(TextInputEditText usernameEditText, TextInputEditText passwordEditText){
        if(isEmpty(usernameEditText) == true){
            return 1;
        }else
            usernameTextLayout.setError(null);

        if(isEmpty(passwordEditText) == true){
            return 2;
        }else
            usernameTextLayout.setError(null);

        return 0;
    }

    private boolean isEmpty(TextInputEditText etText) {
        if(etText.getText().toString().length() > 0)
            return false;
        return true;
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    public void openMain(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        System.out.println("username in input:"+username);
        System.out.println("password in input:"+password);
        //final Intent mainIntent = new Intent(this, HomeFragment.class);
        final Fragment fragment = new HomeFragment();
        db.collection("Utenti").whereEqualTo("mail", username).whereEqualTo("password",password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) { System.out.println(querySnapshots.isEmpty());
                if(querySnapshots.isEmpty()) {
                    //setContentView(R.layout.activity_login);
                    Toast.makeText(getApplicationContext(), "Mail o password errati", Toast.LENGTH_SHORT).show();
                } else {
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    //fragment = new HomeFragment();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    //inflater.inflate(R.layout.my_first_fragment, container, false);
                    //setContentView(R.layout.fragment_home);
                }
            }
        });
    }

    public void openRegistration(){
        Intent signUpIntent = new Intent(this, RegistrationActivity.class);
        startActivity(signUpIntent);
    }
}