package com.example.contagiapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.contagiapp.MainActivity;
import com.example.contagiapp.R;
import com.example.contagiapp.registrazione.RegistrationActivity;
import com.example.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG ="LoginActivity";
    private LoginViewModel loginViewModel;
    private TextInputEditText mailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout mailTextLayout;
    private TextInputLayout passwordTextLayout;
    private boolean ricord;
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
                    CheckBox ricordami = (CheckBox) findViewById(R.id.checkBox);
                    ricord = ricordami.isChecked();
                    System.out.println(ricord);
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

        //utente già registrato?
        db.collection("Utenti").whereEqualTo("mail", username).whereEqualTo("password",password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshots) {
                if(querySnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Mail o password errati", Toast.LENGTH_SHORT).show();
                } else {
                    //checkbox ricordami
                    if(ricord) {
                        db.collection("Utenti").whereEqualTo("mail", username).whereEqualTo("password",password)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Utente utente = document.toObject(Utente.class);

                                        //TODO non cancellate i commenti in questo try
                                        try {
                                            FileOutputStream osw1 = openFileOutput("Utente", MODE_PRIVATE);
                                            OutputStreamWriter osw = new OutputStreamWriter(osw1);
                                            osw.write(utente.getMail());
                                            osw.write(utente.getPassword());

                                            //questo toast può essere cancellato ma per il momento lasciatelo
                                            Toast.makeText(getApplicationContext(), "Saved to " + getFilesDir() + "/Utente", Toast.LENGTH_LONG).show();

                                            osw.flush();
                                            osw.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
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