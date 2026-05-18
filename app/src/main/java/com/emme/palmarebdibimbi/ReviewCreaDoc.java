package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ReviewCreaDoc extends AppCompatActivity {

    ArrayList<String> codArt, desc, alias, qta;
    String fileName, utente, nomeP, insert = "", ipNeg, store;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_crea_doc);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = findViewById(R.id.lvRiepCD);

        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qta = new ArrayList<>();
        alias = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            fileName = extras.getString("fileName");
            utente = extras.getString("utente");
            nomeP = extras.getString("nomeP");
            ipNeg = extras.getString("ipNeg");
            store = extras.getString("store");
        }

        XSSFWorkbook workbook;

        try {
            if(store.equals("MASTER") || store.equals("TIBURTINA") || store.equals("IN LAVORAZIONE") || store.equals("INTRANSITO") || store.equals("INTEMPORANEO") || store.equals("MasterMagRoma") || store.equals("CEDIROMAINLAV")){
                ReviewCreaDoc.CancDoc cancDoc = new ReviewCreaDoc.CancDoc();
                cancDoc.execute();
            }

            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/createdDocs");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            int i = 1;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                codArt.add(row.getCell(0).getStringCellValue());
                desc.add(row.getCell(1).getStringCellValue());
                alias.add(row.getCell(2).getStringCellValue());
                qta.add(row.getCell(3).getStringCellValue());
                i++;

                insert = insert + "('"+row.getCell(0).getStringCellValue()+"', '"+row.getCell(1).getStringCellValue()+"', '"+row.getCell(2).getStringCellValue()+"', "+row.getCell(3).getStringCellValue()+", '', '"+nomeP+"', '"+utente+"'),";
            }
            insert = insert.substring(0,insert.length()-1);
            if(store.equals("MASTER") || store.equals("TIBURTINA") || store.equals("IN LAVORAZIONE") || store.equals("INTRANSITO") || store.equals("INTEMPORANEO") || store.equals("MasterMagRoma") || store.equals("CEDIROMAINLAV")){
                ReviewCreaDoc.CreaDoc creaDoc = new ReviewCreaDoc.CreaDoc();
                creaDoc.execute();
            }

            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setRows();
    }

    public void setRows(){
        AdapterReviewCreaDoc whatever = new AdapterReviewCreaDoc(this, codArt, desc, qta, alias, fileName);
        listView.setAdapter(whatever);
    }

    public class CreaDoc extends AsyncTask<String,String,String> {
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
                    String query = "INSERT INTO mcNewDoc (codArt, descrizione, alias, qta, note, palmare, utente) " +
                            "VALUES "+insert+" ";
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
                            "FROM mcNewDoc a " +
                            "WHERE palmare like '"+nomeP+"' ";
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
}