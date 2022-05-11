package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.File;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        Spinner spinnerStore = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        Spinner spinnerSPrint = (Spinner) findViewById(R.id.selPrinter);
        ArrayAdapter<CharSequence> adapterP = ArrayAdapter.createFromResource(this, R.array.printers_array, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSPrint.setAdapter(adapterP);

        EditText txtExternalIp = findViewById(R.id.txtExternalIP);
        EditText txtLocalIp = findViewById(R.id.txtLocalIP);
        EditText txtPrinterIp = findViewById(R.id.txtPrinter);
        EditText txtNomePalm = findViewById(R.id.insPalmNome);
        EditText txtEmail = findViewById(R.id.txtEmailSender);
        EditText txtEmailPass = findViewById(R.id.txtPassMail);
        RadioButton rbYes = findViewById(R.id.rbYes);
        RadioButton rbNo = findViewById(R.id.rbNo);
        RadioButton rbLocal = findViewById(R.id.rbConnLoc);
        RadioButton rbExt = findViewById(R.id.rbConnExt);
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

}