package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewInvLocal extends AppCompatActivity {

    private static final String TAG = "ReviewInvLocal";

    private ListView listView;
    private Button btnSave;
    private TextView txtTitle;

    private Button btnFindInInv;
    private EditText insRicInInv;

    private AppDb db;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private String ipNeg = "";
    private String gondola = "";
    private String sparata = "";

    private String nomeP = "";
    private String utente = "";

    private List<InventarioRowEntity> rows = new ArrayList<>();
    private AdapterReviewInvLocal adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_inv_local);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        db = AppDb.getInstance(this);

        listView = findViewById(R.id.lvRiepInvLocal);
        btnSave  = findViewById(R.id.btnSaveDocInvLocal);
        txtTitle = findViewById(R.id.txtTitleInvLocal);
        btnFindInInv = findViewById(R.id.btnFindInInv);
        insRicInInv  = findViewById(R.id.insRicInInv);

        // ipNeg: da extras (preferito) o SharedPreferences
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        // ipNeg = p.getString("ipNeg","");

        if (getIntent().getExtras() != null && getIntent().getExtras().getString("ipNeg") != null) {
            ipNeg = getIntent().getExtras().getString("ipNeg");
        }
        readExtras();

        loadNotImportedRows();
        //loadLastZoneAndRows();

        btnFindInInv.setOnClickListener(v -> {
            String q = insRicInInv.getText() == null ? "" : insRicInInv.getText().toString().trim();
            io.execute(() -> {
                List<InventarioRowEntity> filtered =
                        (q.isEmpty())
                                ? db.inventarioDao().getAllNotImported()
                                : db.inventarioDao().searchNotImported(q);

                runOnUiThread(() -> adapter.setRows(filtered));
            });
        });

        btnSave.setOnClickListener(v -> showImportDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }

    private void loadZoneRows() {
        io.execute(() -> {
            List<InventarioRowEntity> all = db.inventarioDao().getRowsByZone(gondola, sparata);
            runOnUiThread(() -> adapter.setRows(all));
        });
    }

    private void loadLastZoneAndRows() {
        io.execute(() -> {
            InventarioRowEntity last = db.inventarioDao().getLastOverall();
            if (last == null) {
                runOnUiThread(() -> {
                    txtTitle.setText("Nessuna riga in locale");
                    rows = new ArrayList<>();
                    adapter = new AdapterReviewInvLocal(this, rows);
                    listView.setAdapter(adapter);
                    btnSave.setEnabled(false);
                });
                return;
            }

            gondola = last.gondola;
            sparata = last.sparata;

            rows = db.inventarioDao().getRowsByZone(gondola, sparata);

            runOnUiThread(() -> {
                txtTitle.setText("Riepilogo locale - Zona " + gondola + " / " + sparata);
                adapter = new AdapterReviewInvLocal(this, rows);
                listView.setAdapter(adapter);
                btnSave.setEnabled(true);
            });
        });
    }

    private void showImportDialog() {
        io.execute(() -> {
            int pending = db.inventarioDao().countNotImported();

            runOnUiThread(() -> {
                if (pending == 0) {
                    showDialog("Info", "Non ci sono righe da importare.");
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Attenzione")
                        .setMessage("Vuoi importare al server " + pending + " righe non importate?")
                        .setNegativeButton("No", (d, w) -> d.dismiss())
                        .setPositiveButton("Si", (d, w) -> {
                            d.dismiss();
                            importAllPendingRows();
                        })
                        .show();
            });
        });
    }

    private void importAllPendingRows() {
        setUiEnabled(false);

        checkServerConnectionAsync(
                this::doImportAllPendingRows,
                () -> {
                    setUiEnabled(true);
                    showServerConnFailedDialog(
                            this::importAllPendingRows,
                            () -> showDialog("Info", "Ok, invierai più tardi. Nessuna modifica è stata fatta.")
                    );
                }
        );
    }

    private void doImportAllPendingRows() {
        io.execute(() -> {

            // 1) Carica pending
            List<InventarioRowEntity> pendingRows;
            try {
                pendingRows = db.inventarioDao().getAllNotImported();
            } catch (Exception e) {
                Log.e(TAG, "Errore lettura DB locale", e);
                final String msg = safeMsg(e);
                runOnUiThread(() -> {
                    setUiEnabled(true);
                    showDialog("Errore locale", "Impossibile leggere le righe non importate.\n\nDettagli: " + msg);
                });
                return;
            }

            if (pendingRows == null || pendingRows.isEmpty()) {
                runOnUiThread(() -> {
                    setUiEnabled(true);
                    showDialog("Info", "Nessuna riga da importare.");
                    btnSave.setEnabled(false);
                });
                return;
            }

            final int totalToSend = pendingRows.size();

            // 2) Upload + mark per batch (dentro uploadInventarioToServerRobust)
            try {
                uploadInventarioToServerRobust(pendingRows);

            } catch (Exception eUpload) {
                Log.e(TAG, "Upload fallito", eUpload);
                final String err = safeMsg(eUpload);

                // Dopo un fallimento, alcune righe potrebbero essere già state committate e marcate (se fai mark per batch)
                // quindi ricarichiamo comunque lo stato reale locale.
                List<InventarioRowEntity> stillPending;
                try {
                    stillPending = db.inventarioDao().getAllNotImported();
                } catch (Exception ignored) {
                    stillPending = null;
                }
                final List<InventarioRowEntity> finalStillPending = stillPending;
                final int remaining = (stillPending == null) ? -1 : stillPending.size();

                runOnUiThread(() -> {
                    setUiEnabled(true);

                    if (finalStillPending != null && adapter != null) {
                        adapter.setRows(finalStillPending);
                        txtTitle.setText("Righe non importate: " + finalStillPending.size());
                        btnSave.setEnabled(finalStillPending.size() > 0);
                    }

                    showDialog(
                            "Connessione/Server",
                            "Import NON completato.\n" +
                                    "Tentate: " + totalToSend + "\n" +
                                    (remaining >= 0 ? ("Rimaste non importate: " + remaining + "\n") : "") +
                                    "\nDettagli: " + err
                    );
                });
                return;
            }

            // 3) Se siamo qui, upload completato (e mark già fatto per batch)
            //    Ricarichiamo e mostriamo esito
            List<InventarioRowEntity> remainingRows;
            try {
                remainingRows = db.inventarioDao().getAllNotImported();
            } catch (Exception e) {
                Log.e(TAG, "Upload ok ma reload locale fallito", e);
                final String err = safeMsg(e);

                runOnUiThread(() -> {
                    setUiEnabled(true);
                    showDialog(
                            "Attenzione",
                            "Upload completato (" + totalToSend + " righe) ma errore nel rileggere il DB locale.\n\nDettagli: " + err
                    );
                });
                return;
            }

            final List<InventarioRowEntity> finalRemainingRows = remainingRows;

            runOnUiThread(() -> {
                setUiEnabled(true);

                if (adapter != null) adapter.setRows(finalRemainingRows);

                int remaining = finalRemainingRows == null ? 0 : finalRemainingRows.size();
                txtTitle.setText("Righe non importate: " + remaining);
                showDialog("OK", "Import completato. Inviate " + totalToSend + " righe.");
                btnSave.setEnabled(remaining > 0);
            });
        });
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        nomeP = extras.getString("nomeP", "");
        utente = extras.getString("utente", "");
    }

    private void importPendingRows() {
        setUiEnabled(false);

        // 1) check connessione prima di procedere
        checkServerConnectionAsync(
                () -> doImportPendingRows(),   // OK
                () -> {                        // FAIL
                    setUiEnabled(true);
                    showServerConnFailedDialog(
                            this::importPendingRows,
                            () -> showDialog("Info", "Ok, invierai la zona più tardi. Nessuna modifica è stata fatta.")
                    );
                }
        );
    }

    private void doImportPendingRows() {
        io.execute(() -> {
            try {
                List<InventarioRowEntity> pendingRows =
                        db.inventarioDao().getNotImportedByZone(gondola, sparata);

                if (pendingRows == null || pendingRows.isEmpty()) {
                    runOnUiThread(() -> {
                        setUiEnabled(true);
                        showDialog("Info", "Nessuna riga da importare.");
                    });
                    return;
                }

                // 2) Upload SOLO pending
                uploadInventarioToServerRobust(pendingRows);

                // 3) Mark imported=true SOLO dopo upload ok
                db.inventarioDao().markImportedByZone(gondola, sparata);

                // 4) Reload lista
                rows = db.inventarioDao().getRowsByZone(gondola, sparata);

                runOnUiThread(() -> {
                    setUiEnabled(true);
                    adapter.setRows(rows);
                    showDialog("OK", "Importate " + pendingRows.size() + " righe. Ora sono marcate come importate.");
                });

            } catch (Exception e) {
                Log.e(TAG, "Import fallito", e);
                runOnUiThread(() -> {
                    setUiEnabled(true);
                    showServerConnFailedDialog(
                            this::importPendingRows,
                            () -> showDialog("Info", "Ok, puoi inviare la zona in un secondo momento. Le righe restano non importate.")
                    );
                });
            }
        });
    }

    private void loadNotImportedRows() {
        io.execute(() -> {
            rows = db.inventarioDao().getAllNotImported();

            runOnUiThread(() -> {
                if (rows == null || rows.isEmpty()) {
                    txtTitle.setText("Nessuna riga NON importata");
                    adapter = new AdapterReviewInvLocal(this, new ArrayList<>());
                    listView.setAdapter(adapter);
                    btnSave.setEnabled(false);
                    return;
                }

                txtTitle.setText("Righe non importate: " + rows.size());
                adapter = new AdapterReviewInvLocal(this, rows);
                listView.setAdapter(adapter);
                btnSave.setEnabled(true);
            });
        });
    }

    private void setUiEnabled(boolean enabled) {
        runOnUiThread(() -> {
            btnSave.setEnabled(enabled);
            listView.setEnabled(enabled);
            btnFindInInv.setEnabled(enabled);
            insRicInInv.setEnabled(enabled);
        });
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> d.dismiss())
                .show();
    }

    private static String safeMsg(Throwable t) {
        if (t == null) return "Errore sconosciuto";
        String m = t.getMessage();
        return (m == null || m.trim().isEmpty()) ? t.getClass().getSimpleName() : m;
    }

    private static String timing10(long millis) {
        // server: timing varchar(10) -> secondi unix (10 cifre)
        return String.valueOf(millis / 1000L);
    }

    // --- Config connessione (riusa ipNeg)
    private String buildSqlUrlQuickCheck() {
        if (ipNeg == null || ipNeg.trim().isEmpty()) return "";
        return "jdbc:jtds:sqlserver://" + ipNeg +
                "/PassepartoutRetail;" +
                "user=sa;password=SaSqlPass*01;" +
                "loginTimeout=5;" +
                "socketTimeout=5;";
    }

    /** Check veloce: connessione + SELECT 1 */
    private void checkServerConnectionAsync(@NonNull Runnable onOk, @NonNull Runnable onFail) {
        io.execute(() -> {
            boolean ok = false;
            try {
                if (ipNeg == null || ipNeg.trim().isEmpty()) throw new IllegalStateException("ipNeg mancante");
                Class.forName("net.sourceforge.jtds.jdbc.Driver");

                try (java.sql.Connection con = DriverManager.getConnection(buildSqlUrlQuickCheck());
                     java.sql.Statement st = con.createStatement()) {

                    st.setQueryTimeout(5);
                    try (java.sql.ResultSet rs = st.executeQuery("SELECT 1")) {
                        ok = rs.next();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Check connessione server fallito", e);
                ok = false;
            }

            boolean finalOk = ok;
            runOnUiThread(() -> {
                if (finalOk) onOk.run();
                else onFail.run();
            });
        });
    }

    private void showServerConnFailedDialog(@NonNull Runnable onRetry, @NonNull Runnable onLater) {
        new AlertDialog.Builder(ReviewInvLocal.this)
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

    /**
     * Upload robusto:
     * - batch piccoli (es. 100)
     * - commit per batch (non una transazione unica gigante)
     * - in caso di BatchUpdateException, diagnostica con inserimento riga-per-riga
     *   per trovare la riga esatta che rompe.
     */
    /**
     * Upload robusto + anti-doppioni:
     * - batch piccoli (es. 100)
     * - commit per batch
     * - DOPO ogni commit: marca importate in locale SOLO le righe di quel batch
     * - se batch fallisce: rollback del batch e diagnostica riga-per-riga per trovare l’errore preciso
     */
    private void uploadInventarioToServerRobust(List<InventarioRowEntity> pendingRows) throws Exception {
        if (pendingRows == null || pendingRows.isEmpty()) return;
        if (ipNeg == null || ipNeg.trim().isEmpty()) throw new IllegalStateException("ipNeg mancante.");

        Class.forName("net.sourceforge.jtds.jdbc.Driver");

        final String url =
                "jdbc:jtds:sqlserver://" + ipNeg +
                        "/PassepartoutRetail;" +
                        "user=sa;password=SaSqlPass*01;" +
                        "loginTimeout=10;" +
                        "socketTimeout=60;";

        final String sql = "INSERT INTO inventario2023 " +
                "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        final int batchSize = 100;

        try (Connection con = DriverManager.getConnection(url);
             PreparedStatement ps = con.prepareStatement(sql)) {

            con.setAutoCommit(false);
            ps.setQueryTimeout(60);

            int batchStartIndex = 0;
            int inBatch = 0;

            for (int idx = 0; idx < pendingRows.size(); idx++) {
                InventarioRowEntity r = pendingRows.get(idx);

                bindRow(ps, r);
                ps.addBatch();
                inBatch++;

                if (inBatch >= batchSize) {
                    // Esegui batch [batchStartIndex..idx] con diagnostica
                    executeBatchWithDiagnostics(con, pendingRows, batchStartIndex, idx);

                    // Commit del batch: a questo punto le righe sono davvero sul server
                    con.commit();

                    // ✅ Marca in locale SOLO dopo commit riuscito
                    markImportedRange(pendingRows, batchStartIndex, idx);

                    // Preparati al prossimo batch
                    ps.clearBatch();
                    inBatch = 0;
                    batchStartIndex = idx + 1;
                }
            }

            // Batch finale (se rimasto)
            if (inBatch > 0) {
                int lastIdx = pendingRows.size() - 1;

                executeBatchWithDiagnostics(con, pendingRows, batchStartIndex, lastIdx);
                con.commit();

                // ✅ Marca in locale SOLO dopo commit riuscito
                markImportedRange(pendingRows, batchStartIndex, lastIdx);

                ps.clearBatch();
            }
        }
    }

    /**
     * Marca come importate in locale le righe nel range [fromIdx..toIdx].
     * Da chiamare SOLO dopo con.commit() riuscito per quel batch.
     */
    private void markImportedRange(List<InventarioRowEntity> rows, int fromIdx, int toIdx) {
        List<Long> ids = new ArrayList<>();
        for (int i = fromIdx; i <= toIdx; i++) ids.add(rows.get(i).id);
        db.inventarioDao().markImportedByIds(ids);
    }

    /**
     * Esegue l’inserimento del range [fromIdx..toIdx].
     * Se il batch fallisce, fa rollback del batch e poi prova riga-per-riga per capire quale rompe.
     *
     * IMPORTANTISSIMO:
     * - qui NON facciamo commit: lo fa il chiamante subito dopo (così commit e mark restano coerenti).
     */
    private void executeBatchWithDiagnostics(Connection con,
                                             List<InventarioRowEntity> allRows,
                                             int fromIdx,
                                             int toIdx) throws Exception {
        final String sql = "INSERT INTO inventario2023 " +
                "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        // Prova batch “normale”
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setQueryTimeout(60);

            for (int i = fromIdx; i <= toIdx; i++) {
                bindRow(ps, allRows.get(i));
                ps.addBatch();
            }

            ps.executeBatch();
            return; // ✅ batch ok
        } catch (BatchUpdateException bue) {
            Exception next = bue.getNextException();
            Log.e(TAG,
                    "Batch fallito range [" + fromIdx + "-" + toIdx + "] " +
                            "msg=" + safeMsg(bue) +
                            (next != null ? (" next=" + safeMsg(next)) : ""),
                    bue);

            // 🔴 Il batch può aver lasciato lo statement in stato sporco: rollback prima di diagnosticare
            try { con.rollback(); } catch (Exception ignored) {}

            // Diagnostica: prova riga-per-riga nello stesso range, così trovi la riga che rompe
            for (int i = fromIdx; i <= toIdx; i++) {
                InventarioRowEntity r = allRows.get(i);

                try (PreparedStatement one = con.prepareStatement(sql)) {
                    one.setQueryTimeout(60);
                    bindRow(one, r);
                    one.executeUpdate();
                } catch (Exception exRow) {
                    Log.e(TAG,
                            "RIGA FALLITA index=" + i +
                                    " id=" + r.id +
                                    " codArt=" + r.codArt +
                                    " gondola=" + (r.gondola == null ? "" : r.gondola.toUpperCase()) +
                                    " sparata=" + r.sparata +
                                    " qta=" + r.qta +
                                    " timing=" + timing10(r.timing) +
                                    " esistenza=" + r.esistenza +
                                    " store=" + r.store +
                                    " sp1=" + r.sp1 +
                                    " sp2=" + r.sp2 +
                                    " palmare=" + nomeP +
                                    " utente=" + utente +
                                    " ERRORE=" + safeMsg(exRow),
                            exRow);

                    // rollback anche qui: evita mezze scritture nella diagnostica
                    try { con.rollback(); } catch (Exception ignored) {}
                    throw exRow;
                }
            }

            // Se TUTTE le righe singole sono andate, allora questa fase è ok:
            // il chiamante farà commit e poi mark locale.
            return;
        }
    }

    private void bindRow(PreparedStatement ps, InventarioRowEntity r) throws Exception {
        ps.setString(1, safe(r.codArt, 50));
        ps.setString(2, safe(r.gondola.toUpperCase(), 10));
        ps.setInt(3, safeParseInt(r.sparata));      // sparata int sul server
        ps.setInt(4, r.qta);
        ps.setString(5, timing10(r.timing));        // varchar(10)
        ps.setInt(6, r.esistenza);
        ps.setString(7, safe(r.store, 50));
        ps.setInt(8, r.sp1);
        ps.setInt(9, r.sp2);
        ps.setString(10, safe(nomeP, 50));
        ps.setString(11, safe(utente, 50));
    }

    /**
     * Esegue il batch. Se fallisce, stampa info e prova a reinserire riga-per-riga
     * nel range indicato per scoprire ESATTAMENTE quale riga rompe (e perché).
     */
    private void executeBatchWithDiagnostics(Connection con,
                                             PreparedStatement ps,
                                             List<InventarioRowEntity> allRows,
                                             int fromIdx,
                                             int toIdx) throws Exception {
        try {
            ps.executeBatch();
        } catch (BatchUpdateException bue) {
            Exception next = bue.getNextException();
            Log.e(TAG,
                    "Batch fallito range [" + fromIdx + "-" + toIdx + "] " +
                            "msg=" + safeMsg(bue) +
                            (next != null ? (" next=" + safeMsg(next)) : ""),
                    bue);

            // Diagnostica: prova riga per riga per individuare quella errata.
            for (int i = fromIdx; i <= toIdx; i++) {
                InventarioRowEntity r = allRows.get(i);

                try (PreparedStatement one = con.prepareStatement(
                        "INSERT INTO inventario2023 " +
                                "(codArt, gondola, sparata, qta, timing, esistenza, store, sp1, sp2, palmare, utente) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {

                    one.setQueryTimeout(60);
                    bindRow(one, r);
                    one.executeUpdate();

                } catch (Exception exRow) {
                    Log.e(TAG,
                            "RIGA FALLITA index=" + i +
                                    " id=" + r.id +
                                    " codArt=" + r.codArt +
                                    " gondola=" + r.gondola.toUpperCase() +
                                    " sparata=" + r.sparata +
                                    " qta=" + r.qta +
                                    " timing=" + timing10(r.timing) +
                                    " esistenza=" + r.esistenza +
                                    " store=" + r.store +
                                    " sp1=" + r.sp1 +
                                    " sp2=" + r.sp2 +
                                    " palmare=" + nomeP +
                                    " utente=" + utente +
                                    " ERRORE=" + safeMsg(exRow),
                            exRow);

                    // rollback del batch in corso (così non rimani in stato "mezzo sporco")
                    try { con.rollback(); } catch (Exception ignored) {}
                    throw exRow;
                }
            }

            // Se per qualche motivo non troviamo la riga (raro), rilanciamo l'eccezione batch
            try { con.rollback(); } catch (Exception ignored) {}
            throw bue;
        }
    }

    private static int safeParseInt(String s) {
        try {
            return Integer.parseInt(s == null ? "0" : s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static String safe(String s, int maxLen) {
        if (s == null) return "";
        s = s.trim();
        return (s.length() <= maxLen) ? s : s.substring(0, maxLen);
    }
}