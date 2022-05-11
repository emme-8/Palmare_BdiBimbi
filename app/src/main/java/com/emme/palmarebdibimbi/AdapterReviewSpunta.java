package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import static android.view.View.GONE;

public class AdapterReviewSpunta extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt, alias;
    private ArrayList<String> desc, nDoc, note;
    private ArrayList<String> ubic, subic;
    private ArrayList<String> qtaDoc, qtaSpunta, timeSp, qtaColli;
    private int tipo;
    String docsName;
    String magazzino;
    private ArrayList<String> newcodArt, newalias;
    private ArrayList<String> newdesc, newnDoc, newNote;
    private ArrayList<String> newubic, newsubic;
    private ArrayList<String> newqtaDoc, newqtaSpunta, newtimeSp, newQtaColli;

    public AdapterReviewSpunta(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> qtaDocArrayParam,
                               ArrayList<String> qtaSpArrayParam, ArrayList<String> ubicArrayParam, ArrayList<String> subicArrayParam, ArrayList<String> aliasArrayParam,
                               ArrayList<String> nDoc, int tipo, String docsName, String magazzino, ArrayList<String> timeSp, ArrayList<String> note) {

        super(context, R.layout.adapter_review_spunta, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qtaDoc = qtaDocArrayParam;
        this.qtaSpunta = qtaSpArrayParam;
        this.ubic = ubicArrayParam;
        this.subic = subicArrayParam;
        this.alias = aliasArrayParam;
        this.nDoc = nDoc;
        this.timeSp = timeSp;
        this.note = note;

        this.newcodArt =new ArrayList<>();
        this.newdesc = new ArrayList<>();
        this.newqtaDoc = new ArrayList<>();
        this.newqtaSpunta = new ArrayList<>();
        this.newubic = new ArrayList<>();
        this.newsubic = new ArrayList<>();
        this.newalias = new ArrayList<>();
        this.newnDoc = new ArrayList<>();
        this.newtimeSp = new ArrayList<>();
        this.newNote = new ArrayList<>();

        this.tipo = tipo;
        this.docsName = docsName;
        this.magazzino = magazzino;
    }

    public AdapterReviewSpunta(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> qtaDocArrayParam,
                               ArrayList<String> qtaSpArrayParam, ArrayList<String> ubicArrayParam, ArrayList<String> subicArrayParam, ArrayList<String> aliasArrayParam,
                               ArrayList<String> nDoc, int tipo, String docsName, ArrayList<String> note, ArrayList<String> timeSp, ArrayList<String> qtaColli) {

        super(context, R.layout.adapter_review_spunta, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qtaDoc = qtaDocArrayParam;
        this.qtaSpunta = qtaSpArrayParam;
        this.ubic = ubicArrayParam;
        this.subic = subicArrayParam;
        this.alias = aliasArrayParam;
        this.nDoc = nDoc;
        this.note = note;
        this.timeSp = timeSp;
        this.qtaColli = qtaColli;

        this.newcodArt =new ArrayList<>();
        this.newdesc = new ArrayList<>();
        this.newqtaDoc = new ArrayList<>();
        this.newqtaSpunta = new ArrayList<>();
        this.newubic = new ArrayList<>();
        this.newsubic = new ArrayList<>();
        this.newalias = new ArrayList<>();
        this.newnDoc = new ArrayList<>();
        this.newNote = new ArrayList<>();
        this.newtimeSp = new ArrayList<>();
        this.newQtaColli = new ArrayList<>();

        this.tipo = tipo;
        this.docsName = docsName;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_review_spunta, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowSpunta);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRowSpunta);
        TextView txtQtaSpunta = rowView.findViewById(R.id.txtQtaRowSpunta);
        TextView txtQtaDocSpunta = rowView.findViewById(R.id.txtQtaRowDocSpunta);
        TextView txtDiff = rowView.findViewById(R.id.txtQtaRowDiff);
        TextView txtUbic = rowView.findViewById(R.id.txtUbicRev);
        TextView txtSubic = rowView.findViewById(R.id.txtSubicRev);
        TextView txtTColli = rowView.findViewById(R.id.txtTColli);
        TextView txtColli = rowView.findViewById(R.id.txtColli);
        Button btnMod = rowView.findViewById(R.id.btnModRigaRev);
        LinearLayout backg = rowView.findViewById(R.id.rowSpunta);

        TextView canc1 = rowView.findViewById(R.id.textView5Spunta);
        TextView canc2 = rowView.findViewById(R.id.txtNDocaSpunta);
        TextView canc3 = rowView.findViewById(R.id.infoTextViewIDSpunta);
        TextView canc4 = rowView.findViewById(R.id.textView14);
        TextView canc5 = rowView.findViewById(R.id.textView16);
        TextView canc6 = rowView.findViewById(R.id.textView19);
        TextView canc7 = rowView.findViewById(R.id.textView18);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        txtQtaDocSpunta.setText(qtaDoc.get(position));
        txtQtaSpunta.setText(qtaSpunta.get(position));
        Integer diff = Integer.parseInt(qtaSpunta.get(position)) - Integer.parseInt(qtaDoc.get(position));
        if(tipo!=0){
            txtTColli.setVisibility(GONE);
            txtColli.setVisibility(GONE);
        }else{
            txtColli.setText(qtaColli.get(position));
        }
        if (diff == 0) {
            txtCodArt.setVisibility(GONE);
            txtDesc.setVisibility(GONE);
            txtQtaSpunta.setVisibility(GONE);
            txtQtaDocSpunta.setVisibility(GONE);
            txtDiff.setVisibility(GONE);
            txtUbic.setVisibility(GONE);
            txtColli.setVisibility(GONE);
            txtTColli.setVisibility(GONE);
            txtSubic.setVisibility(GONE);
            btnMod.setVisibility(GONE);
            backg.setVisibility(GONE);
            canc1.setVisibility(GONE);
            canc2.setVisibility(GONE);
            canc3.setVisibility(GONE);
            canc4.setVisibility(GONE);
            canc5.setVisibility(GONE);
            canc6.setVisibility(GONE);
            canc7.setVisibility(GONE);
            rowView.setVisibility(GONE);
        } else if (diff < 0) {
            newcodArt.add(codArt.get(position));
            newdesc.add(desc.get(position));
            newqtaDoc.add(qtaDoc.get(position));
            newqtaSpunta.add(qtaSpunta.get(position));
            newubic.add(ubic.get(position));
            newsubic.add(subic.get(position));
            newalias.add(alias.get(position));
            newnDoc.add(nDoc.get(position));
            if(tipo == 0){
                newQtaColli.add(qtaColli.get(position));
                newNote.add(note.get(position));
            }
            newtimeSp.add(timeSp.get(position));
            if(tipo!=0 && qtaSpunta.get(position).equals("0")){
                backg.setBackgroundColor(Color.RED);
            }else if(tipo!=0 && !qtaSpunta.get(position).equals("0")){
                backg.setBackgroundColor(Color.parseColor("#ffa500"));
            }else {
                backg.setBackgroundColor(Color.RED);
            }
        } else {
            newcodArt.add(codArt.get(position));
            newdesc.add(desc.get(position));
            newqtaDoc.add(qtaDoc.get(position));
            newqtaSpunta.add(qtaSpunta.get(position));
            newubic.add(ubic.get(position));
            newsubic.add(subic.get(position));
            newalias.add(alias.get(position));
            newnDoc.add(nDoc.get(position));
            newtimeSp.add(timeSp.get(position));
            if(tipo == 0){
                newQtaColli.add(qtaColli.get(position));
                newNote.add(note.get(position));
            }
            backg.setBackgroundColor(Color.YELLOW);
        }
        txtDiff.setText(diff.toString());
        txtUbic.setText(ubic.get(position));
        txtSubic.setText(subic.get(position));

        btnMod.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modifica valori");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText qta = new EditText(context);
            qta.setHint("Quantità");
            layout.addView(qta);

            final EditText ubicazione = new EditText(context);
            ubicazione.setHint("Ubicazione");
            layout.addView(ubicazione);

            final EditText subicazione = new EditText(context);
            subicazione.setHint("Sottoubicazione");
            layout.addView(subicazione);

            builder.setView(layout);

            builder.setPositiveButton("Salva", (dialog, which) -> {

                XSSFWorkbook workbook;

                try {
                    String outFileName = docsName;
                    File path = null;
                    if(tipo==0){
                        path = new File("/storage/emulated/0/NAS/SpuntaGen");
                    }else{
                        path = new File("/storage/emulated/0/NAS/PresaGen");
                    }

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    Row row = workbook.getSheetAt(0).getRow(position+1);

                    if (!qta.getText().toString().equals("")) {
                        row.createCell(6).setCellValue(qta.getText().toString());
                        Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                        row.getCell(7).setCellValue(newQta.toString());
                        qtaSpunta.set(position, qta.getText().toString());
                        txtQtaDocSpunta.setText(qta.getText().toString());
                    }
                    if (!ubicazione.getText().toString().equals("")) {
                        row.createCell(3).setCellValue(ubicazione.getText().toString());
                        ubic.set(position, ubicazione.getText().toString());
                        txtUbic.setText(ubicazione.getText().toString());
                    }
                    if (!subicazione.getText().toString().equals("")) {
                        row.createCell(4).setCellValue(subicazione.getText().toString());
                        subic.set(position, subicazione.getText().toString());
                        txtSubic.setText(subicazione.getText().toString());
                    }

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

        Button saveDoc = context.findViewById(R.id.btnSaveDocSpunta);
        saveDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                int z=0;
                for (int i = 0; i < codArt.size(); i++) {
                    if(Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i)) != 0){
                        z++;
                    }
                }
                if(tipo==0){
                    file = new File("/storage/emulated/0/NAS/SpuntaDiff", "DIFF_"+z+"_"+docsName);
                }else{
                    file = new File("/storage/emulated/0/NAS/PresaDiff", "DIFF_"+z+"_"+docsName);
                }
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet; //Creating a sheet
                if(tipo==0){
                    sheet = workbook.createSheet("Spunta");
                }else{
                    sheet = workbook.createSheet("Presa");
                }

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
                testata.createCell(9).setCellValue("Note");
                testata.createCell(10).setCellValue("Store");
                testata.createCell(11).setCellValue("Sparata");

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
                        row.createCell(9).setCellValue(note.get(i));
                        if(tipo!=0){
                            if(magazzino!=null){
                                if(magazzino.equals("SESTU")){
                                    magazzino = "ELMAS";
                                }
                            }
                        }
                        row.createCell(10).setCellValue(magazzino);
                        row.createCell(11).setCellValue(timeSp.get(i));
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
                if(tipo==0){
                    pathToSend.add("/storage/emulated/0/NAS/SpuntaGen/"+ docsName);
                    pathToSend.add("/storage/emulated/0/NAS/SpuntaDiff/"+ "DIFF_"+z+"_"+docsName);
                }else{
                    pathToSend.add("/storage/emulated/0/NAS/PresaGen/"+ docsName);
                    pathToSend.add("/storage/emulated/0/NAS/PresaDiff/"+ "DIFF_"+z+"_"+docsName);
                }

                String obj = docsName;
                String[] to = new String[]{"spunte@bdibimbi.it", email};
                try {
                    sendEmail(to,email, obj, " ", pathToSend, email, emailPass);
                    alertDisplayer("Attenzione!", "Documento inviato con successo, verrai riportato alla home");
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

    public void putInExcelFileDiff() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet; //Creating a sheet
        if(tipo==0){
            sheet = workbook.createSheet("Spunta");
        }else{
            sheet = workbook.createSheet("Presa");
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm","");

        for (int i = 0; i < newcodArt.size(); i++) {

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
                testata.createCell(8).setCellValue("nDoc");
                testata.createCell(9).setCellValue("Note");
                testata.createCell(10).setCellValue("Magazzino");
            }
            row.createCell(0).setCellValue(newcodArt.get(i));
            row.createCell(1).setCellValue(newdesc.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(newalias.get(i));
            num2.setCellType(CellType.STRING);
            Cell num3 = row.createCell(3);
            num3.setCellValue(newubic.get(i));
            num3.setCellType(CellType.STRING);
            Cell num4 = row.createCell(4);
            num4.setCellValue(newsubic.get(i));
            num4.setCellType(CellType.STRING);
            row.createCell(5).setCellValue(newqtaDoc.get(i));
            row.createCell(6).setCellValue(newqtaSpunta.get(i));
            int risultato = Integer.parseInt(newqtaSpunta.get(i)) - Integer.parseInt(newqtaDoc.get(i));
            row.createCell(7).setCellValue(risultato);
            row.createCell(8).setCellValue(newnDoc.get(i));
            if(tipo == 0){
                row.createCell(9).setCellValue(newNote.get(i));
            }else{
                row.createCell(9).setCellValue("");
            }
            if(tipo!=0){
                if(magazzino!=null){
                    if(magazzino.equals("SESTU")){
                        magazzino = "ELMAS";
                    }
                }

            }
            row.createCell(10).setCellValue(magazzino);
        }

        File file;
        FileOutputStream os;
        File directory;
        String fileName;
        if(tipo==0){
            fileName = nomeP+"spuntaDiff_"+docsName+".xlsx";
            file = new File("/storage/emulated/0/NAS/SpuntaDiff", fileName);
            directory = new File("/storage/emulated/0/NAS/SpuntaDiff");
            os = null;
        }else{
            fileName = nomeP+"presaDiff_"+docsName+".xlsx";
            file = new File("/storage/emulated/0/NAS/PresaDiff", fileName);
            directory = new File("/storage/emulated/0/NAS/PresaDiff");
            os = null;
        }


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
/*
    public void putInExcelFile() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet;
        if(tipo==0){
            sheet = workbook.createSheet("Spunta");
        }else{
            sheet = workbook.createSheet("Presa");
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm","");

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
                testata.createCell(8).setCellValue("nDoc");
                testata.createCell(9).setCellValue("Note");
                testata.createCell(10).setCellValue("Magazzino");
            }
            row.createCell(0).setCellValue(codArt.get(i));
            row.createCell(1).setCellValue(desc.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(alias.get(i));
            num2.setCellType(CellType.STRING);
            Cell num3 = row.createCell(3);
            num3.setCellValue(ubic.get(i));
            num3.setCellType(CellType.STRING);
            Cell num4 = row.createCell(4);
            num4.setCellValue(subic.get(i));
            num4.setCellType(CellType.STRING);
            row.createCell(5).setCellValue(qtaDoc.get(i));
            row.createCell(6).setCellValue(qtaSpunta.get(i));
            int risultato = Integer.parseInt(qtaSpunta.get(i)) - Integer.parseInt(qtaDoc.get(i));
            row.createCell(7).setCellValue(risultato);
            row.createCell(8).setCellValue(nDoc.get(i));
            if(tipo == 0){
                row.createCell(9).setCellValue(note.get(i));
            }else{
                row.createCell(9).setCellValue("");
            }
            if(tipo!=0){
                if(magazzino!=null) {
                    if (magazzino.equals("SESTU")) {
                        magazzino = "ELMAS";
                    }
                }
            }
            row.createCell(10).setCellValue(magazzino);
        }

        File file;
        FileOutputStream os;
        File directory;
        String fileName;
        if(tipo==0){
            fileName = nomeP+"spunta_"+docsName+".xlsx";
            file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
            directory = new File("/storage/emulated/0/NAS/SpuntaGen");
            os = null;
        }else{
            fileName = nomeP+"presa_"+docsName+".xlsx";
            file = new File("/storage/emulated/0/NAS/PresaGen", fileName);
            directory = new File("/storage/emulated/0/NAS/PresaGen");
            os = null;
        }



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
        if(tipo == 0){
            preferences.edit().remove("codInt").apply();
            preferences.edit().remove("descInt").apply();
            preferences.edit().remove("magInt").apply();
            preferences.edit().remove("listInt").apply();
            preferences.edit().remove("aliasInt").apply();
            preferences.edit().remove("qtaDocInt").apply();
            preferences.edit().remove("qtaResInt").apply();
            preferences.edit().remove("qtaInt").apply();
            preferences.edit().remove("nameInt").apply();
            preferences.edit().remove("numInt").apply();
            preferences.edit().remove("ubiInt").apply();
            preferences.edit().remove("subiInt").apply();
            preferences.edit().remove("note").apply();
        }else{
            preferences.edit().remove("codIntPresa").apply();
            preferences.edit().remove("magIntPresa").apply();
            preferences.edit().remove("nameIntPresa").apply();
            preferences.edit().remove("listIntPresa").apply();
            preferences.edit().remove("descIntPresa").apply();
            preferences.edit().remove("qtaDocIntPresa").apply();
            preferences.edit().remove("qtaResIntPresa").apply();
            preferences.edit().remove("qtaIntPresa").apply();
            preferences.edit().remove("eanIntPresa").apply();
            preferences.edit().remove("ubiIntPresa").apply();
            preferences.edit().remove("subiIntPresa").apply();
            preferences.edit().remove("numIntPresa").apply();
            preferences.edit().remove("esIntPresa").apply();
        }

        if(tipo==0){
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
        }else{
            String email = p.getString("Email", "");
            String emailPass = p.getString("EmailPass", "");
            String filePath = "/storage/emulated/0/NAS/PresaGen/"+ fileName;
            String obj = nomeP+" presa "+docsName;
            String[] to = new String[]{"spunte@bdibimbi.it", email};
            try {
                sendEmail(to,email, obj, " ", filePath, email, emailPass);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
