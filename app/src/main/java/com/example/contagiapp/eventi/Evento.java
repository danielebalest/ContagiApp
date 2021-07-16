package com.example.contagiapp.eventi;

import java.util.ArrayList;

public class Evento {
    private String idEvento;
    private String nome;
    private String descrizione;
    private String citta;
    private String indirizzo;
    private String data;
    private String orario;
    private String admin;
    private ArrayList<String> partecipanti;
    private int numeroMaxPartecipanti;
    private int numPartecipanti;
    private int numeroPostiDisponibili = numeroMaxPartecipanti - numPartecipanti;


    public Evento(){
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public ArrayList<String> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(ArrayList<String> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void aggiornaNroPartecipanti(ArrayList<String> partecipanti) {
        this.numPartecipanti = partecipanti.size();
    }

    public void addPartecipantiGruppo(ArrayList<String> partecipantiGruppo){
        getPartecipanti().addAll(partecipantiGruppo);
    }

    public int getNumeroMaxPartecipanti() {
        return numeroMaxPartecipanti;
    }

    public void setNumeroMaxPartecipanti(int numeroMaxPartecipanti) {
        this.numeroMaxPartecipanti = numeroMaxPartecipanti;
    }

    public int getNumPartecipanti() {
        return numPartecipanti;
    }

    public void setNumPartecipanti(int numPartecipanti) {
        this.numPartecipanti = numPartecipanti;
    }

    public int getNumeroPostiDisponibili() {
        return numeroPostiDisponibili;
    }

    public void setNumeroPostiDisponibili(int numeroMaxPartecipanti, int numParticipanti) {
        this.numeroPostiDisponibili = numeroMaxPartecipanti - numParticipanti ;
    }

}
