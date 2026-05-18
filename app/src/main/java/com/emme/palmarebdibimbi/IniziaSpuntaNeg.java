package com.emme.palmarebdibimbi;

import static com.emme.palmarebdibimbi.VerificaGiacenze.fromCharCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zebra.sdk.comm.BluetoothConnection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
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

public class IniziaSpuntaNeg extends AppCompatActivity {

    String insert = "", nomeP = "", utente, ipNeg = "", store = "";
    long idSpuntaDocRoom = -1;
    AppDb appDb;
    ConnectionClass connectionClass;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> qtaDaScalare = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    String doc4Print = "";
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> codici = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> qtaSpunta = new ArrayList<>();
    ArrayList<String> qtaDocum = new ArrayList<>();
    ArrayList<String> numero = new ArrayList<>();
    ArrayList<String> costo = new ArrayList<>();
    String przPromo = "0,00";
    String inizioPromo = "";
    String finePromo = "";
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> timeSparata = new ArrayList<>();
    ArrayList<String> serieDoc = new ArrayList<>();
    FloatingActionButton infoSC;
    ProgressBar pbSearchArt;
    String findThis, ean;
    String fileName = "";
    Double prz;
    int nColli = 0;
    Spinner spinner;
    String docsName = "";
    CheckBox chkEtic;
    Integer mag, listino, mag4Ric, idL;
    Integer esistenza = 0;
    Integer of = 0;
    Integer oc = 0;
    Integer scorta = 0;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, txtPrzArt, txtEsSp, txtQtaLP, txtOrdF, txtOrdC, txtScorta;
    Button search, btnNextArt, btnBackArt, fineSpunta;
    FloatingActionButton addBC;
    EditText txtInsEAN, insNColliSpunta, insQtaSpunta, insQtaSS, segnacollo;
    Context context;
    String tipoDoc;
    String printer = "";
    String in;
    String fornitore = "";
    RadioButton rbSS, rbSM;
    int tipoSparata = 0;
    String bt = "";
    int giaPremuto = 0;
    String segnaC = "";
    String tipoEt = "";
    String codArt = "";
    String desc = "";
    String PV = "";
    String PP = "";
    String eanP = "";
    Integer qtaP = 0;
    com.zebra.sdk.comm.Connection connection;

    SharedPreferences prefs;

    public void inizializzaFile(){

        //CancDoc cancDoc = new CancDoc();
        //cancDoc.execute();
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
                    idXArt.add(idDoc.get(i));
                    tempCodArt.add(codArticolo.get(i));
                    tempNumDoc.add(numDoc.get(i));
                    qtaArtXID.add(qta.get(i));
                    tempCosto.add(costo.get(i));
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

        for(int i=0; i<codArticolo.size(); i++){
            codici.add(codArticolo.get(i));
            qtaDaScalare.add(qta.get(i));
            qtaSpunta.add("0");
            alias.add("");
            timeSparata.add("");
            qtaDocum.add(qta.get(i));
            numero.add(numDoc.get(i));
        }

        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet
        Sheet barcodes = workbook.createSheet("Barcode");
        Sheet segnacollo = workbook.createSheet("Info colli");

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
                testata.createCell(9).setCellValue("Sparata");
                testata.createCell(10).setCellValue(utente);
                testata.createCell(11).setCellValue("Magazzino");
                testata.createCell(12).setCellValue("Costo");
                testata.createCell(13).setCellValue("Rifornimento");
                testata.createCell(14).setCellValue("ID Documento");
                Row testataB = barcodes.createRow(i);
                testataB.createCell(0).setCellValue("Codice articolo");
                testataB.createCell(1).setCellValue("EAN");
                Row testataSC = segnacollo.createRow(i);
                testataSC.createCell(0).setCellValue("Codice articolo");
                testataSC.createCell(1).setCellValue("EAN");
                testataSC.createCell(2).setCellValue("Qta");
                testataSC.createCell(3).setCellValue("N. Collo");
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
            row.createCell(11).setCellValue(store);
            row.createCell(12).setCellValue(costo.get(i));
            row.createCell(13).setCellValue("0");
            row.createCell(14).setCellValue(idDoc.get(i));

            /*
            if(x>999){
                anotherInsert.add(insert);
                insert = "";
                x = 0;
            }
            insert = insert + "('"+codici.get(i)+"'," + "'"+descrizioni.get(i)+"', '', '', ''," +
                    qta.get(i) + ", 0, "+risultato+", '"+numDoc.get(i)+"', '',  '"+nomeP+"', '"+utente+"' ),";
            x++;*/
        }
        /*
        insert = insert.substring(0,insert.length()-1);
        anotherInsert.add(insert);*/

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
        //CreaDoc creaDoc = new CreaDoc();
        //creaDoc.execute();
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
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

    @Override
    public void onBackPressed() {
        alertDisplayer("Attenzione!","Sei sicuro di voler abbandonare la pagina di spunta? Verrai riportato alla home!");
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaSpuntaNeg.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void showQtaXC(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaChiudi(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpuntaNeg.this, ReviewSpuntaNeg.class);
                    review.putExtra("docsName", docsName);
                    review.putExtra("store", store);
                    review.putExtra("fileName", fileName);
                    review.putExtra("tipo", 0);
                    review.putExtra("tipoDoc", tipoDoc);
                    review.putExtra("idSpuntaDocRoom", idSpuntaDocRoom);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void aggiungiEan(String title,String message){
        if(!txtCodArt.getText().equals("")){
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.add_barcode_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
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

    private void deleteDoc(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaNeg.this)
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

    private void docPres(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaNeg.this)
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_spunta_neg);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp","");
        printer = preferences.getString("printer", "");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            tipoDoc = extras.getString("tipoDoc");
            fornitore = extras.getString("fornitore");
            utente = extras.getString("utente");
            segnaC = extras.getString("segnaC");
            idSpuntaDocRoom = extras.getLong("idSpuntaDocRoom", -1);
        }
        appDb = AppDb.getInstance(this);
        fornitore = fornitore.replace("è","e");
        fornitore = fornitore.replace("é","e");
        fornitore = fornitore.replace("à","a");
        fornitore = fornitore.replace("ì","i");
        fornitore = fornitore.replace("ò","o");
        fornitore = fornitore.replace("ù","u");
        fornitore = fornitore.replace("'","");

        connectionClass = new ConnectionClass();
        context = this;

        spinner = findViewById(R.id.spnEtic);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        numDoc = ((MyApplication) this.getApplication()).getNum();
        serieDoc = ((MyApplication) this.getApplication()).getSerie();
        nomeP = p.getString("NomePalm","");
        switch (p.getString("storeName", "")) {
            case "MASTER":
                mag4Ric = 1;
                idL = 1;
                break;
            case "SESTU":
                mag4Ric = 77;
                idL = 1;
                break;
            case "MARCONI":
                mag4Ric = 35;
                idL = 1;
                break;
            case "PIRRI":
                mag4Ric = 72;
                idL = 1;
                break;
            case "OLBIA":
                mag4Ric = 76;
                idL = 1;
                break;
            case "SASSARI":
                mag4Ric = 74;
                idL = 1;
                break;
            case "NUORO":
                mag4Ric = 32;
                idL = 1;
                break;
            case "CARBONIA":
                mag4Ric = 78;
                idL = 1;
                break;
            case "TORTOLI":
                mag4Ric = 75;
                idL = 1;
                break;
            case "ORISTANO":
                mag4Ric = 71;
                idL = 1;
                break;
            case "TIBURTINA":
                mag4Ric = 85;
                idL = 1;
                break;
            case "MasterMagRoma":
                mag4Ric = 91;
                idL = 1;
                break;
            case "CEDIROMAINLAV":
                mag4Ric = 93;
                idL = 1;
                break;
            case "CAPENA":
                mag4Ric = 87;
                idL = 1;
                break;
            case "OSTIENSE":
                mag4Ric = 86;
                idL = 1;
                break;
            case "IN LAVORAZIONE":
                mag4Ric = 59;
                idL = 1;
                break;
            case "CASILINA":
                mag4Ric = 90;
                idL = 1;
                break;
            case "ARDEATINA":
                mag4Ric = 112;
                idL = 1;
                break;
            case "VERONA":
                mag4Ric = 114;
                idL = 1;
                break;
            case "POMEZIA":
                mag4Ric = 94;
                idL = 1;
                break;
            case "ROMACEDI":
                mag4Ric = 111;
                idL = 1;
                break;
            case "INTRANSITO":
                mag4Ric = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                mag4Ric = 89;
                idL = 1;
                break;
            default:
                mag4Ric = 1;
                break;
        }
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
                ipNeg = "192.168.2.41";
                store = "MASTER";
                break;
            case 77:
                ipNeg = "192.168.1.20";
                store = "SESTU";
                break;
            case 35:
                ipNeg = "192.168.1.20";
                store = "MARCONI";
                break;
            case 72:
                ipNeg = "192.168.1.20";
                store = "PIRRI";
                break;
            case 76:
                ipNeg = "192.168.1.10";
                store = "OLBIA";
                break;
            case 74:
                ipNeg = "192.168.1.20";
                store = "SASSARI";
                break;
            case 32:
                ipNeg = "192.168.1.20";
                store = "NUORO";
                break;
            case 78:
                ipNeg = "192.168.1.20";
                store = "CARBONIA";
                break;
            case 75:
                ipNeg = "192.168.1.20";
                store = "TORTOLI";
                break;
            case 71:
                ipNeg = "192.168.1.20";
                store = "ORISTANO";
                break;
            case 85:
                ipNeg = "195.100.100.202";
                store = "TIBURTINA";
                break;
            case 91:
                ipNeg = "85.47.29.51";
                store = "MASTER ROMA";
                break;
            case 93:
                ipNeg = "192.168.1.20";
                store = "CEDIROMAINLAV";
                break;
            case 87:
                ipNeg = "192.168.188.20";
                store = "CAPENA";
                break;
            case 86:
                ipNeg = "196.100.100.203";
                store = "OSTIENSE";
                break;
            case 59:
                ipNeg = "192.168.2.41";
                store = "IN LAVORAZIONE";
                break;
            case 90:
                ipNeg = "192.168.1.20";
                store = "CASILINA";
                break;
            case 94:
                ipNeg = "192.168.1.20";
                store = "POMEZIA";
                break;
            case 112:
                ipNeg = "192.168.1.20";
                store = "ARDEATINA";
                break;
            case 114:
                ipNeg = "192.168.16.20";
                store = "VERONA";
                break;
            case 111:
                ipNeg = "192.168.1.20";
                store = "ROMACEDI";
                break;
            case 88:
                ipNeg = "192.168.2.41";
                store = "IN TRANSITO";
                break;
            case 89:
                ipNeg = "192.168.2.41";
                store = "IN TEMPORANEO";
                break;
            default:
                break;
        }

        if(idSpuntaDocRoom > 0){
            inizializzaDaRoom();
        } else {
            File fileEx = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
            if(fileEx.exists()){
                File directory = new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaGen");
                File directory2 = new File(Environment.getExternalStorageDirectory(), "Backup");

                File file = new File(directory, fileName);
                File file2 = new File(directory2, fileName);

                if (isExcelFileEmpty(file)) {
                    alertDisplayer("Attenzione!","Documento danneggiato, ti consigliamo di contattare l'assistenza prima di procedere, potresti perdere i dati ");
                } else {
                    docPres("Attenzione!","Documento presente");
                }
            }else{
                inizializzaFile();
            }
        }

        chkEtic = findViewById(R.id.chkEtic);
        insQtaSS = findViewById(R.id.insQtaSS);
        rbSS = findViewById(R.id.rbSS);
        rbSM = findViewById(R.id.rbSM);
        search = findViewById(R.id.btnSearchThisArtNeg);
        qtaDoc = findViewById(R.id.txtQtaDocNeg);
        txtCodArt = findViewById(R.id.txtShowCodArtNeg);
        txtDesc = findViewById(R.id.txtShowDescNeg);
        txtInsEAN = findViewById(R.id.txtInsEANNeg);
        insNColliSpunta = findViewById(R.id.insNColliSpuntaNeg);
        insQtaSpunta = findViewById(R.id.insQtaSpuntaNeg);
        btnNextArt = findViewById(R.id.btnNextArtNeg);
        btnBackArt = findViewById(R.id.btnBackArtNeg);
        lblColli = findViewById(R.id.lblNCNeg);
        addBC = findViewById(R.id.floatAddBC);
        lblQta = findViewById(R.id.lblQXCNeg);
        txtEsSp = findViewById(R.id.txtEsSpNeg);
        txtPrzArt = findViewById(R.id.txtPrzArtNeg);
        txtQtaLP = findViewById(R.id.txtQtaLPNeg);
        txtOrdF = findViewById(R.id.txtOrdFNeg);
        txtScorta = findViewById(R.id.txtScortaNeg);
        txtOrdC = findViewById(R.id.txtOrdCNeg);
        pbSearchArt = findViewById(R.id.pbSearchArtNeg);
        fineSpunta = findViewById(R.id.btnFineSpunta);
        Button msc = findViewById(R.id.button4);
        Button psc = findViewById(R.id.button);
        segnacollo = findViewById(R.id.insertSC);
        TextView txtSC = findViewById(R.id.txtSC);
        infoSC = findViewById(R.id.btnInfoC);

        rbSS.setChecked(true);

        pbSearchArt.setVisibility(View.GONE);
        btnBackArt.setVisibility(View.GONE);
        btnNextArt.setVisibility(View.GONE);
        lblQta.setVisibility(View.GONE);
        lblColli.setVisibility(View.GONE);
        insNColliSpunta.setVisibility(View.GONE);
        insNColliSpunta.setEnabled(false);
        insQtaSpunta.setVisibility(View.GONE);
        insQtaSpunta.setEnabled(false);

        txtInsEAN.setFocusableInTouchMode(true);
        txtInsEAN.requestFocus();
        txtInsEAN.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        if(segnaC == null){
            segnaC = "NO";
            msc.setVisibility(View.GONE);
            psc.setVisibility(View.GONE);
            segnacollo.setVisibility(View.GONE);
            infoSC.setVisibility(View.GONE);
            txtSC.setVisibility(View.GONE);
        }else if(segnaC.equals("PROGRESSIVO")){
            segnacollo.setText("1");
        }else if(segnaC.equals("LIBERO")){
            msc.setVisibility(View.GONE);
            psc.setVisibility(View.GONE);
        }else{
            msc.setVisibility(View.GONE);
            psc.setVisibility(View.GONE);
            segnacollo.setVisibility(View.GONE);
            infoSC.setVisibility(View.GONE);
            txtSC.setVisibility(View.GONE);
        }
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
        chkEtic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && (printer.equals("ZEBRA") || printer.equals("ZEBRA NUOVA"))){
                    // Non aprire la connessione qui: verrà aperta prima di ogni stampa
                    connection = new BluetoothConnection(bt);
                }else{
                    try{
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        FloatingActionButton btnInfoOX = findViewById(R.id.btnInfoOXP);
        btnInfoOX.setOnClickListener(v -> {
            IniziaSpuntaNeg.InfoOX info = new IniziaSpuntaNeg.InfoOX();
            info.execute();
        });
        rbSS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(rbSS.isChecked()){
                rbSM.setChecked(false);
                tipoSparata = 0;
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                fineSpunta.setVisibility(View.VISIBLE);
                insQtaSpunta.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.VISIBLE);
            }else{
                rbSM.setChecked(true);
                tipoSparata = 1;
                lblColli.setVisibility(View.VISIBLE);
                fineSpunta.setVisibility(View.GONE);
                insQtaSpunta.setVisibility(View.VISIBLE);
                insNColliSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSS.setVisibility(View.GONE);
            }
        });
        rbSM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(rbSM.isChecked()){
                rbSS.setChecked(false);
                tipoSparata = 1;
                lblColli.setVisibility(View.VISIBLE);
                insQtaSpunta.setVisibility(View.VISIBLE);
                insNColliSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                fineSpunta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.GONE);
            }else{
                rbSS.setChecked(true);
                tipoSparata = 0;
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                fineSpunta.setVisibility(View.VISIBLE);
                lblColli.setVisibility(View.GONE);
                insQtaSpunta.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.VISIBLE);
            }
        });
        txtInsEAN.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = txtInsEAN.getText().toString();
                    if(rbSS.isChecked() && !isNumeric(insQtaSS.getText().toString())){
                        qtaErrata("Errore!","Inserisci una quantità valida");
                    }else if(!findThis.equals("")){
                        txtQtaLP.setText("");
                        txtOrdC.setText("");
                        txtOrdF.setText("");
                        txtEsSp.setText("");
                        txtCodArt.setText("");
                        txtDesc.setText("");
                        qtaDoc.setText("");
                        txtPrzArt.setText("");
                        IniziaSpuntaNeg.FindArt cercaArt = new IniziaSpuntaNeg.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });
        insNColliSpunta.setOnFocusChangeListener((v, hasFocus) -> insNColliSpunta.setSelectAllOnFocus(true));
        insQtaSpunta.setOnFocusChangeListener((v, hasFocus) -> insQtaSpunta.setSelectAllOnFocus(true));
        btnNextArt.setOnClickListener(v -> {
            if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSpunta.setEnabled(true);
            }else if(insQtaSpunta.isEnabled() && isNumeric(insQtaSpunta.getText().toString()) && isNumeric(insNColliSpunta.getText().toString())){
                if(idSpuntaDocRoom <= 0){
                XSSFWorkbook workbook;

                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);
                insQtaSpunta.setEnabled(false);
                Integer qtaSpuntata;
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
                if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                    qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                }else{
                    qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                }
                Integer qtaEtic = qtaSpuntata;
                Integer lastIndex = -1;
                int i = 0;
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
                i = 0;
                while(workbook.getSheetAt(0).getRow(i) != null){
                    Row row = workbook.getSheetAt(0).getRow(i);
                    if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                        lastIndex = i;
                        if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))>=qtaSpuntata && qtaSpuntata>0){
                            Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                            Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                            row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                            qtaSpuntata = 0;
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                            row.createCell(9).setCellValue(timing);
                            //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                            //aggDoc.execute();
                        }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                        }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                            Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                            row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                            row.createCell(9).setCellValue(timing);
                            //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                            //aggDoc.execute();
                        }
                    }
                    i++;
                }

                if(qtaSpuntata>0 && lastIndex != -1){
                    Row row = workbook.getSheetAt(0).getRow(lastIndex);
                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                    row.createCell(1).setCellValue(txtDesc.getText().toString());
                    Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                    row.createCell(6).setCellValue(qtaUltima.toString());
                    row.createCell(9).setCellValue(timing);
                    //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaUltima, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                    //aggDoc.execute();
                }else if(qtaSpuntata>0 && lastIndex == -1){
                    Row row = workbook.getSheetAt(0).createRow(i);
                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                    row.createCell(1).setCellValue(txtDesc.getText().toString());
                    row.createCell(0).setCellValue(txtCodArt.getText().toString());
                    row.createCell(6).setCellValue(qtaSpuntata.toString());
                    Integer newDiff = Integer.parseInt(qtaSpuntata.toString());
                    row.createCell(7).setCellValue(newDiff);
                    row.createCell(5).setCellValue("0");
                    row.createCell(8).setCellValue("");
                    row.createCell(9).setCellValue(timing);

                    row.createCell(11).setCellValue("");
                    row.createCell(13).setCellValue("");
                    row.createCell(14).setCellValue("");

                    insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                    //IniziaSpuntaNeg.CreaDoc creaDoc = new CreaDoc();
                    //creaDoc.execute();
                }
                Integer qtaS = 0;
                Integer qtaD = 0;
                i=0;
                while(workbook.getSheetAt(0).getRow(i) != null){
                    Row row = workbook.getSheetAt(0).getRow(i);
                    if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                        qtaS = qtaS + Integer.parseInt(row.getCell(6).getStringCellValue());
                        qtaD = qtaD + Integer.parseInt(row.getCell(5).getStringCellValue());
                    }
                    i++;
                }
                ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                if(qtaS < qtaD){
                    back.setBackgroundColor(Color.RED);
                }else if(qtaS > qtaD){
                    back.setBackgroundColor(Color.YELLOW);
                }else{
                    back.setBackgroundColor(Color.GREEN);
                }
                txtQtaLP.setText(qtaS.toString());


                if(chkEtic.isChecked()){
                    if(printer.equals("ZEBRA")){
                        if(!connection.isConnected()){
                            connection = new BluetoothConnection(bt);
                            try{
                                connection.open();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        stampaZebra(przPromo, inizioPromo, finePromo, false, Integer.parseInt(insNColliSpunta.getText().toString()));
                    }else{
                        if(!connection.isConnected()){
                            connection = new BluetoothConnection(bt);
                            try{
                                connection.open();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        stampaZebra(przPromo, inizioPromo, finePromo, true, Integer.parseInt(insNColliSpunta.getText().toString()));
                    }
                }

                nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                insQtaSpunta.setEnabled(false);
                fineSpunta.setVisibility(View.VISIBLE);
                txtInsEAN.setEnabled(true);
                txtInsEAN.setText("");
                insQtaSpunta.setText("1");
                insNColliSpunta.setText("1");
                txtInsEAN.setFocusableInTouchMode(true);
                txtInsEAN.requestFocus();
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
                } else if(idSpuntaDocRoom > 0){
                insQtaSpunta.setEnabled(false);
                Integer qtaSpuntataRoom;
                if(Integer.parseInt(insNColliSpunta.getText().toString()) == 0){
                    qtaSpuntataRoom = Integer.parseInt(insQtaSpunta.getText().toString());
                } else {
                    qtaSpuntataRoom = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                }
                Date dateR = new Date();
                Calendar calR = GregorianCalendar.getInstance();
                calR.setTime(dateR);
                int hoursR = calR.get(Calendar.HOUR_OF_DAY);
                Integer minutesR = calR.get(Calendar.MINUTE);
                String timingR = hoursR + ":" + (minutesR.toString().length()==1 ? "0"+minutesR : minutesR.toString());
                final String codArtValRM = txtCodArt.getText().toString();
                final String eanValRM = txtInsEAN.getText().toString();
                final int nColliValRM = Integer.parseInt(insNColliSpunta.getText().toString());
                final String segnacolloValRM = segnacollo != null ? segnacollo.getText().toString() : "";
                nColli = nColli + nColliValRM;
                if(chkEtic.isChecked()){
                    if(printer.equals("ZEBRA")){
                        if(!connection.isConnected()){
                            connection = new BluetoothConnection(bt);
                            try{ connection.open(); } catch(Exception e){ e.printStackTrace(); }
                        }
                        stampaZebra(przPromo, inizioPromo, finePromo, false, nColliValRM);
                    }else{
                        if(!connection.isConnected()){
                            connection = new BluetoothConnection(bt);
                            try{ connection.open(); } catch(Exception e){ e.printStackTrace(); }
                        }
                        stampaZebra(przPromo, inizioPromo, finePromo, true, nColliValRM);
                    }
                }
                aggiornaSpuntaInRoom(codArtValRM, qtaSpuntataRoom, eanValRM, timingR, nColliValRM, segnacolloValRM, () -> {
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    fineSpunta.setVisibility(View.VISIBLE);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtInsEAN.setFocusableInTouchMode(true);
                    txtInsEAN.requestFocus();
                });
                }
            }else{
                qtaErrata("Errore!", "Controlla di aver inserita delle quantità valide e riprova");
            }
        });
        btnBackArt.setOnClickListener(v -> {
            if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(true);
            }else if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insNColliSpunta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                txtInsEAN.setEnabled(true);
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
            }else if(!insQtaSpunta.isEnabled() && insQtaSpunta.getVisibility() == View.VISIBLE){
                insQtaSpunta.setEnabled(true);
            }
        });
        fineSpunta.setOnClickListener(v -> {
            salvaChiudi("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
        });
        search.setOnClickListener(v -> {
            if(rbSS.isChecked() && !isNumeric(insQtaSS.getText().toString())){
                qtaErrata("Errore!","Inserisci una quantità valida");
            }else{
                findThis = txtInsEAN.getText().toString();
                if(findThis.isEmpty()){
                    new AlertDialog.Builder(IniziaSpuntaNeg.this)
                            .setTitle("Attenzione!")
                            .setMessage("Impossibile registrare un articolo senza codice. Inserisci o scansiona un codice.")
                            .setPositiveButton("OK", (d, w) -> d.cancel())
                            .create().show();
                }else{
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    FindArt cercaArt = new FindArt();
                    cercaArt.execute("");
                }
            }
        });
        addBC.setOnClickListener(v -> {
            aggiungiEan("","");
        });

        if (idSpuntaDocRoom > 0) {
            new PreloadArticoli().execute();
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;

    protected void connect() {
        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
            this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;
            print_bt();
        }
    }

    private void print_bt() {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            String desc1, desc2;
            if(desc.length() > 28){
                desc1 = desc.substring(0,23);
                desc2 = desc.substring(23);
            }else{
                desc1 = desc;
                desc2 = "";
            }
            String etichetta = "";
            for(int i=0; i<qtaP; i++){
                switch (tipoEt){
                    case "Etichetta piccola":
                        etichetta = "^XA\n" +
                                "^FW^FO95,1^AR,75,75^FD"+PV+"^FS\n" +
                                "^FW^FO1,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO1,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FO80,140^BY1\n" +
                                "^BC,55,Y,N,N\n" +
                                "^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO190,180^AR,100,100^FD"+PV+"^FS\n" +
                                "^FO140,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO100,100^AR,10,10^FD"+codArt+"^FS\n" +
                                "^FO50,100^BY2\n" +
                                "^BC,50,Y,N,N\n" +
                                "^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Etichetta promo":
                        etichetta = "^XA\n" +
                                "^FW^FO140,1^AR,60,60^FD"+PP+"^FS\n" +
                                "^FW^FO8,1^AR,5,5^FD"+PV+"^FS\n" +
                                "^FW^FO8,30^AR,5,5^FDSC% 20%^FS\n" +
                                "^FW^FO8,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO8,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FW^FO100,130^AR,5,5^FD"+codArt+"^FS\n" +
                                "^FW^FO75,160^AR,5,5^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino promo":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO195,110^AR,100,100^FDOFFERTA^FS\n" +
                                "^FO165,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO120,25^AR,10,10^FD"+PP+" E.^FS\n" +
                                "^FO90,25^AR,10,10^FDSC. 29,77%^FS\n" +
                                "^FO70,210^AR,100,100^FD"+PV+" E.^FS\n" +
                                "^FO45,180^BY2\n" +
                                "^AR,10,10^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    default:
                        break;
                }
                btoutputstream.write(etichetta.getBytes());
                btoutputstream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
            if(btsocket != null){
                print_bt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertDisplayer2(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer3("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
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
                    Date date = new Date();
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        Integer minutes = calendar.get(Calendar.MINUTE);
                        String nMinutes;
                        if(minutes.toString().length()==1){
                            nMinutes = "0"+minutes.toString();
                        }else{
                            nMinutes = minutes.toString();
                        }
                        String timing = hours + ":" + nMinutes;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
                    Integer qtaEtic = qtaSpuntata;
                    Integer lastIndex = -1;
                    int i = 0;
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
                                Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                qtaSpuntata = 0;
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                row.createCell(1).setCellValue(txtDesc.getText().toString());
                                row.createCell(9).setCellValue(timing);
                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                row.createCell(9).setCellValue(timing);
                            }
                        }
                        i++;
                    }

                    if(qtaSpuntata>0 && lastIndex != -1){
                        Row row = workbook.getSheetAt(0).getRow(lastIndex);
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                        row.createCell(6).setCellValue(qtaUltima.toString());
                        row.createCell(9).setCellValue(timing);
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        Row row = workbook.getSheetAt(0).createRow(i);
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        row.createCell(0).setCellValue(txtCodArt.getText().toString());
                        row.createCell(6).setCellValue(qtaSpuntata.toString());
                        Integer newDiff = Integer.parseInt(qtaSpuntata.toString());
                        row.createCell(7).setCellValue(newDiff);
                        row.createCell(5).setCellValue("0");
                        row.createCell(8).setCellValue("");
                        row.createCell(9).setCellValue(timing);
                        row.createCell(11).setCellValue("");
                        row.createCell(13).setCellValue("");
                        row.createCell(14).setCellValue("");
                        insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                    }
                    Integer qtaS = 0;
                    Integer qtaD = 0;
                    i=0;
                    while(workbook.getSheetAt(0).getRow(i) != null){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                            qtaS = qtaS + Integer.parseInt(row.getCell(6).getStringCellValue());
                            qtaD = qtaD + Integer.parseInt(row.getCell(5).getStringCellValue());
                        }
                        i++;
                    }
                    ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                    if(qtaS < qtaD){
                        back.setBackgroundColor(Color.RED);
                    }else if(qtaS > qtaD){
                        back.setBackgroundColor(Color.YELLOW);
                    }else{
                        back.setBackgroundColor(Color.GREEN);
                    }
                    txtQtaLP.setText(qtaS.toString());
                    if(chkEtic.isChecked()){
                        if(printer.equals("ZEBRA")){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, qtaEtic);
                            print.main();
                        }else{
                            tipoEt = spinner.getSelectedItem().toString();
                            codArt = txtCodArt.getText().toString();
                            desc = txtDesc.getText().toString();
                            PV =  txtPrzArt.getText().toString();
                            PP = "";
                            eanP = ean;
                            qtaP = qtaEtic;
                            connect();
                        }
                    }
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    dialog.cancel();
                    txtInsEAN.setFocusableInTouchMode(true);
                    txtInsEAN.requestFocus();
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
                    } else {
                        // Room flow: reset UI senza Excel
                        nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                        btnBackArt.setVisibility(View.GONE);
                        btnNextArt.setVisibility(View.GONE);
                        lblQta.setVisibility(View.GONE);
                        lblColli.setVisibility(View.GONE);
                        insNColliSpunta.setVisibility(View.GONE);
                        insNColliSpunta.setEnabled(false);
                        insQtaSpunta.setVisibility(View.GONE);
                        insQtaSpunta.setEnabled(false);
                        txtInsEAN.setEnabled(true);
                        txtInsEAN.setText("");
                        insQtaSpunta.setText("1");
                        insNColliSpunta.setText("1");
                        dialog.cancel();
                        txtInsEAN.setFocusableInTouchMode(true);
                        txtInsEAN.requestFocus();
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
                            Integer.parseInt(insNColliSpunta.getText().toString()),
                            segnacollo != null ? segnacollo.getText().toString() : "");
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
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
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
                    Integer qtaEtic = qtaSpuntata;
                    Integer lastIndex = -1;
                    int i = 0;
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
                                Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                qtaSpuntata = 0;
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                row.createCell(1).setCellValue(txtDesc.getText().toString());
                                row.createCell(9).setCellValue(timing);
                                //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                //aggDoc.execute();
                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                            }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                                row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                row.createCell(9).setCellValue(timing);
                                //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                //aggDoc.execute();
                            }
                        }
                        i++;
                    }

                    if(qtaSpuntata>0 && lastIndex != -1){
                        Row row = workbook.getSheetAt(0).getRow(lastIndex);
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                        row.createCell(6).setCellValue(qtaUltima.toString());
                        row.createCell(9).setCellValue(timing);
                        //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaUltima, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                        //aggDoc.execute();
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        Row row = workbook.getSheetAt(0).createRow(i);
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        row.createCell(0).setCellValue(txtCodArt.getText().toString());
                        row.createCell(6).setCellValue(qtaSpuntata.toString());
                        Integer newDiff = Integer.parseInt(qtaSpuntata.toString());
                        row.createCell(7).setCellValue(newDiff);
                        row.createCell(5).setCellValue("0");
                        row.createCell(8).setCellValue("");
                        row.createCell(9).setCellValue(timing);

                        row.createCell(11).setCellValue("");
                        row.createCell(13).setCellValue("");
                        row.createCell(14).setCellValue("");

                        insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                        //IniziaSpuntaNeg.CreaDoc creaDoc = new CreaDoc();
                        //creaDoc.execute();
                    }
                    Integer qtaS = 0;
                    Integer qtaD = 0;
                    i=0;
                    while(workbook.getSheetAt(0).getRow(i) != null){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                            qtaS = qtaS + Integer.parseInt(row.getCell(6).getStringCellValue());
                            qtaD = qtaD + Integer.parseInt(row.getCell(5).getStringCellValue());
                        }
                        i++;
                    }
                    ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                    if(qtaS < qtaD){
                        back.setBackgroundColor(Color.RED);
                    }else if(qtaS > qtaD){
                        back.setBackgroundColor(Color.YELLOW);
                    }else{
                        back.setBackgroundColor(Color.GREEN);
                    }
                    txtQtaLP.setText(qtaS.toString());
                    if(chkEtic.isChecked()){
                        if(printer.equals("ZEBRA")){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, qtaEtic);
                            print.main();
                        }else{
                            tipoEt = spinner.getSelectedItem().toString();
                            codArt = txtCodArt.getText().toString();
                            desc = txtDesc.getText().toString();
                            PV =  txtPrzArt.getText().toString();
                            PP = "";
                            eanP = ean;
                            qtaP = qtaEtic;
                            connect();
                        }
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
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpuntaNeg.this, ReviewSpuntaNeg.class);
                    review.putExtra("docsName", docsName);
                    review.putExtra("store", store);
                    review.putExtra("fileName", fileName);
                    review.putExtra("tipo", 0);
                    review.putExtra("tipoDoc", tipoDoc);
                    review.putExtra("idSpuntaDocRoom", idSpuntaDocRoom);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    /**
     * Aggiorna le righe spunta in Room dopo che l'utente ha confermato la quantità.
     * Adattato per SpuntaNeg (senza ubic/subic, con costo e rifornimento).
     */
    private void aggiornaSpuntaInRoom(String codiceArt, int qtaSpuntata, String ean, String timing,
                                       int nColliVal, String segnacolloVal) {
        aggiornaSpuntaInRoom(codiceArt, qtaSpuntata, ean, timing, nColliVal, segnacolloVal, null);
    }

    private void aggiornaSpuntaInRoom(String codiceArt, int qtaSpuntata, String ean, String timing,
                                       int nColliVal, String segnacolloVal, Runnable onComplete) {
        if (idSpuntaDocRoom <= 0) return;
        // Cattura valori View prima di entrare nel Thread
        final String descArt = txtDesc.getText().toString();
        final String storeVal = store != null ? store : "";
        new Thread(() -> {
            try {
                SpuntaDao dao = appDb.spuntaDao();
                List<SpuntaRigaEntity> righe = dao.getRigheByCodArt(idSpuntaDocRoom, codiceArt);
                int qtaRimanente = qtaSpuntata;

                for (SpuntaRigaEntity riga : righe) {
                    if (qtaRimanente == 0) break;
                    if (qtaRimanente > 0) {
                        // Aggiunta normale
                        int spazio = riga.qtaDoc - riga.qtaSpunta;
                        if (spazio <= 0) continue;
                        int daAggiungere = Math.min(spazio, qtaRimanente);
                        dao.incrementaSpunta(riga.id, daAggiungere, ean, timing, "", "");
                        dao.incrementaColli(riga.id, nColliVal > 0 ? nColliVal : 0);
                        qtaRimanente -= daAggiungere;
                    } else {
                        // Correzione negativa: sottrai dalle righe già spuntate
                        if (riga.qtaSpunta <= 0) continue;
                        int daTogliere = Math.min(riga.qtaSpunta, -qtaRimanente);
                        dao.incrementaSpunta(riga.id, -daTogliere, ean, timing, "", "");
                        dao.incrementaColli(riga.id, 0);
                        qtaRimanente += daTogliere;
                    }
                }

                // Se rimane quantità positiva (eccedenza rispetto al documento)
                if (qtaRimanente > 0 && !righe.isEmpty()) {
                    SpuntaRigaEntity ultima = righe.get(righe.size() - 1);
                    dao.incrementaSpunta(ultima.id, qtaRimanente, ean, timing, "", "");
                } else if (qtaRimanente > 0 && righe.isEmpty()) {
                    // Articolo non presente nel documento, aggiungi nuova riga
                    SpuntaRigaEntity nuova = new SpuntaRigaEntity();
                    nuova.idSpuntaDoc = idSpuntaDocRoom;
                    nuova.codArt = codiceArt;
                    nuova.desc = descArt;
                    nuova.alias = ean;
                    nuova.ubic = "";
                    nuova.subic = "";
                    nuova.qtaDoc = 0;
                    nuova.qtaSpunta = qtaRimanente;
                    nuova.diff = qtaRimanente;
                    nuova.timeSp = timing;
                    nuova.colli = nColliVal;
                    nuova.segnacollo = segnacolloVal;
                    nuova.store = storeVal;
                    nuova.costo = "0";
                    nuova.rifornimento = "";
                    dao.insertRiga(nuova);
                }

                // Aggiorna UI dopo la scrittura (stesso Thread, nessuna race condition)
                if (onComplete != null) {
                    List<SpuntaRigaEntity> righeAgg = dao.getRigheByCodArt(idSpuntaDocRoom, codiceArt);
                    int totQtaS = 0; int totQtaD = 0;
                    for (SpuntaRigaEntity r : righeAgg) { totQtaS += r.qtaSpunta; totQtaD += r.qtaDoc; }
                    final int fQtaS = totQtaS; final int fQtaD = totQtaD;
                    runOnUiThread(() -> {
                        ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                        if (back != null) {
                            if (fQtaS < fQtaD) back.setBackgroundColor(Color.RED);
                            else if (fQtaS > fQtaD) back.setBackgroundColor(Color.YELLOW);
                            else back.setBackgroundColor(Color.GREEN);
                        }
                        txtQtaLP.setText(String.valueOf(fQtaS));
                        onComplete.run();
                    });
                }

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
                List<SpuntaRigaEntity> righe = dao.getRigheByCodArt(idSpuntaDocRoom, codiceArt);
                int totQtaSpuntata = 0;
                int totQtaDoc = 0;

                for (SpuntaRigaEntity riga : righe) {
                    totQtaSpuntata += riga.qtaSpunta;
                    totQtaDoc += riga.qtaDoc;
                }

                final int fTotSpuntata = totQtaSpuntata;
                final int fTotDoc = totQtaDoc;
                final boolean fPresente = !righe.isEmpty();

                runOnUiThread(() -> {
                    if (fPresente) {
                        txtQtaLP.setText(String.valueOf(fTotSpuntata));
                        qtaDoc.setText(String.valueOf(fTotDoc));
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

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    txtCodArt.setText("");
                    txtDesc.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {

                    if(idSpuntaDocRoom <= 0){
                    XSSFWorkbook workbook;

                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                    if(rbSS.isChecked()){
                        txtInsEAN.setEnabled(false);
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
                        Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                        Integer lastIndex = -1;
                        int i = 0;
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
                                    Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                                    Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                                    row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                    qtaSpuntata = 0;
                                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                    row.createCell(1).setCellValue(txtDesc.getText().toString());
                                    row.createCell(9).setCellValue(timing);
                                    //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                    //aggDoc.execute();
                                }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                                }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                    qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                    Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                    row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                    row.createCell(9).setCellValue(timing);
                                    //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                    //aggDoc.execute();
                                }
                            }
                            i++;
                        }

                        if(qtaSpuntata>0 && lastIndex != -1){
                            Row row = workbook.getSheetAt(0).getRow(lastIndex);
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                            Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                            row.createCell(6).setCellValue(qtaUltima.toString());
                            row.createCell(9).setCellValue(timing);
                            //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaUltima, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                            //aggDoc.execute();
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            Row row = workbook.getSheetAt(0).createRow(i);
                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                            row.createCell(0).setCellValue(txtCodArt.getText().toString());
                            row.createCell(6).setCellValue(qtaSpuntata.toString());
                            row.createCell(5).setCellValue("0");
                            row.createCell(8).setCellValue("");
                            Integer newDiff = Integer.parseInt(qtaSpuntata.toString());
                            row.createCell(7).setCellValue(newDiff);
                            row.createCell(9).setCellValue(timing);

                            row.createCell(11).setCellValue("");
                            row.createCell(13).setCellValue("");
                            row.createCell(14).setCellValue("");

                            insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                            //IniziaSpuntaNeg.CreaDoc creaDoc = new CreaDoc();
                            //creaDoc.execute();
                        }
                        Integer qtaS = 0;
                        Integer qtaD = 0;
                        i=0;
                        while(workbook.getSheetAt(0).getRow(i) != null){
                            Row row = workbook.getSheetAt(0).getRow(i);
                            if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                                qtaS = qtaS + Integer.parseInt(row.getCell(6).getStringCellValue());
                                qtaD = qtaD + Integer.parseInt(row.getCell(5).getStringCellValue());
                            }
                            i++;
                        }
                        ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                        if(qtaS < qtaD){
                            back.setBackgroundColor(Color.RED);
                        }else if(qtaS > qtaD){
                            back.setBackgroundColor(Color.YELLOW);
                        }else{
                            back.setBackgroundColor(Color.GREEN);
                        }
                        txtQtaLP.setText(qtaS.toString());
                        if(chkEtic.isChecked()){
                            if(printer.equals("ZEBRA")){
                                BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                        txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, Integer.parseInt(insQtaSS.getText().toString()));
                                print.main();
                            }else{
                                tipoEt = spinner.getSelectedItem().toString();
                                codArt = txtCodArt.getText().toString();
                                desc = txtDesc.getText().toString();
                                PV =  txtPrzArt.getText().toString();
                                PP = "";
                                eanP = ean;
                                qtaP = Integer.parseInt(insQtaSS.getText().toString());
                                connect();
                            }
                        }
                        txtQtaLP.setText(qtaS.toString());
                        txtInsEAN.setEnabled(true);
                        txtInsEAN.setText("");
                        txtInsEAN.setFocusableInTouchMode(true);
                        txtInsEAN.requestFocus();
                        insQtaSS.setText("1");
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            play.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        txtInsEAN.setEnabled(false);
                        insNColliSpunta.setVisibility(View.VISIBLE);
                        insNColliSpunta.setEnabled(true);
                        lblColli.setVisibility(View.VISIBLE);
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
                    } else {
                        // Room path: aggiungi articolo non presente nel documento
                        if(rbSS.isChecked()){
                            txtInsEAN.setEnabled(false);
                            Date date2 = new Date();
                            Calendar calendar2 = GregorianCalendar.getInstance();
                            calendar2.setTime(date2);
                            int hours2 = calendar2.get(Calendar.HOUR_OF_DAY);
                            Integer minutes2 = calendar2.get(Calendar.MINUTE);
                            String nMinutes2 = minutes2.toString().length() == 1 ? "0" + minutes2 : minutes2.toString();
                            String timing2 = hours2 + ":" + nMinutes2;
                            int qtaSpuntata2 = Integer.parseInt(insQtaSS.getText().toString());
                            aggiornaSpuntaInRoom(txtCodArt.getText().toString(), qtaSpuntata2,
                                    txtInsEAN.getText().toString(), timing2, 0,
                                    segnacollo != null ? segnacollo.getText().toString() : "", () -> {
                                txtInsEAN.setEnabled(true);
                                txtInsEAN.setText("");
                                insQtaSS.setText("1");
                                txtInsEAN.setFocusableInTouchMode(true);
                                txtInsEAN.requestFocus();
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    play.play();
                                } catch (Exception e) { e.printStackTrace(); }
                            });
                        } else {
                            // rbSM: lascia inserire manualmente colli e quantità
                            txtInsEAN.setEnabled(false);
                            insNColliSpunta.setVisibility(View.VISIBLE);
                            insNColliSpunta.setEnabled(true);
                            lblColli.setVisibility(View.VISIBLE);
                        }
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
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

    private void qtaErrata(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if(rbSS.isChecked()){
                        insQtaSS.setFocusableInTouchMode(true);
                        insQtaSS.requestFocus();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaNeg.this)
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
                txtCodArt.setText(txtInsEAN.getText().toString());
                txtEsSp.setText("0");
                txtPrzArt.setText("€ " + "N/A");
                txtOrdF.setText("0");
                txtOrdC.setText("0");
                txtQtaLP.setText("0");
                txtDesc.setText(note.getText().toString());
                txtInsEAN.setEnabled(false);
                if(rbSM.isChecked()){
                    btnBackArt.setVisibility(View.VISIBLE);
                    btnNextArt.setVisibility(View.VISIBLE);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setEnabled(true);
                    lblColli.setVisibility(View.VISIBLE);
                }else{
                    if(idSpuntaDocRoom <= 0){
                    XSSFWorkbook workbook;

                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);
                        int i= 0;
                        while(workbook.getSheetAt(0).getRow(i) != null){
                            i++;
                        }
                        Row row = workbook.getSheetAt(0).createRow(i);
                        row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                        row.createCell(1).setCellValue(txtDesc.getText().toString());
                        row.createCell(0).setCellValue(txtCodArt.getText().toString());
                        row.createCell(6).setCellValue(insQtaSS.getText().toString());
                        row.createCell(5).setCellValue("0");
                        row.createCell(8).setCellValue("");
                        row.createCell(9).setCellValue(timing);

                        insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+insQtaSS.getText().toString()+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                        //IniziaSpuntaNeg.CreaDoc creaDoc = new CreaDoc();
                        //creaDoc.execute();
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
                    } else {
                        // Room path: articolo non nel database, inserisci riga con desc = nota
                        final String codArtNota = txtCodArt.getText().toString();
                        final String eanNota = txtInsEAN.getText().toString();
                        final int qtaNota = Integer.parseInt(insQtaSS.getText().toString());
                        aggiornaSpuntaInRoom(codArtNota, qtaNota, eanNota, timing, 0,
                                segnacollo != null ? segnacollo.getText().toString() : "", null);
                    }
                }
                txtInsEAN.setEnabled(true);
                txtInsEAN.setText("");
                insQtaSS.setText("1");
                txtInsEAN.setFocusableInTouchMode(true);
                txtInsEAN.requestFocus();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void stampaZebra(String przPromo, String inizioPromo, String finePromo, boolean isNew, int qtaStmp){
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

            String przV = txtPrzArt.getText().toString().substring(2).replace(".", ",");

            String addZ = przV.substring(przV.indexOf(","));
            if(addZ.length() < 3){
                przV = przV + "0";
            }
            String printFP = "";
            if(!inizioPromo.equals("")){
                inizioPromo = inizioPromo.substring(8,10) +"/"+ inizioPromo.substring(5,7) +"/"+ inizioPromo.substring(2,4);
                printFP = "dal: " + inizioPromo;
            }
            if(!finePromo.equals("")){
                finePromo = finePromo.substring(8,10) +"/"+ finePromo.substring(5,7) +"/"+ finePromo.substring(2,4);
                printFP = printFP + " al: " + finePromo;
            }
            String a = fromCharCode(0x80);
            Double scp = 0.0;
            String scps = "";
            String sc = "";
            String cpclData = "";

            Date date = Calendar.getInstance().getTime();

            // Display a date in day, month, year format
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            switch(spinner.getSelectedItem().toString()){
                case "Etichetta piccola":
                    cpclData = "!LABEL " +
                            "\n" + "\n" +
                            "! 0 0 0 275 1" + "CENTER" +
                            "\n" +
                            "TEXT 4 0 0 0 " + przV +
                            "\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 55 " + desc1 +
                            "\n" + "\n" +
                            "TEXT 7 0 5 80 " + desc2 +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 110 " + txtCodArt.getText().toString() +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "B 128 1 0 50 5 140 " + ean +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 195 " + ean +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalino":
                    if(txtDesc.getText().toString().length() > 45){
                        desc1 = txtDesc.getText().toString().substring(0,45);
                        desc2 = txtDesc.getText().toString().substring(45);
                    }else{
                        desc1 = txtDesc.getText().toString();
                        desc2 = "";
                    }
                    if(!isNew){
                        cpclData = "! 0 0 0 730 1 " +
                                "\n" +
                                "ENCODING GB18030" + "\n" +
                                //"\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "T270 4 3 570 90 "+a+" " + przV +
                                "\n" + "\n" + "\n" + "\n" +
                                "T270 7 0 630 15 " + desc1 +
                                "\n" + "\n" +
                                "T270 7 0 605 15 " + desc2 +
                                "\n" + "\n" +
                                "T270 5 0 660 15 " + txtCodArt.getText().toString() +
                                "\n" + "\n" +
                                "BARCODE-TEXT OFF " +
                                "\n" + "\n" +
                                "VBARCODE 128 1 0 30 60 280  " + ean +
                                "\n" + "\n" +
                                "T270 5 0 60 65 " + ean +
                                "\n" + "\n" +
                                "T270 0 2 120 15 " + //printPAK +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }else{
                        cpclData = "! 0 0 0 730 1 " +
                                "\r" + "\n" +
                                //"\n" + "\n" +  "CENTER" + "\n" + "\n" +
                                "TEXT 4 3 220 110 "+a+" " + przV.replace(".",",") +
                                "\n" + "\n" + "\n" + "\n" +
                                "TEXT 7 0 165 40 " + desc1 +
                                "\n" + "\n" +
                                "TEXT 7 0 165 65 " + desc2 +
                                "\n" + "\n" +
                                "TEXT 5 0 165 5 " + txtCodArt.getText().toString() +
                                "\n" + "\n" +
                                "BARCODE-TEXT OFF " +
                                "\n" + "\n" +
                                "BARCODE 128 1 0 30 165 235  " + ean +
                                "\n" + "\n" +
                                "TEXT 5 0 215 275 " + ean +
                                "\n" + "\n" +
                                "TEXT 0 2 165 205 " + //printPAK +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }
                    break;
/*
                case "Frontalino":
                    cpclData = "! 100 0 0 730 1" +
                            "\n" +
                            "T270 4 1 605 170 " + przV +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 125 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 2 95 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            alias +
                            "\n" +
                            "VBARCODE 128 1 1 40 30 350  " + ean +
                            "\n" + "\n" +
                            "T270 5 0 25 150 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAK +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;

                     */
                case "Etichetta promo":
                    if ("0,00".equals(przPromo) || przPromo.isEmpty()) {
                        Toast.makeText(context, "Articolo non in promozione", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stampaPromo=1;
                    scp = 100 - ((Double.parseDouble(przPromo.replace(",",".")) * 100)/Double.parseDouble(przV.replace(",",".")));
                    scps = scp.toString();
                    if(scps.endsWith("9")){
                        scp += 1;
                    }
                    sc = scp.toString().substring(0, scp.toString().indexOf("."));
                    if(!isNew) {
                        cpclData = "! 0 0 0 285 1" +
                                "TEXT 5 0 40 25 " + przV +
                                "\n" + "BOX 35 35 120 35 0 " + "\n" +
                                "TEXT 4 0 170 25 " + przPromo +
                                "\n" + "\n" + "\n" + "\n" +
                                "TEXT 5 0 40 55 Sc% " + sc + "%" +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 7 0 5 90 " + desc1 +
                                "\n" + "\n" +
                                "TEXT 7 0 5 110 " + desc2 +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 5 0 5 145 " + txtCodArt.getText().toString() +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 5 0 5 175 " + ean +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 7 0 5 195 " + printFP +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }else{
                        cpclData = "! 0 0 0 285 1" +
                                "TEXT 5 0 300 25 " + przV +
                                "\n" + "BOX 285 35 370 35 0 " + "\n" +
                                "TEXT 4 0 420 25 " + przPromo +
                                "\n" + "\n" + "\n" + "\n" +
                                "TEXT 5 0 290 55 Sc% " + sc + "%" +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 7 0 5 90 " + desc1 +
                                "\n" + "\n" +
                                "TEXT 7 0 5 110 " + desc2 +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 5 0 5 145 " + txtCodArt.getText().toString() +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 5 0 5 175 " + ean +
                                "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 7 0 5 195 " + printFP +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }
                    break;
                case "Frontalino promo":
                    if ("0,00".equals(przPromo) || przPromo.isEmpty()) {
                        Toast.makeText(context, "Articolo non in promozione", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //stampaPromo=1;
                    if(txtDesc.getText().toString().length() > 43){
                        desc1 = txtDesc.getText().toString().substring(0,43);
                        desc2 = txtDesc.getText().toString().substring(43);
                    }else{
                        desc1 = txtDesc.getText().toString();
                        desc2 = "";
                    }
                    scp = 100 - ((Double.parseDouble(przPromo.replace(",",".")) * 100)/Double.parseDouble(przV.replace(",",".")));
                    scps = scp.toString();
                    if(scps.endsWith("9")){
                        scp += 1;
                    }
                    sc = scp.toString().substring(0, scp.toString().indexOf("."));
                    if(!isNew){
                        cpclData = "! 0 0 0 730 1 " +
                                "\r" + "\n" +
                                "! U1 COUNTRY LATIN9" +
                                "\r" + "\n" +
                                //"\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "T270 4 3 570 90 "+a+" " + przPromo +
                                "\n" + "\n" + "\n" + "\n" +
                                "T270 7 0 630 15 " + desc1 +
                                "\n" + "\n" +
                                "T270 7 0 605 15 " + desc2 +
                                "\n" + "\n" +
                                "T270 7 0 120 255 Anziche "+a+" " + przV + " Sc. " + sc +"%"+
                                "\n" + "\n" +
                                "T270 5 0 660 15 " + txtCodArt.getText().toString() +
                                "\n" + "\n" +
                                "BARCODE-TEXT OFF " +
                                "\n" + "\n" +
                                "VBARCODE 128 1 0 30 60 280  " + ean +
                                "\n" + "\n" +
                                "T270 5 0 60 65 " + ean +
                                "\n" + "\n" +
                                "T270 0 2 120 15 " + //printPAKP +
                                "\n" + "\n" +
                                "T270 0 2 95 355 Offerta valida " +
                                "\n" + "\n" +
                                "T270 0 2 75 310 " + printFP +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }else{
                        cpclData = "! 0 0 0 730 1 " +
                                "\r" + "\n" +
                                "! U1 COUNTRY LATIN9" +
                                "\r" + "\n" +
                                //"\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                                "TEXT 4 3 220 110 "+a+" " + przPromo.replace(".",",") +
                                "\n" + "\n" + "\n" + "\n" +
                                "TEXT 7 0 165 40 " + desc1 +
                                "\n" + "\n" +
                                "TEXT 7 0 165 65 " + desc2 +
                                "\n" + "\n" +
                                "TEXT 5 0 165 5 " + txtCodArt.getText().toString() +
                                "\n" + "\n" +
                                "TEXT 7 0 375 205 Anziche "+a+" " + przV + " Sc. " + sc +"%"+
                                "\n" + "\n" +
                                "BARCODE-TEXT OFF " +
                                "\n" + "\n" +
                                "BARCODE 128 1 0 30 165 235  " + ean +
                                "\n" + "\n" +
                                "TEXT 5 0 215 275 " + ean +
                                "\n" + "\n" +
                                "TEXT 0 2 165 205 " + //printPAK +
                                "\n" + "\n" +
                                "TEXT 0 2 510 250 Offerta valida " +
                                "\n" + "\n" +
                                "TEXT 0 2 470 275 " + printFP +
                                "\n" + "\n" + "PRINT" + "\n" + "\n";
                    }

                    /*
                    cpclData = "! 100 0 0 730 1" +
                            "\n" +
                            "T270 4 3 500 250 " + przP +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 585 20 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 3 560 20 " + desc2 +
                            "\n" + "\n" +
                            "T270 0 3 100 80 " + przV +
                            "\n" + "\n" +
                            "BOX 91 75 91 180 0 " +
                            "\n" + "\n" +
                            "T270 0 3 70 80 Sc% " + sc +"%"+
                            "\n" + "\n" +
                            "T270 5 0 525 20 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            alias +
                            "\n" +
                            "VBARCODE EAN13 2 1 40 60 280  " + ean +
                            "\n" + "\n" +
                            "T270 5 0 35 20 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAK +
                            "\n" + "\n" +
                            "T270 0 2 0 250 Offerta " + printFP +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";

                     */
                    /*
                    cpclData = "! 80 0 0 720 1" +
                            "\n" +
                            "T270 4 1 255 150  OFFERTA " +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 165 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 3 145 40 " +
                            desc2 +
                            "\n" + "\n" +
                            "T270 0 5 100 10 " +
                            "\n" + "\n" +
                            "T270 0 5 100 40 " + przV +
                            "\n" + "\n" +
                            "LINE 70 5 85 150  1 " +
                            "\n" + "\n" +
                            "T270 4 1 135 350 " + przP +
                            "\n" + "\n" +
                            "T270 0 2 40 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            "T270 5 0 40 250 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAKP +
                            "\n" + "\n" +
                            "T270 0 2 0 250 " + printFP +
                            "\n" + "\n" +
                            "PRINT" +
                            "\n" + "\n";

                     */
                    break;
                case "Etic. segnacollo estesa":

                    cpclData = "! 5 0 0 540 1" +
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
                            //"TEXT 5 0 570 295 Colli: " + colli +
                            "\n" + "\n" +
                            "TEXT 5 0 570 335 Imb.: " + insQtaSpunta.getText().toString() +
                            "\n" + "\n" +
                            "TEXT 5 0 300 335 Data: " + today +
                            "\n" + "\n" + "\n" + "\n" +
                            "B 128 1 0 60 24 245 " + txtInsEAN.getText().toString() +
                            "\n" + "\n" + "\n" + "\n" +
                            "TEXT 5 0 64 315 " + txtInsEAN.getText().toString() +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";

                    break;
                case "Etic. segnacollo ridotta":
                    cpclData = "!LABEL " +
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
                    break;
                default:
                    break;
            }

            for(int i=0; i<qtaStmp; i++){
                connection.write(cpclData.getBytes());
            }

        } catch (Exception e) {

            // Handle communications error here.

            e.printStackTrace();

        }
    }

    private static Double round(double value, int places){
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

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

                List<SpuntaRigaEntity> righe = dao.getRigheByDocumento(idSpuntaDocRoom);
                if (righe.isEmpty()) return "";

                java.util.LinkedHashSet<String> codSet = new java.util.LinkedHashSet<>();
                for (SpuntaRigaEntity r : righe) {
                    if (!r.codArt.isEmpty()) codSet.add(r.codArt);
                }
                if (codSet.isEmpty()) return "";

                StringBuilder inClause = new StringBuilder("(");
                for (String c : codSet) {
                    inClause.append("'").append(c.replace("'", "''")).append("',");
                }
                inClause.deleteCharAt(inClause.length() - 1).append(")");

                String query =
                        "SELECT articolo.nome, articolo.descrizione, " +
                        "(SELECT CAST(ProgressivoArticolo.esistenza AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag4Ric + ") AS esistenza, " +
                        "(SELECT CAST(ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag4Ric + ") AS ordFornitore, " +
                        "(SELECT CAST(ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino AS int) FROM ProgressivoArticolo " +
                        " WHERE MetaArticolo = articolo.id AND da < GETDATE() AND a > GETDATE() AND MetaMagazzino = " + mag4Ric + ") AS ordCliente, " +
                        "(SELECT CAST(scortaMassima AS int) FROM articoloxmagazzino WHERE idArticolo = articolo.id AND idmagazzino = " + mag4Ric + ") AS scorta, " +
                        "(SELECT CAST(ArticoloXListino.prezzo AS decimal(10,2)) FROM articoloxlistino WHERE idArticolo = articolo.id AND idListino = " + idL + ") AS prezzo, " +
                        "(SELECT Ubicazione FROM articoloxmagazzino WHERE idArticolo = articolo.id AND idmagazzino = " + mag4Ric + ") AS ubicazione, " +
                        "(SELECT sottoUbicazione FROM articoloxmagazzino WHERE idArticolo = articolo.id AND idmagazzino = " + mag4Ric + ") AS sottoubicazione " +
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
                        String scStr  = res.getString("scorta");
                        c.scorta      = scStr  != null ? (int) Double.parseDouble(scStr)  : 0;
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

                // ── Precaricamento promo ────────────────────────────────────────
                java.util.Map<String, String[]> promoMap = new java.util.HashMap<>();
                java.util.Map<String, Double> prezziMap = new java.util.HashMap<>();
                for (SpuntaArticoloCacheEntity c : cacheList) { prezziMap.put(c.codArt, c.prz); }

                String condPromo =
                    "Promozione.tipo = '8' AND Articolo.nome IN " + inClause + " " +
                    "AND ( " +
                    "(cast(inizioValidita as date) <= GETDATE() AND cast(fineValidita as date) >= GETDATE()) " +
                    "OR (fineValidita IS NULL AND inizioValidita IS NULL) " +
                    "OR (cast(fineValidita as date) >= GETDATE() AND inizioValidita IS NULL) " +
                    "OR (fineValidita IS NULL AND cast(inizioValidita as date) <= GETDATE()) )";

                // Query 1: promo per articolo (ElementoxPromozione)
                Connection conP1 = connectionClass.CONN(context);
                if (conP1 != null) {
                    try {
                        String qp1 =
                            "SELECT Articolo.nome, Promozione.TipoValoreSconto, " +
                            "CAST(ElementoxPromozione.valore AS decimal(10,2)) AS prezzoPromo, " +
                            "Promozione.priorita, fineValidita, inizioValidita " +
                            "FROM Promozione JOIN ElementoxPromozione ON (Promozione.id = ElementoXPromozione.idPromozione) " +
                            "JOIN Articolo ON (Articolo.id = ElementoxPromozione.idElemento) " +
                            "WHERE " + condPromo + " ORDER BY Articolo.nome, Promozione.priorita DESC";
                        Statement sp1 = conP1.createStatement();
                        ResultSet rp1 = sp1.executeQuery(qp1);
                        while (rp1.next()) {
                            String nome = rp1.getString("nome");
                            if (!promoMap.containsKey(nome)) {
                                double prezzo = prezziMap.getOrDefault(nome, 0.0);
                                String pp = rp1.getInt("TipoValoreSconto") == 0
                                    ? String.format("%.2f", prezzo - (prezzo * rp1.getDouble("prezzoPromo") / 100)).replace(".", ",")
                                    : String.format("%.2f", rp1.getDouble("prezzoPromo")).replace(".", ",");
                                String fine  = rp1.getString("fineValidita")  != null ? rp1.getString("fineValidita")  : "";
                                String iniz  = rp1.getString("inizioValidita") != null ? rp1.getString("inizioValidita") : "";
                                promoMap.put(nome, new String[]{pp, iniz, fine, String.valueOf(rp1.getInt("priorita"))});
                            }
                        }
                        rp1.close(); sp1.close();
                    } finally { try { conP1.close(); } catch (Exception ignored) {} }
                }

                // Query 2: promo per categoria (CategoriaElementoxPromozione)
                Connection conP2 = connectionClass.CONN(context);
                if (conP2 != null) {
                    try {
                        String qp2 =
                            "SELECT Articolo.nome, Promozione.TipoValoreSconto, " +
                            "CAST(CategoriaElementoxPromozione.valore AS decimal(10,2)) AS prezzoPromo, " +
                            "Promozione.priorita, fineValidita, inizioValidita " +
                            "FROM Promozione JOIN CategoriaElementoxPromozione ON (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                            "JOIN Articolo ON (Articolo.idCategoriaArticolo = CategoriaElementoxpromozione.idCategoria) " +
                            "WHERE " + condPromo + " ORDER BY Articolo.nome, Promozione.priorita DESC";
                        Statement sp2 = conP2.createStatement();
                        ResultSet rp2 = sp2.executeQuery(qp2);
                        while (rp2.next()) {
                            String nome = rp2.getString("nome");
                            int pri = rp2.getInt("priorita");
                            String[] existing = promoMap.get(nome);
                            int existingPri = existing != null ? Integer.parseInt(existing[3]) : -1;
                            if (pri > existingPri) {
                                double prezzo = prezziMap.getOrDefault(nome, 0.0);
                                String pp = rp2.getInt("TipoValoreSconto") == 0
                                    ? String.format("%.2f", prezzo - (prezzo * rp2.getDouble("prezzoPromo") / 100)).replace(".", ",")
                                    : String.format("%.2f", rp2.getDouble("prezzoPromo")).replace(".", ",");
                                String fine  = rp2.getString("fineValidita")  != null ? rp2.getString("fineValidita")  : "";
                                String iniz  = rp2.getString("inizioValidita") != null ? rp2.getString("inizioValidita") : "";
                                promoMap.put(nome, new String[]{pp, iniz, fine, String.valueOf(pri)});
                            }
                        }
                        rp2.close(); sp2.close();
                    } finally { try { conP2.close(); } catch (Exception ignored) {} }
                }

                // Applica promo alla lista cache
                for (SpuntaArticoloCacheEntity c : cacheList) {
                    String[] promo = promoMap.get(c.codArt);
                    if (promo != null) {
                        c.przPromo    = promo[0];
                        c.inizioPromo = promo[1];
                        c.finePromo   = promo[2];
                    }
                }

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
            przPromo = "0,00";
            inizioPromo = "";
            finePromo = "";
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbSearchArt.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            if(isSuccess) {
                if(idSpuntaDocRoom <= 0) {
                    findPromo();
                }

                if(idSpuntaDocRoom > 0) {
                    // Usa Room per leggere lo stato dell'articolo
                    if(rbSS.isChecked()){
                        txtCodArt.setText(codiceArt);
                        txtDesc.setText(description);
                        if(of!=null){ txtOrdF.setText(of.toString()); }else{ txtOrdF.setText("0"); }
                        if(oc!=null){ txtOrdC.setText(oc.toString()); }else{ txtOrdC.setText("0"); }
                        if(esistenza!=null){ txtEsSp.setText(esistenza.toString()); }else{ txtEsSp.setText("0"); }
                        txtScorta.setText(scorta.toString());
                        if(prz!=null){ txtPrzArt.setText("€ " + prz.toString()); }else{ txtPrzArt.setText("€ N/A"); }

                        final String artCode = codiceArt;
                        leggiStatoArticoloDaRoom(artCode, () -> {
                            new Thread(() -> {
                                List<SpuntaRigaEntity> righe = appDb.spuntaDao().getRigheByCodArt(idSpuntaDocRoom, artCode);
                                boolean pres = !righe.isEmpty();
                                runOnUiThread(() -> {
                                    if(pres){
                                        txtInsEAN.setEnabled(false);
                                        Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                                        Date date = new Date();
                                        Calendar calendar = GregorianCalendar.getInstance();
                                        calendar.setTime(date);
                                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                        Integer minutes = calendar.get(Calendar.MINUTE);
                                        String nMinutes;
                                        if(minutes.toString().length()==1){ nMinutes = "0"+minutes.toString(); }else{ nMinutes = minutes.toString(); }
                                        String timing = hours + ":" + nMinutes;

                                        aggiornaSpuntaInRoom(txtCodArt.getText().toString(), qtaSpuntata,
                                                txtInsEAN.getText().toString(), timing, 0,
                                                segnacollo != null ? segnacollo.getText().toString() : "", () -> {
                                            // UI reset eseguito dopo la scrittura Room (nessuna race condition)
                                            if(chkEtic.isChecked()){
                                                if(printer.equals("ZEBRA")){
                                                    if(!connection.isConnected()){
                                                        connection = new BluetoothConnection(bt);
                                                        try{ connection.open(); } catch (Exception e) { e.printStackTrace(); }
                                                    }
                                                    stampaZebra(przPromo, inizioPromo, finePromo, false, 1);
                                                }else{
                                                    if(!connection.isConnected()){
                                                        connection = new BluetoothConnection(bt);
                                                        try{ connection.open(); } catch (Exception e) { e.printStackTrace(); }
                                                    }
                                                    stampaZebra(przPromo, inizioPromo, finePromo, true, 1);
                                                }
                                            }
                                            txtInsEAN.setEnabled(true);
                                            txtInsEAN.setText("");
                                            insQtaSS.setText("1");
                                            txtInsEAN.setFocusableInTouchMode(true);
                                            txtInsEAN.requestFocus();
                                            try {
                                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                                play.play();
                                            } catch (Exception e) { e.printStackTrace(); }
                                        });
                                    }else{
                                        articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                                    }
                                });
                            }).start();
                        });
                    }else{
                        // rbSM - mostra info e lascia confermare manualmente
                        btnBackArt.setVisibility(View.VISIBLE);
                        btnNextArt.setVisibility(View.VISIBLE);
                        txtCodArt.setText(codiceArt);
                        txtDesc.setText(description);
                        if(of!=null){ txtOrdF.setText(of.toString()); }else{ txtOrdF.setText("0"); }
                        if(oc!=null){ txtOrdC.setText(oc.toString()); }else{ txtOrdC.setText("0"); }
                        if(esistenza!=null){ txtEsSp.setText(esistenza.toString()); }else{ txtEsSp.setText("0"); }
                        txtScorta.setText(scorta.toString());
                        if(prz!=null){ txtPrzArt.setText("€ " + prz.toString()); }else{ txtPrzArt.setText("€ N/A"); }

                        final String artCode = codiceArt;
                        leggiStatoArticoloDaRoom(artCode, () -> {
                            new Thread(() -> {
                                List<SpuntaRigaEntity> righe = appDb.spuntaDao().getRigheByCodArt(idSpuntaDocRoom, artCode);
                                boolean pres = !righe.isEmpty();
                                runOnUiThread(() -> {
                                    if(pres){
                                        txtInsEAN.setEnabled(false);
                                        insNColliSpunta.setVisibility(View.VISIBLE);
                                        insNColliSpunta.setEnabled(true);
                                        lblColli.setVisibility(View.VISIBLE);
                                    }else{
                                        articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                                    }
                                });
                            }).start();
                        });
                    }
                } else {
                    // Fallback: usa Excel
                    XSSFWorkbook workbook;

                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        if(rbSS.isChecked()){
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
                            txtScorta.setText(scorta.toString());
                            if(prz!=null){
                                txtPrzArt.setText("€ " + prz.toString());
                            }else{
                                txtPrzArt.setText("€ N/A");
                            }
                            Boolean presente = false;
                            Integer totQta = 0;

                        int i = 0;
                        String qtaLP = "0";
                        while(workbook.getSheetAt(0).getRow(i) != null){
                            Row row = workbook.getSheetAt(0).getRow(i);
                            if(row.getCell(0).getStringCellValue().equals(codiceArt)){
                                Integer newQta = Integer.parseInt(qtaLP) + Integer.parseInt(row.getCell(6).getStringCellValue());
                                totQta = totQta + Integer.parseInt(row.getCell(5).getStringCellValue());
                                qtaLP = newQta.toString();
                                presente = true;
                            }
                            i++;
                        }
                        txtQtaLP.setText(qtaLP);

                            String sqta = totQta.toString();
                            qtaDoc.setText(sqta);
                            if(presente){
                                txtInsEAN.setEnabled(false);
                                Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                                Integer lastIndex = -1;
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
                                i=0;
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
                                            Integer qtaRimasta = (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))-qtaSpuntata;
                                            Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + qtaSpuntata;
                                            row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                            qtaSpuntata = 0;
                                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                            row.createCell(1).setCellValue(txtDesc.getText().toString());
                                            row.createCell(9).setCellValue(timing);
                                            //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                            //aggDoc.execute();
                                        }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))==0 && qtaSpuntata>0){

                                        }else if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))<qtaSpuntata && qtaSpuntata>0){
                                            row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                            qtaSpuntata = qtaSpuntata - (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                            Integer qtaParzialeSpunta = Integer.parseInt(row.getCell(6).getStringCellValue()) + (Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()));
                                            row.createCell(6).setCellValue(qtaParzialeSpunta.toString());
                                            row.createCell(9).setCellValue(timing);
                                            //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaParzialeSpunta, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                            //aggDoc.execute();
                                        }
                                    }
                                    i++;
                                }

                                if(qtaSpuntata>0 && lastIndex != -1){
                                    Row row = workbook.getSheetAt(0).getRow(lastIndex);
                                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                    row.createCell(1).setCellValue(txtDesc.getText().toString());
                                    Integer qtaUltima = qtaSpuntata + Integer.parseInt(row.getCell(6).getStringCellValue());
                                    row.createCell(6).setCellValue(qtaUltima.toString());
                                    row.createCell(9).setCellValue(timing);
                                    //IniziaSpuntaNeg.AggDoc aggDoc = new AggDoc(qtaUltima, txtInsEAN.getText().toString(), timing, txtCodArt.getText().toString(), row.getCell(8).getStringCellValue());
                                    //aggDoc.execute();
                                }else if(qtaSpuntata>0 && lastIndex == -1){
                                    Row row = workbook.getSheetAt(0).createRow(i);
                                    row.createCell(2).setCellValue(txtInsEAN.getText().toString());
                                    row.createCell(1).setCellValue(txtDesc.getText().toString());
                                    row.createCell(0).setCellValue(txtCodArt.getText().toString());
                                    row.createCell(6).setCellValue(qtaSpuntata.toString());
                                    Integer newDiff = Integer.parseInt(qtaSpuntata.toString());
                                    row.createCell(7).setCellValue(newDiff);
                                    row.createCell(5).setCellValue("0");
                                    row.createCell(8).setCellValue("");
                                    row.createCell(9).setCellValue(timing);

                                    row.createCell(11).setCellValue("");
                                    row.createCell(13).setCellValue("");
                                    row.createCell(14).setCellValue("");

                                    insert = "('"+txtCodArt.getText().toString()+"', '"+txtDesc.getText().toString()+"', '"+txtInsEAN.getText().toString()+"', '', '', 0, "+qtaSpuntata+", 0, '', '"+timing+"', '"+nomeP+"', '"+utente+"')";
                                    //IniziaSpuntaNeg.CreaDoc creaDoc = new CreaDoc();
                                    //creaDoc.execute();
                                }
                                Integer qtaS = 0;
                                Integer qtaD = 0;
                                i=0;
                                while(workbook.getSheetAt(0).getRow(i) != null){
                                    Row row = workbook.getSheetAt(0).getRow(i);
                                    if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                                        qtaS = qtaS + Integer.parseInt(row.getCell(6).getStringCellValue());
                                        qtaD = qtaD + Integer.parseInt(row.getCell(5).getStringCellValue());
                                    }
                                    i++;
                                }
                                ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                                if(qtaS < qtaD){
                                    back.setBackgroundColor(Color.RED);
                                }else if(qtaS > qtaD){
                                    back.setBackgroundColor(Color.YELLOW);
                                }else{
                                    back.setBackgroundColor(Color.GREEN);
                                }
                                txtQtaLP.setText(qtaS.toString());
                                if(chkEtic.isChecked()){
                                    if(printer.equals("ZEBRA")){
                                        if(!connection.isConnected()){
                                            connection = new BluetoothConnection(bt);
                                            try{
                                                connection.open();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        stampaZebra(przPromo, inizioPromo, finePromo, false, 1);
                                    }else{
                                        if(!connection.isConnected()){
                                            connection = new BluetoothConnection(bt);
                                            try{
                                                connection.open();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        stampaZebra(przPromo, inizioPromo, finePromo, true, 1);
                                    }
                                }
                                txtInsEAN.setEnabled(true);
                                txtInsEAN.setText("");
                                insQtaSS.setText("1");
                                txtInsEAN.setFocusableInTouchMode(true);
                                txtInsEAN.requestFocus();
                                try {
                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                    play.play();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                            }
                        }else{
                            btnBackArt.setVisibility(View.VISIBLE);
                            btnNextArt.setVisibility(View.VISIBLE);
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
                            txtScorta.setText(scorta.toString());
                            if(prz!=null){
                                txtPrzArt.setText("€ " + prz.toString());
                            }else{
                                txtPrzArt.setText("€ N/A");
                            }
                            Boolean presente = false;
                            Integer totQta = 0;
                            int i = 0;
                            String qtaLP = "0";
                            while(workbook.getSheetAt(0).getRow(i) != null){
                                Row row = workbook.getSheetAt(0).getRow(i);
                                if(row.getCell(0).getStringCellValue().equals(codiceArt)){
                                    Integer newQta = Integer.parseInt(qtaLP) + Integer.parseInt(row.getCell(6).getStringCellValue());
                                    totQta = totQta + Integer.parseInt(row.getCell(5).getStringCellValue());
                                    qtaLP = newQta.toString();
                                    presente = true;
                                }
                                i++;
                            }
                            txtQtaLP.setText(qtaLP);
                            String sqta = totQta.toString();
                            qtaDoc.setText(sqta);
                            if(presente){
                                txtInsEAN.setEnabled(false);
                                insNColliSpunta.setVisibility(View.VISIBLE);
                                insNColliSpunta.setEnabled(true);
                                lblColli.setVisibility(View.VISIBLE);
                            }else{
                                articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                            }
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
                } // fine else Excel fallback
            }else{
                alertArt("Errore!","Articolo non presente nel database, inserisci una nota");
            }
            if(giaPremuto == 1){
                giaPremuto = 0;
            }
            pbSearchArt.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        codiceArt   = cached.codArt;
                        description = cached.desc;
                        esistenza   = cached.esistenza;
                        of          = cached.ordFornitore;
                        oc          = cached.ordCliente;
                        scorta      = cached.scorta;
                        prz         = cached.prz;
                        if (!cached.przPromo.isEmpty()) {
                            przPromo    = cached.przPromo;
                            inizioPromo = cached.inizioPromo;
                            finePromo   = cached.finePromo;
                        }
                        isSuccess   = true;
                        String foundEan = artDao.findFirstEanByCodArt(codiceArt);
                        ean = foundEan != null ? foundEan : "";
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
                            scorta      = 0;
                            prz         = 0.0;
                            isSuccess   = true;
                            String foundEan = artDao.findFirstEanByCodArt(codiceArt);
                            ean = foundEan != null ? foundEan : "";
                        }
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
                    String query = "select nome, articolo.descrizione, " +
                            "(select cast (ProgressivoArticolo.esistenza as int) as esistenza from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = "+mag4Ric+") as esistenza, \n" +
                            "(select cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = "+mag4Ric+") as OrdinatoFornitoreArticoloXMagazzino,\n" +
                            "(select cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a > GETDATE() and MetaMagazzino = "+mag4Ric+") as OrdinatoClienteArticoloXMagazzino, \n" +
                            "(select cast (scortaMassima as int) from articoloxmagazzino where articoloxmagazzino.idarticolo=articolo.id and idmagazzino="+mag4Ric+") as scorta, \n" +
                            "(select cast (ArticoloXListino.prezzo as decimal (10,2)) from articoloxlistino where idArticolo = articolo.id and idListino = "+idL+") as prezzo, (select top 1 [alias].codice from [alias] where idArticolo = articolo.id) as ean, " +
                            "(select idListino from articoloxlistino where idArticolo = articolo.id and idListino = "+idL+") as idListino " +
                            "from articolo left join [alias] on ([alias].idArticolo = articolo.id) " +
                            "where codice = '"+ findThis +"' or nome = '"+ findThis +"' " +
                            "OR codice = '"+ findThis.trim() +"' or nome = '"+ findThis.trim() +"'  ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int ok = 0;
                    int i = 0;
                    while(res.next()) {
                        if(res.getInt("idListino") == idL && ok == 0){
                            ok = 1;
                            prz = res.getDouble("prezzo");
                            esistenza = res.getInt("esistenza");
                            of = res.getInt("OrdinatoFornitoreArticoloXMagazzino");
                            oc = res.getInt("OrdinatoClienteArticoloXMagazzino");
                            if(res.getString("scorta")!=null){
                                scorta = res.getInt("scorta");
                            }else{
                                scorta = 0;
                            }
                        }
                        if(i==0){
                            if(res.getString("ean") != null){
                                ean = res.getString("ean");
                            }else{
                                ean = "";
                            }
                        }
                        codiceArt = res.getString("nome");
                        description = res.getString("descrizione");
                        i++;
                    }
                    if (codiceArt != null) {
                        if(ok == 0){
                            prz = 0.00;
                            esistenza = 0;
                            of = 0;
                            oc = 0;
                            scorta = 0;
                        }
                        isSuccess = true;
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

        public void prezziPromoCat(int priorita){
            Connection con = null;
            ResultSet res;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT fineValidita, iniziovalidita, Articolo.nome, Promozione.TipoValoreSconto, cast(CategoriaElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                            "Promozione.priorita, fineValidita, promozione.nome as pName " +
                            "FROM Promozione join CategoriaElementoxPromozione on (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                            "join Articolo on (Articolo.idCategoriaArticolo = CategoriaElementoxpromozione.idCategoria) " +
                            "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and Articolo.nome = '" + codiceArt + "' " +
                            "and Promozione.tipo = '8' " +
                            "or fineValidita is null and inizioValidita is null and Articolo.nome = '" + codiceArt + "' " +
                            "and Promozione.tipo = '8' " +
                            "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and Articolo.nome = '" + codiceArt + "' " +
                            "and Promozione.tipo = '8' " +
                            "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and Articolo.nome = '" + codiceArt + "' " +
                            "and Promozione.tipo = '8' " +
                            "ORDER BY Promozione.priorita desc";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        if(res.getInt("priorita") > priorita){
                            if(res.getInt("TipoValoreSconto") == 0){
                                Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                                przPromo = pP.toString();
                            }else{
                                przPromo = res.getString("prezzoPromo");
                            }
                            if(res.getString("fineValidita")!=null){
                                finePromo = res.getString("fineValidita");
                            }
                            if(res.getString("inizioValidita")!=null){
                                inizioPromo = res.getString("inizioValidita");
                            }
                        }
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
        }

        protected void findPromo() {
            Connection con = null;
            ResultSet res;
            int priorita = 0;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT fineValidita, inizioValidita, Articolo.nome, Promozione.TipoValoreSconto, cast(ElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                                "Promozione.priorita, fineValidita, promozione.nome as pName " +
                                "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                                "join Articolo on (Articolo.id = ElementoxPromozione.idElemento) " +
                                "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and Articolo.nome = '"+codiceArt+"' " +
                                "and Promozione.tipo = '8'" +
                                "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+codiceArt+"' " +
                                "and Promozione.tipo = '8' " +
                                "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and Articolo.nome = '"+codiceArt+"' " +
                                "and Promozione.tipo = '8' " +
                                "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and Articolo.nome = '"+codiceArt+"' " +
                                "and Promozione.tipo = '8' " +
                                "ORDER BY Promozione.priorita desc";

                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        priorita = res.getInt("priorita");
                        if(res.getInt("TipoValoreSconto") == 0){
                            Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                            przPromo = pP.toString();
                        }else{
                            przPromo = res.getString("prezzoPromo");
                        }
                        if(res.getString("fineValidita")!=null){
                            finePromo = res.getString("fineValidita");
                        }
                        if(res.getString("inizioValidita")!=null){
                            inizioPromo = res.getString("inizioValidita");
                        }
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
            prezziPromoCat(priorita);
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
    /*

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

        String ean, timing, codArt, nDoc;

        public AggDoc(Integer qta, String ean, String timing, String codArt, String nDoc){
            this.qta = qta;
            this.ean = ean;
            this.timing = timing;
            this.codArt = codArt;
            this.nDoc = nDoc;
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
                    String query = "UPDATE mcSpunte " +
                            "SET qtaS = "+qta+", alias = '"+ean+"', sparata = '"+timing+"', diff = "+qta+"-qtaDoc " +
                            "WHERE codArt = '"+codArt+"' and nDoc = '"+nDoc+"' and palmare = '"+nomeP+"' and utente = '"+utente+"' ";
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
*/

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
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNeg.this)
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
                    String query = "select articolo.nome as codicearticolo, RigaDocumentoCommerciale.quantita, RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie " +
                            "from RigaDocumentoCommerciale join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "join articolo on RigaDocumentoCommerciale.idarticolo = articolo.id " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' and articolo.nome like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' and RigaDocumentoCommerciale.stato in (0,3) and RigaDocumentoCommerciale.idMagazzinoDestinazione = "+mag4Ric+" " +
                            "order by RigaDocumentoCommerciale.stato desc ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess=true;
                        numOX.add(res.getString("numero"));
                        dataC.add(res.getString("dataConsegna").substring(0,11));
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
}