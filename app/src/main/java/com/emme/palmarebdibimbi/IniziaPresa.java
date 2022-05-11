package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class IniziaPresa extends AppCompatActivity {

    ConnectionClass connectionClass;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> serieDoc = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> ubicazione = new ArrayList<>();
    ArrayList<String> esistenza = new ArrayList<>();
    ArrayList<String> impegnati = new ArrayList<>();
    ArrayList<String> sottoubicazione = new ArrayList<>();
    TextView txtCodArtPresa, txtUbicPresa, txtQtaPresa, txtDescPresa, txtSubicPresa, txtesPresa, txtEanPresa, txtImpPresa, txtCount, txtStore;
    Button btnFindArtPresa, btnFinePresa;
    ImageButton btnNext, btnPrev;
    EditText insCodArt, insQtaPresa;
    Context context;
    String findThis;
    String trovaQuesto;
    String ean = null;
    String magazzino;
    String docsName ="";
    String fornitore = "";
    String tipoDoc = "";
    String fileName = "";
    String timing = "";
    int giaPremuto = 0;
    int noMatch = 0; int inDoc = 0;
    int listino = 0; int mag = 0; int i, j, f;
    int magRif;
    SharedPreferences prefs;

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void inizializzaFile(){

        codArticolo = ((MyApplication) this.getApplication()).getCodArt();
        alias = ((MyApplication) this.getApplication()).getAlias();
        qta = ((MyApplication) this.getApplication()).getQuantita();
        idDoc = ((MyApplication) this.getApplication()).getID();
        numDoc = ((MyApplication) this.getApplication()).getNum();
        descrizioni = ((MyApplication) this.getApplication()).getDesc();
        ubicazione = ((MyApplication) this.getApplication()).getUbic();
        sottoubicazione = ((MyApplication) this.getApplication()).getSubic();
        esistenza = ((MyApplication) this.getApplication()).getEsistenza();
        impegnati = ((MyApplication) this.getApplication()).getImpegnati();

        File file = new File("/storage/emulated/0/NAS/PresaGen", fileName);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Presa"); //Creating a sheet

        for (int i = 0; i < codArticolo.size(); i++) {

            Row row = sheet.createRow(i + 1);
            if (i == 0) {
                Row testata = sheet.createRow(i);
                testata.createCell(0).setCellValue("Codice articolo");
                testata.createCell(1).setCellValue("Descrizione");
                testata.createCell(2).setCellValue("Alias");
                testata.createCell(3).setCellValue("Ubicazione");
                testata.createCell(4).setCellValue("Sottoubicazione");
                testata.createCell(5).setCellValue("Quantita documento");
                testata.createCell(6).setCellValue("Quantita presa");
                testata.createCell(7).setCellValue("Differenza");
                testata.createCell(8).setCellValue("N. Doc");
                testata.createCell(9).setCellValue("Note");
                testata.createCell(10).setCellValue("Magazzino");
                testata.createCell(11).setCellValue("Sparata");
                testata.createCell(12).setCellValue("Esistenza");
                testata.createCell(13).setCellValue("Impegnati");
                testata.createCell(14).setCellValue("Sparata");
            }
            row.createCell(0).setCellValue(codArticolo.get(i));
            row.createCell(1).setCellValue(descrizioni.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(alias.get(i));
            num2.setCellType(CellType.STRING);
            row.createCell(3).setCellValue(ubicazione.get(i));
            row.createCell(4).setCellValue(sottoubicazione.get(i));
            row.createCell(5).setCellValue(qta.get(i));
            row.createCell(6).setCellValue("0");
            int risultato = Integer.parseInt(qta.get(i)) - (Integer.parseInt(qta.get(i))*2);
            row.createCell(7).setCellValue(risultato);
            row.createCell(8).setCellValue(numDoc.get(i));
            row.createCell(9).setCellValue("");
            row.createCell(10).setCellValue(magazzino);
            row.createCell(11).setCellValue("");
            row.createCell(12).setCellValue(esistenza.get(i));
            row.createCell(13).setCellValue(impegnati.get(i));
            row.createCell(14).setCellValue("");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
    }

    @Override
    public void onBackPressed() {
        alertDisplayer("Attenzione!","Sei sicuro di voler abbandonare la pagina di spunta? Verrai riportato alla home!");
    }


    private void deleteDoc(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.cancel();
                    docPres("Attenzione!","Esiste già un documento per questa presa");
                })
                .setPositiveButton("SI", (dialog, which) -> {
                    File fileEx = new File("/storage/emulated/0/NAS/PresaGen", fileName);
                    fileEx.delete();
                    inizializzaFile();
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void docPres(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaPresa.this)
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

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaPresa.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void esistenzaImpegnata(String title,String message){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.alert_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setView(dialoglayout);

        Button close = dialoglayout.findViewById(R.id.btnCloseAlert);
        AlertDialog ok = builder.create();
        ok.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok.dismiss();
                ok.cancel();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_presa);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            magRif = extras.getInt("magRif");
            tipoDoc = extras.getString("tipoDoc");
            fornitore = extras.getString("fornitore");
            magazzino = extras.getString("magazzino");
        }

        connectionClass = new ConnectionClass();
        context = this;

        j = 1;
        i = 1;
        f = -1;

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        numDoc = ((MyApplication) this.getApplication()).getNum();
        serieDoc = ((MyApplication) this.getApplication()).getSerie();
        String nomeP = p.getString("NomePalm","");
        String docsName = "";
        ArrayList<String> tempnDoc = new ArrayList<>();
        for(int z=0; z<numDoc.size(); z++) {
            if(z==0){
                docsName = tipoDoc + "_" + fornitore + "_" + numDoc.get(0) + "_" + serieDoc.get(0);
                tempnDoc.add(numDoc.get(0));
            }else if(!tempnDoc.contains(numDoc.get(z))){
                docsName = docsName + "_" + numDoc.get(z) + "_" + serieDoc.get(z);
                tempnDoc.add(numDoc.get(z));
            }
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        fileName = nomeP+"_presa_"+docsName+"_"+year+".xlsx"; //Name of the file

        File fileEx = new File("/storage/emulated/0/NAS/PresaGen", fileName);
        if(fileEx.exists()){
            docPres("Attenzione!","Esiste già un documento per questa presa");
        }else{
            inizializzaFile();
        }

        txtCount = findViewById(R.id.txtCount);
        txtCodArtPresa = findViewById(R.id.txtCodArtPresa);
        txtUbicPresa = findViewById(R.id.txtWherePresa);
        txtSubicPresa = findViewById(R.id.txtWherePresa2);
        txtQtaPresa = findViewById(R.id.txtHowManyPresa);
        txtStore = findViewById(R.id.txtStorePresa);
        btnNext = findViewById(R.id.btnSuccArt);
        btnPrev = findViewById(R.id.btnPrevArt);
        txtDescPresa = findViewById(R.id.txtDescArtPresa);
        insCodArt = findViewById(R.id.editMatchArt);
        btnFindArtPresa = findViewById(R.id.btnFindArtPresa);
        insQtaPresa = findViewById(R.id.txtInsQtaPresa);
        btnFinePresa = findViewById(R.id.btnFinePresa);
        txtesPresa = findViewById(R.id.txtEsPresa);
        txtEanPresa = findViewById(R.id.txtEanPresa);
        txtImpPresa = findViewById(R.id.txtImpPresa);
        FloatingActionButton btnInfoOX = findViewById(R.id.btnInfoOXP);
        txtStore.setText(magazzino);

        XSSFWorkbook workbook2;
        try {
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/PresaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook2 = new XSSFWorkbook(file);


            txtesPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
            txtEanPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
            txtImpPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
            txtCodArtPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
            txtQtaPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
            txtUbicPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
            txtSubicPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
            txtDescPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(1).getStringCellValue());
            insQtaPresa.setText(workbook2.getSheetAt(0).getRow(i).getCell(6).getStringCellValue());

        txtCount.setText("1/"+(workbook2.getSheetAt(0).getLastRowNum()));

            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook2.write(outFile);
            outFile.close();

            insQtaPresa.setEnabled(false);
            btnPrev.setVisibility(View.INVISIBLE);

            insQtaPresa.setSelectAllOnFocus(true);

            insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

            if(codArticolo.size() == 1){
                btnNext.setVisibility(View.INVISIBLE);
                btnFinePresa.setVisibility(View.VISIBLE);
            }

            trovaQuesto = workbook2.getSheetAt(0).getRow(i).getCell(0).getStringCellValue();
            FindEAN findEan = new FindEAN();
            findEan.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = insCodArt.getText().toString();
                    if(!findThis.equals("")){
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
                        timing = hours + ":" + nMinutes;
                        IniziaPresa.FindArt cercaArt = new IniziaPresa.FindArt();
                        cercaArt.execute("");
                        showSoftKeyboard(insQtaPresa);
                    }
                }
            }
            return false;
        });
        btnFinePresa.setOnClickListener(v -> {
            salvaPresa("Attenzione!", "Sei sicuro di voler concludere e salvare?");
        });
        btnFindArtPresa.setOnClickListener(v -> {
            if(!insCodArt.getText().toString().equals("")){
                timing = "";
                findThis = insCodArt.getText().toString();
                FindArt findArt = new FindArt();
                findArt.execute();
            }
        });
        btnInfoOX.setOnClickListener(v -> {
            InfoOX info = new InfoOX();
            info.execute();
        });
        btnNext.setOnClickListener(v -> {
            XSSFWorkbook workbook;
            try {
                String outFileName = fileName;

                File path = new File("/storage/emulated/0/NAS/PresaGen");

                FileInputStream file = new FileInputStream(new File(path, outFileName));
                workbook = new XSSFWorkbook(file);
            if(i != workbook.getSheetAt(0).getLastRowNum()){

                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0){
                    inDoc = 0;
                    if(noMatch == 1){
                        noMatch = 0;
                        workbook.getSheetAt(0).getRow(f).createCell(6).setCellValue(insQtaPresa.getText().toString());
                        workbook.getSheetAt(0).getRow(f).createCell(14).setCellValue(timing);
                    }else{
                        workbook.getSheetAt(0).getRow(i).createCell(6).setCellValue(insQtaPresa.getText().toString());
                        workbook.getSheetAt(0).getRow(i).createCell(14).setCellValue(timing);
                        i++;
                    }
                    btnPrev.setVisibility(View.VISIBLE);
                    if(i == workbook.getSheetAt(0).getLastRowNum()){
                        btnFinePresa.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                    }else{
                        btnNext.setVisibility(View.VISIBLE);
                    }
                    txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                    txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                    txtEanPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
                    txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                    txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
                    txtQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
                    txtUbicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
                    txtSubicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
                    txtDescPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(1).getStringCellValue());
                    insQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(6).getStringCellValue());
                    insQtaPresa.setEnabled(false);
                    insCodArt.setText("");
                    trovaQuesto = workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue();
                    FindEAN findEan = new FindEAN();
                    findEan.execute();
                    insCodArt.setFocusableInTouchMode(true);
                    insCodArt.requestFocus();
                    hideKeyboard(this);
                    Integer txPresa = Integer.parseInt(txtesPresa.getText().toString()), txImpPresa = Integer.parseInt(txtImpPresa.getText().toString());
                    if(txPresa<=txImpPresa){
                        esistenzaImpegnata("","");
                    }
                }else if(insQtaPresa.isEnabled() && !txtesPresa.getText().toString().equals(txtImpPresa.getText().toString())){
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }else{
                    qtaZero("Attenzione!", "Stai saltando un articolo presente nel documento, continuare?", 1);
                }

            }file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        });
        btnPrev.setOnClickListener(v -> { XSSFWorkbook workbook;
            try {
                String outFileName = fileName;

                File path = new File("/storage/emulated/0/NAS/PresaGen");

                FileInputStream file = new FileInputStream(new File(path, outFileName));
                workbook = new XSSFWorkbook(file);
            if(i != 0){

                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0){
                    inDoc = 0;
                    if(noMatch == 1){
                        noMatch = 0;
                        workbook.getSheetAt(0).getRow(workbook.getSheetAt(0).getLastRowNum()).createCell(6).setCellValue(insQtaPresa.getText().toString());
                        workbook.getSheetAt(0).getRow(workbook.getSheetAt(0).getLastRowNum()).createCell(14).setCellValue(timing);
                    }else{
                        workbook.getSheetAt(0).getRow(i).createCell(6).setCellValue(insQtaPresa.getText().toString());
                        workbook.getSheetAt(0).getRow(i).createCell(14).setCellValue(timing);
                        i--;
                    }
                    btnNext.setVisibility(View.VISIBLE);
                    if (i == 1) {
                        btnPrev.setVisibility(View.INVISIBLE);
                    } else {
                        btnPrev.setVisibility(View.VISIBLE);
                    }
                    txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                    txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                    txtEanPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
                    txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                    txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
                    txtQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
                    txtUbicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
                    txtSubicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
                    txtDescPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(1).getStringCellValue());
                    insQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(6).getStringCellValue());
                    insQtaPresa.setEnabled(false);
                    insCodArt.setText("");
                    trovaQuesto = workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue();
                    FindEAN findEan = new FindEAN();
                    findEan.execute();
                    Integer txPresa = Integer.parseInt(txtesPresa.getText().toString()), txImpPresa = Integer.parseInt(txtImpPresa.getText().toString());
                    if(txPresa<=txImpPresa){
                        esistenzaImpegnata("","");
                    }
                }else if(insQtaPresa.isEnabled() && !txtesPresa.getText().toString().equals(txtImpPresa.getText().toString())){
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }else{
                    qtaZero("Attenzione!", "Stai saltando un articolo presente nel documento, continuare?", 0);
                }
            }if(noMatch == 1){
                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0) {
                    noMatch = 0;
                    txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                    workbook.getSheetAt(0).getRow(workbook.getSheetAt(0).getLastRowNum()).createCell(6).setCellValue(insQtaPresa.getText().toString());
                    btnNext.setVisibility(View.VISIBLE);
                    btnPrev.setVisibility(View.INVISIBLE);
                    txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                    txtEanPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
                    txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                    txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
                    txtQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
                    txtUbicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
                    txtSubicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
                    txtDescPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(1).getStringCellValue());
                    insQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(6).getStringCellValue());
                    insQtaPresa.setEnabled(false);
                    insCodArt.setText("");
                }else{
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }
            }
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        });
        insQtaPresa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showSoftKeyboard(insQtaPresa);
                }
            }
        });

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

    private void qtaZero(String title,String message, int tipo){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/PresaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);
                    if(tipo == 0){
                        if(noMatch == 1){
                            noMatch = 0;
                            workbook.getSheetAt(0).getRow(f).createCell(6).setCellValue(insQtaPresa.getText().toString());
                            workbook.getSheetAt(0).getRow(f).createCell(14).setCellValue(timing);
                        }else{
                            workbook.getSheetAt(0).getRow(i).createCell(6).setCellValue(insQtaPresa.getText().toString());
                            workbook.getSheetAt(0).getRow(i).createCell(14).setCellValue(timing);
                            i--;
                        }
                        btnNext.setVisibility(View.VISIBLE);
                        if (i == 1) {
                            btnPrev.setVisibility(View.INVISIBLE);
                        } else {
                            btnPrev.setVisibility(View.VISIBLE);
                        }
                        txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                        txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                        txtEanPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
                        txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                        txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
                        txtQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
                        txtUbicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
                        txtSubicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
                    }else{
                        if(noMatch == 1){
                            noMatch = 0;
                            workbook.getSheetAt(0).getRow(f).createCell(6).setCellValue(insQtaPresa.getText().toString());
                            workbook.getSheetAt(0).getRow(f).createCell(14).setCellValue(timing);
                        }else{
                            workbook.getSheetAt(0).getRow(i).createCell(6).setCellValue(insQtaPresa.getText().toString());
                            workbook.getSheetAt(0).getRow(i).createCell(14).setCellValue(timing);
                            i++;
                        }
                        btnPrev.setVisibility(View.VISIBLE);
                        if(i == (workbook.getSheetAt(0).getLastRowNum())){
                            btnFinePresa.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.INVISIBLE);
                        }else{
                            btnNext.setVisibility(View.VISIBLE);
                        }
                        txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                        txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                        txtEanPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(2).getStringCellValue());
                        txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                        txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(0).getStringCellValue());
                        txtQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(5).getStringCellValue());
                        txtUbicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue());
                        txtSubicPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(4).getStringCellValue());
                    }
                    inDoc = 0;
                    txtCount.setText((i)+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                    txtImpPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(13).getStringCellValue());
                    txtesPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(12).getStringCellValue());
                    txtDescPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(1).getStringCellValue());
                    insQtaPresa.setText(workbook.getSheetAt(0).getRow(i).getCell(6).getStringCellValue());
                    insQtaPresa.setEnabled(false);
                    insCodArt.setText("");
                    Integer txPresa = Integer.parseInt(txtesPresa.getText().toString()), txImpPresa = Integer.parseInt(txtImpPresa.getText().toString());
                    if(txPresa<=txImpPresa){
                        esistenzaImpegnata("","");
                    }
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
            e.printStackTrace();
        }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaPresa(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                    if(PORCODIO!=0) {
                        XSSFWorkbook workbook;
                        try {
                            String outFileName = fileName;

                            File path = new File("/storage/emulated/0/NAS/PresaGen");

                            FileInputStream file = new FileInputStream(new File(path, outFileName));
                            workbook = new XSSFWorkbook(file);
                        inDoc = 0;
                            if(noMatch == 1){
                                noMatch = 0;
                                workbook.getSheetAt(0).getRow(f).createCell(6).setCellValue(insQtaPresa.getText().toString());
                                workbook.getSheetAt(0).getRow(f).createCell(14).setCellValue(timing);
                            }else{
                                workbook.getSheetAt(0).getRow(i).createCell(6).setCellValue(insQtaPresa.getText().toString());
                                workbook.getSheetAt(0).getRow(i).createCell(14).setCellValue(timing);
                                i++;
                            }
                        insQtaPresa.setEnabled(false);
                        insCodArt.setText("");
                        file.close();

                            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                            workbook.write(outFile);
                            outFile.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    dialog.cancel();
                    Intent review = new Intent(IniziaPresa.this, ReviewSpunta.class);
                    review.putExtra("tipo", 1);
                    review.putExtra("docsName", docsName);
                    review.putExtra("fileName", fileName);
                    review.putExtra("magazzino", magazzino);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void quantitaMancante(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Si", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloNonTrovato(String title,String message, String art, String desc, FileInputStream file, XSSFWorkbook workbook, File path, String outFileName){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    insCodArt.setText("");
                    noMatch = 0;
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    try {

                        Row row = workbook.getSheetAt(0).createRow(workbook.getSheetAt(0).getLastRowNum()+1);
                        row.createCell(0).setCellValue(art);
                        row.createCell(5).setCellValue("0");
                        row.createCell(7).setCellValue("0");
                        row.createCell(9).setCellValue("");
                        row.createCell(10).setCellValue(magazzino);
                        row.createCell(11).setCellValue("");
                        row.createCell(13).setCellValue("");
                        row.createCell(6).setCellValue("0");
                        row.createCell(2).setCellValue(ean);
                        row.createCell(1).setCellValue(desc);
                        row.createCell(3).setCellValue("");
                        row.createCell(4).setCellValue("");
                        row.createCell(12).setCellValue("");
                        row.createCell(14).setCellValue(timing);
                        row.createCell(8).setCellValue(workbook.getSheetAt(0).getRow(1).getCell(8).getStringCellValue());

                    btnNext.setVisibility(View.INVISIBLE);
                    btnPrev.setVisibility(View.VISIBLE);

                    txtCodArtPresa.setText(art);
                    txtQtaPresa.setText("0");
                    txtesPresa.setText("");
                    txtCount.setText((workbook.getSheetAt(0).getLastRowNum())+"/"+(workbook.getSheetAt(0).getLastRowNum()));
                    txtImpPresa.setText("");
                    txtUbicPresa.setText("");
                    txtSubicPresa.setText("");
                    txtDescPresa.setText(desc);
                    insQtaPresa.setText("0");
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
            e.printStackTrace();
        }
                    dialog.cancel();
                    insQtaPresa.setEnabled(true);
                    insQtaPresa.setFocusableInTouchMode(true);
                    insQtaPresa.requestFocus();
                    showSoftKeyboard(insQtaPresa);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String codiceArt = null;
        String descrizioneLet = null;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess) {
                if(codiceArt.equals(txtCodArtPresa.getText())){
                    insQtaPresa.setText("1");
                    insQtaPresa.setEnabled(true);
                    insQtaPresa.setFocusableInTouchMode(true);
                    insQtaPresa.requestFocus();
                    insQtaPresa.setSelectAllOnFocus(true);
                }else{
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName;

                        File path = new File("/storage/emulated/0/NAS/PresaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                    noMatch = 1;
                    f = -1;
                    j=1;

                        while(workbook.getSheetAt(0).getRow(j) != null){
                            Row row = workbook.getSheetAt(0).getRow(j);
                            if(row.getCell(0).getStringCellValue().equals(codiceArt)){
                                inDoc = 1;
                                f = j;
                                if(j == workbook.getSheetAt(0).getLastRowNum()){
                                    btnFinePresa.setVisibility(View.VISIBLE);
                                    btnNext.setVisibility(View.INVISIBLE);
                                }else{
                                    btnNext.setVisibility(View.VISIBLE);
                                }if(j == 0){
                                    btnPrev.setVisibility(View.INVISIBLE);
                                }else{
                                    btnPrev.setVisibility(View.VISIBLE);
                                }

                                txtesPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(12).getStringCellValue());
                                txtEanPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(2).getStringCellValue());
                                txtImpPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(13).getStringCellValue());
                                txtCodArtPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(0).getStringCellValue());
                                txtQtaPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(5).getStringCellValue());
                                txtUbicPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(3).getStringCellValue());
                                txtSubicPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(4).getStringCellValue());
                                txtDescPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(1).getStringCellValue());
                                insQtaPresa.setText(workbook.getSheetAt(0).getRow(j).getCell(6).getStringCellValue());
                                txtCount.setText((j+1)+"/"+workbook.getSheetAt(0).getLastRowNum());
                                insQtaPresa.setEnabled(true);
                            }
                            j++;
                        }
                    if(inDoc == 0){
                        articoloNonTrovato("Attenzione!","Articolo non presente nel documento, vuoi aggiungerlo?", codiceArt, descrizioneLet, file, workbook, path, outFileName);
                    }else{
                        insQtaPresa.setFocusableInTouchMode(true);
                        insQtaPresa.requestFocus();
                        showSoftKeyboard(insQtaPresa);
                    }
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                }
            }else{

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
                    String query = "select articolo.nome, Alias.codice, articolo.descrizione " +
                            "from articolo left join alias on (alias.idArticolo = articolo.id) " +
                            "where codice = '"+ findThis +"' or nome = '"+ findThis +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        isSuccess = true;
                        if(res.getString("codice")!=null){
                            ean = res.getString("codice");
                        }else{
                            ean = "";
                        }
                        codiceArt = res.getString("nome");
                        descrizioneLet = res.getString("descrizione");
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
            artOX = txtCodArtPresa.getText().toString();
            numOX = new ArrayList<>();
            dataC = new ArrayList<>();
            stato = new ArrayList<>();
            qtaOX = new ArrayList<>();
            serieOX = new ArrayList<>();
        }

        private void infoOX(String title,EditText message){
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresa.this)
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
                case "34": return "CASILINA";
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
                    String query = "select RigaDocumentoCommerciale.codiceArticolo, RigaDocumentoCommerciale.quantita, RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie " +
                            "from RigaDocumentoCommerciale join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' and codiceArticolo like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' and RigaDocumentoCommerciale.stato in (0,3) and RigaDocumentoCommerciale.idMagazzinoDestinazione = "+magRif+" " +
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

    public class FindEAN extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String questoEAN = "";
        String eanToSave = "";

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess){
                txtEanPresa.setText(questoEAN);
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
                    String query = "select top 1 alias.codice " +
                            "from articolo left join alias on (alias.idArticolo = articolo.id) " +
                            "where nome = '"+ trovaQuesto +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        questoEAN = res.getString("codice");
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