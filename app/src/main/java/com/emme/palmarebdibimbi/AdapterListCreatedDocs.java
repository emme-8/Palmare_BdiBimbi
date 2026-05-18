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

import java.io.File;
import java.util.ArrayList;

    public class AdapterListCreatedDocs extends ArrayAdapter {

        private final Activity context;
        private ArrayList<String> nome;
        private ArrayList<String> year;
        private ArrayList<String> fileName;
        int mag, listino, tipoFile;
        TextView txtNome, txtLNome;
        LinearLayout thisRow;
        Button del, open;

        public AdapterListCreatedDocs(Activity context, ArrayList<String> fornArrayParam, ArrayList<String> yearArrayParam,
                                      ArrayList<String> fileNameArrayParam, int mag, int listino, int tipoFile) {

            super(context, R.layout.adapter_doc_row, fornArrayParam);
            this.context = context;
            this.nome = fornArrayParam;
            this.year = yearArrayParam;
            this.fileName = fileNameArrayParam;
            this.mag = mag;
            this.listino = listino;
            this.tipoFile = tipoFile;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_list_created_docs, null, true);

            //this code gets references to objects in the listview_row.xml file
            txtNome = convertView.findViewById(R.id.txtNumeroDR);
            txtLNome = convertView.findViewById(R.id.txtLNumeroDR);
            thisRow = convertView.findViewById(R.id.thisRowDR);
            del = convertView.findViewById(R.id.btnDelDR);
            open = convertView.findViewById(R.id.btnOpenDR);

            if(tipoFile==3){
                txtLNome.setText("Posizione");
            }

            txtNome.setText(fileName.get(position));

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDoc("Attenzione!","Sei sicuro di voler eliminare questo file?", position);
                }
            });
            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> numDoc = new ArrayList<>();
                    ArrayList<String> serieDoc = new ArrayList<>();
                    ((MyApplication) context.getApplication()).setNum(numDoc);
                    ((MyApplication) context.getApplication()).setSerie(serieDoc);
                    Intent spunta=null;
                    int magRif = 1;
                    String magazzino = "";
                    if (tipoFile == 2) {
                        spunta = new Intent(context, CreaDocumenti.class);
                        spunta.putExtra("nomeDaGestione",nome.get(position));
                    } else if (tipoFile == 3) {
                        spunta = new Intent(context, InvDepOff.class);
                        spunta.putExtra("nG", nome.get(position));
                        spunta.putExtra("nSp", year.get(position));
                    }
                    spunta.putExtra("listino", listino);
                    spunta.putExtra("mag", mag);
                    spunta.putExtra("storeName", magazzino);
                    spunta.putExtra("rip", 0);
                    spunta.putExtra("magRif", magRif);
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
                        if (tipoFile == 2) {
                            fileToDel = new File("/storage/emulated/0/NAS/CreatedDocs/" + fileName.get(position));
                        } else {
                            fileToDel = new File("/storage/emulated/0/NAS/SpuntaGen/" + fileName.get(position));
                        }
                        fileToDel.delete();
                        txtNome.setVisibility(View.GONE);
                        txtLNome.setVisibility(View.GONE);
                        del.setVisibility(View.GONE);
                        open.setVisibility(View.GONE);
                        thisRow.setVisibility(View.GONE);
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }
    }
