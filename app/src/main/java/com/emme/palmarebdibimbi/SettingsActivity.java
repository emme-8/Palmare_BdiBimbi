package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    Context context;
    EditText txtNomePalm;
    String IMEI;
    ConnectionClass connectionClass;
    Spinner spinnerStore, spinnerSPrint;
    EditText txtExternalIp, txtLocalIp, txtPrinterIp, txtEmail, txtEmailPass;
    ImageButton editNP;
    RadioButton rbYes, rbNo, rbLocal, rbExt;
    ArrayList<String> mail;
    ArrayList<String> pass;
    ArrayList<String> store;

    static final Integer REQUEST_CODE = 101;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        connectionClass = new ConnectionClass();

        RecuperaMail recuperaMail = new RecuperaMail();
        recuperaMail.execute();

        IMEI = getDeviceId(context);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        spinnerStore = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        spinnerSPrint = (Spinner) findViewById(R.id.selPrinter);
        ArrayAdapter<CharSequence> adapterP = ArrayAdapter.createFromResource(this, R.array.printers_array, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSPrint.setAdapter(adapterP);

        txtExternalIp = findViewById(R.id.txtExternalIP);
        txtLocalIp = findViewById(R.id.txtLocalIP);
        txtPrinterIp = findViewById(R.id.txtPrinter);
        txtNomePalm = findViewById(R.id.insPalmNome);
        txtEmail = findViewById(R.id.txtEmailSender);
        txtEmailPass = findViewById(R.id.txtPassMail);
        editNP = findViewById(R.id.editNP);
        rbYes = findViewById(R.id.rbYes);
        rbNo = findViewById(R.id.rbNo);
        rbLocal = findViewById(R.id.rbConnLoc);
        rbExt = findViewById(R.id.rbConnExt);

        if(sharedPref.getInt("Store", -1) != -1){
            spinnerStore.setSelection(sharedPref.getInt("Store",-1));
        }
        if(sharedPref.getString("ExternalIp", "") != null){
            txtExternalIp.setText(sharedPref.getString("ExternalIp", ""));
        }
        if(sharedPref.getString("LocalIp", "") != null){
            txtLocalIp.setText(sharedPref.getString("LocalIp", ""));
        }
        if(sharedPref.getString("Email", "") != null){
            txtEmail.setText(sharedPref.getString("Email", ""));
        }
        if(sharedPref.getString("EmailPass", "") != null){
            txtEmailPass.setText(sharedPref.getString("EmailPass", ""));
        }
        if(sharedPref.getString("printer", "") != null){
            if(sharedPref.getString("printer", "").equals("ZEBRA")){
                spinnerSPrint.setSelection(0);
            }else if(sharedPref.getString("printer", "").equals("SATO")){
                spinnerSPrint.setSelection(1);
            }else if(sharedPref.getString("printer", "").equals("ZEBRA NUOVA")){
                spinnerSPrint.setSelection(2);
            }
        }
        if(sharedPref.getString("PrinterIp", "") != null){
            txtPrinterIp.setText(sharedPref.getString("PrinterIp", ""));
        }
        if(sharedPref.getBoolean("Ubicazione", false)){
            rbYes.setChecked(true);
        }else{
            rbNo.setChecked(true);
        }
        if(sharedPref.getBoolean("Connessione", false)){
            rbLocal.setChecked(true);
        }else{
            rbExt.setChecked(true);
        }
        if(sharedPref.getString("NomePalm", "") != null){
            txtNomePalm.setText(sharedPref.getString("NomePalm", ""));
        }

        txtNomePalm.setEnabled(false);
        editNP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticazione("Attenzione!", "Il nome del palmare può essere modificato solo dall'amministratore dell'app");
            }
        });
        Button btnSave = findViewById(R.id.btnSaveImp);
        btnSave.setOnClickListener(v -> {
            Boolean ubicazione;
            Boolean isLocal;
            if(rbYes.isChecked()){
                ubicazione = true;
            }else{
                ubicazione = false;
            }
            if(rbLocal.isChecked()){
                isLocal = true;
            }else{
                isLocal = false;
            }
            setThis(txtLocalIp.getText().toString(), txtExternalIp.getText().toString(), txtPrinterIp.getText().toString(), ubicazione, spinnerStore.getSelectedItemPosition(),
                    txtNomePalm.getText().toString(), spinnerStore.getSelectedItem().toString(), spinnerSPrint.getSelectedItem().toString(),
                    txtEmail.getText().toString(), txtEmailPass.getText().toString(), isLocal);
        });
        Button delAll = findViewById(R.id.btnDelAll);
        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArt("Attenzione!","Scegli la cartella che vuoi svuotare");
            }
        });
        spinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String search = spinnerStore.getItemAtPosition(position).toString();
                for(int i=0; i<store.size(); i++){
                    if(store.get(i).equals(search)){
                        txtEmail.setText(mail.get(i));
                        txtEmailPass.setText(pass.get(i));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FindInfo findInfo = new FindInfo();
        findInfo.execute();
    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
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
            if(username.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                txtNomePalm.setEnabled(true);
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> spinnerArrayDoc = new ArrayList<String>();
        spinnerArrayDoc.add("Tutto");
        spinnerArrayDoc.add("Spunte");
        spinnerArrayDoc.add("Prese");
        spinnerArrayDoc.add("Documenti creati");
        spinnerArrayDoc.add("Inventari");
        Spinner spinnerDoc = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapterDoc = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayDoc);
        spinnerDoc.setAdapter(spinnerArrayAdapterDoc);

        layout.addView(spinnerDoc);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            switch(spinnerDoc.getSelectedItem().toString()){
                case "Tutto":
                    File directory = new File("/storage/emulated/0/NAS/SpuntaGen");
                    File[] files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        if(files[i].getName().contains("spunta")){
                            files[i].delete();
                        }
                    }
                    directory = new File("/storage/emulated/0/NAS/SpuntaImp");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/SpuntaDiff");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/PresaGen");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/PresaDiff");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/PresaImp");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/CreatedDocs");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        files[i].delete();
                    }
                    directory = new File("/storage/emulated/0/NAS/SpuntaGen");
                    files = directory.listFiles();
                    for (int i = 0; i < files.length; i++){
                        if(files[i].getName().contains("inventario")){
                            files[i].delete();
                        }
                    }
                    break;
                case "Spunte":
                    File directorySp1 = new File("/storage/emulated/0/NAS/SpuntaGen");
                    File[] filesSp1 = directorySp1.listFiles();
                    for (int i = 0; i < filesSp1.length; i++){
                        if(filesSp1[i].getName().contains("spunta")){
                            filesSp1[i].delete();
                        }
                    }
                    File directorySp2 = new File("/storage/emulated/0/NAS/SpuntaImp");
                    File[] filesSp2 = directorySp2.listFiles();
                    for (int i = 0; i < filesSp2.length; i++){
                        filesSp2[i].delete();
                    }
                    File directorySp3 = new File("/storage/emulated/0/NAS/SpuntaDiff");
                    File[] filesSp3 = directorySp3.listFiles();
                    for (int i = 0; i < filesSp3.length; i++){
                        filesSp3[i].delete();
                    }
                    break;
                case "Prese":
                    File directoryPr1 = new File("/storage/emulated/0/NAS/PresaGen");
                    File[] filesPr1 = directoryPr1.listFiles();
                    for (int i = 0; i < filesPr1.length; i++){
                        filesPr1[i].delete();
                    }
                    File directoryPr2 = new File("/storage/emulated/0/NAS/PresaDiff");
                    File[] filesPr2 = directoryPr2.listFiles();
                    for (int i = 0; i < filesPr2.length; i++){
                        filesPr2[i].delete();
                    }
                    File directoryPr3 = new File("/storage/emulated/0/NAS/PresaImp");
                    File[] filesPr3 = directoryPr3.listFiles();
                    for (int i = 0; i < filesPr3.length; i++){
                        filesPr3[i].delete();
                    }
                    break;
                case "Documenti creati":
                    File directoryCD = new File("/storage/emulated/0/NAS/CreatedDocs");
                    File[] filesCD = directoryCD.listFiles();
                    for (int i = 0; i < filesCD.length; i++){
                        filesCD[i].delete();
                    }
                    break;
                case "Inventari":
                    File directoryInv = new File("/storage/emulated/0/NAS/SpuntaGen");
                    File[] filesInv = directoryInv.listFiles();
                    for (int i = 0; i < filesInv.length; i++){
                        if(filesInv[i].getName().contains("inventario")){
                            filesInv[i].delete();
                        }
                    }
                    break;
                default:
                    break;
            }
        });builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.cancel();

        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public void setThis(String localIp, String externalIp, String printerIp, Boolean ubicazione, int store, String nomePalm, String storeName, String printer, String email, String emailPass, Boolean isLocal){
        AggPalmInfo aggPalmInfo = new AggPalmInfo();
        aggPalmInfo.execute();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ExternalIp", externalIp);
        editor.putString("LocalIp", localIp);
        editor.putString("NomePalm", nomePalm);
        editor.putString("PrinterIp", printerIp);
        editor.putString("storeName", storeName);
        editor.putString("Email", email.trim());
        editor.putString("EmailPass", emailPass);
        editor.putString("printer", printer);
        editor.putBoolean("Ubicazione", ubicazione);
        editor.putBoolean("Connessione", isLocal);
        editor.putInt("Store", store);
        editor.apply();
        Intent home = new Intent(SettingsActivity.this,MainActivity.class);
        home.putExtra("externalIp",externalIp);
        home.putExtra("localIp",localIp);
        home.putExtra("nomePalm",nomePalm);
        home.putExtra("storeName",storeName);
        home.putExtra("email",email.trim());
        home.putExtra("emailPass",emailPass);
        home.putExtra("printer",printer);
        home.putExtra("printerIp",printerIp);
        home.putExtra("ubicazione",ubicazione);
        home.putExtra("connessione", isLocal);
        home.putExtra("store",store);
        startActivity(home);
        finish();
    }

    public class AggPalmInfo extends AsyncTask<String,String,String> {

        String z = "";
        String info = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                int tipoConn, ubics;
                if(rbYes.isChecked()){
                    ubics = 0;
                }else{
                    ubics = 1;
                }
                if(rbLocal.isChecked()){
                    tipoConn = 0;
                }else{
                    tipoConn = 1;
                }
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "UPDATE mcPalmare " +
                            "SET nome = '"+txtNomePalm.getText().toString()+"', store='"+spinnerStore.getSelectedItem().toString()+"', tipoConn = "+tipoConn+", printerAddress = '"+txtPrinterIp.getText().toString()+"', ubics = "+ubics+" " +
                            "WHERE imei = '" + IMEI + "' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        isSuccess = true;
                        if (res.getString("nome") != null) {
                            txtNomePalm.setText(res.getString("nome"));
                        }
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }
    }

    public class RecuperaMail extends AsyncTask<String,String,String> {

        String z = "";
        String info = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected String doInBackground(String... params) {
            mail = new ArrayList<>();
            pass = new ArrayList<>();
            store = new ArrayList<>();
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select * " +
                            "from mcEmail ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while (res.next()) {
                        isSuccess = true;
                        mail.add(res.getString("email"));
                        pass.add(res.getString("password"));
                        store.add(res.getString("store"));
                    }
                }
            } catch (Exception ex) {
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


        @Override
        protected void onPostExecute(String r) {
            if(isSuccess) {

            }else{
                newPalm();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "select * " +
                            "from mcPalmare " +
                            "where imei = '"+IMEI+"' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if(res.next()){
                        isSuccess=true;
                        if(res.getString("nome")!=null){
                            txtNomePalm.setText(res.getString("nome"));
                        }if(res.getString("store")!=null){
                            switch (res.getString("store")){
                                case "MASTER" :
                                    spinnerStore.setSelection(0);
                                    break;
                                case "MARCONI" :
                                    spinnerStore.setSelection(1);
                                    break;
                                case "PIRRI" :
                                    spinnerStore.setSelection(2);
                                    break;
                                case "SESTU" :
                                    spinnerStore.setSelection(3);
                                    break;
                                case "SASSARI" :
                                    spinnerStore.setSelection(4);
                                    break;
                                case "OLBIA" :
                                    spinnerStore.setSelection(5);
                                    break;
                                case "TORTOLI" :
                                    spinnerStore.setSelection(6);
                                    break;
                                case "CARBONIA" :
                                    spinnerStore.setSelection(7);
                                    break;
                                case "ORISTANO" :
                                    spinnerStore.setSelection(8);
                                    break;
                                case "NUORO" :
                                    spinnerStore.setSelection(9);
                                    break;
                                case "TIBURTINA" :
                                    spinnerStore.setSelection(10);
                                    break;
                                case "CAPENA" :
                                    spinnerStore.setSelection(11);
                                    break;
                                case "OSTIENSE" :
                                    spinnerStore.setSelection(12);
                                    break;
                                case "IN LAVORAZIONE" :
                                    spinnerStore.setSelection(13);
                                    break;
                                case "CASILINA" :
                                    spinnerStore.setSelection(14);
                                    break;
                                case "POMEZIA" :
                                    spinnerStore.setSelection(19);
                                    break;
                                case "ARDEATINA" :
                                    spinnerStore.setSelection(21);
                                    break;
                                case "VERONA" :
                                    spinnerStore.setSelection(22);
                                    break;
                                case "INTRANSITO" :
                                    spinnerStore.setSelection(15);
                                    break;
                                case "INTEMPORANEO" :
                                    spinnerStore.setSelection(16);
                                    break;
                                case "MasterMagRoma" :
                                    spinnerStore.setSelection(17);
                                    break;
                                case "CEDIROMAINLAV" :
                                    spinnerStore.setSelection(18);
                                    break;
                                case "ROMACEDI" :
                                    spinnerStore.setSelection(20);
                                    break;
                            }if(res.getString("printerAddress")!=null){
                                txtPrinterIp.setText(res.getString("printerAddress"));
                            }if(res.getString("tipoConn")!=null){
                                if(res.getInt("tipoConn")==0){
                                    rbLocal.setChecked(true);
                                    rbExt.setChecked(false);
                                }else{
                                    rbLocal.setChecked(false);
                                    rbExt.setChecked(true);
                                }
                            }if(res.getString("ubics")!=null){
                                if(res.getInt("ubics")==0){
                                    rbYes.setChecked(true);
                                    rbNo.setChecked(false);
                                }else{
                                    rbYes.setChecked(false);
                                    rbNo.setChecked(true);
                                }
                            }
                        }
                    }else{
                        isSuccess=false;
                    }
                }
            }
            catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

        protected void newPalm() {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "INSERT INTO mcPalmare(imei) \n" +
                            "VALUES('"+IMEI+"') " ;
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);

                }
            }
            catch (Exception ex) {
                isSuccess = false;
            }
        }

    }

}