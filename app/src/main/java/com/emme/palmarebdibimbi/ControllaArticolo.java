package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControllaArticolo extends AppCompatActivity {

    String codArt = "", fileName = "", desc = "", ean = "", art = "", utente, store, categoria;
    boolean isPres = false;
    Context context;
    int i, mag, giaPremuto = 0;
    Button btnFatto, btnReg, btnGo;
    TextView txtCod, txtDesc, txtEan, txtQta, txtEs;
    ConnectionClass connectionClass;
    EditText insCod, insQta;
    Switch switchQta;

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
        setContentView(R.layout.activity_controlla_articolo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;
        connectionClass = new ConnectionClass();

        btnFatto = findViewById(R.id.btnArtCon);
        txtCod = findViewById(R.id.codTC);
        txtDesc = findViewById(R.id.descTC);
        txtEan = findViewById(R.id.eanTC);
        txtQta = findViewById(R.id.qtaTC);
        txtEs = findViewById(R.id.esTC);
        switchQta = findViewById(R.id.swtQta);
        insCod = findViewById(R.id.codRTC);
        insQta = findViewById(R.id.insQtaTC);
        btnReg = findViewById(R.id.btnRegTC);
        btnGo = findViewById(R.id.btnGoTC);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            codArt = extras.getString("codArt");
            fileName = extras.getString("fileName");
            desc = extras.getString("desc");
            ean = extras.getString("ean");
            mag = extras.getInt("mag");

            utente = extras.getString("utente");
            categoria = extras.getString("categoria");
            store = extras.getString("store");
        }

        txtCod.setText(codArt);
        txtDesc.setText(desc);
        txtEan.setText(ean);

        XSSFWorkbook workbook;
        try {
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);
            i = 0;
            int j = 0;

            while(workbook.getSheetAt(0).getRow(j) != null){
                Row row = workbook.getSheetAt(0).getRow(j);
                if(row.getCell(0).getStringCellValue().equals(codArt)){
                    isPres = true;
                    Integer qtaSp = (int)row.getCell(2).getNumericCellValue();
                    Integer qtaEs = (int)row.getCell(3).getNumericCellValue();
                    txtEs.setText(""+qtaEs.toString());
                    txtQta.setText(""+qtaSp.toString());
                }
                j++;
                if(!isPres){
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

        if(!isPres){
            txtEs.setText("0");
            FindArt findArt = new FindArt();
            findArt.execute();
        }

        switchQta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    insQta.setEnabled(false);
                }else{
                    insQta.setEnabled(true);
                }
            }
        });

        insCod.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    insQta.setFocusable(false);
                    insQta.setFocusableInTouchMode(false);
                    art = insCod.getText().toString();
                    CheckIfHim checkIfHim = new CheckIfHim();
                    checkIfHim.execute();
                }
            }
            btnReg.setEnabled(true);
            switchQta.setEnabled(true);
            btnFatto.setEnabled(true);
            return false;
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!insCod.getText().toString().equals("")){
                    regArt();
                }
                insCod.setFocusableInTouchMode(true);
                insCod.requestFocus();
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insQta.setFocusable(false);
                insQta.setFocusableInTouchMode(false);
                art = insCod.getText().toString();
                CheckIfHim checkIfHim = new CheckIfHim();
                checkIfHim.execute();

            }
        });
        btnFatto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XSSFWorkbook workbook;
                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    Row row = workbook.getSheetAt(0).getRow(i);
                    if(row.getCell(2).getNumericCellValue()==0){
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ControllaArticolo.this)
                                .setTitle("Attenzione!")
                                .setMessage("Non hai inserito nessuna quantità, in questo modo la giacenza verrà azzerata, premi OK per continuare, ELIMINA per annullare");

                        builder.setPositiveButton("OK", (dialog, which) -> {
                            dialog.cancel();
                            Intent review = new Intent(context, ControlloInventario.class);

                            review.putExtra("utente", utente);
                            review.putExtra("categoria", categoria);
                            review.putExtra("storeName", store);

                            context.startActivity(review);
                        });
                        builder.setNegativeButton("ELIMINA", (dialog, which) -> {
                            workbook.getSheetAt(0).removeRow(row);
                            try {
                                file.close();
                                FileOutputStream outFile = null;
                                outFile = new FileOutputStream(new File(path, outFileName));
                                workbook.write(outFile);
                                outFile.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            dialog.cancel();
                            Intent review = new Intent(context, ControlloInventario.class);

                            review.putExtra("utente", utente);
                            review.putExtra("categoria", categoria);
                            review.putExtra("storeName", store);

                            context.startActivity(review);
                        });
                        android.app.AlertDialog ok = builder.create();
                        ok.show();
                    }else{
                        Intent review = new Intent(context, ControlloInventario.class);

                        review.putExtra("utente", utente);
                        review.putExtra("categoria", categoria);
                        review.putExtra("storeName", store);

                        context.startActivity(review);
                    }
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void regArt(){
        if(!insQta.getText().toString().equals("") && insQta.getText().toString().length()<5 && !insQta.getText().toString().contains(" ")
                && !insQta.getText().toString().contains(",") && !insQta.getText().toString().contains(".") && !insQta.getText().toString().equals("-")){

            writeSheet(txtCod.getText().toString(), insQta.getText().toString());

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
            play.play();

            insCod.setEnabled(true);
            insCod.setText("");
            insQta.setText("1");
            insQta.setFocusable(false);
            insCod.setFocusableInTouchMode(true);
            insCod.requestFocus();
            insQta.setFocusable(true);
            insCod.setFocusableInTouchMode(true);
            insCod.requestFocus();
            if(switchQta.isChecked()){
                hideKeyboard(this);
            }
            insCod.setEnabled(true);
            insCod.setFocusableInTouchMode(true);
            insCod.requestFocus();
        }else{
            alertArt("Errore!","Devi inserire una quantità valida per poter registrare l'articolo");
        }
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ControllaArticolo.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }


    public void writeSheet(String codArt, String qta) {

        XSSFWorkbook workbook;

        try {
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            boolean present = false;
            while(workbook.getSheetAt(0).getRow(i) != null && !present){
                Row row = workbook.getSheetAt(0).getRow(i);
                if(row.getCell(0).getStringCellValue().equals(codArt)){
                    present = true;

                    double convQta = row.getCell(2).getNumericCellValue();
                    Integer newQta = (int)convQta + Integer.parseInt(qta);
                    row.getCell(2).setCellValue(newQta);

                    double convQtaDiff = row.getCell(1).getNumericCellValue();
                    Integer newQtaDiff = (int)convQtaDiff + Integer.parseInt(qta);
                    row.getCell(1).setCellValue(newQtaDiff);

                    Integer qtaSp = (int) row.getCell(2).getNumericCellValue();
                    txtQta.setText(""+qtaSp.toString());
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

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        Integer es = 0;

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess){
                txtEs.setText(""+es.toString());
                XSSFWorkbook workbook;
                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    Row row = workbook.getSheetAt(0).createRow(i);
                    row.createCell(0).setCellValue(codArt);
                    row.createCell(1).setCellValue(es*-1);
                    row.createCell(2).setCellValue(0);
                    row.createCell(3).setCellValue(es);
                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
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
                String query = "select cast (ProgressivoArticolo.esistenza as int) as esistenza " +
                        "from ProgressivoArticolo join articolo on ProgressivoArticolo.MetaArticolo = articolo.id  \n" +
                        "where articolo.nome = '"+ codArt +"' and da < GETDATE() and a > GETDATE() and metamagazzino = "+mag+" " +
                        "or articolo.nome = '"+ codArt.trim() +"' and da < GETDATE() and a > GETDATE() and metamagazzino = "+mag+" ";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                if(res.next()) {
                    es = res.getInt("esistenza");
                }
                isSuccess = true;
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

    public class CheckIfHim extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess){
                if(!switchQta.isChecked()){
                    insCod.setEnabled(false);
                    insCod.clearFocus();
                    insQta.setEnabled(true);
                    insQta.setFocusable(true);
                    insQta.setFocusableInTouchMode(true);
                    insQta.requestFocus();
                    insQta.setCursorVisible(true);
                    insQta.setSelectAllOnFocus(true);
                    showSoftKeyboard(insQta);
                }else{
                    insQta.setEnabled(false);

                    btnFatto.setEnabled(false);
                    switchQta.setEnabled(false);
                    btnReg.setEnabled(false);

                    btnReg.clearFocus();
                    switchQta.clearFocus();
                    insQta.clearFocus();
                    insCod.setFocusable(true);
                    insCod.setFocusableInTouchMode(true);
                    insCod.requestFocus();
                    regArt();
                }
            }else{
                alertArt("Errore!","Il codice inserito non corrisponde all'articolo "+codArt+" ");
            }
            btnReg.setEnabled(true);
            switchQta.setEnabled(true);
            btnFatto.setEnabled(true);
            giaPremuto = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome as name " +
                            "from articolo left join alias on articolo.id = alias.idarticolo " +
                            "where articolo.nome = '"+ art +"' or articolo.nome = '"+ art +"' " +
                            "or alias.codice = '"+art+"' or alias.codice = '"+ art +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        if(res.getString("name").equals(codArt)){
                            isSuccess = true;
                        }
                    }
                }
            }catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
                ex.getMessage();
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