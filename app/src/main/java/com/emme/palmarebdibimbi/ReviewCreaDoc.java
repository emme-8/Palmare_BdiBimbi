package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ReviewCreaDoc extends AppCompatActivity {

    ArrayList<String> codArt, desc, alias, qta;
    String fileName;
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
        }

        XSSFWorkbook workbook;

        try {
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
}