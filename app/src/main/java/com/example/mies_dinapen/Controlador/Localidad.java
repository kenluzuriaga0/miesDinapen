package com.example.mies_dinapen.Controlador;

import java.io.Serializable;

public class Localidad implements Serializable {

   short IDLugar;
   int cedula;
   double Latitud;
   double Longitud;

    public Localidad(short IDLugar, int cedula, double latitud, double longitud) {
        this.IDLugar = IDLugar;
        this.cedula = cedula;
        Latitud = latitud;
        Longitud = longitud;
    }

    public Localidad() {
    }

    public short getIDLugar() {
        return IDLugar;
    }

    public void setIDLugar(short IDLugar) {
        this.IDLugar = IDLugar;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        Longitud = longitud;
    }
}
