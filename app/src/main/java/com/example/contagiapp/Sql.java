package com.example.contagiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class Sql extends SQLiteOpenHelper {

    public static final String DBNAME = "BILLBOOK";
    public Sql(Context context){
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q= "CREATE TABLE TABELLA1 (riga1 varchar(10), riga2 varchar(10))";
        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
