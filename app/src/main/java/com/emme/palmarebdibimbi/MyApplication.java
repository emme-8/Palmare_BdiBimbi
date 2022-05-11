package com.emme.palmarebdibimbi;

import android.app.Application;
import android.content.Intent;

import java.util.ArrayList;

public class MyApplication extends Application {

    private ArrayList<String> codArt;
    private ArrayList<String> alias;
    private ArrayList<String> quantita;
    private ArrayList<String> id;
    private ArrayList<String> num;
    private ArrayList<String> desc;
    private ArrayList<String> ubic;
    private ArrayList<String> subic;
    private ArrayList<String> serie;
    private ArrayList<String> esistenza;
    private ArrayList<String> impegnati;

    @Override
    public void onCreate() {

        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException (Thread thread, Throwable e) {
                        handleUncaughtException (thread, e);
                    }
                });
    }

    private void handleUncaughtException (Thread thread, Throwable e) {

        // The following shows what I'd like, though it won't work like this.
        Intent intent = new Intent(getApplicationContext(),IniziaSpunta.class);
        startActivity(intent);

        // Add some code logic if needed based on your requirement
    }

    public ArrayList<String> getCodArt() {
        return codArt;
    }

    public void setCodArt(ArrayList<String> codArt) {
        this.codArt = codArt;
    }

    public ArrayList<String> getQuantita() {
        return quantita;
    }

    public void setQuantita(ArrayList<String> quantita) {
        this.quantita = quantita;
    }

    public ArrayList<String> getID() {
        return id;
    }

    public void setID(ArrayList<String> id) {
        this.id = id;
    }

    public ArrayList<String> getSerie() {
        return serie;
    }

    public void setSerie(ArrayList<String> serie) {
        this.serie = serie;
    }

    public ArrayList<String> getNum() {
        return num;
    }

    public void setNum(ArrayList<String> num) {
        this.num = num;
    }

    public ArrayList<String> getDesc() {
        return desc;
    }

    public void setDesc(ArrayList<String> desc) {
        this.desc = desc;
    }

    public ArrayList<String> getUbic() {
        return ubic;
    }

    public void setUbic(ArrayList<String> ubic) {
        this.ubic = ubic;
    }

    public ArrayList<String> getSubic() {
        return subic;
    }

    public void setSubic(ArrayList<String> subic) {
        this.subic = subic;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void setAlias(ArrayList<String> alias) {
        this.alias = alias;
    }

    public ArrayList<String> getEsistenza() {
        return esistenza;
    }

    public void setEsistenza(ArrayList<String> esistenza) {
        this.esistenza = esistenza;
    }

    public ArrayList<String> getImpegnati() {
        return impegnati;
    }

    public void setImpegnati(ArrayList<String> impegnati) {
        this.impegnati = impegnati;
    }
}
