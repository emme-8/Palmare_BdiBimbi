package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterReviewInvLocal extends ArrayAdapter<InventarioRowEntity> {

    private final Activity context;
    private List<InventarioRowEntity> rows = new ArrayList<>();

    public AdapterReviewInvLocal(Activity context, List<InventarioRowEntity> rows) {
        super(context, R.layout.adapter_review_inv_local, rows);
        this.context = context;
        this.rows = rows != null ? rows : new ArrayList<>();
    }

    public void setRows(List<InventarioRowEntity> newRows) {
        this.rows = newRows != null ? newRows : new ArrayList<>();
        clear();
        addAll(this.rows);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.adapter_review_inv_local, parent, false);
        }

        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowInv);
        TextView txtDesc   = rowView.findViewById(R.id.txtDescRowInv);
        TextView txtAlias  = rowView.findViewById(R.id.txtAliasRowInv);

        TextView txtQta    = rowView.findViewById(R.id.txtQtaRowInv);
        TextView txtUbi    = rowView.findViewById(R.id.txtUbicRowInv);
        TextView txtStatus = rowView.findViewById(R.id.txtImportedStatus);

        InventarioRowEntity r = rows.get(position);

        txtCodArt.setText(nonNull(r.codArt));
        txtQta.setText(String.valueOf(r.qta));
        txtUbi.setText(nonNull(r.gondola) + " / " + nonNull(r.sparata));
        txtStatus.setText(r.imported ? "IMPORTATA" : "DA IMPORTARE");

        // ✅ descrizione + alias (devono essere presenti nell'Entity)
        txtDesc.setText(nonNull(r.desc));
        txtAlias.setText(nonNull(r.alias));

        return rowView;
    }

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }
}