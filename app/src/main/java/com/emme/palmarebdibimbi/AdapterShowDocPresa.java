package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterShowDocPresa extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> codArt;
    private ArrayList<String> desc;
    private ArrayList<String> qta;
    private ArrayList<String> ubic;
    private ArrayList<String> subic;

    public AdapterShowDocPresa(Activity context, ArrayList<String> codArtArrayParam, ArrayList<String> descArrayParam, ArrayList<String> qtaArrayParam, ArrayList<String> ubicArrayParam, ArrayList<String> subicArrayParam) {

        super(context, R.layout.adapter_show_doc_presa, codArtArrayParam);

        this.context = context;
        this.codArt = codArtArrayParam;
        this.desc = descArrayParam;
        this.qta = qtaArrayParam;
        this.ubic = ubicArrayParam;
        this.subic = subicArrayParam;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_show_doc_presa, null, true);

        //this code gets references to objects in the listview_row.xml file
        TextView txtCodArt = rowView.findViewById(R.id.txtCodArtRowPresa);
        TextView txtDesc = rowView.findViewById(R.id.txtDescRowPresa);
        TextView txtQta = rowView.findViewById(R.id.txtQtaRowPresa);
        TextView txtUbic = rowView.findViewById(R.id.txtUbiPresa);
        TextView txtSubic = rowView.findViewById(R.id.txtSubiPresa);

        txtCodArt.setText(codArt.get(position));
        txtDesc.setText(desc.get(position));
        String quta = qta.get(position);
        txtQta.setText(quta);
        txtUbic.setText(ubic.get(position));
        txtSubic.setText(subic.get(position));

        return rowView;

    }
}
