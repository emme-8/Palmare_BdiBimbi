package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.zebra.sdk.comm.BluetoothConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VerificaGiacenzeSmart extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private String barcodeData;
    Spinner spinner;
    Button btnVG;
    String alias = "";
    ListView listView;
    TextView txtSelectedArt;
    ConnectionClass connectionClass;
    Context context;
    ArrayList<String> codiciR = new ArrayList<>();
    ArrayList<String> descrizioniR = new ArrayList<>();
    ArrayList<String> eanR = new ArrayList<>();
    TextView txtCodArt, txtDesc, txtPV, txtPP;
    TextView txtEsSestu, txtEsMarconi,txtEsPirri,txtEsSassari,txtEsOlbia,txtEsNuoro,txtEsOristano,txtEsTortoli,txtEsCarbonia,txtEsTiburtina,txtEsCapena,txtEsOstiense,txtEsDep;
    TextView txtOFSestu, txtOFMarconi,txtOFPirri,txtOFSassari,txtOFOlbia,txtOFNuoro,txtOFOristano,txtOFTortoli,txtOFCarbonia,txtOFTiburtina,txtOFCapena,txtOFOstiense,txtOFDep;
    TextView txtOCSestu, txtOCMarconi,txtOCPirri,txtOCSassari,txtOCOlbia,txtOCNuoro,txtOCOristano,txtOCTortoli,txtOCCarbonia,txtOCTiburtina,txtOCCapena,txtOCOstiense,txtOCDep;
    EditText barcodeText;
    int idL = 1;
    int mag = 0;
    String store;
    String bt = "";
    String printer = "";
    String artOrForn = "";
    String artOrFornDesc = "";
    ProgressBar pbVG;
    int giaPremuto = 0;
    String tipoEt = "";
    String codArt = "";
    EditText codArtET;
    String desc = "";
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
                idL = 3052;
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
                            "TEXT 4 0 170 25 " + przP +
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
        setContentView(R.layout.activity_verifica_giacenze_smart);

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

        btnVG = findViewById(R.id.btnVGS);
        pbVG = findViewById(R.id.pbVGS);
        pbVG.setVisibility(View.GONE);
        txtCodArt = findViewById(R.id.txtCodArtVGS);
        txtDesc = findViewById(R.id.txtDescVGS);
        txtPV = findViewById(R.id.txtPVVGS);
        txtPP = findViewById(R.id.txtPPVGS);
        txtEsSestu = findViewById(R.id.txtEsSestuS);
        txtEsMarconi = findViewById(R.id.txtEsMarconiS);
        txtEsPirri = findViewById(R.id.txtEsPirriS);
        txtEsSassari = findViewById(R.id.txtEsSassariS);
        txtEsOlbia = findViewById(R.id.txtEsOlbiaS);
        txtEsNuoro = findViewById(R.id.txtEsNuoroS);
        txtEsOristano = findViewById(R.id.txtEsOristanoS);
        txtEsTortoli = findViewById(R.id.txtEsTortoliS);
        txtEsCarbonia = findViewById(R.id.txtEsCarboniaS);
        txtEsTiburtina = findViewById(R.id.txtEsTiburtinaS);
        txtEsCapena = findViewById(R.id.txtEsCapenaS);
        txtEsOstiense = findViewById(R.id.txtEsOstienseS);
        txtEsDep = findViewById(R.id.txtEsDepS);
        txtOFSestu = findViewById(R.id.txtOFSestuS);
        txtOFMarconi = findViewById(R.id.txtOFMarconiS);
        txtOFPirri = findViewById(R.id.txtOFPirriS);
        txtOFSassari = findViewById(R.id.txtOFSassariS);
        txtOFOlbia = findViewById(R.id.txtOFOlbiaS);
        txtOFNuoro = findViewById(R.id.txtOFNuoroS);
        txtOFOristano = findViewById(R.id.txtOFOristanoS);
        txtOFTortoli = findViewById(R.id.txtOFTortoliS);
        txtOFCarbonia = findViewById(R.id.txtOFCarboniaS);
        txtOFTiburtina = findViewById(R.id.txtOFTiburtinaS);
        txtOFCapena = findViewById(R.id.txtOFCapenaS);
        txtOFOstiense = findViewById(R.id.txtOFOstienseS);
        txtOFDep = findViewById(R.id.txtOFDepS);
        txtOCSestu = findViewById(R.id.txtOCSestuS);
        txtOCMarconi = findViewById(R.id.txtOCMarconiS);
        txtOCPirri = findViewById(R.id.txtOCPirriS);
        txtOCSassari = findViewById(R.id.txtOCSassariS);
        txtOCOlbia = findViewById(R.id.txtOCOlbiaS);
        txtOCNuoro = findViewById(R.id.txtOCNuoroS);
        txtOCOristano = findViewById(R.id.txtOCOristanoS);
        txtOCTortoli = findViewById(R.id.txtOCTortoliS);
        txtOCCarbonia = findViewById(R.id.txtOCCarboniaS);
        txtOCTiburtina = findViewById(R.id.txtOCTiburtinaS);
        txtOCCapena = findViewById(R.id.txtOCCapenaS);
        txtOCOstiense = findViewById(R.id.txtOCOstienseS);
        txtOCDep = findViewById(R.id.txtOCDepS);
        barcodeText = findViewById(R.id.edtTxtCodArtVGS);
        btnVG = findViewById(R.id.btnVGS);
        chkPL = findViewById(R.id.chkPrintLS);
        spnTL = findViewById(R.id.spnTLS);
        rbQU = findViewById(R.id.rbQUnoS);
        rbQEs = findViewById(R.id.rbQEsS);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTL.setAdapter(adapter);

        rbQU.setChecked(true);

        barcodeText.setFocusableInTouchMode(true);
        barcodeText.requestFocus();
        barcodeText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        if(!codArtRec.equals("")){
            barcodeText.setText(codArtRec);
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
        barcodeText.setOnKeyListener((v, keyCode, event) -> {
            svuotaTutto();
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    artOrForn = barcodeText.getText().toString();
                    VerificaGiacenzeSmart.FindArt cercaArt = new VerificaGiacenzeSmart.FindArt();
                    cercaArt.execute("");
                }
            }
            return false;
        });
        Button ricArt = findViewById(R.id.btnRicVGS);
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
                artOrForn = codArtET.getText().toString();
                artOrFornDesc = descArt.getText().toString();
                VerificaGiacenzeSmart.RicArt cercaArt = new VerificaGiacenzeSmart.RicArt();
                cercaArt.execute("");
            });
        });
        btnVG.setOnClickListener(v -> {
            svuotaTutto();
            artOrForn = barcodeText.getText().toString();
            VerificaGiacenzeSmart.FindArt findArt = new VerificaGiacenzeSmart.FindArt();
            findArt.execute();
        });

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,     100);
        surfaceView = findViewById(R.id.surfaceView3);
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
                    if (ActivityCompat.checkSelfPermission(VerificaGiacenzeSmart.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(VerificaGiacenzeSmart.this, new
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAll("");
    }

    public void svuotaTutto(){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificaGiacenzeSmart.this)
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
            default:
                return 1;
        }
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificaGiacenzeSmart.this)
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
        barcodeText.setText("");
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
                        "WHERE Articolo.nome = '" + artOrForn + "' " +
                        "AND ArticoloxListino.idListino = '"+idL+"'" +
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
                barcodeText.setText("");
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
                            "WHERE articolo.nome = '"+artOrForn+"'" +
                            "OR alias.codice = '"+artOrForn+"'";
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
        VerificaGiacenzeSmart.AdapterRicArt whatever = new VerificaGiacenzeSmart.AdapterRicArt(this, codiciR, descrizioniR, eanR, txtSelectedArt);
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
}