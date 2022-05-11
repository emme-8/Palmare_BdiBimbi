package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class InvActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button cerca = findViewById(R.id.btnFind);
        EditText txtCodArt = findViewById(R.id.txtCodArt);
        EditText txtDesc = findViewById(R.id.txtDesc);
        EditText txtEan = findViewById(R.id.txtEan);
        EditText txtInputEan = findViewById(R.id.txtInputEan);

        Bundle extras = getIntent().getExtras();
        if(extras.getString("externalIp") != null){
            txtDesc.setText(extras.getString("externalIp"));
        }

    }
}