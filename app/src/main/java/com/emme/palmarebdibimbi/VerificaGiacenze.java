package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.zebra.sdk.comm.BluetoothConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VerificaGiacenze extends AppCompatActivity {

    Spinner spinner;
    Button btnVG;
    String alias = "";
    ListView listView;
    TextView txtSelectedArt;
    String ipNeg = "";
    ConnectionClass connectionClass;
    ArrayList<String> codiciR = new ArrayList<>();
    ArrayList<String> descrizioniR = new ArrayList<>();
    ArrayList<String> eanR = new ArrayList<>();
    ArrayList<String> esistenzaR = new ArrayList<>();
    TextView txtCodArt, txtDesc, txtPV, txtPP;
    TextView txtEsSestu, txtEsMarconi,txtEsPirri,txtEsSassari,txtEsOlbia,txtEsNuoro,txtEsOristano,txtEsTortoli,txtEsCarbonia,txtEsTiburtina,txtEsCapena,txtEsOstiense,txtEsDep, txtEsCas;
    TextView txtOFSestu, txtOFMarconi,txtOFPirri,txtOFSassari,txtOFOlbia,txtOFNuoro,txtOFOristano,txtOFTortoli,txtOFCarbonia,txtOFTiburtina,txtOFCapena,txtOFOstiense,txtOFDep, txtOFCas;
    TextView txtOCSestu, txtOCMarconi,txtOCPirri,txtOCSassari,txtOCOlbia,txtOCNuoro,txtOCOristano,txtOCTortoli,txtOCCarbonia,txtOCTiburtina,txtOCCapena,txtOCOstiense,txtOCDep, txtOCCas;
    EditText insCodArt;
    int idL = 1;
    int mag = 0;
    String store;
    String bt = "";
    String printer = "";
    String artOrForn = "";
    String artOrFornDesc = "";
    ProgressBar pbVG;
    int giaPremuto = 0;
    Context context;
    String tipoEt = "";
    String codArt = "";
    EditText codArtET;
    String desc = "";
    String finePromo = "";
    String PV = "";
    String PP = "";
    String ean = "";
    String UDM = "";
    Double conv = 0.0;
    RadioButton rbQU, rbQEs;
    Integer qta = 0;
    CheckBox chkPL;
    Spinner spnTL;
    Integer qtaL = 0;
    com.zebra.sdk.comm.Connection connection;

    public void risolviMagL(){
        switch (store) {
            case "MASTER":
                ipNeg = "192.168.1.41";
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                mag = 77;
                idL = 5;
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                mag = 35;
                idL = 5;
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                mag = 72;
                idL = 5;
                break;
            case "OLBIA":
                ipNeg = "192.168.1.20";
                mag = 76;
                idL = 6;
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                mag = 74;
                idL = 2;
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                mag = 32;
                idL = 7;
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                mag = 78;
                idL = 4;
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                mag = 75;
                idL = 8;
                break;
            case "ORISTANO":
                ipNeg = "192.168.1.20";
                mag = 71;
                idL = 3;
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                mag = 85;
                idL = 4;
                break;
            case "CAPENA":
                ipNeg = "198.100.100.204";
                mag = 87;
                idL = 5;
                break;
            case "OSTIENSE":
                ipNeg = "196.100.100.203";
                mag = 86;
                idL = 3;
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.1.41";
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                mag = 90;
                idL = 23;
                break;
            case "INTRANSITO":
                ipNeg = "192.168.1.41";
                mag = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.1.41";
                mag = 89;
                idL = 1;
                break;
            default:
                ipNeg = "192.168.1.20";
                mag = 0;
                idL = 1;
                break;
        }
    }

    private void stampaZebra(){
        try {

            String desc1, desc2;
            if(txtDesc.getText().toString().length() > 28){
                desc1 = txtDesc.getText().toString().substring(0,28);
                desc2 = txtDesc.getText().toString().substring(28);
            }else{
                desc1 = txtDesc.getText().toString();
                desc2 = "";
            }
            desc1 = desc1.replace('\n', ' ');
            desc2 = desc2.replace('\n', ' ');

            String przV = txtPV.getText().toString();
            String przP = txtPP.getText().toString();
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
            String printFP = "";
            if(!finePromo.equals("")){
                finePromo = finePromo.substring(8,10) +"/"+ finePromo.substring(5,7) +"/"+ finePromo.substring(0,4);
                printFP = "Scadenza promo: " + finePromo;
            }

            String cpclData = "";
            switch(spnTL.getSelectedItem().toString()){
                case "Etichetta piccola":
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
                            "TEXT 7 0 5 110 " + txtCodArt.getText().toString() +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "B 128 1 0 50 5 140 " + ean +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 195 " + ean +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalino":
                    cpclData = "! 100 0 0 730 1" +
                            "\n" +
                            "T270 4 1 605 170 " + przV +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 125 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 2 95 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            alias +
                            "\n" +
                            "VBARCODE 128 1 1 40 30 350  " + ean +
                            "\n" + "\n" +
                            "T270 5 0 25 150 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAK +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Etichetta promo":
                    Double scp = 100 - ((Double.parseDouble(przP.replace(",",".")) * 100)/Double.parseDouble(przV.replace(",",".")));
                    String scps = scp.toString();
                    if(scps.endsWith("9")){
                        scp += 1;
                    }
                    String sc = scp.toString().substring(0, scp.toString().indexOf("."));
                    cpclData = "! 0 0 0 285 1" +
                            "TEXT 5 0 40 25 " + przV +
                            "\n" + "LINE 8 8 150 8 1 " + "\n" +
                            "TEXT 4 0 170 0 " + przP +
                            "\n" + "\n" + "\n" + "\n" +
                            "TEXT 5 0 40 55 Sc% " + sc +"%"+
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 90 " + desc1 +
                            "\n" + "\n" +
                            "TEXT 7 0 5 110 " + desc2 +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 145 " + txtCodArt.getText().toString() +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 5 0 5 175 " + ean +
                            "\n" + "\n" + "CENTER" + "\n" + "\n" +
                            "TEXT 7 0 5 195 " + printFP +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalino promo":
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
                            "T270 0 2 40 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            "T270 5 0 40 250 " + ean +
                            "\n" + "\n" +
                            "T270 0 2 0 30 " + printPAKP +
                            "\n" + "\n" +
                            "T270 0 2 0 250 " + printFP +
                            "\n" + "\n" +
                            "PRINT" +
                            "\n" + "\n";
                    break;
                default:
                    break;
            }

            if(qtaL == 0){
                qta = 1;
            }else{
                qta = risolviMag();
            }

            for(int i=0; i<qta; i++){
                connection.write(cpclData.getBytes());
            }

        } catch (Exception e) {

            // Handle communications error here.

            e.printStackTrace();

        }
    }

    private static Double round(double value, int places){
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void loadAll(String codArtRec){
        setContentView(R.layout.activity_verifica_giacenze);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
        }
        risolviMagL();

        connectionClass = new ConnectionClass();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp","");
        printer = preferences.getString("printer", "");

        btnVG = findViewById(R.id.btnVG);
        pbVG = findViewById(R.id.pbVG);
        pbVG.setVisibility(View.GONE);
        txtCodArt = findViewById(R.id.txtCodArtVG);
        txtDesc = findViewById(R.id.txtDescVG);
        txtPV = findViewById(R.id.txtPVVG);
        txtPP = findViewById(R.id.txtPPVG);
        txtEsSestu = findViewById(R.id.txtEsSestu);
        txtEsMarconi = findViewById(R.id.txtEsMarconi);
        txtEsPirri = findViewById(R.id.txtEsPirri);
        txtEsSassari = findViewById(R.id.txtEsSassari);
        txtEsOlbia = findViewById(R.id.txtEsOlbia);
        txtEsNuoro = findViewById(R.id.txtEsNuoro);
        txtEsOristano = findViewById(R.id.txtEsOristano);
        txtEsTortoli = findViewById(R.id.txtEsTortoli);
        txtEsCarbonia = findViewById(R.id.txtEsCarbonia);
        txtEsTiburtina = findViewById(R.id.txtEsTiburtina);
        txtEsCapena = findViewById(R.id.txtEsCapena);
        txtEsCas = findViewById(R.id.txtEsCas);
        txtEsOstiense = findViewById(R.id.txtEsOstiense);
        txtEsDep = findViewById(R.id.txtEsDep);
        txtOFSestu = findViewById(R.id.txtOFSestu);
        txtOFMarconi = findViewById(R.id.txtOFMarconi);
        txtOFPirri = findViewById(R.id.txtOFPirri);
        txtOFSassari = findViewById(R.id.txtOFSassari);
        txtOFOlbia = findViewById(R.id.txtOFOlbia);
        txtOFNuoro = findViewById(R.id.txtOFNuoro);
        txtOFOristano = findViewById(R.id.txtOFOristano);
        txtOFTortoli = findViewById(R.id.txtOFTortoli);
        txtOFCarbonia = findViewById(R.id.txtOFCarbonia);
        txtOFTiburtina = findViewById(R.id.txtOFTiburtina);
        txtOFCapena = findViewById(R.id.txtOFCapena);
        txtOFCas = findViewById(R.id.txtOFCas);
        txtOFOstiense = findViewById(R.id.txtOFOstiense);
        txtOFDep = findViewById(R.id.txtOFDep);
        txtOCSestu = findViewById(R.id.txtOCSestu);
        txtOCMarconi = findViewById(R.id.txtOCMarconi);
        txtOCPirri = findViewById(R.id.txtOCPirri);
        txtOCSassari = findViewById(R.id.txtOCSassari);
        txtOCOlbia = findViewById(R.id.txtOCOlbia);
        txtOCNuoro = findViewById(R.id.txtOCNuoro);
        txtOCOristano = findViewById(R.id.txtOCOristano);
        txtOCTortoli = findViewById(R.id.txtOCTortoli);
        txtOCCarbonia = findViewById(R.id.txtOCCarbonia);
        txtOCTiburtina = findViewById(R.id.txtOCTiburtina);
        txtOCCapena = findViewById(R.id.txtOCCapena);
        txtOCOstiense = findViewById(R.id.txtOCOstiense);
        txtOCDep = findViewById(R.id.txtOCDep);
        txtOCCas = findViewById(R.id.txtOCCas);
        insCodArt = findViewById(R.id.edtTxtCodArtVG);
        btnVG = findViewById(R.id.btnVG);
        chkPL = findViewById(R.id.chkPrintL);
        spnTL = findViewById(R.id.spnTL);
        rbQU = findViewById(R.id.rbQUno);
        rbQEs = findViewById(R.id.rbQEs);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTL.setAdapter(adapter);

        rbQU.setChecked(true);

        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        if(!codArtRec.equals("")){
            insCodArt.setText(codArtRec);
        }

        rbQEs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    qtaL = 1;
                }
            }
        });
        rbQU.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    qtaL = 0;
                }
            }
        });
        chkPL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && printer.equals("ZEBRA")){
                    connection = new BluetoothConnection(bt);
                    try{
                        connection.open();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try{
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            svuotaTutto();
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    txtPP.setText("");
                    finePromo = "";
                    hideKeyboard(this);
                    artOrForn = insCodArt.getText().toString();
                    VerificaGiacenze.FindArt cercaArt = new VerificaGiacenze.FindArt();
                    cercaArt.execute("");
                }
            }
            return false;
        });
        Button ricArt = findViewById(R.id.btnRicVG);
        ricArt.setOnClickListener(v -> {
            setContentView(R.layout.ricerca_crea_doc);

            codArtET = findViewById(R.id.edtCodR);
            txtSelectedArt = findViewById(R.id.txtSelectedArt);
            EditText descArt = findViewById(R.id.edtDescR);
            listView = findViewById(R.id.listRicArt);

            Button btnCloseRic = findViewById(R.id.btnExitRic);
            btnCloseRic.setOnClickListener(x -> {
                loadAll("");
            });
            txtSelectedArt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    loadAll(txtSelectedArt.getText().toString());
                }
            });
            Button btnRic = findViewById(R.id.btnRicXCD);
            btnRic.setOnClickListener(x ->{
                codiciR = new ArrayList<>();
                descrizioniR = new ArrayList<>();
                eanR = new ArrayList<>();
                esistenzaR = new ArrayList<>();
                listView.setAdapter(null);
                artOrForn = codArtET.getText().toString();
                artOrFornDesc = descArt.getText().toString();
                VerificaGiacenze.RicArt cercaArt = new VerificaGiacenze.RicArt();
                cercaArt.execute("");
            });
        });
        btnVG.setOnClickListener(v -> {
            svuotaTutto();
            artOrForn = insCodArt.getText().toString();
            VerificaGiacenze.FindArt findArt = new VerificaGiacenze.FindArt();
            findArt.execute();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAll("");
    }

    public void svuotaTutto(){
            finePromo = "";
            txtPP.setText("");
            txtEsDep.setText("");
            txtEsSestu.setText("");
            txtEsMarconi.setText("");
            txtEsPirri.setText("");
            txtEsOlbia.setText("");
            txtEsSassari.setText("");
            txtEsNuoro.setText("");
            txtEsCarbonia.setText("");
            txtEsTortoli.setText("");
            txtEsOristano.setText("");
            txtEsTiburtina.setText("");
            txtEsCapena.setText("");
            txtEsCas.setText("");
            txtEsOstiense.setText("");
            txtOFDep.setText("");
            txtOFSestu.setText("");
            txtOFMarconi.setText("");
            txtOFPirri.setText("");
            txtOFOlbia.setText("");
            txtOFSassari.setText("");
            txtOFNuoro.setText("");
            txtOFCarbonia.setText("");
            txtOFTortoli.setText("");
            txtOFOristano.setText("");
            txtOFTiburtina.setText("");
            txtOFCapena.setText("");
            txtOFCas.setText("");
            txtOFOstiense.setText("");
            txtOCDep.setText("");
            txtOCSestu.setText("");
            txtOCMarconi.setText("");
            txtOCPirri.setText("");
            txtOCOlbia.setText("");
            txtOCSassari.setText("");
            txtOCNuoro.setText("");
            txtOCCarbonia.setText("");
            txtOCTortoli.setText("");
            txtOCOristano.setText("");
            txtOCTiburtina.setText("");
            txtOCCapena.setText("");
            txtOCCas.setText("");
            txtOCOstiense.setText("");
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void stampaEtichette(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificaGiacenze.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        spinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPadding(0,8,0,0);
        layout.addView(spinner);

        Spinner spinnerQta = new Spinner(this);
        ArrayAdapter<CharSequence> adapterQta = ArrayAdapter.createFromResource(this, R.array.qta_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQta.setAdapter(adapterQta);
        spinnerQta.setPadding(0,8,0,0);
        layout.addView(spinnerQta);

        builder.setView(layout);

        builder.setNegativeButton("Annulla", (dialog, which) -> {
                    dialog.cancel();
                });
        builder.setPositiveButton("Ok", (dialog, which) -> {

        });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private static BluetoothSocket btsocket;
    private static OutputStream btoutputstream;

    protected void connect() {
        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), BTDeviceList.class);
            this.startActivityForResult(BTIntent, BTDeviceList.REQUEST_CONNECT_BT);
        }
        else{

            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;
            print_bt();

        }

    }
    private void print_bt() {
        try {

            btoutputstream = btsocket.getOutputStream();

            String desc1, desc2;
            desc = desc.replace('\n', ' ');
            if(desc.length() > 28){
                desc1 = desc.substring(0,23);
                desc2 = desc.substring(23);
            }else{
                desc1 = desc;
                desc2 = "";
            }
            //byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
            //btoutputstream.write(printformat);
            if(qtaL == 0){
                qta = 1;
            }else{
                qta = risolviMag();
            }

            String etichetta = "";
            for(int i=0; i<qta; i++){
                switch (spnTL.getSelectedItem().toString()){
                    case "Etichetta piccola":
                        etichetta = "^XA\n" +
                                "^FW^FO95,1^AR,75,75^FD"+round(Double.parseDouble(PV),2)+"^FS\n" +
                                "^FW^FO1,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO1,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FO80,140^BY1\n" +
                                "^BC,55,Y,N,N\n" +
                                "^FD"+ean+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO190,180^AR,100,100^FD"+round(Double.parseDouble(PV),2)+"^FS\n" +
                                "^FO140,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO100,100^AR,10,10^FD"+codArt+"^FS\n" +
                                "^FO50,100^BY2\n" +
                                "^BC,50,Y,N,N\n" +
                                "^FD"+ean+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Etichetta promo":
                        etichetta = "^XA\n" +
                                "^FW^FO140,1^AR,60,60^FD"+round(Double.parseDouble(PP),2)+"^FS\n" +
                                "^FW^FO8,1^AR,5,5^FD"+round(Double.parseDouble(PV),2)+"^FS\n" +
                                "^FW^FO8,30^AR,5,5^FDSC% 20%^FS\n" +
                                "^FW^FO8,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO8,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FW^FO100,130^AR,5,5^FD"+codArt+"^FS\n" +
                                "^FW^FO75,160^AR,5,5^FD"+ean+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino promo":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO195,110^AR,100,100^FDOFFERTA^FS\n" +
                                "^FO165,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO120,25^AR,10,10^FD"+round(Double.parseDouble(PP),2)+" E.^FS\n" +
                                "^FO90,25^AR,10,10^FDSC. 29,77%^FS\n" +
                                "^FO70,210^AR,100,100^FD"+round(Double.parseDouble(PV),2)+" E.^FS\n" +
                                "^FO45,180^BY2\n" +
                                "^AR,10,10^FD"+ean+"^FS\n" +
                                "^XZ\n";
                        break;
                    default:
                        break;
                }
                btoutputstream.write(etichetta.getBytes());
                btoutputstream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btsocket!= null){
                btoutputstream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = BTDeviceList.getSocket();
            if(btsocket != null){
                print_bt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int risolviMag(){
        switch (mag){
            case 1:
            case 59:
                if(!txtEsDep.getText().toString().equals("")){
                    return Integer.parseInt(txtEsDep.getText().toString());
                }else{
                    return 1;
                }
            case 77:
                if(!txtEsSestu.getText().toString().equals("")){
                    return Integer.parseInt(txtEsSestu.getText().toString());
                }else{
                    return 1;
                }
            case 35:
                if(!txtEsMarconi.getText().toString().equals("")){
                    return Integer.parseInt(txtEsMarconi.getText().toString());
                }else{
                    return 1;
                }
            case 72:
                if(!txtEsPirri.getText().toString().equals("")){
                    return Integer.parseInt(txtEsPirri.getText().toString());
                }else{
                    return 1;
                }
            case 76:
                if(!txtEsOlbia.getText().toString().equals("")){
                    return Integer.parseInt(txtEsOlbia.getText().toString());
                }else{
                    return 1;
                }
            case 74:
                if(!txtEsSassari.getText().toString().equals("")){
                    return Integer.parseInt(txtEsSassari.getText().toString());
                }else{
                    return 1;
                }
            case 32:
                if(!txtEsNuoro.getText().toString().equals("")){
                    return Integer.parseInt(txtEsNuoro.getText().toString());
                }else{
                    return 1;
                }
            case 78:
                if(!txtEsCarbonia.getText().toString().equals("")){
                    return Integer.parseInt(txtEsCarbonia.getText().toString());
                }else{
                    return 1;
                }
            case 75:
                if(!txtEsTortoli.getText().toString().equals("")){
                    return Integer.parseInt(txtEsTortoli.getText().toString());
                }else{
                    return 1;
                }
            case 71:
                if(!txtEsOristano.getText().toString().equals("")){
                    return Integer.parseInt(txtEsOristano.getText().toString());
                }else{
                    return 1;
                }
            case 85:
                if(!txtEsTiburtina.getText().toString().equals("")){
                    return Integer.parseInt(txtEsTiburtina.getText().toString());
                }else{
                    return 1;
                }
            case 87:
                if(!txtEsCapena.getText().toString().equals("")){
                    return Integer.parseInt(txtEsCapena.getText().toString());
                }else{
                    return 1;
                }
            case 86:
                if(!txtEsOstiense.getText().toString().equals("")){
                    return Integer.parseInt(txtEsOstiense.getText().toString());
                }else{
                    return 1;
                }
            case 90:
                if(!txtEsCas.getText().toString().equals("")){
                    return Integer.parseInt(txtEsCas.getText().toString());
                }else{
                    return 1;
                }
            default:
                return 1;
        }
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificaGiacenze.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }


    public void prezziPromoCat(int priorita){
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
                String query = "SELECT fineValidita, Articolo.nome, Promozione.TipoValoreSconto, cast(CategoriaElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join CategoriaElementoxPromozione on (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.idCategoriaArticolo = CategoriaElementoxpromozione.idCategoria) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"' " +
                        "ORDER BY Promozione.priorita desc";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                if(res.next()) {
                    if(res.getInt("priorita") > priorita){
                        if(res.getInt("TipoValoreSconto") == 0){
                            Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                            txtPP.setText(pP.toString());
                        }else{
                            txtPP.setText(res.getString("prezzoPromo"));
                        }
                        if(res.getString("fineValidita")!=null){
                            finePromo = res.getString("fineValidita");
                        }
                    }
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(con!=null){
            try{
                con.close();
            }catch (SQLException ex){

            }
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pbVG.setVisibility(View.GONE);
        if(chkPL.isChecked()){
            if(txtCodArt.getText().toString().equals("")){
                articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
            }else{
                ean = alias;
                if (printer.equals("ZEBRA")) {
                    stampaZebra();
                } else {
                    connect();
                }
            }
        }
        insCodArt.setText("");
    }

    protected void findPromo() {
        Connection con = null;
        ResultSet res;
        int priorita = 0;
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
                String query = "SELECT fineValidita, Articolo.nome, Promozione.TipoValoreSconto, cast(ElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.id = ElementoxPromozione.idElemento) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8'" +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or cast(fineValidita as date) >= GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and cast(inizioValidita as date) <= GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"' " +
                        "ORDER BY Promozione.priorita desc";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                if(res.next()) {
                    priorita = res.getInt("priorita");
                    if(res.getInt("TipoValoreSconto") == 0){
                        Double pP = res.getDouble("prz") - ((res.getDouble("prz")*res.getDouble("prezzoPromo"))/100);
                        txtPP.setText(pP.toString());
                    }else{
                        txtPP.setText(res.getString("prezzoPromo"));
                    }
                    if(res.getString("fineValidita")!=null){
                        finePromo = res.getString("fineValidita");
                    }
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(con!=null){
            try{
                con.close();
            }catch (SQLException ex){

            }
        }
        prezziPromoCat(priorita);
    }

    protected void recuperaPV() {
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
                String query = "SELECT cast (ArticoloxListino.prezzo as decimal(10,2)) as prz " +
                        "FROM Articolo " +
                        "JOIN ArticoloxListino ON (ArticoloxListino.idArticolo = Articolo.id) " +
                        "WHERE Articolo.nome = '" + artOrForn + "' " +
                        "AND ArticoloxListino.idListino = '"+idL+"' " +
                        "ORDER BY Articolo.nome ";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()) {
                    txtPV.setText(res.getString("prz"));

                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(con!=null){
            try{
                con.close();
            }catch (SQLException ex){

            }
        }
        findPromo();
    }

    protected void recuperaGiacenze() {
        Connection con = null;
        ResultSet res;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT cast (ProgressivoArticolo.esistenza as int) as Esistenza, " +
                        "cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) as OrdiniFornitore, " +
                        "cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) as OrdiniCliente, " +
                        "(Magazzino.id) as magn " +
                        "FROM ProgressivoArticolo " +
                        "JOIN Articolo ON (ProgressivoArticolo.MetaArticolo = Articolo.id) " +
                        "JOIN Magazzino on (ProgressivoArticolo.MetaMagazzino = Magazzino.id)" +
                        "WHERE Articolo.nome = '" + artOrForn + "' " +
                        "AND da < GETDATE() " +
                        "AND a > GETDATE() " +
                        "AND isNascosto = '0' " +
                        "ORDER BY Articolo.nome, Magazzino.nome";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()) {
                    switch (res.getInt("magn")){
                        case 1:
                            txtEsDep.setText(res.getString("Esistenza"));
                            txtOCDep.setText(res.getString("OrdiniCliente"));
                            txtOFDep.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 77:
                            txtEsSestu.setText(res.getString("Esistenza"));
                            txtOCSestu.setText(res.getString("OrdiniCliente"));
                            txtOFSestu.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 35:
                            txtEsMarconi.setText(res.getString("Esistenza"));
                            txtOCMarconi.setText(res.getString("OrdiniCliente"));
                            txtOFMarconi.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 72:
                            txtEsPirri.setText(res.getString("Esistenza"));
                            txtOCPirri.setText(res.getString("OrdiniCliente"));
                            txtOFPirri.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 76:
                            txtEsOlbia.setText(res.getString("Esistenza"));
                            txtOCOlbia.setText(res.getString("OrdiniCliente"));
                            txtOFOlbia.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 74:
                            txtEsSassari.setText(res.getString("Esistenza"));
                            txtOCSassari.setText(res.getString("OrdiniCliente"));
                            txtOFSassari.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 32:
                            txtEsNuoro.setText(res.getString("Esistenza"));
                            txtOCNuoro.setText(res.getString("OrdiniCliente"));
                            txtOFNuoro.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 78:
                            txtEsCarbonia.setText(res.getString("Esistenza"));
                            txtOCCarbonia.setText(res.getString("OrdiniCliente"));
                            txtOFCarbonia.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 75:
                            txtEsTortoli.setText(res.getString("Esistenza"));
                            txtOCTortoli.setText(res.getString("OrdiniCliente"));
                            txtOFTortoli.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 71:
                            txtEsOristano.setText(res.getString("Esistenza"));
                            txtOCOristano.setText(res.getString("OrdiniCliente"));
                            txtOFOristano.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 85:
                            txtEsTiburtina.setText(res.getString("Esistenza"));
                            txtOCTiburtina.setText(res.getString("OrdiniCliente"));
                            txtOFTiburtina.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 87:
                            txtEsCapena.setText(res.getString("Esistenza"));
                            txtOCCapena.setText(res.getString("OrdiniCliente"));
                            txtOFCapena.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 86:
                            txtEsOstiense.setText(res.getString("Esistenza"));
                            txtOCOstiense.setText(res.getString("OrdiniCliente"));
                            txtOFOstiense.setText(res.getString("OrdiniFornitore"));
                            break;
                        case 90:
                            txtEsCas.setText(res.getString("Esistenza"));
                            txtOCCas.setText(res.getString("OrdiniCliente"));
                            txtOFCas.setText(res.getString("OrdiniFornitore"));
                            break;
                        default:
                            break;
                    }
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(con!=null){
            try{
                con.close();
            }catch (SQLException ex){

            }
        }
        recuperaPV();
    }

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            pbVG.setVisibility(View.VISIBLE);
            desc = "";
        }

        @Override
        protected void onPostExecute(String r) {

            if(isSuccess) {
                txtCodArt.setText(artOrForn);
                txtDesc.setText(desc);
                recuperaGiacenze();
            }else{
                articoloNonTrovato("Attenzione!", "Articolo non presente nel database");
                insCodArt.setText("");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pbVG.setVisibility(View.GONE);
            }
            if(giaPremuto == 1){
                giaPremuto = 0;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice, " +
                            "(select fattoreconversione from udmxarticolo where idArticolo = articolo.id) as conv, " +
                            "(select idUDM from udmxarticolo where idArticolo = articolo.id) as UDM " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo)" +
                            "WHERE articolo.nome = '"+artOrForn+"' " +
                            "OR alias.codice = '"+artOrForn+"'" +
                            "OR articolo.nome = '"+artOrForn.trim()+"'" +
                            "OR alias.codice = '"+artOrForn.trim()+"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        isSuccess = true;
                        artOrForn = res.getString("nome");
                        desc = res.getString("descrizione");
                        alias = res.getString("codice");
                        if(res.getString("UDM") != null){
                            UDM=res.getString("UDM");
                            conv=res.getDouble("conv");
                        }else{
                            UDM="";
                            conv=0.0;
                        }
                    }
                }
            }catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if(con!=null){
                try{
                    con.close();
                }catch (SQLException ex){

                }
            }
            return z;
        }

    }

    public void setRows(){
        VerificaGiacenze.AdapterRicArt whatever = new VerificaGiacenze.AdapterRicArt(this, codiciR, descrizioniR, eanR, txtSelectedArt, esistenzaR);
        listView.setAdapter(whatever);
    }

    public static class AdapterRicArt extends ArrayAdapter {

        private final Activity context;
        private ArrayList<String> codArt, alias;
        private ArrayList<String> desc, esistenza;
        private TextView codArtET;

        public AdapterRicArt(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> aliasArrayParam, TextView codArtET, ArrayList<String> esistenzaArrayParam) {

            super(context, R.layout.adapter_show_arts, codArtArrayParam);

            this.context = context;
            this.codArt = codArtArrayParam;
            this.desc = descArrayParam;
            this.alias = aliasArrayParam;
            this.codArtET = codArtET;
            this.esistenza = esistenzaArrayParam;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.adapter_show_arts, null, true);

            TextView txtCodArt = rowView.findViewById(R.id.txtCodArtR);
            TextView txtDesc = rowView.findViewById(R.id.txtDescArtR);
            TextView txtEs = rowView.findViewById(R.id.txtGiacArtR);
            TextView txtEAN = rowView.findViewById(R.id.txtEanArtR);

            txtCodArt.setText(codArt.get(position));
            txtDesc.setText(desc.get(position));
            txtEAN.setText(alias.get(position));
            txtEs.setText("Esistenza: "+esistenza.get(position));

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    codArtET.setText(codArt.get(position));
                }
            });

            return rowView;
        }

    }

    public class RicArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                setRows();
            }else{
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice, " +
                            "(select cast(esistenza as int) from progressivoArticolo where Articolo.id = metaarticolo and metamagazzino = "+mag+" and da < GETDATE() AND a > GETDATE()) as esistenza " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo)" +
                            "WHERE articolo.nome like '%" + artOrForn.trim() + "%' and articolo.descrizione like '%" + artOrFornDesc + "%' " +
                            "OR alias.codice = '%" + artOrForn.trim() + "%' and articolo.descrizione like '%" + artOrFornDesc + "%' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess = true;
                        codiciR.add(res.getString("nome"));
                        descrizioniR.add(res.getString("descrizione"));
                        eanR.add(res.getString("codice"));
                        if(res.getString("esistenza")!=null){
                            esistenzaR.add(res.getString("esistenza"));
                        }else{
                            esistenzaR.add("0");
                        }
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {

                }
            }
            return z;
        }
    }
}