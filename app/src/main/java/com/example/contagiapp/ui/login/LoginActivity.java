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
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileOutputStream;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG ="LoginActivity";
    private LoginViewModel loginViewModel;
    private TextInputEditText mailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout mailTextLayout;
    private TextInputLayout passwordTextLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        mailEditText = findViewById(R.id.mail);
        passwordEditText = findViewById(R.id.password);
        mailTextLayout = findViewById(R.id.textFieldMail);
        passwordTextLayout = findViewById(R.id.textFieldPassword);


        final Button loginButton = findViewById(R.id.login);
        final Button createAccountButton = findViewById(R.id.createAccount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(controlloTextFieldVuoto(mailEditText, passwordEditText) == 0){
                    mailTextLayout.setError(null);
                    passwordTextLayout.setError(null);
                    openMain();
                }


            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistration();
            }
        });
    }

    private int controlloTextFieldVuoto(TextInputEditText mailEditText, TextInputEditText passwordEditText){

        if(isEmpty(mailEditText) == true && isEmpty(passwordEditText) == false ){
            mailTextLayout.setError("Inserisci mail");
            passwordTextLayout.setError(null);
            return 1;
        }

        if(isEmpty(passwordEditText) == true && isEmpty(mailEditText) == false){
            passwordTextLayout.setError("Inserisci password");
            mailTextLayout.setError(null);
            return 2;
        }else
            passwordTextLayout.setError(null);

        if(isEmpty(mailEditText) && isEmpty(passwordEditText) == true){
            mailTextLayout.setError("Inserisci mail");
            passwordTextLayout.setError("Inserisci password");
            return 3;
        }
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
        final String username = mailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        System.out.println("username in input:"+username);
        System.out.println("password in input:"+password);

        db.collection("Utenti").whereEqualTo("mail", username).whereEqualTo("password",password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) { System.out.println(querySnapshots.isEmpty());
                if(querySnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mail o password errati", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("Utenti").whereEqualTo("mail", username).whereEqualTo("password",password)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Utente utente = document.toObject(Utente.class);
                                            //FileOutputStream openFileOutput = new FileOutputStream("FileUtente.txt", "r");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }
        });
    }

    public void openRegistration(){
        Intent signUpIntent = new Intent(this, RegistrationActivity.class);
        startActivity(signUpIntent);
    }
}