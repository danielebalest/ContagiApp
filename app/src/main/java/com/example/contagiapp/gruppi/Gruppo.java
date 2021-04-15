package com.example.contagiapp.gruppi;

import java.util.ArrayList;

public class Gruppo {
    private String admin;
    private String nomeGruppo;
    private String descrizione;
    private int nroPartecipanti;

    public Gruppo() {
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

    public int getNroPartecipanti() {
        return nroPartecipanti;
    }

    public void setNroPartecipanti(int nroPartecipanti) {
        this.nroPartecipanti = nroPartecipanti;
    }
}
