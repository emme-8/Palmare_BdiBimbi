package com.emme.palmarebdibimbi;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class MainSpuntaWEB extends AppCompatActivity {

    Boolean ubic = true;
    int tipo = -1;
    boolean isOnline;
    boolean isRemoto;
    EditText txtInsID;
    EditText txtAnnoDoc;
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainSpuntaWEB.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainSpuntaWEB.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    Intent riprendi = new Intent(MainSpuntaWEB.this, IniziaSpuntaNeg.class);
                    riprendi.putExtra("rip", 1);
                    startActivity(riprendi);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_spunta_web);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        connectionClass = new ConnectionClass();
        context = this;

        Button findBF = findViewById(R.id.btnFindBF);
        txtInsID = findViewById(R.id.txtCodArtRow);
        txtAnnoDoc = findViewById(R.id.txtAnnoDoc);
        txtNumDoc = findViewById(R.id.txtInsNDoc);

        Calendar cal = Calendar.getInstance();
        Integer anno = cal.get(Calendar.YEAR);

        txtAnnoDoc.setText(anno.toString());

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            ubic = extras.getBoolean("ubicazione");
            tipo = extras.getInt("tipo");
            utente = extras.getString("utente");
            verificaVersione(extras.getInt("versione"));
        }

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
        }

        findBF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRemoto = true;
                goTo();
            }
        });
    }

    public void goTo(){
        int mag, listino, magRif, listinoRif;
        String selRiga;

        selRiga = "MetaRigaOrdineCorrispettivo";
        mag = 111;
        listino = 3054;
        String magazzino = "ROMACEDI";
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("MagDest", magazzino);
        editor.apply();
        Intent goTo = new Intent(MainSpuntaWEB.this, SelezionaOxWebActivity.class);

        goTo.putExtra("store", "");
        goTo.putExtra("tipoDoc", "OX");
        goTo.putExtra("idDoc", txtInsID.getText().toString());
        goTo.putExtra("anno", txtAnnoDoc.getText().toString());
        goTo.putExtra("fornitore", "");
        goTo.putExtra("numDoc", txtNumDoc.getText().toString());
        goTo.putExtra("mag", mag);
        goTo.putExtra("segnaC", "");
        goTo.putExtra("rip", 0);
        goTo.putExtra("listino", listino);
        goTo.putExtra("ipNeg", ipNeg);
        goTo.putExtra("nomeMag", "RomaCedi");
        goTo.putExtra("isRemoto", isRemoto);
        goTo.putExtra("selettore", selRiga);
        goTo.putExtra("ubicazione", ubic);
        goTo.putExtra("tipo", tipo);
        goTo.putExtra("utente", utente);
        goTo.putExtra("magazzino", magazzino);
        goTo.putExtra("magazzinoRif", magazzino);
        startActivity(goTo);
    }
}