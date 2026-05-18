package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

public class ReviewSpuntaNeg extends AppCompatActivity {

    ArrayList<String> qtaDoc, qtaSpunta, codArt, desc, alias, nDoc, timeSp, idDoc;
    ListView listView;
    ConnectionClass connectionClass;
    Context context;
    int nDiff = 0;
    String docsName, tipoDoc, nDocs, store;
    Button btnFind;
    EditText insFind;
    String fileName, utenteSpunta;
    Integer tipo;
    Double costoDoc = 0.0;
    int nColli = 0;
    long idSpuntaDocRoom = -1;
    AppDb appDb;

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReviewSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_spunta_neg);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        connectionClass = new ConnectionClass();

        listView = findViewById(R.id.lvRiepSpuntaNeg);
        btnFind = findViewById(R.id.btnFindSpNeg);
        insFind = findViewById(R.id.insRicSpNeg);
        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        timeSp = new ArrayList<>();
        idDoc = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            fileName = extras.getString("fileName");
            docsName = extras.getString("docsName");
            store = extras.getString("store");
            tipo = extras.getInt("tipo");
            idSpuntaDocRoom = extras.getLong("idSpuntaDocRoom", -1);
        }

        appDb = AppDb.getInstance(this);
        context = this;

        if(idSpuntaDocRoom > 0){
            caricaDaRoom();
        } else {
            caricaDaExcel();
        }

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idSpuntaDocRoom > 0){
                    cercaInRoom(insFind.getText().toString().trim());
                } else {
                    cercaInExcel(insFind.getText().toString().trim());
                }
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
            righe = dao.getRigheByDocumento(idSpuntaDocRoom);

            for(int i=0; i<righe.size(); i++){
                SpuntaRigaEntity r = righe.get(i);
                int diff = r.qtaSpunta - r.qtaDoc;
                if(diff != 0){
                    nDiff++;
                    if(nDiff == 1){
                        nDocs = docsName.substring(2).replace("_", " ");
                    }
                }
                tipoDoc = docsName.substring(0,2);

                codArt.add(r.codArt);
                desc.add(r.desc);
                alias.add(r.alias);
                qtaDoc.add(String.valueOf(r.qtaDoc));
                qtaSpunta.add(String.valueOf(r.qtaSpunta));
                idDoc.add(r.idDocRemoto);
                if(!r.costo.isEmpty()){
                    try {
                        costoDoc += (Double.parseDouble(r.costo) * r.qtaSpunta);
                    } catch(NumberFormatException ignored){}
                }
                timeSp.add(r.timeSp);
                nDoc.add(r.nDoc);
            }

            alertDisplayer("Attenzione!", "Il valore della merce spuntata è pari a: € " + costoDoc);

            generaExcelDaRoom(righe);

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

    private void generaExcelDaRoom(List<SpuntaRigaEntity> righe){
        try {
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");
            if(!path.exists()) path.mkdirs();

            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Spunta");
            Sheet barcodes = workbook.createSheet("Barcode");
            Sheet segnacolloSheet = workbook.createSheet("Info colli");

            Row testata = sheet.createRow(0);
            testata.createCell(0).setCellValue("Codice articolo");
            testata.createCell(1).setCellValue("Descrizione");
            testata.createCell(2).setCellValue("Alias");
            testata.createCell(3).setCellValue("Qta");
            testata.createCell(4).setCellValue("Sparata");
            testata.createCell(5).setCellValue("Quantita documento");
            testata.createCell(6).setCellValue("Quantita spunta");
            testata.createCell(7).setCellValue("Differenza");
            testata.createCell(8).setCellValue("N. Doc");
            testata.createCell(9).setCellValue("Sparata");
            testata.createCell(10).setCellValue(utenteSpunta != null ? utenteSpunta : "");
            testata.createCell(11).setCellValue("Magazzino");
            testata.createCell(12).setCellValue("Costo");
            testata.createCell(13).setCellValue("Rifornimento");
            testata.createCell(14).setCellValue("ID Documento");

            Row testataB = barcodes.createRow(0);
            testataB.createCell(0).setCellValue("Codice articolo");
            testataB.createCell(1).setCellValue("EAN");

            Row testataSC = segnacolloSheet.createRow(0);
            testataSC.createCell(0).setCellValue("Codice articolo");
            testataSC.createCell(1).setCellValue("EAN");
            testataSC.createCell(2).setCellValue("Qta");
            testataSC.createCell(3).setCellValue("N. Collo");

            for(int i=0; i<righe.size(); i++){
                SpuntaRigaEntity r = righe.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(r.codArt);
                row.createCell(1).setCellValue(r.desc);
                row.createCell(2).setCellValue(r.alias);
                row.createCell(3).setCellValue(String.valueOf(r.qtaDoc));
                row.createCell(4).setCellValue("0");
                row.createCell(5).setCellValue(String.valueOf(r.qtaDoc));
                row.createCell(6).setCellValue(String.valueOf(r.qtaSpunta));
                row.createCell(7).setCellValue(String.valueOf(r.qtaSpunta - r.qtaDoc));
                row.createCell(8).setCellValue(r.nDoc);
                row.createCell(9).setCellValue(r.timeSp);
                row.createCell(10).setCellValue(utenteSpunta != null ? utenteSpunta : "");
                row.createCell(11).setCellValue(r.store);
                row.createCell(12).setCellValue(r.costo);
                row.createCell(13).setCellValue(r.rifornimento);
                row.createCell(14).setCellValue(r.idDocRemoto);
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

    private void caricaDaExcel(){
        XSSFWorkbook workbook;
        try {
            String outFileName = fileName;
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");
            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            utenteSpunta = workbook.getSheetAt(0).getRow(0).getCell(10).getStringCellValue();

            int i = 1;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);

                Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                Cell cell = row.getCell(7);
                if (cell == null) {
                    cell = row.createCell(7);
                }
                cell.setCellValue(newQta.toString());

                if(newQta!=0){
                    nDiff++;
                    if(nDiff==1){
                        nDocs = docsName.substring(2);
                        nDocs = nDocs.replace("_", " ");
                    }
                }
                tipoDoc = docsName.substring(0,2);

                codArt.add(row.getCell(0).getStringCellValue());
                desc.add(row.getCell(1).getStringCellValue());
                alias.add(row.getCell(2).getStringCellValue());
                qtaDoc.add(row.getCell(5).getStringCellValue());
                qtaSpunta.add(row.getCell(6).getStringCellValue());
                idDoc.add(row.getCell(14).getStringCellValue());
                if(row.getCell(12)!=null){
                    costoDoc = costoDoc + (Double.parseDouble(row.getCell(12).getStringCellValue())*Integer.parseInt(row.getCell(6).getStringCellValue()));
                }
                timeSp.add(row.getCell(9).getStringCellValue());
                nDoc.add(row.getCell(8).getStringCellValue());
                i++;
            }

            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();
            workbook.close();

            alertDisplayer("Attenzione!", "Il valore della merce spuntata è pari a: € " + costoDoc);

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
        timeSp = new ArrayList<>();
        idDoc = new ArrayList<>();

        try {
            SpuntaDao dao = appDb.spuntaDao();
            List<SpuntaRigaEntity> risultati = dao.cercaPerAlias(idSpuntaDocRoom, query);
            if(risultati.isEmpty()){
                risultati = dao.cercaPerCodArt(idSpuntaDocRoom, query);
            }
            for(SpuntaRigaEntity r : risultati){
                codArt.add(r.codArt);
                desc.add(r.desc);
                alias.add(r.alias);
                qtaDoc.add(String.valueOf(r.qtaDoc));
                qtaSpunta.add(String.valueOf(r.qtaSpunta));
                timeSp.add(r.timeSp);
                nDoc.add(r.nDoc);
                idDoc.add(r.idDocRemoto);
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
        timeSp = new ArrayList<>();

        try {
            String outFileName = fileName;
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");
            FileInputStream file = new FileInputStream(new File(path, outFileName));
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
                    timeSp.add(row.getCell(9).getStringCellValue());
                    nDoc.add(row.getCell(8).getStringCellValue());
                    find = true;
                }
                i++;
            }
            i = 1;
            if(!find){
                while(workbook4Ric.getSheetAt(0).getRow(i) != null){
                    if(workbook4Ric.getSheetAt(0).getRow(i).getCell(0).getStringCellValue().contains(query)){
                        Row row = workbook4Ric.getSheetAt(0).getRow(i);
                        codArt.add(row.getCell(0).getStringCellValue());
                        desc.add(row.getCell(1).getStringCellValue());
                        alias.add(row.getCell(2).getStringCellValue());
                        qtaDoc.add(row.getCell(5).getStringCellValue());
                        qtaSpunta.add(row.getCell(6).getStringCellValue());
                        timeSp.add(row.getCell(9).getStringCellValue());
                        nDoc.add(row.getCell(8).getStringCellValue());
                    }
                    i++;
                }
            }

            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook4Ric.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        setRows();
    }

    public void setRows(){
        AdapterReviewSpuntaNeg whatever = new AdapterReviewSpuntaNeg(this, codArt, desc, qtaDoc, qtaSpunta, alias, nDoc, fileName, tipo, timeSp, idDoc, tipoDoc, utenteSpunta, idSpuntaDocRoom);
        listView.setAdapter(whatever);
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
                            "Where store = '"+store+"' and ndoc = '"+nDocs+"' and tipodoc = '"+tipoDoc+"' ";
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
                    String query = "INSERT INTO mcDiscordanze (store, ndoc, ndiff, tipodoc, data) " +
                            "VALUES ('"+store+"', '"+nDocs+"', "+nDiff+", '"+tipoDoc+"', GETDATE() ) ";
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
}