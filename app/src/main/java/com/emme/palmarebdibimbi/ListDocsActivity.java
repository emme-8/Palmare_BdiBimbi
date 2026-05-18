package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListDocsActivity extends AppCompatActivity {

    ArrayList<String> years = new ArrayList<>();
    ArrayList<String> forns = new ArrayList<>();
    String utente;
    ArrayList<String> fileName = new ArrayList<>();
    ListView listView;
    int mag=0, listino=0, tipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_docs);

        listView = findViewById(R.id.lvListDocs);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            tipo = extras.getInt("tipo");
            utente = extras.getString("utente");
        }

        if(tipo==0){
            spuntaOrPresa(getSortedFilesByDate(new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaGen")),tipo);
        }else if(tipo==1){
            spuntaOrPresa(getSortedFilesByDate(new File(Environment.getExternalStorageDirectory(), "NAS/PresaGen")),tipo);
        }else if(tipo==2){
            spuntaOrPresa(getSortedFilesByDate(new File(Environment.getExternalStorageDirectory(), "NAS/CreatedDocs")),tipo);
        }else if(tipo==3){
            spuntaOrPresa(getSortedFilesByDate(new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaGen")),tipo);
        }

    }

    public void createdFiles(File directorySp){
        File[] filesSp1 = directorySp.listFiles();
        for (int i = 0; i < filesSp1.length; i++){
            String name = filesSp1[i].getName();
            fileName.add(name);
            if(tipo==2){
                int start = name.indexOf("newDoc");
                name = name.substring(start+7);
                forns.add(name.substring(0, name.indexOf("2022")-1));
                years.add(name.substring(name.indexOf(".xlsx")-4,name.indexOf(".xlsx")));
            }else if(tipo==3){
                int start = name.indexOf("inventario");
                name = name.substring(start+11);
                forns.add(name.substring(0,name.indexOf('_')));
                years.add(name.substring(name.indexOf('_')+1,name.indexOf(".xlsx")));
            }
        }

        AdapterListCreatedDocs whatever = new AdapterListCreatedDocs(this, forns, years, fileName, mag, listino, tipo);
        listView.setAdapter(whatever);
    }

    public List<File> getSortedFilesByDate(File directory) {
        List<File> fileList = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                fileList.addAll(Arrays.asList(files));
                // Ordina i file per data di ultima modifica, dal più recente al meno recente
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified());
                    }
                });
            }
        }
        return fileList;
    }

    public void spuntaOrPresa(List<File> fileList, int typeSel){
        for (int i = 0; i < fileList.size(); i++){
            String name = fileList.get(i).getName();

            switch (typeSel){
                case 0: if(name.contains("spunta")){
                            fileName.add(name);
                        }
                    break;
                case 1: if(name.contains("presa")){
                            fileName.add(name);
                        }
                    break;
                case 2: if(name.contains("aglio")){
                            fileName.add(name);
                        }
                    break;
                case 3: if(name.contains("inventario")){
                            fileName.add(name);
                        }
                    break;
                default:
                    break;
            }

        }

        AdapterListDocs whatever = new AdapterListDocs(this, fileName, mag, listino, tipo, utente);
        listView.setAdapter(whatever);
    }
}