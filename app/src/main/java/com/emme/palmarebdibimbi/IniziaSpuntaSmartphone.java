package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class IniziaSpuntaSmartphone extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    EditText barcodeText;
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
    ArrayList<String> ubicazioni = new ArrayList<>();
    ArrayList<String> subicazioni = new ArrayList<>();
    ArrayList<String> numero = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> note = new ArrayList<>();
    String docsName ="";
    String fornitore = "";
    ProgressBar pbSearchArt;
    String findThis, ubiDef, subiDef;
    Double prz;
    Integer mag, listino, esistenza, of, oc;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, lblUbic, lblSubic, txtPrzArt, txtEsSp, txtQtaLP, txtUbiDef, txtOrdF, txtOrdC, txtUbiRead, txtSubiDef, txtSubiRead;
    Button search, btnNextArt, btnBackArt;
    EditText insNColliSpunta, insQtaSpunta, insUbicSpunta, insSubicSpunta;
    Context context;
    int giaPremuto = 0;
    int totColli = 0;
    String tipoDoc ="";
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
            JSONArray jsonArrayUbi = new JSONArray(preferences.getString("ubiInt", "[]"));
            JSONArray jsonArraySubi = new JSONArray(preferences.getString("subiInt", "[]"));
            JSONArray jsonArrayNumDoc = new JSONArray(preferences.getString("numInt", "[]"));
            JSONArray jsonArrayNote = new JSONArray(preferences.getString("note", "[]"));
            for (int i = 0; i < jsonArrayCod.length(); i++) {
                codici.add(jsonArrayCod.getString(i));
                codArticolo.add(jsonArrayCod.getString(i));
                descrizioni.add(jsonArrayDesc.getString(i));
                qta.add(jsonArrayQtaDoc.getString(i));
                qtaDocum.add(jsonArrayQtaDoc.getString(i));
                qtaDaScalare.add(jsonArrayQtaRes.getString(i));
                qtaSpunta.add(jsonArrayQta.getString(i));
                alias.add(jsonArrayAlias.getString(i));
                ubicazioni.add(jsonArrayUbi.getString(i));
                subicazioni.add(jsonArraySubi.getString(i));
                numDoc.add(jsonArrayNumDoc.getString(i));
                numero.add(jsonArrayNumDoc.getString(i));
                note.add(jsonArrayNote.getString(i));
            }
            docsName = preferences.getString("nameInt","");
            mag = preferences.getInt("magInt", 1);
            listino = preferences.getInt("listInt",6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEditTextMaxLength(EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
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
        JSONArray jsonArrayUbi = new JSONArray();
        JSONArray jsonArraySubi = new JSONArray();
        JSONArray jsonArrayNumDoc = new JSONArray();
        JSONArray jsonArrayNote = new JSONArray();
        for(int i=0; i<codici.size(); i++){
            jsonArrayCod.put(codici.get(i));
            jsonArrayDesc.put(descrizioni.get(i));
            jsonArrayQtaDoc.put(qtaDocum.get(i));
            jsonArrayQta.put(qtaSpunta.get(i));
            jsonArrayQtaRes.put(qtaDaScalare.get(i));
            jsonArrayAlias.put(alias.get(i));
            jsonArrayUbi.put(ubicazioni.get(i));
            jsonArraySubi.put(subicazioni.get(i));
            jsonArrayNumDoc.put(numero.get(i));
            jsonArrayNote.put(note.get(i));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("codInt", jsonArrayCod.toString());
        editor.putInt("magInt", mag);
        editor.putInt("listInt", listino);
        editor.putString("nameInt", docsName);
        editor.putString("note", jsonArrayNote.toString());
        editor.putString("descInt", jsonArrayDesc.toString());
        editor.putString("qtaDocInt", jsonArrayQtaDoc.toString());
        editor.putString("qtaResInt", jsonArrayQtaRes.toString());
        editor.putString("qtaInt", jsonArrayQta.toString());
        editor.putString("eanInt", jsonArrayAlias.toString());
        editor.putString("ubiInt", jsonArrayUbi.toString());
        editor.putString("subiInt", jsonArraySubi.toString());
        editor.putString("numInt", jsonArrayNumDoc.toString());
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
        return false;
    }

    @Override
    public void onBackPressed() {
        alertDisplayer("Attenzione!","Sei sicuro di voler abbandonare la pagina di spunta? Verrai riportato alla home!");
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaSpuntaSmartphone.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void changeUbic(String title,String message, int sOrU){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    if(sOrU==1){
                        insUbicSpunta.setEnabled(false);
                        insSubicSpunta.setVisibility(View.VISIBLE);
                        lblSubic.setVisibility(View.VISIBLE);
                        insSubicSpunta.setEnabled(true);
                    }else{
                        insSubicSpunta.setEnabled(false);
                    }
                })
                .setPositiveButton("Si", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_spunta_smartphone);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int rip = 0;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            tipoDoc = extras.getString("tipoDoc");
            rip = extras.getInt("rip");
            fornitore = extras.getString("fornitore");
        }

        connectionClass = new ConnectionClass();

        context = this;

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
                ubicazioni.add("");
                subicazioni.add("");
                qtaDocum.add(qta.get(i));
                numero.add(numDoc.get(i));
                note.add("");
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

        barcodeText = findViewById(R.id.txtInsEANS);
        search = findViewById(R.id.btnSearchThisArtS);
        qtaDoc = findViewById(R.id.txtQtaDocS);
        txtCodArt = findViewById(R.id.txtShowCodArtS);
        txtDesc = findViewById(R.id.txtShowDescS);
        insNColliSpunta = findViewById(R.id.insNColliSpuntaS);
        insQtaSpunta = findViewById(R.id.insQtaSpuntaS);
        insUbicSpunta = findViewById(R.id.insUbicSpuntaS);
        btnNextArt = findViewById(R.id.btnNextArtS);
        btnBackArt = findViewById(R.id.btnBackArtS);
        lblUbic = findViewById(R.id.lblUbicS);
        lblColli = findViewById(R.id.lblNCS);
        lblQta = findViewById(R.id.lblQXCS);
        lblSubic = findViewById(R.id.lblSubicS);
        insSubicSpunta = findViewById(R.id.insSubicSpuntaS);
        txtEsSp = findViewById(R.id.txtEsSpS);
        txtPrzArt = findViewById(R.id.txtPrzArtS);
        txtQtaLP = findViewById(R.id.txtQtaLPS);
        txtUbiDef = findViewById(R.id.txtUbiDefS);
        txtOrdF = findViewById(R.id.txtOrdFS);
        txtOrdC = findViewById(R.id.txtOrdCS);
        txtUbiRead = findViewById(R.id.txtUbiReadS);
        pbSearchArt = findViewById(R.id.pbSearchArtS);
        txtSubiDef = findViewById(R.id.txtSubiDefS);
        txtSubiRead = findViewById(R.id.txtSubiReadS);

        pbSearchArt.setVisibility(View.GONE);
        btnBackArt.setVisibility(View.GONE);
        btnNextArt.setVisibility(View.GONE);
        lblQta.setVisibility(View.GONE);
        lblColli.setVisibility(View.GONE);
        lblUbic.setVisibility(View.GONE);
        insNColliSpunta.setVisibility(View.GONE);
        insNColliSpunta.setEnabled(false);
        insQtaSpunta.setVisibility(View.GONE);
        insQtaSpunta.setEnabled(false);
        insUbicSpunta.setVisibility(View.GONE);
        insUbicSpunta.setEnabled(false);
        insSubicSpunta.setVisibility(View.GONE);
        insSubicSpunta.setEnabled(false);
        lblSubic.setVisibility(View.GONE);

        setEditTextMaxLength(insUbicSpunta,4);
        setEditTextMaxLength(insSubicSpunta,4);

        barcodeText.setFocusableInTouchMode(true);
        barcodeText.requestFocus();
        barcodeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insUbicSpunta.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insSubicSpunta.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        insQtaSpunta.setSelectAllOnFocus(true);
        insNColliSpunta.setSelectAllOnFocus(true);
        insSubicSpunta.setSelectAllOnFocus(true);
        insUbicSpunta.setSelectAllOnFocus(true);

        barcodeText.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = barcodeText.getText().toString();
                    if(!findThis.equals("")){
                        IniziaSpuntaSmartphone.FindArt cercaArt = new IniziaSpuntaSmartphone.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });
        insUbicSpunta.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && !insUbicSpunta.getText().toString().equals("")){
                changeUbic("Attenzione!", "Esiste già un ubicazione per questo articolo, modificandola verrà sostituita anche alle altre occorrenze dello stesso, modificare comunque?", 1);
            }
        });
        insSubicSpunta.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus && !insSubicSpunta.getText().toString().equals("")){
                changeUbic("Attenzione!", "Esiste già una sottoubicazione per questo articolo, modificandola verrà sostituita anche alle altre occorrenze dello stesso, modificare comunque?", 2);
            }
        });
        btnNextArt.setOnClickListener(v -> {
            if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.VISIBLE);
                lblQta.setVisibility(View.VISIBLE);
                insQtaSpunta.setEnabled(true);
                insQtaSpunta.setFocusableInTouchMode(true);
                insQtaSpunta.requestFocus();
                showSoftKeyboard(insQtaSpunta);
            }else if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insUbicSpunta.setVisibility(View.VISIBLE);
                lblUbic.setVisibility(View.VISIBLE);
                insUbicSpunta.setEnabled(true);
                insUbicSpunta.setFocusableInTouchMode(true);
                insUbicSpunta.requestFocus();
                showSoftKeyboard(insUbicSpunta);
            }else if(insUbicSpunta.isEnabled()){
                insUbicSpunta.setEnabled(false);
                insSubicSpunta.setVisibility(View.VISIBLE);
                lblSubic.setVisibility(View.VISIBLE);
                insSubicSpunta.setEnabled(true);
                insSubicSpunta.setFocusableInTouchMode(true);
                insSubicSpunta.requestFocus();
                showSoftKeyboard(insSubicSpunta);
            }else if(insSubicSpunta.isEnabled()){
                insSubicSpunta.setEnabled(false);
            }else if(!insSubicSpunta.isEnabled() && insSubicSpunta.getVisibility() == View.VISIBLE){
                alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
            }
        });
        btnBackArt.setOnClickListener(v -> {
            if(insSubicSpunta.isEnabled()){
                insSubicSpunta.setEnabled(false);
                insSubicSpunta.setVisibility(View.GONE);
                lblSubic.setVisibility(View.GONE);
                insUbicSpunta.setEnabled(true);
                insUbicSpunta.setFocusableInTouchMode(true);
                insUbicSpunta.requestFocus();
                showSoftKeyboard(insUbicSpunta);
            }else if(insUbicSpunta.isEnabled()){
                insUbicSpunta.setEnabled(false);
                insUbicSpunta.setVisibility(View.GONE);
                lblUbic.setVisibility(View.GONE);
                insQtaSpunta.setEnabled(true);
                insQtaSpunta.setFocusableInTouchMode(true);
                insQtaSpunta.requestFocus();
                showSoftKeyboard(insQtaSpunta);
            }else if(insQtaSpunta.isEnabled()){
                insQtaSpunta.setEnabled(false);
                insQtaSpunta.setVisibility(View.GONE);
                lblQta.setVisibility(View.GONE);
                insNColliSpunta.setEnabled(true);
                insNColliSpunta.setFocusableInTouchMode(true);
                insNColliSpunta.requestFocus();
                showSoftKeyboard(insNColliSpunta);
            }else if(insNColliSpunta.isEnabled()){
                insNColliSpunta.setEnabled(false);
                insNColliSpunta.setVisibility(View.GONE);
                lblColli.setVisibility(View.GONE);
                barcodeText.setEnabled(true);
                barcodeText.setFocusableInTouchMode(true);
                barcodeText.requestFocus();
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
            }else if(!insSubicSpunta.isEnabled() && insSubicSpunta.getVisibility() == View.VISIBLE){
                insSubicSpunta.setEnabled(true);
                insSubicSpunta.setFocusableInTouchMode(true);
                insSubicSpunta.requestFocus();
                showSoftKeyboard(insSubicSpunta);
            }
        });
        search.setOnClickListener(v -> {
            findThis = barcodeText.getText().toString();
            if(findThis.equals("")){
                nienteInRicerca("Error!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                IniziaSpuntaSmartphone.FindArt cercaArt = new IniziaSpuntaSmartphone.FindArt();
                cercaArt.execute("");
            }
        });

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = findViewById(R.id.surfaceView);
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
                    if (ActivityCompat.checkSelfPermission(IniziaSpuntaSmartphone.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(IniziaSpuntaSmartphone.this, new
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

    private void alertDisplayer2(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer3("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
                })
                .setNeutralButton("Inserisci nota",((dialog, which) -> {
                    inserisciNota(txtCodArt.getText().toString());
                }))
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer qtaSpuntata;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
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
                                ubicazioni.set(i,insUbicSpunta.getText().toString());
                                subicazioni.set(i,insSubicSpunta.getText().toString());
                            }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){

                            }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                alias.set(i,barcodeText.getText().toString());
                                ubicazioni.set(i,insUbicSpunta.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                subicazioni.set(i,insSubicSpunta.getText().toString());
                                qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                qtaDaScalare.set(i, "0");
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                            }
                        }else if(i == 0 && qtaSpuntata == 0){
                            for(int j = 0; j<codici.size(); j++){
                                if(codici.get(j).equals(txtCodArt.getText().toString())){
                                    ubicazioni.set(j, insUbicSpunta.getText().toString());
                                    subicazioni.set(j, insSubicSpunta.getText().toString());
                                }
                            }
                        }
                    }
                    if(qtaSpuntata>0 && lastIndex != -1){
                        alias.set(lastIndex,barcodeText.getText().toString());
                        ubicazioni.set(lastIndex,insUbicSpunta.getText().toString());
                        subicazioni.set(lastIndex,insSubicSpunta.getText().toString());
                        descrizioni.set(lastIndex,txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                        qtaSpunta.set(lastIndex, qtaUltima.toString());
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        if(note.size()==codici.size()){
                            note.add("");
                        }
                        codici.add(txtCodArt.getText().toString());
                        alias.add(txtCodArt.getText().toString());
                        qtaSpunta.add(qtaSpuntata.toString());
                        descrizioni.add(txtDesc.getText().toString());
                        qtaDocum.add("0");
                        qtaDaScalare.add("0");
                        numDoc.add("");
                        numero.add("");
                        ubicazioni.add(insUbicSpunta.getText().toString());
                        subicazioni.add(insSubicSpunta.getText().toString());
                    }
                    totColli += Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    lblUbic.setVisibility(View.GONE);
                    lblSubic.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    insUbicSpunta.setVisibility(View.GONE);
                    insUbicSpunta.setEnabled(false);
                    insSubicSpunta.setVisibility(View.GONE);
                    insSubicSpunta.setEnabled(false);
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaSpunta.setText("1");
                    insUbicSpunta.setText("");
                    insSubicSpunta.setText("");
                    insNColliSpunta.setText("1");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtUbiDef.setText("");
                    txtSubiDef.setText("");
                    txtSubiRead.setText("");
                    txtSubiDef.setText("");
                    txtUbiRead.setText("");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    dialog.cancel();
                    salvaStato();
                    barcodeText.setFocusableInTouchMode(true);
                    barcodeText.requestFocus();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer qtaSpuntata;
                    if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                    }else{
                        qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                    }
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
                                ubicazioni.set(i,insUbicSpunta.getText().toString());
                                subicazioni.set(i,insSubicSpunta.getText().toString());
                            }else if(Integer.parseInt(qtaDaScalare.get(i))==0 && qtaSpuntata>0){
                            }else if(Integer.parseInt(qtaDaScalare.get(i))<qtaSpuntata && qtaSpuntata>0){
                                alias.set(i,barcodeText.getText().toString());
                                ubicazioni.set(i,insUbicSpunta.getText().toString());
                                descrizioni.set(i,txtDesc.getText().toString());
                                subicazioni.set(i,insSubicSpunta.getText().toString());
                                qtaSpuntata = qtaSpuntata - Integer.parseInt(qtaDaScalare.get(i));
                                Integer qtaParzialeSpunta = Integer.parseInt(qtaSpunta.get(i)) + Integer.parseInt(qtaDaScalare.get(i));
                                qtaDaScalare.set(i, "0");
                                qtaSpunta.set(i, qtaParzialeSpunta.toString());
                            }
                        }else if(i == 0 && qtaSpuntata == 0){
                            for(int j = 0; j<codici.size(); j++){
                                if(codici.get(j).equals(txtCodArt.getText().toString())){
                                    ubicazioni.set(j, insUbicSpunta.getText().toString());
                                    subicazioni.set(j, insSubicSpunta.getText().toString());
                                }
                            }
                        }
                    }
                    if(qtaSpuntata>0 && lastIndex != -1){
                        alias.set(lastIndex,barcodeText.getText().toString());
                        ubicazioni.set(lastIndex,insUbicSpunta.getText().toString());
                        subicazioni.set(lastIndex,insSubicSpunta.getText().toString());
                        descrizioni.set(lastIndex,txtDesc.getText().toString());
                        Integer qtaUltima = qtaSpuntata + Integer.parseInt(qtaSpunta.get(lastIndex));
                        qtaSpunta.set(lastIndex, qtaUltima.toString());
                    }else if(qtaSpuntata>0 && lastIndex == -1){
                        if(note.size()==codici.size()){
                            note.add("");
                        }
                        codici.add(txtCodArt.getText().toString());
                        alias.add(txtCodArt.getText().toString());
                        qtaSpunta.add(qtaSpuntata.toString());
                        descrizioni.add(txtDesc.getText().toString());
                        qtaDocum.add("0");
                        qtaDaScalare.add("0");
                        numero.add("");
                        numDoc.add("");
                        ubicazioni.add(insUbicSpunta.getText().toString());
                        subicazioni.add(insSubicSpunta.getText().toString());
                    }
                    totColli += Integer.parseInt(insNColliSpunta.getText().toString());
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    lblUbic.setVisibility(View.GONE);
                    lblSubic.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    insUbicSpunta.setVisibility(View.GONE);
                    insUbicSpunta.setEnabled(false);
                    insSubicSpunta.setVisibility(View.GONE);
                    insSubicSpunta.setEnabled(false);
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaSpunta.setText("1");
                    insUbicSpunta.setText("");
                    insSubicSpunta.setText("");
                    insNColliSpunta.setText("1");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtQtaLP.setText("");
                    txtOrdC.setText("");
                    txtOrdF.setText("");
                    txtEsSp.setText("");
                    txtUbiDef.setText("");
                    txtSubiDef.setText("");
                    txtSubiRead.setText("");
                    txtUbiRead.setText("");
                    txtSubiDef.setText("");
                    qtaDoc.setText("");
                    txtPrzArt.setText("");
                    salvaStato();
                    dialog.cancel();
                    Intent review = new Intent(IniziaSpuntaSmartphone.this, ReviewSpunta.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putStringArrayListExtra("qtaDoc", qtaDocum);
                    review.putStringArrayListExtra("note", note);
                    review.putExtra("tipo", 0);
                    review.putExtra("docsName", docsName);
                    review.putStringArrayListExtra("qtaSpunta", qtaSpunta);
                    review.putStringArrayListExtra("ubicazioni", ubicazioni);
                    review.putStringArrayListExtra("subicazioni", subicazioni);
                    review.putStringArrayListExtra("nDoc", numero);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void inserisciNota(String codxNota){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setMessage("Inserisci una nota per l'articolo");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText not = new EditText(this);
        not.setHint("Note");
        layout.addView(not);

        builder.setView(layout);

        ArrayList<Integer> indexes = new ArrayList<>();
        int i;
        for(i = 0; i<codici.size(); i++){
            if(codxNota.equals(codici.get(i))){
                if (note.get(i).equals("")) {
                    indexes.add(i);
                }else{
                    not.setText(note.get(i));
                }
            }
        }

        builder.setNegativeButton("No", (dialog, which) -> {
            alertDisplayer2("Attenzione!","Ci sono altri articoli da spuntare?");
        });
        builder.setPositiveButton("Si", (dialog, which) -> {
            int j;
            for(j = 0; j<indexes.size(); j++){
                note.set(indexes.get(j), not.getText().toString());
            }
            if(j==0){
                note.add(not.getText().toString());
            }
            alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
        });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    txtCodArt.setText("");
                    txtDesc.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    barcodeText.setEnabled(false);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setEnabled(true);
                    lblColli.setVisibility(View.VISIBLE);
                    insNColliSpunta.setFocusableInTouchMode(true);
                    insNColliSpunta.requestFocus();
                    showSoftKeyboard(insNColliSpunta);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaSmartphone.this)
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
                btnBackArt.setVisibility(View.VISIBLE);
                btnNextArt.setVisibility(View.VISIBLE);
                txtCodArt.setText(barcodeText.getText().toString());
                txtEsSp.setText("0");
                txtPrzArt.setText("€ " + "N/A");
                txtOrdF.setText("0");
                txtOrdC.setText("0");
                txtQtaLP.setText("0");
                txtUbiDef.setText("N/A");
                txtUbiRead.setText("N/A");
                txtSubiDef.setText("N/A");
                txtSubiRead.setText("N/A");
                txtDesc.setText(note.getText().toString());
                barcodeText.setEnabled(false);
                insNColliSpunta.setVisibility(View.VISIBLE);
                insNColliSpunta.setEnabled(true);
                lblColli.setVisibility(View.VISIBLE);
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void nienteInRicerca(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaSmartphone.this)
                .setTitle(title)
                .setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
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

            pbSearchArt.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess) {
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
                if(ubiDef!=null){
                    txtUbiDef.setText(ubiDef);
                }else{
                    txtUbiDef.setText("N/A");
                }
                if(prz!=null){
                    txtPrzArt.setText("€ " + prz.toString());
                }else{
                    txtPrzArt.setText("€ N/A");
                }
                if(subiDef==null){
                    txtSubiDef.setText("N/A");
                }else{
                    txtSubiDef.setText(subiDef);
                }
                Boolean presente = false;
                Integer totQta = 0;
                for(int j=0; j<codici.size(); j++){
                    if(codici.get(j).equals(codiceArt)){
                        txtUbiRead.setText(ubicazioni.get(j));
                        txtSubiRead.setText(subicazioni.get(j));
                        insUbicSpunta.setText(ubicazioni.get(j));
                        insSubicSpunta.setText(subicazioni.get(j));
                        txtQtaLP.setText(qtaSpunta.get(j));
                        String ubi = ubicazioni.get(j);
                        String subi = subicazioni.get(j);
                        if (ubi.equals("")) {
                            if(!txtUbiDef.getText().toString().equals("N/A")){
                                insUbicSpunta.setText(ubiDef);
                            }
                        }
                        if (subi.equals("")) {
                            if(!txtSubiDef.getText().toString().equals("N/A")){
                                insSubicSpunta.setText(subiDef);
                            }
                        }
                        txtQtaLP.setText(qtaSpunta.get(j));
                    }
                }
                for(int i=0; i<codArticolo.size(); i++){
                    if(codArticolo.get(i).equals(codiceArt)){
                        totQta = totQta + Integer.parseInt(qtaDaScalare.get(i));
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
                    insNColliSpunta.setFocusableInTouchMode(true);
                    insNColliSpunta.requestFocus();
                    showSoftKeyboard(insNColliSpunta);
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    play.play();
                }else{
                    articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
                }
            }else{
                alertArt("Errore!","Articolo non presente nel database, inserisci una nota");
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
                    String query = "select nome, articolo.descrizione, \n" +
                            "(select cast (ProgressivoArticolo.esistenza as int) as esistenza from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = 1) as esistenza, \n" +
                            "(select cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a >GETDATE() and MetaMagazzino = 1) as OrdinatoFornitoreArticoloXMagazzino,\n" +
                            "(select cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) from ProgressivoArticolo where MetaArticolo = articolo.id and da < GETDATE() and a > GETDATE() and MetaMagazzino = 1) as OrdinatoClienteArticoloXMagazzino, \n" +
                            "(select prezzo from articoloxlistino where idArticolo = articolo.id and idListino = 1) as prezzo, \n" +
                            "(select sottoUbicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=1) as sottoubicazione, \n" +
                            "(select Ubicazione from articoloxmagazzino where idArticolo=articolo.id and idmagazzino=1) as ubicazione, \n" +
                            "(select idListino from articoloxlistino where idArticolo = articolo.id and idListino = 1) as idListino\n" +
                            "from articolo left join alias on (alias.idArticolo = articolo.id)\n" +
                            "where nome = '"+findThis+"' or alias.codice = '"+findThis+"'\n" +
                            "order by ubicazione desc";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    int j = 0;
                    while(res.next()) {
                        if(j==0){
                            codiceArt = res.getString("nome");
                            description = res.getString("descrizione");
                        }
                        if(res.getInt("idListino") == listino){
                            j++;
                            prz = res.getDouble("prezzo");
                            esistenza = res.getInt("esistenza");
                            of = res.getInt("OrdinatoFornitoreArticoloXMagazzino");
                            oc = res.getInt("OrdinatoClienteArticoloXMagazzino");
                            ubiDef = res.getString("ubicazione");
                            subiDef = res.getString("sottoUbicazione");
                        }
                    }
                    if(j==0){
                        prz = 0.00;
                        esistenza = 0;
                        of = 0;
                        oc = 0;
                        ubiDef = "N/A";
                        subiDef = "N/A";
                    }
                    if (codiceArt != null) {
                        //z = "Ordine trovato";
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