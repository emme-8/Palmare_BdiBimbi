package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class IniziaSpuntaOffline extends AppCompatActivity implements Serializable {

    ArrayList<Articolo> restoredData;

    ConnectionClass connectionClass;
    ArrayList<String> numDoc = new ArrayList<>();
    ArrayList<String> codici = new ArrayList<>();
    ArrayList<String> descrizioni = new ArrayList<>();
    ArrayList<String> qtaSpunta = new ArrayList<>();
    ArrayList<String> qtaDocum = new ArrayList<>();
    ArrayList<String> numero = new ArrayList<>();
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<Articolo> artDoc = new ArrayList<>();
    ProgressBar pbSearchArt;
    String findThis, ean;
    Spinner spinner;
    String docsName = "";
    CheckBox chkEtic;
    Integer mag, listino;
    TextView qtaDoc, txtCodArt, txtDesc, lblQta, lblColli, txtPrzArt, txtEsSp, txtQtaLP, txtOrdF, txtOrdC;
    Button search, btnNextArt, btnBackArt, fineSpunta;
    EditText txtInsEAN, insNColliSpunta, insQtaSpunta, insQtaSS;
    Context context;
    String tipoDoc;
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
    String printer = "";
    String fornitore ="";
    int nColli = 0;

    public void recuperaStato(Context context) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            docsName = preferences.getString("nameIntOff","");
            artDoc = gson.fromJson(preferences.getString("articoliOff", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void salvaStato(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(artDoc);
        editor.putString("nameIntOff", docsName);
        editor.putString("articoliOff", json);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaOffline.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent exit = new Intent(IniziaSpuntaOffline.this, MainActivity.class);
                    startActivity(exit);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inizia_spunta_offline);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp","");
        printer = preferences.getString("printer", "");
        Gson gson = new Gson();
        restoredData = gson.fromJson(preferences.getString("ListaArticoli", ""), new TypeToken<ArrayList<Articolo>>() {}.getType());

        Bundle extras = getIntent().getExtras();
        int rip = 0;
        if(extras != null){
            listino = extras.getInt("listino");
            mag = extras.getInt("mag");
            rip = extras.getInt("rip");
            fornitore = extras.getString("fornitore");
            tipoDoc = extras.getString("tipoDoc");
            artDoc = (ArrayList<Articolo>) getIntent().getSerializableExtra("MyClass");
        }

        connectionClass = new ConnectionClass();
        context = this;

        spinner = findViewById(R.id.spnEticOFF);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.labels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(rip == 1){
            recuperaStato(this);
        }else{
            String tempnDoc = "";
            ArrayList<String> idXArt = new ArrayList<>();
            ArrayList<String> qtaArtXID = new ArrayList<>();
            ArrayList<String> tempCodArt = new ArrayList<>();
            ArrayList<String> tempNumDoc = new ArrayList<>();
            ArrayList<String> tempDesc = new ArrayList<>();
            for(int i=0; i<artDoc.size(); i++) {
                Boolean ce = false;
                if (i == 0) {
                    idXArt.add(artDoc.get(i).getIdDoc());
                    tempCodArt.add(artDoc.get(i).getCodArt());
                    tempNumDoc.add(artDoc.get(i).getNumDoc());
                    qtaArtXID.add(artDoc.get(i).getQtaDoc().toString());
                    tempDesc.add(artDoc.get(i).getDesc());
                }else{
                    for (int j = 0; j < tempCodArt.size(); j++) {
                        if (tempCodArt.get(j).equals(artDoc.get(i).getCodArt()) && idXArt.get(j).equals(artDoc.get(i).getIdDoc())) {
                            artDoc.get(i).setQtaDoc(artDoc.get(i).getQtaDoc() + Integer.parseInt(qtaArtXID.get(j)));
                            artDoc.remove(j);
                            ce = true;
                        }
                    }
                    if (!ce) {
                        idXArt.add(artDoc.get(i).getIdDoc());
                        tempCodArt.add(artDoc.get(i).getCodArt());
                        tempNumDoc.add(artDoc.get(i).getNumDoc());
                        qtaArtXID.add(artDoc.get(i).getQtaDoc().toString());
                        tempDesc.add(artDoc.get(i).getDesc());
                    }
                }
            }

            int z;
            for(z = 0; z<artDoc.size(); z++){
                if(z==0){
                    docsName = tipoDoc + fornitore + artDoc.get(z).getNumDoc();
                }else if(!artDoc.get(z).getNumDoc().equals(tempnDoc)){
                    docsName = docsName + "&" + artDoc.get(z).getNumDoc();
                }
                tempnDoc = artDoc.get(z).getNumDoc();
            }
        }

        chkEtic = findViewById(R.id.chkEticOFF);
        insQtaSS = findViewById(R.id.insQtaSSOFF);
        rbSS = findViewById(R.id.rbSSOFF);
        rbSM = findViewById(R.id.rbSMOFF);
        search = findViewById(R.id.btnSearchThisArtNegOFF);
        qtaDoc = findViewById(R.id.txtQtaDocNegOFF);
        txtCodArt = findViewById(R.id.txtShowCodArtNegOFF);
        txtDesc = findViewById(R.id.txtShowDescNegOFF);
        txtInsEAN = findViewById(R.id.txtInsEANNegOFF);
        insNColliSpunta = findViewById(R.id.insNColliSpuntaNegOFF);
        insQtaSpunta = findViewById(R.id.insQtaSpuntaNegOFF);
        btnNextArt = findViewById(R.id.btnNextArtNegOFF);
        btnBackArt = findViewById(R.id.btnBackArtNegOFF);
        lblColli = findViewById(R.id.lblNCNegOFF);
        lblQta = findViewById(R.id.lblQXCNegOFF);
        txtEsSp = findViewById(R.id.txtEsSpNegOFF);
        txtPrzArt = findViewById(R.id.txtPrzArtNegOFF);
        txtQtaLP = findViewById(R.id.txtQtaLPNegOFF);
        txtOrdF = findViewById(R.id.txtOrdFNegOFF);
        txtOrdC = findViewById(R.id.txtOrdCNegOFF);
        pbSearchArt = findViewById(R.id.pbSearchArtNegOFF);
        fineSpunta = findViewById(R.id.btnFineSpuntaOFF);

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

        txtInsEAN.setFocusableInTouchMode(true);
        txtInsEAN.requestFocus();
        txtInsEAN.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

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
        txtInsEAN.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                if(giaPremuto == 0){
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    findThis = txtInsEAN.getText().toString();
                    if(!findThis.equals("")){
                        if(rbSS.isChecked()){
                            findArtSS(findThis);
                        }else{
                            findArtSM(findThis);
                        }

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
            }else if(!insQtaSpunta.isEnabled() && insQtaSpunta.getVisibility() == View.VISIBLE){
                alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
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
                txtInsEAN.setEnabled(true);
                btnBackArt.setVisibility(View.GONE);
                btnNextArt.setVisibility(View.GONE);
            }else if(!insQtaSpunta.isEnabled() && insQtaSpunta.getVisibility() == View.VISIBLE){
                insQtaSpunta.setEnabled(true);
            }
        });
        fineSpunta.setOnClickListener(v -> {
            alertConcludiSpunta("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
        });
        search.setOnClickListener(v -> {
            findThis = txtInsEAN.getText().toString();
            if(rbSS.isChecked()){
                findArtSS(findThis);
            }else{
                findArtSM(findThis);
            }
        });
    }

    private void findArtSS(String codEAN){
        pbSearchArt.setVisibility(View.VISIBLE);
        boolean presente = false;
        for(int i=0;i<artDoc.size();i++){
            Articolo questoArt = artDoc.get(i);
            for(int j=0;j<questoArt.getAllEan().size();j++){
                if(questoArt.getEan(j).equals(codEAN) || questoArt.getCodArt().equals(codEAN)){
                    presente = true;
                    txtCodArt.setText(questoArt.getCodArt());
                    txtDesc.setText(questoArt.getDesc());
                    txtOrdF.setText(questoArt.getOf().toString());
                    txtOrdC.setText(questoArt.getOc().toString());
                    txtEsSp.setText(questoArt.getEs().toString());
                    txtPrzArt.setText("€ " + questoArt.getPrz().toString());
                    txtQtaLP.setText(questoArt.getQtaLetta().toString());
                    qtaDoc.setText(questoArt.getQtaDoc().toString());
                }
            }
        }
        if(presente){
            txtInsEAN.setEnabled(false);
            Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
            Integer lastIndex = -1;
            for(int i=0; i<artDoc.size(); i++){
                Articolo questoArt = artDoc.get(i);
                if(questoArt.getCodArt().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                    lastIndex = i;
                    if(questoArt.getQtaDaScal()>qtaSpuntata && qtaSpuntata>0){
                        Integer qtaRimasta = questoArt.getQtaDaScal()-qtaSpuntata;
                        Integer qtaParzialeSpunta = questoArt.getQtaLetta() + qtaSpuntata;
                        questoArt.setQtaLetta(qtaParzialeSpunta);
                        qtaSpuntata = 0;
                        questoArt.setQtaDaScal(qtaRimasta);
                    }else if(questoArt.getQtaDaScal()==0 && qtaSpuntata>0){

                    }else if(questoArt.getQtaDaScal()<qtaSpuntata && qtaSpuntata>0){
                        qtaSpuntata = qtaSpuntata - questoArt.getQtaDaScal();
                        Integer qtaParzialeSpunta = questoArt.getQtaLetta() + questoArt.getQtaDaScal();
                        questoArt.setQtaDaScal(0);
                        questoArt.setQtaLetta(qtaParzialeSpunta);
                    }
                }
            }
            if(qtaSpuntata>0 && lastIndex != -1){
                Integer qtaUltima = qtaSpuntata + artDoc.get(lastIndex).getQtaLetta();
                artDoc.get(lastIndex).setQtaLetta(qtaUltima);
            }else if(qtaSpuntata>0 && lastIndex == -1){
                ArrayList <String> eans = new ArrayList<>();
                eans.add(txtInsEAN.getText().toString());
                artDoc.add(new Articolo(txtCodArt.getText().toString(), txtDesc.getText().toString(), 0, 0, qtaSpuntata, 0, 0, eans, 0.0, "", ""));
            }
            if(chkEtic.isChecked()){
                BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                        txtPrzArt.getText().toString(), txtPrzArt.getText().toString(), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, Integer.parseInt(insQtaSS.getText().toString()));
                print.main();
            }
            giaPremuto = 0;
            txtInsEAN.setEnabled(true);
            txtInsEAN.setText("");
            insQtaSS.setText("1");
            txtInsEAN.setFocusableInTouchMode(true);
            txtInsEAN.requestFocus();
            salvaStato();
        }else{
            articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
        }
        pbSearchArt.setVisibility(View.GONE);
    }

    private void findArtSM(String codEAN){
        pbSearchArt.setVisibility(View.VISIBLE);
        txtInsEAN.setEnabled(false);
        boolean presente = false;
        for(int i=0;i<artDoc.size();i++){
            Articolo questoArt = artDoc.get(i);
            for(int j=0;j<questoArt.getAllEan().size();j++){
                if(questoArt.getEan(j).equals(codEAN) || questoArt.getCodArt().equals(codEAN)){
                    presente = true;
                    txtCodArt.setText(questoArt.getCodArt());
                    txtDesc.setText(questoArt.getDesc());
                    txtOrdF.setText(questoArt.getOf().toString());
                    txtOrdC.setText(questoArt.getOc().toString());
                    txtEsSp.setText(questoArt.getEs().toString());
                    txtPrzArt.setText("€ " + questoArt.getPrz().toString());
                    txtQtaLP.setText(questoArt.getQtaLetta().toString());
                    qtaDoc.setText(questoArt.getQtaDoc().toString());
                }
            }
        }
        if(presente){
            btnNextArt.setVisibility(View.VISIBLE);
            btnBackArt.setVisibility(View.VISIBLE);
            insNColliSpunta.setVisibility(View.VISIBLE);
            insNColliSpunta.setEnabled(true);
            lblColli.setVisibility(View.VISIBLE);
            insQtaSpunta.setVisibility(View.VISIBLE);
            lblQta.setVisibility(View.VISIBLE);
        }else{
            articoloNonTrovato("Attenzione!", "Articolo non presente nel documento, aggiungere comunque?");
        }
        pbSearchArt.setVisibility(View.GONE);
    }

    private void alertDisplayer2(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaOffline.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer3("Attenzione!", "Sei sicuro di voler concludere la spunta e salvare il documento?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    pbSearchArt.setVisibility(View.VISIBLE);
                    boolean presente = false;
                    for(int i=0;i<artDoc.size();i++){
                        Articolo questoArt = artDoc.get(i);
                        for(int j=0;j<questoArt.getAllEan().size();j++){
                            if(questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)){
                                presente = true;
                                txtCodArt.setText(questoArt.getCodArt());
                                txtDesc.setText(questoArt.getDesc());
                                txtOrdF.setText(questoArt.getOf().toString());
                                txtOrdC.setText(questoArt.getOc().toString());
                                txtEsSp.setText(questoArt.getEs().toString());
                                txtPrzArt.setText("€ " + questoArt.getPrz().toString());
                                txtQtaLP.setText(questoArt.getQtaLetta().toString());
                                qtaDoc.setText(questoArt.getQtaDoc().toString());
                            }
                        }
                    }
                    if(presente){
                        Integer qtaSpuntata;
                        if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                            qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                        }else{
                            qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                        }
                        Integer qtaEtic = qtaSpuntata;
                        Integer lastIndex = -1;
                        txtInsEAN.setEnabled(false);
                        for(int i=0; i<artDoc.size(); i++){
                            Articolo questoArt = artDoc.get(i);
                            if(questoArt.getCodArt().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                                lastIndex = i;
                                if(questoArt.getQtaDaScal()>qtaSpuntata && qtaSpuntata>0){
                                    Integer qtaRimasta = questoArt.getQtaDaScal()-qtaSpuntata;
                                    Integer qtaParzialeSpunta = questoArt.getQtaLetta() + qtaSpuntata;
                                    questoArt.setQtaLetta(qtaParzialeSpunta);
                                    qtaSpuntata = 0;
                                    questoArt.setQtaDaScal(qtaRimasta);
                                }else if(questoArt.getQtaDaScal()==0 && qtaSpuntata>0){

                                }else if(questoArt.getQtaDaScal()<qtaSpuntata && qtaSpuntata>0){
                                    qtaSpuntata = qtaSpuntata - questoArt.getQtaDaScal();
                                    Integer qtaParzialeSpunta = questoArt.getQtaLetta() + questoArt.getQtaDaScal();
                                    questoArt.setQtaDaScal(0);
                                    questoArt.setQtaLetta(qtaParzialeSpunta);
                                }
                            }
                        }
                        if(qtaSpuntata>0 && lastIndex != -1){
                            Integer qtaUltima = qtaSpuntata + artDoc.get(lastIndex).getQtaLetta();
                            artDoc.get(lastIndex).setQtaLetta(qtaUltima);
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            ArrayList <String> eans = new ArrayList<>();
                            eans.add(txtCodArt.getText().toString());
                            artDoc.add(new Articolo(txtCodArt.getText().toString(), txtDesc.getText().toString(), 0, 0, qtaSpuntata, 0, 0, eans, 0.0, "", ""));
                        }
                        if(chkEtic.isChecked()){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString(), txtPrzArt.getText().toString(), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, Integer.parseInt(insQtaSS.getText().toString()));
                            print.main();
                        }
                        txtInsEAN.setEnabled(true);
                        txtInsEAN.setText("");
                        insQtaSS.setText("1");
                        txtInsEAN.setFocusableInTouchMode(true);
                        txtInsEAN.requestFocus();
                    }else{
                        int index = 0;
                        for(int i=0;i<restoredData.size();i++){
                            Articolo questoArt = restoredData.get(i);
                            for(int j=0;j<questoArt.getAllEan().size();j++){
                                if(questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)){
                                    presente = true;
                                    index = i;
                                    txtCodArt.setText(questoArt.getCodArt());
                                    txtDesc.setText(questoArt.getDesc());
                                    txtQtaLP.setText("0");
                                    qtaDoc.setText("0");
                                }
                            }
                        }if(presente){
                            txtInsEAN.setEnabled(false);
                            Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                            Articolo addArt = restoredData.get(index);
                            Integer lastIndex = -1;
                            artDoc.add(new Articolo(addArt.getCodArt(), addArt.getDesc(), 0, 0, qtaSpuntata, 0, 0, addArt.getAllEan(), 0.0, "", ""));
                            txtInsEAN.setEnabled(true);
                            txtInsEAN.setText("");
                            insQtaSS.setText("1");
                            txtInsEAN.setFocusableInTouchMode(true);
                            txtInsEAN.requestFocus();
                        }else{
                            alertArt("Errore!","Articolo non presente nel database, inserisci una nota");
                        }
                    }
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    giaPremuto = 0;
                    pbSearchArt.setVisibility(View.GONE);
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
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
                    txtInsEAN.setFocusableInTouchMode(true);
                    txtInsEAN.requestFocus();
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertConcludiSpunta(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaOffline.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    salvaStato();
                    giaPremuto = 0;
                    dialog.cancel();
                    salvaInVariabiliComuni();
                    Intent review = new Intent(IniziaSpuntaOffline.this, ReviewSpuntaNeg.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putExtra("docsName", docsName);
                    review.putStringArrayListExtra("qtaDoc", qtaDocum);
                    review.putStringArrayListExtra("qtaSpunta", qtaSpunta);
                    review.putStringArrayListExtra("nDoc", numero);
                    review.putExtra("tipo", 0);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertDisplayer3(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaOffline.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    alertDisplayer2("Attenzione!", "Ci sono altri articoli da spuntare?");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    pbSearchArt.setVisibility(View.VISIBLE);
                    boolean presente = false;
                    for(int i=0;i<artDoc.size();i++){
                        Articolo questoArt = artDoc.get(i);
                        for(int j=0;j<questoArt.getAllEan().size();j++){
                            if(questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)){
                                presente = true;
                                txtCodArt.setText(questoArt.getCodArt());
                                txtDesc.setText(questoArt.getDesc());
                                txtOrdF.setText(questoArt.getOf().toString());
                                txtOrdC.setText(questoArt.getOc().toString());
                                txtEsSp.setText(questoArt.getEs().toString());
                                txtPrzArt.setText("€ " + questoArt.getPrz().toString());
                                txtQtaLP.setText(questoArt.getQtaLetta().toString());
                                qtaDoc.setText(questoArt.getQtaDoc().toString());
                            }
                        }
                    }
                    if(presente){
                        Integer qtaSpuntata;
                        if(Integer.parseInt(insNColliSpunta.getText().toString())==0){
                            qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString());
                        }else{
                            qtaSpuntata = Integer.parseInt(insQtaSpunta.getText().toString()) * Integer.parseInt(insNColliSpunta.getText().toString());
                        }
                        Integer qtaEtic = qtaSpuntata;
                        Integer lastIndex = -1;
                        txtInsEAN.setEnabled(false);
                        for(int i=0; i<artDoc.size(); i++){
                            Articolo questoArt = artDoc.get(i);
                            if(questoArt.getCodArt().equals(txtCodArt.getText().toString()) && qtaSpuntata>0){
                                lastIndex = i;
                                if(questoArt.getQtaDaScal()>qtaSpuntata && qtaSpuntata>0){
                                    Integer qtaRimasta = questoArt.getQtaDaScal()-qtaSpuntata;
                                    Integer qtaParzialeSpunta = questoArt.getQtaLetta() + qtaSpuntata;
                                    questoArt.setQtaLetta(qtaParzialeSpunta);
                                    qtaSpuntata = 0;
                                    questoArt.setQtaDaScal(qtaRimasta);
                                }else if(questoArt.getQtaDaScal()==0 && qtaSpuntata>0){

                                }else if(questoArt.getQtaDaScal()<qtaSpuntata && qtaSpuntata>0){
                                    qtaSpuntata = qtaSpuntata - questoArt.getQtaDaScal();
                                    Integer qtaParzialeSpunta = questoArt.getQtaLetta() + questoArt.getQtaDaScal();
                                    questoArt.setQtaDaScal(0);
                                    questoArt.setQtaLetta(qtaParzialeSpunta);
                                }
                            }
                        }
                        if(qtaSpuntata>0 && lastIndex != -1){
                            Integer qtaUltima = qtaSpuntata + artDoc.get(lastIndex).getQtaLetta();
                            artDoc.get(lastIndex).setQtaLetta(qtaUltima);
                        }else if(qtaSpuntata>0 && lastIndex == -1){
                            ArrayList <String> eans = new ArrayList<>();
                            eans.add(txtCodArt.getText().toString());
                            artDoc.add(new Articolo(txtCodArt.getText().toString(), txtDesc.getText().toString(), 0, 0, qtaSpuntata, 0, 0, eans, 0.0, "", ""));
                        }
                        if(chkEtic.isChecked()){
                            BluetoothConnectionInsecureExample print = new BluetoothConnectionInsecureExample(spinner.getSelectedItem().toString(), bt,
                                    txtPrzArt.getText().toString(), txtPrzArt.getText().toString(), txtDesc.getText().toString(), txtCodArt.getText().toString(), ean, Integer.parseInt(insQtaSS.getText().toString()));
                            print.main();
                        }
                        txtInsEAN.setEnabled(true);
                        txtInsEAN.setText("");
                        insQtaSS.setText("1");
                        txtInsEAN.setFocusableInTouchMode(true);
                        txtInsEAN.requestFocus();
                    }else{
                        int index = 0;
                        for(int i=0;i<restoredData.size();i++){
                            Articolo questoArt = restoredData.get(i);
                            for(int j=0;j<questoArt.getAllEan().size();j++){
                                if(questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)){
                                    presente = true;
                                    index = i;
                                    txtCodArt.setText(questoArt.getCodArt());
                                    txtDesc.setText(questoArt.getDesc());
                                    txtQtaLP.setText("0");
                                    qtaDoc.setText("0");
                                }
                            }
                        }if(presente){
                            txtInsEAN.setEnabled(false);
                            Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                            Articolo addArt = restoredData.get(index);
                            Integer lastIndex = -1;
                            artDoc.add(new Articolo(addArt.getCodArt(), addArt.getDesc(), 0, 0, qtaSpuntata, 0, 0, addArt.getAllEan(), 0.0, "", ""));
                            txtInsEAN.setEnabled(true);
                            txtInsEAN.setText("");
                            insQtaSS.setText("1");
                            txtInsEAN.setFocusableInTouchMode(true);
                            txtInsEAN.requestFocus();
                        }
                    }
                    nColli = nColli + Integer.parseInt(insNColliSpunta.getText().toString());
                    pbSearchArt.setVisibility(View.GONE);
                    btnBackArt.setVisibility(View.GONE);
                    btnNextArt.setVisibility(View.GONE);
                    lblQta.setVisibility(View.GONE);
                    lblColli.setVisibility(View.GONE);
                    insNColliSpunta.setVisibility(View.GONE);
                    insNColliSpunta.setEnabled(false);
                    insQtaSpunta.setVisibility(View.GONE);
                    insQtaSpunta.setEnabled(false);
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
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
                    giaPremuto = 0;
                    dialog.cancel();
                    salvaInVariabiliComuni();
                    Intent review = new Intent(IniziaSpuntaOffline.this, ReviewSpuntaNeg.class);
                    review.putStringArrayListExtra("codici", codici);
                    review.putStringArrayListExtra("desc", descrizioni);
                    review.putStringArrayListExtra("ean", alias);
                    review.putExtra("docsName", docsName);
                    review.putExtra("nColli", nColli);
                    review.putStringArrayListExtra("qtaDoc", qtaDocum);
                    review.putStringArrayListExtra("qtaSpunta", qtaSpunta);
                    review.putStringArrayListExtra("nDoc", numero);
                    startActivity(review);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void salvaInVariabiliComuni(){
        for(int i = 0; i<artDoc.size();i++){
            Articolo articoloDaInv = artDoc.get(i);
            codici.add(articoloDaInv.getCodArt());
            descrizioni.add(articoloDaInv.getDesc());
            alias.add(articoloDaInv.getEan(0));
            qtaDocum.add(articoloDaInv.getQtaDoc().toString());
            qtaSpunta.add(articoloDaInv.getQtaLetta().toString());
            numero.add(articoloDaInv.getNumDoc());
        }
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(IniziaSpuntaOffline.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    txtCodArt.setText("");
                    txtDesc.setText("");
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    int index = 0;
                    if(rbSS.isChecked()) {
                        pbSearchArt.setVisibility(View.VISIBLE);
                        boolean presente = false;
                        for (int i = 0; i < restoredData.size(); i++) {
                            Articolo questoArt = restoredData.get(i);
                            for (int j = 0; j < questoArt.getAllEan().size(); j++) {
                                if (questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)) {
                                    index = i;
                                    presente = true;
                                    txtCodArt.setText(questoArt.getCodArt());
                                    txtDesc.setText(questoArt.getDesc());
                                    txtQtaLP.setText("0");
                                    qtaDoc.setText("0");
                                }
                            }
                        }
                        if (presente) {
                            txtInsEAN.setEnabled(false);
                            Integer qtaSpuntata = Integer.parseInt(insQtaSS.getText().toString());
                            Articolo artTrovato = restoredData.get(index);
                            artDoc.add(new Articolo(artTrovato.getCodArt(), artTrovato.getDesc(), 0, 0, qtaSpuntata, 0, 0, artTrovato.getAllEan(), 0.0, "", ""));
                            txtInsEAN.setEnabled(true);
                            txtInsEAN.setText("");
                            insQtaSS.setText("1");
                            txtInsEAN.setFocusableInTouchMode(true);
                            txtInsEAN.requestFocus();
                            pbSearchArt.setVisibility(View.GONE);
                            salvaStato();
                        } else {
                            alertArt("Errore!", "Articolo non presente nel database, inserisci una nota");
                        }
                    }else{
                        txtInsEAN.setEnabled(false);
                        pbSearchArt.setVisibility(View.VISIBLE);
                        boolean presente = false;
                        for(int i=0;i<restoredData.size();i++) {
                            Articolo questoArt = restoredData.get(i);
                            for (int j = 0; j < questoArt.getAllEan().size(); j++) {
                                if (questoArt.getEan(j).equals(findThis) || questoArt.getCodArt().equals(findThis)) {
                                    presente = true;
                                    txtCodArt.setText(questoArt.getCodArt());
                                    txtDesc.setText(questoArt.getDesc());
                                    txtQtaLP.setText("0");
                                    qtaDoc.setText("0");
                                }
                            }
                        }if(presente) {
                            insQtaSpunta.setVisibility(View.VISIBLE);
                            insNColliSpunta.setVisibility(View.VISIBLE);
                            lblQta.setVisibility(View.VISIBLE);
                            btnNextArt.setVisibility(View.VISIBLE);
                            btnBackArt.setVisibility(View.VISIBLE);
                            insNColliSpunta.setEnabled(true);
                            lblColli.setVisibility(View.VISIBLE);
                        }else {
                            alertArt("Errore!", "Articolo non presente nel database, inserisci una nota");
                        }
                    }
                    pbSearchArt.setVisibility(View.GONE);
                    giaPremuto = 0;
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IniziaSpuntaOffline.this)
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
                txtCodArt.setText(txtInsEAN.getText().toString());
                txtEsSp.setText("0");
                txtPrzArt.setText("€ " + "N/A");
                txtOrdF.setText("0");
                txtOrdC.setText("0");
                txtQtaLP.setText("0");
                txtDesc.setText(note.getText().toString());
                ArrayList<String> eans = new ArrayList<>();
                eans.add(findThis);
                artDoc.add(new Articolo(txtCodArt.getText().toString(), txtDesc.getText().toString(), 0, 0, Integer.parseInt(insQtaSS.getText().toString()), 0, 0, eans, 0.0, "", ""));
                if(rbSS.isChecked()){
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtInsEAN.setEnabled(true);
                    txtInsEAN.setText("");
                    insQtaSS.setText("1");
                    txtInsEAN.setFocusableInTouchMode(true);
                    txtInsEAN.requestFocus();
                    salvaStato();
                }else{
                    btnBackArt.setVisibility(View.VISIBLE);
                    btnNextArt.setVisibility(View.VISIBLE);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setEnabled(true);
                    lblColli.setVisibility(View.VISIBLE);
                    insQtaSpunta.setVisibility(View.VISIBLE);
                    insNColliSpunta.setVisibility(View.VISIBLE);
                    lblQta.setVisibility(View.VISIBLE);
                }
            }
            pbSearchArt.setVisibility(View.GONE);
            giaPremuto = 0;
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }
}