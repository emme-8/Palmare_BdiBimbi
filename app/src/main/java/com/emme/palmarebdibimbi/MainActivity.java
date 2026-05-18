package com.emme.palmarebdibimbi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zebra.sdk.comm.BluetoothConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Articolo> artDoc = new ArrayList<>();
    ConnectionClass connectionClass;
    Intent dest;
    Context context;
    ProgressBar pbDownload;
    String username = "";
    String password = "";
    String nomeP;
    TextView txtProgress;
    SharedPreferences sharedPref;
    int mag4Ric = 0;
    int versione = 172;
    String ipNeg = "", nScontrino = "";
    Switch smartMode;
    ArrayList<String> serie;
    int idL = 0;
    com.zebra.sdk.comm.Connection connection;

    // Data dell'ultimo aggiornamento degli articoli: modificare prima di ogni build
    // Se oggi è successivo a questa data, l'importazione parte automaticamente all'avvio
    static final String DATA_AGG_ART = "2025-01-01";

    View loadingOverlay;
    TextView txtOverlayProgress;

    final static int APP_STORAGE_ACCESS_REQUEST_CODE = 501;

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        smartMode = findViewById(R.id.switch1);

        pbDownload = findViewById(R.id.pbDownload);
        pbDownload.setVisibility(View.GONE);

        context = this;
        connectionClass = new ConnectionClass();

        ImageButton btnLogUser = findViewById(R.id.btnLogUser);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(!Environment.isExternalStorageManager()){
                try {
                    Uri uri = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                    startActivity(intent);
                } catch (Exception ex){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        }

        Button stp2 = findViewById(R.id.button2);
        stp2.setOnClickListener(v -> showPasswordDialog());
        Button btnCheckIntegrity = findViewById(R.id.btnCheckPromo);
        btnCheckIntegrity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIntegrity check = new CheckIntegrity();
                check.execute();
            }
        });

        MyApplication app = (MyApplication)getApplication();

        Bundle extras = getIntent().getExtras();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        RadioButton rbOn = findViewById(R.id.rbOn);
        RadioButton rbOff = findViewById(R.id.rbOff);
        Button btnImp = findViewById(R.id.btnImp);
        Button btnInv = findViewById(R.id.btnInv);
        Button btnPresa = findViewById(R.id.btnPresa);
        Button btnSpunto = findViewById(R.id.btnSpunta);
        Button btnSpuntaWeb = findViewById(R.id.btnSpuntaWEB);
        Button btnStampa = findViewById(R.id.btnStampaEtichetta);
        Button btnCreaDoc = findViewById(R.id.btnCreaDoc);
        Button btnPrintFromDoc = findViewById(R.id.btnPDD);
        Button btnGestFile = findViewById(R.id.btnGestFile);
        Button btnGScorte = findViewById(R.id.btnGScorte);
        Button btnGestGift = findViewById(R.id.btnGestGift);
        ImageButton btnInfo = findViewById(R.id.btnInfoVers);
        Button btnSRA = findViewById(R.id.btnSRA);
        Button inv2026 = findViewById(R.id.btnInv2026);
        txtProgress = findViewById(R.id.txtProgress);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        txtOverlayProgress = findViewById(R.id.txtOverlayProgress);

        if(sharedPref.getBoolean("smartMode", false)){
            smartMode.setChecked(false);
        }else{
            smartMode.setChecked(false);
        }
        smartMode.setChecked(false);

        if(sharedPref.getBoolean("isOnline", true)){
            rbOn.setChecked(true);
        }else{
            rbOff.setChecked(true);
        }
        if(extras != null) {
            editor.putString("ExternalIp", extras.getString("externalIp"));
            editor.putString("LocalIp", extras.getString("localIp"));
            editor.putString("PrinterIp", extras.getString("printerIp"));
            editor.putString("NomePalm", extras.getString("nomePalm"));
            editor.putString("Email", extras.getString("email"));
            editor.putString("EmailPass", extras.getString("emailPass"));
            editor.putString("storeName", extras.getString("storeName"));
            editor.putString("printer", extras.getString("printer"));
            editor.putBoolean("Ubicazione", extras.getBoolean("ubicazione"));
            editor.putBoolean("Connessione", extras.getBoolean("connessione"));
            editor.putInt("Store", extras.getInt("store"));
            editor.apply();
        }
        rbOn.setChecked(true);
        smartMode.setChecked(false);
        try {
            Files.createDirectories(Paths.get("/storage/emulated/0/Backup"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        serie = new ArrayList<>();
        switch (sharedPref.getString("storeName", "")) {
            case "MASTER":
                ipNeg = "192.168.2.41";
                mag4Ric = 1;
                idL = 1;
                serie.add("1");
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                mag4Ric = 77;
                idL = 6;
                serie.add("2");
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                mag4Ric = 35;
                idL = 6;
                serie.add("5");
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                mag4Ric = 72;
                idL = 6;
                serie.add("3");
                break;
            case "OLBIA":
                ipNeg = "192.168.1.10";
                mag4Ric = 76;
                serie.add("4");
                idL = 5;
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                mag4Ric = 74;
                serie.add("9");
                idL = 9;
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                mag4Ric = 32;
                idL = 4;
                serie.add("6");
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                mag4Ric = 78;
                idL = 7;
                serie.add("7");
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                mag4Ric = 75;
                idL = 3;
                serie.add("8");
                break;
            case "ORISTANO":
                ipNeg = "192.168.1.20";
                mag4Ric = 71;
                serie.add("10");
                idL = 8;
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                mag4Ric = 85;
                idL = 3049;
                serie.add("33");
                break;
            case "MasterMagRoma":
                ipNeg = "195.100.100.202";
                mag4Ric = 91;
                idL = 3049;
                serie.add("33");
                break;
            case "CEDIROMAINLAV":
                mag4Ric = 93;
                ipNeg = "192.168.1.20";
                idL = 3054;
                serie.add("38");
                break;
            case "CAPENA":
                ipNeg = "192.168.188.20";
                mag4Ric = 87;
                idL = 3050;
                serie.add("31");
                break;
            case "OSTIENSE":
                ipNeg = "196.100.100.203";
                mag4Ric = 86;
                idL = 3048;
                serie.add("32");
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.2.41";
                mag4Ric = 59;
                idL = 1;
                serie.add("1");
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                mag4Ric = 90;
                idL = 3052;
                serie.add("35");
                break;
            case "POMEZIA":
                ipNeg = "192.168.1.20";
                mag4Ric = 94;
                idL = 3053;
                serie.add("36");
                break;
            case "ROMACEDI":
                ipNeg = "192.168.1.20";
                mag4Ric = 111;
                idL = 3054;
                serie.add("38");
                break;
            case "ARDEATINA":
                ipNeg = "192.168.1.20";
                mag4Ric = 112;
                idL = 3054;
                serie.add("38");
                break;
            case "VERONA":
                ipNeg = "192.168.16.20";
                mag4Ric = 114;
                idL = 3055;
                serie.add("50");
                break;
            case "INTRANSITO":
                ipNeg = "192.168.2.41";
                mag4Ric = 88;
                idL = 1;
                serie.add("1");
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.2.41";
                mag4Ric = 89;
                idL = 1;
                serie.add("1");
                break;
            default:
                mag4Ric = 1;
                break;
        }
        editor.putBoolean("smartMode", false);
        editor.apply();

        btnLogUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dest = new Intent(MainActivity.this,MainActivity.class);
                if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                    verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
                }else{
                    autenticazione("Attenzione!", "Effettua l'accesso per continuare");
                }
            }
        });

        smartMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editor.putBoolean("smartMode", false);
                }else{
                    editor.putBoolean("smartMode", false);
                }
                editor.apply();
            }
        });
        rbOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editor.putBoolean("isOnline", true);
                editor.apply();
            }
        });
        rbOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editor.putBoolean("isOnline", true);
                editor.apply();
            }
        });
        smartMode.setVisibility(View.GONE);
        rbOff.setVisibility(View.GONE);
        rbOn.setVisibility(View.GONE);
        btnCreaDoc.setOnClickListener(v -> {
            if(sharedPref.getBoolean("smartMode", false)){
                dest = new Intent(MainActivity.this, CreaDocumentiSmart.class);
            }else{
                dest = new Intent(MainActivity.this, CreaDocumenti.class);
            }
            dest.putExtra("versione", versione);
            dest.putExtra("storeName", sharedPref.getString("storeName", ""));
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        btnStampa.setOnClickListener(v -> {
            Intent vGiac;
            if(sharedPref.getBoolean("smartMode", false)){
                vGiac = new Intent(MainActivity.this, VerificaGiacenzeSmart.class);
            }else{
                vGiac = new Intent(MainActivity.this, VerificaGiacenze.class);
            }
            vGiac.putExtra("versione", versione);
            vGiac.putExtra("storeName", sharedPref.getString("storeName", ""));
            startActivity(vGiac);
        });
        inv2026.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newInv("Attenzione", "");
            }
        });
        btnPresa.setOnClickListener(v -> {
            dest = new Intent(MainActivity.this,MainSpuntoMerce.class);
            dest.putExtra("store", sharedPref.getInt("Store", 0));
            dest.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            dest.putExtra("tipo", 1);
            dest.putExtra("versione", versione);
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        btnSRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (sharedPref.getString("storeName", "")) {
                    case "MASTER":
                        ipNeg = "192.168.2.41";
                        mag4Ric = 1;
                        idL = 1;
                        break;
                    case "SESTU":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 77;
                        idL = 6;
                        break;
                    case "MARCONI":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 35;
                        idL = 6;
                        break;
                    case "PIRRI":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 72;
                        idL = 6;
                        break;
                    case "OLBIA":
                        ipNeg = "192.168.1.10";
                        mag4Ric = 76;
                        idL = 5;
                        break;
                    case "SASSARI":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 74;
                        idL = 9;
                        break;
                    case "NUORO":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 32;
                        idL = 4;
                        break;
                    case "CARBONIA":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 78;
                        idL = 7;
                        break;
                    case "TORTOLI":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 75;
                        idL = 3;
                        break;
                    case "ORISTANO":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 71;
                        idL = 8;
                        break;
                    case "TIBURTINA":
                        ipNeg = "195.100.100.202";
                        mag4Ric = 85;
                        idL = 3049;
                        break;
                    case "MasterMagRoma":
                        ipNeg = "195.100.100.202";
                        mag4Ric = 91;
                        idL = 3049;
                        break;
                    case "CEDIROMAINLAV":
                        mag4Ric = 93;
                        ipNeg = "192.168.1.20";
                        idL = 3054;
                        break;
                    case "CAPENA":
                        ipNeg = "192.168.188.20";
                        mag4Ric = 87;
                        idL = 3050;
                        break;
                    case "OSTIENSE":
                        ipNeg = "196.100.100.203";
                        mag4Ric = 86;
                        idL = 3048;
                        break;
                    case "IN LAVORAZIONE":
                        ipNeg = "192.168.2.41";
                        mag4Ric = 59;
                        idL = 1;
                        break;
                    case "CASILINA":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 90;
                        idL = 3052;
                        break;
                    case "POMEZIA":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 94;
                        idL = 3053;
                        break;
                    case "ROMACEDI":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 111;
                        idL = 3054;
                        break;
                    case "ARDEATINA":
                        ipNeg = "192.168.1.20";
                        mag4Ric = 112;
                        idL = 3054;
                        break;
                    case "VERONA":
                        ipNeg = "192.168.16.20";
                        mag4Ric = 114;
                        idL = 3055;
                        break;
                    case "INTRANSITO":
                        ipNeg = "192.168.2.41";
                        mag4Ric = 88;
                        idL = 1;
                        break;
                    case "INTEMPORANEO":
                        ipNeg = "192.168.2.41";
                        mag4Ric = 89;
                        idL = 1;
                        break;
                    default:
                        mag4Ric = 1;
                        break;
                }
                alertSpuntaScontrino("Attenzione!", "Scansiona il codice a barre presente nello scontrino");
            }
        });
        btnGScorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scorte = new Intent(MainActivity.this, GestionScorteUbic.class);
                scorte.putExtra("versione", versione);
                scorte.putExtra("storeName", sharedPref.getString("storeName", ""));
                startActivity(scorte);
            }
        });
        btnInv.setOnClickListener(v -> {
            dest = new Intent(MainActivity.this,HomeInventario.class);
            dest.putExtra("storeName", sharedPref.getString("storeName", ""));
            dest.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            dest.putExtra("store", sharedPref.getInt("Store", 0));
            dest.putExtra("tipo", 2);
            dest.putExtra("versione", versione);
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        btnPrintFromDoc.setOnClickListener(v -> {
            Intent pdd = new Intent(MainActivity.this,MainSpuntoMerce.class);
            pdd.putExtra("store", sharedPref.getInt("Store", 0));
            pdd.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            pdd.putExtra("tipo", 3);
            pdd.putExtra("versione", versione);
            startActivity(pdd);
        });
        btnSpunto.setOnClickListener(v -> {
            dest = new Intent(MainActivity.this,MainSpuntoMerce.class);
            dest.putExtra("store", sharedPref.getInt("Store", 0));
            dest.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            dest.putExtra("tipo", 0);
            dest.putExtra("versione", versione);
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        btnSpuntaWeb.setOnClickListener(v -> {
            dest = new Intent(MainActivity.this,MainSpuntaWEB.class);
            dest.putExtra("store", sharedPref.getInt("Store", 0));
            dest.putExtra("ubicazione", false);
            dest.putExtra("tipo", 0);
            dest.putExtra("versione", versione);
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        btnImp.setOnClickListener(v -> {
            Intent imp = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(imp);
        });
        btnGestGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gestGC = new Intent(MainActivity.this,GestioneGiftCard.class);
                gestGC.putExtra("storeName", sharedPref.getString("storeName", ""));
                startActivity(gestGC);
            }
        });
        btnGestFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dest = new Intent(MainActivity.this,ListDocsActivity.class);
                alertArt("Attenzione!", "Scegli il tipo di file da visualizzare");
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.FindInfo info = new MainActivity.FindInfo();
                info.execute();
            }
        });

        File directory = new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaGen");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaDiff");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/SpuntaImp");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/PresaGen");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/PresaDiff");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/PresaImp");
        deleteOldFiles(directory);
        directory = new File(Environment.getExternalStorageDirectory(), "NAS/CreatedDocs");
        deleteOldFiles(directory);

        // Controlla versione e poi avvia auto-import articoli se necessario
        new CheckVersione().execute();

        // Controlla documenti completati non ancora inviati via email
        try {
            List<SpuntaDocumentoEntity> nonInviati = AppDb.getInstance(this).spuntaDao().getDocumentiCompletatiNonInviati();
            if (nonInviati != null && !nonInviati.isEmpty()) {
                StringBuilder docNames = new StringBuilder();
                for (SpuntaDocumentoEntity d : nonInviati) {
                    docNames.append("• ").append(d.fileName).append("\n");
                }
                new AlertDialog.Builder(this)
                    .setTitle("Documenti non inviati")
                    .setMessage("Sono presenti " + nonInviati.size() + " documento/i completato/i non ancora inviato/i:\n\n" + docNames.toString())
                    .setPositiveButton("Invia ora", (dialog, which) -> {
                        new Thread(() -> {
                            String emailAddr = sharedPref.getString("Email", "");
                            String emailPass = sharedPref.getString("EmailPass", "");
                            for (SpuntaDocumentoEntity doc : nonInviati) {
                                try {
                                    ArrayList<String> paths = new ArrayList<>();
                                    paths.add("/storage/emulated/0/NAS/SpuntaGen/" + doc.fileName);
                                    File diffDir = new File("/storage/emulated/0/NAS/SpuntaDiff");
                                    File[] diffFiles = diffDir.listFiles();
                                    if (diffFiles != null) {
                                        for (File f : diffFiles) {
                                            if (f.getName().endsWith("_" + doc.fileName)) {
                                                paths.add(f.getAbsolutePath());
                                                break;
                                            }
                                        }
                                    }
                                    String[] to = new String[]{"spunte@bdibimbi.it", emailAddr};
                                    AdapterReviewSpunta.sendEmail(to, emailAddr, doc.fileName, " ", paths, emailAddr, emailPass);
                                    AdapterReviewSpunta.salvaSuServer(getApplicationContext(), doc.id, doc.fileName);
                                    AppDb.getInstance(getApplicationContext()).spuntaDao().marcaEmailInviata(doc.id);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();
                    })
                    .setNegativeButton("Rimanda", (dialog, which) -> dialog.cancel())
                    .create()
                    .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autenticazione(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText username = new EditText(this);
        username.setHint("Nome utente");
        layout.addView(username);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            this.username = username.getText().toString();
            this.password = password.getText().toString();

            MainActivity.LogInOp logIn = new MainActivity.LogInOp();
            logIn.execute();
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void newInv(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText ubica = new EditText(this);
        ubica.setHint("Ubicazione");
        layout.addView(ubica);

        TextView vuoto = new TextView(context);
        layout.addView(vuoto);

        Spinner splist = new Spinner(context);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.splist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splist.setAdapter(adapter);
        layout.addView(splist);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(ubica.getText().length()==0 || splist.getSelectedItem().toString().equals("Numero sparata")){
                dialog.cancel();
                newInv("Attenzione!", "Inserisci tutti i campi");
            }else {
                dialog.cancel();
                Intent inventario2026 = new Intent(MainActivity.this, NewInvActivity.class);
                inventario2026.putExtra("storeName", sharedPref.getString("storeName", ""));
                inventario2026.putExtra("ubica", ubica.getText().toString());
                inventario2026.putExtra("nsp", splist.getSelectedItem().toString());
                startActivity(inventario2026);
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void verificaAutenticazione(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);
        builder.setNegativeButton("Cambia", (dialog, which) -> {
            dialog.cancel();
            autenticazione("Attenzione!", "Accedi per continuare");
        });
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.cancel();
            dest.putExtra("utente", sharedPref.getString("loggedAccount","NotLoggedIn"));
            startActivity(dest);
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void deleteOldFiles(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                long currentTime = System.currentTimeMillis();
                long oneMonthMillis = 30L * 24 * 60 * 60 * 1000;

                for (File file : files) {
                    long lastModifiedTime = file.lastModified();
                    if (currentTime - lastModifiedTime > oneMonthMillis) {
                        // Elimina il file se più vecchio di un mese
                        if (file.delete()) {
                            Log.d("FileDelete", "Deleted: " + file.getName());
                        } else {
                            Log.e("FileDelete", "Failed to delete: " + file.getName());
                        }
                    }
                }
            }
        }
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("File spunta");
        spinnerArrayTipo.add("File presa");
        spinnerArrayTipo.add("File creati");
        spinnerArrayTipo.add("File inventario");
        Spinner spinnerTipo = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        layout.addView(spinnerTipo);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            int tipo=0;
            if(spinnerTipo.getSelectedItem().toString().equals("File spunta")){
                tipo=0;
            }else if(spinnerTipo.getSelectedItem().toString().equals("File presa")){
                tipo=1;
            }else if(spinnerTipo.getSelectedItem().toString().equals("File creati")){
                tipo=2;
            }else if(spinnerTipo.getSelectedItem().toString().equals("File inventario")){
                tipo=3;
            }

            dest.putExtra("store", sharedPref.getInt("Store", 0));
            dest.putExtra("ubicazione", sharedPref.getBoolean("Ubicazione", true));
            dest.putExtra("mag", mag4Ric);
            dest.putExtra("listino", idL);
            dest.putExtra("tipo", tipo);
            if(!sharedPref.getString("loggedAccount","").equals("")&&!sharedPref.getString("loggedAccount","").equals("NotLoggedIn")){
                verificaAutenticazione("Attenzione!", "L'account attualmente connesso è:\n\n" + sharedPref.getString("loggedAccount","") + "\n\nVuoi continuare con questo account?");
            }else{
                autenticazione("Attenzione!", "Effettua l'accesso per continuare");
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void showPasswordDialog() {

        final EditText input = new EditText(this);
        input.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT |
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Autorizzazione richiesta")
                .setMessage("Inserisci la password per avviare l'importazione")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                .setPositiveButton("OK", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(v -> {
                String pwd = input.getText().toString();

                if ("invbdibimbi!".equals(pwd)) {
                    dialog.dismiss();
                    startImport();
                } else {
                    input.setError("Password errata");
                }
            });
        });

        dialog.show();
    }

    private void startImport() {
        FindRowsOffline find = new FindRowsOffline();
        find.execute();
    }

    private void aggiornaPalmare(String title, String message, String link) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("ANNULLA", (dialog, which) -> dialog.cancel())
                .setPositiveButton("SCARICA E INSTALLA", (dialog, which) -> {
                    dialog.cancel();
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
        builder.create().show();
    }

    public class CheckVersione extends AsyncTask<String, String, String> {

        String z = "";
        int versioneServer = -1;
        String linkApp = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con != null) {
                    String query = "SELECT versioneApp, linkApp FROM mcInfoBdiBimbi";
                    Statement stmt = con.createStatement();
                    ResultSet res = stmt.executeQuery(query);
                    if (res.next()) {
                        versioneServer = res.getInt("versioneApp");
                        linkApp = res.getString("linkApp");
                    }
                    con.close();
                }
            } catch (Exception ex) {
                z = "Errore";
            }
            return z;
        }

        @Override
        protected void onPostExecute(String r) {
            if (versioneServer > 0 && versioneServer != versione) {
                aggiornaPalmare("Attenzione!",
                        "Stai utilizzando una versione non aggiornata dell'app, scarica e installa l'aggiornamento per utilizzare tutte le ultime funzionalità",
                        linkApp);
            }
            // Avvia auto-import articoli se non ancora fatto oggi oppure se la versione è cambiata
            try {
                java.time.LocalDate oggi = java.time.LocalDate.now();
                String lastImportStr = sharedPref.getString("lastImportDate", "2000-01-01");
                java.time.LocalDate ultimaImport = java.time.LocalDate.parse(lastImportStr);
                int lastImportVersion = sharedPref.getInt("lastImportVersion", -1);
                if (oggi.isAfter(ultimaImport) || lastImportVersion != versione) {
                    startImport();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class CheckIntegrity extends AsyncTask<String,String,String> {

        String z = "";
        Boolean isSuccess = false;
        ArrayList<String> codArtSede = new ArrayList<>();
        ArrayList<String> codArtNeg = new ArrayList<>();
        ArrayList<String> przSede = new ArrayList<>();
        ArrayList<String> przNeg = new ArrayList<>();
        int promoSede = 0;
        int promoNeg = 0;
        ResultSet res;

        @Override
        protected void onPreExecute(){
            pbDownload.setVisibility(View.VISIBLE);
        }

        private void fine(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                keepFromNeg();
                boolean different = false;
                for(int i=0; i<codArtSede.size(); i++){
                    int j=0;
                    do{
                        if(!przSede.get(i).equals(przNeg.get(j))){
                            different = true;
                        }
                        if(j<codArtNeg.size()-1){
                            j++;
                        }
                    }while(!codArtSede.get(i).equals(codArtNeg.get(j)) || j==codArtNeg.size()-1);
                }
                promoFromNeg();
                promoFromSede();
                pbDownload.setVisibility(View.GONE);
                if(different && promoNeg!=promoSede){
                    fine("Attenzione!", "\nListini: X \n\nPromozioni: X");
                }else if(different && promoNeg==promoSede){
                    fine("Attenzione!", "\nListini: X \n\nPromozioni: OK");
                }else if(!different && promoNeg!=promoSede){
                    fine("Attenzione!", "\nListini: OK \n\nPromozioni: X");
                }else{
                    fine("Attenzione!", "\nListini: OK \n\nPromozioni: OK");
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome, articoloxlistino.prezzo " +
                            "from articolo join articoloxlistino on (articoloxlistino.idArticolo = articolo.id) " +
                            "where articoloxlistino.idlistino = "+idL+" and cast(articoloxlistino.dataCreazione as date) > DATEADD(day, -2, CAST(GETDATE() AS date)) " +
                            "order by articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        codArtSede.add(res.getString("nome"));
                        przSede.add(res.getString("prezzo"));
                    }
                    isSuccess = true;
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

        public void promoFromSede(){
                    try {
            Connection con = connectionClass.CONN(context);
            if (con == null) {
                z = "Errore di connessione con il server";
            } else {
                String query = "select count(id) as totPromo from promozione " +
                        "where cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE()";
                Statement stmt = con.createStatement();
                res = stmt.executeQuery(query);
                while(res.next()){
                    promoSede = res.getInt("totPromo");
                }
            }
        }
            catch (Exception ex) {
            isSuccess = false;
            z = "Errore";
        }
    }

        public void promoFromNeg(){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connection con = null;
                String ConnURL;

                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    ConnURL = "jdbc:jtds:sqlserver://"+ipNeg+"/PassepartoutRetail;user=sa;password=SaSqlPass*01;";
                    con = DriverManager.getConnection(ConnURL);

                }catch (SQLException se){
                    Log.e("error here 1 : ", se.getMessage());
                }
                catch (ClassNotFoundException e){
                    Log.e("error here 2 : ", e.getMessage());
                }
                catch (Exception e){
                    Log.e("error here 3 : ", e.getMessage());
                }
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select count(id) as totPromo from promozione " +
                            "where cast(inizioValidita as date) <= GETDATE() and cast(fineValidita as date) >= GETDATE()";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        promoNeg = res.getInt("totPromo");
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
        }

        public void keepFromNeg(){
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connection con = null;
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
                }
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select articolo.nome, articoloxlistino.prezzo " +
                            "from articolo join articoloxlistino on (articoloxlistino.idArticolo = articolo.id) " +
                            "where articoloxlistino.idlistino = "+idL+" and cast(articoloxlistino.dataCreazione as date) > DATEADD(day, -2, CAST(GETDATE() AS date)) " +
                            "order by articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while(res.next()){
                        codArtNeg.add(res.getString("nome"));
                        przNeg.add(res.getString("prezzo"));
                    }
                }
            }
            catch (Exception ex) {
                z = "Errore";
            }
        }
    }

    public class FindRowsOffline extends AsyncTask<Void, Integer, String> {

        String z = "";
        Boolean isSuccess = false;
        private int totalRows = 0;

        ArrayList<ArticoloEntity> articoli;
        ArrayList<ArticoloEanEntity> eans;

        @Override
        protected void onPreExecute() {
            pbDownload.setVisibility(View.VISIBLE);
            txtProgress.setText("Inizio elaborazione...");
            loadingOverlay.setVisibility(View.VISIBLE);
            txtOverlayProgress.setText("Importazione articoli in corso...");
        }

        private void fine(String title, String message) {
            pbDownload.setVisibility(View.GONE);
            loadingOverlay.setVisibility(View.GONE);
            String lastImport = sharedPref.getString("lastImportDate", "");
            txtProgress.setText(lastImport.isEmpty() ? "" : "Articoli aggiornati al: " + lastImport);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {

            txtProgress.setText("Salvataggio in DB locale...");

            if (!isSuccess) {
                pbDownload.setVisibility(View.GONE);
                loadingOverlay.setVisibility(View.GONE);
                txtProgress.setText("");
                fine("Errore!", r);
                return;
            }

            // Salvataggio DB (Room) fuori dal thread UI
            new Thread(() -> {
                try {
                    AppDb db = AppDb.getInstance(context);

                    // replaceAll = svuota e reimporta (come hai richiesto finora)
                    db.articoloDao().replaceAll(articoli, eans);

                    runOnUiThread(() -> {
                        // Salva la data di oggi e la versione corrente come ultima importazione riuscita
                        sharedPref.edit()
                                .putString("lastImportDate", java.time.LocalDate.now().toString())
                                .putInt("lastImportVersion", versione)
                                .apply();
                        fine("Attenzione!",
                                "Importazione articoli completata!\nArticoli: " + articoli.size() + "\nEAN: " + eans.size());
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> fine("Errore!", ex.getMessage() != null ? ex.getMessage() : "Errore DB"));
                }
            }).start();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int val = values[0];
            String msg;
            if (val < 0) {
                // Fase 1: lettura righe grezze da SQL (valore negativo = n° righe lette)
                msg = "Lettura SQL: " + (-val) + " righe";
            } else {
                // Fase 2: elaborazione in memoria (valore positivo = n° articoli elaborati)
                msg = "Elaborazione: " + val + " articoli";
            }
            txtProgress.setText(msg);
            txtOverlayProgress.setText(msg);
        }

        @Override
        protected String doInBackground(Void... params) {

            // ── FASE 1: lettura raw da SQL ────────────────────────────────────────
            // La connessione viene aperta e chiusa nel minor tempo possibile.
            // Si leggono solo stringhe grezze senza nessuna elaborazione.
            ArrayList<String[]> rawRows = new ArrayList<>(80000);

            Connection con = null;
            Statement stmt = null;
            ResultSet res = null;

            try {
                con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                    isSuccess = false;
                    return z;
                }

                String query =
                        "select articolo.nome as codiceArticolo, articolo.descrizione, [alias].codice, cast((inventarioarticoloxmagazzino + caricoarticoloxmagazzino - scaricoarticoloxmagazzino) as int ) as esistenza \n" +
                                "from (articolo left join [alias] on ([alias].idArticolo = articolo.id)) \n" +
                                "left join frwprogmetaarticolo on (articolo.id= frwprogmetaarticolo.MetaArticolo and metamagazzino = " + mag4Ric + " and GETDATE() between da and a) \n" +
                                "order by articolo.nome, [alias].codice";

                stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                res = stmt.executeQuery(query);

                int j = 0;
                while (res.next()) {
                    j++;
                    // Aggiorna UI ogni 500 righe per non saturare il thread UI
                    if (j % 500 == 0) publishProgress(-j);
                    // Legge solo stringhe grezze: nessuna creazione di oggetti Entity
                    rawRows.add(new String[]{
                            res.getString("codiceArticolo"),
                            res.getString("descrizione"),
                            res.getString("codice"),
                            res.getString("esistenza")
                    });
                }

            } catch (Exception ex) {
                isSuccess = false;
                z = (ex.getMessage() != null ? ex.getMessage() : "Errore sconosciuto");
                return z;
            } finally {
                // Connessione SQL chiusa immediatamente dopo l’ultima riga letta
                try { if (res != null) res.close(); } catch (Exception ignored) {}
                try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
                try { if (con != null) con.close(); } catch (Exception ignored) {}
            }

            // ── FASE 2: elaborazione in memoria ──────────────────────────────────
            // Nessuna connessione SQL attiva: crea le Entity e deduplica gli EAN.
            try {
                articoli = new ArrayList<>(30000);
                eans = new ArrayList<>(80000);

                String lastCod = null;
                java.util.HashSet<String> eanSeen = new java.util.HashSet<>(8);

                for (int i = 0; i < rawRows.size(); i++) {
                    if (i % 500 == 0) publishProgress(articoli.size());
                    String[] row = rawRows.get(i);
                    final String codice = row[0];
                    final String descr  = row[1];
                    String barcode      = row[2];
                    final String esStr  = row[3];

                    if (barcode != null) barcode = barcode.trim();
                    if (barcode == null || barcode.isEmpty() || "NULLO".equalsIgnoreCase(barcode)) barcode = null;

                    int es = 0;
                    if (esStr != null) {
                        try { es = (int) Double.parseDouble(esStr); } catch (Exception ignored) {}
                    }

                    if (lastCod == null || !lastCod.equals(codice)) {
                        ArticoloEntity ae = new ArticoloEntity();
                        ae.codArt = codice;
                        ae.desc   = descr;
                        ae.es     = es;
                        articoli.add(ae);
                        lastCod = codice;
                        eanSeen.clear();
                    }

                    if (barcode != null && eanSeen.add(barcode)) {
                        eans.add(new ArticoloEanEntity(codice, barcode));
                    }
                }

                isSuccess = !articoli.isEmpty();
                if (!isSuccess) z = "Nessun articolo trovato";

            } catch (Exception ex) {
                isSuccess = false;
                z = (ex.getMessage() != null ? ex.getMessage() : "Errore elaborazione");
            }

            return z;
        }
    }

    public class CancDoc extends AsyncTask<String,String,String>{
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        @Override
        protected String doInBackground(String... params) {
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
                    String query = "DELETE a" +
                            "FROM mcNewDoc a " +
                            "WHERE palmare = '"+nomeP+"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    private void alertSpuntaScontrino(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message);
        nScontrino = "";

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nSc = new EditText(this);
        nSc.setHint("Numero scontrino");
        nSc.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(nSc);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(nSc.getText().toString().equals("")){
                nSc.setHintTextColor(Color.RED);
                alertArt("Errore!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                dialog.cancel();

                boolean isEnd = false;
                for(int i=0; i< nSc.getText().length(); i++){
                    if(Character.isDigit(nSc.getText().charAt(i)) && !isEnd){
                        nScontrino = nScontrino + nSc.getText().charAt(i);
                    }else{
                        isEnd = true;
                    }
                }

                FindBFOFF findBFOFF = new FindBFOFF();
                findBFOFF.execute();
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public class LogInOp extends AsyncTask<String,String,String>{
        String z = "";
        String info = "";
        Boolean isSuccess = false;
        ResultSet res;

        private void logErr(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("loggedAccount", username);
                editor.apply();
                dest.putExtra("utente", username);
                startActivity(dest);
            }else{
                logErr("Errore!", "Nome utente o password errati");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT * " +
                            "FROM GiftCard_Users " +
                            "WHERE nome like '" + username + "' " +
                            "AND password like '" + password + "' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()){
                        isSuccess=true;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class FindInfo extends AsyncTask<String,String,String> {

        String z = "";
        String info = "";
        Boolean isSuccess = false;
        ResultSet res;

        private void fine(String title,String message){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            android.app.AlertDialog ok = builder.create();
            ok.show();
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                fine("Novità:", info);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select whatsNewOnApp \n" +
                            "from mcInfoBdiBimbi ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()){
                        isSuccess=true;
                        info = res.getString("whatsNewOnApp");
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class FindBFOFF extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        ArrayList<String> id, forn, ndoc, data;

        @Override
        protected void onPreExecute(){
            txtProgress.setText("Connessione al database...");
        }

        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {
                Intent openDoc = new Intent(context, ShowDoc.class);
                openDoc.putExtra("spuntaOrPresa", 0);
                openDoc.putStringArrayListExtra("selIds", id);
                openDoc.putStringArrayListExtra("selDocs", ndoc);
                openDoc.putStringArrayListExtra("serieDocs", serie);
                openDoc.putExtra("tipoRiga", "MetaRigaCorrispettivo");
                openDoc.putExtra("tipoOrd", 0);
                openDoc.putExtra("tipoEs", 1);
                openDoc.putExtra("fornitore", "");
                openDoc.putExtra("listino", idL);
                openDoc.putExtra("ubicazione", false);
                openDoc.putExtra("mag", mag4Ric);
                openDoc.putExtra("listinoRif", idL);
                openDoc.putExtra("magRif", mag4Ric);
                openDoc.putExtra("tipo", "CO");
                openDoc.putExtra("utente", username);
                openDoc.putExtra("magazzino", mag4Ric);
                openDoc.putExtra("ipNeg", ipNeg);
                openDoc.putExtra("isRemoto", false);
                context.startActivity(openDoc);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String processed = values[0];

            txtProgress.setText("Articoli letti: "+processed);
        }

        @Override
        protected String doInBackground(String... params) {
            id = new ArrayList<>();
            forn = new ArrayList<>();
            ndoc = new ArrayList<>();
            data = new ArrayList<>();
            int year = Year.now().getValue();
            int annoSucc = year + 1;
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
                    publishProgress(se.getMessage());
                    Log.e("error here 1 : ", se.getMessage());
                }
                catch (ClassNotFoundException e)
                {
                    publishProgress(e.getMessage());
                    Log.e("error here 2 : ", e.getMessage());
                }
                catch (Exception e)
                {
                    publishProgress(e.getMessage());
                    Log.e("error here 3 : ", e.getMessage());
                } if (con != null) {
                    String query = "select distinct DocumentoCommerciale.id, DocumentoCommerciale.numero, DocumentoCommerciale.data, DocumentoCommerciale.dataCreazione, DocumentoCommerciale.serie\n" +
                            "from Conto left join DocumentoCommerciale on (Conto.idDocumentoCommerciale = DocumentoCommerciale.id) \n" +
                            "join RigaDocumentoCommerciale on (RigaDocumentoCommerciale.idMaster = DocumentoCommerciale.id) \n" +
                            "where DocumentoCommerciale.id is not null and DocumentoCommerciale.identificatore like 'CO%'  \n" +
                            "and RigaDocumentoCommerciale.selettore like 'MetaRigaCorrispettivo'  and DocumentoCommerciale.data between '"+year+"-01-01 00:00:00.000' \n" +
                            "and '"+annoSucc+"-01-01 00:00:00.000'  and Conto.progressivo = "+nScontrino+" and idMagazzinoDestinazione is not null order by DocumentoCommerciale.data desc";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);

                    if(res.next()) {
                        publishProgress("Lettura dei risultati...");
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String currDt = dtf.format(now);
                        id.add(res.getString("id"));
                        ndoc.add(res.getString("numero"));
                        serie.add(res.getString("serie"));
                        forn.add("");
                        String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                        data.add(dt);

/*
                        id.add(res.getString("id"));
                        ndoc.add(res.getString("numero"));
                        serie.add(res.getString("serie"));
                        if(res.getString("ragioneSociale")!=null){
                            forn.add(res.getString("ragioneSociale"));
                        }else{
                            forn.add("");
                        }
                        String dt = res.getString("data").substring(8,10) + "-" + res.getString("data").substring(5,7) + "-" + res.getString("data").substring(0,4);
                        data.add(dt);


 */
                    }else{
                        publishProgress("Nessun risultato trovato");
                    }
                    if (!(id.isEmpty())) {
                        //z = "Ordine trovato";
                        isSuccess = true;
                    }
                }
            }
            catch (Exception ex) {
                String err = ex.getMessage();
                publishProgress(err);
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

}