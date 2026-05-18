package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReviewSpunta extends AppCompatActivity {

    ArrayList<String> qtaDoc, qtaSpunta, ubic, subic, codArt, desc, alias, nDoc, note, timeSp, qtaColli, idDoc;
    ConnectionClass connectionClass;
    int nDiff = 0;
    String store = "", insert = "", ipNeg, nomeP, utente;
    ListView listView;
    String docsName, tipoDoc, nDocs, utenteSpunta;
    String fileName;
    Button btnFind;
    EditText insFind;
    TextView txtTotColli;
    String magazzino;
    int tipo, totColli;
    Integer totPz, totRighe;
    Double costoDoc = 0.0;
    Context context;
    long idSpuntaDocRoom = -1;
    AppDb appDb;

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_spunta);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        totPz = 0;
        totRighe = 0;
        connectionClass = new ConnectionClass();

        listView = findViewById(R.id.lvRiepSpunta);
        btnFind = findViewById(R.id.btnFindSp);
        insFind = findViewById(R.id.insRicSp);
        txtTotColli = findViewById(R.id.txtTotColli);

        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        context = this;

        appDb = AppDb.getInstance(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docsName = extras.getString("docsName");
            fileName = extras.getString("fileName");
            tipo = extras.getInt("tipo");
            idSpuntaDocRoom = extras.getLong("idSpuntaDocRoom", -1);
        }
        if(tipo != 0){
            magazzino = extras.getString("magazzino");
            nomeP = extras.getString("nomeP");
            ipNeg = extras.getString("ipNeg");
            utente = extras.getString("utente");
        }

        Context context = this;

        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        note = new ArrayList<>();
        timeSp = new ArrayList<>();
        qtaColli = new ArrayList<>();
        idDoc = new ArrayList<>();

        if(idSpuntaDocRoom > 0){
            // Carica da Room (sia per spunta tipo==0 che per presa tipo==1)
            caricaDaRoom();
        } else if(tipo == 0){
            // Fallback: carica da Excel per spunta
            caricaDaExcelSpunta();
        } else {
            // Presa: usa Excel come prima
            caricaDaExcelPresa();
        }

        btnFind.setOnClickListener(v -> {
            if(idSpuntaDocRoom > 0){
                cercaInRoom(insFind.getText().toString().trim());
            } else {
                cercaInExcel(insFind.getText().toString().trim());
            }
        });

        setRows();
    }

    private void caricaDaRoom(){
        try {
            SpuntaDao dao = appDb.spuntaDao();
            SpuntaDocumentoEntity doc = dao.getDocumentoById(idSpuntaDocRoom);
            if(doc != null){
                utenteSpunta = doc.utente;
            }

            List<SpuntaRigaEntity> righe = dao.getRigheByDocumento(idSpuntaDocRoom);
            dao.ricalcolaDiff(idSpuntaDocRoom);
            // Ricarica dopo ricalcolo
            righe = dao.getRigheByDocumento(idSpuntaDocRoom);

            totColli = 0;
            for(int i=0; i<righe.size(); i++){
                SpuntaRigaEntity r = righe.get(i);
                int diff = r.qtaSpunta - r.qtaDoc;
                if(diff != 0){
                    nDiff++;
                    if(nDiff == 1){
                        store = r.store;
                        nDocs = docsName.substring(2).replace("_", " ");
                    }
                }
                tipoDoc = docsName.substring(0,2);

                codArt.add(r.codArt);
                desc.add(r.desc);
                alias.add(r.alias);
                qtaDoc.add(String.valueOf(r.qtaDoc));
                qtaSpunta.add(String.valueOf(r.qtaSpunta));
                timeSp.add(r.timeSp);
                ubic.add(r.ubic);
                subic.add(r.subic);
                note.add(r.note);
                nDoc.add(r.nDoc);
                qtaColli.add(String.valueOf(r.colli));
                idDoc.add(r.idDocRemoto);
                totColli += r.colli;
                totRighe = i + 1;
                totPz += r.qtaSpunta;
                costoDoc += (Double.parseDouble(r.costo) * r.qtaSpunta);

                // Costruisci insert per presa (tabella mcPrese)
                if(tipo == 1){
                    insert = insert + "('"+r.codArt+"'," + "'"+r.desc+"', '"+r.alias+"', '"+r.ubic+"', '"
                            +r.subic+"'," + r.qtaDoc + ", "+r.qtaSpunta+", "+diff+", '"+r.nDoc+"', '"+r.note+"', '"
                            +r.store+"', "+r.esistenza+", "+r.impegnati+", '"+r.timeSp+"', '"+nomeP+"', '"+utente+"' ),";
                }
            }

            if(tipo == 1){
                // Presa: cancella e ricrea nel DB remoto
                if(insert.length() > 0){
                    insert = insert.substring(0, insert.length()-1);
                }
                CancDoc cancDoc = new CancDoc();
                cancDoc.execute();
                CreaDoc creaDoc = new CreaDoc();
                creaDoc.execute();
            } else {
                txtTotColli.setText("N. colli: "+totColli);
                alertDisplayer("Attenzione!", "Il valore della merce spuntata è pari a: € " + costoDoc);
            }

            // Genera Excel finale e salva
            generaExcelDaRoom(righe);

            // Marca documento come completato
            dao.completaDocumento(idSpuntaDocRoom);

            DelDisc delDisc = new DelDisc();
            delDisc.execute();
            if(nDiff > 0){
                Disc disc = new Disc();
                disc.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera il file Excel finale a partire dai dati Room.
     * Chiamato solo in fase di chiusura spunta.
     */
    private void generaExcelDaRoom(List<SpuntaRigaEntity> righe){
        try {
            File path;
            if(tipo == 1){
                // Presa
                path = new File("/storage/emulated/0/NAS/PresaGen");
            } else {
                path = new File("/storage/emulated/0/NAS/SpuntaGen");
            }
            if(!path.exists()) path.mkdirs();

            XSSFWorkbook workbook = new XSSFWorkbook();
            String sheetName = (tipo == 1) ? "Presa" : "Spunta";
            Sheet sheet = workbook.createSheet(sheetName);
            Sheet barcodes = workbook.createSheet("Barcode");
            Sheet segnacolloSheet = workbook.createSheet("Info colli");
            Sheet oxSheet = workbook.createSheet("Info OX");

            // Testata
            Row testata = sheet.createRow(0);
            testata.createCell(0).setCellValue("Codice articolo");
            testata.createCell(1).setCellValue("Descrizione");
            testata.createCell(2).setCellValue("Alias");
            testata.createCell(3).setCellValue("Ubicazione");
            testata.createCell(4).setCellValue("Sottoubicazione");
            testata.createCell(5).setCellValue("Quantita documento");
            testata.createCell(6).setCellValue(tipo == 1 ? "Quantita presa" : "Quantita spunta");
            testata.createCell(7).setCellValue("Differenza");
            testata.createCell(8).setCellValue("N. Doc");
            testata.createCell(9).setCellValue("Note");
            testata.createCell(10).setCellValue("Magazzino");
            testata.createCell(11).setCellValue("Sparata");
            if(tipo == 1){
                testata.createCell(12).setCellValue("Esistenza");
                testata.createCell(13).setCellValue("Impegnati");
                testata.createCell(14).setCellValue("Sparata");
            } else {
                testata.createCell(12).setCellValue("Colli");
                testata.createCell(13).setCellValue("Costo");
                testata.createCell(14).setCellValue("ID Documento");
                testata.createCell(15).setCellValue(utenteSpunta != null ? utenteSpunta : "");
            }

            Row testataB = barcodes.createRow(0);
            testataB.createCell(0).setCellValue("Codice articolo");
            testataB.createCell(1).setCellValue("EAN");

            // Scrivi EAN salvati in Room
            List<SpuntaEanEntity> eanList = appDb.spuntaDao().getEanByDocumento(idSpuntaDocRoom);
            for(int i = 0; i < eanList.size(); i++){
                SpuntaEanEntity e = eanList.get(i);
                Row rowB = barcodes.createRow(i + 1);
                rowB.createCell(0).setCellValue(e.codArt);
                rowB.createCell(1).setCellValue(e.ean);
            }

            Row testataSC = segnacolloSheet.createRow(0);
            testataSC.createCell(0).setCellValue("Codice articolo");
            testataSC.createCell(1).setCellValue("EAN");
            testataSC.createCell(2).setCellValue("Qta");
            testataSC.createCell(3).setCellValue("N. Collo");

            Row testataOX = oxSheet.createRow(0);
            testataOX.createCell(0).setCellValue("Codice articolo");
            testataOX.createCell(1).setCellValue("EAN");
            testataOX.createCell(2).setCellValue("Qta");
            testataOX.createCell(3).setCellValue("Serie");
            testataOX.createCell(4).setCellValue("N. Doc");
            testataOX.createCell(5).setCellValue("Data consegna");
            testataOX.createCell(6).setCellValue("Cliente");

            // Righe dati
            for(int i=0; i<righe.size(); i++){
                SpuntaRigaEntity r = righe.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(r.codArt);
                row.createCell(1).setCellValue(r.desc);
                row.createCell(2).setCellValue(r.alias);
                row.createCell(3).setCellValue(r.ubic);
                row.createCell(4).setCellValue(r.subic);
                row.createCell(5).setCellValue(String.valueOf(r.qtaDoc));
                row.createCell(6).setCellValue(String.valueOf(r.qtaSpunta));
                row.createCell(7).setCellValue(String.valueOf(r.qtaSpunta - r.qtaDoc));
                row.createCell(8).setCellValue(r.nDoc);
                row.createCell(9).setCellValue(r.note);
                row.createCell(10).setCellValue(r.store);
                row.createCell(11).setCellValue(r.timeSp);
                if(tipo == 1){
                    row.createCell(12).setCellValue(String.valueOf(r.esistenza));
                    row.createCell(13).setCellValue(String.valueOf(r.impegnati));
                    row.createCell(14).setCellValue(r.timeSp);
                } else {
                    row.createCell(12).setCellValue(String.valueOf(r.colli));
                    row.createCell(13).setCellValue(r.costo);
                    row.createCell(14).setCellValue(r.idDocRemoto);
                }
            }

            File file = new File(path, fileName);
            FileOutputStream os = new FileOutputStream(file);
            workbook.write(os);
            os.close();
            workbook.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void caricaDaExcelSpunta(){
        try {
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");
            FileInputStream file = new FileInputStream(new File(path, fileName));
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            utenteSpunta = workbook.getSheetAt(0).getRow(0).getCell(15).getStringCellValue();

            totColli = 0;
            int i = 1;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                row.createCell(7).setCellValue(newQta.toString());
                if(newQta!=0){
                    nDiff++;
                    if(nDiff==1){
                        store = row.getCell(10).getStringCellValue();
                        nDocs = docsName.substring(2).replace("_", " ");
                    }
                }
                tipoDoc = docsName.substring(0,2);
                codArt.add(row.getCell(0).getStringCellValue());
                desc.add(row.getCell(1).getStringCellValue());
                alias.add(row.getCell(2).getStringCellValue());
                qtaDoc.add(row.getCell(5).getStringCellValue());
                qtaSpunta.add(row.getCell(6).getStringCellValue());
                timeSp.add(row.getCell(11).getStringCellValue());
                ubic.add(row.getCell(3).getStringCellValue());
                subic.add(row.getCell(4).getStringCellValue());
                note.add(row.getCell(9).getStringCellValue());
                nDoc.add(row.getCell(8).getStringCellValue());
                qtaColli.add(row.getCell(12).getStringCellValue());
                idDoc.add(row.getCell(14).getStringCellValue());
                totColli += Integer.parseInt(row.getCell(12).getStringCellValue());
                totRighe = i;
                totPz += Integer.parseInt(row.getCell(6).getStringCellValue());
                if(row.getCell(12)!=null){
                    costoDoc += (Double.parseDouble(row.getCell(12).getStringCellValue())*Integer.parseInt(row.getCell(6).getStringCellValue()));
                }
                i++;
            }
            txtTotColli.setText("N. colli: "+totColli);
            alertDisplayer("Attenzione!", "Il valore della merce spuntata è pari a: € " + costoDoc);

            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, fileName));
            workbook.write(outFile);
            outFile.close();

            DelDisc delDisc = new DelDisc();
            delDisc.execute();
            if(nDiff>0){
                Disc disc = new Disc();
                disc.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void caricaDaExcelPresa(){
        try {
            CancDoc cancDoc = new CancDoc();
            cancDoc.execute();

            File path = new File("/storage/emulated/0/NAS/PresaGen");
            FileInputStream file = new FileInputStream(new File(path, fileName));
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            int i = 1;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                row.createCell(7).setCellValue(newQta.toString());
                if(newQta!=0){
                    nDiff++;
                    if(nDiff==1){
                        store = row.getCell(10).getStringCellValue();
                        tipoDoc = docsName.substring(0,2);
                        nDocs = docsName.substring(2).replace("_", " ");
                    }
                }
                codArt.add(row.getCell(0).getStringCellValue());
                desc.add(row.getCell(1).getStringCellValue());
                alias.add(row.getCell(2).getStringCellValue());
                qtaDoc.add(row.getCell(5).getStringCellValue());
                qtaSpunta.add(row.getCell(6).getStringCellValue());
                timeSp.add(row.getCell(11).getStringCellValue());
                ubic.add(row.getCell(3).getStringCellValue());
                subic.add(row.getCell(4).getStringCellValue());
                note.add(row.getCell(9).getStringCellValue());
                nDoc.add(row.getCell(8).getStringCellValue());
                totRighe = i;
                totPz += Integer.parseInt(row.getCell(6).getStringCellValue());
                i++;
                insert = insert + "('"+row.getCell(0).getStringCellValue()+"'," + "'"+row.getCell(1).getStringCellValue()+"', '"+row.getCell(2).getStringCellValue()+"', '"+row.getCell(3).getStringCellValue()+"', '"
                        +row.getCell(4).getStringCellValue()+"'," + row.getCell(5).getStringCellValue() + ", "+row.getCell(6).getStringCellValue()+", "+newQta+", '"+row.getCell(8).getStringCellValue()+"', '"+row.getCell(9).getStringCellValue()+"', '"
                        +row.getCell(10).getStringCellValue()+"', "+row.getCell(12).getStringCellValue()+", "+row.getCell(13).getStringCellValue()+", '"+row.getCell(14).getStringCellValue()+"', '"+nomeP+"', '"+utente+"' ),";
            }
            insert = insert.substring(0,insert.length()-1);
            CreaDoc creaDoc = new CreaDoc();
            creaDoc.execute();

            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, fileName));
            workbook.write(outFile);
            outFile.close();

            DelDisc delDisc = new DelDisc();
            delDisc.execute();
            if(nDiff>0){
                Disc disc = new Disc();
                disc.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cercaInRoom(String query){
        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        note = new ArrayList<>();
        timeSp = new ArrayList<>();
        qtaColli = new ArrayList<>();

        try {
            SpuntaDao dao = appDb.spuntaDao();
            // Prima cerca per alias
            List<SpuntaRigaEntity> risultati = dao.cercaPerAlias(idSpuntaDocRoom, query);
            if(risultati.isEmpty()){
                // Fallback su codArt
                risultati = dao.cercaPerCodArt(idSpuntaDocRoom, query);
            }
            for(SpuntaRigaEntity r : risultati){
                codArt.add(r.codArt);
                desc.add(r.desc);
                alias.add(r.alias);
                qtaDoc.add(String.valueOf(r.qtaDoc));
                qtaSpunta.add(String.valueOf(r.qtaSpunta));
                ubic.add(r.ubic);
                subic.add(r.subic);
                note.add(r.note);
                timeSp.add(r.timeSp);
                nDoc.add(r.nDoc);
                qtaColli.add(String.valueOf(r.colli));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        setRows();
    }

    private void cercaInExcel(String query){
        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        note = new ArrayList<>();
        timeSp = new ArrayList<>();
        qtaColli = new ArrayList<>();

        try {
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");
            FileInputStream file = new FileInputStream(new File(path, fileName));
            XSSFWorkbook workbook4Ric = new XSSFWorkbook(file);

            int i = 1;
            boolean find = false;
            while(workbook4Ric.getSheetAt(0).getRow(i) != null){
                if(workbook4Ric.getSheetAt(0).getRow(i).getCell(2).getStringCellValue().contains(query)){
                    Row row = workbook4Ric.getSheetAt(0).getRow(i);
                    codArt.add(row.getCell(0).getStringCellValue());
                    desc.add(row.getCell(1).getStringCellValue());
                    alias.add(row.getCell(2).getStringCellValue());
                    qtaDoc.add(row.getCell(5).getStringCellValue());
                    qtaSpunta.add(row.getCell(6).getStringCellValue());
                    ubic.add(row.getCell(3).getStringCellValue());
                    subic.add(row.getCell(4).getStringCellValue());
                    note.add(row.getCell(9).getStringCellValue());
                    timeSp.add(row.getCell(11).getStringCellValue());
                    nDoc.add(row.getCell(8).getStringCellValue());
                    qtaColli.add(row.getCell(12).getStringCellValue());
                    find = true;
                }
                i++;
            }
            if(!find){
                i = 1;
                while(workbook4Ric.getSheetAt(0).getRow(i) != null){
                    if(workbook4Ric.getSheetAt(0).getRow(i).getCell(0).getStringCellValue().contains(query)){
                        Row row = workbook4Ric.getSheetAt(0).getRow(i);
                        codArt.add(row.getCell(0).getStringCellValue());
                        desc.add(row.getCell(1).getStringCellValue());
                        alias.add(row.getCell(2).getStringCellValue());
                        qtaDoc.add(row.getCell(5).getStringCellValue());
                        qtaSpunta.add(row.getCell(6).getStringCellValue());
                        ubic.add(row.getCell(3).getStringCellValue());
                        subic.add(row.getCell(4).getStringCellValue());
                        note.add(row.getCell(9).getStringCellValue());
                        timeSp.add(row.getCell(11).getStringCellValue());
                        nDoc.add(row.getCell(8).getStringCellValue());
                        qtaColli.add(row.getCell(12).getStringCellValue());
                    }
                    i++;
                }
            }
            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, fileName));
            workbook4Ric.write(outFile);
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setRows();
    }

    public void setRows() {
        if(tipo == 0){
            AdapterReviewSpunta whatever = new AdapterReviewSpunta(this, codArt, desc, qtaDoc, qtaSpunta, ubic, subic, alias, nDoc, tipo, fileName, note, timeSp, qtaColli, idDoc, tipoDoc, utenteSpunta, idSpuntaDocRoom);
            listView.setAdapter(whatever);
        }else{
            AdapterReviewSpunta whatever = new AdapterReviewSpunta(this, codArt, desc, qtaDoc, qtaSpunta, ubic, subic, alias, nDoc, tipo, fileName, magazzino, timeSp, note);
            listView.setAdapter(whatever);
        }
    }

    public class DelDisc extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "delete a" +
                            "from mcDiscordanze a " +
                            "Where store = '"+store+"' and ndoc = '"+nDocs+"' and tipodoc = "+tipoDoc+" ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class Disc extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "INSERT INTO mcDiscordanze (store, ndoc, ndiff, tipodoc, data)" +
                            "VALUES ('"+store+"', '"+nDocs+"', "+nDiff+", "+tipoDoc+", GETDATE()) ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class CreaDoc extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            ResultSet res;
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
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
                } if (con != null) {
                    String query = "INSERT INTO mcPrese (codArt, descrizione, alias, ubic, subic, qtaDoc, qtaS, diff, nDoc, note, magazzino, esistenza, impegnati, sparata, palmare, utente) " +
                            "VALUES "+insert+" ";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class CancDoc extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            ResultSet res;
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
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
                } if (con != null) {
                    String query = "DELETE a " +
                            "FROM mcPrese a " +
                            "WHERE palmare = '"+nomeP+"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
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