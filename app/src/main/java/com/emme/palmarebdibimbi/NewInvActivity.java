package com.emme.palmarebdibimbi;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewInvActivity extends AppCompatActivity {

    private RadioGroup rgMode;
    private RadioButton rbSingle, rbMulti;
    private EditText etStore, etUbic, etAnno, etNumeroSparata;
    private EditText etBarcode, etQty;
    private Button btnRegister;
    private ProgressBar progress;
    private TextView tvName, tvDesc, tvLastError;

    private InventoryApi api;

    private String pendingBarcodeForMulti = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_inv);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        rgMode = findViewById(R.id.rgMode);
        rbSingle = findViewById(R.id.rbSingle);
        rbMulti = findViewById(R.id.rbMulti);

        etStore = findViewById(R.id.etStore);
        etUbic = findViewById(R.id.etUbic);
        etAnno = findViewById(R.id.etAnno);
        etNumeroSparata = findViewById(R.id.etNumeroSparata);

        etBarcode = findViewById(R.id.etBarcode);
        etQty = findViewById(R.id.etQty);
        btnRegister = findViewById(R.id.btnRegister);

        progress = findViewById(R.id.progress);
        tvName = findViewById(R.id.tvName);
        tvDesc = findViewById(R.id.tvDesc);
        tvLastError = findViewById(R.id.tvLastError);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            etStore.setText(extras.getString("storeName"));
            etUbic.setText(extras.getString("ubica").toUpperCase());
            etNumeroSparata.setText(extras.getString("nsp"));
        }

        etStore.setEnabled(false);
        etUbic.setEnabled(false);
        etAnno.setEnabled(false);
        etNumeroSparata.setEnabled(false);

        setupApi();
        setupUiLogic();

        focusBarcode();
    }

    private void setupApi() {
        // TODO: sostituisci con il tuo baseUrl (attenzione: deve finire con /)
        String baseUrl = "http://195.223.50.205:8080/"; // emulatore -> localhost pc
        OkHttpClient client = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(InventoryApi.class);
    }

    private void setupUiLogic() {
        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            boolean multi = (checkedId == R.id.rbMulti);
            setMultiMode(multi);
        });

        // Quando arriva ENTER dal lettore barcode
        etBarcode.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter =
                    actionId == EditorInfo.IME_ACTION_DONE ||
                            (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER);

            if (!isEnter) return false;

            String barcode = safeTrim(etBarcode.getText().toString());
            if (TextUtils.isEmpty(barcode)) return true;

            if (rbSingle.isChecked()) {
                // Sparata singola: qty=1 e invio subito
                sendRegister(barcode, 1);
            } else {
                // Multipla: dopo lettura barcode vai su quantità, abilita bottone
                pendingBarcodeForMulti = barcode;

                etQty.setEnabled(true);
                btnRegister.setEnabled(true);
                etQty.setText("");
                focusAndShowKeyboard(etQty);
            }
            return true;
        });

        btnRegister.setOnClickListener(v -> {
            if (pendingBarcodeForMulti == null) {
                showError("Nessun barcode in attesa. Leggi un articolo prima.");
                focusBarcode();
                return;
            }
            String qtyStr = safeTrim(etQty.getText().toString());
            int qty = parseIntSafe(etQty.getText().toString());
            if (qty == 0) {
                showError("Quantità non valida (0 non ammesso). Usa + o -.");
                focusAndShowKeyboard(etQty);
                return;
            }
            sendRegister(pendingBarcodeForMulti, qty);
        });

        // di default singola
        setMultiMode(false);
    }

    private void setMultiMode(boolean multi) {
        pendingBarcodeForMulti = null;
        etQty.setText("");
        etQty.setEnabled(multi);
        btnRegister.setEnabled(false);
        tvLastError.setText("");

        // in multi voglio che il barcode resti sempre comodo
        focusBarcode();
    }

    private void focusAndShowKeyboard(EditText editText) {
        editText.requestFocus();
        editText.post(() -> {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void sendRegister(String barcode, int qty) {
        String store = safeTrim(etStore.getText().toString());
        String ubic = safeTrim(etUbic.getText().toString());
        int anno = parsePositiveInt(safeTrim(etAnno.getText().toString()));
        int numeroSparata = parsePositiveInt(safeTrim(etNumeroSparata.getText().toString()));

        if (TextUtils.isEmpty(store) || TextUtils.isEmpty(ubic) || anno <= 0 || (numeroSparata != 1 && numeroSparata != 2)) {
            showError("Parametri mancanti/non validi (store, ubic, anno, numero sparata 1/2).");
            focusBarcode();
            return;
        }

        tvLastError.setText("");
        progress.setVisibility(View.VISIBLE);
        lockUi(true);

        InventoryRequest req = new InventoryRequest(barcode, qty, store, ubic, anno, numeroSparata);

        api.register(req).enqueue(new Callback<InventoryResponse>() {
            @Override
            public void onResponse(Call<InventoryResponse> call, Response<InventoryResponse> response) {
                progress.setVisibility(View.GONE);
                lockUi(false);

                if (!response.isSuccessful() || response.body() == null) {
                    playErrorSound();
                    showError("Errore server: HTTP " + response.code());
                    resetAfterSend();
                    return;
                }

                InventoryResponse body = response.body();
                if (!body.success) {
                    playErrorSound();
                    showError(body.message != null ? body.message : "Errore generico.");
                    resetAfterSend();
                    return;
                }

                playSuccessSound();

                tvName.setText("Nome articolo: " + (body.nome != null ? body.nome : "-"));
                tvDesc.setText("Descrizione: " + (body.descrizione != null ? body.descrizione : "-"));
                resetAfterSend();
            }

            @Override
            public void onFailure(Call<InventoryResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                playErrorSound();
                lockUi(false);
                showError("Errore rete: " + (t.getMessage() != null ? t.getMessage() : "sconosciuto"));
                resetAfterSend();
            }
        });
    }

    private void playSuccessSound() {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (uri == null) {
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
            if (r != null) r.play();
        } catch (Exception ignored) {}
    }

    /** Suono ERRORE (alarm/alert) */
    private void playErrorSound() {
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_SUP_ERROR, 300);
    }

    private void resetAfterSend() {
        // pulizia campi input “operativi”
        etBarcode.setText("");
        pendingBarcodeForMulti = null;

        if (rbMulti.isChecked()) {
            etQty.setText("");
            btnRegister.setEnabled(false);
        }

        hideKeyboard();
        focusBarcode();
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    private void lockUi(boolean lock) {
        rgMode.setEnabled(!lock);
        rbSingle.setEnabled(!lock);
        rbMulti.setEnabled(!lock);

        /*
        etStore.setEnabled(!lock);
        etUbic.setEnabled(!lock);
        etAnno.setEnabled(!lock);
        etNumeroSparata.setEnabled(!lock);
         */

        etBarcode.setEnabled(!lock);
        if (rbMulti.isChecked()) {
            etQty.setEnabled(!lock);
            btnRegister.setEnabled(!lock && pendingBarcodeForMulti != null);
        } else {
            etQty.setEnabled(false);
            btnRegister.setEnabled(false);
        }
    }

    private void focusBarcode() {
        etBarcode.requestFocus();
        etBarcode.selectAll();
    }

    private void showError(String msg) {
        tvLastError.setText(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static int parsePositiveInt(String s) {
        try {
            int v = Integer.parseInt(s);
            return Math.max(v, 0);
        } catch (Exception e) {
            return 0;
        }
    }
}