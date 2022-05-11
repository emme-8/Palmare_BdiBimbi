package com.emme.palmarebdibimbi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Articolo> artDoc = new ArrayList<>();
    ConnectionClass connectionClass;
    Context context;
    ProgressBar pbDownload;
    int mag4Ric = 0;
    String ipNeg = "";
    Switch smartMode;
    int idL = 0;
    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        smartMode = findViewById(R.id.switch1);

        pbDownload = findViewById(R.id.pbDownload);
        pbDownload.setVisibility(View.GONE);

        context = this;
        connectionClass = new ConnectionClass();

        Button stp2 = findViewById(R.id.button2);
        stp2.setOnClickListener(v -> {
            FindRowsOffline find = new FindRowsOffline();
            find.execute();
        });
        Button btnCheckIntegrity = findViewById(R.id.btnCheckPromo);
        btnCheckIntegrity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIntegrity check = new CheckIntegrity();
                check.execute();
            }
        });

        MyApplication app = (MyApplication)getApplication();

        Bundle extras = getIntent().getExtras();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        RadioButton rbOn = findViewById(R.id.rbOn);
        RadioButton rbOff = findViewById(R.id.rbOff);
        Button btnImp = findViewById(R.id.btnImp);
        Button btnInv = findViewById(R.id.btnInv);
        Button btnPresa = findViewById(R.id.btnPresa);
        Button btnSpunto = findViewById(R.id.btnSpunta);
        Button btnStampa = findViewById(R.id.btnStampaEtichetta);
        Button btnCreaDoc = findViewById(R.id.btnCreaDoc);
        Button btnPrintFromDoc = findViewById(R.id.btnPDD);

        if(sharedPref.getBoolean("smartMode", false)){
            smartMode.setChecked(true);
        }else{
            smartMode.setChecked(false);
        }

        if(sharedPref.getBoolean("isOnline", true)){
            rbOn.setChecked(true);
        }else{
            rbOff.setChecked(true);
        }
        if(extras != null) {
            editor.putString("ExternalIp", extras.getString("externalIp"));
            editor.putString("LocalIp", extras.getString("localIp"));
            editor.putString("PrinterIp", extras.getString("printerIp"));
            editor.putString("NomePalm", extras.getString("nomePalm"));
            editor.putString("Email", extras.getString("email"));
            editor.putString("EmailPass", extras.getString("emailPass"));
            editor.putString("storeName", extras.getString("storeName"));
            editor.putString("printer", extras.getString("printer"));
            editor.putBoolean("Ubicazione", extras.getBoolean("ubicazione"));
            editor.putBoolean("Connessione", extras.getBoolean("connessione"));
            editor.putInt("Store", extras.getInt("store"));
            editor.apply();
        }

        switch (sharedPref.getString("storeName", "")) {
            case "MASTER":
                ipNeg = "192.168.1.41";
                mag4Ric = 1;
                idL = 1;
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                mag4Ric = 77;
                idL = 6;
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                mag4Ric = 35;
                idL = 6;
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                mag4Ric = 72;
                idL = 6;
                break;
            case "OLBIA":
                ipNeg = "192.168.1.20";
                mag4Ric = 76;
                idL = 5;
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                mag4Ric = 74;
                idL = 9;
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                mag4Ric = 32;
                idL = 4;
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                mag4Ric = 78;
                idL = 7;
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                mag4Ric = 75;
                idL = 3;
                break;
            case "ORISTANO":
                ipNeg = "192.168.1.20";
                mag4Ric = 71;
                idL = 8;
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                mag4Ric = 85;
                idL = 3049;
                break;
            case "CAPENA":
                ipNeg = "198.100.100.204";
                mag4Ric = 87;
                idL = 3050;
                break;
            case "OSTIENSE":
                ipNeg = "196.100.100.203";
                mag4Ric = 86;
                idL = 3048;
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.1.41";
                mag4Ric = 59;
                idL = 1;
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                mag4Ric = 90;
                idL = 3052;
                break;
            case "INTRANSITO":
                ipNeg = "192.168.1.41";
                mag4Ric = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.1.41";
                mag4Ric = 89;
                idL = 1;
                break;
            default:
                mag4Ric = 1;
                break;
        }

        smartMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("smartMode", true);
                }else{
                    editor.putBoolean("smartMode", false);
                }
                editor.apply();
            }
        });
        rbOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editor.putBoolean("isOnline", true);
                editor.apply();
            }
        });
        rbOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editor.putBoolean("isOnline", false);
                editor.apply();
            }
        });
        btnCreaDoc.setOnClickListener(v -> {
            Intent creaDoc;
            if(sharedPref.getBoolean("smartMode", false)){
                creaDoc = new Intent(MainActivity.this, CreaDocumentiSmart.class);
            }else{
                creaDoc = new Intent(MainActivity.this, CreaDocumenti.class);
            }
            creaDoc.putExtra("storeName", sharedPref.getString("storeName", ""));
            startActivity(creaDoc);
        });
        btnStampa.setOnClickListener(v -> {
            Intent vGiac;
            if(sharedPref.getBoolean("smartMode", false)){
                vGiac = new Intent(MainActivity.this, VerificaGiacenzeSmart.class);
            }else{
                vGiac = new Intent(MainActivity.this, VerificaGiacenze.class);
            }
            vGiac.putExtra("storeName", sharedPref.getString("storeName", ""));
            startActivity(vGiac);
        });
        btnPresa.setOnClickListener(v -> {
            Intent presa = new Intent(MainActivity.this,MainSpuntoMerce.class);
            presa.putExtra("store", sharedPref.getInt("Store", 0));
            presa.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            presa.putExtra("tipo", 1);
            startActivity(presa);
        });
        btnInv.setOnClickListener(v -> {
            Intent inv = new Intent(MainActivity.this,HomeInventario.class);
            inv.putExtra("storeName", sharedPref.getString("storeName", ""));
            inv.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            inv.putExtra("tipo", 2);
            startActivity(inv);
        });
        btnPrintFromDoc.setOnClickListener(v -> {
            Intent pdd = new Intent(MainActivity.this,MainSpuntoMerce.class);
            pdd.putExtra("store", sharedPref.getInt("Store", 0));
            pdd.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            pdd.putExtra("tipo", 3);
            startActivity(pdd);
        });
        btnSpunto.setOnClickListener(v -> {
            Intent spunto = new Intent(MainActivity.this,MainSpuntoMerce.class);
            spunto.putExtra("store", sharedPref.getInt("Store", 0));
            spunto.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            spunto.putExtra("tipo", 0);
            startActivity(spunto);
        });
        btnImp.setOnClickListener(v -> {
            Intent imp = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(imp);
        });
    }

    public class CheckIntegrity extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ArrayList<String> codArtSede = new ArrayList<>();
        ArrayList<String> codArtNeg = new ArrayList<>();
        ArrayList<String> przSede = new ArrayList<>();
        ArrayList<String> przNeg = new ArrayList<>();
        int promoSede = 0;
        int promoNeg = 0;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            pbDownload.setVisibility(View.VISIBLE);
        }

        private void fine(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                keepFromNeg();
                boolean different = false;
                for(int i=0; i<codArtSede.size(); i++){
                    int j=0;
                    do{
                        if(!przSede.get(i).equals(przNeg.get(j))){
                            different = true;
                        }
                        if(j<codArtNeg.size()-1){
                            j++;
                        }
                    }while(!codArtSede.get(i).equals(codArtNeg.get(j)) || j==codArtNeg.size()-1);
                }
                promoFromNeg();
                promoFromSede();
                pbDownload.setVisibility(View.GONE);
                if(different && promoNeg!=promoSede){
                    fine("Attenzione!", "\nListini: X \n\nPromozioni: X");
                }else if(different && promoNeg==promoSede){
                    fine("Attenzione!", "\nListini: X \n\nPromozioni: OK");
                }else if(!different && promoNeg!=promoSede){
                    fine("Attenzione!", "\nListini: OK \n\nPromozioni: X");
                }else{
                    fine("Attenzione!", "\nListini: OK \n\nPromozioni: OK");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome, articoloxlistino.prezzo " +
                            "from articolo join articoloxlistino on (articoloxlistino.idArticolo = articolo.id) " +
                            "where articoloxlistino.idlistino = "+idL+" and cast(articoloxlistino.dataCreazione as date) > DATEADD(day, -2, CAST(GETDATE() AS date)) " +
                            "order by articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        codArtSede.add(res.getString("nome"));
                        przSede.add(res.getString("prezzo"));
                    }
                    isSuccess = true;
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

        public void promoFromSede(){
                    try {
            Connection con = connectionClass.CONN(context);
            if (con == null) {
                z = "Errore di connessione con il server";
            } else {
                String query = "select count(id) as totPromo from promozione " +
                        "where cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE()";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()){
                    promoSede = res.getInt("totPromo");
                }
            }
        }
            catch (Exception ex) {
            isSuccess = false;
            z = "Errore";
        }
    }

        public void promoFromNeg(){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connection con = null;
                String ConnURL;

                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    ConnURL = "jdbc:jtds:sqlserver://"+ipNeg+"/PassepartoutRetail;user=sa;password=SaSqlPass*01;";
                    con = DriverManager.getConnection(ConnURL);

                }catch (SQLException se){
                    Log.e("error here 1 : ", se.getMessage());
                }
                catch (ClassNotFoundException e){
                    Log.e("error here 2 : ", e.getMessage());
                }
                catch (Exception e){
                    Log.e("error here 3 : ", e.getMessage());
                }
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select count(id) as totPromo from promozione " +
                            "where cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE()";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        promoNeg = res.getInt("totPromo");
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
        }

        public void keepFromNeg(){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connection con = null;
                String ConnURL;

                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    ConnURL = "jdbc:jtds:sqlserver://"+ipNeg+"/PassepartoutRetail;user=sa;password=SaSqlPass*01;";
                    con = DriverManager.getConnection(ConnURL);

                }catch (SQLException se)
                {
                    Log.e("error here 1 : ", se.getMessage());
                }
                catch (ClassNotFoundException e)
                {
                    Log.e("error here 2 : ", e.getMessage());
                }
                catch (Exception e)
                {
                    Log.e("error here 3 : ", e.getMessage());
                }
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome, articoloxlistino.prezzo " +
                            "from articolo join articoloxlistino on (articoloxlistino.idArticolo = articolo.id) " +
                            "where articoloxlistino.idlistino = "+idL+" and cast(articoloxlistino.dataCreazione as date) > DATEADD(day, -2, CAST(GETDATE() AS date)) " +
                            "order by articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        codArtNeg.add(res.getString("nome"));
                        przNeg.add(res.getString("prezzo"));
                    }
                }
            }
            catch (Exception ex) {
                z = "Errore";
            }
        }
    }

    public class FindRowsOffline extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            pbDownload.setVisibility(View.VISIBLE);
        }

        private void fine(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(artDoc);
                editor.putString("ListaArticoli", json);
                editor.apply();
                pbDownload.setVisibility(View.GONE);
                fine("Attenzione!", "Importazione articoli completata!");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome as codiceArticolo, articolo.descrizione, alias.codice, esistenza \n" +
                            "from (articolo left join alias on (alias.idArticolo = articolo.id)) left join progressivoarticolo on (articolo.id= ProgressivoArticolo.MetaArticolo and metamagazzino = '"+mag4Ric+"' and (da < GETDATE()) and (a > GETDATE()))   \n" +
                            "where articolo.isAttivo = 1 " +
                            "order by articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int count = 0;
                    int i = 0;
                    ArrayList<String> firstEan;
                    String eanPrec = "";
                    String codPrec = "";
                    while(res.next()){
                        String codice = res.getString("codiceArticolo");
                        String descrip = res.getString("descrizione");
                        String barcode = res.getString("codice");
                        Integer es = 0;
                        if(res.getString("esistenza") != null){
                            es = (int)Double.parseDouble(res.getString("esistenza"));
                        }
                        if(i==0){
                            i++;
                            codPrec = res.getString("codiceArticolo");
                            eanPrec = res.getString("codice");
                            firstEan = new ArrayList<>();
                            if(eanPrec != null){
                                firstEan.add(eanPrec);
                            }
                            artDoc.add(new Articolo(codice, descrip, firstEan, es));
                        }else if(codPrec.equals(codice) && !eanPrec.equals(barcode)){
                            eanPrec = res.getString("codice");
                            artDoc.get(count).setEan(barcode);
                        }else if(!codPrec.equals(codice)){
                            count++;
                            codPrec = res.getString("codiceArticolo");
                            eanPrec = res.getString("codice");
                            firstEan = new ArrayList<>();
                            if(eanPrec != null){
                                firstEan.add(eanPrec);
                            }
                            artDoc.add(new Articolo(codice, descrip, firstEan, es));
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
}