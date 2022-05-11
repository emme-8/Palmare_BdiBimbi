package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
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
import java.util.ArrayList;

public class ReviewSpunta extends AppCompatActivity {

    ArrayList<String> qtaDoc, qtaSpunta, ubic, subic, codArt, desc, alias, nDoc, note, timeSp, qtaColli;
    ListView listView;
    String docsName;
    String fileName;
    Button btnFind;
    EditText insFind;
    TextView txtTotColli;
    String magazzino;
    int tipo, totColli;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_spunta);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = findViewById(R.id.lvRiepSpunta);
        btnFind = findViewById(R.id.btnFindSp);
        insFind = findViewById(R.id.insRicSp);
        txtTotColli = findViewById(R.id.txtTotColli);

        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            docsName = extras.getString("docsName");
            fileName = extras.getString("fileName");
            tipo = extras.getInt("tipo");
        }
        if(tipo != 0){
            magazzino = extras.getString("magazzino");
        }

        XSSFWorkbook workbook;
        Context context = this;

        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        note = new ArrayList<>();
        timeSp = new ArrayList<>();
        qtaColli = new ArrayList<>();

        try {
            String outFileName = fileName;
            File path = null;
            FileInputStream file = null;
            if (tipo==0){
                path = new File("/storage/emulated/0/NAS/SpuntaGen");

                file = new FileInputStream(new File(path, outFileName));
                workbook = new XSSFWorkbook(file);

                totColli = 0;
                int i = 1;
                while(workbook.getSheetAt(0).getRow(i) != null){
                    Row row = workbook.getSheetAt(0).getRow(i);

                    Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                    row.createCell(7).setCellValue(newQta.toString());

                    codArt.add(row.getCell(0).getStringCellValue());
                    desc.add(row.getCell(1).getStringCellValue());
                    alias.add(row.getCell(2).getStringCellValue());
                    qtaDoc.add(row.getCell(5).getStringCellValue());
                    qtaSpunta.add(row.getCell(6).getStringCellValue());
                    timeSp.add(row.getCell(11).getStringCellValue());
                    ubic.add(row.getCell(3).getStringCellValue());
                    subic.add(row.getCell(4).getStringCellValue());
                    note.add(row.getCell(9).getStringCellValue());
                    nDoc.add(row.getCell(8).getStringCellValue());
                    qtaColli.add(row.getCell(12).getStringCellValue());
                    totColli = totColli + Integer.parseInt(row.getCell(12).getStringCellValue());
                    i++;
                }
                txtTotColli.setText("N. colli: "+totColli);
            }else{
                path = new File("/storage/emulated/0/NAS/PresaGen");

                file = new FileInputStream(new File(path, outFileName));
                workbook = new XSSFWorkbook(file);

                int i = 1;
                while(workbook.getSheetAt(0).getRow(i) != null){
                    Row row = workbook.getSheetAt(0).getRow(i);

                    Integer newQta = Integer.parseInt(row.getCell(6).getStringCellValue()) - Integer.parseInt(row.getCell(5).getStringCellValue());
                    row.createCell(7).setCellValue(newQta.toString());

                    codArt.add(row.getCell(0).getStringCellValue());
                    desc.add(row.getCell(1).getStringCellValue());
                    alias.add(row.getCell(2).getStringCellValue());
                    qtaDoc.add(row.getCell(5).getStringCellValue());
                    qtaSpunta.add(row.getCell(6).getStringCellValue());
                    timeSp.add(row.getCell(11).getStringCellValue());
                    ubic.add(row.getCell(3).getStringCellValue());
                    subic.add(row.getCell(4).getStringCellValue());
                    note.add(row.getCell(9).getStringCellValue());
                    nDoc.add(row.getCell(8).getStringCellValue());
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

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XSSFWorkbook workbook4Ric;

                codArt = new ArrayList<>();
                desc = new ArrayList<>();
                qtaDoc = new ArrayList<>();
                qtaSpunta = new ArrayList<>();
                alias = new ArrayList<>();
                nDoc = new ArrayList<>();
                ubic = new ArrayList<>();
                subic = new ArrayList<>();
                note = new ArrayList<>();
                timeSp = new ArrayList<>();
                qtaColli = new ArrayList<>();

                try {
                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                    String outFileName = fileName;

                    File path = new File("/storage/emulated/0/NAS/SpuntaGen");

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
                            qtaDoc.add(row.getCell(5).getStringCellValue());
                            qtaSpunta.add(row.getCell(6).getStringCellValue());
                            ubic.add(row.getCell(3).getStringCellValue());
                            subic.add(row.getCell(4).getStringCellValue());
                            note.add(row.getCell(9).getStringCellValue());
                            timeSp.add(row.getCell(11).getStringCellValue());
                            nDoc.add(row.getCell(8).getStringCellValue());
                            qtaColli.add(row.getCell(12).getStringCellValue());
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
                                qtaDoc.add(row.getCell(5).getStringCellValue());
                                qtaSpunta.add(row.getCell(6).getStringCellValue());
                                ubic.add(row.getCell(3).getStringCellValue());
                                subic.add(row.getCell(4).getStringCellValue());
                                note.add(row.getCell(9).getStringCellValue());
                                timeSp.add(row.getCell(11).getStringCellValue());
                                nDoc.add(row.getCell(8).getStringCellValue());
                                qtaColli.add(row.getCell(12).getStringCellValue());
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

        setRows();
    }

    public void setRows() {
        if(tipo == 0){
            AdapterReviewSpunta whatever = new AdapterReviewSpunta(this, codArt, desc, qtaDoc, qtaSpunta, ubic, subic, alias, nDoc, tipo, fileName, note, timeSp, qtaColli);
            listView.setAdapter(whatever);
        }else{
            AdapterReviewSpunta whatever = new AdapterReviewSpunta(this, codArt, desc, qtaDoc, qtaSpunta, ubic, subic, alias, nDoc, tipo, fileName, magazzino, timeSp, note);
            listView.setAdapter(whatever);
        }
    }
}