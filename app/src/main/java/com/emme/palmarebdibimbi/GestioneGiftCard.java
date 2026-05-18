package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GestioneGiftCard extends AppCompatActivity {

    Spinner spRic;
    EditText nCard, cifraSpesa;
    Button btnGiftCard;
    RadioButton rbSpendi, rbCarica;
    ConnectionClass connectionClass;
    Context context;
    String store;
    Double cifraS;
    Double cifraC;
    int loggedUserId;

    public void risolviMag() {
        switch (store) {
            case "MASTER":
                loggedUserId = 0;
                break;
            case "SESTU":
                loggedUserId = 232;
                break;
            case "MARCONI":
                loggedUserId = 233;
                break;
            case "PIRRI":
                loggedUserId = 234;
                break;
            case "OLBIA":
                loggedUserId = 240;
                break;
            case "SASSARI":
                loggedUserId = 239;
                break;
            case "NUORO":
                loggedUserId = 238;
                break;
            case "CARBONIA":
                loggedUserId = 235;
                break;
            case "TORTOLI":
                loggedUserId = 237;
                break;
            case "ORISTANO":
                loggedUserId = 236;
                break;
            case "TIBURTINA":
                loggedUserId = 241;
                break;
            case "CAPENA":
                loggedUserId = 243;
                break;
            case "OSTIENSE":
                loggedUserId = 242;
                break;
            case "IN LAVORAZIONE":
                loggedUserId = 0;
                break;
            case "CASILINA":
                loggedUserId = 244;
                break;
            case "INTRANSITO":
                loggedUserId = 0;
                break;
            case "INTEMPORANEO":
                loggedUserId = 0;
                break;
            default:
                loggedUserId = 0;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_gift_card);

        spRic = findViewById(R.id.spRicGift);
        rbSpendi = findViewById(R.id.rbSpGift);
        rbCarica = findViewById(R.id.rbRicGift);
        nCard = findViewById(R.id.edtNGift);
        cifraSpesa = findViewById(R.id.edtSpGift);
        btnGiftCard = findViewById(R.id.btnGiftCard);
        context = this;
        connectionClass = new ConnectionClass();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            store = extras.getString("storeName");
        }
        risolviMag();

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("25");
        spinnerArrayTipo.add("50");
        spinnerArrayTipo.add("75");
        spinnerArrayTipo.add("100");
        spinnerArrayTipo.add("125");
        spinnerArrayTipo.add("150");
        spinnerArrayTipo.add("175");
        spinnerArrayTipo.add("200");
        spinnerArrayTipo.add("225");
        spinnerArrayTipo.add("250");
        spinnerArrayTipo.add("275");
        spinnerArrayTipo.add("300");
        spinnerArrayTipo.add("325");
        spinnerArrayTipo.add("350");
        spinnerArrayTipo.add("375");
        spinnerArrayTipo.add("400");
        spinnerArrayTipo.add("425");
        spinnerArrayTipo.add("450");
        spinnerArrayTipo.add("475");
        spinnerArrayTipo.add("500");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spRic.setAdapter(spinnerArrayAdapter);

        rbCarica.setChecked(true);
        btnGiftCard.setText("carica");
        cifraSpesa.setVisibility(View.GONE);
        rbCarica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbCarica.setChecked(true);
                    rbSpendi.setChecked(false);
                    btnGiftCard.setText("carica");
                    cifraSpesa.setVisibility(View.GONE);
                    spRic.setVisibility(View.VISIBLE);
                }
            }
        });
        rbSpendi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rbCarica.setChecked(false);
                    rbSpendi.setChecked(true);
                    btnGiftCard.setText("spendi");
                    spRic.setVisibility(View.GONE);
                    cifraSpesa.setVisibility(View.VISIBLE);
                }
            }
        });

        btnGiftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg;
                int tipo;
                if (btnGiftCard.getText().equals("carica")) {
                    msg = "Sei sicuro di voler ricarica la card?";
                    tipo = 0;
                    cifraC = Double.parseDouble(spRic.getSelectedItem().toString());
                    conferma("Attenzione!", msg, tipo);
                } else {
                    msg = "Sei sicuro di voler spendere la card?";
                    tipo = 1;
                    cifraS = Double.parseDouble(cifraSpesa.getText().toString());
                    conferma("Attenzione!", msg, tipo);
                }
            }
        });
    }

    private void conferma(String title, String message, int tipo) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(tipo==1){
                            GestioneGiftCard.Spendi find = new GestioneGiftCard.Spendi();
                            find.execute();
                        }else{
                            GestioneGiftCard.Carica find = new GestioneGiftCard.Carica();
                            find.execute();
                        }
                    }
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    public class Spendi extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;
        Double saldo;

        @Override
        protected void onPostExecute(String r) {
            if(saldo >= cifraS){
                spendiCard(cifraS);
            }else{
                fine("Errore!", "L'importo inserito è maggiore del credito residuo");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(context);
                if (con == null) {
                    z = "Errore di connessione con il server";
                } else {
                    String query ="SELECT saldo " +
                            "FROM GiftCard " +
                            "WHERE nCard = '" + nCard.getText().toString() + "'";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        saldo = res.getDouble("saldo");
                    } else {
                        fine("Errore!", "Gift card non esistente");
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

    public class Carica extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;
        ResultSet res;

        @Override
        protected void onPostExecute(String r) {
            if (isSuccess) {
                ricaricaCard();
            } else {
                caricaCard();
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
                            "FROM GiftCard " +
                            "WHERE nCard = '" + nCard.getText().toString() + "' ";
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(query);
                    if (res.next()) {
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Errore";
            }
            return z;
        }

    }

    public void spendiCard(Double totPag) {
        boolean result = false;
        try {
            Connection con = connectionClass.CONN(context);
            if (con != null) {
                String query = "UPDATE GiftCard " +
                        "SET saldo = (saldo - " + totPag + ") " +
                        "WHERE nCard = '" + nCard.getText().toString() + "'";
                Statement stmt = con.createStatement();
                result = stmt.execute(query);
                log(1);
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void ricaricaCard() {
        boolean result = false;
        try {
            Connection con = connectionClass.CONN(context);
            if (con != null) {
                String query = "UPDATE GiftCard " +
                        "SET saldo = saldo + " + cifraC + " " +
                        "WHERE nCard = '" + nCard.getText().toString() + "' ";
                Statement stmt = con.createStatement();
                result = stmt.execute(query);
                if (result) {
                    log(0);
                } else {
                    fine("Errore!", "E' avvenuto un errore durante il caricamento della gift card");
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void caricaCard() {
        boolean result = false;
        try {
            Connection con = connectionClass.CONN(context);
            if (con != null) {
                String query = "INSERT INTO GiftCard (id, nCard, saldo, blacklist)" +
                        "VALUES ((SELECT ISNULL(MAX(id)+1,0) FROM GiftCard),'" + nCard.getText().toString() + "','" + cifraC + "', 'NO')";
                Statement stmt = con.createStatement();
                result = stmt.execute(query);
                if (result) {
                    log(0);
                } else {
                    fine("Errore!", "E' avvenuto un errore durante il caricamento della gift card");
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    private void fine(String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
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

    public void log(int source) {
        boolean result = false;
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int minutes = calendar.get(Calendar.MINUTE);
        try {
            Connection con = connectionClass.CONN(context);
            if (con != null) {
                String query;
                if (source == 1) {
                    query = "INSERT INTO GiftCard_Logs(id, idUtente, idCard, scarico, saldo, data, store, idConto, stato, ora, minuto) " +
                            "VALUES ((SELECT ISNULL(MAX(id)+1,0) FROM GiftCard_Logs)," +
                            "'" + loggedUserId + "',(SELECT id FROM GiftCard WHERE nCard = '" + nCard.getText().toString() + "'), " +
                            "'" + cifraS + "',  (SELECT saldo FROM GiftCard WHERE nCard = '" + nCard.getText().toString() + "'), GETDATE(), '" + store + "', '" + 0 + "', 0, " + hours + "," + minutes + ") ";
                } else {
                    query = "INSERT INTO GiftCard_Logs(id, idUtente, idCard, carico, saldo, data, store, stato, ora, minuto) " +
                            "VALUES ((SELECT ISNULL(MAX(id)+1,0) FROM GiftCard_Logs)," +
                            "'" + loggedUserId + "',(SELECT id FROM GiftCard WHERE nCard = '" + nCard + "'), " +
                            "'" + cifraC + "',  (SELECT saldo FROM GiftCard WHERE nCard = '" + nCard + "'), GETDATE(), '" + store + "', 0, " + hours + "," + minutes + ") ";
                }
                Statement stmt = con.createStatement();
                result = stmt.execute(query);
                fine("Attenzione!", "Gift card aggiornata con successo");
                nCard.setText("");
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

}