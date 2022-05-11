package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.zebra.sdk.comm.BluetoothConnection;

import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class IniziaSpuntaNegSmart extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    ConnectionClass connectionClass;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> qtaDaScalare = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> codici = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> qtaSpunta = new ArrayList<>();
    ArrayList<String> qtaDocum = new ArrayList<>();
    ArrayList<String> numero = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> timeSparata = new ArrayList<>();
    ProgressBar pbSearchArt;
    String findThis, ean;
    Double prz;
    int nColli = 0;
    Spinner spinner;
    String docsName = "";
    CheckBox chkEtic;
    Integer mag, listino;
    Integer esistenza = 0;
    Integer of = 0;
    Integer oc = 0;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, txtPrzArt, txtEsSp, txtQtaLP, txtOrdF, txtOrdC;
    Button search, btnNextArt, btnBackArt, fineSpunta;
    EditText insNColliSpunta, insQtaSpunta, insQtaSS, barcodeText;
    Context context;
    String tipoDoc;
    String printer = "";
    String fornitore = "";
    RadioButton rbSS, rbSM;
    int tipoSparata = 0;
    String bt = "";
    int giaPremuto = 0;
    String tipoEt = "";
    String codArt = "";
    String desc = "";
    String PV = "";
    String PP = "";
    String eanP = "";
    Integer qtaP = 0;
    com.zebra.sdk.comm.Connection connection;

    SharedPreferences prefs;

    public void recuperaStato(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArrayCod = new JSONArray(preferences.getString("codInt", "[]"));
            JSONArray jsonArrayDesc = new JSONArray(preferences.getString("descInt", "[]"));
            JSONArray jsonArrayQtaDoc = new JSONArray(preferences.getString("qtaDocInt", "[]"));
            JSONArray jsonArrayQtaRes = new JSONArray(preferences.getString("qtaResInt", "[]"));
            JSONArray jsonArrayQta = new JSONArray(preferences.getString("qtaInt", "[]"));
            JSONArray jsonArrayAlias = new JSONArray(preferences.getString("eanInt", "[]"));
            JSONArray jsonArrayNumDoc = new JSONArray(preferences.getString("numInt", "[]"));
            JSONArray jsonArrayTimeSp = new JSONArray(preferences.getString("timeInt", "[]"));
            for (int i = 0; i < jsonArrayCod.length(); i++) {
                codici.add(jsonArrayCod.getString(i));
                codArticolo.add(jsonArrayCod.getString(i));
                descrizioni.add(jsonArrayDesc.getString(i));
                qta.add(jsonArrayQtaDoc.getString(i));
                qtaDocum.add(jsonArrayQtaDoc.getString(i));
                qtaDaScalare.add(jsonArrayQtaRes.getString(i));
                qtaSpunta.add(jsonArrayQta.getString(i));
                alias.add(jsonArrayAlias.getString(i));
                numDoc.add(jsonArrayNumDoc.getString(i));
                numero.add(jsonArrayNumDoc.getString(i));
                timeSparata.add(jsonArrayTimeSp.getString(i));
            }
            docsName = preferences.getString("nameInt","");
            mag = preferences.getInt("magInt", 1);
            listino = preferences.getInt("listInt",6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void salvaStato(){
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        JSONArray jsonArrayCod = new JSONArray();
        JSONArray jsonArrayDesc = new JSONArray();
        JSONArray jsonArrayQtaDoc = new JSONArray();
        JSONArray jsonArrayQtaRes = new JSONArray();
        JSONArray jsonArrayQta = new JSONArray();
        JSONArray jsonArrayAlias = new JSONArray();
        JSONArray jsonArrayNumDoc = new JSONArray();
        JSONArray jsonArrayTimeSp = new JSONArray();
        for(int i=0; i<codici.size(); i++){
            jsonArrayCod.put(codici.get(i));
            jsonArrayDesc.put(descrizioni.get(i));
            jsonArrayQtaDoc.put(qtaDocum.get(i));
            jsonArrayQta.put(qtaSpunta.get(i));
            jsonArrayQtaRes.put(qtaDaScalare.get(i));
            jsonArrayAlias.put(alias.get(i));
            jsonArrayNumDoc.put(numero.get(i));
            jsonArrayTimeSp.put(timeSparata.get(i));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("codInt", jsonArrayCod.toString());
        editor.putInt("magInt", mag);
        editor.putInt("listInt", listino);
        editor.putString("nameInt", docsName);
        editor.putString("timeInt", jsonArrayTimeSp.toString());
        editor.putString("descInt", jsonArrayDesc.toString());
        editor.putString("qtaDocInt", jsonArrayQtaDoc.toString());
        editor.putString("qtaResInt", jsonArrayQtaDoc.toString());
        editor.putString("qtaInt", jsonArrayQta.toString());
        editor.putString("eanInt", jsonArrayAlias.toString());
        editor.putString("numInt", jsonArrayNumDoc.toString());
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
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

    @Override
    public void onBackPressed() {
        alertDisplayer("Attenzione!","Sei sicuro di voler abbandonare la pagina di spunta? Verrai riportato alla home!");
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaSpuntaNegSmart.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaChiudi(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    salvaStato();
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpuntaNegSmart.this, ReviewSpuntaNeg.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putExtra("docsName", docsName);
                    review.putStringArrayListExtra("qtaDoc", qtaDocum);
                    review.putStringArrayListExtra("qtaSpunta", qtaSpunta);
                    review.putStringArrayListExtra("nDoc", numero);
                    review.putStringArrayListExtra("timeSp", timeSparata);
                    review.putExtra("tipo", 0);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_spunta_neg_smart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp","");
        printer = preferences.getString("printer", "");

        Bundle extras = getIntent().getExtras();
        int rip = 0;
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            rip = extras.getInt("rip");
            tipoDoc = extras.getString("tipoDoc");
            fornitore = extras.getString("fornitore");
        }

        connectionClass = new ConnectionClass();
        context = this;

        spinner = findViewById(R.id.spnEtic);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(rip == 1){
            recuperaStato(this);
        }else{
            codArticolo = ((MyApplication) this.getApplication()).getCodArt();
            qta = ((MyApplication) this.getApplication()).getQuantita();
            idDoc = ((MyApplication) this.getApplication()).getID();
            numDoc = ((MyApplication) this.getApplication()).getNum();
            descrizioni = ((MyApplication) this.getApplication()).getDesc();
            ArrayList<String> idXArt = new ArrayList<>();
            ArrayList<String> qtaArtXID = new ArrayList<>();
            ArrayList<String> tempCodArt = new ArrayList<>();
            ArrayList<String> tempNumDoc = new ArrayList<>();
            ArrayList<String> tempDesc = new ArrayList<>();
            for(int i=0; i<codArticolo.size(); i++) {
                Boolean ce = false;
                if (i == 0) {
                    idXArt.add(idDoc.get(i));
                    tempCodArt.add(codArticolo.get(i));
                    tempNumDoc.add(numDoc.get(i));
                    qtaArtXID.add(qta.get(i));
                    tempDesc.add(descrizioni.get(i));
                }else{
                    for (int j = 0; j < tempCodArt.size(); j++) {
                        if (tempCodArt.get(j).equals(codArticolo.get(i)) && idXArt.get(j).equals(idDoc.get(i))) {
                            Integer qDaAgg = Integer.parseInt(qta.get(i));
                            Integer qPres = Integer.parseInt(qtaArtXID.get(j));
                            Integer totQ = qDaAgg + qPres;
                            qtaArtXID.set(j, totQ.toString());
                            ce = true;
                        }
                    }
                    if (!ce) {
                        idXArt.add(idDoc.get(i));
                        tempCodArt.add(codArticolo.get(i));
                        tempNumDoc.add(numDoc.get(i));
                        qtaArtXID.add(qta.get(i));
                        tempDesc.add(descrizioni.get(i));
                    }
                }
            }
            codArticolo.clear();
            codArticolo = tempCodArt;
            qta.clear();
            qta = qtaArtXID;
            idDoc.clear();
            idDoc = idXArt;
            numDoc.clear();
            numDoc = tempNumDoc;
            descrizioni.clear();
            descrizioni = tempDesc;

            for(int i=0; i<codArticolo.size(); i++){
                codici.add(codArticolo.get(i));
                qtaDaScalare.add(qta.get(i));
                qtaSpunta.add("0");
                alias.add("");
                timeSparata.add("");
                qtaDocum.add(qta.get(i));
                numero.add(numDoc.get(i));
            }

            String tempnDoc = "";
            for(int z=0; z<numDoc.size(); z++) {
                if(z==0){
                    docsName = tipoDoc + fornitore + numDoc.get(0);
                }else if(!numDoc.get(z).equals(tempnDoc)){
                    docsName = docsName + "&" + numDoc.get(z);
                }
                tempnDoc = numDoc.get(z);
            }
        }

        chkEtic = findViewById(R.id.chkEticS);
        insQtaSS = findViewById(R.id.insQtaSSS);
        rbSS = findViewById(R.id.rbSSS);
        rbSM = findViewById(R.id.rbSMS);
        search = findViewById(R.id.btnSearchThisArtNegS);
        qtaDoc = findViewById(R.id.txtQtaDocNegS);
        txtCodArt = findViewById(R.id.txtShowCodArtNegS);
        txtDesc = findViewById(R.id.txtShowDescNegS);
        barcodeText = findViewById(R.id.txtInsEANNegS);
        insNColliSpunta = findViewById(R.id.insNColliSpuntaNegS);
        insQtaSpunta = findViewById(R.id.insQtaSpuntaNegS);
        btnNextArt = findViewById(R.id.btnNextArtNegS);
        btnBackArt = findViewById(R.id.btnBackArtNegS);
        lblColli = findViewById(R.id.lblNCNegS);
        lblQta = findViewById(R.id.lblQXCNegS);
        txtEsSp = findViewById(R.id.txtEsSpNegS);
        txtPrzArt = findViewById(R.id.txtPrzArtNegS);
        txtQtaLP = findViewById(R.id.txtQtaLPNegS);
        txtOrdF = findViewById(R.id.txtOrdFNegS);
        txtOrdC = findViewById(R.id.txtOrdCNegS);
        pbSearchArt = findViewById(R.id.pbSearchArtNegS);
        fineSpunta = findViewById(R.id.btnFineSpuntaS);

        rbSS.setChecked(true);

        pbSearchArt.setVisibility(View.GONE);
        btnBackArt.setVisibility(View.GONE);
        btnNextArt.setVisibility(View.GONE);
        lblQta.setVisibility(View.GONE);
        lblColli.setVisibility(View.GONE);
        insNColliSpunta.setVisibility(View.GONE);
        insNColliSpunta.setEnabled(false);
        insQtaSpunta.setVisibility(View.GONE);
        insQtaSpunta.setEnabled(false);

        barcodeText.setFocusableInTouchMode(true);
        barcodeText.requestFocus();
        barcodeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        chkEtic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        rbSS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(rbSS.isChecked()){
                rbSM.setChecked(false);
                tipoSparata = 0;
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                fineSpunta.setVisibility(View.VISIBLE);
                insQtaSpunta.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.VISIBLE);
            }else{
                rbSM.setChecked(true);
                tipoSparata = 1;
                lblColli.setVisibility(View.VISIBLE);
                fineSpunta.setVisibility(View.GONE);
                insQtaSpunta.setVisibility(View.VISIBLE);
                insNColliSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSS.setVisibility(View.GONE);
            }
        });
        rbSM.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(rbSM.isChecked()){
                rbSS.setChecked(false);
                tipoSparata = 1;
                lblColli.setVisibility(View.VISIBLE);
                insQtaSpunta.setVisibility(View.VISIBLE);
                insNColliSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                fineSpunta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.GONE);
            }else{
                rbSS.setChecked(true);
                tipoSparata = 0;
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                fineSpunta.setVisibility(View.VISIBLE);
                lblColli.setVisibility(View.GONE);
                insQtaSpunta.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insQtaSS.setVisibility(View.VISIBLE);
            }
        });
        barcodeText.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = barcodeText.getText().toString();
                    if(!findThis.equals("")){
                        IniziaSpuntaNegSmart.FindArt cercaArt = new IniziaSpuntaNegSmart.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });
        insNColliSpunta.setOnFocusChangeListener((v, hasFocus) -> insNColliSpunta.setSelectAllOnFocus(true));
        insQtaSpunta.setOnFocusChangeListener((v, hasFocus) -> insQtaSpunta.setSelectAllOnFocus(true));
        btnNextArt.setOnClickListener(v -> {
            if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSpunta.setEnabled(true);
            }else if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                Integer qtaSpuntata;
                Date date = new Date();   // given date
                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                calendar.setTime(date);   // assigns calendar to given date
                int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                int minutes = calendar.get(Calendar.MINUTE);
                String timing = hours + ":" + minutes;
                if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                    qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                }else{
                    qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                }
                Integer qtaEtic = qtaSpuntata;
                Integer lastIndex = -1;
                for(int i=0; i<codici.size(); i++){
                    if(codici.get(i).equals(txtCodArt.getText().toString()) && numero.get(i).equals(numDoc.get(i)) && qtaSpuntata>0){
                        lastIndex = i;
                        if(Integer.parseInt(qtaDaScalare.get(i))>qtaSpuntata && qtaSpuntata>0){
                            Integer qtaRimasta = Integer.parseInt(qtaDaScalare.get(i))-qtaSpuntata;
                            Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + qtaSpuntata;
                            qtaSpunta.set(i, qtaParzialeSpunta.toString());
                            qtaSpuntata = 0;
                            qtaDaScalare.set(i, qtaRimasta.toString());
                            alias.set(i,barcodeText.getText().toString());
                            descrizioni.set(i,txtDesc.getText().toString());
                            timeSparata.set(i, timing);
                        }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                        }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                            alias.set(i,barcodeText.getText().toString());
                            descrizioni.set(i,txtDesc.getText().toString());
                            qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                            Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                            qtaDaScalare.set(i, "0");
                            qtaSpunta.set(i, qtaParzialeSpunta.toString());
                            timeSparata.set(i, timing);
                        }
                    }
                }
                if(qtaSpuntata>0 && lastIndex != -1){
                    alias.set(lastIndex,barcodeText.getText().toString());
                    descrizioni.set(lastIndex,txtDesc.getText().toString());
                    Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                    qtaSpunta.set(lastIndex, qtaUltima.toString());
                    timeSparata.set(lastIndex, timing);
                }else if(qtaSpuntata>0 && lastIndex == -1){
                    codici.add(txtCodArt.getText().toString());
                    alias.add(txtCodArt.getText().toString());
                    qtaSpunta.add(qtaSpuntata.toString());
                    descrizioni.add(txtDesc.getText().toString());
                    qtaDocum.add("0");
                    qtaDaScalare.add("0");
                    numDoc.add("");
                    numero.add("");
                    timeSparata.add(timing);
                }
                int qtaS = 0;
                int qtaD = 0;
                for(int z=0; z<codici.size(); z++){
                    if(txtCodArt.getText().toString().equals(codici.get(z))){
                        qtaS = qtaS + Integer.parseInt(qtaSpunta.get(z));
                        qtaD = qtaD + Integer.parseInt(qtaDocum.get(z));
                    }
                }
                ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                if(qtaS < qtaD){
                    back.setBackgroundColor(Color.RED);
                }else if(qtaS > qtaD){
                    back.setBackgroundColor(Color.YELLOW);
                }else{
                    back.setBackgroundColor(Color.GREEN);
                }
                if(chkEtic.isChecked()){
                    if(printer.equals("ZEBRA")){
                        BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, qtaEtic);
                        print.main();
                    }else{
                        tipoEt = spinner.getSelectedItem().toString();
                        codArt = txtCodArt.getText().toString();
                        desc = txtDesc.getText().toString();
                        PV =  txtPrzArt.getText().toString();
                        PP = "";
                        eanP = ean;
                        qtaP = qtaEtic;
                        connect();
                    }
                }
                nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                insNColliSpunta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                insQtaSpunta.setEnabled(false);
                barcodeText.setEnabled(true);
                barcodeText.setText("");
                insQtaSpunta.setText("1");
                insNColliSpunta.setText("1");
                txtQtaLP.setText("");
                txtOrdC.setText("");
                txtOrdF.setText("");
                txtEsSp.setText("");
                txtCodArt.setText("");
                txtDesc.setText("");
                qtaDoc.setText("");
                txtPrzArt.setText("");
                salvaStato();
                barcodeText.setFocusableInTouchMode(true);
                barcodeText.requestFocus();
            }
        });
        btnBackArt.setOnClickListener(v -> {
            if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(true);
            }else if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insNColliSpunta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                barcodeText.setEnabled(true);
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
            }else if(!insQtaSpunta.isEnabled() && insQtaSpunta.getVisibility() == View.VISIBLE){
                insQtaSpunta.setEnabled(true);
            }
        });
        fineSpunta.setOnClickListener(v -> {
            salvaChiudi("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
        });
        search.setOnClickListener(v -> {
            findThis = barcodeText.getText().toString();
            IniziaSpuntaNegSmart.FindArt cercaArt = new IniziaSpuntaNegSmart.FindArt();
            cercaArt.execute("");
        });

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = findViewById(R.id.surfaceView2);
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(IniziaSpuntaNegSmart.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(IniziaSpuntaNegSmart.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {

                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);

                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            String desc1, desc2;
            if(desc.length() > 28){
                desc1 = desc.substring(0,23);
                desc2 = desc.substring(23);
            }else{
                desc1 = desc;
                desc2 = "";
            }
            String etichetta = "";
            for(int i=0; i<qtaP; i++){
                switch (tipoEt){
                    case "Etichetta piccola":
                        etichetta = "^XA\n" +
                                "^FW^FO95,1^AR,75,75^FD"+PV+"^FS\n" +
                                "^FW^FO1,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO1,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FO80,140^BY1\n" +
                                "^BC,55,Y,N,N\n" +
                                "^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO190,180^AR,100,100^FD"+PV+"^FS\n" +
                                "^FO140,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO100,100^AR,10,10^FD"+codArt+"^FS\n" +
                                "^FO50,100^BY2\n" +
                                "^BC,50,Y,N,N\n" +
                                "^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Etichetta promo":
                        etichetta = "^XA\n" +
                                "^FW^FO140,1^AR,60,60^FD"+PP+"^FS\n" +
                                "^FW^FO8,1^AR,5,5^FD"+PV+"^FS\n" +
                                "^FW^FO8,30^AR,5,5^FDSC% 20%^FS\n" +
                                "^FW^FO8,60^AR,5,5^FD"+desc1+"^FS\n" +
                                "^FW^FO8,90^AR,5,5^FD"+desc2+"^FS\n" +
                                "^FW^FO100,130^AR,5,5^FD"+codArt+"^FS\n" +
                                "^FW^FO75,160^AR,5,5^FD"+eanP+"^FS\n" +
                                "^XZ\n";
                        break;
                    case "Frontalino promo":
                        etichetta = "^XA\n" +
                                "^FWR" +
                                "^FO195,110^AR,100,100^FDOFFERTA^FS\n" +
                                "^FO165,5^AR,40,40^FD"+desc+"^FS\n" +
                                "^FO120,25^AR,10,10^FD"+PP+" E.^FS\n" +
                                "^FO90,25^AR,10,10^FDSC. 29,77%^FS\n" +
                                "^FO70,210^AR,100,100^FD"+PV+" E.^FS\n" +
                                "^FO45,180^BY2\n" +
                                "^AR,10,10^FD"+eanP+"^FS\n" +
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

    private void alertDisplayer2(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer3("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer qtaSpuntata;
                    Date date = new Date();   // given date
                    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                    calendar.setTime(date);   // assigns calendar to given date
                    int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                    int minutes = calendar.get(Calendar.MINUTE);
                    String timing = hours + ":" + minutes;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
                    Integer qtaEtic = qtaSpuntata;
                    Integer lastIndex = -1;
                    for(int i=0; i<codici.size(); i++){
                        if(codici.get(i).equals(txtCodArt.getText().toString()) && numero.get(i).equals(numDoc.get(i)) && qtaSpuntata>0){
                            lastIndex = i;
                            if(Integer.parseInt(qtaDaScalare.get(i))>qtaSpuntata && qtaSpuntata>0){
                                Integer qtaRimasta = Integer.parseInt(qtaDaScalare.get(i))-qtaSpuntata;
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + qtaSpuntata;
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                qtaSpuntata = 0;
                                qtaDaScalare.set(i, qtaRimasta.toString());
                                alias.set(i,barcodeText.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                timeSparata.set(i, timing);
                            }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                            }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                alias.set(i,barcodeText.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                qtaDaScalare.set(i, "0");
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                timeSparata.set(i, timing);
                            }
                        }
                    }
                    if(qtaSpuntata>0 && lastIndex != -1){
                        alias.set(lastIndex,barcodeText.getText().toString());
                        descrizioni.set(lastIndex,txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                        qtaSpunta.set(lastIndex, qtaUltima.toString());
                        timeSparata.set(lastIndex, timing);
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        codici.add(txtCodArt.getText().toString());
                        alias.add(barcodeText.getText().toString());
                        qtaSpunta.add(qtaSpuntata.toString());
                        descrizioni.add(txtDesc.getText().toString());
                        qtaDocum.add("0");
                        qtaDaScalare.add("0");
                        numDoc.add("");
                        numero.add("");
                        timeSparata.add(timing);
                    }
                    int qtaS = 0;
                    int qtaD = 0;
                    for(int z=0; z<codici.size(); z++){
                        if(txtCodArt.getText().toString().equals(codici.get(z))){
                            qtaS = qtaS + Integer.parseInt(qtaSpunta.get(z));
                            qtaD = qtaD + Integer.parseInt(qtaDocum.get(z));
                        }
                    }
                    ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                    if(qtaS < qtaD){
                        back.setBackgroundColor(Color.RED);
                    }else if(qtaS > qtaD){
                        back.setBackgroundColor(Color.YELLOW);
                    }else{
                        back.setBackgroundColor(Color.GREEN);
                    }
                    if(chkEtic.isChecked()){
                        if(printer.equals("ZEBRA")){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, qtaEtic);
                            print.main();
                        }else{
                            tipoEt = spinner.getSelectedItem().toString();
                            codArt = txtCodArt.getText().toString();
                            desc = txtDesc.getText().toString();
                            PV =  txtPrzArt.getText().toString();
                            PP = "";
                            eanP = ean;
                            qtaP = qtaEtic;
                            connect();
                        }
                    }
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    salvaStato();
                    dialog.cancel();
                    barcodeText.setFocusableInTouchMode(true);
                    barcodeText.requestFocus();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer qtaSpuntata;
                    Date date = new Date();   // given date
                    Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                    calendar.setTime(date);   // assigns calendar to given date
                    int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                    int minutes = calendar.get(Calendar.MINUTE);
                    String timing = hours + ":" + minutes;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
                    Integer qtaEtic = qtaSpuntata;
                    Integer lastIndex = -1;
                    for(int i=0; i<codici.size(); i++){
                        if(codici.get(i).equals(txtCodArt.getText().toString()) && numero.get(i).equals(numDoc.get(i)) && qtaSpuntata>0){
                            lastIndex = i;
                            if(Integer.parseInt(qtaDaScalare.get(i))>qtaSpuntata && qtaSpuntata>0){
                                Integer qtaRimasta = Integer.parseInt(qtaDaScalare.get(i))-qtaSpuntata;
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + qtaSpuntata;
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                qtaSpuntata = 0;
                                qtaDaScalare.set(i, qtaRimasta.toString());
                                alias.set(i,barcodeText.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                timeSparata.set(i, timing);
                            }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                            }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                alias.set(i,barcodeText.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                qtaDaScalare.set(i, "0");
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                timeSparata.set(i, timing);
                            }
                        }
                    }
                    if(qtaSpuntata>0 && lastIndex != -1){
                        alias.set(lastIndex,barcodeText.getText().toString());
                        descrizioni.set(lastIndex,txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                        qtaSpunta.set(lastIndex, qtaUltima.toString());
                        timeSparata.set(lastIndex, timing);
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        codici.add(txtCodArt.getText().toString());
                        alias.add(barcodeText.getText().toString());
                        qtaSpunta.add(qtaSpuntata.toString());
                        descrizioni.add(txtDesc.getText().toString());
                        qtaDocum.add("0");
                        numero.add("");
                        qtaDaScalare.add("0");
                        numDoc.add("");
                        timeSparata.add(timing);
                    }
                    int qtaS = 0;
                    int qtaD = 0;
                    for(int z=0; z<codici.size(); z++){
                        if(txtCodArt.getText().toString().equals(codici.get(z))){
                            qtaS = qtaS + Integer.parseInt(qtaSpunta.get(z));
                            qtaD = qtaD + Integer.parseInt(qtaDocum.get(z));
                        }
                    }
                    ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                    if(qtaS < qtaD){
                        back.setBackgroundColor(Color.RED);
                    }else if(qtaS > qtaD){
                        back.setBackgroundColor(Color.YELLOW);
                    }else{
                        back.setBackgroundColor(Color.GREEN);
                    }
                    if(chkEtic.isChecked()){
                        if(printer.equals("ZEBRA")){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, qtaEtic);
                            print.main();
                        }else{
                            tipoEt = spinner.getSelectedItem().toString();
                            codArt = txtCodArt.getText().toString();
                            desc = txtDesc.getText().toString();
                            PV =  txtPrzArt.getText().toString();
                            PP = "";
                            eanP = ean;
                            qtaP = qtaEtic;
                            connect();
                        }
                    }
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaSpunta.setText("1");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    salvaStato();
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpuntaNegSmart.this, ReviewSpuntaNeg.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putExtra("docsName", docsName);
                    review.putStringArrayListExtra("qtaDoc", qtaDocum);
                    review.putStringArrayListExtra("qtaSpunta", qtaSpunta);
                    review.putStringArrayListExtra("nDoc", numero);
                    review.putExtra("nColli", nColli);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    txtCodArt.setText("");
                    txtDesc.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    if(rbSS.isChecked()){
                        barcodeText.setEnabled(false);
                        Date date = new Date();   // given date
                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                        int minutes = calendar.get(Calendar.MINUTE);
                        String timing = hours + ":" + minutes;
                        Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                        Integer lastIndex = -1;
                        for(int i=0; i<codici.size(); i++){
                            if(codici.get(i).equals(txtCodArt.getText().toString()) && numero.get(i).equals(numDoc.get(i)) && qtaSpuntata>0){
                                lastIndex = i;
                                if(Integer.parseInt(qtaDaScalare.get(i))>qtaSpuntata && qtaSpuntata>0){
                                    Integer qtaRimasta = Integer.parseInt(qtaDaScalare.get(i))-qtaSpuntata;
                                    Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + qtaSpuntata;
                                    qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                    qtaSpuntata = 0;
                                    qtaDaScalare.set(i, qtaRimasta.toString());
                                    alias.set(i,barcodeText.getText().toString());
                                    descrizioni.set(i,txtDesc.getText().toString());
                                    timeSparata.set(i, timing);
                                }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                                }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                    alias.set(i,barcodeText.getText().toString());
                                    descrizioni.set(i,txtDesc.getText().toString());
                                    qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                    Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                    qtaDaScalare.set(i, "0");
                                    qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                    timeSparata.set(i, timing);
                                }
                            }
                        }
                        if(qtaSpuntata>0 && lastIndex != -1){
                            alias.set(lastIndex,barcodeText.getText().toString());
                            descrizioni.set(lastIndex,txtDesc.getText().toString());
                            Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                            qtaSpunta.set(lastIndex, qtaUltima.toString());
                            timeSparata.set(lastIndex, timing);
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            codici.add(txtCodArt.getText().toString());
                            alias.add(barcodeText.getText().toString());
                            qtaSpunta.add(qtaSpuntata.toString());
                            descrizioni.add(txtDesc.getText().toString());
                            qtaDocum.add("0");
                            qtaDaScalare.add("0");
                            numDoc.add("");
                            numero.add("");
                            timeSparata.add(timing);
                        }
                        int qtaS = 0;
                        int qtaD = 0;
                        for(int z=0; z<codici.size(); z++){
                            if(txtCodArt.getText().toString().equals(codici.get(z))){
                                qtaS = qtaS + Integer.parseInt(qtaSpunta.get(z));
                                qtaD = qtaD + Integer.parseInt(qtaDocum.get(z));
                            }
                        }
                        ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                        if(qtaS < qtaD){
                            back.setBackgroundColor(Color.RED);
                        }else if(qtaS > qtaD){
                            back.setBackgroundColor(Color.YELLOW);
                        }else{
                            back.setBackgroundColor(Color.GREEN);
                        }
                        if(chkEtic.isChecked()){
                            if(printer.equals("ZEBRA")){
                                BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                        txtPrzArt.getText().toString().substring(1), txtPrzArt.getText().toString().substring(1), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, Integer.parseInt(insQtaSS.getText().toString()));
                                print.main();
                            }else{
                                tipoEt = spinner.getSelectedItem().toString();
                                codArt = txtCodArt.getText().toString();
                                desc = txtDesc.getText().toString();
                                PV =  txtPrzArt.getText().toString();
                                PP = "";
                                eanP = ean;
                                qtaP = Integer.parseInt(insQtaSS.getText().toString());
                                connect();
                            }
                        }
                        barcodeText.setEnabled(true);
                        barcodeText.setText("");
                        barcodeText.setFocusableInTouchMode(true);
                        barcodeText.requestFocus();
                        insQtaSS.setText("1");
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            play.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        barcodeText.setEnabled(false);
                        insNColliSpunta.setVisibility(View.VISIBLE);
                        insNColliSpunta.setEnabled(true);
                        lblColli.setVisibility(View.VISIBLE);
                    }
                    salvaStato();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaNegSmart.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText note = new EditText(context);
        note.setHint("Note");
        layout.addView(note);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(note.getText().toString().equals("")){
                note.setHintTextColor(Color.RED);
                alertArt("Error!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                dialog.cancel();
                Date date = new Date();   // given date
                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                calendar.setTime(date);   // assigns calendar to given date
                int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                int minutes = calendar.get(Calendar.MINUTE);
                String timing = hours + ":" + minutes;
                txtCodArt.setText(barcodeText.getText().toString());
                txtEsSp.setText("0");
                txtPrzArt.setText("€ " + "N/A");
                txtOrdF.setText("0");
                txtOrdC.setText("0");
                txtQtaLP.setText("0");
                txtDesc.setText(note.getText().toString());
                barcodeText.setEnabled(false);
                if(rbSM.isChecked()){
                    btnBackArt.setVisibility(View.VISIBLE);
                    btnNextArt.setVisibility(View.VISIBLE);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setEnabled(true);
                    lblColli.setVisibility(View.VISIBLE);
                }else{
                    codici.add(txtCodArt.getText().toString());
                    alias.add(barcodeText.getText().toString());
                    qtaSpunta.add(insQtaSS.getText().toString());
                    descrizioni.add(txtDesc.getText().toString());
                    qtaDocum.add("0");
                    qtaDaScalare.add("0");
                    numDoc.add("");
                    numero.add("");
                    timeSparata.add(timing);
                }
                barcodeText.setEnabled(true);
                barcodeText.setText("");
                insQtaSS.setText("1");
                barcodeText.setFocusableInTouchMode(true);
                barcodeText.requestFocus();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
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

            String przV = txtPrzArt.getText().toString().substring(2).replace(".", ",");
            String przP = txtPrzArt.getText().toString().substring(2).replace(".", ",");

            String addZ = przV.substring(przV.indexOf(","));
            if(addZ.length() < 3){
                przV = przV + "0";
            }

            String cpclData = "";
            switch(spinner.getSelectedItem().toString()){
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
                            "T270 4 1 580 170 " + przV +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 100 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 2 70 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            alias +
                            "\n" +
                            "VBARCODE 128 1 1 40 5 350  " + ean +
                            "\n" + "\n" +
                            "T270 5 0 0 150 " + ean +
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Etichetta promo":
                    Double scp = 100 - ((Double.parseDouble(przP.replace(",",".")) * 100)/Double.parseDouble(przV.replace(",",".")));
                    String sc = scp.toString().substring(0, scp.toString().indexOf("."));
                    cpclData = "! 0 0 0 285 1" +
                            "TEXT 5 0 40 25 " + przV +
                            "\n" + "LINE 8 8 150 8 1 " + "\n" +
                            "TEXT 4 0 170 25 " + round(Double.parseDouble(przP),2) +
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
                            "\n" + "\n" + "PRINT" + "\n" + "\n";
                    break;
                case "Frontalino promo":
                    cpclData = "! 80 0 0 720 1" +
                            "\n" +
                            "T270 4 1 240 150  OFFERTA " +
                            "\n" + "\n" + "\n" + "\n" +
                            "T270 0 3 150 40 " + desc1 +
                            "\n" + "\n" +
                            "T270 0 3 130 40 " +
                            desc2 +
                            "\n" + "\n" +
                            "T270 0 5 85 10 " +
                            "\n" + "\n" +
                            "T270 0 5 85 40 " + przV +
                            "\n" + "\n" +
                            "LINE 70 5 70 150  1 " +
                            "\n" + "\n" +
                            "T270 4 1 120 260 " + round(Double.parseDouble(przP),2) +
                            "\n" + "\n" +
                            "T270 0 2 0 70 " + txtCodArt.getText().toString() +
                            "\n" + "\n" +
                            "T270 5 0 20 150 " + ean +
                            "\n" + "\n" +
                            "PRINT" +
                            "\n" + "\n";
                    break;
                default:
                    break;
            }

            for(int i=0; i<Integer.parseInt(insQtaSS.getText().toString()); i++){
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

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String codiceArt = null;
        String description = null;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbSearchArt.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            if(isSuccess) {
                if(rbSS.isChecked()){
                    txtCodArt.setText(codiceArt);
                    txtDesc.setText(description);
                    if(of!=null){
                        txtOrdF.setText(of.toString());
                    }else{
                        txtOrdF.setText("0");
                    }
                    if(oc!=null){
                        txtOrdC.setText(oc.toString());
                    }else{
                        txtOrdC.setText("0");
                    }
                    if(esistenza!=null){
                        txtEsSp.setText(esistenza.toString());
                    }else{
                        txtEsSp.setText("0");
                    }
                    if(prz!=null){
                        txtPrzArt.setText("€ " + prz.toString());
                    }else{
                        txtPrzArt.setText("€ N/A");
                    }
                    Boolean presente = false;
                    Integer totQta = 0;
                    for(int j=0; j<codici.size(); j++){
                        if(codici.get(j).equals(codiceArt)){
                            txtQtaLP.setText(qtaSpunta.get(j));
                        }
                    }
                    for(int i=0; i<codArticolo.size(); i++){
                        if(codArticolo.get(i).equals(codiceArt)){
                            totQta = totQta + Integer.parseInt(qtaDocum.get(i));
                            presente = true;
                        }
                    }
                    String sqta = totQta.toString();
                    qtaDoc.setText(sqta);
                    if(presente){
                        barcodeText.setEnabled(false);
                        Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                        Integer lastIndex = -1;
                        Date date = new Date();   // given date
                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                        int minutes = calendar.get(Calendar.MINUTE);
                        String timing = hours + ":" + minutes;
                        for(int i=0; i<codici.size(); i++){
                            if(codici.get(i).equals(txtCodArt.getText().toString()) && numero.get(i).equals(numDoc.get(i)) && qtaSpuntata>0){
                                lastIndex = i;
                                if(Integer.parseInt(qtaDaScalare.get(i))>qtaSpuntata && qtaSpuntata>0){
                                    Integer qtaRimasta = Integer.parseInt(qtaDaScalare.get(i))-qtaSpuntata;
                                    Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + qtaSpuntata;
                                    qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                    qtaSpuntata = 0;
                                    qtaDaScalare.set(i, qtaRimasta.toString());
                                    alias.set(i,barcodeText.getText().toString());
                                    descrizioni.set(i,txtDesc.getText().toString());
                                    timeSparata.set(i, timing);
                                }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                                }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                    alias.set(i,barcodeText.getText().toString());
                                    descrizioni.set(i,txtDesc.getText().toString());
                                    qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                    Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                    qtaDaScalare.set(i, "0");
                                    qtaSpunta.set(i, qtaParzialeSpunta.toString());
                                    timeSparata.set(i, timing);
                                }
                            }
                        }
                        if(qtaSpuntata>0 && lastIndex != -1){
                            alias.set(lastIndex,barcodeText.getText().toString());
                            descrizioni.set(lastIndex,txtDesc.getText().toString());
                            Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                            qtaSpunta.set(lastIndex, qtaUltima.toString());
                            timeSparata.set(lastIndex, timing);
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            codici.add(txtCodArt.getText().toString());
                            alias.add(barcodeText.getText().toString());
                            qtaSpunta.add(qtaSpuntata.toString());
                            descrizioni.add(txtDesc.getText().toString());
                            qtaDocum.add("0");
                            numDoc.add("");
                            qtaDaScalare.add("0");
                            numero.add("");
                            timeSparata.add(timing);
                        }
                        int qtaS = 0;
                        int qtaD = 0;
                        for(int z=0; z<codici.size(); z++){
                            if(txtCodArt.getText().toString().equals(codici.get(z))){
                                qtaS = qtaS + Integer.parseInt(qtaSpunta.get(z));
                                qtaD = qtaD + Integer.parseInt(qtaDocum.get(z));
                            }
                        }
                        ConstraintLayout back = findViewById(R.id.laySpuntaNeg);
                        if(qtaS < qtaD){
                            back.setBackgroundColor(Color.RED);
                        }else if(qtaS > qtaD){
                            back.setBackgroundColor(Color.YELLOW);
                        }else{
                            back.setBackgroundColor(Color.GREEN);
                        }
                        if(chkEtic.isChecked()){
                            if(printer.equals("ZEBRA")){
                                stampaZebra();
                            }else{
                                tipoEt = spinner.getSelectedItem().toString();
                                codArt = txtCodArt.getText().toString();
                                desc = txtDesc.getText().toString();
                                PV =  txtPrzArt.getText().toString();
                                PP = "";
                                eanP = ean;
                                qtaP = Integer.parseInt(insQtaSS.getText().toString());
                                connect();
                            }
                        }
                        barcodeText.setEnabled(true);
                        barcodeText.setText("");
                        insQtaSS.setText("1");
                        barcodeText.setFocusableInTouchMode(true);
                        barcodeText.requestFocus();
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            play.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                    }
                }else{
                    btnBackArt.setVisibility(View.VISIBLE);
                    btnNextArt.setVisibility(View.VISIBLE);
                    txtCodArt.setText(codiceArt);
                    txtDesc.setText(description);
                    if(of!=null){
                        txtOrdF.setText(of.toString());
                    }else{
                        txtOrdF.setText("0");
                    }
                    if(oc!=null){
                        txtOrdC.setText(oc.toString());
                    }else{
                        txtOrdC.setText("0");
                    }
                    if(esistenza!=null){
                        txtEsSp.setText(esistenza.toString());
                    }else{
                        txtEsSp.setText("0");
                    }
                    if(prz!=null){
                        txtPrzArt.setText("€ " + prz.toString());
                    }else{
                        txtPrzArt.setText("€ N/A");
                    }
                    Boolean presente = false;
                    Integer totQta = 0;
                    for(int j=0; j<codici.size(); j++){
                        if(codici.get(j).equals(codiceArt)){
                            txtQtaLP.setText(qtaSpunta.get(j));
                        }
                    }
                    for(int i=0; i<codArticolo.size(); i++){
                        if(codArticolo.get(i).equals(codiceArt)){
                            totQta = totQta + Integer.parseInt(qtaDocum.get(i));
                            presente = true;
                        }
                    }
                    String sqta = totQta.toString();
                    qtaDoc.setText(sqta);
                    if(presente){
                        barcodeText.setEnabled(false);
                        insNColliSpunta.setVisibility(View.VISIBLE);
                        insNColliSpunta.setEnabled(true);
                        lblColli.setVisibility(View.VISIBLE);
                    }else{
                        articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                    }
                }
            }else{
                alertArt("Errore!","Articolo non presente nel database, inserisci una nota");

            }
            if(giaPremuto == 1){
                giaPremuto = 0;
            }
            salvaStato();
            pbSearchArt.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select nome, articolo.descrizione, " +
                            "(select cast (ProgressivoArticolo.esistenza as int) as esistenza from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = "+mag+") as esistenza, \n" +
                            "(select cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = "+mag+") as OrdinatoFornitoreArticoloXMagazzino,\n" +
                            "(select cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a > GETDATE() and MetaMagazzino = "+mag+") as OrdinatoClienteArticoloXMagazzino, \n" +
                            "(select cast (ArticoloXListino.prezzo as decimal (10,2)) from articoloxlistino where idArticolo = articolo.id and idListino = "+listino+") as prezzo, (select top 1 alias.codice from alias where idArticolo = articolo.id) as ean, " +
                            "(select idListino from articoloxlistino where idArticolo = articolo.id and idListino = "+listino+") as idListino " +
                            "from articolo left join alias on (alias.idArticolo = articolo.id) " +
                            "where codice = '"+ findThis +"' or nome = '"+ findThis +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int ok = 0;
                    int i = 0;
                    while(res.next()) {
                        if(res.getInt("idListino") == listino && ok == 0){
                            ok = 1;
                            prz = res.getDouble("prezzo");
                            esistenza = res.getInt("esistenza");
                            of = res.getInt("OrdinatoFornitoreArticoloXMagazzino");
                            oc = res.getInt("OrdinatoClienteArticoloXMagazzino");
                        }
                        if(i==0){
                            if(res.getString("ean") != null){
                                ean = res.getString("ean");
                            }else{
                                ean = "";
                            }
                        }
                        codiceArt = res.getString("nome");
                        description = res.getString("descrizione");
                        i++;
                    }
                    if (codiceArt != null) {
                        //z = "Ordine trovato";
                        if(ok == 0){
                            prz = 0.00;
                            esistenza = 0;
                            of = 0;
                            oc = 0;
                        }
                        isSuccess = true;
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
}