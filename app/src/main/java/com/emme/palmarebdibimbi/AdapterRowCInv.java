package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterRowCInv extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt, alias, valDiff;
    private ArrayList<String> desc, es;
    private ArrayList<String> diff;
    private ArrayList<Boolean> isCOn;
    private ArrayList<String> qtaInv;
    private String fileName="",store, utente, categoria;
    private int mag;

    public AdapterRowCInv(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam,
                            ArrayList<String> qtaInvArrayParam, ArrayList<String> esArrayParam, ArrayList<String> aliasArrayParam,
                          ArrayList<String> diffArrayParam, ArrayList<String> valDiff, String fileName, int mag, ArrayList<Boolean> isCon,
                          String store, String utente, String categoria){

        super(context, R.layout.adapter_row_cinv, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.alias = aliasArrayParam;
        this.desc = descArrayParam;
        this.qtaInv = qtaInvArrayParam;
        this.es = esArrayParam;
        this.diff = diffArrayParam;
        this.valDiff = valDiff;
        this.fileName = fileName;
        this.mag = mag;
        this.isCOn = isCon;
        this.store = store;
        this.utente = utente;
        this.categoria = categoria;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_row_cinv, null, true);

        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtCInv);
        TextView txtDesc = rowView.findViewById(R.id.txtDescCInv);
        TextView txtQtaInv = rowView.findViewById(R.id.txtQtaCInv);
        TextView txtEsInv = rowView.findViewById(R.id.txtEsCInv);
        TextView txtValDiffInv = rowView.findViewById(R.id.txtValCInv);
        TextView txtDiffInv = rowView.findViewById(R.id.txtDiffCInv);
        TextView txtEAN = rowView.findViewById(R.id.txtEanCInv);
        Button btnControlla = rowView.findViewById(R.id.btnModCInv);
        LinearLayout backg = rowView.findViewById(R.id.backCInv);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        txtQtaInv.setText(qtaInv.get(position));
        txtEsInv.setText(es.get(position));
        txtDiffInv.setText(diff.get(position));
        txtEAN.setText(alias.get(position));
        txtValDiffInv.setText(valDiff.get(position));

        if(isCOn.get(position)){
            backg.setBackgroundColor(Color.GREEN);
        }
/*
        btnMod.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Modifica valori");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText qta = new EditText(context);
            qta.setHint("Quantità");
            layout.addView(qta);

            final EditText ubicazione = new EditText(context);
            ubicazione.setHint("Ubicazione");
            layout.addView(ubicazione);

            final EditText subicazione = new EditText(context);
            subicazione.setHint("Sottoubicazione");
            layout.addView(subicazione);

            builder.setView(layout);

            builder.setPositiveButton("Salva", (dialog, which) -> {
                if (!qta.getText().toString().equals("")) {
                    qtaInv.set(position, qta.getText().toString());
                    txtQtaInv.setText(qta.getText().toString());
                }
                if (!ubicazione.getText().toString().equals("")) {
                    ubic.set(position, ubicazione.getText().toString());
                    txtUbic.setText(ubicazione.getText().toString());
                }
                if (!subicazione.getText().toString().equals("")) {
                    subic.set(position, subicazione.getText().toString());
                    txtSubic.setText(subicazione.getText().toString());
                }
            });
            builder.setNegativeButton("Annulla", (dialog, which) -> dialog.cancel());

            builder.show();
        });

 */

        btnControlla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent review = new Intent(context, ControllaArticolo.class);
                review.putExtra("codArt",codArt.get(position));
                review.putExtra("desc",desc.get(position));
                review.putExtra("ean",alias.get(position));
                review.putExtra("fileName",fileName);
                review.putExtra("mag", mag);

                review.putExtra("utente", utente);
                review.putExtra("categoria", categoria);
                review.putExtra("store", store);

                context.startActivity(review);
            }
        });

        return rowView;
    }
}
