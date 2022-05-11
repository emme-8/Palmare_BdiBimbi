package com.emme.palmarebdibimbi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HomeInventario extends AppCompatActivity {

    String store = "";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_inventario);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            store = extras.getString("storeName");
        }

        Button invOn = findViewById(R.id.btnInvOn);
        Button invOff = findViewById(R.id.btnInvOff);
        Button btnsendFiles = findViewById(R.id.btnInvFile);

        invOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inv = new Intent(HomeInventario.this,InvDep.class);
                inv.putExtra("storeName", store);
                startActivity(inv);
            }
        });
        invOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertArt("Attenzione!","Inserisci i seguenti campi per iniziare o riprendere l'inventario");
            }
        });
        btnsendFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
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

    private void alertArt(String title,String message){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeInventario.this)
                .setTitle(title)
                .setMessage(message);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nG = new EditText(this);
        nG.setHint("Numero gondola");
        layout.addView(nG);

        final EditText nSp = new EditText(this);
        nSp.setHint("Numero sparata");
        layout.addView(nSp);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(nG.getText().toString().equals("")){
                nG.setHintTextColor(Color.RED);
                alertArt("Errore!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else if(nSp.getText().toString().equals("")){
                nSp.setHintTextColor(Color.RED);
                alertArt("Errore!", "Wait bruh, what are u doing? Put something in textbox pls");
            }else{
                dialog.cancel();
                Intent inv = new Intent(HomeInventario.this,InvDepOff.class);
                inv.putExtra("nSp", nSp.getText().toString());
                inv.putExtra("nG", nG.getText().toString());
                inv.putExtra("storeName", store);
                startActivity(inv);
            }
        });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }
}