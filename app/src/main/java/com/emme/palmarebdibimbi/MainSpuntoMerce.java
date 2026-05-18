package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class MainSpuntoMerce extends AppCompatActivity {

    Boolean ubic = true;
    int tipo = -1;
    boolean isOnline;
    boolean isRemoto;
    Spinner spinnerStore;
    Spinner spinnerType;
    Spinner spinnerMag;
    Spinner spinnerSegnaC;
    EditText txtInsID;
    EditText txtAnnoDoc;
    EditText txtForn;
    EditText txtNumDoc;
    Context context;
    String utente = "", ipNeg = "";
    ConnectionClass connectionClass;
    String nomeMag ="";

    public void verificaVersione(int versione) {
        Connection con = null;
        try {
            con = connectionClass.CONN(context);
            String query = "SELECT versioneApp, linkApp " +
                    "FROM mcInfoBdiBimbi " ;
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            if (res.next()) {
                if(res.getInt("versioneApp")!=versione){
                    aggiornaPalmare("Attenzione!", "Stai utilizando una versione non aggiornata dell'app, scarica e installa l'aggiornamento per utilizzare tutte le ultime funzionalità", res.getString("linkApp"));
                }
            }

        } catch (Exception ex) {

        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }
    }

    private void aggiornaPalmare(String title,String message, String link){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSpuntoMerce.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("ANNULLA", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("SCARICA E INSTALLA", (dialog, which) -> {
                    dialog.cancel();
                    Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public boolean getDefaultsInv(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray2 = new JSONArray(preferences.getString("codIntInv", "[]"));
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

    public boolean getDefaultsSpunta(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray2 = new JSONArray(preferences.getString("codInt", "[]"));
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

    public boolean getDefaultsOffline(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            String docsName = preferences.getString("nameIntOff","");
            if (docsName.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getDefaultsPresa(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            JSONArray jsonArray2 = new JSONArray(preferences.getString("codIntPresa", "[]"));
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

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainSpuntoMerce.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    if(tipo==0){
                        if(ubic){
                            Intent riprendi = new Intent(MainSpuntoMerce.this, IniziaSpunta.class);
                            riprendi.putExtra("rip", 1);
                            startActivity(riprendi);
                        }else{
                            if(isOnline){
                                Intent riprendi = new Intent(MainSpuntoMerce.this, IniziaSpuntaNeg.class);
                                riprendi.putExtra("rip", 1);
                                startActivity(riprendi);
                            }else{
                                Intent riprendi = new Intent(MainSpuntoMerce.this, IniziaSpuntaOffline.class);
                                riprendi.putExtra("rip", 1);
                                startActivity(riprendi);
                            }
                        }
                    }else if(tipo == 1){
                        Intent riprendi = new Intent(MainSpuntoMerce.this, IniziaPresa.class);
                        riprendi.putExtra("rip", 1);
                        startActivity(riprendi);
                    }else{
                        Intent riprendi = new Intent(MainSpuntoMerce.this, IniziaInventario.class);
                        riprendi.putExtra("rip", 1);
                        startActivity(riprendi);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_spunto_merce);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        connectionClass = new ConnectionClass();
        context = this;

        Button findBF = findViewById(R.id.btnFindBF);
        txtInsID = findViewById(R.id.txtCodArtRow);
        txtAnnoDoc = findViewById(R.id.txtAnnoDoc);
        txtForn = findViewById(R.id.txtInsForn);
        txtNumDoc = findViewById(R.id.txtInsNDoc);
        txtForn.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        spinnerStore = (Spinner) findViewById(R.id.spinnerStore);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.stores_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStore.setAdapter(adapter);

        spinnerMag = (Spinner) findViewById(R.id.spinnerMag);
        spinnerMag.setAdapter(adapter);
        Calendar cal = Calendar.getInstance();
        Integer anno = cal.get(Calendar.YEAR);

        txtAnnoDoc.setText(anno.toString());

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            spinnerStore.setSelection(extras.getInt("store"));
            spinnerMag.setSelection(extras.getInt("store"));
            ubic = extras.getBoolean("ubicazione");
            tipo = extras.getInt("tipo");
            utente = extras.getString("utente");
            verificaVersione(extras.getInt("versione"));
        }

        if(tipo!=1){
            TextView txtm = findViewById(R.id.txtMDR);
            spinnerMag.setVisibility(View.GONE);
            txtm.setVisibility(View.GONE);
        }

        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this, R.array.doctypes_array, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        spinnerSegnaC = (Spinner) findViewById(R.id.spnSegnaC);
        ArrayAdapter<CharSequence> adapterSegnaC = ArrayAdapter.createFromResource(this, R.array.segnacollo, android.R.layout.simple_spinner_item);
        adapterSegnaC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSegnaC.setAdapter(adapterSegnaC);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isOnline = preferences.getBoolean("isOnline",true);

        if(tipo==0){
            if(isOnline){
                if(getDefaultsSpunta(this)){
                    alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi continuare la spunta di quel documento? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
                }
            }else{
                if(getDefaultsOffline(this)){
                    alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi continuare la spunta di quel documento? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
                }
            }
        }else if(tipo==1){
            if(getDefaultsPresa(this)){
                alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi continuare la presa di quel documento? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
            }
        }else if(tipo==2){
            if(getDefaultsInv(this)){
                alertDisplayer("Attenzione!","Risulta un operazione in sospeso, vuoi continuare l'inventario'? Se scegli no il documento andrà perso e non sarà più possibile recuperarlo!");
            }
        }

        findBF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSpuntoMerce.this)
                        .setTitle("Attenzione!")
                        .setMessage("Seleziona il server in cui cercare il documento")
                        .setNegativeButton("LOCALE", (dialog, which) -> {
                            isRemoto = false;
                            goTo();
                            dialog.cancel();
                        })
                        .setPositiveButton("REMOTO", (dialog, which) -> {
                            isRemoto = true;
                            goTo();
                            dialog.cancel();
                        });
                android.app.AlertDialog ok = builder.create();
                ok.show();
            }
        });
    }

    public void goTo(){
        int mag, listino, magRif, listinoRif;
        String selRiga;
        switch (spinnerMag.getSelectedItem().toString()) {
            case "MASTER":
                magRif = 1;
                listinoRif = 1;
                ipNeg = "192.168.2.41";
                nomeMag = "MasterMag";
                break;
            case "SESTU":
                magRif = 77;
                listinoRif = 6;
                nomeMag = "ELMAS";
                ipNeg = "192.168.1.20";
                break;
            case "MARCONI":
                magRif = 35;
                listinoRif = 6;
                ipNeg = "192.168.1.20";
                nomeMag = "MARCONI";
                break;
            case "PIRRI":
                magRif = 72;
                listinoRif = 6;
                ipNeg = "192.168.1.20";
                nomeMag = "PIRRI";
                break;
            case "OLBIA":
                magRif = 76;
                listinoRif = 5;
                ipNeg = "192.168.1.10";
                nomeMag = "OLBIA";
                break;
            case "SASSARI":
                magRif = 74;
                listinoRif = 9;
                ipNeg = "192.168.1.20";
                nomeMag = "SASSARI";
                break;
            case "NUORO":
                magRif = 32;
                listinoRif = 4;
                ipNeg = "192.168.1.20";
                nomeMag = "NUORO";
                break;
            case "CARBONIA":
                magRif = 78;
                listinoRif = 7;
                ipNeg = "192.168.1.20";
                nomeMag = "CARBONIA";
                break;
            case "TORTOLI":
                magRif = 75;
                listinoRif = 3;
                ipNeg = "192.168.1.20";
                nomeMag = "TORTOLI";
                break;
            case "ORISTANO":
                magRif = 71;
                listinoRif = 8;
                ipNeg = "85.47.29.51";
                break;
            case "TIBURTINA":
                magRif = 85;
                listinoRif = 3049;
                ipNeg = "195.100.100.202";
                nomeMag = "Tiburtina";
                break;
            case "MasterMagRoma":
                magRif = 91;
                listinoRif = 3049;
                ipNeg = "195.100.100.202";
                nomeMag = "MasterMagRoma";
                break;
            case "CEDIROMAINLAV":
                magRif = 93;
                listinoRif = 3054;
                ipNeg = "192.168.1.20";
                nomeMag = "CEDIROMAINLAV";
                break;
            case "CAPENA":
                magRif = 87;
                listinoRif = 3050;
                nomeMag = "Capena";
                ipNeg = "192.168.188.20";
                break;
            case "OSTIENSE":
                magRif = 86;
                listinoRif = 3048;
                nomeMag = "Ostiense";
                ipNeg = "196.100.100.203";
                break;
            case "IN LAVORAZIONE":
                magRif = 59;
                listinoRif = 1;
                nomeMag = "INLAVORAZIONE";
                ipNeg = "192.168.2.41";
                break;
            case "CASILINA":
                magRif = 90;
                listinoRif = 3052;
                nomeMag = "Casilina";
                ipNeg = "192.168.1.20";
                break;
            case "POMEZIA":
                magRif = 94;
                listinoRif = 3053;
                nomeMag = "Pomezia";
                ipNeg = "192.168.1.20";
                break;
            case "ARDEATINA":
                magRif = 112;
                listinoRif = 3054;
                nomeMag = "Ardeatina";
                ipNeg = "192.168.1.20";
                break;
            case "VERONA":
                magRif = 114;
                listinoRif = 3055;
                nomeMag = "Verona";
                ipNeg = "192.168.16.20";
                break;
            case "ROMACEDI":
                magRif = 111;
                listinoRif = 3054;
                nomeMag = "RomaCedi";
                ipNeg = "192.168.1.20";
                break;
            case "INTRANSITO":
                magRif = 88;
                nomeMag = "INTRANSITO";
                listinoRif = 1;
                ipNeg = "192.168.2.41";
                break;
            case "INTEMPORANEO":
                magRif = 89;
                listinoRif = 1;
                nomeMag = "INTEMPORANEO";
                break;
            default:
                magRif = 1;
                listinoRif = 1;
                ipNeg = "192.168.2.41";
                break;
        }
        switch (spinnerStore.getSelectedItem().toString()) {
            case "MASTER":
                mag = 1;
                listino = 1;
                if(tipo!=1) {
                    ipNeg = "192.168.2.41";
                    nomeMag = "MasterMag";
                }
                break;
            case "SESTU":
                mag = 77;
                listino = 6;
                if(tipo!=1) {
                    ipNeg = "192.168.1.20";
                    nomeMag = "ELMAS";
                }
                break;
            case "MARCONI":
                mag = 35;
                listino = 6;
                if(tipo!=1) {
                    ipNeg = "192.168.1.20";
                    nomeMag = "MARCONI";
                }
                break;
            case "PIRRI":
                mag = 72;
                listino = 6;
                if(tipo!=1) {
                    nomeMag = "PIRRI";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "OLBIA":
                mag = 76;
                listino = 5;
                if(tipo!=1) {
                    nomeMag = "OLBIA";
                    ipNeg = "192.168.1.10";
                }
                break;
            case "SASSARI":
                mag = 74;
                listino = 9;
                if(tipo!=1) {
                    nomeMag = "SASSARI";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "NUORO":
                mag = 32;
                listino = 4;
                if(tipo!=1) {
                    nomeMag = "NUORO";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "CARBONIA":
                mag = 78;
                listino = 7;
                if(tipo!=1) {
                    nomeMag = "CARBONIA";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "TORTOLI":
                mag = 75;
                listino = 3;
                if(tipo!=1) {
                    nomeMag = "TORTOLI";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "ORISTANO":
                mag = 71;
                listino = 8;
                if(tipo!=1) {
                    nomeMag = "ORISTANO";
                    ipNeg = "85.47.29.51";
                }
                break;
            case "TIBURTINA":
                mag = 85;
                listino = 3049;
                if(tipo!=1) {
                    nomeMag = "TIBURTINA";
                    ipNeg = "195.100.100.202";
                }
                break;
            case "MasterMagRoma":
                mag = 91;
                listino = 3049;
                if(tipo!=1) {
                    nomeMag = "MasterMagRoma";
                    ipNeg = "195.100.100.202";
                }
                break;
            case "CEDIROMAINLAV":
                mag = 93;
                listino = 3054;
                if(tipo!=1) {
                    nomeMag = "CEDIROMAINLAV";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "CAPENA":
                mag = 87;
                listino = 3050;
                if(tipo!=1) {
                    nomeMag = "Capena";
                    ipNeg = "192.168.188.20";
                }
                break;
            case "OSTIENSE":
                mag = 86;
                listino = 3048;
                if(tipo!=1) {
                    nomeMag = "Ostiense";
                    ipNeg = "196.100.100.203";
                }
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                listino = 1;
                if(tipo!=1) {
                    nomeMag = "INLAVORAZIONE";
                    ipNeg = "192.168.2.41";
                }
                break;
            case "CASILINA":
                mag = 90;
                listino = 3052;
                if(tipo!=1) {
                    nomeMag = "Casilina";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "POMEZIA":
                mag = 94;
                listino = 3053;
                if(tipo!=1) {
                    nomeMag = "Pomezia";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "ARDEATINA":
                mag = 112;
                listino = 3054;
                if(tipo!=1) {
                    nomeMag = "Ardeatina";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "VERONA":
                mag = 114;
                listino = 3055;
                if(tipo!=1) {
                    nomeMag = "Verona";
                    ipNeg = "192.168.16.20";
                }
                break;
            case "ROMACEDI":
                mag = 111;
                listino = 3054;
                if(tipo!=1) {
                    nomeMag = "RomaCedi";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "INTRANSITO":
                mag = 88;
                listino = 1;
                if(tipo!=1) {
                    nomeMag = "INTRANSITO";
                    ipNeg = "192.168.1.20";
                }
                break;
            case "INTEMPORANEO":
                mag = 89;
                listino = 1;
                if(tipo!=1) {
                    nomeMag = "INTEMPORANEO";
                    ipNeg = "192.168.1.20";
                }
                break;
            default:
                mag = 1;
                listino = 1;
                break;
        }
        switch (spinnerType.getSelectedItem().toString()) {
            case "BD":
                selRiga = "MetaRigaBollaDeposito";
                break;
            case "BF":
                selRiga = "MetaRigaBollaDaFornitore";
                break;
            case "PF":
                selRiga = "MetaRigaPreventivoFornitore";
                break;
            case "PX":
                selRiga = "MetaRigaPreventivoCorrispettivo";
                break;
            case "OF":
                selRiga = "MetaRigaOrdineFornitore";
                break;
            case "PR":
                selRiga = "MetaRigaPreventivo";
                break;
            case "CO":
                selRiga = "MetaRigaCorrispettivo";
                break;
            case "OX":
                selRiga = "MetaRigaOrdineCorrispettivo";
                break;
            case "OC":
                selRiga = "MetaRigaOrdineCliente";
                break;
            case "CL":
                selRiga = "MetaRigaCaricoLavorazioni";
                break;
            case "IN":
                selRiga = "MetaRigaInventario";
                break;
            case "RC":
                selRiga = "MetaRigaResoCliente";
                break;
            case "RF":
                selRiga = "MetaRigaResoFornitore";
                break;
            case "SL":
                selRiga = "MetaRigaScaricoLavorazioni";
                break;
            default:
                selRiga = "MetaRiga";
                break;
        }
        String magazzino = spinnerStore.getSelectedItem().toString();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("MagDest", magazzino);
        editor.apply();
        Intent goTo = new Intent(MainSpuntoMerce.this, SelezionaBF.class);

        goTo.putExtra("store", spinnerStore.getSelectedItem().toString());
        goTo.putExtra("tipoDoc", spinnerType.getSelectedItem().toString());
        goTo.putExtra("idDoc", txtInsID.getText().toString());
        goTo.putExtra("anno", txtAnnoDoc.getText().toString());
        goTo.putExtra("fornitore", txtForn.getText().toString());
        goTo.putExtra("numDoc", txtNumDoc.getText().toString());
        goTo.putExtra("mag", mag);
        goTo.putExtra("segnaC", spinnerSegnaC.getSelectedItem().toString());
        goTo.putExtra("rip", 0);
        goTo.putExtra("listino", listino);
        goTo.putExtra("ipNeg", ipNeg);
        goTo.putExtra("nomeMag", nomeMag);
        goTo.putExtra("isRemoto", isRemoto);
        goTo.putExtra("magRif", magRif);
        goTo.putExtra("listinoRif", listinoRif);
        goTo.putExtra("selettore", selRiga);
        goTo.putExtra("ubicazione", ubic);
        goTo.putExtra("tipo", tipo);
        goTo.putExtra("utente", utente);
        goTo.putExtra("magazzino", magazzino);
        goTo.putExtra("magazzinoRif", magazzino);
        startActivity(goTo);
    }
}