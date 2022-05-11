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
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.zebra.sdk.comm.BluetoothConnection;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class IniziaSpunta extends AppCompatActivity{

    ConnectionClass connectionClass;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
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
    String doc4Print = "";
    String data4Print = "";
    String bt = "";
    Spinner spinner;
    String fornitore = "";
    String fileName = "";
    ProgressBar pbSearchArt;
    String findThis, ubiDef, subiDef;
    Double prz;
    Integer mag, listino, esistenza, of, oc;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, lblUbic, lblSubic, txtPrzArt, txtEsSp, txtQtaLP, txtUbiDef, txtOrdF, txtOrdC, txtUbiRead, txtSubiDef, txtSubiRead;
    Button search, btnNextArt, btnBackArt, btnFine, addEan;
    EditText txtInsEAN, insNColliSpunta, insQtaSpunta, insUbicSpunta, insSubicSpunta;
    Context context;
    int giaPremuto = 0;
    int totColli = 0;
    String tipoDoc ="";
    com.zebra.sdk.comm.Connection connection;
    SharedPreferences prefs;

    public void inizializzaFile(){

        codArticolo = ((MyApplication) this.getApplication()).getCodArt();
        qta = ((MyApplication) this.getApplication()).getQuantita();
        idDoc = ((MyApplication) this.getApplication()).getID();
        descrizioni = ((MyApplication) this.getApplication()).getDesc();
        ArrayList<String> idXArt = new ArrayList<>();
        ArrayList<String> qtaArtXID = new ArrayList<>();
        ArrayList<String> tempCodArt = new ArrayList<>();
        ArrayList<String> tempNumDoc = new ArrayList<>();
        ArrayList<String> tempDesc = new ArrayList<>();
        for(int i=0; i<codArticolo.size(); i++) {
            Boolean ce = false;
            if (i == 0) {
                idXArt.add(idDoc.get(i));
                tempCodArt.add(codArticolo.get(i));
                tempNumDoc.add(numDoc.get(i));
                qtaArtXID.add(qta.get(i));
                tempDesc.add(descrizioni.get(i));
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
                Row testataB = barcodes.createRow(i);
                testataB.createCell(0).setCellValue("Codice articolo");
                testataB.createCell(1).setCellValue("EAN");
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
            row.createCell(10).setCellValue("");
            row.createCell(11).setCellValue("");
            row.createCell(12).setCellValue("0");
        }

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

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            tipoDoc = extras.getString("tipoDoc");
            fornitore = extras.getString("fornitore");
        }

        connectionClass = new ConnectionClass();

        context = this;

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        bt = p.getString("PrinterIp","");
        numDoc = ((MyApplication) this.getApplication()).getNum();
        serieDoc = ((MyApplication) this.getApplication()).getSerie();
        String nomeP = p.getString("NomePalm","");
        String docsName = "";
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

        File fileEx = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        if(fileEx.exists()){
            docPres("Attenzione!","Esiste già un documento per questa spunta");
        }else{
            inizializzaFile();
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

        pbSearchArt.setVisibility(View.GONE);
        btnBackArt.setVisibility(View.GONE);
        btnNextArt.setVisibility(View.GONE);
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

        txtInsEAN.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = txtInsEAN.getText().toString();
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
                alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
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
                FindArt cercaArt = new FindArt();
                cercaArt.execute("");
            }
        });
        addEan.setOnClickListener(v -> {
            aggiungiEan("","");
        });
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

    private void alertDisplayer2(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer3("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
                })
                .setNeutralButton("Inserisci nota",((dialog, which) -> {
                    inserisciNota(txtCodArt.getText().toString());
                }))
                .setPositiveButton("Si", (dialog, which) -> {
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
                        while(workbook.getSheetAt(0).getRow(i) != null){
                            Row row = workbook.getSheetAt(0).getRow(i);
                            if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                                lastIndex = i;
                                if((Integer.parseInt(row.getCell(5).getStringCellValue())-Integer.parseInt(row.getCell(6).getStringCellValue()))>=qtaSpuntata && qtaSpuntata>0){
                                    doc4Print = row.getCell(8).getStringCellValue();
                                    nColli = qtaSpuntata/Integer.parseInt(insQtaSpunta.getText().toString());
                                    stampaZebra(nColli.toString());
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
                                    stampaZebra(nColli.toString());
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
                            stampaZebra(nColli.toString());
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
                            stampaZebra(nColli.toString());
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
                        }
                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    totColli += Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnFine.setVisibility(View.VISIBLE);
                    btnNextArt.setVisibility(View.GONE);
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
                    dialog.cancel();
                    txtInsEAN.setFocusableInTouchMode(true);
                    txtInsEAN.requestFocus();
                });
        AlertDialog ok = builder.create();
        ok.show();
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
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpunta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
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
                                stampaZebra(nColli.toString());
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
                                stampaZebra(nColli.toString());
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
                            stampaZebra(nColli.toString());
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
                            stampaZebra(nColli.toString());
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
                        }

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stampaZebra("");
                    totColli += Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
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
                    review.putExtra("fileName", fileName);
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
            alertDisplayer2("Attenzione!","Ci sono altri articoli da spuntare?");
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
            alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
        });
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
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
                cpclData = "! 5 0 0 430 1" +
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
            Boolean presente = false;
            if(isSuccess) {

                XSSFWorkbook workbook;

                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                btnBackArt.setVisibility(View.VISIBLE);
                btnNextArt.setVisibility(View.VISIBLE);
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

                Integer totQta = 0;
                    int i = 0;
                    String qtaLP = "0";

                    while(workbook.getSheetAt(0).getRow(i) != null){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        if(row.getCell(0).getStringCellValue().equals(codiceArt)){
                            Integer newQta = Integer.parseInt(qtaLP) + Integer.parseInt(row.getCell(6).getStringCellValue());
                            totQta = totQta + Integer.parseInt(row.getCell(5).getStringCellValue());
                            qtaLP = newQta.toString();
                            txtUbiRead.setText(row.getCell(3).getStringCellValue());
                            txtSubiRead.setText(row.getCell(4).getStringCellValue());
                            insUbicSpunta.setText(row.getCell(3).getStringCellValue());
                            insSubicSpunta.setText(row.getCell(4).getStringCellValue());
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
                String sqta = totQta.toString();
                qtaDoc.setText(sqta);
                file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
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

            }else{
                alertArt("Errore!","Articolo non presente nel database, inserisci una nota");
            }
            if(giaPremuto == 1){
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
                    String query = "select nome, articolo.descrizione, \n" +
                            "(select cast (ProgressivoArticolo.esistenza as int) as esistenza from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = 1) as esistenza, \n" +
                            "(select cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = 1) as OrdinatoFornitoreArticoloXMagazzino,\n" +
                            "(select cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a > GETDATE() and MetaMagazzino = 1) as OrdinatoClienteArticoloXMagazzino, \n" +
                            "(select prezzo from articoloxlistino where idArticolo = articolo.id and idListino = 1) as prezzo, \n" +
                            "(select sottoUbicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=1) as sottoubicazione, \n" +
                            "(select Ubicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=1) as ubicazione, \n" +
                            "(select idListino from articoloxlistino where idArticolo = articolo.id and idListino = 1) as idListino\n" +
                            "from articolo left join alias on (alias.idArticolo = articolo.id)\n" +
                            "where nome = '"+findThis+"' or alias.codice = '"+findThis+"' " +
                            "or nome = '"+findThis.trim()+"' or alias.codice = '"+findThis.trim()+"' " +
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
                        //z = "Ordine trovato";
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

    }
}