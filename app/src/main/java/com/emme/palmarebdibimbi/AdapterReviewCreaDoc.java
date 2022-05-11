package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdapterReviewCreaDoc extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt, alias;
    private ArrayList<String> desc;
    private ArrayList<String> qta;
    private String docsName;

    public AdapterReviewCreaDoc(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> qtaDocArrayParam, ArrayList<String> aliasArrayParam, String docsName) {

        super(context, R.layout.adapter_review_spunta, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qta = qtaDocArrayParam;
        this.alias = aliasArrayParam;
        this.docsName = docsName;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_review_crea_doc, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowCD);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRowCD);
        TextView txtQta = rowView.findViewById(R.id.txtQtaRowCD);
        Button btnMod = rowView.findViewById(R.id.btnModRigaCD);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        txtQta.setText(qta.get(position));

        btnMod.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modifica valori");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText insQta = new EditText(context);
            insQta.setHint("Quantità");
            layout.addView(insQta);

            builder.setView(layout);

            builder.setPositiveButton("Salva", (dialog, which) -> {

                if (!insQta.getText().toString().equals("")) {
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = docsName;

                        File path = new File("/storage/emulated/0/NAS/createdDocs");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        Row row = workbook.getSheetAt(0).getRow(position+1);
                        row.createCell(3).setCellValue(insQta.getText().toString());
                        qta.set(position, insQta.getText().toString());
                        txtQta.setText(insQta.getText().toString());

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        Button saveDoc = context.findViewById(R.id.btnSaveDocCD);
        saveDoc.setOnClickListener(v -> {
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
            String nomeP = p.getString("NomePalm", "");
            docPerSede("Attenzione","Si desidera inviare il file creato al reparto commerciale?", docsName, p, nomeP);
        });

        return rowView;
    }

    /*
    public void putInExcelFileDiff() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet; //Creating a sheet

        sheet = workbook.createSheet("ListaArticoli");

        for (int i = 0; i < codArt.size(); i++) {

            Row row = sheet.createRow(i + 1);
            if (i == 0) {
                Row testata = sheet.createRow(i);
                testata.createCell(0).setCellValue("Codice articolo");
                testata.createCell(1).setCellValue("Descrizione");
                testata.createCell(2).setCellValue("Alias");
                testata.createCell(3).setCellValue("Quantità");
                testata.createCell(4).setCellValue("Note");
            }
            row.createCell(0).setCellValue(codArt.get(i));
            row.createCell(1).setCellValue(desc.get(i));
            Cell num2 = row.createCell(2);
            num2.setCellValue(alias.get(i));
            num2.setCellType(CellType.STRING);
            row.createCell(3).setCellValue(qta.get(i));
            row.createCell(4).setCellValue("");
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String nomeP = p.getString("NomePalm", "");
        File file;
        FileOutputStream os;
        String fileName;
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minutes = calendar.get(Calendar.MINUTE);
        fileName = nomeP + "newDoc"+"_"+day+"_"+month+"_"+year+"_"+hours+"_"+minutes+".xlsx";
        file = new File("/storage/emulated/0/NAS/CreatedDocs", fileName);
        os = null;

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
        preferences.edit().remove("codIntCD").apply();
        preferences.edit().remove("magIntCD").apply();
        preferences.edit().remove("listIntCD").apply();
        preferences.edit().remove("descIntCD").apply();
        preferences.edit().remove("qtaIntCD").apply();
        preferences.edit().remove("eanIntCD").apply();
        preferences.edit().remove("numIntCD").apply();

        docPerSede("Attenzione","Si desidera inviare il file creato al reparto commerciale?", fileName, p, nomeP);

    }

     */

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

    private void alertDisplayer(String title, String message) {
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

    private void alertArt(String title,String message, String fileName, String nomeP, SharedPreferences p){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
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
                alertArt("Errore!", "Inserisci i dati relativi al documento", fileName, nomeP, p);
            }else{
                dialog.cancel();
                File dir = Environment.getExternalStorageDirectory();
                String newName = "";
                if(dir.exists()){
                    File from = new File("/storage/emulated/0/NAS/CreatedDocs",fileName);
                    String noteStore = note.getText().toString().replace(" ", "_");
                    newName = nomeP + "newDoc"+"_"+noteStore+".xlsx";
                    File to = new File("/storage/emulated/0/NAS/CreatedDocs",newName);
                    if(from.exists())
                        from.renameTo(to);
                }
                String email = p.getString("Email", "");
                String[] to = new String[]{email, "spunte@bdibimbi.it"};
                String emailPass = p.getString("EmailPass", "");
                String filePath = "/storage/emulated/0/NAS/CreatedDocs/"+ newName;
                try {
                    sendEmail(to,email, fileName, " ", filePath, email, emailPass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDisplayer("Attenzione!", "Documento di spunta creato con successo, verrai riportato alla home");
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void docPerSede(String title, String message, String fileName, SharedPreferences p, String nomeP) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Si", (dialog, which) -> {
                    alertArt("Attenzione", "Inserisci i dati relativi al documento", fileName, nomeP, p);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    String email = p.getString("Email", "");
                    String emailPass = p.getString("EmailPass", "");
                    String filePath = "/storage/emulated/0/NAS/CreatedDocs/"+ fileName;
                    try {
                        sendEmail(new String[]{email},email, fileName, " ", filePath, email, emailPass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alertDisplayer("Attenzione!", "Documento di spunta creato con successo, verrai riportato alla home");
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}
