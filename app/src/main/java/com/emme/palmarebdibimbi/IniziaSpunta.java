package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.ConnectionException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IniziaSpunta extends AppCompatActivity{

    ConnectionClass connectionClass;
    String insert = "";

    String docsName = "";
    String utente, nomeP, ipNeg, storeXFile;
    long idSpuntaDocRoom = -1;
    AppDb appDb;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> costo = new ArrayList<>();
    ArrayList<String> qtaDaScalare = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> codici = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> qtaSpunta = new ArrayList<>();
    ArrayList<String> qtaDocum = new ArrayList<>();
    ArrayList<String> numero = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> timeSparata = new ArrayList<>();
    ArrayList<String> serieDoc = new ArrayList<>();
    CheckBox chkEtic;
    String in;
    FloatingActionButton infoSC, printEtc, clearPage;
    String doc4Print = "";
    String printer = "";
    String data4Print = "";
    String bt = "";
    Spinner spinner;
    String fornitore = "";
    String segnaC = "";
    String fileName = "";
    ProgressBar pbSearchArt;
    String findThis, ubiDef, subiDef;
    Double prz;
    Integer mag, listino, esistenza, of, oc;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, lblUbic, lblSubic, txtPrzArt, txtEsSp, txtQtaLP, txtUbiDef, txtOrdF, txtOrdC, txtUbiRead, txtSubiDef, txtSubiRead;
    Button search, btnNextArt, btnBackArt, btnFine, addEan;
    EditText txtInsEAN, insNColliSpunta, insQtaSpunta, insUbicSpunta, insSubicSpunta, segnacollo;
    Context context;
    int giaPremuto = 0;
    int totColli = 0;
    String tipoDoc ="";
    com.zebra.sdk.comm.Connection connection;
    SharedPreferences prefs;
    FloatingActionButton btnChgUbic;

    public void inizializzaFile(){
        CancDoc cancDoc = new CancDoc();
        cancDoc.execute();
        codArticolo = ((MyApplication) this.getApplication()).getCodArt();
        qta = ((MyApplication) this.getApplication()).getQuantita();
        idDoc = ((MyApplication) this.getApplication()).getID();
        descrizioni = ((MyApplication) this.getApplication()).getDesc();
        costo = ((MyApplication) this.getApplication()).getCosto();
        ArrayList<String> idXArt = new ArrayList<>();
        ArrayList<String> qtaArtXID = new ArrayList<>();
        ArrayList<String> tempCodArt = new ArrayList<>();
        ArrayList<String> tempNumDoc = new ArrayList<>();
        ArrayList<String> tempDesc = new ArrayList<>();
        ArrayList<String> tempCosto = new ArrayList<>();
        for(int i=0; i<codArticolo.size(); i++) {
            Boolean ce = false;
            if (i == 0) {
                idXArt.add(idDoc.get(i));
                tempCodArt.add(codArticolo.get(i));
                tempNumDoc.add(numDoc.get(i));
                qtaArtXID.add(qta.get(i));
                tempDesc.add(descrizioni.get(i));
                tempCosto.add(costo.get(i));
            }else{
                for (int j = 0; j < tempCodArt.size(); j++) {
                    if (tempCodArt.get(j).equals(codArticolo.get(i)) && idXArt.get(j).equals(idDoc.get(i))) {
                        Integer qDaAgg = Integer.parseInt(qta.get(i));
                        Integer qPres = Integer.parseInt(qtaArtXID.get(j));
                        Integer totQ = qDaAgg + qPres;
                        qtaArtXID.set(j, totQ.toString());
                        ce = true;
                    }
                }
                if (!ce) {
                    tempCosto.add(costo.get(i));
                    idXArt.add(idDoc.get(i));
                    tempCodArt.add(codArticolo.get(i));
                    tempNumDoc.add(numDoc.get(i));
                    qtaArtXID.add(qta.get(i));
                    tempDesc.add(descrizioni.get(i));
                }
            }
        }
        codArticolo.clear();
        codArticolo = tempCodArt;
        qta.clear();
        qta = qtaArtXID;
        idDoc.clear();
        idDoc = idXArt;
        numDoc.clear();
        numDoc = tempNumDoc;
        descrizioni.clear();
        descrizioni = tempDesc;
        costo.clear();
        costo = tempCosto;

        for(int i=0; i<codArticolo.size(); i++){
            codici.add(codArticolo.get(i));
            qtaDaScalare.add(qta.get(i));
            qtaSpunta.add("0");
            alias.add("");
            timeSparata.add("");
            qtaDocum.add(qta.get(i));
            numero.add(numDoc.get(i));
        }

        Set<String> setUnico = new HashSet<>(idDoc);
        setUnico.remove(""); // escludi righe senza idDocRemoto
        ArrayList<String> idUnici = new ArrayList<>(setUnico);

        for(int i=0;i< idUnici.size(); i++){
            if(i==0){
                in = "('"+idUnici.get(i)+"'";
            }else{
                in = in + ",'" + idUnici.get(i) + "'";
            }
        }
        in = in + ")";
        updDocInArr updDocInArr = new updDocInArr();
        updDocInArr.execute();

        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet
        Sheet barcodes = workbook.createSheet("Barcode");
        Sheet segnacollo = workbook.createSheet("Info colli");
        Sheet ox = workbook.createSheet("Info OX");

        int x = 0;
        ArrayList<String> anotherInsert = new ArrayList<>();
        for (int i = 0; i < codici.size(); i++) {

            Row row = sheet.createRow(i + 1);
            if (i == 0) {
                Row testata = sheet.createRow(i);
                testata.createCell(0).setCellValue("Codice articolo");
                testata.createCell(1).setCellValue("Descrizione");
                testata.createCell(2).setCellValue("Alias");
                testata.createCell(3).setCellValue("Ubicazione");
                testata.createCell(4).setCellValue("Sottoubicazione");
                testata.createCell(5).setCellValue("Quantita documento");
                testata.createCell(6).setCellValue("Quantita spunta");
                testata.createCell(7).setCellValue("Differenza");
                testata.createCell(8).setCellValue("N. Doc");
                testata.createCell(9).setCellValue("Note");
                testata.createCell(10).setCellValue("Magazzino");
                testata.createCell(11).setCellValue("Sparata");
                testata.createCell(12).setCellValue("Colli");
                testata.createCell(13).setCellValue("Costo");
                testata.createCell(14).setCellValue("ID Documento");
                testata.createCell(15).setCellValue(utente);
                Row testataB = barcodes.createRow(i);
                testataB.createCell(0).setCellValue("Codice articolo");
                testataB.createCell(1).setCellValue("EAN");
                Row testataSC = segnacollo.createRow(i);
                testataSC.createCell(0).setCellValue("Codice articolo");
                testataSC.createCell(1).setCellValue("EAN");
                testataSC.createCell(2).setCellValue("Qta");
                testataSC.createCell(3).setCellValue("N. Collo");
                Row testataOX = ox.createRow(i);
                testataOX.createCell(0).setCellValue("Codice articolo");
                testataOX.createCell(1).setCellValue("EAN");
                testataOX.createCell(2).setCellValue("Qta");
                testataOX.createCell(3).setCellValue("Serie");
                testataOX.createCell(4).setCellValue("N. Doc");
                testataOX.createCell(5).setCellValue("Data consegna");
                testataOX.createCell(6).setCellValue("Cliente");
            }
            row.createCell(0).setCellValue(codici.get(i));
            row.createCell(1).setCellValue(descrizioni.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue("");
            num2.setCellType(CellType.STRING);
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(qta.get(i));
            row.createCell(6).setCellValue("0");
            int risultato = Integer.parseInt(qta.get(i)) - (Integer.parseInt(qta.get(i))*2);
            row.createCell(7).setCellValue(risultato);
            row.createCell(8).setCellValue(numDoc.get(i));
            row.createCell(9).setCellValue("");
            row.createCell(10).setCellValue(storeXFile);
            row.createCell(11).setCellValue("");
            row.createCell(12).setCellValue("0");
            row.createCell(13).setCellValue(costo.get(i));
            row.createCell(14).setCellValue(idDoc.get(i));

            if(x>999){
                anotherInsert.add(insert);
                insert = "";
                x = 0;
            }
            insert = insert + "('"+codici.get(i)+"'," + "'"+descrizioni.get(i)+"', '', '', ''," +
                    qta.get(i) + ", 0, "+risultato+", '"+numDoc.get(i)+"', '',  '"+nomeP+"', '"+utente+"' ),";
            x++;
        }
        insert = insert.substring(0,insert.length()-1);
        anotherInsert.add(insert);

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            Log.w("FileUtils", "Writing file" + file);
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {

                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        CreaDoc creaDoc = new CreaDoc();
        creaDoc.execute();
    }

    /**
     * Popola le ArrayList locali leggendo direttamente da Room.
     * Nessun file Excel viene creato.
     */
    private void inizializzaDaRoom(){
        try {
            SpuntaDao dao = appDb.spuntaDao();
            List<SpuntaRigaEntity> righe = dao.getRigheByDocumento(idSpuntaDocRoom);

            for(SpuntaRigaEntity r : righe){
                codici.add(r.codArt);
                descrizioni.add(r.desc);
                qtaDaScalare.add(String.valueOf(r.qtaDoc));
                qtaSpunta.add(String.valueOf(r.qtaSpunta));
                alias.add(r.alias);
                timeSparata.add(r.timeSp);
                qtaDocum.add(String.valueOf(r.qtaDoc));
                numero.add(r.nDoc);
                codArticolo.add(r.codArt);
                qta.add(String.valueOf(r.qtaDoc));
                idDoc.add(r.idDocRemoto);
                numDoc.add(r.nDoc);
                costo.add(r.costo);
            }

            // updDocInArr per marcare i documenti come "in arrivo" sul server
            Set<String> setUnico = new HashSet<>(idDoc);
            setUnico.remove(""); // escludi righe senza idDocRemoto
            ArrayList<String> idUnici = new ArrayList<>(setUnico);
            for(int i=0; i< idUnici.size(); i++){
                if(i==0){
                    in = "('"+idUnici.get(i)+"'";
                }else{
                    in = in + ",'" + idUnici.get(i) + "'";
                }
            }
            in = in + ")";
            updDocInArr updDoc = new updDocInArr();
            updDoc.execute();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void deleteDoc(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.cancel();
                    docPres("Attenzione!","Esiste già un documento per questa spunta");
                })
                .setPositiveButton("SI", (dialog, which) -> {
                    File fileEx = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
                    fileEx.delete();
                    inizializzaFile();
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void docPres(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("CANCELLA FILE", (dialog, which) -> {
                    deleteDoc("Attenzione!", "Sei sicuro di voler eliminare il file e riniziare la spunta? Il file non potrà essere in alcun modo recuperato!");
                })
               .setPositiveButton("RECUPERA FILE", (dialog, which) -> {
                    dialog.cancel();
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
    }

    @Override
    public void onBackPressed() {
        alertDisplayer("Attenzione!","Sei sicuro di voler abbandonare la pagina di spunta? Verrai riportato alla home!");
    }

    private void showQtaXC(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public boolean isExcelFileEmpty(File file) {
        if (!file.exists() || !file.isFile()) {
            return true; // Il file non esiste o non è un file valido
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = WorkbookFactory.create(fis);

            // Itera su tutti i fogli
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                // Itera su tutte le righe del foglio
                for (Row row : sheet) {
                    // Itera su tutte le celle della riga
                    for (Cell cell : row) {
                        if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                            // Se troviamo almeno una cella non vuota, il file non è vuoto
                            workbook.close();
                            return false;
                        }
                    }
                }
            }

            // Chiudi il workbook per liberare risorse
            workbook.close();

            // Se non sono state trovate celle non vuote, il file è vuoto
            return true;

        } catch (IOException | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
            return true; // Se c'è un errore nel leggere il file, consideralo vuoto o invalido
        }
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaSpunta.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void changeUbic(String title,String message, int sOrU){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    if(sOrU==1){
                        insUbicSpunta.setEnabled(false);
                        insSubicSpunta.setVisibility(View.VISIBLE);
                        lblSubic.setVisibility(View.VISIBLE);
                        insSubicSpunta.setEnabled(true);
                    }else{
                        insSubicSpunta.setEnabled(false);
                    }
                })
                .setPositiveButton("Si", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_spunta);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        appDb = AppDb.getInstance(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            utente = extras.getString("utente");
            tipoDoc = extras.getString("tipoDoc");
            fornitore = extras.getString("fornitore");
            segnaC = extras.getString("segnaC");
            idSpuntaDocRoom = extras.getLong("idSpuntaDocRoom", -1);
        }
        fornitore = fornitore.replace("è","e");
        fornitore = fornitore.replace("é","e");
        fornitore = fornitore.replace("à","a");
        fornitore = fornitore.replace("ì","i");
        fornitore = fornitore.replace("ò","o");
        fornitore = fornitore.replace("ù","u");
        fornitore = fornitore.replace("'","");

        connectionClass = new ConnectionClass();

        context = this;

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        bt = p.getString("PrinterIp","");
        printer = p.getString("printer", "");
        numDoc = ((MyApplication) this.getApplication()).getNum();
        serieDoc = ((MyApplication) this.getApplication()).getSerie();
        nomeP = p.getString("NomePalm","");
        docsName = "";
        String tempnDoc = "";
        for(int z=0; z<numDoc.size(); z++) {
            if(z==0){
                docsName = tipoDoc + "_" + fornitore + "_" + numDoc.get(0) + "_" + serieDoc.get(0);
            }else if(!numDoc.get(z).equals(tempnDoc)){
                docsName = docsName + "_" + numDoc.get(z) + "_" + serieDoc.get(z);
            }
            tempnDoc = numDoc.get(z);
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        fileName = nomeP+"_spunta_"+docsName+"_"+year+".xlsx"; //Name of the file

        switch (mag) {
            case 1:
                storeXFile = "MasterMag";
                ipNeg = "192.168.2.41";
                break;
            case 77:
                storeXFile = "SESTU";
                ipNeg = "192.168.1.20";
                break;
            case 35:
                storeXFile = "MARCONI";
                ipNeg = "192.168.1.20";
                break;
            case 72:
                storeXFile = "PIRRI";
                ipNeg = "192.168.1.20";
                break;
            case 76:
                storeXFile = "OLBIA";
                ipNeg = "192.168.1.10";
                break;
            case 74:
                storeXFile = "SASSARI";
                ipNeg = "192.168.1.20";
                break;
            case 32:
                storeXFile = "NUORO";
                ipNeg = "192.168.1.20";
                break;
            case 78:
                storeXFile = "CARBONIA";
                ipNeg = "192.168.1.20";
                break;
            case 75:
                storeXFile = "TORTOLI";
                ipNeg = "192.168.1.20";
                break;
            case 71:
                storeXFile = "ORISTANO";
                ipNeg = "192.168.1.20";
                break;
            case 85:
                storeXFile = "Tiburtina";
                ipNeg = "195.100.100.202";
                break;
            case 87:
                storeXFile = "Capena";
                ipNeg = "192.168.188.20";
                break;
            case 86:
                storeXFile = "Ostiense";
                ipNeg = "196.100.100.203";
                break;
            case 59:
                storeXFile = "INLAVORAZIONE";
                mag = 1;
                ipNeg = "192.168.2.41";
                break;
            case 90:
                storeXFile = "Casilina";
                ipNeg = "192.168.1.20";
                break;
            case 94:
                storeXFile = "Pomezia";
                ipNeg = "192.168.1.20";
                break;
            case 112:
                storeXFile = "Ardeatina";
                ipNeg = "192.168.1.20";
                break;
            case 114:
                storeXFile = "Verona";
                ipNeg = "192.168.16.20";
                break;
            case 111:
                storeXFile = "RomaCedi";
                ipNeg = "192.168.1.20";
                break;
            case 91:
                storeXFile = "MasterMagRoma";
                ipNeg = "195.100.100.202";
                break;
            case 88:
                storeXFile = "INTRANSITO";
                mag = 1;
                ipNeg = "192.168.2.41";
                break;
            case 89:
                storeXFile = "INTEMPORANEO";
                mag = 1;
                ipNeg = "192.168.2.41";
                break;
            case 93:
                storeXFile = "CEDIROMAINLAV";
                mag = 111;
                ipNeg = "192.168.1.20";
                break;
            default:
                break;
        }

        if(idSpuntaDocRoom > 0){
            // Documento Room già creato in ShowDoc, popola strutture locali da Room
            inizializzaDaRoom();
        } else {
            // Fallback: controlla il file Excel (compatibilità)
            File fileEx = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
            if(fileEx.exists()){
                File directory = new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaGen");
                File file = new File(directory, fileName);
                if (isExcelFileEmpty(file)) {
                    alertDisplayer("Attenzione!","Documento danneggiato, ti consigliamo di contattare l'assistenza prima di procedere, potresti perdere i dati ");
                } else {
                    docPres("Attenzione!","Documento presente");
                }
            }else{
                inizializzaFile();
            }
        }

        spinner = findViewById(R.id.spnEtDep);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_dep_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        search = findViewById(R.id.btnSearchThisArt);
        qtaDoc = findViewById(R.id.txtQtaDoc);
        txtCodArt = findViewById(R.id.txtShowCodArt);
        txtDesc = findViewById(R.id.txtShowDesc);
        txtInsEAN = findViewById(R.id.txtInsEAN);
        insNColliSpunta = findViewById(R.id.insNColliSpunta);
        insQtaSpunta = findViewById(R.id.insQtaSpunta);
        insUbicSpunta = findViewById(R.id.insUbicSpunta);
        btnNextArt = findViewById(R.id.btnNextArt);
        btnBackArt = findViewById(R.id.btnBackArt);
        btnFine = findViewById(R.id.btnChiudiSpunta);
        lblUbic = findViewById(R.id.lblUbic);
        lblColli = findViewById(R.id.lblNC);
        addEan = findViewById(R.id.btnAddEan);
        lblQta = findViewById(R.id.lblQXC);
        lblSubic = findViewById(R.id.lblSubic);
        insSubicSpunta = findViewById(R.id.insSubicSpunta);
        txtEsSp = findViewById(R.id.txtEsSp);
        txtPrzArt = findViewById(R.id.txtPrzArt);
        txtQtaLP = findViewById(R.id.txtQtaLP);
        txtUbiDef = findViewById(R.id.txtUbiDef);
        txtOrdF = findViewById(R.id.txtOrdF);
        txtOrdC = findViewById(R.id.txtOrdC);
        txtUbiRead = findViewById(R.id.txtUbiRead);
        pbSearchArt = findViewById(R.id.pbSearchArt);
        txtSubiDef = findViewById(R.id.txtSubiDef);
        txtSubiRead = findViewById(R.id.txtSubiRead);
        chkEtic = findViewById(R.id.chkEtDep);
        Button msc = findViewById(R.id.btnMSC);
        Button psc = findViewById(R.id.btnPSC);
        segnacollo = findViewById(R.id.edtSegnaC);
        TextView txtSC = findViewById(R.id.txtSC);
        infoSC = findViewById(R.id.floatInfoSC);
        printEtc = findViewById(R.id.btnPrintEtc);
        clearPage = findViewById(R.id.btnClearPage);
        btnChgUbic = findViewById(R.id.fltBtnChgUbic);

        pbSearchArt.setVisibility(View.GONE);
        btnBackArt.setVisibility(View.GONE);
        btnNextArt.setVisibility(View.GONE);
        printEtc.setVisibility(View.GONE);
        clearPage.setVisibility(View.GONE);
        btnChgUbic.setVisibility(View.GONE);
        lblQta.setVisibility(View.GONE);
        lblColli.setVisibility(View.GONE);
        lblUbic.setVisibility(View.GONE);
        insNColliSpunta.setVisibility(View.GONE);
        insNColliSpunta.setEnabled(false);
        insQtaSpunta.setVisibility(View.GONE);
        insQtaSpunta.setEnabled(false);
        insUbicSpunta.setVisibility(View.GONE);
        insUbicSpunta.setEnabled(false);
        insSubicSpunta.setVisibility(View.GONE);
        insSubicSpunta.setEnabled(false);
        lblSubic.setVisibility(View.GONE);

        setEditTextMaxLength(insUbicSpunta,4);
        setEditTextMaxLength(insSubicSpunta,4);

        txtInsEAN.setFocusableInTouchMode(true);
        txtInsEAN.requestFocus();
        txtInsEAN.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbicSpunta.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insSubicSpunta.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insQtaSpunta.setSelectAllOnFocus(true);
        insNColliSpunta.setSelectAllOnFocus(true);
        insSubicSpunta.setSelectAllOnFocus(true);
        insUbicSpunta.setSelectAllOnFocus(true);

        if(segnaC.equals("PROGRESSIVO")){
            segnacollo.setText("1");
        }else if(segnaC.equals("LIBERO")){
            msc.setVisibility(View.GONE);
            psc.setVisibility(View.GONE);
        }else{
            msc.setVisibility(View.GONE);
            psc.setVisibility(View.GONE);
            segnacollo.setVisibility(View.GONE);
            txtSC.setVisibility(View.GONE);
            infoSC.setVisibility(View.GONE);
        }
        clearPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtQtaLP.setText("");
                txtOrdC.setText("");
                txtOrdF.setText("");
                txtEsSp.setText("");
                txtUbiDef.setText("");
                txtSubiDef.setText("");
                txtSubiRead.setText("");
                txtSubiDef.setText("");
                txtUbiRead.setText("");
                txtCodArt.setText("");
                txtDesc.setText("");
                qtaDoc.setText("");
                txtPrzArt.setText("");
                btnBackArt.setVisibility(View.GONE);
                btnFine.setVisibility(View.VISIBLE);
                btnNextArt.setVisibility(View.GONE);
                printEtc.setVisibility(View.GONE);
                clearPage.setVisibility(View.GONE);
                btnChgUbic.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                lblUbic.setVisibility(View.GONE);
                lblSubic.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                insQtaSpunta.setEnabled(false);
                insUbicSpunta.setVisibility(View.GONE);
                insUbicSpunta.setEnabled(false);
                insSubicSpunta.setVisibility(View.GONE);
                insSubicSpunta.setEnabled(false);
                txtInsEAN.setEnabled(true);
                txtInsEAN.setText("");
                insQtaSpunta.setText("1");
                insUbicSpunta.setText("");
                insSubicSpunta.setText("");
                insNColliSpunta.setText("1");
                txtInsEAN.setFocusableInTouchMode(true);
                txtInsEAN.requestFocus();
            }
        });
        printEtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                    connection = new BluetoothConnection(bt);
                    try{
                        connection.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    }
                    alertStampaEtichette("Stampa etichette", "");
                }
            }
        });
        psc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer sc = Integer.parseInt(segnacollo.getText().toString());
                sc++;
                segnacollo.setText(sc.toString());
            }
        });
        msc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer sc = Integer.parseInt(segnacollo.getText().toString());
                sc--;
                segnacollo.setText(sc.toString());
            }
        });
        FloatingActionButton btnInfoOX = findViewById(R.id.btnInfoOXP);
        btnInfoOX.setOnClickListener(v -> {
            IniziaSpunta.InfoOX info = new IniziaSpunta.InfoOX();
            info.execute();
        });
        infoSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer totXCollo = 0;
                String cercaCollo = segnacollo.getText().toString();
                if(idSpuntaDocRoom > 0){
                    try {
                        totXCollo = appDb.spuntaDao().sumQtaPerSegnacollo(idSpuntaDocRoom, cercaCollo);
                    } catch(Exception e){ e.printStackTrace(); }
                } else {
                XSSFWorkbook workbook;

                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);
                    int i = 0;
                    if(!segnaC.equals("NO")){
                        while(workbook.getSheetAt(2).getRow(i) != null){
                            Row row = workbook.getSheetAt(2).getRow(i);
                            if(row.getCell(3).getStringCellValue().equals(cercaCollo)){
                                totXCollo = totXCollo + Integer.parseInt(row.getCell(2).getStringCellValue());
                            }
                            i++;
                        }
                    }
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
                showQtaXC("Info","Sono stati sparati "+totXCollo+" articoli nel collo "+cercaCollo+" ");
            }
        });
        txtInsEAN.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = txtInsEAN.getText().toString();
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtUbiDef.setText("");
                    txtSubiDef.setText("");
                    txtSubiRead.setText("");
                    txtSubiDef.setText("");
                    txtUbiRead.setText("");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    if(!findThis.equals("")){
                        IniziaSpunta.FindArt cercaArt = new IniziaSpunta.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });
        chkEtic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    connection = new BluetoothConnection(bt);
                    try{
                        connection.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try{
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        insUbicSpunta.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && !insUbicSpunta.getText().toString().equals("")){
                changeUbic("Attenzione!", "Esiste già un ubicazione per questo articolo, modificandola verrà sostituita anche alle altre occorrenze dello stesso, modificare comunque?", 1);
            }
        });
        insSubicSpunta.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && !insSubicSpunta.getText().toString().equals("")){
                changeUbic("Attenzione!", "Esiste già una sottoubicazione per questo articolo, modificandola verrà sostituita anche alle altre occorrenze dello stesso, modificare comunque?", 2);
            }
        });
        btnFine.setOnClickListener(v -> {
            chiudiSpunta("Attenzione","Sei sicuro di voler concludere la spunta e salvare il documento?");
        });
        btnNextArt.setOnClickListener(v -> {
            if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSpunta.setEnabled(true);
                insQtaSpunta.setFocusableInTouchMode(true);
                insQtaSpunta.requestFocus();
                showSoftKeyboard(insQtaSpunta);
            }else if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insUbicSpunta.setVisibility(View.VISIBLE);
                lblUbic.setVisibility(View.VISIBLE);
                insUbicSpunta.setEnabled(true);
                insUbicSpunta.setFocusableInTouchMode(true);
                insUbicSpunta.requestFocus();

                cercaEStampa();
                /*
                if(!connection.isConnected()){
                    connection = new BluetoothConnection(bt);
                    try{
                        connection.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
                cercaOXetichette cercaOXetichette = new cercaOXetichette();
                cercaOXetichette.execute();

                showSoftKeyboard(insUbicSpunta);
            }else if(insUbicSpunta.isEnabled()){
                insUbicSpunta.setEnabled(false);
                insSubicSpunta.setVisibility(View.VISIBLE);
                lblSubic.setVisibility(View.VISIBLE);
                insSubicSpunta.setEnabled(true);
                insSubicSpunta.setFocusableInTouchMode(true);
                insSubicSpunta.requestFocus();
                showSoftKeyboard(insSubicSpunta);
            }else if(insSubicSpunta.isEnabled()){
                insSubicSpunta.setEnabled(false);
            }else if(!insSubicSpunta.isEnabled() && insSubicSpunta.getVisibility() == View.VISIBLE){
                alertDisplayer2();
            }
        });
        btnBackArt.setOnClickListener(v -> {
            if(insSubicSpunta.isEnabled()){
                insSubicSpunta.setEnabled(false);
                insSubicSpunta.setVisibility(View.GONE);
                lblSubic.setVisibility(View.GONE);
                insUbicSpunta.setEnabled(true);
                insUbicSpunta.setFocusableInTouchMode(true);
                insUbicSpunta.requestFocus();
                showSoftKeyboard(insUbicSpunta);
            }else if(insUbicSpunta.isEnabled()){
                insUbicSpunta.setEnabled(false);
                insUbicSpunta.setVisibility(View.GONE);
                lblUbic.setVisibility(View.GONE);
                insQtaSpunta.setEnabled(true);
                insQtaSpunta.setFocusableInTouchMode(true);
                insQtaSpunta.requestFocus();
                showSoftKeyboard(insQtaSpunta);
            }else if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(true);
                insNColliSpunta.setFocusableInTouchMode(true);
                insNColliSpunta.requestFocus();
                showSoftKeyboard(insNColliSpunta);
            }else if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insNColliSpunta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                txtInsEAN.setEnabled(true);
                txtInsEAN.setFocusableInTouchMode(true);
                txtInsEAN.requestFocus();
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                printEtc.setVisibility(View.GONE);
                clearPage.setVisibility(View.GONE);
                btnChgUbic.setVisibility(View.GONE);
                btnFine.setVisibility(View.VISIBLE);
            }else if(!insSubicSpunta.isEnabled() && insSubicSpunta.getVisibility() == View.VISIBLE){
                insSubicSpunta.setEnabled(true);
                insSubicSpunta.setFocusableInTouchMode(true);
                insSubicSpunta.requestFocus();
                showSoftKeyboard(insSubicSpunta);
            }
        });
        search.setOnClickListener(v -> {
            findThis = txtInsEAN.getText().toString();
            if(findThis.equals("")){
                nienteInRicerca("Error!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                txtQtaLP.setText("");
                txtOrdC.setText("");
                txtOrdF.setText("");
                txtEsSp.setText("");
                txtUbiDef.setText("");
                txtSubiDef.setText("");
                txtSubiRead.setText("");
                txtSubiDef.setText("");
                txtUbiRead.setText("");
                txtCodArt.setText("");
                txtDesc.setText("");
                qtaDoc.setText("");
                txtPrzArt.setText("");
                FindArt cercaArt = new FindArt();
                cercaArt.execute("");
            }
        });
        addEan.setOnClickListener(v -> {
            aggiungiEan("","");
        });

        btnChgUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUbic changeUbic = new ChangeUbic(txtCodArt.getText().toString());
                changeUbic.execute();
            }
        });

        // Pre-carica i dati degli articoli del documento in cache locale
        if (idSpuntaDocRoom > 0) {
            new PreloadArticoli().execute();
        }
    }

    public void cercaEStampa(){
        if(idSpuntaDocRoom > 0){
            // Percorso Room
            try {
                Integer nColli = Integer.parseInt(insNColliSpunta.getText().toString());
                if(nColli <= 0) nColli = 1;
                List<SpuntaRigaEntity> righe = appDb.spuntaDao().getRigheByCodArt(idSpuntaDocRoom, txtCodArt.getText().toString());
                doc4Print = !righe.isEmpty() ? righe.get(0).nDoc : "";
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{ connection.open(); } catch(Exception e){ e.printStackTrace(); }
                    }
                    stampaZebra(nColli.toString());
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            return;
        }
        if(idSpuntaDocRoom <= 0){
        XSSFWorkbook workbook;
        try {
            String outFileName = fileName;
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);
            Integer qtaSpuntata;
            if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
            }else{
                qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
            }
            Integer lastIndex = -1;
            int i = 0, z = 0;
            Integer nColli = 0;
            i=0;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                    lastIndex = i;
                    if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))>=qtaSpuntata && qtaSpuntata>0){
                        doc4Print = row.getCell(8).getStringCellValue();
                        nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                        if(chkEtic.isChecked()){
                            if(!connection.isConnected()){
                                connection = new BluetoothConnection(bt);
                                try{
                                    connection.open();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            stampaZebra(nColli.toString());
                        }qtaSpuntata = 0;
                    }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){
                    }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                        doc4Print = row.getCell(8).getStringCellValue();
                        Integer qtaRim = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                        qtaSpuntata = qtaSpuntata - qtaRim;
                        nColli = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))/Integer.parseInt(insQtaSpunta.getText().toString());
                        if(chkEtic.isChecked()){
                            if(!connection.isConnected()){
                                connection = new BluetoothConnection(bt);
                                try{
                                    connection.open();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            stampaZebra(nColli.toString());
                        }
                    }
                }
                i++;
            }
            Row row = workbook.getSheetAt(0).getRow(lastIndex);
            if(qtaSpuntata>0 && lastIndex != -1){
                doc4Print = row.getCell(8).getStringCellValue();
                nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{
                            connection.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    stampaZebra(nColli.toString());
                }
            }else if(qtaSpuntata>0 && lastIndex == -1){
                doc4Print = "";
                nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{
                            connection.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    stampaZebra(nColli.toString());
                }
            }
            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }

    private void aggiungiEan(String title,String message){
        if(!txtCodArt.getText().equals("")){
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.add_barcode_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                    .setView(dialoglayout);
            TextView codArt = dialoglayout.findViewById(R.id.txtCodArtAddBC);
            TextView desc = dialoglayout.findViewById(R.id.txtDescAddBC);
            EditText insert = dialoglayout.findViewById(R.id.insertBarcode);
            Button close = dialoglayout.findViewById(R.id.btnAddBCClose);
            Button aggiungi = dialoglayout.findViewById(R.id.btnAddBcToFile);
            String stk = txtCodArt.getText().toString();
            codArt.setText(stk);
            desc.setText(txtDesc.getText());
            AlertDialog ok = builder.create();
            ok.show();
            insert.setFocusableInTouchMode(true);
            insert.requestFocus();
            insert.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
            aggiungi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!insert.getText().toString().equals("")){
                        ArrayList<String> eanToAdd = new ArrayList<>();
                        String tuttiGliEan = insert.getText().toString();
                        int i=0;
                        while(!tuttiGliEan.equals("")){
                            if(tuttiGliEan.charAt(i)=='\n'){
                                boolean present = false;
                                for(int j=0;j<eanToAdd.size();j++){
                                    if(eanToAdd.get(j).equals(tuttiGliEan.substring(0,i))){
                                        present = true;
                                    }
                                }
                                if(!present){
                                    eanToAdd.add(tuttiGliEan.substring(0,i));
                                }
                                tuttiGliEan = tuttiGliEan.substring(i+1);
                                i=0;
                            }
                            i++;
                        }
                        if(idSpuntaDocRoom > 0){
                            try {
                                for(int x = 0; x < eanToAdd.size(); x++){
                                    SpuntaEanEntity eanEntity = new SpuntaEanEntity();
                                    eanEntity.idSpuntaDoc = idSpuntaDocRoom;
                                    eanEntity.codArt = stk;
                                    eanEntity.ean = eanToAdd.get(x);
                                    appDb.spuntaDao().insertEan(eanEntity);
                                }
                            } catch(Exception e){ e.printStackTrace(); }
                        } else {
                        XSSFWorkbook workbook;
                        try {
                            String outFileName = fileName;

                            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                            FileInputStream file = new FileInputStream(new File(path, outFileName));
                            workbook = new XSSFWorkbook(file);
                            int z = 0;
                            while(workbook.getSheet("Barcode").getRow(z) != null){
                                z++;
                            }
                            for(int x=0;x<eanToAdd.size();x++){
                                Row row = workbook.getSheetAt(1).createRow(z);
                                row.createCell(0).setCellValue(stk);
                                row.createCell(1).setCellValue(eanToAdd.get(x));
                                z++;
                            }
                            file.close();

                            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                            workbook.write(outFile);
                            outFile.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String outFileName = fileName;
                        String path = "/storage/emulated/0/NAS/SpuntaGen";
                        copyFile(path, outFileName, "/storage/emulated/0/Backup");
                        }
                    }
                    ok.dismiss();
                    ok.cancel();
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ok.dismiss();
                    ok.cancel();
                }
            });
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

    private void alertDisplayer2(){
        if(idSpuntaDocRoom <= 0){
        XSSFWorkbook workbook;
        try {
            String outFileName = fileName;
            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);
            Integer qtaSpuntata;
            if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
            }else{
                qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
            }
            Date date = new Date();   // given date
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
            Integer minutes = calendar.get(Calendar.MINUTE);
            String nMinutes;
            if(minutes.toString().length()==1){
                nMinutes = "0"+minutes.toString();
            }else{
                nMinutes = minutes.toString();
            }
            String timing = hours + ":" + nMinutes;
            Integer lastIndex = -1;
            int i = 0, z = 0;
            Integer nColli = 0;
            if(!segnaC.equals("NO")){
                boolean inSC = false;
                while(workbook.getSheetAt(2).getRow(i) != null){
                    Row row = workbook.getSheetAt(2).getRow(i);
                    if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && row.getCell(3).getStringCellValue().equals(segnacollo.getText().toString())){
                        inSC = true;
                        Integer actQta = Integer.parseInt(row.getCell(2).getStringCellValue());
                        Integer newQta = actQta+qtaSpuntata;
                        row.createCell(2).setCellValue(newQta.toString());
                    }
                    i++;
                }
                if(!inSC){
                    Row row = workbook.getSheetAt(2).createRow(i);
                    row.createCell(0).setCellValue(txtCodArt.getText().toString());
                    row.createCell(1).setCellValue(txtInsEAN.getText().toString());
                    row.createCell(2).setCellValue(qtaSpuntata.toString());
                    row.createCell(3).setCellValue(segnacollo.getText().toString());
                }
            }
            i=0;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                    lastIndex = i;
                    if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))>=qtaSpuntata && qtaSpuntata>0){
                        doc4Print = row.getCell(8).getStringCellValue();
                        nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                        if(chkEtic.isChecked()){
                            if(!connection.isConnected()){
                                connection = new BluetoothConnection(bt);
                                try{
                                    connection.open();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //stampaZebra(nColli.toString());
                        }
                        Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                        Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                        row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                        qtaSpuntata = 0;
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        row.createCell(11).setCellValue(timing);
                        row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                        row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                        Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                        row.createCell(12).setCellValue(newTotColli.toString());
                        AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue(), insUbicSpunta.getText().toString(), insSubicSpunta.getText().toString(), 0);
                        aggDoc.execute();
                    }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){
                     }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                        doc4Print = row.getCell(8).getStringCellValue();
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        Integer qtaRim = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                        qtaSpuntata = qtaSpuntata - qtaRim;
                        nColli = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))/Integer.parseInt(insQtaSpunta.getText().toString());
                        if(chkEtic.isChecked()){
                            if(!connection.isConnected()){
                                connection = new BluetoothConnection(bt);
                                try{
                                    connection.open();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            //stampaZebra(nColli.toString());
                        }
                        Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                        row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                        row.createCell(11).setCellValue(timing);
                        row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                        row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                        Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                        row.createCell(12).setCellValue(newTotColli.toString());
                        AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue(), insUbicSpunta.getText().toString(), insSubicSpunta.getText().toString(), 0);
                        aggDoc.execute();
                    }
                }else if(i == 0 && qtaSpuntata == 0){
                    int j = 0;
                    while(workbook.getSheetAt(0).getRow(j) != null){
                        Row row2 = workbook.getSheetAt(0).getRow(i);
                        if(row2.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                            row2.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                            row2.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                            AggDoc aggDoc = new AggDoc(txtCodArt.getText().toString(), insUbicSpunta.getText().toString(), insSubicSpunta.getText().toString(), 1);
                            aggDoc.execute();
                        }
                        j++;
                    }
                }
                i++;
            }
            Row row = workbook.getSheetAt(0).getRow(lastIndex);
            if(qtaSpuntata>0 && lastIndex != -1){
                doc4Print = row.getCell(8).getStringCellValue();
                nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{
                            connection.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //stampaZebra(nColli.toString());
                }
                row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                row.createCell(1).setCellValue(txtDesc.getText().toString());
                Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                row.createCell(6).setCellValue(qtaUltima.toString());
                row.createCell(11).setCellValue(timing);
                Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                row.createCell(12).setCellValue(newTotColli.toString());
                AggDoc aggDoc = new AggDoc(qtaUltima, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue(), insUbicSpunta.getText().toString(), insSubicSpunta.getText().toString(), 0);
                aggDoc.execute();
            }else if(qtaSpuntata>0 && lastIndex == -1){
                row = workbook.getSheetAt(0).createRow(i);
                doc4Print = "";
                nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                if(chkEtic.isChecked()){
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{
                            connection.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //stampaZebra(nColli.toString());
                }
                row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                row.createCell(1).setCellValue(txtDesc.getText().toString());
                row.createCell(0).setCellValue(txtCodArt.getText().toString());
                row.createCell(6).setCellValue(qtaSpuntata.toString());
                row.createCell(5).setCellValue("0");
                row.createCell(8).setCellValue("");
                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
                row.createCell(7).setCellValue(qtaSpuntata.toString());
                row.createCell(11).setCellValue(timing);
                row.createCell(12).setCellValue(nColli.toString());
                row.createCell(14).setCellValue("");
                insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '"+insUbicSpunta.getText().toString()+"', '"+insSubicSpunta.getText().toString()+"', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                CreaDoc creaDoc = new CreaDoc();
                creaDoc.execute();

            }

            UpdateThings updateThings = new UpdateThings();
            updateThings.execute();

            i = 0;
            Integer qtaLP = 0;
            while(workbook.getSheetAt(0).getRow(i) != null){
                row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                    qtaLP = qtaLP + Integer.parseInt(row.getCell(6).getStringCellValue());;
                }
                i++;
            }
            txtQtaLP.setText(qtaLP.toString());
            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String outFileName = fileName;
        String path = "/storage/emulated/0/NAS/SpuntaGen";
        copyFile(path, outFileName, "/storage/emulated/0/Backup");
        }

        // Aggiorna anche Room
        Integer qtaSpuntataRoom;
        if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
            qtaSpuntataRoom = Integer.parseInt(insQtaSpunta.getText().toString());
        }else{
            qtaSpuntataRoom = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
        }
        Date dateR = new Date();
        Calendar calR = GregorianCalendar.getInstance();
        calR.setTime(dateR);
        int hoursR = calR.get(Calendar.HOUR_OF_DAY);
        Integer minutesR = calR.get(Calendar.MINUTE);
        String timingR = hoursR + ":" + (minutesR.toString().length()==1 ? "0"+minutesR : minutesR.toString());
        aggiornaSpuntaInRoom(txtCodArt.getText().toString(), qtaSpuntataRoom,
                txtInsEAN.getText().toString(), timingR,
                insUbicSpunta.getText().toString(), insSubicSpunta.getText().toString(),
                Integer.parseInt(insNColliSpunta.getText().toString()),
                segnacollo != null ? segnacollo.getText().toString() : "");

        // Aggiorna immediatamente la quantità letta visualizzata, senza attendere il thread Room
        int qtaLPCorrente = 0;
        try { qtaLPCorrente = Integer.parseInt(txtQtaLP.getText().toString()); } catch (Exception ignored) {}
        txtQtaLP.setText(String.valueOf(qtaLPCorrente + qtaSpuntataRoom));

        txtUbiRead.setText(insUbicSpunta.getText().toString());
        txtSubiRead.setText(insSubicSpunta.getText().toString());
        totColli += Integer.parseInt(insNColliSpunta.getText().toString());
        btnBackArt.setVisibility(View.GONE);
        btnFine.setVisibility(View.VISIBLE);
        btnNextArt.setVisibility(View.GONE);
        printEtc.setVisibility(View.GONE);
        clearPage.setVisibility(View.GONE);
        btnChgUbic.setVisibility(View.GONE);
        lblQta.setVisibility(View.GONE);
        lblColli.setVisibility(View.GONE);
        lblUbic.setVisibility(View.GONE);
        lblSubic.setVisibility(View.GONE);
        insNColliSpunta.setVisibility(View.GONE);
        insNColliSpunta.setEnabled(false);
        insQtaSpunta.setVisibility(View.GONE);
        insQtaSpunta.setEnabled(false);
        insUbicSpunta.setVisibility(View.GONE);
        insUbicSpunta.setEnabled(false);
        insSubicSpunta.setVisibility(View.GONE);
        insSubicSpunta.setEnabled(false);
        txtInsEAN.setEnabled(true);
        txtInsEAN.setText("");
        insQtaSpunta.setText("1");
        insUbicSpunta.setText("");
        insSubicSpunta.setText("");
        insNColliSpunta.setText("1");
        txtInsEAN.setFocusableInTouchMode(true);
        txtInsEAN.requestFocus();
    }

    private void chiudiSpunta(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpunta.this, ReviewSpunta.class);
                    review.putExtra("tipo", 0);
                    review.putExtra("fileName", fileName);
                    review.putExtra("docsName", docsName);
                    review.putExtra("idSpuntaDocRoom", idSpuntaDocRoom);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    /**
     * Aggiorna le righe spunta in Room dopo che l'utente ha confermato la quantità.
     * Replica la logica di alertDisplayer2 ma su Room.
     */
    private void aggiornaSpuntaInRoom(String codiceArt, int qtaSpuntata, String ean, String timing,
                                       String ubic, String subic, int nColli, String segnacolloVal) {
        if (idSpuntaDocRoom <= 0) return;
        // Cattura i valori delle View prima di entrare nel Thread
        final String descArt = txtDesc.getText().toString();
        final String storeVal = storeXFile != null ? storeXFile : "";
        new Thread(() -> {
            try {
                SpuntaDao dao = appDb.spuntaDao();
                java.util.List<SpuntaRigaEntity> righe = dao.getRigheByCodArt(idSpuntaDocRoom, codiceArt);
                int qtaRimanente = qtaSpuntata;

                for (SpuntaRigaEntity riga : righe) {
                    if (qtaRimanente == 0) break;
                    if (qtaRimanente > 0) {
                        // Aggiunta normale
                        int spazio = riga.qtaDoc - riga.qtaSpunta;
                        if (spazio <= 0) continue;
                        int daAggiungere = Math.min(spazio, qtaRimanente);
                        dao.incrementaSpunta(riga.id, daAggiungere, ean, timing, ubic, subic);
                        dao.incrementaColli(riga.id, nColli > 0 ? nColli : 0);
                        qtaRimanente -= daAggiungere;
                    } else {
                        // Correzione negativa: sottrai dalle righe già spuntate
                        if (riga.qtaSpunta <= 0) continue;
                        int daTogliere = Math.min(riga.qtaSpunta, -qtaRimanente);
                        dao.incrementaSpunta(riga.id, -daTogliere, ean, timing, ubic, subic);
                        dao.incrementaColli(riga.id, 0);
                        qtaRimanente += daTogliere;
                    }
                }

                // Se rimane quantità positiva (eccedenza rispetto al documento)
                if (qtaRimanente > 0 && !righe.isEmpty()) {
                    SpuntaRigaEntity ultima = righe.get(righe.size() - 1);
                    dao.incrementaSpunta(ultima.id, qtaRimanente, ean, timing, ubic, subic);
                } else if (qtaRimanente > 0 && righe.isEmpty()) {
                    // Articolo non presente nel documento, aggiungi nuova riga
                    SpuntaRigaEntity nuova = new SpuntaRigaEntity();
                    nuova.idSpuntaDoc = idSpuntaDocRoom;
                    nuova.codArt = codiceArt;
                    nuova.desc = descArt;
                    nuova.alias = ean;
                    nuova.ubic = ubic;
                    nuova.subic = subic;
                    nuova.qtaDoc = 0;
                    nuova.qtaSpunta = qtaRimanente;
                    nuova.diff = qtaRimanente;
                    nuova.timeSp = timing;
                    nuova.colli = nColli;
                    nuova.store = storeVal;
                    nuova.segnacollo = segnacolloVal;
                    dao.insertRiga(nuova);
                }

                // Aggiorna ubicazione per tutte le righe dello stesso articolo
                dao.aggiornaUbicPerArticolo(idSpuntaDocRoom, codiceArt, ubic, subic);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Legge la quantità già spuntata per un articolo da Room.
     * Usato in FindArt.onPostExecute come alternativa alla lettura Excel.
     */
    private void leggiStatoArticoloDaRoom(String codiceArt, Runnable onComplete) {
        if (idSpuntaDocRoom <= 0) {
            if (onComplete != null) onComplete.run();
            return;
        }
        new Thread(() -> {
            try {
                SpuntaDao dao = appDb.spuntaDao();
                java.util.List<SpuntaRigaEntity> righe = dao.getRigheByCodArt(idSpuntaDocRoom, codiceArt);
                int totQtaSpuntata = 0;
                int totQtaDoc = 0;
                String primaUbic = "";
                String primaSubic = "";
                boolean trovato = false;

                for (SpuntaRigaEntity riga : righe) {
                    totQtaSpuntata += riga.qtaSpunta;
                    totQtaDoc += riga.qtaDoc;
                    if (!trovato && !riga.ubic.isEmpty()) {
                        primaUbic = riga.ubic;
                        primaSubic = riga.subic;
                        trovato = true;
                    }
                }

                final int fTotSpuntata = totQtaSpuntata;
                final int fTotDoc = totQtaDoc;
                final String fUbic = primaUbic;
                final String fSubic = primaSubic;
                final boolean fPresente = !righe.isEmpty();

                runOnUiThread(() -> {
                    if (fPresente) {
                        txtQtaLP.setText(String.valueOf(fTotSpuntata));
                        qtaDoc.setText(String.valueOf(fTotDoc));
                        if (!fUbic.isEmpty()) {
                            txtUbiRead.setText(fUbic);
                            insUbicSpunta.setText(fUbic);
                        }
                        if (!fSubic.isEmpty()) {
                            txtSubiRead.setText(fSubic);
                            insSubicSpunta.setText(fSubic);
                        }
                    }
                    if (onComplete != null) onComplete.run();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (onComplete != null) onComplete.run();
                });
            }
        }).start();
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        inputPath = inputPath + "/";
        outputPath = outputPath + "/";
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile, false);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    if(idSpuntaDocRoom <= 0){
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);
                    Integer qtaSpuntata;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
                    Date date = new Date();   // given date
                    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                    calendar.setTime(date);   // assigns calendar to given date
                    int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                    Integer minutes = calendar.get(Calendar.MINUTE);
                    String nMinutes;
                    if(minutes.toString().length()==1){
                        nMinutes = "0"+minutes.toString();
                    }else{
                        nMinutes = minutes.toString();
                    }
                    String timing = hours + ":" + nMinutes;
                    Integer lastIndex = -1;
                    int i = 0, z=0;
                    Integer nColli = 0;
                    while(workbook.getSheetAt(0).getRow(i) != null){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                            lastIndex = i;
                            if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))>=qtaSpuntata && qtaSpuntata>0){
                                doc4Print = row.getCell(8).getStringCellValue();
                                nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                                if(chkEtic.isChecked()){
                                    if(!connection.isConnected()){
                                        connection = new BluetoothConnection(bt);
                                        try{
                                            connection.open();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //stampaZebra(nColli.toString());
                                }
                                Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                qtaSpuntata = 0;
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                row.createCell(1).setCellValue(txtDesc.getText().toString());
                                row.createCell(11).setCellValue(timing);
                                row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                                row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                                Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                                row.createCell(12).setCellValue(newTotColli.toString());
                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                                doc4Print = row.getCell(8).getStringCellValue();
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                nColli = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))/Integer.parseInt(insQtaSpunta.getText().toString());
                                if(chkEtic.isChecked()){
                                    if(!connection.isConnected()){
                                        connection = new BluetoothConnection(bt);
                                        try{
                                            connection.open();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //stampaZebra(nColli.toString());
                                }
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                row.createCell(11).setCellValue(timing);
                                row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                                row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                                Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                                row.createCell(12).setCellValue(newTotColli.toString());
                            }
                        }else if(i == 0 && qtaSpuntata == 0){
                            int j = 0;
                            while(workbook.getSheetAt(0).getRow(j) != null){
                                Row row2 = workbook.getSheetAt(0).getRow(i);
                                if(row2.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                                    row2.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                                    row2.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                                }
                                j++;
                            }
                        }
                        i++;
                    }
                        Row row = workbook.getSheetAt(0).getRow(lastIndex);
                        if(qtaSpuntata>0 && lastIndex != -1){
                            doc4Print = row.getCell(8).getStringCellValue();
                            nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                            if(chkEtic.isChecked()){
                                if(!connection.isConnected()){
                                    connection = new BluetoothConnection(bt);
                                    try{
                                        connection.open();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                //stampaZebra(nColli.toString());
                            }
                            row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                            row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                            Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                            row.createCell(6).setCellValue(qtaUltima.toString());
                            row.createCell(11).setCellValue(timing);
                            Integer newTotColli = Integer.parseInt(row.getCell(12).getStringCellValue()) + nColli;
                            row.createCell(12).setCellValue(newTotColli.toString());
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            row = workbook.getSheetAt(0).createRow(i);
                            doc4Print = "";
                            nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                            if(chkEtic.isChecked()){
                                if(!connection.isConnected()){
                                    connection = new BluetoothConnection(bt);
                                    try{
                                        connection.open();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                //stampaZebra(nColli.toString());
                            }
                            row.createCell(3).setCellValue(insUbicSpunta.getText().toString());
                            row.createCell(4).setCellValue(insSubicSpunta.getText().toString());
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                            row.createCell(0).setCellValue(txtCodArt.getText().toString());
                            row.createCell(6).setCellValue(qtaSpuntata.toString());
                            row.createCell(5).setCellValue("0");
                            row.createCell(8).setCellValue("");
                            row.createCell(9).setCellValue("");
                            row.createCell(11).setCellValue(timing);
                            row.createCell(12).setCellValue(nColli.toString());
                            row.createCell(14).setCellValue("");
                        }

                        UpdateThings updateThings = new UpdateThings();
                        updateThings.execute();

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String outFileName = fileName;
                    String path = "/storage/emulated/0/NAS/SpuntaGen";
                    copyFile(path, outFileName, "/storage/emulated/0/Backup");
                    }
                    if(chkEtic.isChecked()){
                        if(!connection.isConnected()){
                            connection = new BluetoothConnection(bt);
                            try{
                                connection.open();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //stampaZebra("");
                    }
                    totColli += Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    printEtc.setVisibility(View.GONE);
                    clearPage.setVisibility(View.GONE);
                    btnChgUbic.setVisibility(View.GONE);
                    btnFine.setVisibility(View.VISIBLE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    lblUbic.setVisibility(View.GONE);
                    lblSubic.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    insUbicSpunta.setVisibility(View.GONE);
                    insUbicSpunta.setEnabled(false);
                    insSubicSpunta.setVisibility(View.GONE);
                    insSubicSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSpunta.setText("1");
                    insUbicSpunta.setText("");
                    insSubicSpunta.setText("");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtUbiDef.setText("");
                    txtSubiDef.setText("");
                    txtSubiRead.setText("");
                    txtUbiRead.setText("");
                    txtSubiDef.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpunta.this, ReviewSpunta.class);
                    review.putExtra("tipo", 0);
                    review.putExtra("docsName", docsName);
                    review.putExtra("fileName", fileName);
                    review.putExtra("idSpuntaDocRoom", idSpuntaDocRoom);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void inserisciNota(String codxNota){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setMessage("Inserisci una nota per l'articolo");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText not = new EditText(this);
        not.setHint("Note");
        layout.addView(not);

        builder.setView(layout);

        if(idSpuntaDocRoom > 0){
            // Percorso Room: pre-riempie la nota esistente
            try {
                String notaEsistente = appDb.spuntaDao().getNotePerArticolo(idSpuntaDocRoom, codxNota);
                if(notaEsistente != null && !notaEsistente.isEmpty()){
                    not.setText(notaEsistente);
                }
            } catch(Exception e){ e.printStackTrace(); }
            builder.setNegativeButton("No", (dialog, which) -> alertDisplayer2());
            builder.setPositiveButton("Si", (dialog, which) -> {
                try {
                    appDb.spuntaDao().aggiornaNotePerArticolo(idSpuntaDocRoom, codxNota, not.getText().toString());
                } catch(Exception e){ e.printStackTrace(); }
                alertDisplayer2();
            });
        } else {
        XSSFWorkbook workbook;
        try {
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

        ArrayList<Integer> indexes = new ArrayList<>();
        int i = 0;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(codxNota)){
                    if (row.getCell(0).getStringCellValue().equals("")) {
                        indexes.add(i);
                    }else{
                        not.setText(row.getCell(9).getStringCellValue());
                    }
            }
                i++;
        }

        builder.setNegativeButton("No", (dialog, which) -> {
            alertDisplayer2();
        });
            int finalI = i;
            builder.setPositiveButton("Si", (dialog, which) -> {
            int j;
            for(j = 0; j<indexes.size(); j++){
                Row row = workbook.getSheetAt(0).getRow(indexes.get(j));
                row.createCell(9).setCellValue(not.getText().toString());
            }
            if(j==0){
                Row row = workbook.getSheetAt(0).getRow(finalI);
                row.createCell(9).setCellValue(not.getText().toString());
            }
            alertDisplayer2();
        });
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String outFileName = fileName;
        String path = "/storage/emulated/0/NAS/SpuntaGen";
        copyFile(path, outFileName, "/storage/emulated/0/Backup");
        }
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    txtCodArt.setText("");
                    txtDesc.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    txtInsEAN.setEnabled(false);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setEnabled(true);
                    lblColli.setVisibility(View.VISIBLE);
                    insNColliSpunta.setFocusableInTouchMode(true);
                    insNColliSpunta.requestFocus();
                    showSoftKeyboard(insNColliSpunta);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertStampaEtichette(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText colli = new EditText(context);
        colli.setHint("Quantità");
        layout.addView(colli);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String desc1, desc2;
            int qtaSel = Integer.parseInt(colli.getText().toString());
            if(txtDesc.getText().toString().length() > 28){
                desc1 = txtDesc.getText().toString().substring(0,28);
                desc2 = txtDesc.getText().toString().substring(28);
            }else{
                desc1 = txtDesc.getText().toString();
                desc2 = "";
            }
            String cpclData;
            if(spinner.getSelectedItem().toString().equals("Estesa")){
                cpclData = "! 5 0 0 540 1" +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" +
                        "TEXT 4 1 16 10 " + txtCodArt.getText().toString() +
                        "\n" + "\n" +
                        "TEXT 4 0 16 110 " + desc1 +
                        "\n" + "\n" +
                        "TEXT 4 0 16 160 " + desc2 +
                        "\n" + "\n" +
                        "B 128 1 0 60 24 245 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "\n" + "\n" +
                        "TEXT 5 0 64 315 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "PRINT" + "\n" + "\n";
            }else{
                cpclData = "!LABEL " +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" +
                        "! 0 0 0 275 1" + "CENTER" +
                        "\n" +
                        "TEXT 4 0 0 5 " + txtCodArt.getText().toString() +
                        "\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "TEXT 7 0 5 60 " + desc1 +
                        "\n" + "\n" +
                        "TEXT 7 0 5 85 " + desc2 +
                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "B 128 1 0 50 5 135 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "TEXT 5 0 5 190 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "PRINT" + "\n" + "\n";
            }
            for(int i=0; i<qtaSel; i++){
                try {
                    connection.write(cpclData.getBytes());
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message);

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText note = new EditText(context);
                note.setHint("Note");
                layout.addView(note);

                builder.setView(layout);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    if(note.getText().toString().equals("")){
                        note.setHintTextColor(Color.RED);
                        alertArt("Error!", "Wait bruh, what are u doing? Put something in textbox pls");
                    }else{
                        dialog.cancel();
                        btnBackArt.setVisibility(View.VISIBLE);
                        btnNextArt.setVisibility(View.VISIBLE);
                        printEtc.setVisibility(View.VISIBLE);
                        clearPage.setVisibility(View.VISIBLE);
                        btnChgUbic.setVisibility(View.VISIBLE);
                        btnFine.setVisibility(View.GONE);
                        txtCodArt.setText(txtInsEAN.getText().toString());
                        txtEsSp.setText("0");
                        txtPrzArt.setText("€ " + "N/A");
                        txtOrdF.setText("0");
                        txtOrdC.setText("0");
                        txtQtaLP.setText("0");
                        txtUbiDef.setText("N/A");
                        txtUbiRead.setText("N/A");
                        txtSubiDef.setText("N/A");
                        txtSubiRead.setText("N/A");
                        txtDesc.setText(note.getText().toString());
                        txtInsEAN.setEnabled(false);
                        insNColliSpunta.setVisibility(View.VISIBLE);
                        insNColliSpunta.setEnabled(true);
                        lblColli.setVisibility(View.VISIBLE);
                    }
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void nienteInRicerca(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void stampaOX(int qta, String serieOX, String consegnaOX, String numOX){
        try {

            String codArtP;
            String descP;
            if(txtCodArt.getText().toString().length()>45){
                codArtP = txtCodArt.getText().toString().substring(0,45);
            }else{
                codArtP = txtCodArt.getText().toString();
            }if(txtDesc.getText().toString().length()>45){
                descP = txtDesc.getText().toString().substring(0,45);
            }else{
                descP = txtDesc.getText().toString();
            }

            consegnaOX = consegnaOX.trim();
            String newData = consegnaOX.substring(8) + "/" + consegnaOX.substring(5,7) + "/" + consegnaOX.substring(0,4);

            String cpclData = "! 5 0 0 540 1" +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" +
                        "SETMAG 2 2" +
                        "\n" + "\n" +
                        "TEXT 4 1 16 0 OX" +
                        "\n" + "\n" +
                        "SETMAG 2 2" +
                        "\n" + "\n" +
                        "TEXT 4 1 240 0 " + serieOX +
                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "SETMAG 2 2" +
                        "\n" + "\n" +
                        "TEXT 4 1 365 0 " + newData +
                        "\n" + "\n" +
                        "SETMAG 3 4" +
                        "\n" + "\n" +
                        "TEXT 4 1 0 90 " + numOX +
                        "\n" + "\n" + "LEFT" + "\n" + "\n" +
                        "SETMAG 0 3" +
                        "\n" + "\n" +
                        "TEXT 0 3 20 280 " + codArtP +
                        "\n" + "\n" +
                        "SETMAG 0 3" +
                        "\n" + "\n" +
                        "TEXT 0 3 20 320 " + descP +
                        "\n" + "\n" +
                        "B 128 1 0 50 20 340 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" + "PRINT" + "\n" + "\n";


            for(int i=0; i<qta; i++){
                connection.write(cpclData.getBytes());
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void stampaZebra(String colli){
        try {

            String desc1, desc2, forn1, forn2;
            if(txtDesc.getText().toString().length() > 28){
                desc1 = txtDesc.getText().toString().substring(0,28);
                desc2 = txtDesc.getText().toString().substring(28);
            }else{
                desc1 = txtDesc.getText().toString();
                desc2 = "";
            }
            if(fornitore.length() > 30){
                forn1 = fornitore.substring(0,30);
                forn2 = fornitore.substring(30);
            }else{
                forn1 = fornitore;
                forn2 = "";
            }

            Date date = Calendar.getInstance().getTime();

            // Display a date in day, month, year format
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            String cpclData;
            if(spinner.getSelectedItem().toString().equals("Estesa")){
                cpclData = "! 5 0 0 540 1" +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" +
                        "TEXT 4 1 16 10 " + txtCodArt.getText().toString() +
                        "\n" + "\n" +
                        "TEXT 4 0 16 110 " + desc1 +
                        "\n" + "\n" +
                        "TEXT 4 0 16 160 " + desc2 +
                        "\n" + "\n" +
                        "TEXT 5 0 300 225 " + forn1 +
                        "\n" + "\n" +
                        "TEXT 5 0 300 255 " + forn2 +
                        "\n" + "\n" +
                        "TEXT 5 0 300 295 DDT: " + doc4Print +
                        "\n" + "\n" +
                        "TEXT 5 0 570 295 Colli: " + colli +
                        "\n" + "\n" +
                        "TEXT 5 0 570 335 Imb.: " + insQtaSpunta.getText().toString() +
                        "\n" + "\n" +
                        "TEXT 5 0 300 335 Data: " + today +
                        "\n" + "\n" + "\n" + "\n" +
                        "B 128 1 0 60 24 245 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "\n" + "\n" +
                        "TEXT 5 0 64 315 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "PRINT" + "\n" + "\n";
            }else{
                cpclData = "!LABEL " +
                        "\n" + "\n" +
                        "SETMAG 0 0" +
                        "\n" + "\n" +
                        "! 0 0 0 275 1" + "CENTER" +
                        "\n" +
                        "TEXT 4 0 0 5 " + txtCodArt.getText().toString() +
                        "\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "TEXT 7 0 5 60 " + desc1 +
                        "\n" + "\n" +
                        "TEXT 7 0 5 85 " + desc2 +
                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "B 128 1 0 50 5 135 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "CENTER" + "\n" + "\n" +
                        "TEXT 5 0 5 190 " + txtInsEAN.getText().toString() +
                        "\n" + "\n" + "PRINT" + "\n" + "\n";
            }

            for(int i=0; i<Integer.parseInt(colli); i++){
                connection.write(cpclData.getBytes());
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    /**
     * Carica in una singola query SQL i dati di tutti gli articoli del documento
     * e li salva in SpuntaArticoloCacheEntity. FindArt leggerà da lì senza SQL.
     */
    public class PreloadArticoli extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pbSearchArt.setVisibility(View.VISIBLE);
            txtInsEAN.setEnabled(false);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                SpuntaDao dao = appDb.spuntaDao();

                // Raccoglie i codici articolo distinti dal documento
                List<SpuntaRigaEntity> righe = dao.getRigheByDocumento(idSpuntaDocRoom);
                if (righe.isEmpty()) return "";

                java.util.LinkedHashSet<String> codSet = new java.util.LinkedHashSet<>();
                for (SpuntaRigaEntity r : righe) {
                    if (!r.codArt.isEmpty()) codSet.add(r.codArt);
                }
                if (codSet.isEmpty()) return "";

                // Costruisce la IN clause con escape degli apostrofi
                StringBuilder inClause = new StringBuilder("(");
                for (String c : codSet) {
                    inClause.append("'").append(c.replace("'", "''")).append("',");
                }
                inClause.deleteCharAt(inClause.length() - 1).append(")");

                String query =
                        "SELECT articolo.nome, articolo.descrizione, " +
                        "(SELECT CAST(ProgressivoArticolo.esistenza AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag + ") AS esistenza, " +
                        "(SELECT CAST(ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag + ") AS ordFornitore, " +
                        "(SELECT CAST(ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag + ") AS ordCliente, " +
                        "(SELECT prezzo FROM articoloxlistino WHERE idArticolo = articolo.id AND idListino = " + listino + ") AS prezzo, " +
                        "(SELECT Ubicazione FROM articoloxmagazzino WHERE idArticolo = articolo.id AND idmagazzino = " + mag + ") AS ubicazione, " +
                        "(SELECT sottoUbicazione FROM articoloxmagazzino WHERE idArticolo = articolo.id AND idmagazzino = " + mag + ") AS sottoubicazione " +
                        "FROM articolo WHERE articolo.nome IN " + inClause;

                Connection con = connectionClass.CONN(context);
                if (con == null) return "Errore connessione";

                List<SpuntaArticoloCacheEntity> cacheList = new ArrayList<>();
                try {
                    Statement stmt = con.createStatement();
                    ResultSet res = stmt.executeQuery(query);
                    while (res.next()) {
                        SpuntaArticoloCacheEntity c = new SpuntaArticoloCacheEntity();
                        c.idSpuntaDoc = idSpuntaDocRoom;
                        c.codArt = res.getString("nome");
                        c.desc   = res.getString("descrizione");
                        String esStr  = res.getString("esistenza");
                        c.esistenza   = esStr  != null ? (int) Double.parseDouble(esStr)  : 0;
                        String ofStr  = res.getString("ordFornitore");
                        c.ordFornitore = ofStr != null ? (int) Double.parseDouble(ofStr)  : 0;
                        String ocStr  = res.getString("ordCliente");
                        c.ordCliente  = ocStr  != null ? (int) Double.parseDouble(ocStr)  : 0;
                        String przStr = res.getString("prezzo");
                        c.prz         = przStr != null ? Double.parseDouble(przStr)       : 0.0;
                        String ubicStr  = res.getString("ubicazione");
                        c.ubic          = ubicStr  != null ? ubicStr  : "";
                        String subicStr = res.getString("sottoubicazione");
                        c.subic         = subicStr != null ? subicStr : "";
                        cacheList.add(c);
                    }
                    res.close();
                    stmt.close();
                } finally {
                    try { con.close(); } catch (Exception ignored) {}
                }

                // Salva in Room (rimpiazza cache precedente per questo documento)
                dao.deleteCacheByDocumento(idSpuntaDocRoom);
                if (!cacheList.isEmpty()) {
                    dao.insertCacheArticoli(cacheList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String r) {
            pbSearchArt.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            txtInsEAN.setEnabled(true);
            txtInsEAN.setFocusableInTouchMode(true);
            txtInsEAN.requestFocus();
        }
    }

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String codiceArt = null;
        String description = null;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbSearchArt.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {

            pbSearchArt.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(isSuccess) {

                btnBackArt.setVisibility(View.VISIBLE);
                btnNextArt.setVisibility(View.VISIBLE);
                printEtc.setVisibility(View.VISIBLE);
                clearPage.setVisibility(View.VISIBLE);
                btnChgUbic.setVisibility(View.VISIBLE);
                btnFine.setVisibility(View.GONE);
                txtCodArt.setText(codiceArt);
                txtDesc.setText(description);
                if(of!=null){
                    txtOrdF.setText(of.toString());
                }else{
                    txtOrdF.setText("0");
                }
                if(oc!=null){
                    txtOrdC.setText(oc.toString());
                }else{
                    txtOrdC.setText("0");
                }
                if(esistenza!=null){
                    txtEsSp.setText(esistenza.toString());
                }else{
                    txtEsSp.setText("0");
                }
                if(ubiDef!=null){
                    txtUbiDef.setText(ubiDef);
                }else{
                    txtUbiDef.setText("N/A");
                }
                if(prz!=null){
                    txtPrzArt.setText("€ " + prz.toString());
                }else{
                    txtPrzArt.setText("€ N/A");
                }
                if(subiDef==null){
                    txtSubiDef.setText("N/A");
                }else{
                    txtSubiDef.setText(subiDef);
                }

                if(idSpuntaDocRoom > 0) {
                    // Usa Room per leggere lo stato dell'articolo
                    final String artCode = codiceArt;
                    leggiStatoArticoloDaRoom(artCode, () -> {
                        // Controlla se l'articolo è presente nel documento Room
                        new Thread(() -> {
                            java.util.List<SpuntaRigaEntity> righe = appDb.spuntaDao().getRigheByCodArt(idSpuntaDocRoom, artCode);
                            boolean pres = !righe.isEmpty();
                            runOnUiThread(() -> {
                                if(!txtUbiDef.getText().toString().equals("N/A") && insUbicSpunta.getText().toString().equals("")){
                                    insUbicSpunta.setText(ubiDef);
                                }
                                if(!txtSubiDef.getText().toString().equals("N/A") && insSubicSpunta.getText().toString().equals("")){
                                    insSubicSpunta.setText(subiDef);
                                }
                                if(pres){
                                    txtInsEAN.setEnabled(false);
                                    insNColliSpunta.setVisibility(View.VISIBLE);
                                    insNColliSpunta.setEnabled(true);
                                    lblColli.setVisibility(View.VISIBLE);
                                    insNColliSpunta.setFocusableInTouchMode(true);
                                    insNColliSpunta.requestFocus();
                                    showSoftKeyboard(insNColliSpunta);
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    play.play();
                                }else{
                                    articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                                }
                            });
                        }).start();
                    });
                } else {
                    // Fallback: usa Excel
                    Boolean presente = false;
                    if(idSpuntaDocRoom <= 0){
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName;
                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");
                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        Integer totQta = 0;
                        int i = 0;
                        String qtaLP = "0";
                        String fRU = "";
                        String fRS = "";
                        while(workbook.getSheetAt(0).getRow(i) != null){
                            Row row = workbook.getSheetAt(0).getRow(i);
                            if(row.getCell(0).getStringCellValue().equals(codiceArt)){
                                Integer newQta = Integer.parseInt(qtaLP) + Integer.parseInt(row.getCell(6).getStringCellValue());
                                totQta = totQta + Integer.parseInt(row.getCell(5).getStringCellValue());
                                qtaLP = newQta.toString();
                                if(fRU.equals("")){
                                    txtUbiRead.setText(row.getCell(3).getStringCellValue());
                                    insUbicSpunta.setText(row.getCell(3).getStringCellValue());
                                    fRU = row.getCell(3).getStringCellValue();
                                }else{
                                    txtUbiRead.setText(fRU);
                                    insUbicSpunta.setText(fRU);
                                }
                                if(fRS.equals("")){
                                    txtSubiRead.setText(row.getCell(4).getStringCellValue());
                                    insSubicSpunta.setText(row.getCell(4).getStringCellValue());
                                    fRS = row.getCell(4).getStringCellValue();
                                }else{
                                    txtSubiRead.setText(fRS);
                                    insSubicSpunta.setText(fRS);
                                }
                                if(!txtUbiDef.getText().toString().equals("N/A") && insUbicSpunta.getText().toString().equals("")){
                                    insUbicSpunta.setText(ubiDef);
                                }
                                if(!txtSubiDef.getText().toString().equals("N/A") && insSubicSpunta.getText().toString().equals("")){
                                    insSubicSpunta.setText(subiDef);
                                }
                                txtQtaLP.setText(qtaLP);
                                presente = true;
                            }
                            i++;
                        }
                        qtaDoc.setText(totQta.toString());
                        file.close();
                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String outFileName = fileName;
                    String path = "/storage/emulated/0/NAS/SpuntaGen";
                    copyFile(path, outFileName, "/storage/emulated/0/Backup");
                    }
                    if(presente){
                        txtInsEAN.setEnabled(false);
                        insNColliSpunta.setVisibility(View.VISIBLE);
                        insNColliSpunta.setEnabled(true);
                        lblColli.setVisibility(View.VISIBLE);
                        insNColliSpunta.setFocusableInTouchMode(true);
                        insNColliSpunta.requestFocus();
                        showSoftKeyboard(insNColliSpunta);
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        play.play();
                    }else{
                        articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                    }
                }

            }else{
                alertArt("Errore!","Articolo non presente nel database, inserisci una nota");
            }
            if(giaPremuto == 1){
                giaPremuto = 0;
            }
        }

        @Override
        protected String doInBackground(String... params) {

            if (idSpuntaDocRoom > 0) {
                // ── Percorso Room: nessuna connessione SQL ───────────────────────
                try {
                    String searchTerm = (findThis != null) ? findThis.trim() : "";
                    if (searchTerm.isEmpty()) return z;

                    SpuntaDao dao = appDb.spuntaDao();
                    ArticoloDao artDao = appDb.articoloDao();

                    // 1. Cerca nella cache per codArt diretto
                    SpuntaArticoloCacheEntity cached = dao.getCacheArticolo(idSpuntaDocRoom, searchTerm);

                    // 2. Non trovato per codArt: risolve EAN → codArt e riprova in cache
                    if (cached == null) {
                        ArticoloDao.ArticoloMini mini = artDao.findByEanMini(searchTerm);
                        if (mini != null) {
                            cached = dao.getCacheArticolo(idSpuntaDocRoom, mini.codArt);
                        }
                    }

                    if (cached != null) {
                        // Trovato in cache: tutti i dati pre-caricati
                        codiceArt  = cached.codArt;
                        description = cached.desc;
                        esistenza  = cached.esistenza;
                        of         = cached.ordFornitore;
                        oc         = cached.ordCliente;
                        prz        = cached.prz;
                        ubiDef     = cached.ubic.isEmpty()  ? null : cached.ubic;
                        subiDef    = cached.subic.isEmpty() ? null : cached.subic;
                        isSuccess  = true;
                    } else {
                        // 3. Articolo non nel documento: fallback su ArticoloEntity
                        ArticoloDao.ArticoloMini mini = artDao.findByCodArtMini(searchTerm);
                        if (mini == null) {
                            mini = artDao.findByEanMini(searchTerm);
                        }
                        if (mini != null) {
                            codiceArt   = mini.codArt;
                            description = mini.desc != null ? mini.desc : "";
                            esistenza   = mini.es   != null ? mini.es  : 0;
                            of          = 0;
                            oc          = 0;
                            prz         = 0.0;
                            ubiDef      = null;
                            subiDef     = null;
                            isSuccess   = true;
                        }
                        // mini == null → isSuccess rimane false → alertArt
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Errore";
                }
                return z;
            }

            // ── Percorso SQL (solo per idSpuntaDocRoom <= 0) ────────────────────
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select nome, articolo.descrizione, \n" +
                            "(select cast (ProgressivoArticolo.esistenza as int) as esistenza from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino =  "+mag+" ) as esistenza, \n" +
                            "(select cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = "+mag+" ) as OrdinatoFornitoreArticoloXMagazzino,\n" +
                            "(select cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a > GETDATE() and MetaMagazzino = "+mag+" ) as OrdinatoClienteArticoloXMagazzino, \n" +
                            "(select prezzo from articoloxlistino where idArticolo = articolo.id and idListino = "+listino+" ) as prezzo, \n" +
                            "(select sottoUbicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=  "+mag+" ) as sottoubicazione, \n" +
                            "(select Ubicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=  "+mag+" ) as ubicazione, \n" +
                            "(select idListino from articoloxlistino where idArticolo = articolo.id and idListino = "+listino+" ) as idListino\n" +
                            "from articolo left join [alias] on ([alias].idArticolo = articolo.id)\n" +
                            "where nome = '"+findThis+"' or [alias].codice = '"+findThis+"' " +
                            "or nome = '"+findThis.trim()+"' or [alias].codice = '"+findThis.trim()+"' " +
                            "order by ubicazione desc";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int j = 0;
                    while(res.next()) {
                        if(j==0){
                            codiceArt = res.getString("nome");
                            description = res.getString("descrizione");
                        }
                        if(res.getInt("idListino") == listino){
                            j++;
                            prz = res.getDouble("prezzo");
                            esistenza = res.getInt("esistenza");
                            of = res.getInt("OrdinatoFornitoreArticoloXMagazzino");
                            oc = res.getInt("OrdinatoClienteArticoloXMagazzino");
                            ubiDef = res.getString("ubicazione");
                            subiDef = res.getString("sottoUbicazione");
                        }
                    }
                    if(j==0){
                        prz = 0.00;
                        esistenza = 0;
                        of = 0;
                        oc = 0;
                        ubiDef = "N/A";
                        subiDef = "N/A";
                    }
                    if (codiceArt != null) {
                        isSuccess = true;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if (con != null) {
                try { con.close(); } catch (SQLException ex) {}
            }
            return z;
        }

    }

    public class ChangeUbic extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;
        String ubicAtt = "";
        String subicAtt = "";
        String esAtt = "";
        final String codArtVal;

        ChangeUbic(String codArt) {
            this.codArtVal = codArt;
        }

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                alertModUbic(codArtVal,"Esistenza: " + esAtt);
            }
        }

        private void alertModUbic(String title,String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Annulla",(dialog, which) -> {
                        dialog.cancel();
                    });

            LinearLayout layout = new LinearLayout(IniziaSpunta.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText ubic1 = new EditText(IniziaSpunta.this);
            ubic1.setText(ubicAtt);
            ubic1.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(ubic1);

            builder.setView(layout);

            final EditText ubic2 = new EditText(IniziaSpunta.this);
            ubic2.setText(subicAtt);
            ubic2.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(ubic2);

            builder.setView(layout);

            builder.setPositiveButton("Salva", (dialog, which) -> {
                dialog.cancel();
                if(!ubic1.getText().toString().equals(ubicAtt) || !ubic2.getText().toString().equals(subicAtt)){
                    cambia(ubic1.getText().toString(), ubic2.getText().toString());
                }
            });
            AlertDialog ok = builder.create();
            ok.show();
        }

        protected void cambia(String newUbic, String newSubic) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "update articoloxmagazzino " +
                            "set ubicazione = '"+newUbic+"', sottoubicazione = '"+newSubic+"' " +
                            "from articoloxmagazzino join articolo on (articoloxmagazzino.idarticolo = articolo.id) " +
                            "where nome = '"+ codArtVal +"' and articoloxmagazzino.idmagazzino = "+mag+" ";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {

                }
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
                    String query = "select cast(esistenza as int) as esistenza, " +
                            "(select ubicazione from articoloxmagazzino where idarticolo=articolo.id and idmagazzino = metamagazzino) as ubic, " +
                            "(select sottoubicazione from articoloxmagazzino where idarticolo=articolo.id and idmagazzino = metamagazzino) as subic " +
                            "from progressivoarticolo join articolo on (progressivoarticolo.metaarticolo = articolo.id) " +
                            "where nome = '"+ codArtVal +"' and da<GETDATE() and a > GETDATE() and metamagazzino = "+mag+" ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        isSuccess = true;
                        esAtt = res.getString("esistenza");
                        if(res.getString("ubic")!=null){
                            ubicAtt = res.getString("ubic");
                        }if(res.getString("subic")!=null){
                            subicAtt = res.getString("subic");
                        }
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {

                }
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
                    String query = "INSERT INTO mcSpunte (codArt, descrizione, alias, ubic, subic, qtaDoc, qtaS, diff, nDoc, sparata, palmare, utente)" +
                            "VALUES "+insert+" ";
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
                            "FROM mcSpunte a " +
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

    public class AggDoc extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        Integer qta;

        int tipoQ;

        String ean, timing, codArt, nDoc, ubic, subic;

        public AggDoc(Integer qta, String ean, String timing, String codArt, String nDoc, String ubic, String subic, int tipoQ){
            this.qta = qta;
            this.ean = ean;
            this.timing = timing;
            this.codArt = codArt;
            this.nDoc = nDoc;
            this.ubic = ubic;
            this.subic = subic;
            this.tipoQ = tipoQ;
        }

        public AggDoc(String codArt, String ubic, String subic, int tipoQ){
            this.codArt = codArt;
            this.ubic = ubic;
            this.subic = subic;
            this.tipoQ = tipoQ;
        }
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
                    String query = "";
                    if(tipoQ==0){
                        query = "UPDATE mcSpunte " +
                                "SET qtaS = "+qta+", alias = '"+ean+"', sparata = '"+timing+"', diff = "+qta+"-qtaDoc, ubic = '"+ubic+"', subic = '"+subic+"' " +
                                "WHERE codArt = '"+codArt+"' and nDoc = '"+nDoc+"' and palmare = '"+nomeP+"' and utente = '"+utente+"' ";
                    }else{
                        query = "UPDATE mcSpunte " +
                                "SET ubic = '"+ubic+"', subic = '"+subic+"' " +
                                "WHERE codArt = '"+codArt+"' and palmare = '"+nomeP+"' and utente = '"+utente+"' ";
                    }

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

    public class updDocInArr extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPostExecute(String r) {
            updInizioSpunta();
        }

        @Override
        protected String doInBackground(String... params) {
            // PF e RF non vanno registrate in mcDocInArrivo
            if ("PF".equals(tipoDoc) || "RF".equals(tipoDoc)) return z;
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    // INSERT diretto: il LEFT JOIN + WHERE mcDocInArrivo.id IS NULL gestisce già i duplicati riga per riga
                    String query = "INSERT INTO mcDocInArrivo (idDoc, numDoc, dataDoc, magPartenza, magDestinazione, tipoDoc, esercizio, importo, fornitore, serieDoc) \n" +
                            "select documento.id, documento.numero, documento.data, idMagazzinoPartenza, idMagazzinoDestinazione, SUBSTRING(identificatore,0,3), year(data), documento.totaleDocumento, anagrafica.ragionesociale, documento.serie \n" +
                            "from Documento join RigaDocumento on documento.id = RigaDocumento.idMaster and SUBSTRING(documento.selettoreDocumento,5,20) like SUBSTRING(rigadocumento.selettore,9,20) \n" +
                            "join fornitore on fornitore.id = documento.idFornitore \n" +
                            "join anagrafica on anagrafica.id = fornitore.idAnagrafica \n" +
                            "left join mcDocInArrivo on mcDocInArrivo.idDoc = documento.id and SUBSTRING(identificatore,0,3) = mcDocInArrivo.tipoDoc and documento.numero = mcDocInArrivo.numDoc \n" +
                            "where idMagazzinoDestinazione = "+mag+" and documento.id in "+in+" and SUBSTRING(identificatore,0,3) in ('"+tipoDoc+"') and documento.data >= '2025-01-01T00:00:00.000' and mcDocInArrivo.id is null \n" +
                            "group by documento.id, documento.numero, documento.data, idMagazzinoPartenza, idMagazzinoDestinazione, SUBSTRING(identificatore,0,3), documento.totaleDocumento, anagrafica.ragionesociale, documento.serie;";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            }catch (Exception ex) {
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

        protected void updInizioSpunta(String... params) {
            // PF e RF non vanno registrate in mcDocInArrivo
            if ("PF".equals(tipoDoc) || "RF".equals(tipoDoc)) return;
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "update mcDocInArrivo \n" +
                            "set inizioSpunta = GETDATE() \n" +
                            "where idDoc in "+in+" and tipoDoc = '"+tipoDoc+"' ";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            }catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
        }
    }

    public class cercaOXetichette extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String artOX = "";
        ArrayList<String> numOX, dataC, stato, qtaOX, serieOX, clienteOX, idRigaOX, founded;
        ArrayList<String> numOXNeg, statoNeg, qtaOXNeg, serieOXNeg, clienteOXNeg, idDocOX, idROXSede, idDOXSede;
        ArrayList<Boolean> already;
        ArrayList<EditText> infoOX;
        int j;
        int totChecked = 0;

        @Override
        protected void onPreExecute() {
            artOX = txtCodArt.getText().toString();
            numOX = new ArrayList<>();
            dataC = new ArrayList<>();
            stato = new ArrayList<>();
            idROXSede = new ArrayList<>();
            idDOXSede = new ArrayList<>();
            qtaOX = new ArrayList<>();
            serieOX = new ArrayList<>();
            clienteOX = new ArrayList<>();
            idRigaOX = new ArrayList<>();
            idDocOX = new ArrayList<>();
            founded = new ArrayList<>();
            already = new ArrayList<>();
            numOXNeg = new ArrayList<>();
            statoNeg = new ArrayList<>();
            qtaOXNeg = new ArrayList<>();
            serieOXNeg = new ArrayList<>();
            clienteOXNeg = new ArrayList<>();
        }

        private void qtaSuperata(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpunta.this)
                    .setTitle(title)
                    .setMessage(message);

            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.cancel();
            });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        private void infoOX(String title,ArrayList<EditText> message){
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                    .setTitle(title);

            ScrollView scrollView = new ScrollView(context);
            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            scrollView.addView(layout);

            ArrayList<CheckBox> checkBoxes = new ArrayList<>();

            for(int i=0; i<message.size(); i++){
                CheckBox checkBox = new CheckBox(context);
                checkBoxes.add(checkBox);
                layout.addView(checkBox);
                layout.addView(message.get(i));
            }

            for(j=0;j<checkBoxes.size();j++){
                int qtaOrd = Integer.parseInt(qtaOX.get(j));
                CheckBox thisChkB = checkBoxes.get(j);
                checkBoxes.get(j).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int qtaS = Integer.parseInt(insQtaSpunta.getText().toString());
                        int qtaC = Integer.parseInt(insNColliSpunta.getText().toString());
                        int totS = qtaS*qtaC;
                        int qtaTemp = totChecked + qtaOrd;
                        if(isChecked){
                            if(qtaTemp>totS){
                                qtaSuperata("Errore!", "Hai selezionato un numero di etichette da stampare superiore alla quantità spuntata");
                                thisChkB.setChecked(false);
                            }else{
                                totChecked = totChecked + qtaOrd;
                            }
                        }else{
                            totChecked = totChecked - qtaOrd;
                        }
                    }
                });
            }

            builder.setView(scrollView);

            builder.setPositiveButton("Stampa", (dialog, which) -> {

                for(int i=0; i<checkBoxes.size(); i++){
                    if(checkBoxes.get(i).isChecked()){
                        int qtaOrd = Integer.parseInt(qtaOX.get(i));
                        stampaOX(qtaOrd,serieOX.get(i),dataC.get(i),numOX.get(i));
                        if(idSpuntaDocRoom <= 0){
                        XSSFWorkbook workbook;
                        try {
                            String outFileName = fileName;

                            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                            FileInputStream file = new FileInputStream(new File(path, outFileName));
                            workbook = new XSSFWorkbook(file);
                            int z = 0;
                            while(workbook.getSheet("Info OX").getRow(z) != null){
                                z++;
                            }
                            String consegnaOX = dataC.get(i).trim();
                            String newData = consegnaOX.substring(8) + "/" + consegnaOX.substring(5,7) + "/" + consegnaOX.substring(0,4);
                            Row row = workbook.getSheet("Info OX").createRow(z);
                            row.createCell(0).setCellValue(txtCodArt.getText().toString());
                            row.createCell(1).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(2).setCellValue(qtaOX.get(i));
                            row.createCell(3).setCellValue(serieOX.get(i));
                            row.createCell(4).setCellValue(numOX.get(i));
                            row.createCell(5).setCellValue(newData);
                            row.createCell(6).setCellValue(clienteOX.get(i));
                            if(founded.get(i).equals("N")){
                                row.createCell(7).setCellValue("NO SYNC");
                            }

                            file.close();

                            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                            workbook.write(outFile);
                            outFile.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        }
                        if(founded.get(i).equals("Y")){
                            updState(idRigaOX.get(i),idDocOX.get(i));
                            updStateSede(idROXSede.get(i),idDOXSede.get(i));
                        }
                    }
                }

                dialog.cancel();
            });
            AlertDialog ok = builder.create();
            ok.show();
        }

        public String decodificaSerie(String serie){
            switch (serie) {
                case "1": return "MASTER";
                case "20": return "SESTU";
                case "5": return "MARCONI";
                case "3":return "PIRRI";
                case "4": return "OLBIA";
                case "9": return "SASSARI";
                case "6": return "NUORO";
                case "7": return "CARBONIA";
                case "8": return "TORTOLI";
                case "10": return "ORISTANO";
                case "33": return "TIBURTINA";
                case "31": return "CAPENA";
                case "32": return "OSTIENSE";
                case "34": return "MasterMagRoma";
                case "35": return "CASILINA";
                case "36": return "POMEZIA";
                case "37": return "ROMACEDI";
                case "38": return "ARDEATINA";
                case "50": return "VERONA";
                default: return "";
            }
        }

        protected void findOXNeg() {
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
                    String query = "select articolo.nome as codiceArticolo, RigaDocumentoCommerciale.quantita, " +
                            "RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, " +
                            "RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie, ragioneSociale, " +
                            "RigaDocumentoCommerciale.id as idRiga, DocumentoCommerciale.id as idDoc " +
                            "from RigaDocumentoCommerciale " +
                            "join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "join Cliente on (Cliente.id=DocumentoCommerciale.idCliente) " +
                            "join Anagrafica on (Anagrafica.id=Cliente.idAnagrafica) " +
                            "join magazzino on (magazzino.id = rigadocumentocommerciale.idMagazzinoDestinazione) " +
                            "join articolo on articolo.id = rigadocumentocommerciale.idarticolo " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' " +
                            "and articolo.nome like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' " +
                            "and RigaDocumentoCommerciale.stato in (0) and Magazzino.nome like '"+storeXFile+"' " +
                            "order by dataConsegna ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess=true;
                        numOXNeg.add(res.getString("numero"));
                        statoNeg.add(res.getString("stato"));
                        qtaOXNeg.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        serieOXNeg.add(res.getString("serie"));
                        clienteOXNeg.add(res.getString("ragioneSociale"));
                        idRigaOX.add(res.getString("idRiga"));
                        idDocOX.add(res.getString("idDoc"));
                        already.add(false);
                    }
                }
            }catch (Exception ex) {
                ex.getMessage();
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
        }

        protected void updStateSede(String idR, String idD) {
            Connection con = null;
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String ConnURL;
                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    ConnURL = "jdbc:jtds:sqlserver://85.47.29.51/PassepartoutRetail;user=mdrsa;password=MDRSqlPass@2020;";
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
                } if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    Date date = Calendar.getInstance().getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    Integer day = cal.get(Calendar.DAY_OF_MONTH);
                    String dayS = day.toString();
                    Integer year = cal.get(Calendar.YEAR);
                    Integer month = cal.get(Calendar.MONTH);
                    String monthS;
                    if(month == 12){
                        monthS = "01";
                        year++;
                    }else{
                        month++;
                        monthS = month.toString();
                    }
                    if(dayS.length()==1){
                        dayS = "0"+dayS;
                    }
                    if(monthS.length()==1){
                        monthS = "0"+monthS;
                    }
                    String myDate = year+"-"+monthS+"-"+dayS+" "+date.getHours()+":"+date.getMinutes()+":59.999";
                    Log.d("DATA AGG", myDate);
                    String query = "BEGIN TRANSACTION; \n" +
                            "UPDATE RigaDocumentoCommerciale \n" +
                            "SET RigaDocumentoCommerciale.stato = 3, \n" +
                            "RigaDocumentoCommerciale.dataUltimaModifica = GETDATE(), RigaDocumentoCommerciale.timeStmp = (select timeStmp from rigadocumentocommerciale where id = "+idR+") + 1  " +
                            "FROM RigaDocumentoCommerciale T1, DocumentoCommerciale T2 \n" +
                            "WHERE T1.idMaster = T2.id \n" +
                            "AND T1.id = "+idR+" ; \n" +
                            "UPDATE DocumentoCommerciale \n" +
                            "SET DocumentoCommerciale.dataUltimaModifica = GETDATE(), DocumentoCommerciale.timeStmp = (select timeStmp from documentocommerciale where id = "+idD+") + 1," +
                            "DocumentoCommerciale.note = CONCAT((select note from documentocommerciale where id = "+idD+"),'.') " +
                            "FROM RigaDocumentoCommerciale T1, DocumentoCommerciale T2 \n" +
                            "WHERE T1.idMaster = T2.id \n" +
                            "AND T2.id = "+idD+"; \n" +
                            "COMMIT; ";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            }catch (Exception ex) {
                ex.getMessage();
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
        }

        protected void updState(String idR, String idD) {
            Connection con = null;
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
                } if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    Date date = Calendar.getInstance().getTime();
                    Calendar cal = Calendar.getInstance();
                    cal.getTime();
                    Integer day = cal.get(Calendar.DAY_OF_MONTH);
                    String dayS = day.toString();
                    Integer year = cal.get(Calendar.YEAR);
                    Integer month = cal.get(Calendar.MONTH);
                    String monthS;
                    if(month == 12){
                        monthS = "01";
                        year++;
                    }else{
                        month++;
                        monthS = month.toString();
                    }
                    if(dayS.length()==1){
                        dayS = "0"+dayS;
                    }
                    if(monthS.length()==1){
                        monthS = "0"+monthS;
                    }
                    String myDate = year+"-"+monthS+"-"+dayS+" "+date.getHours()+":"+date.getMinutes()+":59.999";
                    Log.d("DATA AGG", myDate);
                    String query = "BEGIN TRANSACTION; \n" +
                            "UPDATE RigaDocumentoCommerciale \n" +
                            "SET RigaDocumentoCommerciale.stato = 3, \n" +
                            "RigaDocumentoCommerciale.dataUltimaModifica = GETDATE(), RigaDocumentoCommerciale.timeStmp = (select timeStmp from rigadocumentocommerciale where id = "+idR+") + 1  " +
                            "FROM RigaDocumentoCommerciale T1, DocumentoCommerciale T2 \n" +
                            "WHERE T1.idMaster = T2.id \n" +
                            "AND T1.id = "+idR+" ; \n" +
                            "UPDATE DocumentoCommerciale \n" +
                            "SET DocumentoCommerciale.dataUltimaModifica = GETDATE(), DocumentoCommerciale.timeStmp = (select timeStmp from documentocommerciale where id = "+idD+") + 1," +
                            "DocumentoCommerciale.note = CONCAT((select note from documentocommerciale where id = "+idD+"),'.') " +
                            "FROM RigaDocumentoCommerciale T1, DocumentoCommerciale T2 \n" +
                            "WHERE T1.idMaster = T2.id \n" +
                            "AND T2.id = "+idD+"; \n" +
                            "COMMIT; ";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                }
            }catch (Exception ex) {
                ex.getMessage();
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess){
                infoOX = new ArrayList<>();
                findOXNeg();
                for(int i=0; i<numOX.size();i++){
                    EditText thisEditText = new EditText(context);
                    thisEditText.setFocusableInTouchMode(false);
                    thisEditText.clearFocus();
                    boolean found = false;
                    for(int j=0; j<numOXNeg.size(); j++){
                        if(numOX.get(i).equals(numOXNeg.get(j))&&serieOX.get(i).equals(serieOXNeg.get(j))&&!already.get(j)&&!found){
                            found=true;
                            already.set(j,true);
                        }
                    }if(!found){
                        founded.add("N");
                        thisEditText.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    }else{
                        founded.add("Y");
                    }
                    thisEditText.setText("Numero: " + numOX.get(i) + "\n");
                    thisEditText.append("Quantità: " + qtaOX.get(i) + "\n");
                    thisEditText.append("Data consegna: " + dataC.get(i) + "\n");
                    thisEditText.append("Store: " + decodificaSerie(serieOX.get(i)) + "\n");

                    infoOX.add(thisEditText);
                }
                infoOX("Info OX " + artOX, infoOX);
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
                    String query = "select articolo.nome as codiceArticolo, RigaDocumentoCommerciale.quantita, " +
                            "RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, " +
                            "RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie, ragioneSociale, " +
                            "RigaDocumentoCommerciale.id as idRiga, DocumentoCommerciale.id as idDoc " +
                            "from RigaDocumentoCommerciale " +
                            "join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "join Cliente on (Cliente.id=DocumentoCommerciale.idCliente) " +
                            "join Anagrafica on (Anagrafica.id=Cliente.idAnagrafica) " +
                            "join articolo on articolo.id = rigadocumentocommerciale.idarticolo " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' " +
                            "and articolo.nome like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' " +
                            "and RigaDocumentoCommerciale.stato in (0) and RigaDocumentoCommerciale.idMagazzinoDestinazione = "+mag+" " +
                            "order by dataConsegna ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess=true;
                        numOX.add(res.getString("numero"));
                        idROXSede.add(res.getString("idRiga"));
                        idDOXSede.add(res.getString("idDoc"));
                        if(res.getString("dataConsegna")!=null){
                            dataC.add(res.getString("dataConsegna").substring(0,11));
                        }else{
                            dataC.add("31/12/9999");
                        }
                        stato.add(res.getString("stato"));
                        qtaOX.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        serieOX.add(res.getString("serie"));
                        clienteOX.add(res.getString("ragioneSociale"));
                    }
                }
            }catch (Exception ex) {
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

    public class InfoOX extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String artOX = "";
        ArrayList<String> numOX, dataC, stato, qtaOX, serieOX;
        EditText infoOX;

        @Override
        protected void onPreExecute() {
            artOX = txtCodArt.getText().toString();
            numOX = new ArrayList<>();
            dataC = new ArrayList<>();
            stato = new ArrayList<>();
            qtaOX = new ArrayList<>();
            serieOX = new ArrayList<>();
        }

        private void infoOX(String title,EditText message){
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                    .setTitle(title)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.cancel());

            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            layout.addView(message);

            builder.setView(layout);

            AlertDialog ok = builder.create();
            ok.show();
        }

        public String decodificaSerie(String serie){
            switch (serie) {
                case "1": return "MASTER";
                case "20": return "SESTU";
                case "5": return "MARCONI";
                case "3":return "PIRRI";
                case "4": return "OLBIA";
                case "9": return "SASSARI";
                case "6": return "NUORO";
                case "7": return "CARBONIA";
                case "8": return "TORTOLI";
                case "10": return "ORISTANO";
                case "33": return "TIBURTINA";
                case "31": return "CAPENA";
                case "32": return "OSTIENSE";
                case "34": return "MasterMagRoma";
                case "35": return "CASILINA";
                case "36": return "POMEZIA";
                case "37": return "ROMACEDI";
                case "38": return "ARDEATINA";
                case "50": return "VERONA";
                default: return "";
            }
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess){
                infoOX = new EditText(context);
                infoOX.setFocusableInTouchMode(false);
                infoOX.clearFocus();
                for(int i=0; i<numOX.size();i++){
                    String thisState;
                    if(stato.get(i).equals("0")){
                        thisState = "SOSPESO";
                    }else{
                        thisState = "BLOCCATO";
                    }
                    if(i==0){
                        infoOX.setText("Numero: " + numOX.get(i) + "\n");
                        infoOX.append("Quantità: " + qtaOX.get(i) + "\n");
                        infoOX.append("Data consegna: " + dataC.get(i) + "\n");
                        infoOX.append("Stato: " + thisState + "\n");
                        infoOX.append("Store: " + decodificaSerie(serieOX.get(i)) + "\n\n\n");
                    }else{
                        infoOX.append("Numero: " + numOX.get(i) + "\n");
                        infoOX.append("Quantità: " + qtaOX.get(i) + "\n");
                        infoOX.append("Data consegna: " + dataC.get(i) + "\n");
                        infoOX.append("Stato: " + thisState + "\n");
                        infoOX.append("Store: " + decodificaSerie(serieOX.get(i)) + "\n\n\n");
                    }
                }
                infoOX("Info OX " + artOX, infoOX);
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
                    String query = "select articolo.nome as codiceArticolo, RigaDocumentoCommerciale.quantita, RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie " +
                            "from RigaDocumentoCommerciale join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "join articolo on articolo.id = rigadocumentocommerciale.idarticolo " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' and articolo.nome like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' and RigaDocumentoCommerciale.stato in (0,3) and RigaDocumentoCommerciale.idMagazzinoDestinazione = "+mag+" " +
                            "order by RigaDocumentoCommerciale.stato desc ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess=true;
                        numOX.add(res.getString("numero"));
                        if(res.getString("dataConsegna")!=null){
                            dataC.add(res.getString("dataConsegna").substring(0,11));
                        }else{
                            dataC.add("31/12/9999");
                        }
                        stato.add(res.getString("stato"));
                        qtaOX.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        serieOX.add(res.getString("serie"));
                    }
                }
            }catch (Exception ex) {
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

    public class UpdateThings extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;


        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query="";
                    if(!insUbicSpunta.getText().toString().equals("")){
                        if(!insSubicSpunta.getText().toString().equals("")){
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbicSpunta.getText().toString()+"', sottoUbicazione = '"+insSubicSpunta.getText().toString()+"' " +
                                    "FROM articoloxmagazzino join Articolo on articoloxmagazzino.idArticolo = articolo.id " +
                                    "WHERE articolo.nome = '"+txtCodArt.getText()+"' and articoloxmagazzino.idMagazzino = "+mag+" ";
                        }else{
                            query = "UPDATE articoloxmagazzino " +
                                    "SET ubicazione = '"+insUbicSpunta.getText().toString()+"' " +
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
}