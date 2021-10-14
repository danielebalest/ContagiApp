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
    private ArrayList<String> gruppiPartecipanti;
    private boolean statoRosso;
    private String dataRosso;

    public boolean getStatoRosso() {
        return statoRosso;
    }

    public void setStatoRosso(boolean statoRosso) {
        this.statoRosso = statoRosso;
    }

    public String getDataRosso() {
        return dataRosso;
    }

    public void setDataRosso(String dataRosso) {
        this.dataRosso = dataRosso;
    }

    public ArrayList<String> getGruppiPartecipanti() {
        return gruppiPartecipanti;
    }

    public void setGruppiPartecipanti(ArrayList<String> gruppiPartecipanti) {
        this.gruppiPartecipanti = gruppiPartecipanti;
    }

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

    public int getNumeroMaxPartecipanti() {
        return numeroMaxPartecipanti;
    }

    public void setNumeroMaxPartecipanti(int numeroMaxPartecipanti) {
        this.numeroMaxPartecipanti = numeroMaxPartecipanti;
    }
}
