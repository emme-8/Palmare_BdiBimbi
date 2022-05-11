package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zebra.sdk.comm.BluetoothConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdapterShowDocPrintLabel extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener {
    private final Activity context;
    private ArrayList<String> codArt;
    private ArrayList<String> desc;
    private ArrayList<String> qta;
    private ArrayList<String> es;
    private ArrayList<String> pl;
    private ArrayList<String> alias;
    private ArrayList<String> pp;
    private ArrayList<String> UDMs;
    private ArrayList<Double> convs;
    SparseBooleanArray mCheckStates;
    TextView txtCodArt;
    TextView txtDesc;
    TextView txtQta;
    TextView txtEs;
    CheckBox selArt;
    TextView txtPV;
    TextView txtPP;
    String bt = "";
    Button btnOpenDoc;
    com.zebra.sdk.comm.Connection connection;

    public AdapterShowDocPrintLabel(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> aliasArrayParam,
                                    ArrayList<String> qtaArrayParam, ArrayList<String> esArrayParam, ArrayList<String> plArrayParam,
                                    ArrayList <String> ppArrayParam, ArrayList <String> udmArrayParam, ArrayList <Double> convArrayParam) {

        super(context, R.layout.adapter_show_doc_print_label, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qta = qtaArrayParam;
        this.es = esArrayParam;
        this.pl = plArrayParam;
        this.pp = ppArrayParam;
        this.UDMs = udmArrayParam;
        this.convs = convArrayParam;
        this.alias = aliasArrayParam;
        mCheckStates = new SparseBooleanArray(codArt.size());
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_show_doc_print_label, null, true);

        //this code gets references to objects in the listview_row.xml file
        txtCodArt = rowView.findViewById(R.id.txtCodArtRowPrint);
        txtDesc = rowView.findViewById(R.id.txtDescRowPrint);
        txtQta = rowView.findViewById(R.id.txtQtaRowPrint);
        txtEs = rowView.findViewById(R.id.txtEsRowPrint);
        selArt = rowView.findViewById(R.id.cbSelArt);
        txtPV = rowView.findViewById(R.id.txtPLPL);
        txtPP = rowView.findViewById(R.id.txtPPPL);
        btnOpenDoc = context.findViewById(R.id.btnSpuntaDoc);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        String quta = qta.get(position);
        txtQta.setText(quta);
        if(es.get(position) == null || es.get(position).equals("")){
            txtEs.setText("0");
        }else{
            txtEs.setText(es.get(position));
        }
        txtPV.setText(pl.get(position));
        txtPP.setText(pp.get(position));
        selArt.setTag(position);
        selArt.setChecked(mCheckStates.get(position, false));
        selArt.setOnCheckedChangeListener(this);

        btnOpenDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                bt = preferences.getString("PrinterIp","");
                connection = new BluetoothConnection(bt);
                try{
                    connection.open();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertArt("","");
            }
        });

        return rowView;

    }

    private void stampaZebra(String desc, String pvp, String ppp, String UDM, Double conv, String ean, String tipo, String codArt, String tipoQta, Integer qtaDoc, Integer qtaEs){
        try {

            String desc1, desc2;
            if(desc.length() > 28){
                desc1 = desc.substring(0,28);
                desc2 = desc.substring(28);
            }else{
                desc1 = desc;
                desc2 = "";
            }

            String przV = pvp;
            String przP = ppp;
            if(przV.substring(przV.indexOf(".")+1).length()<2){
                przV = przV + "0";
            }else if(przP.substring(przP.indexOf(".")+1).length()>2){
                przV = przV.substring(0,przV.indexOf(".")) + przV.substring(przV.indexOf("."),przV.indexOf(".")+3);
            }
            if(przP.substring(przP.indexOf(".")+1).length()<2){
                przP = przP + "0";
            }else if(przP.substring(przP.indexOf(".")+1).length()>2){
                przP = przP.substring(0,przP.indexOf(".")) + przP.substring(przP.indexOf("."),przP.indexOf(".")+3);
            }
            String printPAK = "";
            String printPAKP = "";
            if(!UDM.equals("")){
                Double przAlKL = Double.parseDouble(przV) * conv;
                Double przAlKLP = Double.parseDouble(przP) * conv;
                przAlKL = round(przAlKL,2);
                if(!UDM.equals("6")){
                    printPAK = "Prezzo al kg " + przAlKL.toString();
                    printPAKP = "Prezzo al kg " + przAlKLP.toString();
                }else{
                    printPAK="Prezzo al lt " + przAlKL.toString();
                    printPAKP="Prezzo al lt " + przAlKLP.toString();
                }
            }

            String cpclData = "";
            switch(tipo){
                case "Etichette piccole":
                    cpclData = "!LABEL " +
                            "\n" + "\n" +
                            "! 0 0 0 275 1" + "CENTER" +
                            "\n" +
                            "TEXT 4 0 0 0 " + przV +
                            "\n" + "\n" + "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 55 " + desc1 +
                            "\n" + "\n" +
                            "TEXT 7 0 5 80 " + desc2 +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 110 " + codArt +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "B 128 1 0 50 5 140 " + ean +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 195 " + ean +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalini":
                    cpclData = "! 100 0 0 730 1" +
                            "\n" +
                            "T270 4 1 605 170 " + przV +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 125 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 2 95 70 " + codArt +
                            "\n" + "\n" +
                            ean +
                            "\n" +
                            "VBARCODE 128 1 1 40 30 350  " + ean +
                            "\n" + "\n" +
                            "T270 5 0 25 150 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAK +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Etichette promo":
                    Double scp = 100 - ((Double.parseDouble(przP.replace(",",".")) * 100)/Double.parseDouble(przV.replace(",",".")));
                    String scps = scp.toString();
                    if(scps.endsWith("9")){
                        scp += 1;
                    }
                    String sc = scp.toString().substring(0, scp.toString().indexOf("."));
                    cpclData = "! 0 0 0 285 1" +
                            "TEXT 5 0 40 25 " + przV +
                            "\n" + "LINE 8 8 150 8 1 " + "\n" +
                            "TEXT 4 0 170 25 " + przP +
                            "\n" + "\n" + "\n" + "\n" +
                            "TEXT 5 0 40 55 Sc% " + sc +"%"+
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 90 " + desc1 +
                            "\n" + "\n" +
                            "TEXT 7 0 5 110 " + desc2 +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 145 " + codArt +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 175 " + ean +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalini promo":
                    cpclData = "! 80 0 0 720 1" +
                            "\n" +
                            "T270 4 1 255 150  OFFERTA " +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 165 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 3 145 40 " +
                            desc2 +
                            "\n" + "\n" +
                            "T270 0 5 100 10 " +
                            "\n" + "\n" +
                            "T270 0 5 100 40 " + przV +
                            "\n" + "\n" +
                            "LINE 70 5 85 150  1 " +
                            "\n" + "\n" +
                            "T270 4 1 135 350 " + przP +
                            "\n" + "\n" +
                            "T270 0 2 40 70 " + codArt +
                            "\n" + "\n" +
                            "T270 5 0 40 250 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAKP +
                            "\n" + "\n" +
                            "T270 0 2 0 250 " + "Scadenza promo: " +
                            "\n" + "\n" +
                            "PRINT" +
                            "\n" + "\n";
                    break;
                default:
                    break;
            }

            int qs;
            switch (tipoQta){
                case "Quantità documento": qs = qtaDoc;
                    break;
                case "Quantità 1": qs = 1;
                    break;
                case "Quantità esistenza": qs = qtaEs;
                    break;
                default: qs= 0;
                    break;
            }

            for(int i=0; i<qs; i++){
                connection.write(cpclData.getBytes());
            }

        } catch (Exception e) {

            // Handle communications error here.

            e.printStackTrace();

        }
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("Etichette piccole");
        spinnerArrayTipo.add("Frontalini");
        spinnerArrayTipo.add("Etichette promo");
        spinnerArrayTipo.add("Frontalini promo");
        Spinner spinnerTipo = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        layout.addView(spinnerTipo);

        ArrayList<String> spinnerArrayDoc = new ArrayList<String>();
        spinnerArrayDoc.add("Intero documento");
        spinnerArrayDoc.add("Righe selezionate");
        Spinner spinnerDoc = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapterDoc = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayDoc);
        spinnerDoc.setAdapter(spinnerArrayAdapterDoc);

        layout.addView(spinnerDoc);

        ArrayList<String> spinnerArrayQta = new ArrayList<String>();
        spinnerArrayQta.add("Quantità documento");
        spinnerArrayQta.add("Quantità 1");
        spinnerArrayQta.add("Quantità esistenza");
        Spinner spinnerQta = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapterQta = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayQta);
        spinnerQta.setAdapter(spinnerArrayAdapterQta);

        layout.addView(spinnerQta);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            if(spinnerDoc.getSelectedItem().toString().equals("Righe selezionate")){
                for(int i = 0; i<codArt.size(); i++){
                    if(isChecked(i)){
                        stampaZebra(desc.get(i), pl.get(i), pp.get(i), UDMs.get(i), convs.get(i), alias.get(i),
                                spinnerTipo.getSelectedItem().toString(), codArt.get(i), spinnerQta.getSelectedItem().toString(),
                                Integer.parseInt(qta.get(i)), Integer.parseInt(es.get(i)));
                    }
                }
            }else{
                for(int i = 0; i<codArt.size(); i++){
                    if(es.get(i) == null || es.get(i).equals("")){
                        es.set(i,"0");
                    }
                    stampaZebra(desc.get(i), pl.get(i), pp.get(i), UDMs.get(i), convs.get(i), alias.get(i),
                            spinnerTipo.getSelectedItem().toString(), codArt.get(i), spinnerQta.getSelectedItem().toString(),
                            Integer.parseInt(qta.get(i)), Integer.parseInt(es.get(i)));
                }
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private static Double round(double value, int places){
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
