package com.example.contagiapp.gruppi;

import java.util.ArrayList;

public class Gruppo {
    private String idGruppo;
    private String admin;
    private String nomeGruppo;
    private String descrizione;
    private ArrayList<String> partecipanti;
    private int nroPartecipanti;
    private String statoGruppo;


    public Gruppo() {
    }

    public String getIdGruppo() {
        return idGruppo;
    }

    public void setIdGruppo(String idGruppo) {
        this.idGruppo = idGruppo;
    }

    public Gruppo(String nomeGruppo) {
        this.nomeGruppo = nomeGruppo;
    }

    public static int lastId = 0;

    public static ArrayList<Gruppo> createName(int nameNumber){
        ArrayList<Gruppo> name = new ArrayList<>();

        for(int i=0; i<nameNumber; i++){
            name.add(new Gruppo("Nome persona" + i));
        }
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getNomeGruppo() {
        return nomeGruppo;
    }

    public void setNomeGruppo(String nomeGruppo) {
        this.nomeGruppo = nomeGruppo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public ArrayList<String> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(ArrayList<String> partecipanti) {
        this.partecipanti = partecipanti;
    }

    public void addPartecipante(String mailPartecipante){
        partecipanti.add(mailPartecipante);
    }

    public int getNroPartecipanti() {
        return nroPartecipanti;
    }

    public void setNroPartecipanti(int nroPartecipanti) {
        this.nroPartecipanti = nroPartecipanti;
    }

    public void aggiornaNroPartecipanti(ArrayList<String> partecipanti) {
        this.nroPartecipanti = partecipanti.size();
    }

    public String getStatoGruppo() {
        return statoGruppo;
    }

    public void setStatoGruppo(String statoGruppo) {
        this.statoGruppo = statoGruppo;
    }


}
