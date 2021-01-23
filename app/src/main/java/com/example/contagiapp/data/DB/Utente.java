package com.example.contagiapp.data.DB;

import androidx.appcompat.app.AppCompatActivity;

public class Utente extends AppCompatActivity {
    private String nome;
    private String cognome;
    private String sesso;
    private String dataNascita;
    private String password;
    private String mail;
    private String nazione;
    private String regione;
    private String provincia;
    private String citta;
    private String numeroCell;

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getSesso() {
        return sesso;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public String getNazione() {
        return nazione;
    }

    public String getRegione() {
        return regione;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getCitta() {
        return citta;
    }

    public String getNumeroCell() {
        return numeroCell;
    }
}
