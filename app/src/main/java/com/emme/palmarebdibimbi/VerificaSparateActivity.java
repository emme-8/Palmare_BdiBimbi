package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VerificaSparateActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    Context context;
    String artOrForn = "";
    int giaPremuto = 0;
    EditText insCodArt;
    TextView txtCodArt, txtDesc;
    Button btnCD;
    ListView listView;
    ArrayList<String> zone, qta;
    String store = "";
    String ipNeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_sparate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
        }
        risolviMag();

        insCodArt = findViewById(R.id.edtTxtCodArtV);
        btnCD = findViewById(R.id.btnGoV);
        txtCodArt = findViewById(R.id.txtCodArtV);
        txtDesc = findViewById(R.id.txtDescV);
        listView = findViewById(R.id.listVS);

        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                listView.setAdapter(null);
                if(giaPremuto==0) {
                    giaPremuto++;
                }else{
                    hideKeyboard(this);
                    artOrForn = insCodArt.getText().toString().trim();
                    if(artOrForn.equals("")){
                        articoloNonTrovato("Errore!", "Devi prima cercare un articolo");
                    }else{
                        VerificaSparateActivity.FindArt cercaArt = new VerificaSparateActivity.FindArt();
                        cercaArt.execute("");
                    }
                }
            }
            return false;
        });

        btnCD.setOnClickListener(v -> {
            listView.setAdapter(null);
            artOrForn = insCodArt.getText().toString().trim();
            VerificaSparateActivity.FindArt findArt = new VerificaSparateActivity.FindArt();
            findArt.execute();
        });
    }

    private void articoloNonTrovato(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificaSparateActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.cancel();
                });
        AlertDialog ok = builder.create();
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

    public void setRows() {
        AdapterVerificaSparate whatever = new AdapterVerificaSparate(this, zone, qta);
        listView.setAdapter(whatever);
    }

    public void risolviMag(){
        switch (store) {
            case "MASTER":
                ipNeg = "192.168.2.41";
                break;
            case "SESTU":
                ipNeg = "192.168.1.20";
                break;
            case "MARCONI":
                ipNeg = "192.168.1.20";
                break;
            case "PIRRI":
                ipNeg = "192.168.1.20";
                break;
            case "OLBIA":
                ipNeg = "192.168.1.10";
                break;
            case "SASSARI":
                ipNeg = "192.168.1.20";
                break;
            case "NUORO":
                ipNeg = "192.168.1.20";
                break;
            case "CARBONIA":
                ipNeg = "192.168.1.20";
                break;
            case "TORTOLI":
                ipNeg = "192.168.1.20";
                break;
            case "ORISTANO":
                ipNeg = "192.168.1.20";
                break;
            case "TIBURTINA":
                ipNeg = "195.100.100.202";
                break;
            case "MasterMagRoma":
                ipNeg = "195.100.100.202";
                break;
            case "CEDIROMAINLAV":
                ipNeg = "192.168.1.20";
                break;
            case "CAPENA":
                ipNeg = "192.168.188.20";
                break;
            case "OSTIENSE":
                ipNeg = "196.100.100.203";
                break;
            case "IN LAVORAZIONE":
                ipNeg = "192.168.2.41";
                break;
            case "CASILINA":
                ipNeg = "192.168.1.20";
                break;
            case "POMEZIA":
                ipNeg = "192.168.1.20";
                break;
            case "ROMACEDI":
                ipNeg = "192.168.1.20";
                break;
            case "ARDEATINA":
                ipNeg = "192.168.1.20";
                break;
            case "VERONA":
                ipNeg = "192.168.16.20";
                break;
            case "INTRANSITO":
                ipNeg = "192.168.2.41";
                break;
            case "INTEMPORANEO":
                ipNeg = "192.168.2.41";
                break;
            default:
                ipNeg = "192.168.1.20";
                break;
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

        desc = "";
    }

        @Override
        protected void onPostExecute(String r) {

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (isSuccess) {
                txtCodArt.setText(artOrForn);
                txtDesc.setText(desc);
                setRows();
            }
            if(giaPremuto == 1){
                giaPremuto = 0;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Connection con = null;
            zone = new ArrayList<>();
            qta = new ArrayList<>();
            ResultSet res;
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String ConnURL;

                try {

                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    ConnURL = "jdbc:jtds:sqlserver://"+ ipNeg +"/PassepartoutRetail;user=sa;password=SaSqlPass*01;";
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
                } if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query = "SELECT gondola, sum(qta) as qta, articolo.descrizione, articolo.nome " +
                            "FROM inventario2023 " +
                            "LEFT JOIN articolo on articolo.nome = inventario2023.codart " +
                            "LEFT JOIN alias on articolo.id = alias.idarticolo " +
                            "WHERE codart like '"+artOrForn+"' " +
                            "OR alias.codice = '" + artOrForn + "' " +
                            "GROUP BY gondola, articolo.descrizione, articolo.nome";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    while (res.next()) {
                        isSuccess = true;
                        artOrForn = res.getString("nome");
                        desc = res.getString("descrizione");
                        zone.add(res.getString("gondola"));
                        qta.add(res.getString("qta"));
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