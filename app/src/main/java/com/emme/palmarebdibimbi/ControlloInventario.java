package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class ControlloInventario extends AppCompatActivity {

    String store = "", fileName = "", nomeP = "";
    String utente = "";
    ConnectionClass connectionClass;
    String categoria = "", ipNeg = "";
    ArrayList<String> codArt = new ArrayList<>(), desc = new ArrayList<>(), codEan = new ArrayList<>(), qta = new ArrayList<>(), diff = new ArrayList<>(), es = new ArrayList<>(), valDiff = new ArrayList<>();
    ArrayList<Boolean> isCon = new ArrayList<>();
    String catR1 = "", catR2 = "", catR3 = "";
    int mag = 0, idL = 1;
    Context context;
    Button btnSend;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controllo_inventario);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
            utente = extras.getString("utente");
            categoria = extras.getString("categoria");
        }

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        nomeP = p.getString("NomePalm","");
        Calendar cal = Calendar.getInstance();
        cal.get(Calendar.DAY_OF_MONTH);
        cal.get(Calendar.MONTH);
        fileName = nomeP+"_controllo_inv_"+categoria.replace(" ","_")+".xlsx"; //Name of the file

        btnSend = findViewById(R.id.btnSaveSend);

        File file = new File("/storage/emulated/0/NAS/SpuntaGen", fileName);
        if(!file.exists()){
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1"); //Creating a sheet

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

        connectionClass = new ConnectionClass();
        context = this;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSend("Attenzione!", "Sei sicuro di voler salvare e inviare il documento? Dopo l'invio verrai reindirizzato alla home");
            }
        });
        listView = findViewById(R.id.listCInv);
        TextView txtCat = findViewById(R.id.txtCatCInv);
        txtCat.setText(categoria);

        catR1 = " and (select c1.nome from CategoriaArticolo c1 where c1.id = articolo.idCategoriaArticolo) like '"+categoria+"' ";
        catR2 = " and (select c2.nome from CategoriaArticolo c1 join CategoriaArticolo c2 on c1.idCategoriaMadre = c2.id where c1.id = articolo.idCategoriaArticolo) like '"+categoria+"' ";
        catR3 = " and (select c3.nome from CategoriaArticolo c1 join CategoriaArticolo c2 on c1.idCategoriaMadre = c2.id join CategoriaArticolo c3 on c2.idCategoriaMadre = c3.id where c1.id = articolo.idCategoriaArticolo) like '"+categoria+"' ";

        risolviMagL();

        FindRowsControlli find = new FindRowsControlli();
        find.execute();

    }

    private void confirmSend(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("NO", ((dialog, which) -> {
                    dialog.cancel();
                }))
                .setPositiveButton("SI", (dialog, which) -> {
                    dialog.cancel();

                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                    String email = p.getString("Email", "");
                    String emailPass = p.getString("EmailPass", "");

                    ArrayList<String> pathToSend = new ArrayList<>();
                    pathToSend.add("/storage/emulated/0/NAS/SpuntaGen/"+ fileName);

                    String obj = fileName;
                    String[] to = new String[]{"spunte@bdibimbi.it", email};
                    try {
                        sendEmail(to,email, obj, " ", pathToSend, email, emailPass);
                        Intent retHome = new Intent(context, MainActivity.class);
                        startActivity(retHome);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        emailError("Errore","E' avvenuto un errore durante l'invio del documento",to, obj, email, pathToSend, emailPass);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
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
                    startActivity(retHome);
                    finish();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void risolviMagL(){
        switch (store) {
            case "MASTER":
                ipNeg = "192.168.2.41";
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                mag = 77;
                idL = 1;
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                mag = 35;
                idL = 1;
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                mag = 72;
                idL = 1;
                break;
            case "OLBIA":
                ipNeg = "192.168.1.10";
                mag = 76;
                idL = 1;
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                mag = 74;
                idL = 1;
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                mag = 32;
                idL = 1;
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                mag = 78;
                idL = 1;
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                mag = 75;
                idL = 1;
                break;
            case "ORISTANO":
                ipNeg = "85.47.29.51";
                mag = 71;
                idL = 1;
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                mag = 85;
                idL = 1;
                break;
            case "MasterMagRoma":
                ipNeg = "195.100.100.202";
                mag = 91;
                idL = 1;
                break;
            case "CEDIROMAINLAV":
                ipNeg = "192.168.1.20";
                mag = 93;
                idL = 1;
                break;
            case "CAPENA":
                ipNeg = "192.168.188.20";
                mag = 87;
                idL = 1;
                break;
            case "OSTIENSE":
                ipNeg = "95.255.234.195";
                mag = 86;
                idL = 1;
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.2.41";
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                mag = 90;
                idL = 1;
                break;
            case "POMEZIA":
                ipNeg = "192.168.1.20";
                mag = 94;
                idL = 1;
                break;
            case "ARDEATINA":
                ipNeg = "192.168.1.20";
                mag = 112;
                idL = 1;
                break;
            case "VERONA":
                ipNeg = "192.168.16.20";
                mag = 114;
                idL = 1;
                break;
            case "ROMACEDI":
                ipNeg = "192.168.1.20";
                mag = 111;
                idL = 1;
                break;
            case "INTRANSITO":
                ipNeg = "192.168.2.41";
                mag = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.2.41";
                mag = 89;
                idL = 1;
                break;
            default:
                ipNeg = "192.168.1.20";
                mag = 0;
                idL = 1;
                break;
        }
    }

    public class FindRowsControlli extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet resultSet;
        int count = 0;

        private static Double round(double value, int places){
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                XSSFWorkbook workbook;
                try {
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook = new XSSFWorkbook(file);

                    for(int i = 0; i<codArt.size(); i++){
                        boolean isPres = false;
                        int j = 0;
                        while(workbook.getSheetAt(0).getRow(j) != null){
                            Row row = workbook.getSheetAt(0).getRow(j);
                            if(row.getCell(0).getStringCellValue().equals(codArt.get(i))){
                                isPres = true;
                                isCon.add(true);
                            }
                            j++;
                        }
                        if(!isPres){
                            isCon.add(false);
                        }
                    }

                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                AdapterRowCInv adapterShowDocPresa = new AdapterRowCInv(ControlloInventario.this, codArt, desc, qta, es, codEan, diff, valDiff, fileName, mag, isCon, store, utente, categoria);
                listView.setAdapter(adapterShowDocPresa);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:jtds:sqlserver://"+ipNeg+"/PassepartoutRetail;user=sa;password=SaSqlPass*01;");

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
                } if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select ImportInv2023.*, articolo.descrizione, (ImportInv2023.qta - ImportInv2023.esistenza) as diff, ABS((ImportInv2023.qta - ImportInv2023.esistenza)*InventarioProgressivi.costo) as costoultimo," +
                            "(select c1.nome from CategoriaArticolo c1 where c1.id = articolo.idCategoriaArticolo) as cat0,\n" +
                            "(select c2.nome from CategoriaArticolo c1 join CategoriaArticolo c2 on c1.idCategoriaMadre = c2.id where c1.id = articolo.idCategoriaArticolo) as cat1,\n" +
                            "(select TOP(1)codice from alias where alias.idArticolo = (select articolo.id from articolo where articolo.nome = importinv2023.codart)) as ean,\n" +
                            "(select c3.nome from CategoriaArticolo c1 join CategoriaArticolo c2 on c1.idCategoriaMadre = c2.id join CategoriaArticolo c3 on c2.idCategoriaMadre = c3.id where c1.id = articolo.idCategoriaArticolo) as cat2  " +
                            "from ImportInv2023 join articolo on (articolo.nome = importInv2023.codart) " +
                            "left join InventarioProgressivi on (InventarioProgressivi.codArt = articolo.nome) " +
                            "where importinv2023.id is not null " + catR1 + " and (ImportInv2023.qta - ImportInv2023.esistenza) <> 0 " +
                            "OR importinv2023.id is not null " + catR2 + " and (ImportInv2023.qta - ImportInv2023.esistenza) <> 0 " +
                            "OR importinv2023.id is not null " + catR3 + " and (ImportInv2023.qta - ImportInv2023.esistenza) <> 0 " +
                            "order by cat2 desc, costoultimo desc, cat1, cat0 ";
                    Statement stmt = con.createStatement();
                    resultSet = stmt.executeQuery(query);
                    while(resultSet.next()){
                        isSuccess = true;
                        String ean = "";
                        Double val = 0.0;
                        if(resultSet.getString("costoultimo")!=null){
                            val = round(resultSet.getDouble("costoultimo"),2);
                        }
                        if(resultSet.getString("ean")!=null){
                            ean = resultSet.getString("ean");
                        }
                        codArt.add(resultSet.getString("codArt"));
                        codEan.add(ean);
                        desc.add(resultSet.getString("descrizione"));
                        qta.add(resultSet.getString("qta"));
                        es.add(resultSet.getString("esistenza"));
                        diff.add(resultSet.getString("diff"));
                        valDiff.add(val.toString());
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }
}