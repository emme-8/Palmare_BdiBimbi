package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ShowDoc extends AppCompatActivity implements Serializable {

    ConnectionClass connectionClass;
    ArrayList<String> codArt = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    ArrayList<String> serieDoc = new ArrayList<>();
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> ubicaz = new ArrayList<>();
    ArrayList<String> subic = new ArrayList<>();
    ArrayList<String> esistenza = new ArrayList<>();
    ArrayList<String> impegnati = new ArrayList<>();
    ArrayList<String> przL = new ArrayList<>();
    ArrayList<String> przP = new ArrayList<>();
    ArrayList<String> UDM = new ArrayList<>();
    ArrayList<Double> conv = new ArrayList<>();
    ArrayList<Articolo> artDoc = new ArrayList<>();
    int listino, mag, spuntaOrPresa, magRif, listinoRif;
    ListView listView;
    TextView txtInfoDic;
    String fornitore = "";
    String tipo;
    String magazzino;
    int ordinamento;
    SharedPreferences preferences;
    boolean isOnline, smartMode;
    String whereID, whereNum, whereSel;
    Context thisContext;
    Boolean ubic;
    Button btnSpunta;
    ProgressBar pbShowDoc;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doc);
        thisContext = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnSpunta = findViewById(R.id.btnSpuntaDoc);
        pbShowDoc = findViewById(R.id.pbShowDoc);
        pbShowDoc.setVisibility(View.GONE);
        listView = findViewById(R.id.showDocRow);
        txtInfoDic = findViewById(R.id.txtInfoDoc);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isOnline = preferences.getBoolean("isOnline", true);
        smartMode = preferences.getBoolean("smartMode", true);

        Bundle extras = getIntent().getExtras();
        String ids = ""; String docs = "";
        if(extras != null){
            for(int i=0; i<extras.getStringArrayList("selIds").size(); i++){
                if(i==0){
                    ids="'"+extras.getStringArrayList("selIds").get(i)+"'";
                    docs="'"+extras.getStringArrayList("selDocs").get(i)+"'";
                }else{
                    ids+=", '"+extras.getStringArrayList("selIds").get(i)+"'";
                    docs+=", '"+extras.getStringArrayList("selDocs").get(i)+"'";
                }
            }
            spuntaOrPresa = extras.getInt("spuntaOrPresa");
            mag = extras.getInt("mag");
            magRif = extras.getInt("magRif");
            fornitore = extras.getString("fornitore");
            magazzino = extras.getString("magazzino");
            tipo = extras.getString("tipo");
            listino = extras.getInt("listino");
            listinoRif = extras.getInt("listinoRif");
            ubic = extras.getBoolean("ubicazione");
            whereID = " and Documento.id in (" + ids + ") ";
            whereNum = " and Documento.numero in (" + docs + ") ";
            whereSel = " and RigaDocumento.selettore like '" + extras.getString("tipoRiga") + "' ";
        }

        txtInfoDic.setText(extras.getString("tipo") + " N. " + docs);

        connectionClass = new ConnectionClass();

        if(spuntaOrPresa == 0){
            btnSpunta.setText("INIZIA SPUNTA");
            if(isOnline){
                ShowDoc.FindRows cerca = new ShowDoc.FindRows();
                cerca.execute("");
            }else{
                ShowDoc.FindRowsOffline cerca = new ShowDoc.FindRowsOffline();
                cerca.execute("");
            }
        }else if(spuntaOrPresa == 1){
            btnSpunta.setText("INIZIA PRESA");
            ordinamento = extras.getInt("tipoOrd");
            ShowDoc.FindRowsPresa cerca = new ShowDoc.FindRowsPresa();
            cerca.execute("");
        }else if(spuntaOrPresa == 2){
            btnSpunta.setText("INIZIA INVENTARIO");
            ShowDoc.FindRows cerca = new ShowDoc.FindRows();
            cerca.execute("");
        }else{
            btnSpunta.setText("STAMPA ETICHETTE");
            ShowDoc.FindRows4Print cerca = new ShowDoc.FindRows4Print();
            cerca.execute("");
        }

        btnSpunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCodPerSpunta();
                if(ubic && spuntaOrPresa == 0){
                    Intent spunta;
                    if(smartMode){
                        spunta = new Intent(ShowDoc.this, IniziaSpuntaSmartphone.class);
                    }else{
                        spunta = new Intent(ShowDoc.this, IniziaSpunta.class);
                    }
                    spunta.putExtra("listino", listino);
                    spunta.putExtra("tipoDoc", tipo);
                    spunta.putExtra("fornitore", fornitore);
                    spunta.putExtra("mag", mag);
                    spunta.putExtra("rip", 0);
                    startActivity(spunta);
                }else if(!ubic && spuntaOrPresa == 0){
                    if(isOnline){
                        Intent spunta;
                        if(smartMode){
                            spunta = new Intent(ShowDoc.this, IniziaSpuntaNegSmart.class);
                        }else{
                            spunta = new Intent(ShowDoc.this, IniziaSpuntaNeg.class);
                        }
                        spunta.putExtra("listino", listino);
                        spunta.putExtra("tipoDoc", tipo);
                        spunta.putExtra("mag", mag);
                        spunta.putExtra("fornitore", fornitore);
                        spunta.putExtra("rip", 0);
                        startActivity(spunta);
                    }else{
                        Intent spuntaOff = new Intent(ShowDoc.this, IniziaSpuntaOffline.class);
                        spuntaOff.putExtra("listino", listino);
                        spuntaOff.putExtra("tipoDoc", tipo);
                        spuntaOff.putExtra("fornitore", fornitore);
                        spuntaOff.putExtra("mag", mag);
                        spuntaOff.putExtra("rip", 0);
                        spuntaOff.putExtra("MyClass", artDoc);
                        startActivity(spuntaOff);
                    }
                }else if(spuntaOrPresa==1){
                    Intent presa;
                    if(smartMode){
                        presa = new Intent(ShowDoc.this, IniziaPresaSmart.class);
                    }else{
                        presa = new Intent(ShowDoc.this, IniziaPresa.class);
                    }
                    presa.putExtra("listino", listino);
                    presa.putExtra("mag", mag);
                    presa.putExtra("listinoRif", listinoRif);
                    presa.putExtra("magRif", magRif);
                    presa.putExtra("fornitore", fornitore);
                    presa.putExtra("tipoDoc", tipo);
                    presa.putExtra("rip", 0);
                    presa.putExtra("magazzino", magazzino);
                    startActivity(presa);
                }else{
                    Intent inv = new Intent(ShowDoc.this, IniziaInventario.class);
                    inv.putExtra("listino", listino);
                    inv.putExtra("tipoDoc", tipo);
                    inv.putExtra("mag", mag);
                    inv.putExtra("fornitore", fornitore);
                    inv.putExtra("rip", 0);
                    startActivity(inv);
                }
            }
        });
    }

    public void setCodPerSpunta(){
        if(spuntaOrPresa == 0){
            ((MyApplication) this.getApplication()).setCodArt(codArt);
            ((MyApplication) this.getApplication()).setQuantita(qta);
            ((MyApplication) this.getApplication()).setID(idDoc);
            ((MyApplication) this.getApplication()).setNum(numDoc);
            ((MyApplication) this.getApplication()).setSerie(serieDoc);
            ((MyApplication) this.getApplication()).setDesc(desc);
        }else if(spuntaOrPresa == 1){
            ((MyApplication) this.getApplication()).setCodArt(codArt);
            ((MyApplication) this.getApplication()).setAlias(alias);
            ((MyApplication) this.getApplication()).setSerie(serieDoc);
            ((MyApplication) this.getApplication()).setQuantita(qta);
            ((MyApplication) this.getApplication()).setID(idDoc);
            ((MyApplication) this.getApplication()).setNum(numDoc);
            ((MyApplication) this.getApplication()).setDesc(desc);
            ((MyApplication) this.getApplication()).setUbic(ubicaz);
            ((MyApplication) this.getApplication()).setSubic(subic);
            ((MyApplication) this.getApplication()).setEsistenza(esistenza);
            ((MyApplication) this.getApplication()).setImpegnati(impegnati);
        }else{
            ((MyApplication) this.getApplication()).setCodArt(codArt);
            ((MyApplication) this.getApplication()).setQuantita(qta);
            ((MyApplication) this.getApplication()).setID(idDoc);
            ((MyApplication) this.getApplication()).setNum(numDoc);
            ((MyApplication) this.getApplication()).setDesc(desc);
        }
    }

    public void setRows(){
        if(spuntaOrPresa == 0){
            if(!isOnline){
                for(int i=0; i<artDoc.size(); i++){
                    codArt.add(artDoc.get(i).getCodArt());
                    desc.add(artDoc.get(i).getDesc());
                    qta.add(artDoc.get(i).getQtaDoc().toString());
                    idDoc.add(artDoc.get(i).getIdDoc());
                    numDoc.add(artDoc.get(i).getNumDoc());
                }
            }
            AdapterShowDoc whatever = new AdapterShowDoc(this, codArt, desc, qta);
            listView.setAdapter(whatever);
        }else if(spuntaOrPresa == 1){
            AdapterShowDocPresa adapterShowDocPresa = new AdapterShowDocPresa(this, codArt, desc, qta, ubicaz, subic);
            listView.setAdapter(adapterShowDocPresa);
        }else if(spuntaOrPresa == 2){
            AdapterShowDoc whatever = new AdapterShowDoc(this, codArt, desc, qta);
            listView.setAdapter(whatever);
        }else{
            AdapterShowDocPrintLabel whatever = new AdapterShowDocPrintLabel(this, codArt, desc, alias, qta, esistenza, przL, przP, UDM, conv);
            listView.setAdapter(whatever);
        }
    }

    public class FindRowsOffline extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            listView.setVisibility(View.GONE);
            btnSpunta.setVisibility(View.GONE);
            pbShowDoc.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                pbShowDoc.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                btnSpunta.setVisibility(View.VISIBLE);
                setRows();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(thisContext);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome as codiceArticolo, articolo.descrizione, quantita, documento.id, documento.numero, \n" +
                            " alias.codice, RigaDocumento.id as idRiga, OrdinatoFornitoreArticoloXMagazzino, OrdinatoClienteArticoloXMagazzino, esistenza, ProgressivoArticolo.PrezzoBase, metaMagazzino \n" +
                            "from RigaDocumento join Documento on (RigaDocumento.idMaster = Documento.id) \n" +
                            "join articolo on (articolo.id = RigaDocumento.idArticolo) \n" +
                            "left join alias on (alias.idArticolo = articolo.id) \n" +
                            "left join progressivoarticolo on (progressivoarticolo.metaarticolo = articolo.id) \n" +
                            "where Documento.id is not null " + whereID + whereNum + whereSel +
                            " and codiceArticolo not like '.%' and codiceArticolo not like ',%' " +
                            "order by idRiga";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int count = 0;
                    int i = 0;
                    ArrayList<String> firstEan;
                    String eanPrec = "";
                    String rigaPrec = "";
                    while(res.next()){
                        String codice = res.getString("codiceArticolo");
                        String descrip = res.getString("descrizione");
                        Integer howMany = Integer.parseInt(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        String docID = res.getString("id");
                        String docNum = res.getString("numero");
                        String barcode = res.getString("codice");
                        String riga = res.getString("idRiga");
                        Integer of = res.getInt("OrdinatoFornitoreArticoloXMagazzino");
                        Integer oc = res.getInt("OrdinatoClienteArticoloXMagazzino");
                        Integer es = res.getInt("esistenza");
                        Double prz = res.getDouble("PrezzoBase");
                        if(i==0){
                            i++;
                            rigaPrec = res.getString("idRiga");
                            eanPrec = res.getString("codice");
                            firstEan = new ArrayList<>();
                            if(eanPrec != null){
                                firstEan.add(eanPrec);
                            }if(of==null){
                                of = 0;
                            }if(oc==null){
                                oc = 0;
                            }if(es==null){
                                es = 0;
                            }if(prz==null){
                                prz = 0.0;
                            }
                            if(mag == res.getInt("metaMagazzino")){
                                artDoc.add(new Articolo(codice, descrip, of, oc, 0, howMany, es, firstEan, prz, docID, docNum));
                            }else{
                                artDoc.add(new Articolo(codice, descrip, 0, 0, 0, howMany, 0, firstEan, prz, docID, docNum));
                            }
                        }else if(rigaPrec.equals(riga) && !eanPrec.equals(barcode)){
                            eanPrec = res.getString("codice");
                            artDoc.get(count).setEan(barcode);
                        }else if(!rigaPrec.equals(riga)){
                            count++;
                            rigaPrec = res.getString("idRiga");
                            eanPrec = res.getString("codice");
                            firstEan = new ArrayList<>();
                            if(eanPrec != null){
                                firstEan.add(eanPrec);
                            }if(of==null){
                                of = 0;
                            }if(oc==null){
                                oc = 0;
                            }if(es==null){
                                es = 0;
                            }if(prz==null){
                                prz = 0.0;
                            }
                            if(mag == res.getInt("metaMagazzino")){
                                artDoc.add(new Articolo(codice, descrip, of, oc, 0, howMany, es, firstEan, prz, docID, docNum));
                            }else{
                                artDoc.add(new Articolo(codice, descrip, 0, 0, 0, howMany, 0, firstEan, prz, docID, docNum));
                            }
                        }if(mag == res.getInt("metaMagazzino")){
                            if(of==null){
                                of = 0;
                            }if(oc==null){
                                oc = 0;
                            }if(es==null){
                                es = 0;
                            }
                            artDoc.get(count).setEs(es);
                            artDoc.get(count).setOf(of);
                            artDoc.get(count).setOc(oc);
                        }
                    }
                    if ((artDoc.size()>0)) {
                        //z = "Ordine trovato";
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

    public class FindRows extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            listView.setVisibility(View.GONE);
            btnSpunta.setVisibility(View.GONE);
            pbShowDoc.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                pbShowDoc.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                btnSpunta.setVisibility(View.VISIBLE);
                setRows();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(thisContext);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome as codiceArticolo, descrizione, quantita, documento.id, documento.serie, documento.numero \n" +
                            "from RigaDocumento join Documento on (RigaDocumento.idMaster = Documento.id) " +
                            "join articolo on (articolo.id = RigaDocumento.idArticolo) " +
                            "where Documento.id is not null " + whereID + whereNum + whereSel +
                            " and codiceArticolo not like '.%' and codiceArticolo not like ',%'";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        codArt.add(res.getString("codiceArticolo"));
                        desc.add(res.getString("descrizione"));
                        qta.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        idDoc.add(res.getString("id"));
                        serieDoc.add(res.getString("serie"));
                        numDoc.add(res.getString("numero"));
                    }
                    if (!(codArt.isEmpty())) {
                        //z = "Ordine trovato";
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

    public class FindRows4Print extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            listView.setVisibility(View.GONE);
            btnSpunta.setVisibility(View.GONE);
            pbShowDoc.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                pbShowDoc.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                btnSpunta.setVisibility(View.VISIBLE);
                setRows();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(thisContext);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome as codiceArticolo, descrizione, quantita, documento.id, documento.numero, " +
                            "(select fattoreconversione from udmxarticolo where idArticolo = articolo.id) as conv, " +
                            "(select idUDM from udmxarticolo where idArticolo = articolo.id) as UDM, " +
                            "(SELECT max(cast(CategoriaElementoxPromozione.valore as decimal(10,2))) as prezzoPromo " +
                            "FROM Promozione join CategoriaElementoxPromozione on (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                            "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and idCategoria = Articolo.idCategoriaArticolo " +
                            "and Promozione.tipo = '8' " +
                            "or fineValidita is null and inizioValidita is null and idCategoria = Articolo.idCategoriaArticolo " +
                            "and Promozione.tipo = '8' " +
                            "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and idCategoria = Articolo.idCategoriaArticolo " +
                            "and Promozione.tipo = '8' " +
                            "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and idCategoria = Articolo.idCategoriaArticolo " +
                            "and Promozione.tipo = '8' ) as scontoCat, " +
                            "(SELECT min(cast(ElementoxPromozione.valore as decimal(10,2))) as prezzoPromo " +
                            "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                            "WHERE inizioValidita < GETDATE() and fineValidita > GETDATE() and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto > 0 " +
                            "or fineValidita is null and inizioValidita is null and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto > 0 " +
                            "or fineValidita > GETDATE() and inizioValidita is null and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto > 0 " +
                            "or fineValidita is null and inizioValidita < GETDATE() and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto > 0) as valPromo, " +
                            "(SELECT max(cast(ElementoxPromozione.valore as decimal(10,2))) as prezzoPromo " +
                            "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                            "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto = 0 " +
                            "or fineValidita is null and inizioValidita is null and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto = 0 " +
                            "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto = 0 " +
                            "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and idElemento = Articolo.id " +
                            "and Promozione.tipo = '8' and TipoValoreSconto = 0) as scontoPromo, " +
                            "(select max(codice) as ean from alias where idArticolo = articolo.id) as ean, " +
                            "(select cast(prezzo as decimal(10,2)) as prezzo from articoloxlistino where idArticolo = articolo.id and idListino = "+listino+") as przL, " +
                            "(select cast (ProgressivoArticolo.esistenza as int) from ProgressivoArticolo where MetaArticolo = Articolo.id and MetaMagazzino = "+mag+" AND da < GETDATE() AND a > GETDATE() ) as Esistenza " +
                            "from RigaDocumento join Documento on (RigaDocumento.idMaster = Documento.id) " +
                            "join articolo on (articolo.id = RigaDocumento.idArticolo) " +
                            "where Documento.id is not null " + whereID + whereNum + whereSel +
                            " and codiceArticolo not like '.%' and codiceArticolo not like ',%'";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    Double przScCat, przScArt, przValArt;
                    while(res.next()) {
                        codArt.add(res.getString("codiceArticolo"));
                        desc.add(res.getString("descrizione"));
                        qta.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        idDoc.add(res.getString("id"));
                        numDoc.add(res.getString("numero"));
                        esistenza.add(res.getString("Esistenza"));
                        przL.add(res.getString("przL"));
                        alias.add(res.getString("ean"));
                        przP.add("");
                        if(res.getString("scontoCat") != null){
                            przScCat = Double.parseDouble(res.getString("przL")) - (Double.parseDouble(res.getString("przL")) * Double.parseDouble(res.getString("scontoCat")) / 100);
                            przP.set(przP.size()-1, przScCat.toString());
                        }
                        if(res.getString("scontoPromo") != null){
                            przScArt = Double.parseDouble(res.getString("przL")) - (Double.parseDouble(res.getString("przL")) * Double.parseDouble(res.getString("scontoPromo")) / 100);
                            if(!przP.get(przP.size()-1).equals("")){
                                if(przScArt < Double.parseDouble(przP.get(przP.size()-1))){
                                    przP.set(przP.size()-1, przScArt.toString());
                                }
                            }else{
                                przP.set(przP.size()-1, przScArt.toString());
                            }
                        }
                        if(res.getString("valPromo") != null){
                            przValArt = Double.parseDouble(res.getString("valPromo"));
                            if(!przP.get(przP.size()-1).equals("")){
                                if(przValArt < Double.parseDouble(przP.get(przP.size()-1))){
                                    przP.set(przP.size()-1, przValArt.toString());
                                }
                            }else{
                                przP.set(przP.size()-1, przValArt.toString());
                            }
                        }
                        if(res.getString("UDM") != null){
                            UDM.add(res.getString("UDM"));
                            conv.add(res.getDouble("conv"));
                        }else{
                            UDM.add("");
                            conv.add(0.0);
                        }
                    }
                    if (!(codArt.isEmpty())) {
                        //z = "Ordine trovato";
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

    public class FindRowsPresa extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            listView.setVisibility(View.GONE);
            btnSpunta.setVisibility(View.GONE);
            pbShowDoc.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                pbShowDoc.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                btnSpunta.setVisibility(View.VISIBLE);
                setRows();
            }else{
                pbShowDoc.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(thisContext);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "";
                    if(ordinamento==0){
                        query = "SELECT articolo.nome AS codiceArticolo,\n" +
                                "            articolo.descrizione,\n" +
                                "            quantita,\n" +
                                "            Documento.id,\n" +
                                "            Documento.serie,\n" +
                                "            Documento.numero,\n" +
                                "            (SELECT ubicazione FROM articoloxmagazzino WHERE articoloxmagazzino.idArticolo = articolo.id AND idMagazzino = '"+magRif+"') AS ubicazione,\n" +
                                "            (SELECT sottoubicazione FROM articoloxmagazzino WHERE articoloxmagazzino.idArticolo = articolo.id AND idMagazzino = '"+magRif+"') AS sottoubicazione,\n" +
                                "            alias.codice,\n" +
                                "            RigaDocumento.id AS idRiga,\n" +
                                "            (SELECT esistenza FROM progressivoarticolo WHERE metaarticolo=articolo.id AND esistenza > 0 AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE()) AS esistenza,\n" +
                                "            (SELECT OrdinatoClienteArticoloXMagazzino FROM progressivoarticolo WHERE metaarticolo=articolo.id AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE()) AS impegnati\n" +
                                "FROM RigaDocumento\n" +
                                "            JOIN Documento ON (RigaDocumento.idMaster = Documento.id)\n" +
                                "            JOIN Articolo ON (articolo.id = RigaDocumento.idArticolo)\n" +
                                "            LEFT JOIN ArticoloxMagazzino ON (Articolo.id = ArticoloXMagazzino.idArticolo)\n" +
                                "            LEFT JOIN Alias ON (alias.idArticolo = Articolo.id)\n" +
                                "WHERE Documento.id IS NOT NULL\n" + whereID + whereNum + whereSel +
                                "            AND codiceArticolo NOT IN ('.%',',%')\n" +
                                "            and (SELECT esistenza FROM progressivoarticolo WHERE metaarticolo=articolo.id AND esistenza > 0 AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE())>0 " +
                                "order by ubicazione, codiceArticolo";
                    } else {
                        query = "SELECT articolo.nome AS codiceArticolo,\n" +
                                "            articolo.descrizione,\n" +
                                "            quantita,\n" +
                                "            Documento.id,\n" +
                                "            Documento.serie,\n" +
                                "            Documento.numero,\n" +
                                "            (select substring(ubicazione, 4, 1) from articoloxmagazzino where articoloxmagazzino.idArticolo = articolo.id and idMagazzino = '"+magRif+"') as piano, " +
                                "            (select substring(ubicazione, 1, 2) from articoloxmagazzino where articoloxmagazzino.idArticolo = articolo.id and idMagazzino = '"+magRif+"') as corsia, " +
                                "            (select substring(ubicazione, 3, 1) from articoloxmagazzino where articoloxmagazzino.idArticolo = articolo.id and idMagazzino = '"+magRif+"') as colonna, " +
                                "            (SELECT ubicazione FROM articoloxmagazzino WHERE articoloxmagazzino.idArticolo = articolo.id AND idMagazzino = '"+magRif+"') AS ubicazione,\n" +
                                "            (SELECT sottoubicazione FROM articoloxmagazzino WHERE articoloxmagazzino.idArticolo = articolo.id AND idMagazzino = '"+magRif+"') AS sottoubicazione,\n" +
                                "            alias.codice,\n" +
                                "            RigaDocumento.id AS idRiga,\n" +
                                "            (SELECT esistenza FROM progressivoarticolo WHERE metaarticolo=articolo.id AND esistenza > 0 AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE()) AS esistenza,\n" +
                                "            (SELECT OrdinatoClienteArticoloXMagazzino FROM progressivoarticolo WHERE metaarticolo=articolo.id AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE()) AS impegnati\n" +
                                "FROM RigaDocumento\n" +
                                "            JOIN Documento ON (RigaDocumento.idMaster = Documento.id)\n" +
                                "            JOIN Articolo ON (articolo.id = RigaDocumento.idArticolo)\n" +
                                "            LEFT JOIN ArticoloxMagazzino ON (Articolo.id = ArticoloXMagazzino.idArticolo)\n" +
                                "            LEFT JOIN Alias ON (alias.idArticolo = Articolo.id)\n" +
                                "WHERE Documento.id IS NOT NULL\n" + whereID + whereNum + whereSel +
                                "            AND codiceArticolo NOT IN ('.%',',%')\n" +
                                "            and (SELECT esistenza FROM progressivoarticolo WHERE metaarticolo=articolo.id AND esistenza > 0 AND metamagazzino = '"+magRif+"' AND da < GETDATE() AND a > GETDATE())>0 " +
                                "order by corsia, piano, colonna, codiceArticolo";
                    }
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    ArrayList<String> serieApp = new ArrayList<>();
                    ArrayList<String> codArtApp = new ArrayList<>();
                    ArrayList<String> aliasApp = new ArrayList<>();
                    ArrayList<String> descApp = new ArrayList<>();
                    ArrayList<String> qtaApp = new ArrayList<>();
                    ArrayList<String> idDocApp = new ArrayList<>();
                    ArrayList<String> numDocApp = new ArrayList<>();
                    ArrayList<String> ubicazApp = new ArrayList<>();
                    ArrayList<String> subicApp = new ArrayList<>();
                    ArrayList<String> esistenzaApp = new ArrayList<>();
                    ArrayList<String> impegnatiApp = new ArrayList<>();
                    ArrayList<String> idR = new ArrayList<>();
                    ArrayList<String> idRApp = new ArrayList<>();
                    int riga = 0;
                    int i = 0;
                    while(res.next()) {
                        if(res.getString("ubicazione")==null){
                            Boolean presente = false;
                            for(int k=0; k<idR.size(); k++){
                                if(idR.get(k).equals(res.getString("idRiga"))){
                                    presente = true;
                                }
                            }if(!presente){
                                codArt.add(res.getString("codiceArticolo"));
                                if(res.getString("codice")!=null){
                                    alias.add(res.getString("codice"));
                                }else{
                                    alias.add("");
                                }
                                desc.add(res.getString("descrizione"));
                                qta.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                                idDoc.add(res.getString("id"));
                                serieDoc.add(res.getString("serie"));
                                numDoc.add(res.getString("numero"));
                                ubicaz.add("");
                                subic.add("");
                                idR.add(res.getString("idRiga"));
                                if(res.getString("impegnati")!=null){
                                    impegnati.add(res.getString("impegnati").substring(0,res.getString("impegnati").indexOf(".")));
                                }else{
                                    impegnati.add("0");
                                }
                                esistenza.add(res.getString("esistenza").substring(0,res.getString("esistenza").indexOf(".")));
                            }
                        }else if(res.getString("ubicazione").length()>0){
                            Boolean presente = false;
                            for(int k=0; k<idR.size(); k++){
                                if(idR.get(k).equals(res.getString("idRiga"))){
                                    presente = true;
                                }
                            }if(!presente){
                                codArt.add(res.getString("codiceArticolo"));
                                if(res.getString("codice")!=null){
                                    alias.add(res.getString("codice"));
                                }else{
                                    alias.add("");
                                }
                                desc.add(res.getString("descrizione"));
                                qta.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                                idDoc.add(res.getString("id"));
                                serieDoc.add(res.getString("serie"));
                                numDoc.add(res.getString("numero"));
                                ubicaz.add(res.getString("ubicazione"));
                                subic.add(res.getString("sottoubicazione"));
                                idR.add(res.getString("idRiga"));
                                if(res.getString("impegnati")!=null){
                                    impegnati.add(res.getString("impegnati").substring(0,res.getString("impegnati").indexOf(".")));
                                }else{
                                    impegnati.add("0");
                                }
                                esistenza.add(res.getString("esistenza").substring(0,res.getString("esistenza").indexOf(".")));
                            }
                        }else{
                            codArtApp.add(res.getString("codiceArticolo"));
                            if(res.getString("codice")!=null){
                                aliasApp.add(res.getString("codice"));
                            }else{
                                aliasApp.add("");
                            }
                            descApp.add(res.getString("descrizione"));
                            qtaApp.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                            idDocApp.add(res.getString("id"));
                            serieApp.add(res.getString("serie"));
                            numDocApp.add(res.getString("numero"));
                            ubicazApp.add(res.getString("ubicazione"));
                            subicApp.add(res.getString("sottoubicazione"));
                            idRApp.add(res.getString("idRiga"));
                            if(res.getString("impegnati")!=null){
                                impegnatiApp.add(res.getString("impegnati").substring(0,res.getString("impegnati").indexOf(".")));
                            }else{
                                impegnatiApp.add("0");
                            }
                            esistenzaApp.add(res.getString("esistenza").substring(0,res.getString("esistenza").indexOf(".")));
                        }
                        i++;
                        riga = res.getInt("idRiga");
                    }
                    for(int j=0; j<codArtApp.size(); j++){
                        Boolean present = false;
                        for(int p=0; p<codArt.size(); p++){
                            if(codArtApp.get(j).equals(codArt.get(p))){
                                present = true;
                            }
                        }
                        if(!present){
                            codArt.add(codArtApp.get(j));
                            alias.add(aliasApp.get(j));
                            desc.add(descApp.get(j));
                            qta.add(qtaApp.get(j));
                            idDoc.add(idDocApp.get(j));
                            serieDoc.add(serieApp.get(j));
                            numDoc.add(numDocApp.get(j));
                            ubicaz.add(ubicazApp.get(j));
                            subic.add(subicApp.get(j));
                            idR.add(idRApp.get(j));
                            impegnati.add(impegnatiApp.get(j));
                            esistenza.add(esistenzaApp.get(j));
                        }
                    }
                    if (!(codArt.isEmpty())) {
                        //z = "Ordine trovato";
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }
}