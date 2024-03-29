package com.batsoftware.contagiapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.batsoftware.contagiapp.MainActivity;
import com.batsoftware.contagiapp.R;
import com.batsoftware.contagiapp.registrazione.RegistrationActivity;
import com.batsoftware.contagiapp.utente.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

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
            mailTextLayout.setError(getText(R.string.enter_mail));
            passwordTextLayout.setError(null);
            return 1;
        }

        if(isEmpty(passwordEditText) == true && isEmpty(mailEditText) == false){
            passwordTextLayout.setError(getText(R.string.enter_password));
            mailTextLayout.setError(null);
            return 2;
        }else
            passwordTextLayout.setError(null);

        if(isEmpty(mailEditText) && isEmpty(passwordEditText) == true){
            mailTextLayout.setError(getText(R.string.enter_mail));
            passwordTextLayout.setError(getText(R.string.enter_password));
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
                    Toast.makeText(getApplicationContext(), getText(R.string.incorrect_mail_or_password), Toast.LENGTH_SHORT).show();
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

                                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        Gson gson = new Gson();
                                        String json = gson.toJson(utente);
                                        editor.putString("utente", json);
                                        editor.commit ();
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    } else {
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("LoginTemporaneo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("mail", username);
                        editor.commit ();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            }
        });
    }

    public void openRegistration(){
        Intent signUpIntent = new Intent(this, RegistrationActivity.class);
        startActivity(signUpIntent);
    }
}