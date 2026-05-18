package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zebra.sdk.comm.BluetoothConnection;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvCodQta extends AppCompatActivity {

    Button btnCD, btnReg, btnSvuota, salvaInv, chgUbic;
    FloatingActionButton infoUbis;
    EditText insCodArt, insQtaND, insUbi1, insUbi2, insNote, insNSp;
    int idL = 1;
    int da = 0;
    int index = 1;
    int mag = 0;
    Integer esistenza = 0;
    String artOrForn = "";
    String store = "";
    String nG = "";
    String bt = "";
    String nSp = "";
    String nomeP = "";
    String artPrec = "";
    String qtaPrec = "", utente;
    Switch autoQta;
    TextView txtArtP, txtQtaP;
    int giaPremuto = 0;
    String fileName="";
    com.zebra.sdk.comm.Connection connection;
    ArrayList<Articolo> restoredData, restoredData1,restoredData2,restoredData3,restoredData4,restoredData5;

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
        setContentView(R.layout.activity_inv_cod_qta);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        checkAndCreateFolder();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            da = extras.getInt("da");
            if(da == 1){
                fileName = extras.getString("nomeF");
            }
            store = extras.getString("storeName");
            utente = extras.getString("utente");
            nG = extras.getString("nG");
            nSp = extras.getString("nSp");
        }
        risolviMag();

        btnCD = findViewById(R.id.btnCD);
        insCodArt = findViewById(R.id.edtTxtCodArtCD);
        btnReg = findViewById(R.id.btnReg);
        btnSvuota = findViewById(R.id.btnNewCod);
        insQtaND = findViewById(R.id.insQtaND);
        insUbi1 = findViewById(R.id.insUbi1Inv);
        insUbi2 = findViewById(R.id.insUbi2Inv);
        insNote = findViewById(R.id.edtTxtNoteInv);
        salvaInv = findViewById(R.id.btnFineInv);
        txtArtP = findViewById(R.id.txtCodArtPrec);
        txtQtaP = findViewById(R.id.txtQtaPrec);
        autoQta = findViewById(R.id.swtQtaAuto);
        insNSp = findViewById(R.id.insNSp);
        chgUbic = findViewById(R.id.btnInvChgUbic);
        infoUbis = findViewById(R.id.btnInfoUbis);

        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbi1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbi2.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insCodArt.setMaxLines(1);
        insCodArt.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        insUbi1.setText(nG);
        insUbi2.setText(nG);
        insNSp.setText(nSp);
        insNSp.setEnabled(false);
        insUbi1.setEnabled(false);
        insUbi2.setEnabled(false);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        nomeP = p.getString("NomePalm","");
        if(da!=1){
            fileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx"; //Name of the file
        }

        File file = new File("/storage/emulated/0/NAS/Inventario", fileName);
        if(file.exists()){
            alertArt("Attenzione!","Documento presente");

            XSSFWorkbook workbook;

            try {
                String outFileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx";

                File path = new File("/storage/emulated/0/NAS/Inventario");

                FileInputStream f = new FileInputStream(new File(path, outFileName));
                workbook = new XSSFWorkbook(f);

                while(workbook.getSheetAt(0).getRow(index) != null){
                    index++;
                }

                f.close();

                FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                workbook.write(outFile);
                outFile.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet

            Row testata = sheet.createRow(0);
            testata.createCell(0).setCellValue("Codice");
            testata.createCell(1).setCellValue("Ubicazione");
            testata.createCell(2).setCellValue("Sottoubicazione");
            testata.createCell(3).setCellValue("Quantita");
            testata.createCell(4).setCellValue("Note");
            testata.createCell(5).setCellValue("Store");

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

        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    insQtaND.setFocusable(false);
                    insQtaND.setFocusableInTouchMode(false);
                    artOrForn = insCodArt.getText().toString();
                    cercaInDB();
                    if(!autoQta.isChecked()){
                        insCodArt.setEnabled(false);
                        insCodArt.clearFocus();
                        insQtaND.setEnabled(true);
                        insQtaND.setFocusable(true);
                        insQtaND.setFocusableInTouchMode(true);
                        insQtaND.requestFocus();
                        insQtaND.setCursorVisible(true);
                        insQtaND.setSelectAllOnFocus(true);
                        showSoftKeyboard(insQtaND);
                    }else{
                        insQtaND.setEnabled(false);
                        insNote.setEnabled(false);

                        btnReg.setEnabled(false);
                        chgUbic.setEnabled(false);
                        salvaInv.setEnabled(false);
                        autoQta.setEnabled(false);
                        btnSvuota.setEnabled(false);

                        insNote.clearFocus();
                        btnReg.clearFocus();
                        chgUbic.clearFocus();
                        salvaInv.clearFocus();
                        autoQta.clearFocus();
                        btnSvuota.clearFocus();
                        insQtaND.clearFocus();
                        insCodArt.setFocusable(true);
                        insCodArt.setFocusableInTouchMode(true);
                        insCodArt.requestFocus();

                    }
                }
            }
            btnReg.setEnabled(true);
            chgUbic.setEnabled(true);
            salvaInv.setEnabled(true);
            autoQta.setEnabled(true);
            btnSvuota.setEnabled(true);
            return false;
        });
        insQtaND.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {

                insUbi1.setFocusable(true);
                insUbi2.setFocusable(true);
                insNote.setFocusable(true);
                insUbi1.setFocusableInTouchMode(true);
                insUbi2.setFocusableInTouchMode(true);
                insNote.setFocusableInTouchMode(true);
                insQtaND.setFocusableInTouchMode(true);
            }
            return false;
        });
        chgUbic.setOnClickListener(v -> {
            alertChgUbic("Attenzione!", "Inserisci una nuova ubicazione",insUbi1.getText().toString());
        });
        btnReg.setOnClickListener(v -> {
            if(!insCodArt.getText().toString().equals("")){
                regArt();
            }
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
            //newArt("Attenzione!","Ci sono altri articoli da inventariare?");
        });
        btnCD.setOnClickListener(v -> {
            insQtaND.setFocusable(false);
            insQtaND.setFocusableInTouchMode(false);
            artOrForn = insCodArt.getText().toString();
            cercaInDB();
            if(!autoQta.isChecked()){
                insCodArt.clearFocus();
                hideKeyboard(this);
                insQtaND.setFocusable(true);
                insQtaND.setFocusableInTouchMode(true);
                insQtaND.requestFocus();
                insQtaND.setCursorVisible(true);
                insQtaND.setSelectAllOnFocus(true);
                showSoftKeyboard(insQtaND);
            }else{
                insCodArt.setFocusable(true);
                insCodArt.setFocusableInTouchMode(true);
                insCodArt.requestFocus();
            }
        });
        btnSvuota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svuotaTutto();
            }
        });
        salvaInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection = new BluetoothConnection(bt);
                try{
                    connection.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                salvaDoc("Attenzione!","Sei sicuro di voler concludere l'inventario ed essere riportato alla home?");
            }
        });
        infoUbis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XSSFWorkbook workbook;
                Integer i = 1;
                Integer nPz = 0;
                try {
                    String outFileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx";

                    File path = new File("/storage/emulated/0/NAS/Inventario");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    while(workbook.getSheetAt(0).getRow(i) != null){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        String convQta = row.getCell(3).getStringCellValue();
                        nPz = Integer.parseInt(convQta) + nPz;
                        i++;
                    }

                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i--;
                alertInfo("Info", "\nN. pezzi: "+nPz+"");
            }
        });
    }

    private void stampaZebra(String nRef, String nPz, String note){
        try {

            String cpclData = "";

            Date date = Calendar.getInstance().getTime();

            // Display a date in day, month, year format
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            cpclData = "! 5 0 0 540 1" +
                    "\n" + "\n" +
                    "TEXT 4 1 16 0 " + today +
                    "\n" + "\n" +
                    "TEXT 4 1 16 100 " + note +
                    "\n" + "\n" +
                    //"TEXT 4 1 16 200 N. Referenze: " + nRef +
                    "\n" + "\n" +
                    "TEXT 4 1 16 200 N. pezzi: " + nPz +
                    //"TEXT 4 1 16 300 N. pezzi: " + nPz +
                    "\n" + "\n" +
                    //"TEXT 5 0 300 255 " + forn2 +
                    "\n" + "\n" +
                    //"TEXT 5 0 300 295 DDT: " + doc4Print +
                    "\n" + "\n" +
                    //"TEXT 5 0 570 295 Colli: " + colli +
                    "\n" + "\n" +
                    //"TEXT 5 0 570 335 Imb.: " + insQtaSpunta.getText().toString() +
                    "\n" + "\n" +
                    //"TEXT 5 0 300 335 Data: " + today +
                    "\n" + "\n" + "\n" + "\n" +
                    //"B 128 1 0 60 24 245 " + txtInsEAN.getText().toString() +
                    "\n" + "\n" + "\n" + "\n" +
                    //"TEXT 5 0 64 315 " + txtInsEAN.getText().toString() +
                    "\n" + "\n" + "PRINT" + "\n" + "\n";



            connection.write(cpclData.getBytes());

        } catch (Exception e) {

            // Handle communications error here.

            e.printStackTrace();

        }
    }

    public static void checkAndCreateFolder() {
        // Ottieni il percorso della directory principale del telefono
        File directory = new File("/storage/emulated/0/NAS/Inventario");

        // Controlla se la cartella esiste
        if (!directory.exists()) {
            // Prova a creare la cartella
            if (directory.mkdirs()) {
                System.out.println("Cartella creata con successo: " + directory.getAbsolutePath());
            } else {
                System.out.println("Errore nella creazione della cartella: " + directory.getAbsolutePath());
            }
        } else {
            System.out.println("La cartella esiste già: " + directory.getAbsolutePath());
        }
    }

    private void alertChgUbic(String title,String message, String oldUbi){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText nuovaUbic = new EditText(this);
        nuovaUbic.setHint("Ubicazione...");
        nuovaUbic.setInputType(InputType.TYPE_CLASS_TEXT);

        layout.addView(nuovaUbic);

        builder.setView(layout);

        builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.cancel();
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            if(!nuovaUbic.getText().toString().equals("")&&!nuovaUbic.getText().toString().equals(oldUbi)){
                if(!connection.isConnected()){
                    connection = new BluetoothConnection(bt);
                    try{
                        connection.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                XSSFWorkbook workbook;
                Integer i = 1;
                Integer nPz = 0;
                try {
                    String outFileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx";

                    File path = new File("/storage/emulated/0/NAS/Inventario");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    while(workbook.getSheetAt(0).getRow(i) != null && workbook.getSheetAt(0).getRow(i).getCell(3).getStringCellValue().equals(oldUbi)){
                        Row row = workbook.getSheetAt(0).getRow(i);
                        String convQta = row.getCell(3).getStringCellValue();
                        nPz = Integer.parseInt(convQta) + nPz;
                        i++;
                    }

                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                i--;
                stampaZebra(i.toString(), nPz.toString(), oldUbi);
                insUbi1.setText(nuovaUbic.getText());
                insUbi2.setText(nuovaUbic.getText());
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void writeSheet(String alias, String ubi1, String ubi2, String qta, String note) {

        XSSFWorkbook workbook;

        try {
            String outFileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx";

            File path = new File("/storage/emulated/0/NAS/Inventario");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            /*
            int i = 0;
            boolean present = false;
            while(workbook.getSheetAt(0).getRow(i) != null && !present){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(codArt) && row.getCell(3).getStringCellValue().equals(ubi1)){
                    present = true;
                    String convQta = row.getCell(5).getStringCellValue();
                    Integer newQta = Integer.parseInt(convQta) + Integer.parseInt(qta);
                    //row.createCell(5).setCellValue(newQta.toString());
                    row.getCell(5).setCellValue(newQta.toString());
                }
                i++;
            }

             */

            if(alias.equals("") || alias.equals(" ")){
                alertArt("Errore!","Articolo non presente, non verrà registrato, mettilo da parte per caricarlo in seguito");
            }else{
                Row row = workbook.getSheetAt(0).createRow(index);
                row.createCell(0).setCellValue(alias);
                row.createCell(1).setCellValue(ubi1);
                row.createCell(2).setCellValue(ubi2);
                row.createCell(3).setCellValue(qta);
                row.createCell(4).setCellValue(note);
                row.createCell(5).setCellValue(store);
                index++;
            }

            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        String outFileName = nomeP+"_inventario_"+nG+"_"+nSp+".xlsx";
        String path = "/storage/emulated/0/NAS/SpuntaGen";
        copyFile(path, outFileName, "/storage/emulated/0/Backup");

         */
    }

    public void cercaInDB(){
        boolean presente = false;
        int index = 0;
        int i=0;
        /*
        while (i<restoredData.size() && !presente){
            Articolo questoArt = restoredData.get(i);
            for (int j = 0; j < questoArt.getAllEan().size(); j++) {
                if (questoArt.getEan(j).equals(artOrForn) || questoArt.getCodArt().equals(artOrForn)
                        || questoArt.getEan(j).equals(artOrForn.trim()) || questoArt.getCodArt().equals(artOrForn.trim())
                        || questoArt.getEan(j).equals(artOrForn.trim()+" ") || questoArt.getCodArt().equals(artOrForn.trim()+" ")
                        || questoArt.getEan(j).equals(" "+artOrForn.trim()) || questoArt.getCodArt().equals(" "+artOrForn.trim())) {
                    index = i;
                    presente = true;
                    txtCodArt.setText(questoArt.getCodArt());
                    txtDesc.setText(questoArt.getDesc());
                    txtEs.setText(questoArt.getEs().toString());
                }
            }if(!presente){
                i++;
            }
        }
        if (presente) {
            Articolo artTrovato = restoredData.get(index);
            txtEs.setText(artTrovato.getEs().toString());
            txtDesc.setText(artTrovato.getDesc());
            txtCodArt.setText(artTrovato.getCodArt());
            esistenza = artTrovato.getEs();
        } else {
            alertArt("Errore!", "Articolo non trovato, inserisci una nota");
        }*/
        giaPremuto = 0;
        if(!autoQta.isChecked()){
            insCodArt.clearFocus();
            hideKeyboard(this);
            insCodArt.clearFocus();
            insUbi1.setFocusable(false);
            insUbi2.setFocusable(false);
            insNote.setFocusable(false);
            insQtaND.setFocusableInTouchMode(true);
            insQtaND.requestFocus();
            insQtaND.setSelectAllOnFocus(true);
            showSoftKeyboard(insQtaND);
        }else{
            btnReg.setEnabled(false);
            chgUbic.setEnabled(false);
            salvaInv.setEnabled(false);
            autoQta.setEnabled(false);
            btnSvuota.setEnabled(false);

            insQtaND.setFocusable(false);
            insQtaND.setFocusableInTouchMode(false);
            insUbi1.setFocusable(false);
            insUbi2.setFocusable(false);
            insNote.setFocusable(false);
        }
        if(autoQta.isChecked()){
            regArt();
            insCodArt.setFocusable(true);
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
        }
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvCodQta.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertInfo(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvCodQta.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void regArt(){
        if(!insQtaND.getText().toString().equals("") && insQtaND.getText().toString().length()<5 && !insQtaND.getText().toString().contains(" ")
                && !insQtaND.getText().toString().contains(",") && !insQtaND.getText().toString().contains(".") && !insQtaND.getText().toString().equals("-")){

            writeSheet(insCodArt.getText().toString(), insUbi1.getText().toString(), insUbi2.getText().toString(), insQtaND.getText().toString(), insNote.getText().toString());

            artPrec = insCodArt.getText().toString();
            qtaPrec = insQtaND.getText().toString();
            txtArtP.setText(artPrec);
            txtQtaP.setText(qtaPrec);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
            play.play();
            //salvaStato();
            insCodArt.setEnabled(true);
            insCodArt.setText("");
            insNote.setText("");
            insQtaND.setText("1");
            insUbi1.setFocusable(false);
            insUbi2.setFocusable(false);
            insNote.setFocusable(false);
            insQtaND.setFocusable(false);
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
            insQtaND.setFocusable(true);
            insUbi1.setFocusable(true);
            insUbi2.setFocusable(true);
            insNote.setFocusable(true);
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
            if(autoQta.isChecked()){
                hideKeyboard(this);
            }
            insCodArt.setEnabled(true);
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
        }else{
            alertArt("Errore!","Devi inserire una quantità valida per poter registrare l'articolo");
        }
    }

    public void svuotaTutto(){
        insCodArt.setText("");
        insQtaND.setText("1");
        insNote.setText("");
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
    }

    private void stampaRiep(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvCodQta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    ricercaArticoli();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    if(!connection.isConnected()){
                        connection = new BluetoothConnection(bt);
                        try{
                            connection.open();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    XSSFWorkbook workbook;
                    Integer i = 1;
                    Integer nPz = 0;
                    try {
                        String outFileName = "inventario_"+nG+"_"+nSp+"_"+nomeP+"_"+store+".xlsx";

                        File path = new File("/storage/emulated/0/NAS/Inventario");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        while(workbook.getSheetAt(0).getRow(i) != null){
                            Row row = workbook.getSheetAt(0).getRow(i);
                            String convQta = row.getCell(3).getStringCellValue();
                            nPz = Integer.parseInt(convQta) + nPz;
                            i++;
                        }

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    i--;
                    stampaZebra(i.toString(), nPz.toString(), fileName);
                    dialog.cancel();
                    ricercaArticoli();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaDoc(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvCodQta.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    stampaRiep("Attenzione!","Vuoi stampare l'etichetta di riepilogo?");
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void ricercaArticoli(){
        /*
        connectionClass = new ConnectionClass();
        for(int i=0;i<ean.size(); i++){
            findThisEAN = ean.get(i);
            findArts();
        }

         */
        sendEmail();
    }

    public void sendEmail() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            String smtpHost = "mail.bdibimbi.it";
            String smtpPort = "587"; // SSL

            MailSender sender = new MailSender("assistenza@bdibimbi.it", "Assistenza@2020", smtpHost, smtpPort);

            try {
                String destinatario = "cuccu.r@gmail.com";
                String ccUtente = "mauro.cuccu@bdibimbi.it";
                String oggetto = "Inventario " + fileName;
                String corpo = "";

                File file = new File("/storage/emulated/0/NAS/inventario/"+ fileName);

                sender.sendMail(oggetto, corpo, "BdiBimbi", destinatario, ccUtente, file);

                // 🔔 opzionale: notifica su UI Thread dopo invio
                runOnUiThread(() -> {
                    Toast.makeText(this, "Email inviata con successo", Toast.LENGTH_SHORT).show();
                });

                Intent review = new Intent(InvCodQta.this, MainActivity.class);
                /*
                review.putExtra("magazzino",store);
                review.putExtra("utente",utente);
                review.putExtra("tipo",nSp);
                review.putExtra("nG",nG);
                review.putExtra("da",da);
                review.putExtra("fileName", fileName);
                review.putExtra("causale","inventario");
                review.putExtra("cartella","Inventario");*/
                startActivity(review);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Errore durante l'invio", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void risolviMag(){
        switch (store) {
            case "MASTER":
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                mag = 77;
                idL = 1;
                break;
            case "MARCONI":
                mag = 35;
                idL = 1;
                break;
            case "PIRRI":
                mag = 72;
                idL = 1;
                break;
            case "OLBIA":
                mag = 76;
                idL = 1;
                break;
            case "SASSARI":
                mag = 74;
                idL = 1;
                break;
            case "NUORO":
                mag = 32;
                idL = 1;
                break;
            case "CARBONIA":
                mag = 78;
                idL = 1;
                break;
            case "TORTOLI":
                mag = 75;
                idL = 1;
                break;
            case "ORISTANO":
                mag = 71;
                idL = 1;
                break;
            case "TIBURTINA":
                mag = 85;
                idL = 1;
                break;
            case "MasterMagRoma":
                mag = 91;
                idL = 1;
                break;
            case "CAPENA":
                mag = 87;
                idL = 1;
                break;
            case "OSTIENSE":
                mag = 86;
                idL = 1;
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                mag = 90;
                idL = 1;
                break;
            case "POMEZIA":
                mag = 94;
                idL = 1;
                break;
            case "ARDEATINA":
                mag = 112;
                idL = 1;
                break;
            case "VERONA":
                mag = 114;
                idL = 1;
                break;
            case "ROMACEDI":
                mag = 111;
                idL = 1;
                break;
            case "INTRANSITO":
                mag = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                mag = 89;
                idL = 1;
                break;
            default:
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

}