package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class InvDepOff extends AppCompatActivity {

    Button btnCD, btnReg, btnSvuota, salvaInv;
    EditText insCodArt, insQtaND, insUbi1, insUbi2, insNote, insNSp;
    int idL = 1;
    int mag = 0;
    Integer esistenza = 0;
    String artOrForn = "";
    String store = "";
    String nG = "";
    String nSp = "";
    String nomeP = "";
    String artPrec = "";
    String descPrec = "";
    String qtaPrec = "";
    Switch autoQta;
    TextView txtCodArt, txtDesc, txtEs, txtArtP, txtDescP, txtQtaP;
    int giaPremuto = 0;
    ArrayList<Articolo> restoredData;

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

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
            nG = extras.getString("nG");
            nSp = extras.getString("nSp");
        }
        risolviMag();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        restoredData = gson.fromJson(preferences.getString("ListaArticoli", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());

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

        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbi1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbi2.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        insUbi1.setText(nG);
        insUbi2.setText(nG);
        insNSp.setText(nSp);
        insNSp.setEnabled(false);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        nomeP = p.getString("NomePalm","");
        String fileName = nomeP+"inventario_"+nG+"_"+nSp+".xlsx"; //Name of the file

        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        if(file.exists()){
            alertArt("Attenzione!","Documento presente");
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
                    artOrForn = insCodArt.getText().toString();
                    cercaInDB();
                }
            }
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

        btnReg.setOnClickListener(v -> {
            if(!insCodArt.getText().toString().equals("")){
                regArt();
            }
            //newArt("Attenzione!","Ci sono altri articoli da inventariare?");
        });
        btnCD.setOnClickListener(v -> {
            artOrForn = insCodArt.getText().toString();
            cercaInDB();
            insQtaND.setFocusableInTouchMode(true);
            insQtaND.requestFocus();
            insQtaND.setSelectAllOnFocus(true);
            showSoftKeyboard(insQtaND);
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
                salvaDoc("Attenzione!","Sei sicuro di voler concludere l'inventario?");
            }
        });
    }

    public void writeSheetNotFound(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

        XSSFWorkbook workbook;

        try {
            String outFileName = nomeP+"inventario_"+nG+"_"+nSp+".xlsx";

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

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
            if(!present){
                Row row = workbook.getSheetAt(0).createRow(i);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(ubi1);
                row.createCell(4).setCellValue(ubi2);
                row.createCell(5).setCellValue(qta);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);
            }

            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeSheet(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

        XSSFWorkbook workbook;

        try {
            String outFileName = nomeP+"inventario_"+nG+"_"+nSp+".xlsx";

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

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
            if(!present){
                Row row = workbook.getSheetAt(0).createRow(i);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(ubi1);
                row.createCell(4).setCellValue(ubi2);
                row.createCell(5).setCellValue(qta);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);
            }

            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cercaInDB(){
        boolean presente = false;
        int index = 0;
        for (int i = 0; i < restoredData.size(); i++) {
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
        insQtaND.setFocusableInTouchMode(true);
        insUbi1.setFocusable(false);
        insUbi2.setFocusable(false);
        insNote.setFocusable(false);
        insQtaND.requestFocus();
        insQtaND.setSelectAllOnFocus(true);
        showSoftKeyboard(insQtaND);
        insUbi1.setFocusable(true);
        insUbi2.setFocusable(true);
        insNote.setFocusable(true);
        if(autoQta.isChecked() && !txtCodArt.getText().equals("")){
            regArt();
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
            if(autoQta.isChecked()){
                hideKeyboard(this);
            }
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

    private void salvaDoc(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    ricercaArticoli();
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
        review.putExtra("tipo",nSp);
        review.putExtra("nG",nG);
        startActivity(review);
    }

    public void risolviMag(){
        switch (store) {
            case "MASTER":
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                mag = 77;
                idL = 6;
                break;
            case "MARCONI":
                mag = 35;
                idL = 6;
                break;
            case "PIRRI":
                mag = 72;
                idL = 6;
                break;
            case "OLBIA":
                mag = 76;
                idL = 5;
                break;
            case "SASSARI":
                mag = 74;
                idL = 9;
                break;
            case "NUORO":
                mag = 32;
                idL = 4;
                break;
            case "CARBONIA":
                mag = 78;
                idL = 7;
                break;
            case "TORTOLI":
                mag = 75;
                idL = 3;
                break;
            case "ORISTANO":
                mag = 71;
                idL = 8;
                break;
            case "TIBURTINA":
                mag = 85;
                idL = 3049;
                break;
            case "CAPENA":
                mag = 87;
                idL = 3050;
                break;
            case "OSTIENSE":
                mag = 86;
                idL = 3048;
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                mag = 90;
                idL = 3049;
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