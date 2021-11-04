package com.batsoftware.contagiapp.utente;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Utente {
    private String citta;
    private String cognome;
    private String dataNascita;
    private String genere;
    private String mail;
    private String nome;
    private String password;
    private String province;
    private String regione;
    private String telefono;
    private String mailPath;
    private String stato;
    private ArrayList<String> amici;
    private ArrayList<String> richiesteRicevute;
    private ArrayList<String> invitiRicevuti;
    private String dataNegativita;
    private String dataPositivita;

    public Utente() {
    }

    public String getDataNegativita() {
        return dataNegativita;
    }

    public void setDataNegativita(String dataNegativita) {
        this.dataNegativita = dataNegativita;
    }

    public String getDataPositivita() {
        return dataPositivita;
    }

    public void setDataPositivita(String dataPositivita) {
        this.dataPositivita = dataPositivita;
    }

    public ArrayList<String> getInvitiRicevuti() {
        return invitiRicevuti;
    }

    public void setInvitiRicevuti(ArrayList<String> invitiRicevuti) {
        this.invitiRicevuti = invitiRicevuti;
    }
    public void addInvito(String idGruppo){
        invitiRicevuti.add(idGruppo);
    }

    public void rimuoviInvito(String idGruppo){
        invitiRicevuti.remove(idGruppo);
    }

    public ArrayList<String> getRichiesteRicevute() {
        return richiesteRicevute;
    }

    public void setRichiesteRicevute(ArrayList<String> richiesteRicevute) {
        this.richiesteRicevute = richiesteRicevute;
    }

    public void addRichiesta(String mailMittente){
        richiesteRicevute.add(mailMittente);
    }

    public void rimuoviRichiesta(String mailMittente){
        richiesteRicevute.remove(mailMittente);
    }

    public ArrayList<String> getAmici() {
        return amici;
    }

    public void setAmici(ArrayList<String> amici) {
        this.amici = amici;
    }

    public void addAmico(String mailAmico){
        amici.add(mailAmico);
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMailPath() { return mailPath; }

    public void setMailPath(String mailPath) { this.mailPath = mailPath; }

    public int getAge(){
        int age;
        GregorianCalendar cal = new GregorianCalendar(); //per ottenere il giorno di oggi

        String dataNascita = getDataNascita();
        String [] values = dataNascita.split("/");
        int giornoMese = Integer.parseInt(values[0]);
        int mese = Integer.parseInt(values[1]);
        int anno = Integer.parseInt(values[2]);


        age = cal.get(Calendar.YEAR) - anno;
        if(cal.get(Calendar.MONTH) == mese){
            if(cal.get(Calendar.DAY_OF_MONTH) < giornoMese){
                age --;
            }
        }else if(cal.get(Calendar.MONTH) < mese){
            age --;
        }

        return age;
    }



  //  public ImageView getPropic(){ return propic; }

   // public void setPropic(ImageView propic){ this.propic= propic;  }

    public int statoToNumber(){
        int numberStato = 0;
        switch (getStato()){
            case "rosso":
                numberStato = 4;
                break;
            case "arancione":
                numberStato = 3;
                break;
            case "giallo":
                numberStato = 2;
                break;
            case "verde":
                numberStato = 1;
                break;
            default:
                numberStato = 0;
                break;
        }
        return numberStato;
    }


}
