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

import com.emme.palmarebdibimbi.R;

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

public class InvCodQta {
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
    String descPrec = "";
    String qtaPrec = "", utente;
    Switch autoQta;
    TextView txtCodArt, txtDesc, txtEs, txtArtP, txtDescP, txtQtaP;
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
        setContentView(R.layout.activity_inv_dep_off);

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

        restoredData = new ArrayList<>();
        restoredData1 = new ArrayList<>();
        restoredData2 = new ArrayList<>();
        restoredData3 = new ArrayList<>();
        restoredData4 = new ArrayList<>();
        restoredData5 = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp","");
        Gson gson = new Gson();
        restoredData1 = gson.fromJson(preferences.getString("ListaArticoli1", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());
        restoredData2 = gson.fromJson(preferences.getString("ListaArticoli2", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());
        restoredData3 = gson.fromJson(preferences.getString("ListaArticoli3", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());
        restoredData4 = gson.fromJson(preferences.getString("ListaArticoli4", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());
        restoredData5 = gson.fromJson(preferences.getString("ListaArticoli5", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());

        restoredData.addAll(restoredData1);
        restoredData.addAll(restoredData2);
        restoredData.addAll(restoredData3);
        restoredData.addAll(restoredData4);
        restoredData.addAll(restoredData5);

        btnCD = findViewById(R.id.btnCD);
        insCodArt = findViewById(R.id.edtTxtCodArtCD);
        btnReg = findViewById(R.id.btnReg);
        btnSvuota = findViewById(R.id.btnNewCod);
        insQtaND = findViewById(R.id.insQtaND);
        insUbi1 = findViewById(R.id.insUbi1Inv);
        insUbi2 = findViewById(R.id.insUbi2Inv);
        insNote = findViewById(R.id.edtTxtNoteInv);
        txtCodArt = findViewById(R.id.txtCodArtCD);
        txtDesc = findViewById(R.id.txtDescCD);
        txtEs = findViewById(R.id.txtEsInvOff);
        salvaInv = findViewById(R.id.btnFineInv);
        txtArtP = findViewById(R.id.txtCodArtPrec);
        txtDescP = findViewById(R.id.txtDescPrec);
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
            testata.createCell(0).setCellValue("Codice articolo");
            testata.createCell(1).setCellValue("Descrizione");
            testata.createCell(2).setCellValue("Alias");
            testata.createCell(3).setCellValue("Ubicazione");
            testata.createCell(4).setCellValue("Sottoubicazione");
            testata.createCell(5).setCellValue("Quantita inventario");
            testata.createCell(6).setCellValue("Esistenza");
            testata.createCell(7).setCellValue("Note");
            testata.createCell(8).setCellValue("Store");

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
                salvaDoc("Attenzione!","Sei sicuro di voler concludere l'inventario?");
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
                        String convQta = row.getCell(5).getStringCellValue();
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
                        String convQta = row.getCell(5).getStringCellValue();
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

    public void writeSheetNotFound(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

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
                if(row.getCell(2).getStringCellValue().equals(alias) && row.getCell(3).getStringCellValue().equals(ubi1)){
                    present = true;
                    String convQta = row.getCell(5).getStringCellValue();
                    Integer newQta = Integer.parseInt(convQta) + Integer.parseInt(qta);
                    //row.createCell(5).setCellValue(newQta.toString());
                    row.getCell(5).setCellValue(newQta.toString());
                }
                i++;
            }

             */
            if(codArt.equals("") || codArt.equals(" ")){
                alertArt("Errore!","Articolo non presente, non verrà registrato, mettilo da parte per caricarlo in seguito");
            }else {
                Row row = workbook.getSheetAt(0).createRow(index);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(ubi1);
                row.createCell(4).setCellValue(ubi2);
                row.createCell(5).setCellValue(qta);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);
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

    public void writeSheet(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

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

            if(codArt.equals("") || codArt.equals(" ")){
                alertArt("Errore!","Articolo non presente, non verrà registrato, mettilo da parte per caricarlo in seguito");
            }else{
                Row row = workbook.getSheetAt(0).createRow(index);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(ubi1);
                row.createCell(4).setCellValue(ubi2);
                row.createCell(5).setCellValue(qta);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);
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
        }
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
        if(autoQta.isChecked() && !txtCodArt.getText().equals("")){
            regArt();
            insCodArt.setFocusable(true);
            insCodArt.setFocusableInTouchMode(true);
            insCodArt.requestFocus();
        }
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertInfo(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvDepOff.this)
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
            if(txtCodArt.getText().toString().equals("")){
                writeSheetNotFound(txtCodArt.getText().toString(), txtDesc.getText().toString(), insCodArt.getText().toString(), insUbi1.getText().toString(), insUbi2.getText().toString(), insQtaND.getText().toString(), insNote.getText().toString(), "0");
            }else{
                writeSheet(txtCodArt.getText().toString(), txtDesc.getText().toString(), insCodArt.getText().toString(), insUbi1.getText().toString(), insUbi2.getText().toString(), insQtaND.getText().toString(), insNote.getText().toString(), esistenza.toString());
            }
            artPrec = txtCodArt.getText().toString();
            descPrec = txtDesc.getText().toString();
            qtaPrec = insQtaND.getText().toString();
            txtArtP.setText(artPrec);
            txtDescP.setText(descPrec);
            txtQtaP.setText(qtaPrec);
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
            play.play();
            //salvaStato();
            insCodArt.setEnabled(true);
            insCodArt.setText("");
            insNote.setText("");
            txtCodArt.setText("");
            txtDesc.setText("");
            txtEs.setText("");
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
        txtCodArt.setText("");
        txtDesc.setText("");
        txtEs.setText("");
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
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

    private void newArt(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    regArt();
                    salvaDoc("Attenzione!", "Sei sicuro di voler conlcudere l'inventario?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    regArt();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void stampaRiep(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDepOff.this)
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
                            String convQta = row.getCell(5).getStringCellValue();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDepOff.this)
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
        Intent review = new Intent(InvDepOff.this, ReviewInv.class);
        review.putExtra("magazzino",store);
        review.putExtra("utente",utente);
        review.putExtra("tipo",nSp);
        review.putExtra("nG",nG);
        review.putExtra("da",da);
        review.putExtra("fileName", fileName);
        review.putExtra("causale","inventario");
        review.putExtra("cartella","Inventario");
        startActivity(review);
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
