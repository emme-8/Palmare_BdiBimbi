package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterSelBF extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener{

    private final Activity context;
    SparseBooleanArray mCheckStates;
    private ArrayList<String> idSel = new ArrayList<>();
    private ArrayList<String> nDocSel = new ArrayList<>();
    private ArrayList<String> serieSel = new ArrayList<>();
    private ArrayList<String> id;
    private ArrayList<String> ndoc;
    private ArrayList<String> forn;
    private ArrayList<String> serie;
    private ArrayList<String> data;
    Boolean ubic;
    int listino, mag, spuntaOrPresa, listinoRif, magRif;
    String tipoRiga, tipo, magazzino, fornitore;

    public AdapterSelBF(Activity context, ArrayList<String> idArrayParam, ArrayList<String> ndocArrayParam, ArrayList<String> fornArrayParam, ArrayList<String> dataArrayParam,
                        String tipoRiga, Boolean ubic, int listino, int mag, String tipo, int spuntaOrPresa, String magazzino, int listinoRif, int magRif, ArrayList<String> serieArrayParam){

        super(context,R.layout.adapter_bf , idArrayParam);
        this.tipo = tipo;
        this.context=context;
        this.id = idArrayParam;
        this.ndoc = ndocArrayParam;
        this.forn = fornArrayParam;
        this.data = dataArrayParam;
        this.serie = serieArrayParam;
        this.tipoRiga = tipoRiga;
        this.ubic = ubic;
        this.listino = listino;
        this.mag = mag;
        this.listinoRif = listinoRif;
        this.magRif = magRif;
        this.spuntaOrPresa = spuntaOrPresa;
        this.magazzino = magazzino;
        mCheckStates = new SparseBooleanArray(id.size());
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.adapter_bf, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtId = convertView.findViewById(R.id.txtCodArtRow);
        TextView txtNDoc = convertView.findViewById(R.id.txtDescRow);
        TextView txtForn = convertView.findViewById(R.id.txtQtaRow);
        TextView txtData = convertView.findViewById(R.id.txtData);
        TextView txtSerie = convertView.findViewById(R.id.txtSerieDoc);
        Button btnOpenDoc = context.findViewById(R.id.btnOpenMulDoc);
        CheckBox selDoc = convertView.findViewById(R.id.cbSpunta);

        txtId.setText(id.get(position));
        txtNDoc.setText(ndoc.get(position));
        txtForn.setText(forn.get(position));
        txtData.setText(data.get(position));
        txtSerie.setText(serie.get(position));
        selDoc.setTag(position);
        selDoc.setChecked(mCheckStates.get(position, false));
        selDoc.setOnCheckedChangeListener(this);
        /*
        selDoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(selDoc.isChecked()){
                    selDoc.setChecked(true);
                    idSel.add(id.get(position));
                    nDocSel.add(ndoc.get(position));
                }else{
                    selDoc.setChecked(false);
                    idSel.remove(id.get(position));
                    nDocSel.remove(ndoc.get(position));
                }
            }
        });*/

        btnOpenDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fornitore = "";
                idSel=new ArrayList<>();
                nDocSel=new ArrayList<>();
                boolean right = true;
                for(int i = 0; i<id.size(); i++){
                    if(isChecked(i)){
                        if(fornitore.equals("") || fornitore.equals(forn.get(i))){
                            fornitore = forn.get(i);
                            idSel.add(id.get(i));
                            nDocSel.add(ndoc.get(i));
                            serieSel.add(serie.get(i));
                        }else{
                            right = false;
                            alertDisplayer("Errore!","Non puoi selezionare documenti di fornitori diversi");
                        }
                    }
                }if(idSel.size()==0 && right){
                    alertDisplayer("Errore!","Non hai selezionato nessun documento, riprova");
                }else if(right){
                    if(spuntaOrPresa==1){
                        alertArt("Attenzione!","Scegli il tipo di ordinamento che intendi visualizzare");
                    }else{
                        Intent openDoc = new Intent(context, ShowDoc.class);
                        openDoc.putExtra("spuntaOrPresa", spuntaOrPresa);
                        openDoc.putStringArrayListExtra("selIds", idSel);
                        openDoc.putStringArrayListExtra("selDocs", nDocSel);
                        openDoc.putStringArrayListExtra("serieDocs", serieSel);
                        openDoc.putExtra("tipoRiga", tipoRiga);
                        openDoc.putExtra("fornitore", fornitore);
                        openDoc.putExtra("listino", listino);
                        openDoc.putExtra("ubicazione", ubic);
                        openDoc.putExtra("mag", mag);
                        openDoc.putExtra("listinoRif", listinoRif);
                        openDoc.putExtra("magRif", magRif);
                        openDoc.putExtra("tipo", tipo);
                        openDoc.putExtra("magazzino", magazzino);
                        context.startActivity(openDoc);
                    }
                }
            }
        });
        return convertView;
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("Ordinamento classico");
        spinnerArrayTipo.add("Ordinamento per piano");
        Spinner spinnerTipo = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        layout.addView(spinnerTipo);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            Intent openDoc = new Intent(context, ShowDoc.class);
            openDoc.putExtra("spuntaOrPresa", spuntaOrPresa);
            openDoc.putStringArrayListExtra("selIds", idSel);
            openDoc.putStringArrayListExtra("selDocs", nDocSel);
            openDoc.putStringArrayListExtra("serieDocs", serieSel);
            openDoc.putExtra("tipoRiga", tipoRiga);
            openDoc.putExtra("tipoOrd", spinnerTipo.getSelectedItemPosition());
            openDoc.putExtra("fornitore", fornitore);
            openDoc.putExtra("listino", listino);
            openDoc.putExtra("ubicazione", ubic);
            openDoc.putExtra("mag", mag);
            openDoc.putExtra("listinoRif", listinoRif);
            openDoc.putExtra("magRif", magRif);
            openDoc.putExtra("tipo", tipo);
            openDoc.putExtra("magazzino", magazzino);
            context.startActivity(openDoc);
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        if(buttonView.isChecked()){
            mCheckStates.put((Integer) buttonView.getTag(), true);
            setChecked((Integer) buttonView.getTag(), true);
        }else{
            mCheckStates.put((Integer) buttonView.getTag(), false);
            setChecked((Integer) buttonView.getTag(), false);
        }


    }
}
