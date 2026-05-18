package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeInventario extends AppCompatActivity {

    String store = "", ipNeg = "";

    String utente;
    String nomeP = "";
    Context context;
    int mag = 0;
    boolean ubicazione=false;
    ConnectionClass connectionClass;
    private AppDb db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeInventario.this)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inventario);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = AppDb.getInstance(this);
        context = this;
        connectionClass = new ConnectionClass();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
            verificaVersione(extras.getInt("versione"));
            utente = extras.getString("utente");
            ubicazione = extras.getBoolean("ubicazione");
            mag = extras.getInt("store");
        }
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        nomeP = p.getString("NomePalm","");

        risolviMag();

        Button invOn = findViewById(R.id.btnInvOn);
        Button invCodQta = findViewById(R.id.btnCodQta);
        Button invOff = findViewById(R.id.btnInvOff);
        Button btnsendFiles = findViewById(R.id.btnInvFile);
        Button btnCInv = findViewById(R.id.btnCInv);
        Button btnVS = findViewById(R.id.btnVSp);
        Button btnRinv = findViewById(R.id.btnRimZ);

        invOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArt("Attenzione!","Inserisci i seguenti campi per iniziare o riprendere l'inventario", 0);
            }
        });
        invOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArt("Attenzione!","Inserisci i seguenti campi per iniziare o riprendere l'inventario", 1);
            }
        });
        invCodQta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArt("Attenzione!","Inserisci i seguenti campi per iniziare o riprendere l'inventario", 2);
            }
        });
        btnCInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCat("Attenzione!","Seleziona la categoria da controllare");
            }
        });
        btnVS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inv = new Intent(HomeInventario.this,VerificaSparateActivity.class);
                inv.putExtra("storeName", store);
                inv.putExtra("utente", utente);
                startActivity(inv);
            }
        });
        Button btnSvuotaInv = findViewById(R.id.btnSvuotaInv); // id del tuo bottone nel layout
        btnSvuotaInv.setOnClickListener(v -> promptPasswordAndClearInventario());
        btnRinv.setOnClickListener(v -> promptPasswordThenResendMenu());
        btnsendFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                File directory = new File("/storage/emulated/0/NAS/SpuntaGen");
                File[] files = directory.listFiles();
                ArrayList<String> pathToSend = new ArrayList<>();
                for (int i = 0; i < files.length; i++){
                    if(files[i].getName().contains("inventario")){
                        pathToSend.add(files[i].getPath());
                    }
                }
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                String email = p.getString("Email", "");
                String emailPass = p.getString("EmailPass", "");
                String nomeP = p.getString("NomePalm","");
                String obj = nomeP+"_Riepilogo_Inventari";
                String[] to = new String[]{"spunte@bdibimbi.it", email};
                try {
                    sendEmail(to,email, obj, " ", pathToSend, email, emailPass);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                 */
                exportInventarioToExcel();
            }
        });
        Button btnReportZone = findViewById(R.id.btnReportZ);
        btnReportZone.setOnClickListener(v -> {
            io.execute(() -> {
                try {
                    final String table = "inventario_rows"; // cambia se diverso

                    // Query helper
                    java.util.function.Function<String, android.database.Cursor> runQ = (sql) -> {
                        androidx.sqlite.db.SupportSQLiteQuery q =
                                new androidx.sqlite.db.SimpleSQLiteQuery(sql);
                        return db.inventarioDao().rawQuery(q);
                    };

                    // 1) Zone NON importate
                    String sqlNotImported =
                            "SELECT " +
                                    "  TRIM(gondola) AS g, " +
                                    "  TRIM(sparata) AS s, " +
                                    "  COALESCE(SUM(qta), 0) AS pezzi, " +
                                    "  COUNT(DISTINCT TRIM(codArt)) AS referenze " +
                                    "FROM " + table + " " +
                                    "WHERE imported = 0 " +
                                    "  AND gondola IS NOT NULL AND TRIM(gondola) <> '' " +
                                    "  AND sparata IS NOT NULL AND TRIM(sparata) <> '' " +
                                    "  AND codArt IS NOT NULL AND TRIM(codArt) <> '' " +
                                    "GROUP BY TRIM(gondola), TRIM(sparata) " +
                                    "ORDER BY TRIM(gondola), TRIM(sparata)";

                    // 2) Zone IMPORTATE
                    String sqlImported =
                            "SELECT " +
                                    "  TRIM(gondola) AS g, " +
                                    "  TRIM(sparata) AS s, " +
                                    "  COALESCE(SUM(qta), 0) AS pezzi, " +
                                    "  COUNT(DISTINCT TRIM(codArt)) AS referenze " +
                                    "FROM " + table + " " +
                                    "WHERE imported = 1 " +
                                    "  AND gondola IS NOT NULL AND TRIM(gondola) <> '' " +
                                    "  AND sparata IS NOT NULL AND TRIM(sparata) <> '' " +
                                    "  AND codArt IS NOT NULL AND TRIM(codArt) <> '' " +
                                    "GROUP BY TRIM(gondola), TRIM(sparata) " +
                                    "ORDER BY TRIM(gondola), TRIM(sparata)";

                    // 3) Query "come ora" (tutte)
                    String sqlAll =
                            "SELECT " +
                                    "  TRIM(gondola) AS g, " +
                                    "  TRIM(sparata) AS s, " +
                                    "  COALESCE(SUM(qta), 0) AS pezzi, " +
                                    "  COUNT(DISTINCT TRIM(codArt)) AS referenze " +
                                    "FROM " + table + " " +
                                    "WHERE gondola IS NOT NULL AND TRIM(gondola) <> '' " +
                                    "  AND sparata IS NOT NULL AND TRIM(sparata) <> '' " +
                                    "  AND codArt IS NOT NULL AND TRIM(codArt) <> '' " +
                                    "GROUP BY TRIM(gondola), TRIM(sparata) " +
                                    "ORDER BY TRIM(gondola), TRIM(sparata)";

                    // --- Leggi NON importate
                    StringBuilder sbNot = new StringBuilder();
                    int rowsNot = 0, totPzNot = 0, totRefNot = 0;

                    android.database.Cursor cNot = runQ.apply(sqlNotImported);
                    try {
                        int ixG = cNot.getColumnIndexOrThrow("g");
                        int ixS = cNot.getColumnIndexOrThrow("s");
                        int ixPz = cNot.getColumnIndexOrThrow("pezzi");
                        int ixRef = cNot.getColumnIndexOrThrow("referenze");

                        while (cNot.moveToNext()) {
                            rowsNot++;
                            String g = cNot.isNull(ixG) ? "" : cNot.getString(ixG);
                            String s = cNot.isNull(ixS) ? "" : cNot.getString(ixS);
                            int pezzi = cNot.getInt(ixPz);
                            int ref = cNot.getInt(ixRef);

                            sbNot.append(g).append(" / ").append(s)
                                    .append("  -  Ref: ").append(ref)
                                    .append("  -  Pz: ").append(pezzi)
                                    .append("\n");

                            totPzNot += pezzi;
                            totRefNot += ref;
                        }
                    } finally {
                        cNot.close();
                    }

                    // ✅ Se NON ci sono zone non importate, visualizzazione IDENTICA a prima
                    if (rowsNot == 0) {
                        android.database.Cursor cAll = runQ.apply(sqlAll);
                        StringBuilder sb = new StringBuilder();
                        int rows = 0, totPz = 0, totRef = 0;

                        try {
                            int ixG = cAll.getColumnIndexOrThrow("g");
                            int ixS = cAll.getColumnIndexOrThrow("s");
                            int ixPz = cAll.getColumnIndexOrThrow("pezzi");
                            int ixRef = cAll.getColumnIndexOrThrow("referenze");

                            while (cAll.moveToNext()) {
                                rows++;
                                String g = cAll.isNull(ixG) ? "" : cAll.getString(ixG);
                                String s = cAll.isNull(ixS) ? "" : cAll.getString(ixS);
                                int pezzi = cAll.getInt(ixPz);
                                int ref = cAll.getInt(ixRef);

                                sb.append(g).append(" / ").append(s)
                                        .append("  -  Ref: ").append(ref)
                                        .append("  -  Pz: ").append(pezzi)
                                        .append("\n");

                                totPz += pezzi;
                                totRef += ref;
                            }
                        } finally {
                            cAll.close();
                        }

                        if (rows > 0) {
                            sb.append("\nTOTALE  -  Ref: ").append(totRef)
                                    .append("  -  Pz: ").append(totPz);
                        }

                        final String text = (rows == 0) ? "Nessun dato presente." : sb.toString();
                        runOnUiThread(() -> showScrollableDialog("Report zone", text));
                        return;
                    }

                    // --- Leggi IMPORTATE (solo se esistono non importate)
                    StringBuilder sbImp = new StringBuilder();
                    int rowsImp = 0, totPzImp = 0, totRefImp = 0;

                    android.database.Cursor cImp = runQ.apply(sqlImported);
                    try {
                        int ixG = cImp.getColumnIndexOrThrow("g");
                        int ixS = cImp.getColumnIndexOrThrow("s");
                        int ixPz = cImp.getColumnIndexOrThrow("pezzi");
                        int ixRef = cImp.getColumnIndexOrThrow("referenze");

                        while (cImp.moveToNext()) {
                            rowsImp++;
                            String g = cImp.isNull(ixG) ? "" : cImp.getString(ixG);
                            String s = cImp.isNull(ixS) ? "" : cImp.getString(ixS);
                            int pezzi = cImp.getInt(ixPz);
                            int ref = cImp.getInt(ixRef);

                            sbImp.append(g).append(" / ").append(s)
                                    .append("  -  Ref: ").append(ref)
                                    .append("  -  Pz: ").append(pezzi)
                                    .append("\n");

                            totPzImp += pezzi;
                            totRefImp += ref;
                        }
                    } finally {
                        cImp.close();
                    }

                    // --- Costruisci output diviso
                    StringBuilder out = new StringBuilder();

                    out.append("Zone NON importate:\n");
                    out.append(sbNot);
                    out.append("TOTALE NON importate  -  Ref: ").append(totRefNot)
                            .append("  -  Pz: ").append(totPzNot)
                            .append("\n\n");

                    out.append("Zone importate:\n");
                    if (rowsImp == 0) {
                        out.append("Nessuna zona importata.\n");
                    } else {
                        out.append(sbImp);
                        out.append("TOTALE importate  -  Ref: ").append(totRefImp)
                                .append("  -  Pz: ").append(totPzImp)
                                .append("\n");
                    }

                    final String text = out.toString();
                    runOnUiThread(() -> showScrollableDialog("Report zone", text));

                } catch (Exception e) {
                    Log.e("ReportZone", "Errore report zone", e);
                    runOnUiThread(() -> alertErr("Errore", "Report fallito: " + e.getMessage()));
                }
            });
        });
    }

    private void showScrollableDialog(String title, String message) {
        TextView tv = new TextView(this);
        tv.setText(message);

        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        tv.setPadding(pad, pad, pad, pad);

        tv.setTextIsSelectable(true); // utile per copiare
        tv.setVerticalScrollBarEnabled(true);

        android.widget.ScrollView sv = new android.widget.ScrollView(this);
        sv.addView(tv);

        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setView(sv)
                .setPositiveButton("OK", (d, w) -> d.dismiss())
                .show();
    }

    private static final String CLEAR_PWD = "invbdibimbi!";

    private void promptPasswordAndClearInventario() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Attenzione!")
                .setMessage("Inserisci la password.\n\nOperazione irreversibile.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        final EditText pwd = new EditText(this);
        pwd.setHint("Password");
        pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(pwd);

        builder.setView(layout);
        builder.setNegativeButton("Annulla", (d, w) -> d.dismiss());
        builder.setPositiveButton("OK", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button ok = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(v -> {
                String inserted = pwd.getText() == null ? "" : pwd.getText().toString();
                if (!CLEAR_PWD.equals(inserted)) {
                    pwd.setError("Password errata");
                    return;
                }
                dialog.dismiss();
                showClearMenuDialog(); // ✅ qui
            });
        });

        dialog.show();
    }

    private void promptPasswordThenResendZone() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Attenzione!")
                .setMessage("Inserisci la password.\n\nOperazione protetta.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        final EditText pwd = new EditText(this);
        pwd.setHint("Password");
        pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(pwd);

        builder.setView(layout);
        builder.setNegativeButton("Annulla", (d, w) -> d.dismiss());
        builder.setPositiveButton("OK", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button ok = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(v -> {
                String inserted = pwd.getText() == null ? "" : pwd.getText().toString();
                if (!CLEAR_PWD.equals(inserted)) {
                    pwd.setError("Password errata");
                    return;
                }
                dialog.dismiss();
                showZonePickerForResend(); // ✅ parte la scelta zona
            });
        });

        dialog.show();
    }

    private void checkServerAndConfirmResend(String gondola, String sparata) {
        io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) {
                    throw new IllegalStateException("ipNeg mancante per lo store: " + store);
                }

                int localRows = db.inventarioDao().countRowsByZone(gondola, sparata);
                int serverRows = serverCountZoneRows(gondola, sparata);

                runOnUiThread(() -> {
                    if (localRows <= 0) {
                        alertInfo("Info", "Nessuna riga trovata per la zona " + gondola + " / " + sparata);
                        return;
                    }

                    if (serverRows > 0) {
                        // ✅ zona già presente: warning + ulteriore conferma
                        new androidx.appcompat.app.AlertDialog.Builder(HomeInventario.this)
                                .setTitle("Zona già presente sul server")
                                .setMessage("La zona " + gondola + " / " + sparata +
                                        " risulta già presente sul server.\n\n" +
                                        "Righe server: " + serverRows +
                                        "\nRighe palmare: " + localRows +
                                        "\n\nVuoi continuare comunque?")
                                .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                                .setPositiveButton("Continua", (d, w) -> {
                                    d.dismiss();
                                    confirmResendZoneFinal(gondola, sparata, localRows, serverRows);
                                })
                                .show();
                    } else {
                        // ✅ non presente: conferma “normale”
                        confirmResendZoneFinal(gondola, sparata, localRows, 0);
                    }
                });

            } catch (Exception e) {
                Log.e("RINV", "Errore controllo server zona", e);
                runOnUiThread(() -> alertErr("Errore", "Controllo server fallito: " + e.getMessage()));
            }
        });
    }

    private void promptPasswordThenResendMenu() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Attenzione!")
                .setMessage("Inserisci la password.\n\nOperazione protetta.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        final EditText pwd = new EditText(this);
        pwd.setHint("Password");
        pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(pwd);

        builder.setView(layout);
        builder.setNegativeButton("Annulla", (d, w) -> d.dismiss());
        builder.setPositiveButton("OK", null);

        android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dlg -> {
            Button ok = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(v -> {
                String inserted = pwd.getText() == null ? "" : pwd.getText().toString();
                if (!CLEAR_PWD.equals(inserted)) {
                    pwd.setError("Password errata");
                    return;
                }
                dialog.dismiss();
                showResendMenu(); // ✅ scelta UNA / TUTTE
            });
        });

        dialog.show();
    }

    private void showResendMenu() {
        new AlertDialog.Builder(HomeInventario.this)
                .setTitle("Rinvio zone al server")
                .setItems(new String[]{
                        "Rimanda UNA zona",
                        "Rimanda TUTTO"
                }, (d, which) -> {
                    d.dismiss();
                    if (which == 0) {
                        showZonePickerForResend();   // ✔ già esiste
                    } else {
                        confirmResendAllRows();     // ✅ QUI
                    }
                })
                .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                .show();
    }

    private void confirmResendAllNotImported() {
        io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) {
                    throw new IllegalStateException("ipNeg mancante per lo store: " + store);
                }

                int localRows = db.inventarioDao().countNotImportedRows(); // ✅ nuovo DAO
                if (localRows <= 0) {
                    runOnUiThread(() -> alertInfo("Info", "Non ci sono righe NON importate da rinviare."));
                    return;
                }

                // opzionale: info server
                int serverRows = serverCountAllRows();

                runOnUiThread(() -> new androidx.appcompat.app.AlertDialog.Builder(HomeInventario.this)
                        .setTitle("Conferma invio TUTTO")
                        .setMessage("Vuoi rinviare TUTTE le righe NON importate?\n\n" +
                                "Righe NON importate (palmare): " + localRows + "\n" +
                                "Righe presenti sul server (store): " + serverRows + "\n\n" +
                                "A fine invio verranno flaggate come IMPORTATE.")
                        .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                        .setPositiveButton("Invia tutto", (d, w) -> {
                            d.dismiss();
                            resendAllNotImportedToServer();
                        })
                        .show());

            } catch (Exception e) {
                Log.e("RINV_ALL", "Errore pre-check", e);
                runOnUiThread(() -> alertErr("Errore", "Controllo fallito: " + e.getMessage()));
            }
        });
    }

    private void resendAllNotImportedToServer() {
        io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) {
                    throw new IllegalStateException("ipNeg mancante per lo store: " + store);
                }

                List<InventarioRowEntity> rows = db.inventarioDao().getNotImportedRows(); // ✅ nuovo DAO
                if (rows == null || rows.isEmpty()) {
                    runOnUiThread(() -> alertInfo("Info", "Non ci sono righe NON importate."));
                    return;
                }

                int inserted = uploadRowsToServer(rows);

                // ✅ flagga tutte le non importate
                int updated = db.inventarioDao().markImportedAllNotImported(); // ✅ nuovo DAO

                runOnUiThread(() -> alertInfo("OK",
                        "Invio completato.\n" +
                                "Righe inviate: " + inserted + "\n" +
                                "Righe flaggate imported=1: " + updated));

            } catch (Exception e) {
                Log.e("RINV_ALL", "Errore invio tutte", e);
                runOnUiThread(() -> alertErr("Errore", "Invio TUTTE fallito: " + e.getMessage()));
            }
        });
    }

    private void confirmResendZoneFinal(String gondola, String sparata, int localRows, int serverRows) {
        String msg = "Vuoi rimandare al server la zona " + gondola + " / " + sparata + "?\n" +
                "Righe palmare: " + localRows +
                (serverRows > 0 ? ("\nRighe server: " + serverRows) : "");

        new androidx.appcompat.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle("Conferma invio zona")
                .setMessage(msg)
                .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                .setPositiveButton("Invia", (d, w) -> {
                    d.dismiss();
                    resendZoneToServer(gondola, sparata);
                })
                .show();
    }

    private void resendAllRowsToServer() {
        io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) {
                    throw new IllegalStateException("ipNeg mancante per lo store: " + store);
                }

                List<InventarioRowEntity> rows = db.inventarioDao().getAll(); // ✅ tutte, importate e non
                if (rows == null || rows.isEmpty()) {
                    runOnUiThread(() -> alertInfo("Info", "Nessuna riga da inviare."));
                    return;
                }

                int inserted = uploadZoneToServer(rows); // ✅ la tua funzione va già bene

                // ✅ se vuoi: dopo invio, marca tutto importato localmente
                int updated = db.inventarioDao().markImportedAll(); // vedi DAO sotto

                runOnUiThread(() -> alertInfo("OK",
                        "Invio completato.\n" +
                                "Righe inviate: " + inserted + "\n" +
                                "Righe flaggate imported=1: " + updated));

            } catch (Exception e) {
                Log.e("RINV_ALL", "Errore invio TUTTO", e);
                runOnUiThread(() -> alertErr("Errore", "Invio fallito: " + e.getMessage()));
            }
        });
    }

    private void confirmResendAllRows() {
        io.execute(() -> {
            int localRows = db.inventarioDao().countAllRows();

            runOnUiThread(() -> new AlertDialog.Builder(HomeInventario.this)
                    .setTitle("Conferma invio TUTTO")
                    .setMessage(
                            "Stai per rinviare TUTTE le righe dell'inventario.\n\n" +
                                    "Righe sul palmare: " + localRows + "\n\n" +
                                    "ATTENZIONE:\n" +
                                    "- Le righe verranno rinviate al server\n" +
                                    "- Non verrà cancellato nulla sul server\n" +
                                    "- Potrebbero crearsi duplicati\n\n" +
                                    "Vuoi continuare?"
                    )
                    .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                    .setPositiveButton("Continua", (d, w) -> {
                        d.dismiss();
                        resendAllRowsToServer(); // ✅ QUI PARTE DAVVERO
                    })
                    .show());
        });
    }

    private void showZonePickerForResend() {
        io.execute(() -> {
            try {
                List<String> zones = db.inventarioDao().getAllZoneKeys();

                runOnUiThread(() -> {
                    if (zones == null || zones.isEmpty()) {
                        alertInfo("Info", "Nessuna zona presente nel DB locale.");
                        return;
                    }

                    String[] items = zones.toArray(new String[0]);

                    new androidx.appcompat.app.AlertDialog.Builder(HomeInventario.this)
                            .setTitle("Seleziona la zona da rimandare")
                            .setItems(items, (d, which) -> {
                                d.dismiss();
                                String zonaKey = items[which]; // es: "A12 / 1"
                                String[] parts = zonaKey.split("\\s*/\\s*");
                                String g = parts.length > 0 ? safeTrim(parts[0]) : "";
                                String s = parts.length > 1 ? safeTrim(parts[1]) : "";

                                if (g.isEmpty() || s.isEmpty()) {
                                    alertErr("Errore", "Zona non valida: " + zonaKey);
                                    return;
                                }

                                checkServerAndConfirmResend(g, s); // ✅ qui il nuovo controllo server
                            })
                            .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                            .show();
                });

            } catch (Exception e) {
                Log.e("RINV", "Errore caricamento zone", e);
                runOnUiThread(() -> alertErr("Errore", "Impossibile caricare le zone: " + e.getMessage()));
            }
        });
    }

    private void showClearMenuDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancellazione inventario locale")
                .setItems(new String[]{"Svuota TUTTO", "Svuota UNA ZONA..."}, (d, which) -> {
                    d.dismiss();
                    if (which == 0) {
                        confirmDeleteAll();
                    } else {
                        showZonePickerDialog();
                    }
                })
                .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                .show();
    }

    private void resendZoneToServer(String gondola, String sparata) {
        io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) {
                    throw new IllegalStateException("ipNeg mancante per lo store: " + store);
                }

                List<InventarioRowEntity> rows = db.inventarioDao().getRowsByZone(gondola, sparata);
                if (rows == null || rows.isEmpty()) {
                    runOnUiThread(() -> alertInfo("Info", "Nessuna riga da inviare."));
                    return;
                }

                // ⚠️ Se vuoi evitare duplicati e “sovrascrivere”:
                // serverDeleteZone(gondola, sparata);

                int inserted = uploadZoneToServer(rows);

                db.inventarioDao().markImportedByZone(gondola, sparata);

                runOnUiThread(() -> alertInfo("OK",
                        "Zona inviata.\nZona: " + gondola + " / " + sparata +
                                "\nRighe inviate: " + inserted));

            } catch (Exception e) {
                Log.e("RINV", "Errore invio zona", e);
                runOnUiThread(() -> alertErr("Errore", "Invio fallito: " + e.getMessage()));
            }
        });
    }

    private int uploadRowsToServer(List<InventarioRowEntity> rows) throws Exception {
        // Funzione "generica" usata per inviare liste qualsiasi:
        // - tutte le righe non importate
        // - righe di più zone insieme
        // Internamente riusa l'implementazione già esistente.
        return uploadZoneToServer(rows);
    }

    private int uploadZoneToServer(List<InventarioRowEntity> rows) throws Exception {
        Connection con = null;
        java.sql.PreparedStatement ps = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(url);
            con.setAutoCommit(false);

            // ⚠️ tabella server (tu sopra hai SERVER_TABLE = Inventario2023)
            String sql = "INSERT INTO Inventario2023 " +
                    "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            ps = con.prepareStatement(sql);
            ps.setQueryTimeout(60);

            int batchSize = 100;
            int inBatch = 0;
            int total = 0;

            for (InventarioRowEntity r : rows) {
                // Se codArt può essere vuoto, puoi decidere di saltare:
                // if (safeTrim(r.codArt).isEmpty()) continue;

                ps.setString(1, safeTrim(r.codArt));
                ps.setString(2, safeTrim(r.gondola.toUpperCase()));
                ps.setInt(3, safeParseInt(r.sparata, 0));
                ps.setInt(4, r.qta);
                ps.setString(5, String.valueOf(r.timing / 1000L)); // come fai già tu
                ps.setInt(6, r.esistenza);
                ps.setString(7, safeTrim(r.store));
                ps.setInt(8, r.sp1);
                ps.setInt(9, r.sp2);
                ps.setString(10, safeTrim(nomeP));
                ps.setString(11, safeTrim(utente));

                ps.addBatch();
                inBatch++;
                total++;

                if (inBatch >= batchSize) {
                    ps.executeBatch();
                    con.commit();
                    ps.clearBatch();
                    inBatch = 0;
                }
            }

            if (inBatch > 0) {
                ps.executeBatch();
                con.commit();
                ps.clearBatch();
            }

            return total;

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) {}
            throw e;
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try { return Integer.parseInt(safeTrim(s)); }
        catch (Exception e) { return fallback; }
    }

    public void risolviMag() {
        switch (store) {
            case "MASTER": ipNeg = "192.168.2.41"; break;
            case "SESTU": ipNeg = "192.168.1.20"; break;
            case "MARCONI": ipNeg = "192.168.1.20"; break;
            case "PIRRI": ipNeg = "192.168.1.20"; break;
            case "OLBIA": ipNeg = "192.168.1.10"; break;
            case "SASSARI": ipNeg = "192.168.1.20"; break;
            case "NUORO": ipNeg = "192.168.1.20"; break;
            case "CARBONIA": ipNeg = "192.168.1.20"; break;
            case "TORTOLI": ipNeg = "192.168.1.20"; break;
            case "ORISTANO": ipNeg = "192.168.1.20"; break;
            case "TIBURTINA": ipNeg = "195.100.100.202"; break;
            case "MasterMagRoma": ipNeg = "79.57.255.45"; break;
            case "CAPENA": ipNeg = "192.168.188.20"; break;
            case "OSTIENSE": ipNeg = "196.100.100.203"; break;
            case "IN LAVORAZIONE": ipNeg = "192.168.2.41"; break;
            case "CASILINA": ipNeg = "192.168.1.20"; break;
            case "POMEZIA": ipNeg = "192.168.1.20"; break;
            case "ARDEATINA": ipNeg = "192.168.1.20"; break;
            case "VERONA": ipNeg = "192.168.16.20"; break;
            case "ROMACEDI": ipNeg = "192.168.1.20"; break;
            case "INTRANSITO": ipNeg = "192.168.2.41"; break;
            case "INTEMPORANEO": ipNeg = "192.168.2.41"; break;
            default: ipNeg = ""; break;
        }
    }

    private void confirmDeleteAll() {
        io.execute(() -> {
            int rows = db.inventarioDao().countAllRows();

            runOnUiThread(() -> {
                if (rows == 0) {
                    alertInfo("Info", "Nessuna riga presente nel DB locale.");
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Conferma cancellazione")
                        .setMessage("Sei sicuro di voler cancellare TUTTE le righe? (" + rows + " righe)")
                        .setNegativeButton("No", (d, w) -> d.dismiss())
                        .setPositiveButton("Sì, svuota", (d, w) -> {
                            d.dismiss();

                            askDeleteScope(
                                    // ✅ SOLO LOCALE
                                    this::clearInventarioRows,
                                    // ✅ LOCALE + SERVER
                                    () -> deleteAllLocalAndServerWithCheck()
                            );
                        })
                        .show();
            });
        });
    }

    private void showZonePickerDialog() {
        io.execute(() -> {
            List<String> zones = db.inventarioDao().getAllZoneKeys();

            runOnUiThread(() -> {
                if (zones == null || zones.isEmpty()) {
                    alertInfo("Info", "Nessuna zona presente nel DB locale.");
                    return;
                }

                String[] items = zones.toArray(new String[0]);

                new android.app.AlertDialog.Builder(this)
                        .setTitle("Seleziona la zona da cancellare")
                        .setItems(items, (d, which) -> {
                            d.dismiss();
                            String zonaKey = items[which]; // es: "A12 / 1"
                            String[] parts = zonaKey.split("\\s*/\\s*");
                            String g = parts.length > 0 ? safeTrim(parts[0]) : "";
                            String s = parts.length > 1 ? safeTrim(parts[1]) : "";

                            if (g.isEmpty() || s.isEmpty()) {
                                alertErr("Errore", "Zona non valida: " + zonaKey);
                                return;
                            }

                            confirmDeleteZone(g, s);
                        })
                        .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                        .show();
            });
        });
    }

    private void deleteAllLocalAndServerWithCheck() {
        io.execute(() -> {
            try {
                int localRows = db.inventarioDao().countAllRows();

                // ⚠️ QUI devi adattare la query al tuo DB SQL Server
                int serverRows = serverCountAllRows(); // TODO

                runOnUiThread(() -> {
                    if (serverRows == localRows) {
                        new AlertDialog.Builder(this)
                                .setTitle("Conferma cancellazione (server + palmare)")
                                .setMessage("Vuoi cancellare " + serverRows + " righe anche dal server?")
                                .setNegativeButton("No", (d,w)-> d.dismiss())
                                .setPositiveButton("Sì, cancella", (d,w)-> {
                                    d.dismiss();
                                    io.execute(() -> doDeleteAllServerThenLocal());
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Attenzione: conteggi diversi")
                                .setMessage("Server: " + serverRows + " righe\nPalmare: " + localRows + " righe\n\nContinuare comunque?")
                                .setNegativeButton("Annulla", (d,w)-> d.dismiss())
                                .setPositiveButton("Continua", (d,w)-> {
                                    d.dismiss();
                                    io.execute(() -> doDeleteAllServerThenLocal());
                                })
                                .show();
                    }
                });

            } catch (Exception e) {
                Log.e("DEL_ALL", "Errore controllo server", e);
                runOnUiThread(() -> alertErr("Errore", "Controllo server fallito: " + e.getMessage()));
            }
        });
    }

    private void doDeleteAllServerThenLocal() {
        try {
            // 1) server
            int deletedServer = serverDeleteAll(); // TODO

            // 2) locale
            db.inventarioDao().deleteAllInventario();

            runOnUiThread(() -> alertInfo("OK",
                    "Cancellazione completata.\nServer: " + deletedServer + " righe\nPalmare: svuotato."));
        } catch (Exception e) {
            Log.e("DEL_ALL", "Errore cancellazione server+locale", e);
            runOnUiThread(() -> alertErr("Errore", "Cancellazione fallita: " + e.getMessage()));
        }
    }

    private void askDeleteScope(Runnable onlyLocal, Runnable alsoServer) {
        runOnUiThread(() -> {
            new androidx.appcompat.app.AlertDialog.Builder(HomeInventario.this)
                    .setTitle("Cancellazione inventario")
                    .setMessage("Vuoi cancellare solo dal palmare o anche dal server?")
                    .setPositiveButton("Solo palmare", (d, w) -> {
                        d.dismiss();
                        onlyLocal.run();
                    })
                    .setNegativeButton("Annulla", (d, w) -> d.dismiss())
                    .setNeutralButton("Palmare + server", (d, w) -> {
                        d.dismiss();
                        alsoServer.run();
                    })
                    .show();
        });
    }

    private void deleteZoneLocalAndServerWithCheck(String gondola, String sparata) {
        io.execute(() -> {
            try {
                int localRows = db.inventarioDao().countRowsByZone(gondola, sparata);

                // ⚠️ QUI devi adattare la query al tuo DB SQL Server
                int serverRows = serverCountZoneRows(gondola, sparata); // TODO

                runOnUiThread(() -> {
                    if (serverRows == localRows) {
                        new AlertDialog.Builder(this)
                                .setTitle("Conferma cancellazione (server + palmare)")
                                .setMessage("Zona " + gondola + " / " + sparata +
                                        "\nVuoi cancellare " + serverRows + " righe anche dal server?")
                                .setNegativeButton("No", (d,w)-> d.dismiss())
                                .setPositiveButton("Sì, cancella", (d,w)-> {
                                    d.dismiss();
                                    io.execute(() -> doDeleteZoneServerThenLocal(gondola, sparata));
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Attenzione: conteggi diversi")
                                .setMessage("Zona " + gondola + " / " + sparata +
                                        "\nServer: " + serverRows + " righe" +
                                        "\nPalmare: " + localRows + " righe" +
                                        "\n\nContinuare comunque?")
                                .setNegativeButton("Annulla", (d,w)-> d.dismiss())
                                .setPositiveButton("Continua", (d,w)-> {
                                    d.dismiss();
                                    io.execute(() -> doDeleteZoneServerThenLocal(gondola, sparata));
                                })
                                .show();
                    }
                });

            } catch (Exception e) {
                Log.e("DEL_ZONE", "Errore controllo server zona", e);
                runOnUiThread(() -> alertErr("Errore", "Controllo server fallito: " + e.getMessage()));
            }
        });
    }

    // ⚠️ ADATTA
    private static final String SERVER_TABLE = "Inventario2023"; // es: mcInventarioRows

    private int serverCountAllRows() throws Exception {
        Connection con = null;
        java.sql.PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(url);

            // ⚠️ aggiungi eventuali filtri di store/magazzino se servono
            String sql = "SELECT COUNT(*) AS cnt FROM " + SERVER_TABLE + " WHERE store = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, safeTrim(store)); // o mag, o storeId

            rs = ps.executeQuery();
            return rs.next() ? rs.getInt("cnt") : 0;

        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    private int serverCountZoneRows(String gondola, String sparata) throws Exception {
        Connection con = null;
        java.sql.PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(url);

            String sql = "SELECT COUNT(*) AS cnt FROM " + SERVER_TABLE +
                    " WHERE store = ? AND gondola = ? AND sparata = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, safeTrim(store));
            ps.setString(2, safeTrim(gondola));
            ps.setString(3, safeTrim(sparata));

            rs = ps.executeQuery();
            return rs.next() ? rs.getInt("cnt") : 0;

        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    private int serverDeleteAll() throws Exception {
        Connection con = null;
        java.sql.PreparedStatement ps = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(url);
            con.setAutoCommit(false);

            String sql = "DELETE FROM " + SERVER_TABLE + " WHERE store = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, safeTrim(store));

            int deleted = ps.executeUpdate();
            con.commit();
            return deleted;

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) {}
            throw e;
        } finally {
            if (ps != null) ps.close();
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
                con.close();
            }
        }
    }

    private int serverDeleteZone(String gondola, String sparata) throws Exception {
        Connection con = null;
        java.sql.PreparedStatement ps = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String url =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(url);
            con.setAutoCommit(false);

            String sql = "DELETE FROM " + SERVER_TABLE +
                    " WHERE store = ? AND gondola = ? AND sparata = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, safeTrim(store));
            ps.setString(2, safeTrim(gondola));
            ps.setString(3, safeTrim(sparata));

            int deleted = ps.executeUpdate();
            con.commit();
            return deleted;

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) {}
            throw e;
        } finally {
            if (ps != null) ps.close();
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignored) {}
                con.close();
            }
        }
    }

    private void doDeleteZoneServerThenLocal(String gondola, String sparata) {
        try {
            // 1) server
            int deletedServer = serverDeleteZone(gondola, sparata); // TODO

            // 2) locale
            int deletedLocal = db.inventarioDao().deleteByZone(gondola, sparata);

            runOnUiThread(() -> alertInfo("OK",
                    "Cancellazione completata.\nZona " + gondola + " / " + sparata +
                            "\nServer: " + deletedServer + " righe\nPalmare: " + deletedLocal + " righe"));
        } catch (Exception e) {
            Log.e("DEL_ZONE", "Errore cancellazione server+locale zona", e);
            runOnUiThread(() -> alertErr("Errore", "Cancellazione fallita: " + e.getMessage()));
        }
    }

    private void confirmDeleteZone(String gondola, String sparata) {
        io.execute(() -> {
            int articoli = 0;
            int pezzi = 0;

            android.database.Cursor c = null;
            try {
                // ⚠️ cambia inventario_rows se il tableName è diverso
                String sql =
                        "SELECT " +
                                "  COUNT(DISTINCT " +
                                "    CASE " +
                                "      WHEN codArt IS NOT NULL AND TRIM(codArt) <> '' THEN TRIM(codArt) " +
                                "      ELSE TRIM(alias) " +
                                "    END" +
                                "  ) AS articoli, " +
                                "  COALESCE(SUM(qta), 0) AS pezzi " +
                                "FROM inventario_rows " +
                                "WHERE gondola = ? AND sparata = ? " +
                                "  AND ( " +
                                "    (codArt IS NOT NULL AND TRIM(codArt) <> '') " +
                                "    OR (alias IS NOT NULL AND TRIM(alias) <> '') " +
                                "  )";

                androidx.sqlite.db.SupportSQLiteQuery q =
                        new androidx.sqlite.db.SimpleSQLiteQuery(
                                sql,
                                new Object[]{gondola, sparata}
                        );

                c = db.inventarioDao().rawQuery(q);

                if (c.moveToFirst()) {
                    articoli = c.getInt(c.getColumnIndexOrThrow("articoli"));
                    pezzi = c.getInt(c.getColumnIndexOrThrow("pezzi"));
                }

            } finally {
                if (c != null) c.close();
            }

            final int fArticoli = articoli;
            final int fPezzi = pezzi;

            runOnUiThread(() -> {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Conferma cancellazione")
                        .setMessage(
                                "Sei sicuro di voler cancellare " +
                                        fArticoli + " articoli / " +
                                        fPezzi + " pezzi " +
                                        "dalla zona " + gondola + " / " + sparata + "?"
                        )
                        .setNegativeButton("No", (d, w) -> d.dismiss())
                        .setPositiveButton("Sì, cancella", (d, w) -> {
                            d.dismiss();

                            askDeleteScope(
                                    // ✅ SOLO LOCALE
                                    () -> deleteZone(gondola, sparata),
                                    // ✅ LOCALE + SERVER
                                    () -> deleteZoneLocalAndServerWithCheck(gondola, sparata)
                            );
                        })
                        .show();
            });
        });
    }

    private void deleteZone(String gondola, String sparata) {
        io.execute(() -> {
            try {
                int deleted = db.inventarioDao().deleteByZone(gondola, sparata);
                runOnUiThread(() ->
                        alertInfo("OK", "Cancellate " + deleted + " righe dalla zona " + gondola + " / " + sparata + ".")
                );
            } catch (Exception e) {
                android.util.Log.e("HomeInventario", "Errore cancellazione zona", e);
                runOnUiThread(() -> alertErr("Errore", "Cancellazione fallita: " + e.getMessage()));
            }
        });
    }

    private void clearInventarioRowsByZone(String g, String s) {
        io.execute(() -> {
            try {
                db.inventarioDao().deleteByZone(g, s);
                runOnUiThread(() -> alertInfo("OK", "Cancellate righe zona " + g + " / " + s + "."));
            } catch (Exception e) {
                android.util.Log.e("HomeInventario", "Errore svuotamento zona", e);
                runOnUiThread(() -> alertErr("Errore", "Svuotamento zona fallito: " + e.getMessage()));
            }
        });
    }

    private void clearInventarioRows() {
        io.execute(() -> {
            try {
                db.inventarioDao().deleteAllInventario();
                runOnUiThread(() -> alertInfo("OK", "Tabella inventario_rows svuotata."));
            } catch (Exception e) {
                android.util.Log.e("HomeInventario", "Errore svuotamento inventario_rows", e);
                runOnUiThread(() -> alertErr("Errore", "Svuotamento fallito: " + e.getMessage()));
            }
        });
    }

    private static final String EXPORT_DIR = "/storage/emulated/0/NAS/Inventario2025";

    private void exportInventarioToExcel() {
        Log.d("EXPORT", "START exportInventarioToExcel()");
        io.execute(() -> {

            try {
                Log.d("EXPORT", "1) getAll()...");
                List<InventarioRowEntity> rows = db.inventarioDao().getAll();
                Log.d("EXPORT", "1b) rows=" + (rows == null ? "null" : rows.size()));

                if (rows == null || rows.isEmpty()) {
                    runOnUiThread(() -> alertInfo("Info", "Nessuna riga da esportare."));
                    return;
                }

                Log.d("EXPORT", "2) mkdirs: " + EXPORT_DIR);
                File dir = new File(EXPORT_DIR);
                Log.d("EXPORT", "2b) exists=" + dir.exists());

                boolean okDir = dir.exists() || dir.mkdirs();
                Log.d("EXPORT", "2c) mkdirs result=" + okDir);

                if (!okDir) {
                    throw new IllegalStateException("Impossibile creare cartella: " + EXPORT_DIR);
                }

                String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
                String fileName = "SpuntaGen_" + safeTrim(store) + "_" + ts + ".xlsx";
                File outFile = new File(dir, fileName);

                Log.d("EXPORT", "3) outFile=" + outFile.getAbsolutePath());

                Log.d("EXPORT", "4) create workbook...");
                org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Inventario");
                Log.d("EXPORT", "4b) sheet created");

                // Header
                int r = 0;
                org.apache.poi.ss.usermodel.Row header = sheet.createRow(r++);
                String[] cols = new String[] {
                        "id","codArt","desc","alias","gondola","sparata","qta",
                        "timingMillis","timingSec","esistenza","store","sp1","sp2","imported"
                };
                for (int c = 0; c < cols.length; c++) header.createCell(c).setCellValue(cols[c]);
                Log.d("EXPORT", "5) header ok");

                int count = 0;
                for (InventarioRowEntity e : rows) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(r++);
                    int c = 0;
                    row.createCell(c++).setCellValue(e.id);
                    row.createCell(c++).setCellValue(nonNull(e.codArt));
                    row.createCell(c++).setCellValue(nonNull(e.desc));
                    row.createCell(c++).setCellValue(nonNull(e.alias));
                    row.createCell(c++).setCellValue(nonNull(e.gondola));
                    row.createCell(c++).setCellValue(nonNull(e.sparata));
                    row.createCell(c++).setCellValue(e.qta);
                    row.createCell(c++).setCellValue(e.timing);
                    row.createCell(c++).setCellValue(e.timing / 1000L);
                    row.createCell(c++).setCellValue(e.esistenza);
                    row.createCell(c++).setCellValue(nonNull(e.store));
                    row.createCell(c++).setCellValue(e.sp1);
                    row.createCell(c++).setCellValue(e.sp2);
                    row.createCell(c++).setCellValue(e.imported ? 1 : 0);

                    count++;
                    if (count % 200 == 0) Log.d("EXPORT", "6) wrote rows=" + count);
                }
                Log.d("EXPORT", "6b) wrote total rows=" + count);

                // IMPORTANT: su Android autoSize può far crashare su dataset grandi o font mancanti
                // Commentalo per debug:
                // for (int c = 0; c < cols.length; c++) sheet.autoSizeColumn(c);
                Log.d("EXPORT", "7) (autoSize skipped)");

                Log.d("EXPORT", "8) open FileOutputStream...");
                java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);

                Log.d("EXPORT", "9) wb.write...");
                wb.write(fos);

                fos.flush();
                fos.close();
                wb.close();

                Log.d("EXPORT", "10) DONE size=" + outFile.length());

                runOnUiThread(() -> alertInfo("OK", "Creato:\n" + outFile.getAbsolutePath() + "\nSize: " + outFile.length()));

            } catch (Exception ex) {
                Log.e("EXPORT", "Export failed", ex);
                runOnUiThread(() -> alertErr("Errore export", ex.getClass().getSimpleName() + ": " + ex.getMessage()));
            }
        });
    }

    private void setActionEnabled(boolean enabled) {
        // Se vuoi davvero disabilitare i bottoni durante l’export, passa qui i riferimenti.
        // Per ora non fa nulla (così compila e non rompe).
    }

    private void alertInfo(String title, String message) {
        new android.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (d,w) -> d.dismiss())
                .show();
    }

    private void alertErr(String title, String message) {
        new android.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (d,w) -> d.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    public static boolean sendEmail(String[] to, String from, String subject,
                                    String message,ArrayList<String> attachement, String user, String pass) throws Exception {
        GMailSender mail = new GMailSender();

        if (user != null && user.length() > 0) {
            mail.setUser(user);
            mail.setFrom(from);
        } else {
            mail.setUser("User");
            mail.setFrom("From");
        }

        if (pass != null && pass.length() > 0) {
            mail.setPassword(pass);
        } else {
            mail.setPassword("Password");
        }

        if (subject != null && subject.length() > 0) {
            mail.setSubject(subject);
        } else {
            mail.setSubject("Subject");
        }

        if (message != null && message.length() > 0) {
            mail.setBody(message);
        } else {
            mail.setBody("Message");
        }

        mail.setTo(to);

        if (attachement != null) {
            for(int i=0; i< attachement.size(); i++){
                mail.addAttachment(attachement.get(i));
            }
        }
        return mail.send();
    }

    private void alertArt(String title,String message, int tipoC){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nG = new EditText(this);
        nG.setHint("Numero gondola");
        nG.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nG);

        /*
        ArrayList<String> spinnerArraySel = new ArrayList<String>();
        spinnerArraySel.add("Selettore...");
        spinnerArraySel.add("Estivo");
        spinnerArraySel.add("Invernale");
        Spinner spinnerSel = new Spinner(context);
        ArrayAdapter<String> spinnerASel = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArraySel);
        spinnerSel.setAdapter(spinnerASel);
        if(tipoC==1){
            layout.addView(spinnerSel);
        }*/

        TextView txtEmpty = new TextView(context);
        layout.addView(txtEmpty);

        Spinner splist = new Spinner(context);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.splist, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splist.setAdapter(adapter);
        layout.addView(splist);

        /*
        ArrayList<String> spinnerArrayPos = new ArrayList<String>();
        spinnerArrayPos.add("Posizione...");
        spinnerArrayPos.add("Vendita");
        spinnerArrayPos.add("Magazzino");
        Spinner spinnerPos = new Spinner(context);
        ArrayAdapter<String> spinnerAPos = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayPos);
        spinnerPos.setAdapter(spinnerAPos);
        if(tipoC==1){
            layout.addView(spinnerPos);
        }*/

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("Causale...");
        spinnerArrayTipo.add("Ubicazione");
        spinnerArrayTipo.add("Rifornimento");
        spinnerArrayTipo.add("Ordine");
        spinnerArrayTipo.add("Inventario");
        spinnerArrayTipo.add("Documento");
        Spinner spinnerTipo = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spinnerTipo.setAdapter(spinnerArrayAdapter);
        if(tipoC!=1){
            layout.addView(spinnerTipo);
        }

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(nG.getText().toString().equals("")){
                nG.setHintTextColor(Color.RED);
                alertArt("Errore!", "Wait bruh, what are u doing? Put something in textbox pls", tipoC);
            }else if(splist.getSelectedItem().toString().equals("Numero sparata")){
                alertArt("Errore!", "Wait bruh, what are u doing? Put something in textbox pls", tipoC);
            }else{
                dialog.cancel();
                if(tipoC==1){
                    //if(spinnerPos.getSelectedItem().toString().equals("Posizione...")){
                      //  alertArt("Errore!", "Devi specificare una posizione", tipoC);
                    //}else{
                        Intent inv = new Intent(HomeInventario.this,InvDepOff.class);
                        inv.putExtra("nSp", splist.getSelectedItem().toString());
                        inv.putExtra("nG", nG.getText().toString());
                        inv.putExtra("utente", utente);
                        inv.putExtra("storeName", store);
                        //inv.putExtra("pos", spinnerPos.getSelectedItem().toString());
                        startActivity(inv);
                    //}
                }else if(tipoC==2){
                    Intent inv = new Intent(HomeInventario.this,InvCodQta.class);
                    inv.putExtra("nSp", splist.getSelectedItem().toString());
                    inv.putExtra("nG", nG.getText().toString());
                    inv.putExtra("utente", utente);
                    inv.putExtra("storeName", store);
                    //inv.putExtra("pos", spinnerPos.getSelectedItem().toString());
                    startActivity(inv);
                }else{
                    if(spinnerTipo.getSelectedItem().toString().equals("Causale...")){
                        alertArt("Errore!", "Devi specificare una causale", tipoC);
                    }else{
                        Intent inv = new Intent(HomeInventario.this,InvDep.class);
                        inv.putExtra("nSp", splist.getSelectedItem().toString());
                        inv.putExtra("nG", nG.getText().toString());
                        inv.putExtra("storeName", store);
                        inv.putExtra("utente", utente);
                        inv.putExtra("causale", spinnerTipo.getSelectedItem().toString());
                        startActivity(inv);
                    }
                }

            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void alertCat(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> spinnerArrayScelta = new ArrayList<String>();
        spinnerArrayScelta.add("Da documento");
        spinnerArrayScelta.add("Da database");
        Spinner spinnerScelta = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapterScelta = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayScelta);
        spinnerScelta.setAdapter(spinnerArrayAdapterScelta);

        layout.addView(spinnerScelta);

        ArrayList<String> spinnerArrayTipo = new ArrayList<String>();
        spinnerArrayTipo.add("A CASA");
        spinnerArrayTipo.add("A PASSEGGIO");
        spinnerArrayTipo.add("ABB_SPORT");
        spinnerArrayTipo.add("ABBIGLIAMENTO CONTINUATIVO");
        spinnerArrayTipo.add("ABBIGLIAMENTO ESTIVO");
        spinnerArrayTipo.add("ABBIGLIAMENTO INVERNALE");
        spinnerArrayTipo.add("ACCESSORI ABBIGLIAMENTO");
        spinnerArrayTipo.add("ALIMENTAZIONE");
        spinnerArrayTipo.add("ALTRE CATEGORIE");
        spinnerArrayTipo.add("CALZATURE");
        spinnerArrayTipo.add("GIOCO NUOVO");
        spinnerArrayTipo.add("IGIENE");
        spinnerArrayTipo.add("IN AUTO");
        spinnerArrayTipo.add("IN CAMERETTA");
        spinnerArrayTipo.add("LA PAPPA");
        spinnerArrayTipo.add("MAMMA");
        spinnerArrayTipo.add("MATERIALE DI CONSUMO");
        spinnerArrayTipo.add("PANNI E SALVIETTE");
        spinnerArrayTipo.add("RICAMBI");
        Spinner spinnerTipo = new Spinner(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArrayTipo);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        layout.addView(spinnerTipo);

        builder.setView(layout);

        spinnerScelta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    spinnerTipo.setVisibility(View.GONE);
                }else{
                    spinnerTipo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(spinnerScelta.getSelectedItem().toString().equals("Da documento")){
                Intent inv = new Intent(HomeInventario.this,MainSpuntoMerce.class);
                inv.putExtra("store", mag);
                inv.putExtra("ubicazione", utente);
                inv.putExtra("tipo", 10);
                inv.putExtra("utente", utente);
                startActivity(inv);
            }else{
                Intent inv = new Intent(HomeInventario.this,ControlloInventario.class);
                inv.putExtra("storeName", store);
                inv.putExtra("utente", utente);
                inv.putExtra("categoria", spinnerTipo.getSelectedItem().toString());
                startActivity(inv);
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }
}