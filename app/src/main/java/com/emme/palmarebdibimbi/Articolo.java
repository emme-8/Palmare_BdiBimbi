package com.emme.palmarebdibimbi;

import java.io.Serializable;
import java.util.ArrayList;

public class Articolo implements Serializable {

    String codArt, desc, idDoc, numDoc;
    Integer of, oc, es, qtaDoc, qtaLetta, qtaDaScal;
    Double prz;
    ArrayList<String> ean;

    public Articolo(String codArt, String desc, int of, int oc, int qtaLetta, int qtaDoc, int es, ArrayList<String> ean, double prz, String idDoc, String numDoc){
        this.codArt = codArt;
        this.desc = desc;
        this.of = of;
        this.es = es;
        this.oc = oc;
        this.qtaDoc = qtaDoc;
        this.qtaLetta = qtaLetta;
        this.ean = ean;
        this.idDoc = idDoc;
        this.numDoc = numDoc;
        this.prz = prz;
        this.qtaDaScal = qtaDoc;
    }

    public Articolo(String codArt, String desc, ArrayList<String> ean, Integer es){
        this.codArt = codArt;
        this.desc = desc;
        this.ean = ean;
        this.es = es;
    }

    public void setCodArt(String codArt){
        this.codArt = codArt;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }

    public void setEan(String ean){
        this.ean.add(ean);
    }

    public void setOf(int of){
        this.of = of;
    }

    public void setOc(int oc){
        this.oc = oc;
    }

    public void setEs(int es){
        this.es = es;
    }

    public void setQtaDoc(Integer qtaDoc){
        this.qtaDoc = qtaDoc;
    }

    public void setQtaLetta(Integer qtaLetta){
        this.qtaLetta = qtaLetta;
    }

    public Integer getOf(){
        return of;
    }

    public Double getPrz(){ return prz; }

    public void setQtaDaScal(Integer qtaDaScal){
        this.qtaDaScal = qtaDaScal;
    }

    public Integer getQtaDaScal(){
        return qtaDaScal;
    }

    public Integer getEs(){
        return es;
    }

    public Integer getQtaDoc(){
        return qtaDoc;
    }

    public Integer getQtaLetta(){
        return qtaLetta;
    }

    public Integer getOc(){
        return oc;
    }

    public String getCodArt(){
        return codArt;
    }

    public String getDesc(){
        return desc;
    }

    public String getIdDoc(){
        return idDoc;
    }

    public String getNumDoc(){
        return numDoc;
    }

    public String getEan(int index){
        return ean.get(index);
    }

    public ArrayList<String> getAllEan(){
        return ean;
    }
}
