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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ReviewSpuntaNeg extends AppCompatActivity {

    ArrayList<String> qtaDoc, qtaSpunta, codArt, desc, alias, nDoc, timeSp;
    ListView listView;
    Button btnFind;
    EditText insFind;
    String fileName;
    Integer tipo;
    int nColli = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_spunta_neg);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = findViewById(R.id.lvRiepSpuntaNeg);
        btnFind = findViewById(R.id.btnFindSpNeg);
        insFind = findViewById(R.id.insRicSpNeg);
        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        qtaDoc = new ArrayList<>();
        qtaSpunta = new ArrayList<>();
        alias = new ArrayList<>();
        nDoc = new ArrayList<>();
        timeSp = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            fileName = extras.getString("fileName");
            tipo = extras.getInt("tipo");
        }

        XSSFWorkbook workbook;
        Context context = this;

        try {
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            String outFileName = fileName;

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
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
                timeSp.add(row.getCell(9).getStringCellValue());
                nDoc.add(row.getCell(8).getStringCellValue());
                i++;
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
                timeSp = new ArrayList<>();

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
                            timeSp.add(row.getCell(9).getStringCellValue());
                            nDoc.add(row.getCell(8).getStringCellValue());
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
                                timeSp.add(row.getCell(9).getStringCellValue());
                                nDoc.add(row.getCell(8).getStringCellValue());
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

    public void setRows(){
        AdapterReviewSpuntaNeg whatever = new AdapterReviewSpuntaNeg(this, codArt, desc, qtaDoc, qtaSpunta, alias, nDoc, fileName, tipo, timeSp);
        listView.setAdapter(whatever);
    }
}