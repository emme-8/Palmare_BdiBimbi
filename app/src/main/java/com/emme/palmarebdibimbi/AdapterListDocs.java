package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AdapterListDocs extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> fileName;
    TextView txtNDoc, txtLNDoc;
    LinearLayout thisRow;
    Button del, open;
    String store="", nG ="", utente;
    int mag, listino, tipoFile;
    ArrayList<String> numDoc, serieDoc;

    public AdapterListDocs(Activity context, ArrayList<String> fileNameArrayParam,
                           int mag, int listino, int tipoFile, String utente){

        super(context,R.layout.adapter_doc_row , fileNameArrayParam);
        this.context=context;
        this.fileName = fileNameArrayParam;
        this.mag = mag;
        this.listino = listino;
        this.tipoFile = tipoFile;
        this.utente = utente;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.adapter_doc_row, null,true);

        //this code gets references to objects in the listview_row.xml file
        txtNDoc = convertView.findViewById(R.id.txtNumeroDR);
        txtLNDoc = convertView.findViewById(R.id.txtLNumeroDR);
        thisRow = convertView.findViewById(R.id.thisRowDR);
        del = convertView.findViewById(R.id.btnDelDR);
        open = convertView.findViewById(R.id.btnOpenDR);

        txtNDoc.setText(fileName.get(position));

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDoc("Attenzione!","Sei sicuro di voler eliminare questo file?", position);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spunta;
                int magRif = 1;
                String magazzino = "";
                String forn = "";
                String tipoDoc = "";
                if(tipoFile==0){
                    int countT = 0;
                    String serie = "";
                    for(int i=0; i<fileName.get(position).length(); i++){
                        if(fileName.get(position).charAt(i)=='_'){
                            countT++;
                        }else{
                            switch (countT){
                                case 2: tipoDoc = tipoDoc + fileName.get(position).charAt(i);
                                    break;
                                case 3: forn = forn + fileName.get(position).charAt(i);
                                    break;
                                case 5: serie = serie + fileName.get(position).charAt(i);
                                    break;
                                default: break;
                            }
                        }
                    }
                    trovaNeS(fileName.get(position), serie, 8);
                    spunta = new Intent(context, IniziaSpuntaNeg.class);
                }else if(tipoFile==1){
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName.get(position);

                        File path = new File("/storage/emulated/0/NAS/PresaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        Row row = workbook.getSheetAt(0).getRow(1);
                        magazzino = row.getCell(10).getStringCellValue();
                        switch (magazzino) {
                            case "MASTER":
                                magRif = 1;
                                break;
                            case "SESTU":
                                magRif = 77;
                                break;
                            case "MARCONI":
                                magRif = 35;
                                break;
                            case "PIRRI":
                                magRif = 72;
                                break;
                            case "OLBIA":
                                magRif = 76;
                                break;
                            case "SASSARI":
                                magRif = 74;
                                break;
                            case "NUORO":
                                magRif = 32;
                                break;
                            case "CARBONIA":
                                magRif = 78;
                                break;
                            case "TORTOLI":
                                magRif = 75;
                                break;
                            case "ORISTANO":
                                magRif = 71;
                                break;
                            case "TIBURTINA":
                                magRif = 85;
                                break;
                            case "MasterMagRoma":
                                magRif = 91;
                                break;
                            case "CEDIROMAINLAV":
                                magRif = 93;
                                break;
                            case "CAPENA":
                                magRif = 87;
                                break;
                            case "OSTIENSE":
                                magRif = 86;
                                break;
                            case "IN LAVORAZIONE":
                                magRif = 59;
                                break;
                            case "CASILINA":
                                magRif = 90;
                                break;
                            case "ARDEATINA":
                                magRif = 112;
                                break;
                            case "VERONA":
                                magRif = 114;
                                break;
                            case "POMEZIA":
                                magRif = 94;
                                break;
                            case "ROMACEDI":
                                magRif = 111;
                                break;
                            case "INTRANSITO":
                                magRif = 88;
                                break;
                            case "INTEMPORANEO":
                                magRif = 89;
                                break;
                            default:
                                magRif = 1;
                                break;
                        }

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    int countT = 0;
                    String serie = "";
                    for(int i=0; i<fileName.get(position).length(); i++){
                        if(fileName.get(position).charAt(i)=='_'){
                            countT++;
                        }else{
                            switch (countT){
                                case 2: tipoDoc = tipoDoc + fileName.get(position).charAt(i);
                                    break;
                                case 3: forn = forn + fileName.get(position).charAt(i);
                                    break;
                                case 5: serie = serie + fileName.get(position).charAt(i);
                                    break;
                                default: break;
                            }
                        }
                    }
                    trovaNeS(fileName.get(position), serie, 8);

                    spunta = new Intent(context, IniziaPresa.class);
                }else if(tipoFile==2){
                    spunta = new Intent(context, IniziaSpunta.class);
                }else{
                    XSSFWorkbook workbook;
                    try {
                        String outFileName = fileName.get(position);

                        File path = new File("/storage/emulated/0/NAS/SpuntaGen");

                        FileInputStream file = new FileInputStream(new File(path, outFileName));
                        workbook = new XSSFWorkbook(file);

                        Row row = workbook.getSheetAt(0).getRow(1);
                        store = row.getCell(8).getStringCellValue();
                        nG = row.getCell(3).getStringCellValue();

                        file.close();

                        FileOutputStream outFile = new FileOutputStream(new File(path, outFileName));
                        workbook.write(outFile);
                        outFile.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    spunta = new Intent(context, InvDepOff.class);
                }
                spunta.putExtra("nomeF", fileName.get(position));
                spunta.putExtra("listino", listino);
                spunta.putExtra("mag", mag);
                spunta.putExtra("magazzino", magazzino);
                spunta.putExtra("storeName", store);
                spunta.putExtra("nG", nG);
                spunta.putExtra("nSp", "1");
                spunta.putExtra("utente", utente);
                spunta.putExtra("rip", 0);
                spunta.putExtra("magRif", magRif);
                spunta.putExtra("da",1);
                spunta.putExtra("fornitore", forn);
                spunta.putExtra("tipoDoc", tipoDoc);
                context.startActivity(spunta);
            }
        });

        return convertView;
    }

    private void deleteDoc(String title,String message,int position){
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("SI", (dialog, which) -> {
                    File fileToDel;
                    if(tipoFile==0){
                        fileToDel = new File("/storage/emulated/0/NAS/SpuntaGen/"+fileName.get(position));
                    }else{
                        fileToDel = new File("/storage/emulated/0/NAS/PresaGen/"+fileName.get(position));
                    }
                    fileToDel.delete();
                    txtNDoc.setVisibility(View.GONE);
                    txtLNDoc.setVisibility(View.GONE);
                    del.setVisibility(View.GONE);
                    open.setVisibility(View.GONE);
                    thisRow.setVisibility(View.GONE);
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void trovaNeS(String thisFN, String serie, int pos){
        XSSFWorkbook workbook;
        numDoc = new ArrayList<>();
        serieDoc = new ArrayList<>();

        try {
            String outFileName = thisFN;

            File path = new File("/storage/emulated/0/NAS/SpuntaGen");

            FileInputStream file = new FileInputStream(new File(path, outFileName));
            workbook = new XSSFWorkbook(file);
            int i = 1;
            while (workbook.getSheetAt(0).getRow(i) != null) {
                Row row = workbook.getSheetAt(0).getRow(i);
                if(i==1){
                    numDoc.add(row.getCell(pos).getStringCellValue());
                    serieDoc.add(serie);
                }else if(!numDoc.contains(row.getCell(pos).getStringCellValue())){
                    numDoc.add(row.getCell(pos).getStringCellValue());
                    serieDoc.add(serie);
                }
                i++;
            }
            ((MyApplication) context.getApplication()).setNum(numDoc);
            ((MyApplication) context.getApplication()).setSerie(serieDoc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
