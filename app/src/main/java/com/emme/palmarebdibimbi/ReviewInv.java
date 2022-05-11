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

public class ReviewInv extends AppCompatActivity {

    ArrayList<String> qtaInv, ubic, subic, codArt, desc, alias, note;
    ListView listView;
    Button btnFind;
    EditText insFind;
    String magazzino, tipo, nG;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_inv);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listView = findViewById(R.id.lvRiepInv);
        btnFind = findViewById(R.id.btnFindInInv);
        insFind = findViewById(R.id.insRicInInv);
        insFind.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        context = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tipo = extras.getString("tipo");
            magazzino = extras.getString("magazzino");
            nG = extras.getString("nG");
        }

        XSSFWorkbook workbook;
        Context context = this;

        qtaInv = new ArrayList<>();
        ubic = new ArrayList<>();
        subic = new ArrayList<>();
        codArt = new ArrayList<>();
        desc = new ArrayList<>();
        alias = new ArrayList<>();
        note = new ArrayList<>();

        try {
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
            String nomeP = p.getString("NomePalm","");
            String outFileName = nomeP+"inventario_"+nG+"_"+tipo+".xlsx";

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

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
                i++;
            }

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
                    String outFileName = nomeP+"inventario_"+nG+"_"+tipo+".xlsx";

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
        AdapterReviewInv whatever = new AdapterReviewInv(this, codArt, desc, qtaInv, ubic, subic, alias, note, magazzino, tipo, nG);
        listView.setAdapter(whatever);
    }
}