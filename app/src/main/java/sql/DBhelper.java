package Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper
{
    public static final String DBNAME = "Contagi";

    public DBhelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {  //boolean = integers 0 (false) and 1 (true)

        String utenti = "create table utenti (id INTEGER PRIMARY KEY AUTOINCREMENT, nome text not null, " +
                "cognome text not null, cellulare integer unique, data_nascita text not null, citta_nascita text not null, " +
                "mail text not null unique, genere text not null, foto_tampone text, consenso integer not null, data_iscrizione text not null, " +
                "data_foto text, colore integer not null, nazione_residenza text not null, regione_residenza text not null, " +
                "citta_residenza text not null, via_residenza text not null)";

        String amici = "create table amici (id_utente integer, id_utente_amico integer, primary key(id_utente, id_utente_amico), " +
                "foreign key (id_utente) references utenti(id))";

        String gruppi = "create table gruppi (id INTEGER PRIMARY KEY AUTOINCREMENT, nome text not null, data_creazione text not null, " +
                "num_utenti integer not null, descrizione text not null)";

        String utenti_gruppi = "create table utenti_gruppi (id_utente integer, id_gruppo integer, primary key(id_utente, id_gruppo), " +
                "foreign key (id_utente) references utenti(id), foreign key (id_gruppo) references gruppi(id))";

        String eventi = "create table eventi (id  INTEGER PRIMARY KEY AUTOINCREMENT, data text not null, ora_inizio integer not null, ora_fine integer not null, " +
                "nome text not null, num_partecipanti_max integer not null, descrizione text not null, nazione text not null, " +
                "regione text not null, citta text not null, via text not null, luogo text not null)";

        String utenti_eventi = "create table utenti_eventi (id_utente integer, id_evento integer, partecipazione_gps integer not null, " +
                "primary key(id_utente, id_evento), foreign key (id_utente) references utenti(id), " +
                "foreign key (id_evento) references eventi(id))";

        String eventi_gruppi = "create table eventi_gruppi (id_gruppo integer, id_evento integer, primary key(id_gruppo, id_evento), " +
                "foreign key (id_gruppo) references gruppi(id), foreign key (id_evento) references eventi(id))";

        String codici = "create table codici (id  INTEGER PRIMARY KEY AUTOINCREMENT, codice integer unique, id_utente integer not null, " +
                "foreign key (id_utente) references utenti(id))";

        String bluetooth = "create table bluetooth (id_codice integer, id_codice_1 integer, data text, durata_contatto integer not null, distanza integer not null, " +
                "primary key(id_codice, id_codice_1, data), foreign key (id_codice) references codici(id))";

        db.execSQL(utenti);
        db.execSQL(amici);
        db.execSQL(gruppi);
        db.execSQL(utenti_gruppi);
        db.execSQL(eventi);
        db.execSQL(utenti_eventi);
        db.execSQL (eventi_gruppi);
        db.execSQL(codici);
        db.execSQL(bluetooth);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    { }
}