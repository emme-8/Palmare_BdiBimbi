package com.emme.palmarebdibimbi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import static android.view.View.GONE;

public class AdapterReviewSpuntaNeg extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt, nDoc;
    private ArrayList<String> desc, alias;
    private ArrayList<String> qtaDoc, qtaSpunta, timeSp;

    String docsName;
    Integer tipo;

    public AdapterReviewSpuntaNeg(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam,
                               ArrayList<String> qtaDocArrayParam, ArrayList<String> qtaSpArrayParam, ArrayList<String> aliasArrayParam,
                                  ArrayList<String> nDoc, String docsName, Integer tipo, ArrayList<String> timeSp) {

        super(context, R.layout.adapter_review_spunta_neg, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qtaDoc = qtaDocArrayParam;
        this.qtaSpunta = qtaSpArrayParam;
        this.alias = aliasArrayParam;
        this.timeSp = timeSp;
        this.nDoc = nDoc;

        this.tipo = tipo;
        this.docsName = docsName;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_review_spunta_neg, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowCD);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRowCD);
        TextView txtQtaSpunta = rowView.findViewById(R.id.txtQtaRowCD);
        TextView txtQtaDocSpunta = rowView.findViewById(R.id.txtQtaRowDocSpuntaNeg);
        TextView txtDiff = rowView.findViewById(R.id.txtQtaRowDiffNeg);
        Button btnMod = rowView.findViewById(R.id.btnModRigaCD);
        LinearLayout backg = rowView.findViewById(R.id.rowSpuntaNeg);

        TextView canc1 = rowView.findViewById(R.id.textView5CD);
        TextView canc2 = rowView.findViewById(R.id.txtNDocaCD);
        TextView canc3 = rowView.findViewById(R.id.infoTextViewIDSpuntaNeg);
        TextView canc4 = rowView.findViewById(R.id.textView14CD);
        TextView canc5 = rowView.findViewById(R.id.textView16Neg);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        txtQtaDocSpunta.setText(qtaDoc.get(position));
        txtQtaSpunta.setText(qtaSpunta.get(position));
        Integer diff = Integer.parseInt(qtaSpunta.get(position)) - Integer.parseInt(qtaDoc.get(position));
        if(diff == 0){
            txtCodArt.setVisibility(GONE);
            txtDesc.setVisibility(GONE);
            txtQtaSpunta.setVisibility(GONE);
            txtQtaDocSpunta.setVisibility(GONE);
            txtDiff.setVisibility(GONE);
            btnMod.setVisibility(GONE);
            backg.setVisibility(GONE);
            canc1.setVisibility(GONE);
            canc2.setVisibility(GONE);
            canc3.setVisibility(GONE);
            canc4.setVisibility(GONE);
            canc5.setVisibility(GONE);
            rowView.setVisibility(GONE);
        }else if(diff < 0){
            backg.setBackgroundColor(Color.RED);
        }else{
            backg.setBackgroundColor(Color.YELLOW);
        }
        txtDiff.setText(diff.toString());

        btnMod.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modifica valori")
                    .setMessage("Quantità spunta");

            final EditText input = new EditText(context);

            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("Salva", (dialog, which) -> {
                XSSFWorkbook workbook;

                try {
                    String outFileName = docsName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    Row row = workbook.getSheetAt(0).getRow(position+1);
                    row.createCell(6).setCellValue(input.getText().toString());
                    Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                    row.getCell(7).setCellValue(newQta.toString());
                    qtaSpunta.set(position, input.getText().toString());
                    txtQtaDocSpunta.setText(input.getText().toString());

                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        Button saveDoc = context.findViewById(R.id.btnSaveDocSpuntaNeg);
        saveDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int z=0;
                for (int i = 0; i < codArt.size(); i++) {
                    if(Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i)) != 0){
                        z++;
                    }
                }
                File file = new File("/storage/emulated/0/NAS/SpuntaDiff", "DIFF_"+z+"_"+docsName);
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet

                Row testata = sheet.createRow(0);
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

                int j=0;
                for (int i = 0; i < codArt.size(); i++) {
                    if(Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i)) != 0){
                        Row row = sheet.createRow(j+1);
                        row.createCell(0).setCellValue(codArt.get(i));
                        row.createCell(1).setCellValue(desc.get(i));
                        Cell num2 = row.createCell(2);
                        num2.setCellValue(alias.get(i));
                        num2.setCellType(CellType.STRING);
                        row.createCell(3).setCellValue("");
                        row.createCell(4).setCellValue("");
                        row.createCell(5).setCellValue(qtaDoc.get(i));
                        row.createCell(6).setCellValue(qtaSpunta.get(i));
                        int risultato = Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i));
                        row.createCell(7).setCellValue(risultato);
                        row.createCell(8).setCellValue(nDoc.get(i));
                        row.createCell(9).setCellValue(timeSp.get(i));
                        j++;
                    }
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
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                String email = p.getString("Email", "");
                String emailPass = p.getString("EmailPass", "");

                ArrayList<String> pathToSend = new ArrayList<>();
                pathToSend.add("/storage/emulated/0/NAS/SpuntaGen/"+ docsName);
                pathToSend.add("/storage/emulated/0/NAS/SpuntaDiff/"+ "DIFF_"+z+"_"+docsName);

                String obj = docsName;
                String[] to = new String[]{"spunte@bdibimbi.it", email};
                try {
                    sendEmail(to,email, obj, " ", pathToSend, email, emailPass);
                    alertDisplayer("Attenzione!", "Documento di spunta creato con successo, verrai riportato alla home");
                } catch (Exception e) {
                    e.printStackTrace();
                    emailError("Errore","E' avvenuto un errore durante l'invio del documento",to, obj, email, pathToSend, emailPass);
                }

            }
        });
        return rowView;

    }

    private void emailError(String title,String message, String[] to, String obj, String email, ArrayList<String> pathToSend, String emailPass){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Riprova", ((dialog, which) -> {
                    try {
                        sendEmail(to,email, obj, " ", pathToSend, email, emailPass);
                        alertDisplayer("Attenzione!", "Documento inviato con successo, verrai riportato alla home");
                    } catch (Exception e) {
                        e.printStackTrace();
                        emailError("Errore","E' avvenuto un errore durante l'invio del documento",to, obj, email, pathToSend, emailPass);
                    }
                }))
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer("Attenzione!", "Documento non inviato, riprova più tardi, verrai riportato alla home");
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
/*
    public void putInExcelFileDiff() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet

        Row testata = sheet.createRow(0);
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

        for (int i = 0; i < codArt.size(); i++) {
            Row row = sheet.createRow(i+1);
            if(Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i)) != 0){
                row.createCell(0).setCellValue(codArt.get(i));
                row.createCell(1).setCellValue(desc.get(i));
                Cell num2 = row.createCell(2);
                num2.setCellValue(alias.get(i));
                num2.setCellType(CellType.STRING);
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue("");
                row.createCell(5).setCellValue(qtaDoc.get(i));
                row.createCell(6).setCellValue(qtaSpunta.get(i));
                int risultato = Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i));
                row.createCell(7).setCellValue(risultato);
                row.createCell(8).setCellValue(nDoc.get(i));
                row.createCell(9).setCellValue(timeSp.get(i));
            }else{
                row.createCell(0).setCellValue(" ");
            }
        }

        int numOfRows = sheet.getLastRowNum();
        for (int i = 0; i < numOfRows; i++){
            Row thisRow = sheet.getRow(i);
            Cell cell = thisRow.getCell(0);
            //Detect if a row is blank
            if (cell.getStringCellValue().equals(" ")){
                thisRow.setZeroHeight(true);
            }
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm","");
        String fileName; //Name of the file
        if(tipo==0){
            fileName = nomeP+"spuntaDiff_"+docsName+".xlsx"; //Name of the file
        }else{
            fileName = nomeP+"inventarioDiff_"+docsName+".xlsx"; //Name of the file
        }

        File file = new File("/storage/emulated/0/NAS/SpuntaDiff", fileName);
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


        ArrayList<File> oldFiles = getAllFilesInDir(new File("/storage/emulated/0/NAS/SpuntaDiff"));

        for(int i = 0; i<oldFiles.size(); i++){
            if(oldFiles.get(i).exists()){
                Calendar time = Calendar.getInstance();
                time.add(Calendar.DAY_OF_YEAR,-7);
                //I store the required attributes here and delete them
                Date lastModified = new Date(oldFiles.get(i).lastModified());
                if(lastModified.before(time.getTime())) {
                    //file is older than a week
                    oldFiles.get(i).delete();
                }
            }
        }


        putInExcelFile();
    }
    */
/*
    public void putInExcelFile() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Spunta"); //Creating a sheet

        for (int i = 0; i < codArt.size(); i++) {

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
            }
            row.createCell(0).setCellValue(codArt.get(i));
            row.createCell(1).setCellValue(desc.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(alias.get(i));
            num2.setCellType(CellType.STRING);
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(qtaDoc.get(i));
            row.createCell(6).setCellValue(qtaSpunta.get(i));
            int risultato = Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i));
            row.createCell(7).setCellValue(risultato);
            row.createCell(8).setCellValue(nDoc.get(i));
            row.createCell(9).setCellValue(timeSp.get(i));
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm","");
        String fileName; //Name of the file
        if(tipo==0){
            fileName = nomeP+"spunta_"+docsName+".xlsx"; //Name of the file
        }else{
            fileName = nomeP+"inventario_"+docsName+".xlsx"; //Name of the file
        }


        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOnline = preferences.getBoolean("isOnline",true);
        if(tipo==0){
            if(isOnline){
                preferences.edit().remove("codInt").apply();
                preferences.edit().remove("descInt").apply();
                preferences.edit().remove("magInt").apply();
                preferences.edit().remove("listInt").apply();
                preferences.edit().remove("aliasInt").apply();
                preferences.edit().remove("nameInt").apply();
                preferences.edit().remove("qtaDocInt").apply();
                preferences.edit().remove("qtaResInt").apply();
                preferences.edit().remove("qtaInt").apply();
                preferences.edit().remove("numInt").apply();
                preferences.edit().remove("timeInt").apply();
            }else{
                preferences.edit().remove("articoliOff").apply();
                preferences.edit().remove("nameIntOff").apply();
            }
        }else{
            preferences.edit().remove("codIntInv").apply();
            preferences.edit().remove("descIntInv").apply();
            preferences.edit().remove("magIntInv").apply();
            preferences.edit().remove("listIntInv").apply();
            preferences.edit().remove("aliasIntInv").apply();
            preferences.edit().remove("nameIntInv").apply();
            preferences.edit().remove("qtaDocIntInv").apply();
            preferences.edit().remove("qtaResIntInv").apply();
            preferences.edit().remove("qtaIntInv").apply();
            preferences.edit().remove("numIntInv").apply();
        }

        String email = p.getString("Email", "");
        String emailPass = p.getString("EmailPass", "");
        String filePath = "/storage/emulated/0/NAS/SpuntaGen/"+ fileName;
        String obj = nomeP+" spunta "+docsName;
        String[] to = new String[]{"spunte@bdibimbi.it", email};
        try {
            sendEmail(to,email, obj, " ", filePath, email, emailPass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        alertDisplayer("Attenzione!", "Documento di spunta creato con successo, verrai riportato alla home");
    }

    public static ArrayList<File> getAllFilesInDir(File dir) {
        if (dir == null)
            return null;

        ArrayList<File> files = new ArrayList<File>();

        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();
        dirlist.push(dir);

        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();

            File[] fileList = dirCurrent.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory())
                    dirlist.push(aFileList);
                else
                    files.add(aFileList);
            }
        }

        return files;
    }

 */

    public static boolean sendEmail(String[] to, String from, String subject,
                                    String message,ArrayList<String> attachement, String user, String pass) throws Exception {
        GMailSender mail = new GMailSender();

        if (user != null && user.length() > 0) {
            mail.setUser(user);
            mail.setFrom(from);
        } else {
            mail.setUser("User");
            mail.setFrom("From");
        }

        if (pass != null && pass.length() > 0) {
            mail.setPassword(pass);
        } else {
            mail.setPassword("Password");
        }

        if (subject != null && subject.length() > 0) {
            mail.setSubject(subject);
        } else {
            mail.setSubject("Subject");
        }

        if (message != null && message.length() > 0) {
            mail.setBody(message);
        } else {
            mail.setBody("Message");
        }

        mail.setTo(to);

        if (attachement != null) {
            for(int i=0; i< attachement.size(); i++){
                mail.addAttachment(attachement.get(i));
            }
        }
        return mail.send();
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent retHome = new Intent(context, MainActivity.class);
                    context.startActivity(retHome);
                    context.finish();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}
