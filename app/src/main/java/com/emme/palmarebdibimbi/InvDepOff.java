package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zebra.sdk.comm.BluetoothConnection;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvDepOff extends AppCompatActivity {

    // UI
    private Button btnCD, btnReg, btnSvuota, salvaInv, chgUbic;
    private FloatingActionButton infoUbis;
    private EditText insCodArt, insQtaND, insUbi1, insUbi2, insNote, insNSp;
    private Switch autoQta;
    private TextView txtCodArt, txtDesc, txtEs, txtArtP, txtDescP, txtQtaP;

    // Stato / input
    private int idL = 1;
    private int da = 0;
    private int mag = 0;

    private Integer esistenza = 0;
    private String store = "";
    private String nG = "";
    private String bt = "";
    private String nSp = "";
    private String nomeP = "";
    private String utente = "";
    private String ipNeg = "";

    private int giaPremuto = 0;

    // Zebra
    private com.zebra.sdk.comm.Connection connection;

    // Room
    private AppDb db;

    // Background
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    // ✅ Messaggio richiesto
    private static final String MSG_NOT_FOUND =
            "L'articolo non verrà registrato in quanto non trovato in anagrafica, " +
                    "mettilo da parte per registrarlo in un secondo momento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_dep_off);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        db = AppDb.getInstance(this);

        readExtras();
        risolviMag();
        readPrefs();

        bindViews();
        setupInputsDefaults();

        checkZonaOnStart();
        setupListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
        try {
            if (connection != null && connection.isConnected()) connection.close();
        } catch (Exception ignored) {}
    }

    // -------------------------
    // Setup
    // -------------------------

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        da = extras.getInt("da", 0);

        store = extras.getString("storeName", "");
        utente = extras.getString("utente", "");
        nG = extras.getString("nG", "");
        nSp = extras.getString("nSp", "");
    }

    private void readPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bt = preferences.getString("PrinterIp", "");
        nomeP = preferences.getString("NomePalm", "");
    }

    private void bindViews() {
        btnCD = findViewById(R.id.btnCD);
        btnReg = findViewById(R.id.btnReg);
        btnSvuota = findViewById(R.id.btnNewCod);
        salvaInv = findViewById(R.id.btnFineInv);
        chgUbic = findViewById(R.id.btnInvChgUbic);
        infoUbis = findViewById(R.id.btnInfoUbis);

        insCodArt = findViewById(R.id.edtTxtCodArtCD);
        insQtaND = findViewById(R.id.insQtaND);
        insUbi1 = findViewById(R.id.insUbi1Inv);
        insUbi2 = findViewById(R.id.insUbi2Inv);
        insNote = findViewById(R.id.edtTxtNoteInv);
        insNSp = findViewById(R.id.insNSp);

        txtCodArt = findViewById(R.id.txtCodArtCD);
        txtDesc = findViewById(R.id.txtDescCD);
        txtEs = findViewById(R.id.txtEsInvOff);

        txtArtP = findViewById(R.id.txtCodArtPrec);
        txtDescP = findViewById(R.id.txtDescPrec);
        txtQtaP = findViewById(R.id.txtQtaPrec);

        autoQta = findViewById(R.id.swtQtaAuto);
    }

    private void updateQtaLettaInZona(String codArt) {
        final String g = safeTrim(nG);
        final String s = safeTrim(nSp);
        final String c = safeTrim(codArt);

        if (g.isEmpty() || s.isEmpty() || c.isEmpty()) {
            runOnUiThread(() -> insUbi2.setText("0"));
            return;
        }

        io.execute(() -> {
            int already = db.inventarioDao().sumQtaByZoneAndCodArt(g, s, c);
            runOnUiThread(() -> insUbi2.setText(String.valueOf(already)));
        });
    }

    private void setupInputsDefaults() {
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        insCodArt.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        insUbi1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        insUbi2.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        insCodArt.setMaxLines(1);
        insCodArt.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        insUbi1.setText(nG);
        insUbi2.setEnabled(false);
        insUbi2.setText("0");

        insNSp.setText(nSp);
        insNSp.setEnabled(false);

        insUbi1.setEnabled(false);

        // ✅ non permettere al sistema di cercare un "next focus" quando arriva TAB/DPAD
        insQtaND.setNextFocusDownId(R.id.insQtaND);
        insQtaND.setNextFocusForwardId(R.id.insQtaND);
        insQtaND.setNextFocusLeftId(R.id.insQtaND);
        insQtaND.setNextFocusRightId(R.id.insQtaND);
        insQtaND.setNextFocusUpId(R.id.insQtaND);
        insQtaND.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(6) // evita barcode lunghi
        });
        if (safeTrim(insQtaND.getText().toString()).isEmpty()) {
            insQtaND.setText("1");
        }
    }

    private void showInvalidQtaDialog() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(InvDepOff.this)
                    .setTitle("Errore!")
                    .setMessage("Quantità non valida.")
                    .setPositiveButton("OK", (d, w) -> {
                        d.dismiss();
                        resetInvalidQta();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void resetInvalidQta() {
        insQtaND.setText("1");
        insQtaND.setEnabled(true);
        insQtaND.setFocusable(true);
        insQtaND.setFocusableInTouchMode(true);
        insQtaND.requestFocus();
        insQtaND.setSelection(insQtaND.getText().length());
        showSoftKeyboard(insQtaND);
    }

    private void setupListeners() {

        insCodArt.setOnKeyListener((v, keyCode, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (giaPremuto == 0) {
                    giaPremuto++;
                    return true;
                }
                giaPremuto = 0;

                String input = safeTrim(insCodArt.getText().toString());
                if (input.isEmpty()) return true;

                cercaInDBRoom(input);
                return true;
            }
            return false;
        });

        btnCD.setOnClickListener(v -> {
            String input = safeTrim(insCodArt.getText().toString());
            if (input.isEmpty()) return;
            cercaInDBRoom(input);
        });

        btnReg.setOnClickListener(v -> {
            // ✅ adesso non basta “barcode non vuoto”: deve esistere codArt valido
            regArt();
        });

        btnSvuota.setOnClickListener(v -> svuotaTutto());

        chgUbic.setOnClickListener(v ->
                alertChgUbic("Attenzione!", "Inserisci una nuova ubicazione", insUbi1.getText().toString())
        );

        salvaInv.setOnClickListener(v -> {
            ensurePrinterConnection();
            salvaDoc("Attenzione!", "Sei sicuro di voler concludere l'inventario?");
        });

        insQtaND.setOnKeyListener((v, keyCode, event) -> {
            if (event == null) return false;

            boolean isNavKey =
                    keyCode == KeyEvent.KEYCODE_TAB ||
                            keyCode == KeyEvent.KEYCODE_ENTER ||
                            keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
                            keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                            keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
                            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;

            // ✅ Se è un tasto di navigazione, lo consumiamo SEMPRE (down + up)
            if (isNavKey) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    // su ACTION_UP non facciamo nulla, ma consumiamo per evitare focusSearch interno
                    return true;
                }

                // ACTION_DOWN
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String txt = safeTrim(insQtaND.getText().toString());
                    if (!isValidQta(txt)) {
                        showInvalidQtaDialog();
                    } else {
                        regArt();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_TAB) {
                    // se vuoi: segnala subito che non è valido (opzionale)
                    // oppure non fare niente
                }
                return true;
            }

            return false;
        });


        infoUbis.setOnClickListener(v -> {
            final String g = safeTrim(nG);
            final String s = safeTrim(nSp);

            io.execute(() -> {
                int pezzi = db.inventarioDao().sumQtaByZone(g, s);
                int referenze = db.inventarioDao().countDistinctCodArtByZone(g, s);

                runOnUiThread(() ->
                        alertInfo("Info",
                                "Zona " + g + " / " + s +
                                        "\nReferenze: " + referenze +
                                        "\nPezzi: " + pezzi)
                );
            });
        });
    }

    // -------------------------
    // ✅ NOT FOUND DIALOG (nuovo)
    // -------------------------

    private void showNotFoundDialogAndReset() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(InvDepOff.this)
                    .setTitle("Errore!")
                    .setMessage(MSG_NOT_FOUND)
                    .setPositiveButton("OK", (d, w) -> {
                        d.dismiss();
                        resetAfterNotFound();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void resetAfterNotFound() {
        // pulisci UI articolo
        txtCodArt.setText("");
        txtDesc.setText("");
        txtEs.setText("");
        esistenza = 0;

        // ✅ svuota barcode e torna pronto sul campo barcode
        insCodArt.setText("");
        insNote.setText("");
        insQtaND.setText("1");

        insUbi2.setText("0");

        focusCodArt();
    }

    // -------------------------
    // Zona on start (Room)
    // -------------------------

    private void checkZonaOnStart() {
        final String gondola = safeTrim(nG);
        final String sparata = safeTrim(nSp);

        io.execute(() -> {
            int notImported = db.inventarioDao().countNotImported();

            if (notImported == 0) {
                runOnUiThread(() -> alertInfo("Info", "Inizio nuova zona"));
                return;
            }

            InventarioRowEntity lastInZone = db.inventarioDao().getLastByZoneNotImported(gondola, sparata);
            if (lastInZone != null) {
                String msg = "Zona già presente (" + gondola + " / " + sparata + ")\n\n" +
                        "Ultimo articolo inserito:\n" +
                        "CodArt: " + nonNull(lastInZone.codArt) + "\n" +
                        "Qta: " + lastInZone.qta;
                runOnUiThread(() -> alertInfo("Info", msg));
                return;
            }

            InventarioRowEntity lastOverall = db.inventarioDao().getLastOverallNotImported();
            runOnUiThread(() -> showZonaApertaDialog(lastOverall));
        });
    }

    private void showZonaApertaDialog(InventarioRowEntity lastOverall) {

        final String zonaAperta = (lastOverall != null)
                ? (nonNull(lastOverall.gondola) + " / " + nonNull(lastOverall.sparata))
                : "sconosciuta";

        new AlertDialog.Builder(this)
                .setTitle("Attenzione!")
                .setMessage(
                        "La zona " + zonaAperta + " risulta ancora aperta.\n\n" +
                                "Vuoi salvarla su server (marcando le righe come importate), continuare quella, oppure farlo più tardi?"
                )
                .setNegativeButton("Continua quella", (d, w) -> {
                    d.dismiss();
                    if (lastOverall == null) {
                        alertArt("Errore!", "Impossibile continuare: nessuna zona trovata.");
                        return;
                    }
                    applyZonaFromLocal(lastOverall.gondola, lastOverall.sparata);
                    alertInfo("Info", "Continuo la zona " + zonaAperta);
                })
                .setNeutralButton("Più tardi", (d, w) -> {
                    d.dismiss();
                    // Non fai nulla: rimani nella zona corrente senza importare
                    alertInfo("Info", "Ok, salverai la zona più tardi.");
                })
                .setPositiveButton("Salva su server", (d, w) -> {
                    d.dismiss();
                    salvaLocaleSuServerEMarcaImportate(
                            () -> { /* rimani nella zona corrente */ },
                            () -> { /* se scegli 'più tardi' nel dialog di errore, rimani qui */ }
                    );
                })
                .setCancelable(false)
                .show();
    }

    private void applyZonaFromLocal(String gondola, String sparata) {
        nG = safeTrim(gondola);
        nSp = safeTrim(sparata);

        insUbi1.setText(nG);
        insUbi2.setText("");
        insNSp.setText(nSp);
    }

    // -------------------------
    // ✅ Room search articolo (ArticoloEntity)
    // -------------------------

    private void cercaInDBRoom(String raw) {
        final String code = safeTrim(raw);

        setActionEnabled(false);

        io.execute(() -> {
            ArticoloDao.ArticoloMini found = db.articoloDao().findByCodArtMini(code);
            if (found == null) found = db.articoloDao().findByEanMini(code);

            final ArticoloDao.ArticoloMini result = found;

            runOnUiThread(() -> {
                if (result == null) {
                    // ✅ BLOCCO: non permettere registrazione di articolo non trovato
                    setActionEnabled(true);
                    showNotFoundDialogAndReset();
                    return;
                }

                // ✅ trovato
                txtCodArt.setText(nonNull(result.codArt));
                txtDesc.setText(nonNull(result.desc));
                Integer es = result.es != null ? result.es : 0;
                txtEs.setText(String.valueOf(es));
                esistenza = es;
                updateQtaLettaInZona(nonNull(result.codArt));

                // ✅ autoQta: ok solo se trovato
                if (autoQta.isChecked()) {
                    regArt();
                } else {
                    moveToQtaInput();
                    setActionEnabled(true);
                }
            });
        });
    }

    private void moveToQtaInput() {
        if (autoQta.isChecked()) return;

        insCodArt.setEnabled(false);
        hideKeyboard(this);

        insQtaND.setEnabled(true);
        insQtaND.setFocusable(true);
        insQtaND.setFocusableInTouchMode(true);
        insQtaND.requestFocus();
        insQtaND.setCursorVisible(true);
        insQtaND.setSelectAllOnFocus(true);
        showSoftKeyboard(insQtaND);
    }

    private void focusCodArt() {
        insCodArt.setEnabled(true);
        insCodArt.setFocusableInTouchMode(true);
        insCodArt.requestFocus();
        showSoftKeyboard(insCodArt);
    }

    private void setActionEnabled(boolean enabled) {
        btnCD.setEnabled(enabled);
        btnReg.setEnabled(enabled);
        btnSvuota.setEnabled(enabled);
        salvaInv.setEnabled(enabled);
        chgUbic.setEnabled(enabled);
        autoQta.setEnabled(enabled);
    }

    // -------------------------
    // ✅ Register: SOLO Room (blocca se non trovato)
    // -------------------------

    public void regArt() {
        // ✅ BLOCCO HARD: se codArt è vuoto/non trovato NON registro
        final String codArt = safeTrim(txtCodArt.getText().toString());
        if (codArt.isEmpty()) {
            showNotFoundDialogAndReset();
            return;
        }

        /*
        final String qtaStr = safeTrim(insQtaND.getText().toString());
        if (!isValidQta(qtaStr)) {
            alertArt("Errore!", "Devi inserire una quantità valida per poter registrare l'articolo");
            return;
        }

         */

        final String qtaStr = safeTrim(insQtaND.getText().toString());
        if (!isValidQta(qtaStr)) {
            showInvalidQtaDialog();
            return;
        }
        final int qtaInt = safeParseInt(qtaStr, 0); // ora è sicuro

        final String desc = safeTrim(txtDesc.getText().toString()); // preview precedente
        final String alias = safeTrim(insCodArt.getText().toString()); // scansione
        //final int qtaInt = safeParseInt(qtaStr, 0);
        final long timingMillis = System.currentTimeMillis();
        final int esInt = (esistenza != null) ? esistenza : 0;



        final int spInt = safeParseInt(nSp, 0);
        final int sp1 = (spInt == 1) ? qtaInt : 0;
        final int sp2 = (spInt == 2) ? qtaInt : 0;

        setActionEnabled(false);

        io.execute(() -> {
            try {
                InventarioRowEntity row = new InventarioRowEntity();
                row.codArt = codArt;
                row.desc   = nonNull(desc);
                row.alias  = nonNull(alias);

                row.gondola = nonNull(nG);
                row.sparata = nonNull(nSp);
                row.qta = qtaInt;
                row.timing = timingMillis;
                row.esistenza = esInt;
                row.store = nonNull(store);
                row.sp1 = sp1;
                row.sp2 = sp2;
                row.imported = false;

                db.inventarioDao().insert(row);

                runOnUiThread(() -> {
                    txtArtP.setText(codArt);
                    txtDescP.setText(desc.isEmpty() ? alias : desc);
                    txtQtaP.setText(qtaStr);

                    playOkSound();

                    updateQtaLettaInZona(codArt);

                    // reset campi
                    insCodArt.setText("");
                    insNote.setText("");
                    txtCodArt.setText("");
                    txtDesc.setText("");
                    txtEs.setText("");
                    insQtaND.setText("1");
                    esistenza = 0;

                    if (autoQta.isChecked()) hideKeyboard(this);
                    focusCodArt();
                    setActionEnabled(true);
                });

            } catch (Exception e) {
                Log.e("InvDepOff", "Errore regArt (Room)", e);
                runOnUiThread(() -> {
                    setActionEnabled(true);
                    alertArt("Errore!", "Errore salvataggio: " + e.getMessage());
                    focusCodArt();
                });
            }
        });
    }

    // --- Config connessione (riusa ipNeg)
    private String buildSqlUrl() {
        if (safeTrim(ipNeg).isEmpty()) return "";
        return "jdbc:jtds:sqlserver://" + ipNeg +
                "/PassepartoutRetail;" +
                "user=sa;password=SaSqlPass*01;" +
                "loginTimeout=5;" +     // più aggressivo per check
                "socketTimeout=5;";     // idem
    }

    /** Check veloce: connessione + SELECT 1 */
    private void checkServerConnectionAsync(@NonNull Runnable onOk, @NonNull Runnable onFail) {
        io.execute(() -> {
            boolean ok = false;
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                try (java.sql.Connection con = DriverManager.getConnection(buildSqlUrl());
                     java.sql.Statement st = con.createStatement();
                     java.sql.ResultSet rs = st.executeQuery("SELECT 1")) {

                    st.setQueryTimeout(5);
                    ok = rs.next();
                }

            } catch (Exception e) {
                Log.e("InvDepOff", "Check connessione server fallito", e);
                ok = false;
            }

            final boolean finalOk = ok;
            runOnUiThread(() -> {
                if (finalOk) onOk.run();
                else onFail.run();
            });
        });
    }

    private void showServerConnFailedDialog(@NonNull Runnable onRetry, @NonNull Runnable onLater) {
        new AlertDialog.Builder(InvDepOff.this)
                .setTitle("Connessione non disponibile")
                .setMessage("Non riesco a connettermi al server.\n\nVuoi riprovare adesso oppure inviare la zona più tardi?")
                .setNegativeButton("Più tardi", (d, w) -> {
                    d.dismiss();
                    onLater.run();
                })
                .setPositiveButton("Riprova", (d, w) -> {
                    d.dismiss();
                    onRetry.run();
                })
                .setCancelable(false)
                .show();
    }



    private boolean isValidQta(String qta) {
        if (qta == null) return false;
        qta = qta.trim();
        if (qta.isEmpty()) return false;

        // accetta solo cifre o '-' in prima posizione
        for (int i = 0; i < qta.length(); i++) {
            char ch = qta.charAt(i);
            if (i == 0 && ch == '-') continue;
            if (!Character.isDigit(ch)) return false;
        }

        if (qta.equals("-")) return false;

        // limite lunghezza (anti barcode)
        if (qta.length() > 5) return false; // es: -9999

        try {
            int v = Integer.parseInt(qta);
            //if (v == 0) return false;        // se vuoi vietare 0
            if (Math.abs(v) > 9999) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void playOkSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone play = RingtoneManager.getRingtone(getApplicationContext(), notification);
            if (play != null) play.play();
        } catch (Exception ignored) {}
    }

    public void svuotaTutto() {
        insCodArt.setText("");
        insQtaND.setText("1");
        insNote.setText("");
        txtCodArt.setText("");
        txtDesc.setText("");
        txtEs.setText("");
        insUbi2.setText("0");
        esistenza = 0;
        focusCodArt();
    }

    // -------------------------
    // Upload server + mark imported
    // -------------------------

    private void salvaLocaleSuServerEMarcaImportate(@NonNull Runnable onSuccessUi,
                                                    @NonNull Runnable onLaterUi) {
        setActionEnabled(false);

        Runnable proceedUpload = () -> io.execute(() -> {
            try {
                if (safeTrim(ipNeg).isEmpty()) throw new IllegalStateException("ipNeg mancante.");

                List<InventarioRowEntity> rows = db.inventarioDao().getAllNotImported();
                if (rows == null || rows.isEmpty()) {
                    runOnUiThread(() -> {
                        setActionEnabled(true);
                        alertInfo("Info", "Nessuna riga da salvare: tutto già importato.");
                        onSuccessUi.run();
                    });
                    return;
                }

                // ✅ Upload
                uploadInventarioToServer(rows);

                // ✅ Marca importate SOLO dopo upload ok
                db.inventarioDao().markImportedByZone(nG, "1");

                runOnUiThread(() -> {
                    setActionEnabled(true);
                    alertInfo("OK", "Salvate " + rows.size() + " righe su server e marcate come importate.");
                    onSuccessUi.run();
                });

            } catch (Exception e) {
                Log.e("InvDepOff", "Errore salvataggio su server", e);
                runOnUiThread(() -> {
                    setActionEnabled(true);

                    showServerConnFailedDialog(
                            () -> salvaLocaleSuServerEMarcaImportate(onSuccessUi, onLaterUi), // retry
                            () -> { // ✅ PIÙ TARDI
                                alertInfo("Info", "Ok, puoi inviare la zona in un secondo momento. Le righe restano non importate.");
                                onLaterUi.run(); // <-- qui fai comunque il cambio ubicazione
                            }
                    );
                });
            }
        });

        checkServerConnectionAsync(
                proceedUpload::run,
                () -> {
                    setActionEnabled(true);
                    showServerConnFailedDialog(
                            () -> salvaLocaleSuServerEMarcaImportate(onSuccessUi, onLaterUi),
                            () -> { // ✅ PIÙ TARDI anche nel caso “no connessione”
                                alertInfo("Info", "Ok, invierai la zona più tardi. Nessuna modifica è stata fatta al server.");
                                onLaterUi.run();
                            }
                    );
                }
        );
    }

    private void uploadInventarioToServer(List<InventarioRowEntity> rows) throws Exception {
        if (safeTrim(ipNeg).isEmpty()) throw new IllegalStateException("ipNeg mancante.");

        Connection con = null;
        PreparedStatement ps = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            String connUrl =
                    "jdbc:jtds:sqlserver://" + ipNeg +
                            "/PassepartoutRetail;" +
                            "user=sa;password=SaSqlPass*01;" +
                            "loginTimeout=10;" +
                            "socketTimeout=60;";

            con = DriverManager.getConnection(connUrl);
            con.setAutoCommit(false);

            String sql = "INSERT INTO inventario2023 " +
                    "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            ps = con.prepareStatement(sql);
            ps.setQueryTimeout(60);

            final int batchSize = 100;
            int batchStart = 0;
            int inBatch = 0;

            for (int idx = 0; idx < rows.size(); idx++) {
                InventarioRowEntity r = rows.get(idx);

                bindServerRow(ps, r);
                ps.addBatch();
                inBatch++;

                if (inBatch >= batchSize) {
                    executeBatchWithDiagnostics(con, ps, rows, batchStart, idx);
                    con.commit();
                    ps.clearBatch();
                    inBatch = 0;
                    batchStart = idx + 1;
                }
            }

            if (inBatch > 0) {
                executeBatchWithDiagnostics(con, ps, rows, batchStart, rows.size() - 1);
                con.commit();
                ps.clearBatch();
            }

        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ignored) {}
            throw e;
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    private void bindServerRow(PreparedStatement ps, InventarioRowEntity r) throws Exception {
        ps.setString(1, clamp(nonNull(r.codArt), 50));
        ps.setString(2, clamp(nonNull(r.gondola), 10));
        ps.setInt(3, safeParseInt(r.sparata, 0));
        ps.setInt(4, r.qta);
        ps.setString(5, String.valueOf(r.timing / 1000L));
        ps.setInt(6, r.esistenza);
        ps.setString(7, clamp(nonNull(r.store), 50));
        ps.setInt(8, r.sp1);
        ps.setInt(9, r.sp2);

        // ✅ nuovi campi
        ps.setString(10, clamp(nonNull(nomeP), 50));
        ps.setString(11, clamp(nonNull(utente), 50));
    }

    private void executeBatchWithDiagnostics(Connection con,
                                             PreparedStatement ps,
                                             List<InventarioRowEntity> allRows,
                                             int fromIdx,
                                             int toIdx) throws Exception {
        try {
            ps.executeBatch();
        } catch (BatchUpdateException bue) {
            Exception next = bue.getNextException();
            Log.e("InvDepOff",
                    "Batch fallito range [" + fromIdx + "-" + toIdx + "] " +
                            "msg=" + safeMsg(bue) +
                            (next != null ? (" next=" + safeMsg(next)) : ""),
                    bue);

            for (int i = fromIdx; i <= toIdx; i++) {
                InventarioRowEntity r = allRows.get(i);

                try (PreparedStatement one = con.prepareStatement(
                        "INSERT INTO inventario2023 " +
                                "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {

                    one.setQueryTimeout(60);
                    bindServerRow(one, r);
                    one.executeUpdate();

                } catch (Exception exRow) {
                    Log.e("InvDepOff",
                            "RIGA FALLITA index=" + i +
                                    " id=" + r.id +
                                    " codArt=" + r.codArt +
                                    " gondola=" + r.gondola +
                                    " sparata=" + r.sparata +
                                    " qta=" + r.qta +
                                    " timing=" + (r.timing / 1000L) +
                                    " esistenza=" + r.esistenza +
                                    " store=" + r.store +
                                    " sp1=" + r.sp1 +
                                    " sp2=" + r.sp2 +
                                    " palmare=" + nomeP +
                                    " utente=" + utente +
                                    " ERRORE=" + safeMsg(exRow),
                            exRow);

                    try { con.rollback(); } catch (Exception ignored) {}
                    throw exRow;
                }
            }

            try { con.rollback(); } catch (Exception ignored) {}
            throw bue;
        }
    }

    private static String safeMsg(Throwable t) {
        if (t == null) return "Errore sconosciuto";
        String m = t.getMessage();
        return (m == null || m.trim().isEmpty()) ? t.getClass().getSimpleName() : m;
    }

    private static String clamp(String s, int max) {
        if (s == null) return "";
        s = s.trim();
        return (s.length() <= max) ? s : s.substring(0, max);
    }

    // -------------------------
    // Zebra printing
    // -------------------------

    private void ensurePrinterConnection() {
        try {
            if (connection == null || !connection.isConnected()) {
                connection = new BluetoothConnection(bt);
                connection.open();
            }
        } catch (Exception e) {
            Log.e("InvDepOff", "Errore connessione Zebra", e);
        }
    }

    private void stampaZebra(String nArticoli, String sommaQta, String note) {
        try {
            ensurePrinterConnection();

            Date date = Calendar.getInstance().getTime();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            String cpclData =
                    "! 5 0 0 540 1\n" +
                            "TEXT 4 1 16 0 " + today + "\n" +
                            "TEXT 4 1 16 60 " + note + "\n" +
                            "TEXT 4 1 16 140 N. referenze: " + nArticoli + "\n" +
                            "TEXT 4 1 16 200 N. pezzi: " + sommaQta + "\n" +
                            "PRINT\n";

            if (connection != null) connection.write(cpclData.getBytes());
        } catch (Exception e) {
            Log.e("InvDepOff", "Errore stampa Zebra", e);
        }
    }

    // -------------------------
    // Dialogs
    // -------------------------

    private void alertArt(String title, String message) {
        new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    private void alertInfo(String title, String message) {
        new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }

    private void alertChgUbic(String title, String message, String oldUbi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText nuovaUbic = new EditText(this);
        nuovaUbic.setHint("Ubicazione...");
        nuovaUbic.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(nuovaUbic);

        builder.setView(layout);

        builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();

            String nu = safeTrim(nuovaUbic.getText().toString());
            String old = safeTrim(oldUbi);
            if (nu.isEmpty() || nu.equalsIgnoreCase(old)) return;

            Runnable applyUbic = () -> {
                insUbi1.setText(nu);
                insUbi2.setText("");
                nG = nu;
            };

            salvaLocaleSuServerEMarcaImportate(
                    applyUbic,   // upload ok -> cambia ubicazione
                    applyUbic    // più tardi -> cambia ubicazione comunque
            );
        });

        builder.create().show();
    }

    private void salvaDoc(String title, String message) {
        new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();
                    stampaRiep("Attenzione!", "Vuoi stampare l'etichetta di riepilogo?");
                })
                .create()
                .show();
    }

    private void stampaRiep(String title, String message) {
        new AlertDialog.Builder(InvDepOff.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                    ricercaArticoli();
                })
                .setPositiveButton("Si", (dialog, which) -> {
                    dialog.cancel();

                    final String g = safeTrim(nG);
                    final String s = safeTrim(nSp);

                    io.execute(() -> {
                        int nArticoli = db.inventarioDao().countDistinctCodArtByZone(g, s);
                        int sommaQta  = db.inventarioDao().sumQtaByZone(g, s);

                        runOnUiThread(() -> {
                            stampaZebra(String.valueOf(nArticoli), String.valueOf(sommaQta), "Zona " + g + "/" + s);
                            ricercaArticoli();
                        });
                    });
                })
                .create()
                .show();
    }

    // -------------------------
    // Navigation
    // -------------------------

    public void ricercaArticoli() {
        Intent i = new Intent(InvDepOff.this, ReviewInvLocal.class);
        i.putExtra("ipNeg", ipNeg);
        i.putExtra("nomeP", nomeP);
        i.putExtra("utente", utente);
        startActivity(i);
    }

    // -------------------------
    // Store mapping + ipNeg
    // -------------------------

    public void risolviMag() {
        switch (store) {
            case "MASTER": mag = 1; idL = 1; ipNeg = "192.168.2.41"; break;
            case "SESTU": mag = 77; idL = 6; ipNeg = "192.168.1.20"; break;
            case "MARCONI": mag = 35; idL = 6; ipNeg = "192.168.1.20"; break;
            case "PIRRI": mag = 72; idL = 6; ipNeg = "192.168.1.20"; break;
            case "OLBIA": mag = 76; idL = 5; ipNeg = "192.168.1.10"; break;
            case "SASSARI": mag = 74; idL = 9; ipNeg = "192.168.1.20"; break;
            case "NUORO": mag = 32; idL = 4; ipNeg = "192.168.1.20"; break;
            case "CARBONIA": mag = 78; idL = 7; ipNeg = "192.168.1.20"; break;
            case "TORTOLI": mag = 75; idL = 3; ipNeg = "192.168.1.20"; break;
            case "ORISTANO": mag = 71; idL = 8; ipNeg = "192.168.1.20"; break;
            case "TIBURTINA": mag = 85; idL = 3049; ipNeg = "195.100.100.202"; break;
            case "MasterMagRoma": mag = 85; idL = 3049; ipNeg = "79.57.255.45"; break;
            case "CAPENA": mag = 87; idL = 3050; ipNeg = "192.168.188.20"; break;
            case "OSTIENSE": mag = 86; idL = 3048; ipNeg = "196.100.100.203"; break;
            case "IN LAVORAZIONE": mag = 59; idL = 1; ipNeg = "192.168.2.41"; break;
            case "CASILINA": mag = 90; idL = 3049; ipNeg = "192.168.1.20"; break;
            case "POMEZIA": mag = 94; idL = 3053; ipNeg = "192.168.1.20"; break;
            case "ARDEATINA": mag = 112; idL = 3054; ipNeg = "192.168.1.20"; break;
            case "VERONA": mag = 114; idL = 3055; ipNeg = "192.168.16.20"; break;
            case "ROMACEDI": mag = 111; idL = 3054; ipNeg = "192.168.1.20"; break;
            case "INTRANSITO": mag = 88; idL = 1; ipNeg = "192.168.2.41"; break;
            case "INTEMPORANEO": mag = 89; idL = 1; ipNeg = "192.168.2.41"; break;
            default: mag = 0; idL = 1; ipNeg = ""; break;
        }
    }

    // -------------------------
    // Utils
    // -------------------------

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private static int safeParseInt(String s, int fallback) {
        try { return Integer.parseInt(safeTrim(s)); }
        catch (Exception e) { return fallback; }
    }
}