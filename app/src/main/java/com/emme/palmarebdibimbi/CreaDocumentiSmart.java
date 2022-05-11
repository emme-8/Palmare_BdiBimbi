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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class CreaDocumentiSmart extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    Button btnCD, btnSalvaDoc;
    String alias = "";
    ListView listView;
    TextView txtSelectedArt;
    Context context;
    ArrayList<String> codici = new ArrayList<>();
    ArrayList<String> qta = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> ean = new ArrayList<>();
    ArrayList<String> codiciR = new ArrayList<>();
    ArrayList<String> descrizioniR = new ArrayList<>();
    ArrayList<String> eanR = new ArrayList<>();
    ConnectionClass connectionClass;
    EditText codArt;
    TextView txtCodArt, txtDesc, txtPV, txtPP;
    TextView txtEsSestu, txtEsMarconi,txtEsPirri,txtEsSassari,txtEsOlbia,txtEsNuoro,txtEsOristano,txtEsTortoli,txtEsCarbonia,txtEsTiburtina,txtEsCapena,txtEsOstiense,txtEsDep, txtEsThis;
    TextView txtOFSestu, txtOFMarconi,txtOFPirri,txtOFSassari,txtOFOlbia,txtOFNuoro,txtOFOristano,txtOFTortoli,txtOFCarbonia,txtOFTiburtina,txtOFCapena,txtOFOstiense,txtOFDep, txtOFThis;
    TextView txtOCSestu, txtOCMarconi,txtOCPirri,txtOCSassari,txtOCOlbia,txtOCNuoro,txtOCOristano,txtOCTortoli,txtOCCarbonia,txtOCTiburtina,txtOCCapena,txtOCOstiense,txtOCDep, txtOCThis;
    EditText barcodeText, insQtaND;
    int idL = 1;
    int mag = 0;
    String artOrForn = "";
    String artOrFornDesc = "";
    String store = "";
    int giaPremuto = 0;
    ProgressBar pbCD;

    SharedPreferences prefs;

    public boolean getDefaults(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray2 = new JSONArray(preferences.getString("codIntCD", "[]"));
            if (jsonArray2.length() == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void recuperaStato(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArrayCod = new JSONArray(preferences.getString("codIntCD", "[]"));
            JSONArray jsonArrayDesc = new JSONArray(preferences.getString("descIntCD", "[]"));
            JSONArray jsonArrayQta = new JSONArray(preferences.getString("qtaIntCD", "[]"));
            JSONArray jsonArrayAlias = new JSONArray(preferences.getString("eanIntCD", "[]"));
            for (int i = 0; i < jsonArrayCod.length(); i++) {
                codici.add(jsonArrayCod.getString(i));
                descrizioni.add(jsonArrayDesc.getString(i));
                qta.add(jsonArrayQta.getString(i));
                ean.add(jsonArrayAlias.getString(i));
            }
            mag = preferences.getInt("magIntCD", 1);
            idL = preferences.getInt("listIntCD",6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void salvaStato(){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        JSONArray jsonArrayCod = new JSONArray();
        JSONArray jsonArrayDesc = new JSONArray();
        JSONArray jsonArrayQta = new JSONArray();
        JSONArray jsonArrayAlias = new JSONArray();
        JSONArray jsonArrayNumDoc = new JSONArray();
        for(int i=0; i<codici.size(); i++){
            jsonArrayCod.put(codici.get(i));
            jsonArrayDesc.put(descrizioni.get(i));
            jsonArrayQta.put(qta.get(i));
            jsonArrayAlias.put(ean.get(i));
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("codIntCD", jsonArrayCod.toString());
        editor.putInt("magIntCD", mag);
        editor.putInt("listIntCD", idL);
        editor.putString("descIntCD", jsonArrayDesc.toString());
        editor.putString("qtaIntCD", jsonArrayQta.toString());
        editor.putString("eanIntCD", jsonArrayAlias.toString());
        editor.putString("numIntCD", jsonArrayNumDoc.toString());
        editor.apply();
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    recuperaStato(this);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void risolviMag(){
        switch (store) {
            case "MASTER":
                mag = 1;
                idL = 1;
                break;
            case "SESTU":
                mag = 77;
                idL = 6;
                break;
            case "MARCONI":
                mag = 35;
                idL = 6;
                break;
            case "PIRRI":
                mag = 72;
                idL = 6;
                break;
            case "OLBIA":
                mag = 76;
                idL = 5;
                break;
            case "SASSARI":
                mag = 74;
                idL = 9;
                break;
            case "NUORO":
                mag = 32;
                idL = 4;
                break;
            case "CARBONIA":
                mag = 78;
                idL = 7;
                break;
            case "TORTOLI":
                mag = 75;
                idL = 3;
                break;
            case "ORISTANO":
                mag = 71;
                idL = 8;
                break;
            case "TIBURTINA":
                mag = 85;
                idL = 3049;
                break;
            case "CAPENA":
                mag = 87;
                idL = 3050;
                break;
            case "OSTIENSE":
                mag = 86;
                idL = 3048;
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                idL = 1;
                break;
            case "CASILINA":
                mag = 90;
                idL = 3049;
                break;
            case "INTRANSITO":
                mag = 88;
                idL = 1;
                break;
            case "INTEMPORANEO":
                mag = 89;
                idL = 1;
                break;
            default:
                mag = 0;
                idL = 1;
                break;
        }
    }

    public void loadAll(String codArtRec, int type){
        setContentView(R.layout.activity_crea_documenti_smart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        if(type == 1){
            if(getDefaults(this)){
                alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi riprendere quel documento? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
            }
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
        }
        risolviMag();

        connectionClass = new ConnectionClass();

        btnCD = findViewById(R.id.btnCDS);
        pbCD = findViewById(R.id.pbCDS);
        pbCD.setVisibility(View.GONE);
        txtCodArt = findViewById(R.id.txtCodArtCDS);
        txtDesc = findViewById(R.id.txtDescCDS);
        txtPV = findViewById(R.id.txtPVCDS);
        txtPP = findViewById(R.id.txtPPCDS);
        txtEsSestu = findViewById(R.id.txtEsSestuCDS);
        txtEsMarconi = findViewById(R.id.txtEsMarconiCDS);
        txtEsPirri = findViewById(R.id.txtEsPirriCDS);
        txtEsSassari = findViewById(R.id.txtEsSassariCDS);
        txtEsOlbia = findViewById(R.id.txtEsOlbiaCDS);
        txtEsNuoro = findViewById(R.id.txtEsNuoroCDS);
        txtEsOristano = findViewById(R.id.txtEsOristanoCDS);
        txtEsTortoli = findViewById(R.id.txtEsTortoliCDS);
        txtEsCarbonia = findViewById(R.id.txtEsCarboniaCDS);
        txtEsTiburtina = findViewById(R.id.txtEsTiburtinaCDS);
        txtEsCapena = findViewById(R.id.txtEsCapenaCDS);
        txtEsOstiense = findViewById(R.id.txtEsOstienseCDS);
        txtEsDep = findViewById(R.id.txtEsDepCDS);
        txtOFSestu = findViewById(R.id.txtOFSestuCDS);
        txtOFMarconi = findViewById(R.id.txtOFMarconiCDS);
        txtOFPirri = findViewById(R.id.txtOFPirriCDS);
        txtOFSassari = findViewById(R.id.txtOFSassariCDS);
        txtOFOlbia = findViewById(R.id.txtOFOlbiaCDS);
        txtOFNuoro = findViewById(R.id.txtOFNuoroCDS);
        txtOFOristano = findViewById(R.id.txtOFOristanoCDS);
        txtOFTortoli = findViewById(R.id.txtOFTortoliCDS);
        txtOFCarbonia = findViewById(R.id.txtOFCarboniaCDS);
        txtOFTiburtina = findViewById(R.id.txtOFTiburtinaCDS);
        txtOFCapena = findViewById(R.id.txtOFCapenaCDS);
        txtOFOstiense = findViewById(R.id.txtOFOstienseCDS);
        txtOFDep = findViewById(R.id.txtOFDepCDS);
        txtOCSestu = findViewById(R.id.txtOCSestuCDS);
        txtOCMarconi = findViewById(R.id.txtOCMarconiCDS);
        txtOCPirri = findViewById(R.id.txtOCPirriCDS);
        txtOCSassari = findViewById(R.id.txtOCSassariCDS);
        txtOCOlbia = findViewById(R.id.txtOCOlbiaCDS);
        txtOCNuoro = findViewById(R.id.txtOCNuoroCDS);
        txtOCOristano = findViewById(R.id.txtOCOristanoCDS);
        txtOCTortoli = findViewById(R.id.txtOCTortoliCDS);
        txtOCCarbonia = findViewById(R.id.txtOCCarboniaCDS);
        txtOCTiburtina = findViewById(R.id.txtOCTiburtinaCDS);
        txtOCCapena = findViewById(R.id.txtOCCapenaCDS);
        txtOCOstiense = findViewById(R.id.txtOCOstienseCDS);
        txtOCDep = findViewById(R.id.txtOCDepCDS);
        barcodeText = findViewById(R.id.edtTxtCodArtCDS);
        btnCD = findViewById(R.id.btnCDS);
        txtEsThis = findViewById(R.id.txtThisEsCDS);
        txtOFThis = findViewById(R.id.txtThisOFCDS);
        txtOCThis = findViewById(R.id.txtThisOCCDS);
        btnSalvaDoc = findViewById(R.id.btnSalvaDocS);
        insQtaND = findViewById(R.id.insQtaNDS);

        barcodeText.setFocusableInTouchMode(true);
        barcodeText.requestFocus();
        barcodeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        if(!codArtRec.equals("")){
            barcodeText.setText(codArtRec);
        }

        barcodeText.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    artOrForn = barcodeText.getText().toString();
                    if(artOrForn.equals("")){
                        articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
                    }else{
                        CreaDocumentiSmart.FindArt cercaArt = new CreaDocumentiSmart.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });
        /*
        btnAddCD.setOnClickListener(v -> {
            if(txtCodArt.getText().toString().equals("")){
                articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
            }else{
                int j = 0;
                boolean find = false;
                for(int i=0; i<codici.size(); i++){
                    if(txtCodArt.getText().toString().equals(codici.get(i))){
                        j=i;
                        find = true;
                    }
                }
                if(find){
                    addQtaToArt("Attenzione!", "Hai già inserito " +qta.get(j)+ " pezzi di questo articolo, inserisci la quantità da aggiungere o sottrarre, inserisci 0 per ignorare", j);
                }else{
                    addNewArt("Attenzione!", "Inserisci la quantità per il seguente articolo: \n" + txtCodArt.getText().toString());
                }
            }
        });*/
        Button ricArt = findViewById(R.id.btnRicCDS);
        ricArt.setOnClickListener(v -> {
            setContentView(R.layout.ricerca_crea_doc);

            codArt = findViewById(R.id.edtCodR);
            txtSelectedArt = findViewById(R.id.txtSelectedArt);
            EditText descArt = findViewById(R.id.edtDescR);
            listView = findViewById(R.id.listRicArt);

            Button btnCloseRic = findViewById(R.id.btnExitRic);
            btnCloseRic.setOnClickListener(x -> {
                loadAll("", 0);
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
                    addArt("Attenzione!", "Sei sicuro di voler aggiungere l'articolo "+txtSelectedArt.getText().toString()+" alla lista?");
                }
            });
            Button btnRic = findViewById(R.id.btnRicXCD);
            btnRic.setOnClickListener(x ->{
                artOrForn = codArt.getText().toString();
                artOrFornDesc = descArt.getText().toString();
                CreaDocumentiSmart.RicArt cercaArt = new CreaDocumentiSmart.RicArt();
                cercaArt.execute("");
            });
        });
        btnSalvaDoc.setOnClickListener(v -> {
            salvaDoc("Attenzione", "Sei sicuro di voler salvare il documento?");
        });
        btnCD.setOnClickListener(v -> {
            artOrForn = barcodeText.getText().toString();
            CreaDocumentiSmart.FindArt findArt = new CreaDocumentiSmart.FindArt();
            findArt.execute();
        });

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = findViewById(R.id.surfaceView5);
        initialiseDetectorsAndSources();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAll("", 1);
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
                    if (ActivityCompat.checkSelfPermission(CreaDocumentiSmart.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CreaDocumentiSmart.this, new
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

    private void salvaDoc(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    Intent review = new Intent(CreaDocumentiSmart.this, ReviewCreaDoc.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", ean);
                    review.putStringArrayListExtra("qta", qta);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void addQtaToArt(String title,String message, int index){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText insQta = new EditText(this);
        insQta.setHint("Quantità");
        insQta.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(insQta);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(insQta.getText().toString().equals("")){
                insQta.setHintTextColor(Color.RED);
                addNewArt("Attenzione!", "Wtf bruh, push back button if u wrong");
            }else{
                dialog.cancel();
                Integer tot = Integer.parseInt(qta.get(index)) + Integer.parseInt(insQta.getText().toString());
                qta.set(index, tot.toString());
                salvaStato();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void addArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message);

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });

        builder.setPositiveButton("Si", (dialog, which) -> {
            artOrForn = txtSelectedArt.getText().toString();
            CreaDocumentiSmart.FindArt cercaArt = new CreaDocumentiSmart.FindArt();
            cercaArt.execute("");
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText note = new EditText(this);
        note.setHint("Note");
        layout.addView(note);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(note.getText().toString().equals("")){
                note.setHintTextColor(Color.RED);
                alertArt("Error!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                dialog.cancel();
                txtCodArt.setText(barcodeText.getText().toString());
                txtOFThis.setText("0");
                txtEsThis.setText("0");
                txtOCThis.setText("0");
                txtDesc.setText(note.getText().toString());
                codici.add(txtDesc.getText().toString());
                ean.add(txtCodArt.getText().toString());
                descrizioni.add(txtDesc.getText().toString());
                qta.add(insQtaND.getText().toString());
                barcodeText.setEnabled(true);
                barcodeText.setText("");
                insQtaND.setText("1");
                barcodeText.setFocusableInTouchMode(true);
                barcodeText.requestFocus();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void addNewArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText insQta = new EditText(this);
        insQta.setHint("Quantità");
        insQta.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(insQta);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(insQta.getText().toString().equals("")){
                insQta.setHintTextColor(Color.RED);
                addNewArt("Attenzione!", "Wtf bruh, push back button if u wrong");
            }else{
                dialog.cancel();
                codici.add(txtCodArt.getText().toString());
                ean.add(barcodeText.getText().toString());
                qta.add(insQta.getText().toString());
                descrizioni.add(txtDesc.getText().toString());
                salvaStato();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
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


    public String risolviMagXOc(){
        switch (mag){
            case 1:
            case 59:
                if(!txtOCDep.getText().toString().equals("")){
                    return txtOCDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtOCSestu.getText().toString().equals("")){
                    return txtOCSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtOCMarconi.getText().toString().equals("")){
                    return txtOCMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtOCPirri.getText().toString().equals("")){
                    return txtOCPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtOCOlbia.getText().toString().equals("")){
                    return txtOCOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtOCSassari.getText().toString().equals("")){
                    return txtOCSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtOCNuoro.getText().toString().equals("")){
                    return txtOCNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtOCCarbonia.getText().toString().equals("")){
                    return txtOCCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtOCTortoli.getText().toString().equals("")){
                    return txtOCTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtOCOristano.getText().toString().equals("")){
                    return txtOCOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtOCTiburtina.getText().toString().equals("")){
                    return txtOCTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtOCCapena.getText().toString().equals("")){
                    return txtOCCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtOCOstiense.getText().toString().equals("")){
                    return txtOCOstiense.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    public String risolviMagXOf(){
        switch (mag){
            case 1:
            case 59:
                if(!txtOFDep.getText().toString().equals("")){
                    return txtOFDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtOFSestu.getText().toString().equals("")){
                    return txtOFSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtOFMarconi.getText().toString().equals("")){
                    return txtOFMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtOFPirri.getText().toString().equals("")){
                    return txtOFPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtOFOlbia.getText().toString().equals("")){
                    return txtOFOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtOFSassari.getText().toString().equals("")){
                    return txtOFSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtOFNuoro.getText().toString().equals("")){
                    return txtOFNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtOFCarbonia.getText().toString().equals("")){
                    return txtOFCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtOFTortoli.getText().toString().equals("")){
                    return txtOFTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtOFOristano.getText().toString().equals("")){
                    return txtOFOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtOFTiburtina.getText().toString().equals("")){
                    return txtOFTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtOFCapena.getText().toString().equals("")){
                    return txtOFCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtOFOstiense.getText().toString().equals("")){
                    return txtOFOstiense.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    public String risolviMagEs(){
        switch (mag){
            case 1:
            case 59:
                if(!txtEsDep.getText().toString().equals("")){
                    return txtEsDep.getText().toString();
                }else{
                    return "0";
                }
            case 77:
                if(!txtEsSestu.getText().toString().equals("")){
                    return txtEsSestu.getText().toString();
                }else{
                    return "0";
                }
            case 35:
                if(!txtEsMarconi.getText().toString().equals("")){
                    return txtEsMarconi.getText().toString();
                }else{
                    return "0";
                }
            case 72:
                if(!txtEsPirri.getText().toString().equals("")){
                    return txtEsPirri.getText().toString();
                }else{
                    return "0";
                }
            case 76:
                if(!txtEsOlbia.getText().toString().equals("")){
                    return txtEsOlbia.getText().toString();
                }else{
                    return "0";
                }
            case 74:
                if(!txtEsSassari.getText().toString().equals("")){
                    return txtEsSassari.getText().toString();
                }else{
                    return "0";
                }
            case 32:
                if(!txtEsNuoro.getText().toString().equals("")){
                    return txtEsNuoro.getText().toString();
                }else{
                    return "0";
                }
            case 78:
                if(!txtEsCarbonia.getText().toString().equals("")){
                    return txtEsCarbonia.getText().toString();
                }else{
                    return "0";
                }
            case 75:
                if(!txtEsTortoli.getText().toString().equals("")){
                    return txtEsTortoli.getText().toString();
                }else{
                    return "0";
                }
            case 71:
                if(!txtEsOristano.getText().toString().equals("")){
                    return txtEsOristano.getText().toString();
                }else{
                    return "0";
                }
            case 85:
                if(!txtEsTiburtina.getText().toString().equals("")){
                    return txtEsTiburtina.getText().toString();
                }else{
                    return "0";
                }
            case 87:
                if(!txtEsCapena.getText().toString().equals("")){
                    return txtEsCapena.getText().toString();
                }else{
                    return "0";
                }
            case 86:
                if(!txtEsOstiense.getText().toString().equals("")){
                    return txtEsOstiense.getText().toString();
                }else{
                    return "0";
                }
            default:
                return "0";
        }
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void articoloInesistente(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreaDocumentiSmart.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Annulla",(dialog, which) -> {
                    dialog.cancel();
                    barcodeText.setEnabled(true);
                    barcodeText.setText("");
                    insQtaND.setText("1");
                    barcodeText.setFocusableInTouchMode(true);
                    barcodeText.requestFocus();
                });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText note = new EditText(this);
        note.setHint("Note");
        layout.addView(note);

        builder.setView(layout);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.cancel();
            int j = 0;
            boolean find = false;
            for(int i=0; i<ean.size(); i++){
                if(barcodeText.getText().toString().equals(ean.get(i))){
                    j=i;
                    find = true;
                }
            }
            if(find){
                Integer tot = Integer.parseInt(qta.get(j)) + Integer.parseInt(insQtaND.getText().toString());
                qta.set(j, tot.toString());
            }else{
                codici.add(note.getText().toString());
                ean.add(barcodeText.getText().toString());
                qta.add(insQtaND.getText().toString());
                descrizioni.add(note.getText().toString());
            }
            barcodeText.setEnabled(true);
            barcodeText.setText("");
            insQtaND.setText("1");
            barcodeText.setFocusableInTouchMode(true);
            barcodeText.requestFocus();
            salvaStato();
        });
        AlertDialog ok = builder.create();
        ok.show();
    }


    public void prezziPromoCat(int priorita){
        Connection con = null;
        ResultSet res;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT Articolo.nome, Promozione.TipoValoreSconto, cast(CategoriaElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join CategoriaElementoxPromozione on (Promozione.id = CategoriaElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.idCategoriaArticolo = CategoriaElementoxpromozione.idCategoria) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE inizioValidita < GETDATE() and fineValidita > GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita > GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and inizioValidita < GETDATE() and Articolo.nome = '"+artOrForn+"' " +
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
        int j = 0;
        boolean find = false;
        for(int i=0; i<codici.size(); i++){
            if(txtCodArt.getText().toString().equals(codici.get(i))){
                j=i;
                find = true;
            }
        }
        if(find){
            Integer tot = Integer.parseInt(qta.get(j)) + Integer.parseInt(insQtaND.getText().toString());
            qta.set(j, tot.toString());
        }else{
            codici.add(txtCodArt.getText().toString());
            ean.add(barcodeText.getText().toString());
            qta.add(insQtaND.getText().toString());
            descrizioni.add(txtDesc.getText().toString());
        }
        salvaStato();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        pbCD.setVisibility(View.GONE);
        barcodeText.setEnabled(true);
        barcodeText.setText("");
        insQtaND.setText("1");
        barcodeText.setFocusableInTouchMode(true);
        barcodeText.requestFocus();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
        play.play();
    }

    protected void findPromo() {
        Connection con = null;
        ResultSet res;
        int priorita = 0;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT Articolo.nome, Promozione.TipoValoreSconto, cast(ElementoxPromozione.valore as decimal(10,2)) as prezzoPromo," +
                        "Promozione.priorita, cast(ArticoloxListino.prezzo as decimal(10,2)) as prz, fineValidita, promozione.nome as pName " +
                        "FROM Promozione join ElementoxPromozione on (Promozione.id = ElementoXPromozione.idPromozione) " +
                        "join Articolo on (Articolo.id = ElementoxPromozione.idElemento) " +
                        "join ArticoloxListino on (Articolo.id = ArticoloxListino.idArticolo) " +
                        "WHERE inizioValidita < GETDATE() and fineValidita > GETDATE() and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8'" +
                        "and Articoloxlistino.idListino = '" + idL + "' " +
                        "or fineValidita is null and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita > GETDATE() and inizioValidita is null and Articolo.nome = '"+artOrForn+"' " +
                        "and Promozione.tipo = '8' " +
                        "and Articoloxlistino.idListino = '" + idL +"'" +
                        "or fineValidita is null and inizioValidita < GETDATE() and Articolo.nome = '"+artOrForn+"' " +
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


    protected void recuperaGiacenze() {
        Connection con = null;
        ResultSet res;
        try {
            con = connectionClass.CONN(context);
            if (con != null) {
                String query = "SELECT cast (ProgressivoArticolo.esistenza as int) as Esistenza, " +
                        "cast (ProgressivoArticolo.OrdinatoFornitoreArticoloXMagazzino as int) as OrdiniFornitore, " +
                        "cast (ProgressivoArticolo.OrdinatoClienteArticoloXMagazzino as int) as OrdiniCliente, " +
                        "(Magazzino.id) as magn, " +
                        "cast (ArticoloxListino.prezzo as decimal(10,2)) as prz " +
                        "FROM ProgressivoArticolo " +
                        "JOIN Articolo ON (ProgressivoArticolo.MetaArticolo = Articolo.id) " +
                        "JOIN Magazzino on (ProgressivoArticolo.MetaMagazzino = Magazzino.id)" +
                        "JOIN ArticoloxListino ON (ArticoloxListino.idArticolo = Articolo.id) " +
                        "WHERE Articolo.nome like '" + artOrForn + "%' " +
                        "AND ArticoloxListino.idListino = '1'" +
                        "AND da < GETDATE() " +
                        "AND a > GETDATE() " +
                        "AND isNascosto = '0' " +
                        "ORDER BY Articolo.nome, Magazzino.nome";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()) {
                    txtPV.setText(res.getString("prz"));
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
        findPromo();
    }

    public void setRows(){
        CreaDocumentiSmart.AdapterRicArt whatever = new CreaDocumentiSmart.AdapterRicArt(this, codiciR, descrizioniR, eanR, txtSelectedArt);
        listView.setAdapter(whatever);
    }

    public static class AdapterRicArt extends ArrayAdapter {

        private final Activity context;
        private ArrayList<String> codArt, alias;
        private ArrayList<String> desc;
        private TextView codArtET;

        public AdapterRicArt(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> aliasArrayParam, TextView codArtET) {

            super(context, R.layout.adapter_show_arts, codArtArrayParam);

            this.context = context;
            this.codArt = codArtArrayParam;
            this.desc = descArrayParam;
            this.alias = aliasArrayParam;
            this.codArtET = codArtET;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.adapter_show_arts, null, true);

            TextView txtCodArt = rowView.findViewById(R.id.txtCodArtR);
            TextView txtDesc = rowView.findViewById(R.id.txtDescArtR);
            TextView txtEAN = rowView.findViewById(R.id.txtEanArtR);

            txtCodArt.setText(codArt.get(position));
            txtDesc.setText(desc.get(position));
            txtEAN.setText(alias.get(position));

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
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo)" +
                            "WHERE articolo.nome like '%" + artOrForn + "%' and articolo.descrizione like '%" + artOrFornDesc + "%' " +
                            "OR alias.codice = '%" + artOrForn + "%' and articolo.descrizione like '%" + artOrFornDesc + "%' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()) {
                        isSuccess = true;
                        codiciR.add(res.getString("nome"));
                        descrizioniR.add(res.getString("descrizione"));
                        eanR.add(res.getString("codice"));
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

    public class FindArt extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        String desc;

        @Override
        protected void onPreExecute() {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            pbCD.setVisibility(View.VISIBLE);
            desc = "";
        }

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess) {
                txtCodArt.setText(artOrForn);
                txtDesc.setText(desc);
                recuperaGiacenze();
                txtEsThis.setText(risolviMagEs());
                txtOFThis.setText(risolviMagXOf());
                txtOCThis.setText(risolviMagXOc());
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                pbCD.setVisibility(View.GONE);
                articoloInesistente("Attenzione!","Articolo non presente nel database, inserisci una nota o premi annulla per tornare indietro");
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
                    String query = "SELECT articolo.nome, articolo.descrizione, alias.codice " +
                            "FROM articolo join alias on (articolo.id = alias.idarticolo)" +
                            "WHERE articolo.nome like '" + artOrForn + "'" +
                            "OR alias.codice = '" + artOrForn + "'";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        isSuccess = true;
                        artOrForn = res.getString("nome");
                        desc = res.getString("descrizione");
                        alias = res.getString("codice");
                    }else{
                        isSuccess = false;
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