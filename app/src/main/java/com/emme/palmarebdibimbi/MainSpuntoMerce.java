package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Calendar;
import java.util.HashSet;

public class MainSpuntoMerce extends AppCompatActivity {

    Boolean ubic = true;
    int tipo = -1;
    boolean isOnline;
    Spinner spinnerStore;
    Spinner spinnerType;
    Spinner spinnerMag;
    EditText txtInsID;
    EditText txtAnnoDoc;
    EditText txtForn;
    EditText txtNumDoc;

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
                goTo();
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
                break;
            case "SESTU":
                magRif = 77;
                listinoRif = 6;
                break;
            case "MARCONI":
                magRif = 35;
                listinoRif = 6;
                break;
            case "PIRRI":
                magRif = 72;
                listinoRif = 6;
                break;
            case "OLBIA":
                magRif = 76;
                listinoRif = 5;
                break;
            case "SASSARI":
                magRif = 74;
                listinoRif = 9;
                break;
            case "NUORO":
                magRif = 32;
                listinoRif = 4;
                break;
            case "CARBONIA":
                magRif = 78;
                listinoRif = 7;
                break;
            case "TORTOLI":
                magRif = 75;
                listinoRif = 3;
                break;
            case "ORISTANO":
                magRif = 71;
                listinoRif = 8;
                break;
            case "TIBURTINA":
                magRif = 85;
                listinoRif = 3049;
                break;
            case "CAPENA":
                magRif = 87;
                listinoRif = 3050;
                break;
            case "OSTIENSE":
                magRif = 86;
                listinoRif = 3048;
                break;
            case "IN LAVORAZIONE":
                magRif = 59;
                listinoRif = 1;
                break;
            case "CASILINA":
                magRif = 90;
                listinoRif = 3052;
                break;
            case "INTRANSITO":
                magRif = 88;
                listinoRif = 1;
                break;
            case "INTEMPORANEO":
                magRif = 89;
                listinoRif = 1;
                break;
            default:
                magRif = 1;
                listinoRif = 1;
                break;
        }
        switch (spinnerStore.getSelectedItem().toString()) {
            case "MASTER":
                mag = 1;
                listino = 1;
                break;
            case "SESTU":
                mag = 77;
                listino = 6;
                break;
            case "MARCONI":
                mag = 35;
                listino = 6;
                break;
            case "PIRRI":
                mag = 72;
                listino = 6;
                break;
            case "OLBIA":
                mag = 76;
                listino = 5;
                break;
            case "SASSARI":
                mag = 74;
                listino = 9;
                break;
            case "NUORO":
                mag = 32;
                listino = 4;
                break;
            case "CARBONIA":
                mag = 78;
                listino = 7;
                break;
            case "TORTOLI":
                mag = 75;
                listino = 3;
                break;
            case "ORISTANO":
                mag = 71;
                listino = 8;
                break;
            case "TIBURTINA":
                mag = 85;
                listino = 3049;
                break;
            case "CAPENA":
                mag = 87;
                listino = 3050;
                break;
            case "OSTIENSE":
                mag = 86;
                listino = 3048;
                break;
            case "IN LAVORAZIONE":
                mag = 59;
                listino = 1;
                break;
            case "CASILINA":
                mag = 90;
                listino = 3052;
                break;
            case "INTRANSITO":
                mag = 88;
                listino = 1;
                break;
            case "INTEMPORANEO":
                mag = 89;
                listino = 1;
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
        goTo.putExtra("rip", 0);
        goTo.putExtra("listino", listino);
        goTo.putExtra("magRif", magRif);
        goTo.putExtra("listinoRif", listinoRif);
        goTo.putExtra("selettore", selRiga);
        goTo.putExtra("ubicazione", ubic);
        goTo.putExtra("tipo", tipo);
        goTo.putExtra("magazzino", magazzino);
        goTo.putExtra("magazzinoRif", magazzino);
        startActivity(goTo);
    }
}