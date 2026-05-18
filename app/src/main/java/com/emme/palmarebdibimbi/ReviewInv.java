package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class ReviewInv extends AppCompatActivity {

    ArrayList<String> qtaInv, ubic, subic, codArt, desc, alias, note, gondole;
    ListView listView;
    Button btnFind;
    EditText insFind;
    int nRighe;
    String magazzino, tipo, nG, causale, insert = "", store, utente, nomeP, ipNeg = "", insertXInv = "", cartella, fileName;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_inv);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = findViewById(R.id.lvRiepInvLocal);
        btnFind = findViewById(R.id.btnFindInInv);
        insFind = findViewById(R.id.insRicInInv);
        TextView txtRiep = findViewById(R.id.txtTitleInvLocal);
        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tipo = extras.getString("tipo");
            magazzino = extras.getString("magazzino");
            if(extras.getString("ipNeg")!=null){
                ipNeg = extras.getString("ipNeg");
            }
            nG = extras.getString("nG");
            utente = extras.getString("utente");
            causale = extras.getString("causale");
            cartella = extras.getString("cartella");
            fileName = extras.getString("fileName");
        }

        txtRiep.setText("Riepilogo " + causale);

                XSSFWorkbook workbook;
        Context context = this;

        qtaInv = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        alias = new ArrayList<>();
        note = new ArrayList<>();
        gondole = new ArrayList<>();
        insert = "";
        try {
            CancDoc cancDoc = new CancDoc();
            cancDoc.execute();

            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            nomeP = p.getString("NomePalm","");
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/"+cartella);

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);

            int i = 1;
            while(workbook.getSheetAt(0).getRow(i) != null){
                Row row = workbook.getSheetAt(0).getRow(i);
                codArt.add(row.getCell(0).getStringCellValue());
                desc.add(row.getCell(1).getStringCellValue());
                alias.add(row.getCell(2).getStringCellValue());
                ubic.add(row.getCell(3).getStringCellValue());
                subic.add(row.getCell(4).getStringCellValue());
                qtaInv.add(row.getCell(5).getStringCellValue());
                note.add(row.getCell(7).getStringCellValue());
                boolean pres = false;
                for(int j=0; j<gondole.size(); j++){
                    if(gondole.get(j).equals(row.getCell(3).getStringCellValue())){
                        pres = true;
                    }
                }
                if(!pres){
                    gondole.add(row.getCell(3).getStringCellValue());
                }

                insert = insert + "('"+row.getCell(0).getStringCellValue()+"', '"+row.getCell(1).getStringCellValue()+"', '"+row.getCell(2).getStringCellValue()+"', " +
                        "'"+row.getCell(3).getStringCellValue()+"', '"+row.getCell(4).getStringCellValue()+"', "+row.getCell(5).getStringCellValue()+", " +
                        ""+row.getCell(6).getStringCellValue()+", '"+row.getCell(7).getStringCellValue()+"', '"+row.getCell(8).getStringCellValue()+"', '"+nomeP+"', '"+utente+"'),";

                insertXInv = insertXInv + "('"+row.getCell(0).getStringCellValue()+"', '"+row.getCell(3).getStringCellValue()+"', "+tipo+", "+row.getCell(5).getStringCellValue()+", ''," +
                        ""+row.getCell(6).getStringCellValue()+", '"+row.getCell(8).getStringCellValue()+"'),";

                store = row.getCell(8).getStringCellValue();
                i++;
            }
            nRighe = i-1;
            insert = insert.substring(0,insert.length()-1);
            insertXInv = insertXInv.substring(0,insertXInv.length()-1);
            //CreaDoc creaDoc = new CreaDoc();
            //creaDoc.execute();
            file.close();

            FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
            workbook.write(outFile);
            outFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setRows();

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XSSFWorkbook workbook4Ric;

                qtaInv = new ArrayList<>();
                ubic = new ArrayList<>();
                subic = new ArrayList<>();
                codArt = new ArrayList<>();
                desc = new ArrayList<>();
                alias = new ArrayList<>();
                note = new ArrayList<>();

                try {
                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                    String nomeP = p.getString("NomePalm","");
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/"+cartella);

                    FileInputStream file = new FileInputStream(new File(path, outFileName));
                    workbook4Ric = new XSSFWorkbook(file);

                    int i = 1;
                    boolean find = false;
                    while(workbook4Ric.getSheetAt(0).getRow(i) != null){
                        if(workbook4Ric.getSheetAt(0).getRow(i).getCell(2).getStringCellValue().contains(insFind.getText().toString().trim())){
                            Row row = workbook4Ric.getSheetAt(0).getRow(i);
                            codArt.add(row.getCell(0).getStringCellValue());
                            desc.add(row.getCell(1).getStringCellValue());
                            alias.add(row.getCell(2).getStringCellValue());
                            ubic.add(row.getCell(3).getStringCellValue());
                            subic.add(row.getCell(4).getStringCellValue());
                            qtaInv.add(row.getCell(5).getStringCellValue());
                            note.add(row.getCell(7).getStringCellValue());
                            find = true;
                        }
                        i++;
                    }
                    i = 1;
                    if(!find){
                        while(workbook4Ric.getSheetAt(0).getRow(i) != null){
                            if(workbook4Ric.getSheetAt(0).getRow(i).getCell(0).getStringCellValue().contains(insFind.getText().toString().trim())){
                                Row row = workbook4Ric.getSheetAt(0).getRow(i);
                                codArt.add(row.getCell(0).getStringCellValue());
                                desc.add(row.getCell(1).getStringCellValue());
                                alias.add(row.getCell(2).getStringCellValue());
                                ubic.add(row.getCell(3).getStringCellValue());
                                subic.add(row.getCell(4).getStringCellValue());
                                qtaInv.add(row.getCell(5).getStringCellValue());
                                note.add(row.getCell(7).getStringCellValue());
                            }
                            i++;
                        }
                    }

                    file.close();

                    FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                    workbook4Ric.write(outFile);
                    outFile.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                setRows();
            }
        });
    }

    public void setRows() {
        AdapterReviewInv whatever = new AdapterReviewInv(this, codArt, desc, qtaInv, ubic, subic, alias, note, magazzino, tipo, gondole, causale, insertXInv, store, nRighe, nG, cartella, fileName);
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
                    String query = "INSERT INTO mcInvDoc (codArt, descrizione, alias, ubic, subic, qtaInv, esistenza, note, store, palmare, utente) " +
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
}