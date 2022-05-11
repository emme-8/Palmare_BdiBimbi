package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterShowDoc extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt;
    private ArrayList<String> desc;
    private ArrayList<String> qta;

    public AdapterShowDoc(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> qtaArrayParam) {

        super(context, R.layout.adapter_rowdoc, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qta = qtaArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_rowdoc, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRow);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRow);
        TextView txtQta = rowView.findViewById(R.id.txtQtaRow);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        String quta = qta.get(position);
        txtQta.setText(quta);

        return rowView;

    }
}
