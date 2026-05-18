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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class InvDep extends AppCompatActivity {

    Button btnCD, btnReg, btnSvuota, salvaInv;
    String alias = "";

    String insert;

    String utente, ipNeg;
    ConnectionClass connectionClass;
    String nG = "";
    String nSp = "";
    String causale = "";
    String nomeP = "";
    TextView txtCodArt, txtDesc, txtPV, txtPP, txtUbic, txtSubic, txtUbic2, txtSubic2, txtEsThis2;
    TextView txtEsSestu, txtEsMarconi,txtEsPirri,txtEsSassari,txtEsOlbia,txtEsNuoro,txtEsOristano,txtEsTortoli,txtEsCarbonia,txtEsTiburtina,txtEsCapena,txtEsOstiense,txtEsDep, txtEsThis, txtEsDepR, txtEsCasilina,txtEsPom,txtEsArd,txtEsVer,txtEsRC;
    TextView txtOFSestu, txtOFMarconi,txtOFPirri,txtOFSassari,txtOFOlbia,txtOFNuoro,txtOFOristano,txtOFTortoli,txtOFCarbonia,txtOFTiburtina,txtOFCapena,txtOFOstiense,txtOFDep, txtOFThis, txtOFDepR, txtOFCasilina,txtOFPom,txtOFArd,txtOFVer,txtOFRC;
    TextView txtOCSestu, txtOCMarconi,txtOCPirri,txtOCSassari,txtOCOlbia,txtOCNuoro,txtOCOristano,txtOCTortoli,txtOCCarbonia,txtOCTiburtina,txtOCCapena,txtOCOstiense,txtOCDep, txtOCThis, txtOCDepR, txtOCCasilina,txtOCPom,txtOCArd,txtOCVer,txtOCRC;
    EditText insCodArt, insQtaND, insUbi1, insUbi2, insNote;
    int idL = 1;
    int mag = 0;
    Context context;
    String artOrForn = "", fileName = "";
    String store = "";
    int giaPremuto = 0;
    ProgressBar pbCD;

    SharedPreferences prefs;

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
        setContentView(R.layout.activity_inv_dep);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
/*
        if(type == 1){
            if(getDefaults(this)){
                alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi riprendere quel documento? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
            }
        }
*/
        context = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
            nG = extras.getString("nG");
            nSp = extras.getString("nSp");
            causale = extras.getString("causale");
            utente = extras.getString("utente");
        }
        risolviMag();

        connectionClass = new ConnectionClass();

        btnCD = findViewById(R.id.btnCD);
        pbCD = findViewById(R.id.pbCD);
        pbCD.setVisibility(View.GONE);
        txtCodArt = findViewById(R.id.txtCodArtCD);
        txtDesc = findViewById(R.id.txtDescCD);
        txtPV = findViewById(R.id.txtPVCD);
        txtPP = findViewById(R.id.txtPPCD);
        txtEsSestu = findViewById(R.id.txtEsSestuCD);
        txtEsMarconi = findViewById(R.id.txtEsMarconiCD);
        txtEsPirri = findViewById(R.id.txtEsPirriCD);
        txtEsSassari = findViewById(R.id.txtEsSassariCD);
        txtEsOlbia = findViewById(R.id.txtEsOlbiaCD);
        txtEsNuoro = findViewById(R.id.txtEsNuoroCD);
        txtEsOristano = findViewById(R.id.txtEsOristanoCD);
        txtEsTortoli = findViewById(R.id.txtEsTortoliCD);
        txtEsCarbonia = findViewById(R.id.txtEsCarboniaCD);
        txtEsTiburtina = findViewById(R.id.txtEsTiburtinaCD);
        txtEsCapena = findViewById(R.id.txtEsCapenaCD);
        txtEsOstiense = findViewById(R.id.txtEsOstienseCD);
        txtEsDep = findViewById(R.id.txtEsDepCD);
        txtEsPom = findViewById(R.id.txtEsPom);
        txtEsArd = findViewById(R.id.txtEsArd);
        txtEsVer = findViewById(R.id.txtEsVer);
        txtEsRC = findViewById(R.id.txtEsRC);
        txtOFSestu = findViewById(R.id.txtOFSestuCD);
        txtOFMarconi = findViewById(R.id.txtOFMarconiCD);
        txtOFPirri = findViewById(R.id.txtOFPirriCD);
        txtOFSassari = findViewById(R.id.txtOFSassariCD);
        txtOFOlbia = findViewById(R.id.txtOFOlbiaCD);
        txtOFNuoro = findViewById(R.id.txtOFNuoroCD);
        txtOFOristano = findViewById(R.id.txtOFOristanoCD);
        txtOFTortoli = findViewById(R.id.txtOFTortoliCD);
        txtOFCarbonia = findViewById(R.id.txtOFCarboniaCD);
        txtOFTiburtina = findViewById(R.id.txtOFTiburtinaCD);
        txtOFCapena = findViewById(R.id.txtOFCapenaCD);
        txtOFOstiense = findViewById(R.id.txtOFOstienseCD);
        txtOFDep = findViewById(R.id.txtOFDep);
        txtOFPom = findViewById(R.id.txtOFPom);
        txtOFArd = findViewById(R.id.txtOFArd);
        txtOFVer = findViewById(R.id.txtOFVer);
        txtOFRC = findViewById(R.id.txtOFRC);
        txtOCSestu = findViewById(R.id.txtOCSestuCD);
        txtOCMarconi = findViewById(R.id.txtOCMarconiCD);
        txtOCPirri = findViewById(R.id.txtOCPirriCD);
        txtOCSassari = findViewById(R.id.txtOCSassariCD);
        txtOCOlbia = findViewById(R.id.txtOCOlbiaCD);
        txtOCNuoro = findViewById(R.id.txtOCNuoroCD);
        txtOCOristano = findViewById(R.id.txtOCOristanoCD);
        txtOCTortoli = findViewById(R.id.txtOCTortoliCD);
        txtOCCarbonia = findViewById(R.id.txtOCCarboniaCD);
        txtOCTiburtina = findViewById(R.id.txtOCTiburtinaCD);
        txtOCCapena = findViewById(R.id.txtOCCapenaCD);
        txtOCOstiense = findViewById(R.id.txtOCOstienseCD);
        txtOCDep = findViewById(R.id.txtOCDep);
        txtOCPom = findViewById(R.id.txtOCPom);
        txtOCArd = findViewById(R.id.txtOCArd);
        txtOCVer = findViewById(R.id.txtOCVer);
        txtOCRC = findViewById(R.id.txtOCRC);
        insCodArt = findViewById(R.id.edtTxtCodArtCD);
        btnCD = findViewById(R.id.btnCD);
        txtEsThis = findViewById(R.id.txtThisEsCD);
        txtOFThis = findViewById(R.id.txtThisOFCD);
        txtOCThis = findViewById(R.id.txtThisOCCD);
        btnReg = findViewById(R.id.btnReg);
        btnSvuota = findViewById(R.id.btnNewCod);
        insQtaND = findViewById(R.id.insQtaND);
        insUbi1 = findViewById(R.id.insUbi1Inv);
        insUbi2 = findViewById(R.id.insUbi2Inv);
        btnSvuota = findViewById(R.id.btnNewCod);
        insNote = findViewById(R.id.edtTxtInsNoteInv);
        txtUbic = findViewById(R.id.txtUbiVG);
        txtSubic = findViewById(R.id.txtSubiVG);
        txtUbic2 = findViewById(R.id.txtUbiVG2);
        txtSubic2 = findViewById(R.id.txtSubiVG2);
        txtEsThis2 = findViewById(R.id.txtThisEsCD2);
        salvaInv = findViewById(R.id.btnFineInv);
        txtEsDepR = findViewById(R.id.txtEsDepR);
        txtOCDepR = findViewById(R.id.txtOCDepR);
        txtOFDepR = findViewById(R.id.txtOFDepR);
        txtEsCasilina = findViewById(R.id.txtEsCasilinaCD);
        txtOCCasilina = findViewById(R.id.txtOCCasilinaCD);
        txtOFCasilina = findViewById(R.id.txtOFCasilinaCD);
        FloatingActionButton btnInfoOX = findViewById(R.id.btnInfoOC);
        FloatingActionButton changeUbic = findViewById(R.id.fltChgUbic);

        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        TextView txtCausale = findViewById(R.id.txtCausale);
        txtCausale.setText(causale);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        nomeP = p.getString("NomePalm","");
        fileName = nomeP+"_"+causale+"_"+nG+"_"+nSp+"_"+store+".xlsx"; //Name of the file

        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        if(file.exists()){
            alertArt("Attenzione!","Documento presente");
        }else{
            CancDoc cancDoc = new CancDoc();
            cancDoc.execute();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet

            Row testata = sheet.createRow(0);
            testata.createCell(0).setCellValue("Codice articolo");
            testata.createCell(1).setCellValue("Descrizione");
            testata.createCell(2).setCellValue("Alias");
            testata.createCell(3).setCellValue("Quantita");
            testata.createCell(4).setCellValue("Ubicazione");
            testata.createCell(5).setCellValue("Sottoubicazione");
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
        insQtaND.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                insNote.setFocusableInTouchMode(false);
                insNote.setFocusable(false);
                insUbi1.setFocusableInTouchMode(true);
                insUbi1.requestFocus();
                insUbi1.setSelectAllOnFocus(true);
                showSoftKeyboard(insUbi1);
                insNote.setFocusable(true);
                insNote.setFocusableInTouchMode(true);
            }
            return false;
        });
        insUbi1.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                insNote.setFocusableInTouchMode(false);
                insNote.setFocusable(false);
                insUbi2.setFocusableInTouchMode(true);
                insUbi2.requestFocus();
                insUbi2.setSelectAllOnFocus(true);
                showSoftKeyboard(insUbi2);
                insNote.setFocusable(true);
                insNote.setFocusableInTouchMode(true);
            }
            return false;
        });
        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    artOrForn = insCodArt.getText().toString().trim();
                    if(artOrForn.equals("")){
                        articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
                    }else{
                        InvDep.FindArt cercaArt = new InvDep.FindArt();
                        cercaArt.execute("");
                        insQtaND.setFocusableInTouchMode(true);
                        insQtaND.requestFocus();
                        insQtaND.setSelectAllOnFocus(true);
                        showSoftKeyboard(insQtaND);
                    }
                }
            }
            return false;
        });
        btnReg.setOnClickListener(v -> {
            if(!insCodArt.getText().toString().equals("")){
                regArt();
            }
        });
        btnCD.setOnClickListener(v -> {
            artOrForn = insCodArt.getText().toString().trim();
            InvDep.FindArt findArt = new InvDep.FindArt();
            findArt.execute();
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
        btnInfoOX.setOnClickListener(v -> {
            InvDep.InfoOX info = new InvDep.InfoOX();
            info.execute();
        });
        changeUbic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artOrForn = insCodArt.getText().toString().trim();
                if(artOrForn.equals("")){
                    articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
                }else{
                    InvDep.ChangeUbic chgU = new InvDep.ChangeUbic();
                    chgU.execute("");
                }
            }
        });
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InvDep.this)
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
                writeSheet(txtCodArt.getText().toString(), txtDesc.getText().toString(), insCodArt.getText().toString(), insUbi1.getText().toString(), insUbi2.getText().toString(), insQtaND.getText().toString(), insNote.getText().toString(), txtEsThis.getText().toString());
            }
            svuotaTutto();
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
            play.play();
        }else{
            alertArt("Errore!","Devi inserire una quantità valida per poter registrare l'articolo");
        }
    }

    public void svuotaTutto(){
        txtPP.setText("");
        txtEsDep.setText("");
        txtEsSestu.setText("");
        txtEsMarconi.setText("");
        txtEsPirri.setText("");
        txtEsOlbia.setText("");
        txtEsSassari.setText("");
        txtEsNuoro.setText("");
        txtEsCarbonia.setText("");
        txtEsTortoli.setText("");
        txtEsOristano.setText("");
        txtEsTiburtina.setText("");
        txtEsCapena.setText("");
        txtEsOstiense.setText("");
        txtEsCasilina.setText("");
        txtEsArd.setText("");
        txtEsVer.setText("");
        txtEsPom.setText("");
        txtEsRC.setText("");
        txtOFDep.setText("");
        txtOFSestu.setText("");
        txtOFMarconi.setText("");
        txtOFPirri.setText("");
        txtOFOlbia.setText("");
        txtOFSassari.setText("");
        txtOFNuoro.setText("");
        txtOFCarbonia.setText("");
        txtOFTortoli.setText("");
        txtOFOristano.setText("");
        txtOFTiburtina.setText("");
        txtOFArd.setText("");
        txtOFVer.setText("");
        txtOFPom.setText("");
        txtOFRC.setText("");
        txtOFCapena.setText("");
        txtOFOstiense.setText("");
        txtOFCasilina.setText("");
        txtOCDep.setText("");
        txtOCSestu.setText("");
        txtOCMarconi.setText("");
        txtOCPirri.setText("");
        txtOCOlbia.setText("");
        txtOCArd.setText("");
        txtOCPom.setText("");
        txtOCRC.setText("");
        txtOCSassari.setText("");
        txtOCNuoro.setText("");
        txtOCCarbonia.setText("");
        txtOCTortoli.setText("");
        txtOCOristano.setText("");
        txtOCTiburtina.setText("");
        txtOCCapena.setText("");
        txtOCOstiense.setText("");
        txtOCCasilina.setText("");
        txtOCVer.setText("");
        insCodArt.setText("");
        insQtaND.setText("1");
        txtUbic.setText("");
        txtSubic.setText("");
        txtUbic2.setText("");
        txtSubic2.setText("");
        insNote.setText("");
        insUbi1.setText("");
        insUbi2.setText("");
        txtEsDepR.setText("");
        txtOFDepR.setText("");
        txtOCDepR.setText("");
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
    }

    private void salvaDoc(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDep.this)
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
        Intent review = new Intent(InvDep.this, ReviewInv.class);
        review.putExtra("magazzino",store);
        review.putExtra("utente",utente);
        review.putExtra("tipo",nSp);
        review.putExtra("ipNeg",ipNeg);
        review.putExtra("nG",nG);
        review.putExtra("causale",causale);
        review.putExtra("cartella","SpuntaGen");
        review.putExtra("fileName",nomeP+"_"+causale+"_"+nG+"_"+nSp+"_"+store+".xlsx");
        startActivity(review);
    }

    public void risolviMag(){
        switch (store) {
            case "MASTER":
                mag = 1;
                idL = 1;
                ipNeg = "192.168.2.41";
                break;
            case "SESTU":
                mag = 77;
                idL = 1;
                ipNeg = "192.168.1.20";
                break;
            case "MARCONI":
                mag = 35;
                ipNeg = "192.168.1.20";
                idL = 6;
                break;
            case "PIRRI":
                mag = 72;
                idL = 6;
                ipNeg = "192.168.1.20";
                break;
            case "OLBIA":
                mag = 76;
                idL = 5;
                ipNeg = "192.168.1.10";
                break;
            case "SASSARI":
                mag = 74;
                idL = 9;
                ipNeg = "192.168.1.20";
                break;
            case "NUORO":
                mag = 32;
                idL = 4;
                ipNeg = "192.168.1.20";
                break;
            case "CARBONIA":
                mag = 78;
                idL = 7;
                ipNeg = "192.168.1.20";
                break;
            case "TORTOLI":
                mag = 75;
                idL = 3;
                ipNeg = "192.168.1.20";
                break;
            case "ORISTANO":
                mag = 71;
                idL = 8;
                ipNeg = "85.47.29.51";
                break;
            case "TIBURTINA":
                mag = 85;
                idL = 3049;
                ipNeg = "195.100.100.202";
                break;
            case "MasterMagRoma":
                mag = 91;
                idL = 3049;
                ipNeg = "195.100.100.202";
                break;
            case "CEDIROMAINLAV":
                mag = 93;
                idL = 3054;
                ipNeg = "192.168.1.20";
                break;
            case "CAPENA":
                mag = 87;
                idL = 3050;
                ipNeg = "192.168.188.20";
                break;
            case "OSTIENSE":
                mag = 86;
                idL = 3048;
                ipNeg = "196.100.100.203";
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                idL = 1;
                ipNeg = "192.168.2.41";
                break;
            case "CASILINA":
                mag = 90;
                idL = 3049;
                ipNeg = "192.168.1.20";
                break;
            case "POMEZIA":
                mag = 94;
                idL = 3053;
                ipNeg = "192.168.1.20";
                break;
            case "ROMACEDI":
                mag = 111;
                idL = 3054;
                ipNeg = "192.168.1.20";
                break;
            case "ARDEATINA":
                mag = 112;
                idL = 3054;
                ipNeg = "192.168.1.20";
                break;
            case "VERONA":
                mag = 114;
                idL = 3055;
                ipNeg = "192.168.16.20";
                break;
            case "INTRANSITO":
                mag = 88;
                idL = 1;
                ipNeg = "192.168.2.41";
                break;
            case "INTEMPORANEO":
                mag = 89;
                ipNeg = "192.168.2.41";
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

    public String risolviMagXOc(){
        switch (mag){
            case 1:
            case 59:
                if(!txtOCDep.getText().toString().equals("")){
                    return txtOCDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtOCSestu.getText().toString().equals("")){
                    return txtOCSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtOCMarconi.getText().toString().equals("")){
                    return txtOCMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtOCPirri.getText().toString().equals("")){
                    return txtOCPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtOCOlbia.getText().toString().equals("")){
                    return txtOCOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtOCSassari.getText().toString().equals("")){
                    return txtOCSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtOCNuoro.getText().toString().equals("")){
                    return txtOCNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtOCCarbonia.getText().toString().equals("")){
                    return txtOCCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtOCTortoli.getText().toString().equals("")){
                    return txtOCTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtOCOristano.getText().toString().equals("")){
                    return txtOCOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtOCTiburtina.getText().toString().equals("")){
                    return txtOCTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtOCCapena.getText().toString().equals("")){
                    return txtOCCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtOCOstiense.getText().toString().equals("")){
                    return txtOCOstiense.getText().toString();
                }else{
                    return "0";
                }
            case 90:
                if(!txtOCCasilina.getText().toString().equals("")){
                    return txtOCCasilina.getText().toString();
                }else{
                    return "0";
                }
            case 94:
                if(!txtOCPom.getText().toString().equals("")){
                    return txtOCPom.getText().toString();
                }else{
                    return "0";
                }
            case 111:
                if(!txtOCRC.getText().toString().equals("")){
                    return txtOCRC.getText().toString();
                }else{
                    return "0";
                }
            case 112:
                if(!txtOCArd.getText().toString().equals("")){
                    return txtOCArd.getText().toString();
                }else{
                    return "0";
                }
            case 114:
                if(!txtOCVer.getText().toString().equals("")){
                    return txtOCVer.getText().toString();
                }else{
                    return "0";
                }
            case 91:
            case 93:
                if(!txtOCDepR.getText().toString().equals("")){
                    return txtOCDepR.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    public String risolviMagXOf(){
        switch (mag){
            case 1:
            case 59:
                if(!txtOFDep.getText().toString().equals("")){
                    return txtOFDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtOFSestu.getText().toString().equals("")){
                    return txtOFSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtOFMarconi.getText().toString().equals("")){
                    return txtOFMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtOFPirri.getText().toString().equals("")){
                    return txtOFPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtOFOlbia.getText().toString().equals("")){
                    return txtOFOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtOFSassari.getText().toString().equals("")){
                    return txtOFSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtOFNuoro.getText().toString().equals("")){
                    return txtOFNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtOFCarbonia.getText().toString().equals("")){
                    return txtOFCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtOFTortoli.getText().toString().equals("")){
                    return txtOFTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtOFOristano.getText().toString().equals("")){
                    return txtOFOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtOFTiburtina.getText().toString().equals("")){
                    return txtOFTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtOFCapena.getText().toString().equals("")){
                    return txtOFCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtOFOstiense.getText().toString().equals("")){
                    return txtOFOstiense.getText().toString();
                }else{
                    return "0";
                }
            case 90:
                if(!txtOFCasilina.getText().toString().equals("")){
                    return txtOFCasilina.getText().toString();
                }else{
                    return "0";
                }
            case 94:
                if(!txtOFPom.getText().toString().equals("")){
                    return txtOFPom.getText().toString();
                }else{
                    return "0";
                }
            case 111:
                if(!txtOFRC.getText().toString().equals("")){
                    return txtOFRC.getText().toString();
                }else{
                    return "0";
                }
            case 112:
                if(!txtOFArd.getText().toString().equals("")){
                    return txtOFArd.getText().toString();
                }else{
                    return "0";
                }
            case 114:
                if(!txtOFVer.getText().toString().equals("")){
                    return txtOFVer.getText().toString();
                }else{
                    return "0";
                }
            case 91:
            case 93:
                if(!txtOFDepR.getText().toString().equals("")){
                    return txtOFDepR.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    public String risolviMagEs(){
        switch (mag){
            case 1:
            case 59:
                if(!txtEsDep.getText().toString().equals("")){
                    return txtEsDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtEsSestu.getText().toString().equals("")){
                    return txtEsSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtEsMarconi.getText().toString().equals("")){
                    return txtEsMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtEsPirri.getText().toString().equals("")){
                    return txtEsPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtEsOlbia.getText().toString().equals("")){
                    return txtEsOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtEsSassari.getText().toString().equals("")){
                    return txtEsSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtEsNuoro.getText().toString().equals("")){
                    return txtEsNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtEsCarbonia.getText().toString().equals("")){
                    return txtEsCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtEsTortoli.getText().toString().equals("")){
                    return txtEsTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtEsOristano.getText().toString().equals("")){
                    return txtEsOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtEsTiburtina.getText().toString().equals("")){
                    return txtEsTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtEsCapena.getText().toString().equals("")){
                    return txtEsCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtEsOstiense.getText().toString().equals("")){
                    return txtEsOstiense.getText().toString();
                }else{
                    return "0";
                }
            case 90:
                if(!txtEsCasilina.getText().toString().equals("")){
                    return txtEsCasilina.getText().toString();
                }else{
                    return "0";
                }
            case 94:
                if(!txtEsPom.getText().toString().equals("")){
                    return txtEsPom.getText().toString();
                }else{
                    return "0";
                }
            case 111:
                if(!txtEsRC.getText().toString().equals("")){
                    return txtEsRC.getText().toString();
                }else{
                    return "0";
                }
            case 112:
                if(!txtEsArd.getText().toString().equals("")){
                    return txtEsArd.getText().toString();
                }else{
                    return "0";
                }
            case 114:
                if(!txtEsVer.getText().toString().equals("")){
                    return txtEsVer.getText().toString();
                }else{
                    return "0";
                }
            case 91:
            case 93:
                if(!txtEsDepR.getText().toString().equals("")){
                    return txtEsDepR.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDep.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloInesistente(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvDep.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Annulla",(dialog, which) -> {
                    dialog.cancel();
                    insCodArt.setEnabled(true);
                    insCodArt.setText("");
                    insQtaND.setText("1");
                    insUbi1.setText("");
                    insUbi2.setText("");
                    insCodArt.setFocusableInTouchMode(true);
                    insCodArt.requestFocus();
                });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText note = new EditText(this);
        note.setHint("Note");
        layout.addView(note);

        builder.setView(layout);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.cancel();
            insNote.setText(note.getText().toString());
        });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void prezziPromoCat(int priorita){
        Connection con = null;
        ResultSet res;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT Articolo.nome, Promozione.TipoValoreSconto, cast(CategoriaElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join CategoriaElementoxPromozione on (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.idCategoriaArticolo = CategoriaElementoxpromozione.idCategoria) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"' " +
                        "ORDER BY Promozione.priorita desc";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                if(res.next()) {
                    if(res.getInt("priorita") > priorita){
                        if(res.getInt("TipoValoreSconto") == 0){
                            Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                            txtPP.setText(pP.toString());
                        }else{
                            txtPP.setText(res.getString("prezzoPromo"));
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
        }/*
        XSSFWorkbook workbook;

        try {
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/createdDocs");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);
            int i=0;
            boolean find = false;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(txtCodArt.getText().toString())){
                    Integer tot = Integer.parseInt(row.getCell(3).getStringCellValue()) + Integer.parseInt(insQtaND.getText().toString());
                    row.createCell(3).setCellValue(tot.toString());
                    find = true;
                }
                i++;
            }
            if(!find){
                Row row = workbook.getSheetAt(0).createRow(i);
                row.createCell(2).setCellValue(insCodArt.getText().toString());
                row.createCell(1).setCellValue(txtDesc.getText().toString());
                row.createCell(0).setCellValue(txtCodArt.getText().toString());
                row.createCell(3).setCellValue(insQtaND.getText().toString());
                row.createCell(4).setCellValue("");
            }
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pbCD.setVisibility(View.GONE);
        insCodArt.setEnabled(true);
        insCodArt.setText("");
        insQtaND.setText("1");
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
        play.play();*/
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pbCD.setVisibility(View.GONE);
        insQtaND.setFocusableInTouchMode(true);
        insQtaND.requestFocus();
        insQtaND.setSelectAllOnFocus(true);
        showSoftKeyboard(insQtaND);
    }

    protected void findPromo() {
        Connection con = null;
        ResultSet res;
        int priorita = 0;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT Articolo.nome, Promozione.TipoValoreSconto, cast(ElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.id = ElementoxPromozione.idElemento) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE inizioValidita < GETDATE() and fineValidita > GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8'" +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita > GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and inizioValidita < GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"' " +
                        "ORDER BY Promozione.priorita desc";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                if(res.next()) {
                    priorita = res.getInt("priorita");
                    if(res.getInt("TipoValoreSconto") == 0){
                        Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                        txtPP.setText(pP.toString());
                    }else{
                        txtPP.setText(res.getString("prezzoPromo"));
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

    public void writeSheetNotFound(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

        XSSFWorkbook workbook;

        try {
            String outFileName = nomeP+"_"+causale+"_"+nG+"_"+nSp+"_"+store+".xlsx";

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            int i = 0;
            boolean present = false;
            while(workbook.getSheetAt(0).getRow(i) != null && !present){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(2).getStringCellValue().equals(alias) && row.getCell(4).getStringCellValue().equals(ubi1)){
                    present = true;
                    String convQta = row.getCell(3).getStringCellValue();
                    Integer newQta = Integer.parseInt(convQta) + Integer.parseInt(qta);
                    //row.createCell(5).setCellValue(newQta.toString());
                    row.getCell(3).setCellValue(newQta.toString());
                    AggDoc aggDoc = new AggDoc(newQta, codArt);
                    aggDoc.execute();
                }
                i++;
            }
            if(!present){
                Row row = workbook.getSheetAt(0).createRow(i);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(qta);
                row.createCell(4).setCellValue(ubi1);
                row.createCell(5).setCellValue(ubi2);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);

                insert = "('"+codArt+"', '"+descrizione+"', '"+alias+"', '"+ubi1+"', '"+ubi2+"', "+qta+", "+es+", '"+note+"', '"+store+"', '"+nomeP+"', '"+utente+"')";
                InvDep.CreaDoc creaDoc = new InvDep.CreaDoc();
                creaDoc.execute();
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

    public void writeSheet(String codArt, String descrizione, String alias, String ubi1, String ubi2, String qta, String note, String es) {

        XSSFWorkbook workbook;

        try {
            String outFileName = nomeP+"_"+causale+"_"+nG+"_"+nSp+"_"+store+".xlsx";

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            int i = 0;
            boolean present = false;
            while(workbook.getSheetAt(0).getRow(i) != null && !present){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(codArt) && row.getCell(4).getStringCellValue().equals(ubi1)){
                    present = true;
                    String convQta = row.getCell(3).getStringCellValue();
                    Integer newQta = Integer.parseInt(convQta) + Integer.parseInt(qta);
                    //row.createCell(5).setCellValue(newQta.toString());
                    row.getCell(3).setCellValue(newQta.toString());
                    AggDoc aggDoc = new AggDoc(newQta, codArt);
                    aggDoc.execute();
                }
                i++;
            }
            if(!present){
                Row row = workbook.getSheetAt(0).createRow(i);
                row.createCell(0).setCellValue(codArt);
                row.createCell(1).setCellValue(descrizione);
                row.createCell(2).setCellValue(alias);
                row.createCell(3).setCellValue(qta);
                row.createCell(4).setCellValue(ubi1);
                row.createCell(5).setCellValue(ubi2);
                row.createCell(6).setCellValue(es);
                row.createCell(7).setCellValue(note);
                row.createCell(8).setCellValue(store);

                insert = "('"+codArt+"', '"+descrizione+"', '"+alias+"', '"+ubi1+"', '"+ubi2+"', "+qta+", "+es+", '"+note+"', '"+store+"', '"+nomeP+"', '"+utente+"')";
                InvDep.CreaDoc creaDoc = new InvDep.CreaDoc();
                creaDoc.execute();
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

    protected void recuperaPV() {
        Connection con = null;
        ResultSet res;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String ConnURL;
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT cast (ArticoloxListino.prezzo as decimal(10,2)) as prz," +
                        "(select ubicazione from articoloxmagazzino where articoloxmagazzino.idarticolo = articolo.id and idMagazzino = "+mag+") as ubi, " +
                        "(select sottoubicazione from articoloxmagazzino where articoloxmagazzino.idarticolo = articolo.id and idMagazzino = "+mag+") as subi " +
                        "FROM Articolo " +
                        "JOIN ArticoloxListino ON (ArticoloxListino.idArticolo = Articolo.id) " +
                        "WHERE Articolo.nome = '" + artOrForn + "' " +
                        "AND ArticoloxListino.idListino = '"+idL+"' " +
                        "ORDER BY Articolo.nome ";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()) {
                    txtPV.setText(res.getString("prz"));
                    if(res.getString("ubi")!=null){
                        txtUbic.setText(res.getString("ubi"));
                        txtUbic2.setText(res.getString("ubi"));
                    }else{
                        txtUbic.setText("N/A");
                        txtUbic2.setText("N/A");
                    }if(res.getString("subi")!=null){
                        txtSubic.setText(res.getString("subi"));
                        txtSubic2.setText(res.getString("subi"));
                    }else{
                        txtSubic.setText("N/A");
                        txtSubic2.setText("N/A");
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
        findPromo();
    }

    protected void recuperaGiacenze() {
        Connection con = null;
        ResultSet res;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT cast (ProgressivoArticolo.esistenza as int) as Esistenza, " +
                        "cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) as OrdiniFornitore, " +
                        "cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) as OrdiniCliente, " +
                        "(Magazzino.id) as magn " +
                        "FROM ProgressivoArticolo " +
                        "JOIN Articolo ON (ProgressivoArticolo.MetaArticolo = Articolo.id) " +
                        "JOIN Magazzino on (ProgressivoArticolo.MetaMagazzino = Magazzino.id)" +
                        "WHERE Articolo.nome = '" + artOrForn + "' " +
                        "AND da < GETDATE() " +
                        "AND a > GETDATE() " +
                        "AND isNascosto = '0' " +
                        "ORDER BY Articolo.nome, Magazzino.nome";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()) {
                    switch (res.getInt("magn")){
                        case 1:
                            txtEsDep.setText(res.getString("Esistenza"));
                            txtOCDep.setText(res.getString("OrdiniCliente"));
                            txtOFDep.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 77:
                            txtEsSestu.setText(res.getString("Esistenza"));
                            txtOCSestu.setText(res.getString("OrdiniCliente"));
                            txtOFSestu.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 35:
                            txtEsMarconi.setText(res.getString("Esistenza"));
                            txtOCMarconi.setText(res.getString("OrdiniCliente"));
                            txtOFMarconi.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 72:
                            txtEsPirri.setText(res.getString("Esistenza"));
                            txtOCPirri.setText(res.getString("OrdiniCliente"));
                            txtOFPirri.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 76:
                            txtEsOlbia.setText(res.getString("Esistenza"));
                            txtOCOlbia.setText(res.getString("OrdiniCliente"));
                            txtOFOlbia.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 74:
                            txtEsSassari.setText(res.getString("Esistenza"));
                            txtOCSassari.setText(res.getString("OrdiniCliente"));
                            txtOFSassari.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 32:
                            txtEsNuoro.setText(res.getString("Esistenza"));
                            txtOCNuoro.setText(res.getString("OrdiniCliente"));
                            txtOFNuoro.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 78:
                            txtEsCarbonia.setText(res.getString("Esistenza"));
                            txtOCCarbonia.setText(res.getString("OrdiniCliente"));
                            txtOFCarbonia.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 75:
                            txtEsTortoli.setText(res.getString("Esistenza"));
                            txtOCTortoli.setText(res.getString("OrdiniCliente"));
                            txtOFTortoli.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 71:
                            txtEsOristano.setText(res.getString("Esistenza"));
                            txtOCOristano.setText(res.getString("OrdiniCliente"));
                            txtOFOristano.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 85:
                            txtEsTiburtina.setText(res.getString("Esistenza"));
                            txtOCTiburtina.setText(res.getString("OrdiniCliente"));
                            txtOFTiburtina.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 87:
                            txtEsCapena.setText(res.getString("Esistenza"));
                            txtOCCapena.setText(res.getString("OrdiniCliente"));
                            txtOFCapena.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 86:
                            txtEsOstiense.setText(res.getString("Esistenza"));
                            txtOCOstiense.setText(res.getString("OrdiniCliente"));
                            txtOFOstiense.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 90:
                            txtEsCasilina.setText(res.getString("Esistenza"));
                            txtOCCasilina.setText(res.getString("OrdiniCliente"));
                            txtOFCasilina.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 94:
                            txtEsPom.setText(res.getString("Esistenza"));
                            txtOCPom.setText(res.getString("OrdiniCliente"));
                            txtOFPom.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 111:
                            txtEsRC.setText(res.getString("Esistenza"));
                            txtOCRC.setText(res.getString("OrdiniCliente"));
                            txtOFRC.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 112:
                            txtEsArd.setText(res.getString("Esistenza"));
                            txtOCArd.setText(res.getString("OrdiniCliente"));
                            txtOFArd.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 114:
                            txtEsVer.setText(res.getString("Esistenza"));
                            txtOCVer.setText(res.getString("OrdiniCliente"));
                            txtOFVer.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 91:
                            txtEsDepR.setText(res.getString("Esistenza"));
                            txtOCDepR.setText(res.getString("OrdiniCliente"));
                            txtOFDepR.setText(res.getString("OrdiniFornitore"));
                            break;
                        default:
                            break;
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
        recuperaPV();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(InvDep.this)
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
                    String query = "select RigaDocumentoCommerciale.codiceArticolo, RigaDocumentoCommerciale.quantita, RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie " +
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
                            dataC.add("2023-01-01");
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

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            pbCD.setVisibility(View.VISIBLE);
            desc = "";
        }

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                txtCodArt.setText(artOrForn);
                txtDesc.setText(desc);
                recuperaGiacenze();
                txtEsThis.setText(risolviMagEs());
                txtEsThis2.setText(risolviMagEs());
                txtOFThis.setText(risolviMagXOf());
                txtOCThis.setText(risolviMagXOc());
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pbCD.setVisibility(View.GONE);
                txtEsThis.setText("0");
                txtEsThis2.setText("0");
                txtUbic.setText("N/A");
                txtUbic2.setText("N/A");
                txtSubic.setText("N/A");
                txtSubic2.setText("N/A");
                articoloInesistente("Attenzione!","Articolo non presente nel database, inserisci una nota o premi annulla per tornare indietro");
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
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo)" +
                            "WHERE articolo.nome like '" + artOrForn + "'" +
                            "OR alias.codice = '" + artOrForn + "'";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        isSuccess = true;
                        artOrForn = res.getString("nome");
                        desc = res.getString("descrizione");
                        alias = res.getString("codice");
                    }else{
                        isSuccess = false;
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
                    String query = "INSERT INTO mcInvDoc (codArt, descrizione, alias, ubic, subic, qtaInv, esistenza, note, store, palmare, utente)" +
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
                            "FROM mcInvDoc a " +
                            "WHERE palmare = '"+nomeP+"' ";
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

    public class AggDoc extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        Integer qta;

        String codArt;

        public AggDoc(Integer qta, String codArt){
            this.qta = qta;
            this.codArt = codArt;
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
                    String query = "UPDATE mcInvDoc " +
                            "SET qtaInv = "+qta+" " +
                            "WHERE codArt = '"+codArt+"' and palmare = '"+nomeP+"' and utente = '"+utente+"' ";
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

    public class ChangeUbic extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;
        String ubicAtt = "";
        String subicAtt = "";
        String esAtt = "";

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                alertModUbic(artOrForn,"Esistenza: " + esAtt);
            }
        }

        private void alertModUbic(String title,String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(InvDep.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Annulla",(dialog, which) -> {
                        dialog.cancel();
                    });

            LinearLayout layout = new LinearLayout(InvDep.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText ubic1 = new EditText(InvDep.this);
            ubic1.setText(ubicAtt);
            ubic1.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(ubic1);

            builder.setView(layout);

            final EditText ubic2 = new EditText(InvDep.this);
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
                            "where nome = '"+ artOrForn +"' and articoloxmagazzino.idmagazzino = "+mag+" ";
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
                            "where nome = '"+ artOrForn +"' and da<GETDATE() and a > GETDATE() and metamagazzino = "+mag+" ";
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
}