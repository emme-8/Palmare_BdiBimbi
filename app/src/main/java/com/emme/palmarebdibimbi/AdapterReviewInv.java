package com.emme.palmarebdibimbi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import androidx.core.app.ActivityCompat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class AdapterReviewInv  extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt, alias;
    private ArrayList<String> desc, note;
    private ArrayList<String> ubic, subic;
    private ArrayList<String> qtaInv;
    String magazzino, nSp, nG;

    public AdapterReviewInv(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam,
                            ArrayList<String> qtaInvArrayParam, ArrayList<String> ubicArrayParam, ArrayList<String> subicArrayParam,
                            ArrayList<String> aliasArrayParam, ArrayList<String> noteArrayParam, String magazzino, String nSp, String nG){

        super(context, R.layout.adapter_review_inv, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.alias = aliasArrayParam;
        this.desc = descArrayParam;
        this.ubic = ubicArrayParam;
        this.subic = subicArrayParam;
        this.note = noteArrayParam;
        this.qtaInv = qtaInvArrayParam;
        this.magazzino = magazzino;
        this.nSp = nSp;
        this.nG = nG;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_review_inv, null, true);

        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowInv);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRowInv);
        TextView txtQtaInv = rowView.findViewById(R.id.txtQtaRowInv);
        TextView txtUbic = rowView.findViewById(R.id.txtUbicRev);
        TextView txtSubic = rowView.findViewById(R.id.txtSubicRev);
        Button btnMod = rowView.findViewById(R.id.btnModRigaRev);
        TextView txtEAN = rowView.findViewById(R.id.txtEanRowInv);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        txtQtaInv.setText(qtaInv.get(position));
        txtUbic.setText(ubic.get(position));
        txtSubic.setText(subic.get(position));
        txtEAN.setText(alias.get(position));

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
                if (!qta.getText().toString().equals("")) {
                    qtaInv.set(position, qta.getText().toString());
                    txtQtaInv.setText(qta.getText().toString());
                }
                if (!ubicazione.getText().toString().equals("")) {
                    ubic.set(position, ubicazione.getText().toString());
                    txtUbic.setText(ubicazione.getText().toString());
                }
                if (!subicazione.getText().toString().equals("")) {
                    subic.set(position, subicazione.getText().toString());
                    txtSubic.setText(subicazione.getText().toString());
                }
            });
            builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        Button saveDoc = context.findViewById(R.id.btnSaveDocInv);
        saveDoc.setOnClickListener(v -> putInExcelFile());

        return rowView;
    }

    public void putInExcelFile() {
        /*
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
                testata.createCell(5).setCellValue("Quantita inventario");
                testata.createCell(6).setCellValue("Note");
            }
            row.createCell(0).setCellValue(codArt.get(i));
            row.createCell(1).setCellValue(desc.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(alias.get(i));
            num2.setCellType(CellType.STRING);
            row.createCell(3).setCellValue(ubic.get(i));
            row.createCell(4).setCellValue(subic.get(i));
            row.createCell(5).setCellValue(qtaInv.get(i));
            row.createCell(6).setCellValue(note.get(i));
        }


        String nomeP = p.getString("NomePalm","");
        String fileName; //Name of the file
        fileName = nomeP+"inventario_"+ubic.get(1)+"_"+nSp+".xlsx"; //Name of the file

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
        }*/

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm","");
        String fileName; //Name of the file
        fileName = nomeP+"inventario_"+nG+"_"+nSp+".xlsx"; //Name of the file

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

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

        String email = p.getString("Email", "");
        String emailPass = p.getString("EmailPass", "");
        String filePath = "/storage/emulated/0/NAS/SpuntaGen/"+ fileName;
        String obj = nomeP+"inventario_"+nG+"_"+nSp;
        String[] to = new String[]{"spunte@bdibimbi.it", email};
        try {
            sendEmail(to,email, obj, " ", filePath, email, emailPass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        alertDisplayer("Attenzione!", "Documento d'inventario creato con successo, verrai riportato alla home");
    }

    public static boolean sendEmail(String[] to, String from, String subject,
                                    String message,String attachement, String user, String pass) throws Exception {
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
            mail.addAttachment(attachement);
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
