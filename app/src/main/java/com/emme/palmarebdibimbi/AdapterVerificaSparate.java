package com.emme.palmarebdibimbi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class AdapterVerificaSparate  extends ArrayAdapter {

    private final Activity context;
    private ArrayList<String> zone, qta;

    public AdapterVerificaSparate(Activity context, ArrayList<String> zonaArrayParam,
                            ArrayList<String> qtaInvArrayParam){

        super(context, R.layout.adapter_verifica_sparate, zonaArrayParam);

        this.context = context;
        this.qta = qtaInvArrayParam;
        this.zone = zonaArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adapter_verifica_sparate, null, true);

        TextView txtZona = rowView.findViewById(R.id.txtZonaV);
        TextView txtQta = rowView.findViewById(R.id.txtQtaV);

        txtZona.setText(zone.get(position));
        txtQta.setText(qta.get(position));

        return rowView;
    }

}
