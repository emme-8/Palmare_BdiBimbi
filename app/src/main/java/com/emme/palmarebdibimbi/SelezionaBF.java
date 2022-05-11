package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class SelezionaBF extends AppCompatActivity {

    ConnectionClass connectionClass;
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> ndoc = new ArrayList<>();
    ArrayList<String> forn = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> serie = new ArrayList<>();
    String tipoRiga;
    String magazzino;
    ListView listView;
    String whereId, whereAnno, whereMag, whereTipo, whereSel, tipo, whereForn, whereNDoc;
    int listino, mag, spuntaOrPresa, magRif, listinoRif;
    Context thisContext;
    Boolean ubic;
    ProgressBar pbSelBF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleziona_b_f);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        connectionClass = new ConnectionClass();
        thisContext = this;
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(thisContext);
        Boolean isLocal=p.getBoolean("Connessione",false);

        pbSelBF = findViewById(R.id.pbSelBF);
        listView = findViewById(R.id.bfrow);
        pbSelBF.setVisibility(View.GONE);

        if(!isInternetAvailable()){
            noConnection("Attenzione!","Verifica la tua connessione internet e riprova", true);
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            spuntaOrPresa = extras.getInt("tipo");
            tipo = extras.getString("tipoDoc");
            magazzino = extras.getString("magazzino");
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            listinoRif = extras.getInt("listinoRif");
            magRif = extras.getInt("magRif");
            ubic = extras.getBoolean("ubicazione");
            if(!extras.getString("numDoc").equals("")){
                whereNDoc = " and Documento.numero in (" + extras.getString("numDoc") + ") ";
            }else{
                whereNDoc = " ";
            }
            if(!extras.getString("fornitore").equals("")){
                whereForn = " and Anagrafica.ragioneSociale like '" + extras.getString("fornitore") + "%' ";
            }else{
                whereForn = " ";
            }
            if(!extras.getString("idDoc").equals("")){
                whereId = " and Documento.id = " + extras.getString("idDoc") + " ";
            }else{
                whereId = " ";
            }
            if(!extras.getString("anno").equals("")){
                Integer annoSucc = Integer.parseInt(extras.getString("anno")) + 1;
                whereAnno = " and Documento.data > " + "'" + extras.getString("anno") + "-01-01 00:00:00.000'" + "and Documento.data < " + "'" + annoSucc + "-01-01 00:00:00.000' ";
            }else{
                whereAnno = " ";
            }
            whereMag = " and RigaDocumento.idMagazzinoDestinazione = " + extras.getInt("mag") + " ";
            whereSel = " and RigaDocumento.selettore like '" + extras.getString("selettore") + "' ";
            whereTipo = " and Documento.identificatore like '" + extras.getString("tipoDoc") + "%' ";
        }
        tipoRiga = extras.getString("selettore");

        SelezionaBF.FindBF cerca = new SelezionaBF.FindBF();
        cerca.execute("");
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void noConnection(String title,String message, final boolean error){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelezionaBF.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(error) {
                            Intent intent = new Intent(SelezionaBF.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void noMatch(String title,String message, final boolean error){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SelezionaBF.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(error) {
                            Intent intent = new Intent(SelezionaBF.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void setRows(){
        AdapterSelBF whatever = new AdapterSelBF(this, id, ndoc, forn, data, tipoRiga, ubic, listino, mag, tipo, spuntaOrPresa, magazzino, listinoRif, magRif, serie);
        listView.setAdapter(whatever);
    }

    public class FindBF extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPreExecute() {
            pbSelBF.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                pbSelBF.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                setRows();
            }else{
                noMatch("Errore!","Nessun documento trovato con i filtri impostati", true);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(thisContext);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select distinct Documento.id, numero, ragioneSociale, Documento.data, Documento.dataCreazione, Documento.serie\n" +
                            "from Documento left join Fornitore on (Documento.idFornitore = fornitore.id)\n" +
                            "left join Anagrafica on (Anagrafica.id = Fornitore.idAnagrafica)\n" +
                            "join RigaDocumento on (RigaDocumento.idMaster = Documento.id) " +
                            "where Documento.id is not null " +
                            whereTipo + whereId + whereAnno + whereMag +
                            whereSel + whereNDoc + whereForn +
                            "and idMagazzinoDestinazione is not null " +
                            "order by ragioneSociale, Documento.data desc";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String currDt = dtf.format(now);
                        if(res.getString("dataCreazione").replace("-","/").substring(0,10).equals(currDt.substring(0,10))){
                            Integer minutes = Integer.parseInt(res.getString("dataCreazione").substring(14,16));
                            Integer currMinutes = Integer.parseInt(currDt.substring(14,16));
                            Integer hour = Integer.parseInt(res.getString("dataCreazione").substring(11,13));
                            Integer currHour = Integer.parseInt(currDt.substring(11,13));
                            int tCase = 0;
                            if(minutes+5 > 60){
                                tCase = 1;
                                minutes = minutes + 5 - 60;
                                hour++;
                            }
                            if(hour.equals(currHour)){
                                if(tCase == 0) {
                                    if(minutes+5<=currMinutes){
                                        id.add(res.getString("id"));
                                        ndoc.add(res.getString("numero"));
                                        serie.add(res.getString("serie"));
                                        if(res.getString("ragioneSociale")!=null){
                                            forn.add(res.getString("ragioneSociale"));
                                        }else{
                                            forn.add("");
                                        }
                                        String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                                        data.add(dt);
                                    }
                                }else if(minutes <= currMinutes){
                                    id.add(res.getString("id"));
                                    ndoc.add(res.getString("numero"));
                                    serie.add(res.getString("serie"));
                                    if(res.getString("ragioneSociale")!=null){
                                        forn.add(res.getString("ragioneSociale"));
                                    }else{
                                        forn.add("");
                                    }
                                    String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                                    data.add(dt);
                                }
                            }else{
                                id.add(res.getString("id"));
                                ndoc.add(res.getString("numero"));
                                serie.add(res.getString("serie"));
                                if(res.getString("ragioneSociale")!=null){
                                    forn.add(res.getString("ragioneSociale"));
                                }else{
                                    forn.add("");
                                }
                                String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                                data.add(dt);
                            }
                        }else{
                            id.add(res.getString("id"));
                            ndoc.add(res.getString("numero"));
                            serie.add(res.getString("serie"));
                            if(res.getString("ragioneSociale")!=null){
                                forn.add(res.getString("ragioneSociale"));
                            }else{
                                forn.add("");
                            }
                            String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                            data.add(dt);
                        }

/*
                        id.add(res.getString("id"));
                        ndoc.add(res.getString("numero"));
                        serie.add(res.getString("serie"));
                        if(res.getString("ragioneSociale")!=null){
                            forn.add(res.getString("ragioneSociale"));
                        }else{
                            forn.add("");
                        }
                        String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                        data.add(dt);


 */
                    }
                    if (!(id.isEmpty())) {
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