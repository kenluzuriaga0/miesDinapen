package com.example.mies_dinapen.BDSQLITE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import com.google.type.DateTime;

import java.sql.Blob;
import java.util.Date;

public class BDHELPER extends SQLiteOpenHelper {
    public static final String DBNAME="MiesDinapen.db";
    public BDHELPER (Context context) {
        super(context,"MiesDinapen.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IntervencionesAudios(Audio longblob primary key, FechaRegistro timestamp)");
        db.execSQL("create table IntervencionesFotos(IDIntervencion INT primary key, FotoIncidente longblob, FechaRegistro timestamp)");
        db.execSQL("create table ListaPoligonosLugares(IDLugar smallint primary key, Latitud decimal, Longitud decimal)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists IntervencionesAudios");
        db.execSQL("drop table if exists IntervencionesFotos");
        db.execSQL("drop table if exists ListaPoligonosLugares");
    }

    public Boolean insertDataAudios (byte [] audio, Date FechaRegistro){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Audio", String.valueOf(audio));
        values.put("FechaRegistro", String.valueOf(FechaRegistro));

        long result= db.insert("IntervencionesAudios", null, values);
        if(result==1)
            return false;
        else
            return true;
    }
    public Boolean insertDataFotos (byte [] fotos, Date FechaRegistro){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("FotoIncidente", String.valueOf(fotos));
        values.put("FechaRegistro", String.valueOf(FechaRegistro));

        long result= db.insert("IntervencionesFotos", null, values);
        if(result==1)
            return false;
        else
            return true;
    }
    public Boolean insertDataCoordenadas (String Latitud, String Longitud){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
      //  values.put("IDLugar", IDLugar);
        values.put("latitud", Latitud);
        values.put("longitud", Longitud);

        long result= db.insert("ListaPoligonosLugares", null, values);
        if(result==1)
            return false;
        else
            return true;
    }

    public Boolean checklatitud (String latitud, String longitud){
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from ListaPoligonosLugares where latitud=? and longitud=? ", new String[]{latitud, longitud});

        if(cursor.getCount()>0)
            return true;
        else
            return false;


    }



}
