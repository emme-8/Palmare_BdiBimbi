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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class IniziaPresaSmart extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    ConnectionClass connectionClass;
    ArrayList<String> codArticolo = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> codArticoloPresa = new ArrayList<>();
    ArrayList<String> qtaPresa = new ArrayList<>();
    ArrayList<String> idDoc = new ArrayList<>();
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> ubicazione = new ArrayList<>();
    ArrayList<String> esistenza = new ArrayList<>();
    ArrayList<String> impegnati = new ArrayList<>();
    ArrayList<String> sottoubicazione = new ArrayList<>();
    TextView txtCodArtPresa, txtUbicPresa, txtQtaPresa, txtDescPresa, txtSubicPresa, txtesPresa, txtEanPresa, txtImpPresa, txtCount;
    Button btnFindArtPresa, btnFinePresa;
    ImageButton btnNext, btnPrev;
    EditText barcodeText, insQtaPresa;
    Context context;
    String findThis;
    String trovaQuesto;
    String ean = null;
    String magazzino;
    String docsName ="";
    String fornitore = "";
    String tipoDoc = "";
    int giaPremuto = 0;
    int noMatch = 0; int inDoc = 0;
    int listino = 0; int mag = 0; int i, j, f;
    int magRif;
    SharedPreferences prefs;

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void recuperaStato(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArrayCod = new JSONArray(preferences.getString("codIntPresa", "[]"));
            JSONArray jsonArrayDesc = new JSONArray(preferences.getString("descIntPresa", "[]"));
            JSONArray jsonArrayQtaDoc = new JSONArray(preferences.getString("qtaDocIntPresa", "[]"));
            JSONArray jsonArrayQta = new JSONArray(preferences.getString("qtaIntPresa", "[]"));
            JSONArray jsonArrayAlias = new JSONArray(preferences.getString("eanIntPresa", "[]"));
            JSONArray jsonArrayUbi = new JSONArray(preferences.getString("ubiIntPresa", "[]"));
            JSONArray jsonArraySubi = new JSONArray(preferences.getString("subiIntPresa", "[]"));
            JSONArray jsonArrayNumDoc = new JSONArray(preferences.getString("numIntPresa", "[]"));
            JSONArray jsonArrayEs = new JSONArray(preferences.getString("esIntPresa", "[]"));
            JSONArray jsonArrayImp = new JSONArray(preferences.getString("impIntPresa", "[]"));
            for (int i = 0; i < jsonArrayCod.length(); i++) {
                codArticoloPresa.add(jsonArrayCod.getString(i));
                codArticolo.add(jsonArrayCod.getString(i));
                descrizioni.add(jsonArrayDesc.getString(i));
                qta.add(jsonArrayQtaDoc.getString(i));
                qtaPresa.add(jsonArrayQta.getString(i));
                alias.add(jsonArrayAlias.getString(i));
                ubicazione.add(jsonArrayUbi.getString(i));
                sottoubicazione.add(jsonArraySubi.getString(i));
                numDoc.add(jsonArrayNumDoc.getString(i));
                esistenza.add(jsonArrayEs.getString(i));
                impegnati.add(jsonArrayImp.getString(i));
            }
            docsName = preferences.getString("nameIntPresa","");
            mag = preferences.getInt("magIntPresa", 1);
            magRif = preferences.getInt("magRifPresa", 1);
            listino = preferences.getInt("listIntPresa",6);
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
        JSONArray jsonArrayQta = new JSONArray();
        JSONArray jsonArrayAlias = new JSONArray();
        JSONArray jsonArrayUbi = new JSONArray();
        JSONArray jsonArraySubi = new JSONArray();
        JSONArray jsonArrayNumDoc = new JSONArray();
        JSONArray jsonArrayEs = new JSONArray();
        JSONArray jsonArrayImp = new JSONArray();
        for(int i=0; i<codArticoloPresa.size(); i++){
            jsonArrayCod.put(codArticoloPresa.get(i));
            jsonArrayDesc.put(descrizioni.get(i));
            jsonArrayQtaDoc.put(qta.get(i));
            jsonArrayQta.put(qtaPresa.get(i));
            jsonArrayAlias.put(alias.get(i));
            jsonArrayUbi.put(ubicazione.get(i));
            jsonArraySubi.put(sottoubicazione.get(i));
            jsonArrayNumDoc.put(numDoc.get(i));
            jsonArrayEs.put(esistenza.get(i));
            jsonArrayImp.put(impegnati.get(i));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("codIntPresa", jsonArrayCod.toString());
        editor.putInt("magIntPresa", mag);
        editor.putInt("magRifPresa", magRif);
        editor.putInt("listIntPresa", listino);
        editor.putString("nameIntPresa", docsName);
        editor.putString("esIntPresa", jsonArrayEs.toString());
        editor.putString("impIntPresa", jsonArrayImp.toString());
        editor.putString("descIntPresa", jsonArrayDesc.toString());
        editor.putString("qtaDocIntPresa", jsonArrayQtaDoc.toString());
        editor.putString("qtaResIntPresa", jsonArrayQtaDoc.toString());
        editor.putString("qtaIntPresa", jsonArrayQta.toString());
        editor.putString("eanIntPresa", jsonArrayAlias.toString());
        editor.putString("ubiIntPresa", jsonArrayUbi.toString());
        editor.putString("subiIntPresa", jsonArraySubi.toString());
        editor.putString("numIntPresa", jsonArrayNumDoc.toString());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaPresaSmart.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_presa_smart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle extras = getIntent().getExtras();
        int rip = 0;
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            magRif = extras.getInt("magRif");
            tipoDoc = extras.getString("tipoDoc");
            rip = extras.getInt("rip");
            fornitore = extras.getString("fornitore");
            magazzino = extras.getString("magazzino");
        }

        connectionClass = new ConnectionClass();
        context = this;

        j = 0;
        i = 0;
        f = -1;

        if(rip == 1){
            recuperaStato(this);
        }else{
            codArticolo = ((MyApplication) this.getApplication()).getCodArt();
            alias = ((MyApplication) this.getApplication()).getAlias();
            qta = ((MyApplication) this.getApplication()).getQuantita();
            idDoc = ((MyApplication) this.getApplication()).getID();
            numDoc = ((MyApplication) this.getApplication()).getNum();
            descrizioni = ((MyApplication) this.getApplication()).getDesc();
            ubicazione = ((MyApplication) this.getApplication()).getUbic();
            sottoubicazione = ((MyApplication) this.getApplication()).getSubic();
            esistenza = ((MyApplication) this.getApplication()).getEsistenza();
            impegnati = ((MyApplication) this.getApplication()).getImpegnati();

            for(int i = 0; i<codArticolo.size(); i++){
                codArticoloPresa.add(codArticolo.get(i));
                qtaPresa.add("0");
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

        txtCount = findViewById(R.id.txtCountS);
        txtCodArtPresa = findViewById(R.id.txtCodArtPresaS);
        txtUbicPresa = findViewById(R.id.txtWherePresaS);
        txtSubicPresa = findViewById(R.id.txtWherePresa2S);
        txtQtaPresa = findViewById(R.id.txtHowManyPresaS);
        btnNext = findViewById(R.id.btnSuccArtS);
        btnPrev = findViewById(R.id.btnPrevArtS);
        txtDescPresa = findViewById(R.id.txtDescArtPresaS);
        barcodeText = findViewById(R.id.editMatchArtS);
        btnFindArtPresa = findViewById(R.id.btnFindArtPresaS);
        insQtaPresa = findViewById(R.id.txtInsQtaPresaS);
        btnFinePresa = findViewById(R.id.btnFinePresaS);
        txtesPresa = findViewById(R.id.txtEsPresaS);
        txtEanPresa = findViewById(R.id.txtEanPresaS);
        txtImpPresa = findViewById(R.id.txtImpPresaS);
        FloatingActionButton btnInfoOX = findViewById(R.id.btnInfoOXPS);
        txtesPresa.setText(esistenza.get(i));
        txtImpPresa.setText(impegnati.get(i));
        txtCodArtPresa.setText(codArticolo.get(i));
        txtQtaPresa.setText(qta.get(i));
        txtUbicPresa.setText(ubicazione.get(i));
        txtSubicPresa.setText(sottoubicazione.get(i));
        txtDescPresa.setText(descrizioni.get(i));
        insQtaPresa.setText(qtaPresa.get(i));
        btnFinePresa.setVisibility(View.GONE);

        txtCount.setText("1/"+codArticolo.size());

        insQtaPresa.setEnabled(false);
        btnPrev.setVisibility(View.INVISIBLE);

        insQtaPresa.setSelectAllOnFocus(true);

        barcodeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        barcodeText.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = barcodeText.getText().toString();
                    if(!findThis.equals("")){
                        IniziaPresaSmart.FindArt cercaArt = new IniziaPresaSmart.FindArt();
                        cercaArt.execute("");
                        showSoftKeyboard(insQtaPresa);
                    }
                }
            }
            return false;
        });
        btnFinePresa.setOnClickListener(v -> {
            salvaPresa("Attenzione!", "Sei sicuro di voler concludere e salvare?");
        });
        btnFindArtPresa.setOnClickListener(v -> {
            if(!barcodeText.getText().toString().equals("")){
                findThis = barcodeText.getText().toString();
                IniziaPresaSmart.FindArt findArt = new IniziaPresaSmart.FindArt();
                findArt.execute();
            }
        });
        btnInfoOX.setOnClickListener(v -> {
            IniziaPresaSmart.InfoOX info = new IniziaPresaSmart.InfoOX();
            info.execute();
        });
        btnNext.setOnClickListener(v -> {
            if(i != codArticolo.size()-1){
                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0){
                    inDoc = 0;
                    if(noMatch == 1){
                        noMatch = 0;
                        qtaPresa.set(f, insQtaPresa.getText().toString());
                    }else{
                        qtaPresa.set(i, insQtaPresa.getText().toString());
                        i++;
                    }
                    btnPrev.setVisibility(View.VISIBLE);
                    if(i == codArticolo.size()-1){
                        btnFinePresa.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.INVISIBLE);
                    }else{
                        btnNext.setVisibility(View.VISIBLE);
                    }
                    txtCount.setText((i+1)+"/"+codArticolo.size());
                    txtesPresa.setText(esistenza.get(i));
                    txtEanPresa.setText(alias.get(i));
                    txtImpPresa.setText(impegnati.get(i));
                    txtCodArtPresa.setText(codArticolo.get(i));
                    txtQtaPresa.setText(qta.get(i));
                    txtUbicPresa.setText(ubicazione.get(i));
                    txtSubicPresa.setText(sottoubicazione.get(i));
                    txtDescPresa.setText(descrizioni.get(i));
                    insQtaPresa.setText((qtaPresa.get(i)));
                    insQtaPresa.setEnabled(false);
                    barcodeText.setText("");
                    trovaQuesto = codArticolo.get(i);
                    IniziaPresaSmart.FindEAN findEan = new IniziaPresaSmart.FindEAN();
                    findEan.execute();
                    barcodeText.setFocusableInTouchMode(true);
                    barcodeText.requestFocus();
                    hideKeyboard(this);
                }else if(insQtaPresa.isEnabled()){
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }else{
                    qtaZero("Attenzione!", "Stai saltando un articolo presente nel documento, continuare?", 1);
                }
            }
            salvaStato();
        });
        btnPrev.setOnClickListener(v -> { ;
            if(i != 0){
                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0){
                    inDoc = 0;
                    if(noMatch == 1){
                        noMatch = 0;
                        btnFinePresa.setVisibility(View.GONE);
                        qtaPresa.set(qtaPresa.size() - 1, insQtaPresa.getText().toString());
                    }else{
                        btnFinePresa.setVisibility(View.GONE);
                        qtaPresa.set(i, insQtaPresa.getText().toString());
                        i--;
                    }
                    btnNext.setVisibility(View.VISIBLE);
                    if (i == 0) {
                        btnPrev.setVisibility(View.INVISIBLE);
                    } else {
                        btnPrev.setVisibility(View.VISIBLE);
                    }
                    txtCount.setText((i+1)+"/"+codArticolo.size());
                    txtesPresa.setText(esistenza.get(i));
                    txtEanPresa.setText(alias.get(i));
                    txtImpPresa.setText(impegnati.get(i));
                    txtCodArtPresa.setText(codArticolo.get(i));
                    txtQtaPresa.setText(qta.get(i));
                    txtUbicPresa.setText(ubicazione.get(i));
                    txtDescPresa.setText(descrizioni.get(i));
                    insQtaPresa.setText((qtaPresa.get(i)));
                    insQtaPresa.setEnabled(false);
                    barcodeText.setText("");
                    trovaQuesto = codArticolo.get(i);
                    IniziaPresaSmart.FindEAN findEan = new IniziaPresaSmart.FindEAN();
                    findEan.execute();
                }else if(insQtaPresa.isEnabled()){
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }else{
                    qtaZero("Attenzione!", "Stai saltando un articolo presente nel documento, continuare?", 0);
                }
            }if(noMatch == 1){
                Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                if(PORCODIO!=0) {
                    noMatch = 0;
                    btnFinePresa.setVisibility(View.GONE);
                    qtaPresa.set(qtaPresa.size() - 1, insQtaPresa.getText().toString());
                    btnNext.setVisibility(View.VISIBLE);
                    btnPrev.setVisibility(View.INVISIBLE);
                    txtCodArtPresa.setText(codArticolo.get(i));
                    txtQtaPresa.setText(qta.get(i));
                    txtCount.setText((i+1)+"/"+codArticolo.size());
                    txtesPresa.setText(esistenza.get(i));
                    txtEanPresa.setText(alias.get(i));
                    txtImpPresa.setText(impegnati.get(i));
                    txtUbicPresa.setText(ubicazione.get(i));
                    txtDescPresa.setText(descrizioni.get(i));
                    insQtaPresa.setText((qtaPresa.get(i)));
                    insQtaPresa.setEnabled(false);
                    barcodeText.setText("");
                }else{
                    quantitaMancante("Errore!", "Hai sparato l'articolo, devi assegnargli una quantità");
                }
            }
            salvaStato();
        });
        if(codArticolo.size() == 1){
            btnNext.setVisibility(View.INVISIBLE);
            btnFinePresa.setVisibility(View.VISIBLE);
        }
        insQtaPresa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showSoftKeyboard(insQtaPresa);
                }
            }
        });
        trovaQuesto = codArticolo.get(i);
        IniziaPresaSmart.FindEAN findEan = new IniziaPresaSmart.FindEAN();
        findEan.execute();

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = findViewById(R.id.surfaceView4);
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
                    if (ActivityCompat.checkSelfPermission(IniziaPresaSmart.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(IniziaPresaSmart.this, new
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

    private void qtaZero(String title,String message, int tipo){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    if(tipo == 0){
                        if(noMatch == 1){
                            noMatch = 0;
                            btnFinePresa.setVisibility(View.GONE);
                            qtaPresa.set(f, insQtaPresa.getText().toString());
                        }else{
                            btnFinePresa.setVisibility(View.GONE);
                            qtaPresa.set(i, insQtaPresa.getText().toString());
                            i--;
                        }
                        btnNext.setVisibility(View.VISIBLE);
                        if (i == 0) {
                            btnPrev.setVisibility(View.INVISIBLE);
                        } else {
                            btnPrev.setVisibility(View.VISIBLE);
                        }
                        txtCount.setText((i+1)+"/"+codArticolo.size());
                        txtesPresa.setText(esistenza.get(i));
                        txtEanPresa.setText(alias.get(i));
                        txtImpPresa.setText(impegnati.get(i));
                        txtCodArtPresa.setText(codArticolo.get(i));
                        txtQtaPresa.setText(qta.get(i));
                        txtUbicPresa.setText(ubicazione.get(i));
                    }else{
                        if(noMatch == 1){
                            noMatch = 0;
                            qtaPresa.set(f, insQtaPresa.getText().toString());
                        }else{
                            qtaPresa.set(i, insQtaPresa.getText().toString());
                            i++;
                        }
                        btnPrev.setVisibility(View.VISIBLE);
                        if(i == codArticolo.size()-1){
                            btnFinePresa.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.INVISIBLE);
                        }else{
                            btnNext.setVisibility(View.VISIBLE);
                        }
                        txtCount.setText((i+1)+"/"+codArticolo.size());
                        txtesPresa.setText(esistenza.get(i));
                        txtEanPresa.setText(alias.get(i));
                        txtImpPresa.setText(impegnati.get(i));
                        txtCodArtPresa.setText(codArticolo.get(i));
                        txtQtaPresa.setText(qta.get(i));
                        txtUbicPresa.setText(ubicazione.get(i));
                        txtSubicPresa.setText(sottoubicazione.get(i));
                    }
                    inDoc = 0;
                    txtCount.setText((i+1)+"/"+codArticolo.size());
                    txtImpPresa.setText(impegnati.get(i));
                    txtesPresa.setText(esistenza.get(i));
                    txtDescPresa.setText(descrizioni.get(i));
                    insQtaPresa.setText((qtaPresa.get(i)));
                    insQtaPresa.setEnabled(false);
                    barcodeText.setText("");
                    salvaStato();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaPresa(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    Integer PORCODIO = Integer.parseInt(insQtaPresa.getText().toString());
                    if(PORCODIO!=0) {
                        inDoc = 0;
                        if (noMatch == 1) {
                            noMatch = 0;
                            qtaPresa.set(f, insQtaPresa.getText().toString());
                        } else {
                            qtaPresa.set(i, insQtaPresa.getText().toString());
                            i++;
                        }
                        insQtaPresa.setEnabled(false);
                        barcodeText.setText("");
                    }
                    dialog.cancel();
                    salvaStato();
                    Intent review = new Intent(IniziaPresaSmart.this, ReviewSpunta.class);
                    review.putStringArrayListExtra("codici", codArticoloPresa);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putStringArrayListExtra("qtaDoc", qta);
                    review.putStringArrayListExtra("nDoc", numDoc);
                    review.putExtra("tipo", 1);
                    review.putExtra("docsName", docsName);
                    review.putExtra("magazzino", magazzino);
                    review.putStringArrayListExtra("qtaSpunta", qtaPresa);
                    review.putStringArrayListExtra("ubicazioni", ubicazione);
                    review.putStringArrayListExtra("subicazioni", sottoubicazione);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void quantitaMancante(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Si", (dialog, which) -> dialog.cancel());
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloNonTrovato(String title,String message, String art, String desc){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    barcodeText.setText("");
                    noMatch = 0;
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    codArticolo.add(art);
                    codArticoloPresa.add(art);
                    qtaPresa.add("0");
                    esistenza.add("");
                    qta.add("0");
                    alias.add(ean);
                    idDoc.add(idDoc.get(0));
                    numDoc.add(idDoc.get(0));
                    descrizioni.add(desc);
                    ubicazione.add("");
                    sottoubicazione.add("");

                    btnNext.setVisibility(View.INVISIBLE);
                    btnPrev.setVisibility(View.VISIBLE);

                    txtCodArtPresa.setText(art);
                    txtQtaPresa.setText("0");
                    txtesPresa.setText("");
                    txtCount.setText(codArticolo.size()+"/"+codArticolo.size());
                    txtImpPresa.setText("");
                    txtUbicPresa.setText("");
                    txtSubicPresa.setText("");
                    txtDescPresa.setText(desc);
                    insQtaPresa.setText("0");
                    insQtaPresa.setEnabled(true);
                    insQtaPresa.setFocusableInTouchMode(true);
                    insQtaPresa.requestFocus();
                    showSoftKeyboard(insQtaPresa);
                    salvaStato();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String codiceArt = null;
        String descrizioneLet = null;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess) {
                if(codiceArt.equals(txtCodArtPresa.getText())){
                    insQtaPresa.setText("1");
                    insQtaPresa.setEnabled(true);
                    insQtaPresa.setFocusableInTouchMode(true);
                    insQtaPresa.requestFocus();
                    insQtaPresa.setSelectAllOnFocus(true);
                }else{
                    noMatch = 1;
                    f = -1;
                    for(j = 0; j<codArticolo.size(); j++){
                        if(codiceArt.equals(codArticolo.get(j))){
                            inDoc = 1;
                            f = j;
                            if(j == codArticolo.size()-1){
                                btnFinePresa.setVisibility(View.VISIBLE);
                                btnNext.setVisibility(View.INVISIBLE);
                            }else{
                                btnNext.setVisibility(View.VISIBLE);
                            }if(j == 0){
                                btnPrev.setVisibility(View.INVISIBLE);
                            }else{
                                btnPrev.setVisibility(View.VISIBLE);
                            }
                            txtCodArtPresa.setText(codArticolo.get(j));
                            txtQtaPresa.setText(qta.get(j));
                            txtUbicPresa.setText(ubicazione.get(j));
                            txtesPresa.setText(esistenza.get(j));
                            txtCount.setText((j+1)+"/"+codArticolo.size());
                            txtImpPresa.setText(impegnati.get(j));
                            txtEanPresa.setText(alias.get(j));
                            txtSubicPresa.setText(sottoubicazione.get(j));
                            txtDescPresa.setText(descrizioni.get(j));
                            insQtaPresa.setText((qtaPresa.get(j)));
                            insQtaPresa.setEnabled(true);

                        }
                    }
                    if(inDoc == 0){
                        articoloNonTrovato("Attenzione!","Articolo non presente nel documento, vuoi aggiungerlo?", codiceArt, descrizioneLet);
                    }else{
                        insQtaPresa.setFocusableInTouchMode(true);
                        insQtaPresa.requestFocus();
                        showSoftKeyboard(insQtaPresa);
                    }
                }
            }else{

            }if(giaPremuto == 1){
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
                    String query = "select articolo.nome, Alias.codice, articolo.descrizione " +
                            "from articolo left join alias on (alias.idArticolo = articolo.id) " +
                            "where codice = '"+ findThis +"' or nome = '"+ findThis +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()) {
                        isSuccess = true;
                        if(res.getString("codice")!=null){
                            ean = res.getString("codice");
                        }else{
                            ean = "";
                        }
                        codiceArt = res.getString("nome");
                        descrizioneLet = res.getString("descrizione");
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

    public class InfoOX extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String artOX = "";
        ArrayList<String> numOX, dataC, stato, qtaOX, serieOX;
        EditText infoOX;

        @Override
        protected void onPreExecute() {
            artOX = txtCodArtPresa.getText().toString();
            numOX = new ArrayList<>();
            dataC = new ArrayList<>();
            stato = new ArrayList<>();
            qtaOX = new ArrayList<>();
            serieOX = new ArrayList<>();
        }

        private void infoOX(String title,EditText message){
            AlertDialog.Builder builder = new AlertDialog.Builder(IniziaPresaSmart.this)
                    .setTitle(title)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.cancel());

            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(parms);

            layout.setGravity(Gravity.CLIP_VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            layout.addView(message);

            builder.setView(layout);

            AlertDialog ok = builder.create();
            ok.show();
        }

        public String decodificaSerie(String serie){
            switch (serie) {
                case "1":
                    return "MASTER";
                case "20":
                    return "SESTU";
                case "5":
                    return "MARCONI";
                case "3":
                    return "PIRRI";
                case "4":
                    return "OLBIA";
                case "9":
                    return "SASSARI";
                case "6":
                    return "NUORO";
                case "7":
                    return "CARBONIA";
                case "8":
                    return "TORTOLI";
                case "10":
                    return "ORISTANO";
                case "33":
                    return "TIBURTINA";
                case "31":
                    return "CAPENA";
                case "32":
                    return "OSTIENSE";
                case "34":
                    return "CASILINA";
                default:
                    return "";
            }
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess){
                infoOX = new EditText(context);
                infoOX.setFocusableInTouchMode(false);
                infoOX.clearFocus();
                for(int i=0; i<numOX.size();i++){
                    String thisState;
                    if(stato.get(i).equals("0")){
                        thisState = "SOSPESO";
                    }else{
                        thisState = "BLOCCATO";
                    }
                    if(i==0){
                        infoOX.setText("Numero: " + numOX.get(i) + "\n");
                        infoOX.append("Quantità: " + qtaOX.get(i) + "\n");
                        infoOX.append("Data consegna: " + dataC.get(i) + "\n");
                        infoOX.append("Stato: " + thisState + "\n");
                        infoOX.append("Store: " + decodificaSerie(serieOX.get(i)) + "\n\n\n");
                    }else{
                        infoOX.append("Numero: " + numOX.get(i) + "\n");
                        infoOX.append("Quantità: " + qtaOX.get(i) + "\n");
                        infoOX.append("Data consegna: " + dataC.get(i) + "\n");
                        infoOX.append("Stato: " + thisState + "\n");
                        infoOX.append("Store: " + decodificaSerie(serieOX.get(i)) + "\n\n\n");
                    }
                }
                infoOX("Info OX " + artOX, infoOX);
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
                    String query = "select RigaDocumentoCommerciale.codiceArticolo, RigaDocumentoCommerciale.quantita, RigaDocumentoCommerciale.stato, RigaDocumentoCommerciale.idMagazzinoDestinazione, RigaDocumentoCommerciale.dataConsegna, DocumentoCommerciale.numero, serie " +
                            "from RigaDocumentoCommerciale join DocumentoCommerciale on (DocumentoCommerciale.id = RigaDocumentoCommerciale.idMaster) " +
                            "where RigaDocumentoCommerciale.selettore like 'MetaRigaOrdineCorrispettivo' and codiceArticolo like '"+artOX+"' and DocumentoCommerciale.selettore like 'MetaOrdineCorrispettivo' and RigaDocumentoCommerciale.stato in (0,3) and RigaDocumentoCommerciale.idMagazzinoDestinazione = "+magRif+" " +
                            "order by RigaDocumentoCommerciale.stato desc ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess=true;
                        numOX.add(res.getString("numero"));
                        dataC.add(res.getString("dataConsegna").substring(0,11));
                        stato.add(res.getString("stato"));
                        qtaOX.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                        serieOX.add(res.getString("serie"));
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

    public class FindEAN extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String questoEAN = "";

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(String r) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(isSuccess){
                txtEanPresa.setText(questoEAN);
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
                    String query = "select top 1 alias.codice " +
                            "from articolo left join alias on (alias.idArticolo = articolo.id) " +
                            "where nome = '"+ trovaQuesto +"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        txtEanPresa.setText(res.getString("codice"));
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