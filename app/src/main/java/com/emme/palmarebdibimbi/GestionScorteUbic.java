package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestionScorteUbic extends AppCompatActivity {

    String artOrForn, store, ipNeg, scortaMax, scortaMin;
    int giaPremuto = 0, mag = 0, idL = 1;
    Context context;
    EditText insCodArt, insUbic, insSubic, insScorta;
    TextView txtCodArt, txtUbic, txtSubic, txtDesc, txtPL, txtOC, txtOF, txtEs;
    ConnectionClass connectionClass;
    RadioButton rbScorte, rbUbic, rbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_scorte_ubic);

        context = this;
        connectionClass = new ConnectionClass();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
            verificaVersione(extras.getInt("versione"));
        }
        risolviMagL();

        Button btnFind = findViewById(R.id.btnFindCodArt);
        insCodArt = findViewById(R.id.insCodArt);
        txtCodArt = findViewById(R.id.txtCodSU);
        txtUbic = findViewById(R.id.txtUbicSU);
        txtSubic = findViewById(R.id.txtSubicSU);
        txtDesc = findViewById(R.id.txtDescSU);
        txtOC = findViewById(R.id.txtOCSU);
        txtOF = findViewById(R.id.txtOFSU);
        txtPL = findViewById(R.id.txtPrzLSU);
        txtEs = findViewById(R.id.txtEsSU);
        insScorta = findViewById(R.id.insScorta);
        insUbic = findViewById(R.id.insUbic);
        insSubic = findViewById(R.id.insSubic);
        rbScorte = findViewById(R.id.rbScorte);
        rbUbic = findViewById(R.id.rbUbic);
        rbAll = findViewById(R.id.rbAll);
        Button btnSalva = findViewById(R.id.btnSalva);
        ImageButton clear = findViewById(R.id.btnClear);

        rbScorte.setChecked(true);
        insUbic.setVisibility(View.GONE);
        insSubic.setVisibility(View.GONE);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artOrForn = insCodArt.getText().toString();
                GestionScorteUbic.FindArt findArt = new GestionScorteUbic.FindArt();
                findArt.execute();
            }
        });
        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if (giaPremuto == 0) {
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    artOrForn = insCodArt.getText().toString();
                    GestionScorteUbic.FindArt cercaArt = new GestionScorteUbic.FindArt();
                    cercaArt.execute("");
                }
            }
            return false;
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insCodArt.setText("");
                insUbic.setText("");
                insSubic.setText("");
                insScorta.setText("");
                txtCodArt.setText("");
                txtDesc.setText("");
                txtEs.setText("");
                txtOC.setText("");
                txtOF.setText("");
                txtUbic.setText("");
                txtSubic.setText("");
                txtPL.setText("");
            }
        });
        insScorta.setSelectAllOnFocus(true);
        insSubic.setSelectAllOnFocus(true);
        insUbic.setSelectAllOnFocus(true);
        rbScorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insUbic.setVisibility(View.GONE);
                insSubic.setVisibility(View.GONE);
                insScorta.setVisibility(View.VISIBLE);
            }
        });
        rbUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insScorta.setVisibility(View.GONE);
                insUbic.setVisibility(View.VISIBLE);
                insSubic.setVisibility(View.VISIBLE);
            }
        });
        rbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insScorta.setVisibility(View.VISIBLE);
                insUbic.setVisibility(View.VISIBLE);
                insSubic.setVisibility(View.VISIBLE);
            }
        });
        btnSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbScorte.isChecked()){
                    if(!insScorta.getText().toString().equals("")){
                        UpdateThings updateThings = new UpdateThings();
                        updateThings.execute();
                    }else{
                        articoloNonTrovato("Errore!","Inserisci un valore valido nel campo scorta");
                    }
                }else if(rbUbic.isChecked()){
                    if(!insUbic.getText().toString().equals("")){
                        UpdateThings updateThings = new UpdateThings();
                        updateThings.execute();
                    }else{
                        articoloNonTrovato("Errore!","Inserisci un valore valido nel campo ubicazione");
                    }
                }else{
                    if(insUbic.getText().toString().equals("")){
                        articoloNonTrovato("Errore!","Inserisci un valore valido nel campo ubicazione");
                    }else if(insScorta.getText().toString().equals("")){
                        articoloNonTrovato("Errore!","Inserisci un valore valido nel campo scorta");
                    }else{
                        UpdateThings updateThings = new UpdateThings();
                        updateThings.execute();
                    }
                }
            }
        });
    }
    public void verificaVersione(int versione) {
        Connection con = null;
        try {
            con = connectionClass.CONN(context);
            String query = "SELECT versioneApp, linkApp " +
                    "FROM mcInfoBdiBimbi " ;
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            if (res.next()) {
                if(res.getInt("versioneApp")!=versione){
                    aggiornaPalmare("Attenzione!", "Stai utilizando una versione non aggiornata dell'app, scarica e installa l'aggiornamento per utilizzare tutte le ultime funzionalità", res.getString("linkApp"));
                }
            }

        } catch (Exception ex) {

        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }
    }
    private void aggiornaPalmare(String title,String message, String link){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(GestionScorteUbic.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("ANNULLA", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("SCARICA E INSTALLA", (dialog, which) -> {
                    dialog.cancel();
                    Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void risolviMagL(){
        switch (store) {
            case "MASTER":
                ipNeg = "192.168.2.41";
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                mag = 77;
                idL = 1;
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                mag = 35;
                idL = 1;
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                mag = 72;
                idL = 1;
                break;
            case "OLBIA":
                ipNeg = "192.168.1.10";
                mag = 76;
                idL = 1;
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                mag = 74;
                idL = 1;
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                mag = 32;
                idL = 1;
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                mag = 78;
                idL = 1;
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                mag = 75;
                idL = 1;
                break;
            case "ORISTANO":
                ipNeg = "85.47.29.51";
                mag = 71;
                idL = 1;
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                mag = 85;
                idL = 1;
                break;
            case "MasterMagRoma":
                ipNeg = "195.100.100.202";
                mag = 91;
                idL = 1;
                break;
            case "CEDIROMAINLAV":
                ipNeg = "192.168.1.20";
                mag = 93;
                idL = 1;
                break;
            case "CAPENA":
                ipNeg = "192.168.188.20";
                mag = 87;
                idL = 1;
                break;
            case "OSTIENSE":
                ipNeg = "95.255.234.195";
                mag = 86;
                idL = 1;
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.2.41";
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                mag = 90;
                idL = 1;
                break;
            case "ARDEATINA":
                ipNeg = "192.168.1.20";
                mag = 112;
                idL = 1;
                break;
            case "VERONA":
                ipNeg = "192.168.16.20";
                mag = 114;
                idL = 1;
                break;
            case "POMEZIA":
                ipNeg = "192.168.1.20";
                mag = 94;
                idL = 1;
                break;
            case "ROMACEDI":
                ipNeg = "192.168.1.20";
                mag = 111;
                idL = 1;
                break;
            case "INTRANSITO":
                ipNeg = "192.168.2.41";
                mag = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.2.41";
                mag = 89;
                idL = 1;
                break;
            default:
                ipNeg = "192.168.1.20";
                mag = 0;
                idL = 1;
                break;
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(GestionScorteUbic.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
    public class UpdateThings extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r) {

            if(isSuccess){
                insSubic.setText("");
                insUbic.setText("");
                insScorta.setText("");
                insCodArt.setText("");
                txtCodArt.setText("");
                txtDesc.setText("");
                txtUbic.setText("");
                txtSubic.setText("");
                txtEs.setText("");
                txtOF.setText("");
                txtOC.setText("");
                txtPL.setText("");
                insCodArt.setFocusable(true);
                showSoftKeyboard(insCodArt);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query;
                    if(rbScorte.isChecked()){
                        query = "UPDATE articoloxmagazzino " +
                                "SET scortaMassima = "+insScorta.getText().toString()+", scortaMinima = "+scortaMin+" " +
                                "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                    }else if(rbUbic.isChecked()){
                        if(!insSubic.getText().toString().equals("")){
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbic.getText().toString()+"', sottoUbicazione = '"+insSubic.getText().toString()+"' " +
                                    "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                    "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                        }else{
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbic.getText().toString()+"' " +
                                    "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                    "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                        }
                    }else{
                        if(!insSubic.getText().toString().equals("")){
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbic.getText().toString()+"', sottoUbicazione ='"+insSubic.getText().toString()+"', scortaMassima = "+insScorta.getText().toString()+", scortaMinima = "+scortaMin+" " +
                                    "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                    "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                        }else{
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbic.getText().toString()+"', scortaMassima = "+insScorta.getText().toString()+", scortaMinima = "+scortaMin+" " +
                                    "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                    "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                        }
                    }

                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                    isSuccess = true;
                }
            }catch (Exception ex) {
                ex.getMessage();
                isSuccess = false;
                z = "Errore";
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
            return z;
        }

    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;
        String ubiArt, subiArt, esistenza, OF, OC, przL;

        @Override
        protected void onPreExecute() {

            desc = "";
        }

        @Override
        protected void onPostExecute(String r) {

            if(isSuccess) {
                txtCodArt.setText(artOrForn);
                txtDesc.setText(desc);
                txtUbic.setText(ubiArt);
                txtSubic.setText(subiArt);
                txtEs.setText(esistenza);
                txtOF.setText(OF);
                txtOC.setText(OC);
                txtPL.setText(przL);
                if(!ubiArt.equals("N/A")){
                    insUbic.setText(ubiArt);
                }if(!subiArt.equals("N/A")){
                    insSubic.setText(subiArt);
                }if(scortaMax!=null){
                    insScorta.setText(scortaMax);
                }if(rbUbic.isChecked()){
                    insUbic.selectAll();
                    showSoftKeyboard(insUbic);
                }else{
                    insScorta.selectAll();
                    showSoftKeyboard(insScorta);
                }

            }else{
                articoloNonTrovato("Errore!", "Riga non presente nella tabella");
                insCodArt.setText("");
            }if(giaPremuto == 1){
                giaPremuto = 0;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice, " +
                            "articoloxmagazzino.ubicazione as ubi, cast(articoloxmagazzino.scortaMassima as int) as scortaMax, " +
                            "cast(articoloxmagazzino.scortaMinima as int) as scortaMin, articoloxmagazzino.sottoubicazione as subi, " +
                            "(select fattoreconversione from udmxarticolo where idArticolo = articolo.id) as conv, " +
                            "(select idUDM from udmxarticolo where idArticolo = articolo.id) as UDM, " +
                            "cast((select prezzo from articoloxlistino where idArticolo = articolo.id and idListino = "+idL+" ) as decimal (18,2)) as przL, " +
                            "cast((select esistenza from progressivoarticolo where metaarticolo = articolo.id and metamagazzino = "+mag+" and da < GETDATE() and a > GETDATE() ) as int) as esistenza, " +
                            "cast((select OrdinatoFornitoreArticoloXMagazzino from progressivoarticolo where metaarticolo = articolo.id and metamagazzino = "+mag+" and da < GETDATE() and a > GETDATE() ) as int) as ordF, " +
                            "cast((select OrdinatoClienteArticoloXMagazzino from progressivoarticolo where metaarticolo = articolo.id and metamagazzino = "+mag+" and da < GETDATE() and a > GETDATE() ) as int) as ordC " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo) " +
                            "JOIN articoloxmagazzino on (articolo.id = articoloxmagazzino.idArticolo) " +
                            "WHERE articolo.nome = '"+artOrForn+"' and articoloxmagazzino.idMagazzino = "+mag+" " +
                            "OR alias.codice = '"+artOrForn+"' and articoloxmagazzino.idMagazzino = "+mag+" " +
                            "OR articolo.nome = '"+artOrForn.trim()+"' and articoloxmagazzino.idMagazzino = "+mag+" " +
                            "OR alias.codice = '"+artOrForn.trim()+"' and articoloxmagazzino.idMagazzino = "+mag+"  ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        isSuccess = true;
                        artOrForn = res.getString("nome");
                        desc = res.getString("descrizione");
                        esistenza = res.getString("esistenza");
                        OF = res.getString("ordF");
                        OC = res.getString("ordC");
                        przL = res.getString("przL");
                        if(res.getString("scortaMax")!=null){
                            scortaMax = res.getString("scortaMax");
                        }else{
                            scortaMax = null;
                        }if(res.getString("scortaMin")!=null){
                            scortaMin = res.getString("scortaMin");
                        }else{
                            scortaMin = "0";
                        }if(res.getString("ubi")!=null){
                            ubiArt = res.getString("ubi");
                        }else{
                            ubiArt = "N/A";
                        }if(res.getString("subi")!=null){
                            subiArt = res.getString("subi");
                        }else{
                            subiArt = "N/A";
                        }
                    }
                }
            }catch (Exception ex) {
                ex.getMessage();
                isSuccess = false;
                z = "Errore";
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
            return z;
        }

    }
}