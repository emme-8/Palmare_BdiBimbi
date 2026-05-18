package com.emme.palmarebdibimbi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

    public class ShowDocWebActivity extends AppCompatActivity implements Serializable {

        ConnectionClass connectionClass;
        ArrayList<String> codArt = new ArrayList<>();
        ArrayList<String> costo = new ArrayList<>();
        ArrayList<String> alias = new ArrayList<>();
        ArrayList<String> desc = new ArrayList<>();
        ArrayList<String> qta = new ArrayList<>();
        ArrayList<String> idDoc = new ArrayList<>();
        ArrayList<String> serieDoc = new ArrayList<>();
        ArrayList<String> numDoc = new ArrayList<>();
        ArrayList<String> ubicaz = new ArrayList<>();
        ArrayList<String> subic = new ArrayList<>();
        ArrayList<String> esistenza = new ArrayList<>();
        ArrayList<String> impegnati = new ArrayList<>();
        ArrayList<String> przL = new ArrayList<>();
        ArrayList<String> przP = new ArrayList<>();
        ArrayList<String> UDM = new ArrayList<>();
        ArrayList<Double> conv = new ArrayList<>();
        ArrayList<Articolo> artDoc = new ArrayList<>();
        int listino, mag, spuntaOrPresa, magRif, listinoRif;
        ListView listView;
        TextView txtInfoDic;
        String fornitore = "", ipNeg = "";
        String tipo, utente;
        String magazzino;
        int ordinamento;
        int tipoEs;
        SharedPreferences preferences;
        boolean isOnline, smartMode;
        String whereID, whereNum, whereSel, segnaC;
        Context thisContext;
        Boolean ubic;
        Button btnSpunta;
        Context context;
        String idXMail;
        boolean isRemoto;
        ProgressBar pbShowDoc;
        private long idSpuntaDocRoom = -1;

        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed(); // one inherited from android.support.v4.app.FragmentActivity
            return false;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_doc_web);
            thisContext = this;

            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }

            context = this;

            btnSpunta = findViewById(R.id.btnSpuntaDoc);
            pbShowDoc = findViewById(R.id.pbShowDoc);
            pbShowDoc.setVisibility(View.GONE);
            listView = findViewById(R.id.showDocRow);
            txtInfoDic = findViewById(R.id.txtInfoDoc);

            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            isOnline = preferences.getBoolean("isOnline", true);
            smartMode = preferences.getBoolean("smartMode", true);

            Bundle extras = getIntent().getExtras();
            String ids = ""; String docs = "";
            if(extras != null){
                for(int i=0; i<extras.getStringArrayList("selIds").size(); i++){
                    if(i==0){
                        idXMail="'"+extras.getStringArrayList("selIds").get(i)+"'";
                        ids="'"+extras.getStringArrayList("selIds").get(i)+"'";
                        docs="'"+extras.getStringArrayList("selDocs").get(i)+"'";
                    }else{
                        idXMail+="- '"+extras.getStringArrayList("selIds").get(i)+"'";
                        ids+=", '"+extras.getStringArrayList("selIds").get(i)+"'";
                        docs+=", '"+extras.getStringArrayList("selDocs").get(i)+"'";
                    }
                }
                isRemoto = extras.getBoolean("isRemoto");
                ipNeg = extras.getString("ipNeg");
                spuntaOrPresa = extras.getInt("spuntaOrPresa");
                mag = extras.getInt("mag");
                magRif = extras.getInt("magRif");
                fornitore = extras.getString("fornitore");
                utente = extras.getString("utente");
                segnaC = extras.getString("segnaC");
                magazzino = extras.getString("magazzino");
                tipo = extras.getString("tipo");
                listino = extras.getInt("listino");
                listinoRif = extras.getInt("listinoRif");
                ubic = extras.getBoolean("ubicazione");
                whereID = " and Documento.id in (" + ids + ") ";
                whereNum = " and Documento.numero in (" + docs + ") ";
                whereSel = " and RigaDocumento.selettore like '" + extras.getString("tipoRiga") + "' ";
            }

            txtInfoDic.setText(extras.getString("tipo") + " N. " + docs);

            connectionClass = new ConnectionClass();

                btnSpunta.setText("INIZIA SPUNTA");
                ShowDocWebActivity.FindRows cerca = new ShowDocWebActivity.FindRows();
                cerca.execute("");


            btnSpunta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCodPerSpunta();
                    Intent spunta;

                    spunta = new Intent(ShowDocWebActivity.this, IniziaSpuntaWebActivity.class);

                    spunta.putExtra("listino", listino);
                    spunta.putExtra("tipoDoc", tipo);
                    spunta.putExtra("mag", mag);
                    spunta.putExtra("segnaC", segnaC);
                    spunta.putExtra("ipNeg", ipNeg);
                    spunta.putExtra("isRemoto", isRemoto);
                    spunta.putExtra("fornitore", fornitore);
                    spunta.putExtra("utente", utente);
                    spunta.putExtra("rip", 0);
                    spunta.putExtra("idSpuntaDocRoom", idSpuntaDocRoom);
                    startActivity(spunta);
                }
            });
        }

        public void setCodPerSpunta(){
            if(spuntaOrPresa == 0 || spuntaOrPresa == 10){
                ((MyApplication) this.getApplication()).setCodArt(codArt);
                ((MyApplication) this.getApplication()).setQuantita(qta);
                ((MyApplication) this.getApplication()).setID(idDoc);
                ((MyApplication) this.getApplication()).setID(idDoc);
                ((MyApplication) this.getApplication()).setNum(numDoc);
                ((MyApplication) this.getApplication()).setSerie(serieDoc);
                ((MyApplication) this.getApplication()).setDesc(desc);
                ((MyApplication) this.getApplication()).setCosto(costo);

                salvaRigheInRoom();
            }else if(spuntaOrPresa == 1){
                ((MyApplication) this.getApplication()).setCodArt(codArt);
                ((MyApplication) this.getApplication()).setAlias(alias);
                ((MyApplication) this.getApplication()).setSerie(serieDoc);
                ((MyApplication) this.getApplication()).setQuantita(qta);
                ((MyApplication) this.getApplication()).setID(idDoc);
                ((MyApplication) this.getApplication()).setNum(numDoc);
                ((MyApplication) this.getApplication()).setDesc(desc);
                ((MyApplication) this.getApplication()).setUbic(ubicaz);
                ((MyApplication) this.getApplication()).setSubic(subic);
                ((MyApplication) this.getApplication()).setEsistenza(esistenza);
                ((MyApplication) this.getApplication()).setImpegnati(impegnati);
            }else{
                ((MyApplication) this.getApplication()).setCodArt(codArt);
                ((MyApplication) this.getApplication()).setQuantita(qta);
                ((MyApplication) this.getApplication()).setID(idDoc);
                ((MyApplication) this.getApplication()).setNum(numDoc);
                ((MyApplication) this.getApplication()).setDesc(desc);
            }
        }

        private void salvaRigheInRoom(){
            try {
                    AppDb db = AppDb.getInstance(getApplicationContext());
                    SpuntaDao dao = db.spuntaDao();

                    SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(thisContext);
                    String nomeP = p.getString("NomePalm","");
                    String forn = fornitore.replace("è","e").replace("é","e")
                            .replace("à","a").replace("ì","i")
                            .replace("ò","o").replace("ù","u").replace("'","");

                    String docsName = "";
                    String tempnDoc = "";
                    for(int z=0; z<numDoc.size(); z++){
                        if(z==0){
                            docsName = tipo + "_" + forn + "_" + numDoc.get(0) + "_" + serieDoc.get(0);
                        }else if(!numDoc.get(z).equals(tempnDoc)){
                            docsName = docsName + "_" + numDoc.get(z) + "_" + serieDoc.get(z);
                        }
                        tempnDoc = numDoc.get(z);
                    }
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    String fileName = nomeP + "_spunta_" + docsName + "_" + year + ".xlsx";

                    SpuntaDocumentoEntity docEsistente = dao.getDocumentoByFileName(fileName);
                    if(docEsistente != null){
                        final long idEsistente = docEsistente.id;
                        final String fFileName = fileName;
                        final String fDocsName = docsName;
                        new android.app.AlertDialog.Builder(thisContext)
                            .setTitle("Documento già in corso")
                            .setMessage("Questo documento ha già una scansione in corso.\nVuoi continuare o ricominciare da zero?")
                            .setPositiveButton("Continua", (d, w) -> {
                                idSpuntaDocRoom = idEsistente;
                            })
                            .setNegativeButton("Ricomincia", (d, w) -> {
                                dao.deleteDocumentoCompleto(idEsistente);
                                File excelFile = new File("/storage/emulated/0/NAS/SpuntaGen", fFileName);
                                if(excelFile.exists()) excelFile.delete();
                                creaDocumentoSpuntaWebInRoom(dao, fFileName, fDocsName);
                            })
                            .setCancelable(false)
                            .show();
                        return;
                    }

                    creaDocumentoSpuntaWebInRoom(dao, fileName, docsName);
                } catch(Exception e){
                    e.printStackTrace();
                }
        }

        private void creaDocumentoSpuntaWebInRoom(SpuntaDao dao, String fileName, String docsName){
            try {
                    String forn = fornitore.replace("è","e").replace("é","e")
                            .replace("à","a").replace("ì","i")
                            .replace("ò","o").replace("ù","u").replace("'","");

                    SpuntaDocumentoEntity doc = new SpuntaDocumentoEntity();
                    doc.fileName = fileName;
                    doc.docsName = docsName;
                    doc.tipoDoc = tipo;
                    doc.store = "CEDIROMA";
                    doc.fornitore = forn;
                    doc.utente = utente != null ? utente : "";
                    doc.ipNeg = ipNeg != null ? ipNeg : "";
                    doc.mag = mag;
                    doc.listino = listino;
                    doc.segnaC = segnaC != null ? segnaC : "";
                    doc.tipoOperazione = 3;

                    long docId = dao.insertDocumento(doc);
                    idSpuntaDocRoom = docId;

                    // Aggregazione duplicati
                    ArrayList<String> tempCodArt = new ArrayList<>();
                    ArrayList<String> tempQta = new ArrayList<>();
                    ArrayList<String> tempIdDoc = new ArrayList<>();
                    ArrayList<String> tempNumDoc2 = new ArrayList<>();
                    ArrayList<String> tempDesc2 = new ArrayList<>();
                    ArrayList<String> tempCosto2 = new ArrayList<>();

                    for(int i=0; i<codArt.size(); i++){
                        boolean ce = false;
                        if(i == 0){
                            tempCodArt.add(codArt.get(i));
                            tempQta.add(qta.get(i));
                            tempIdDoc.add(idDoc.get(i));
                            tempNumDoc2.add(numDoc.get(i));
                            tempDesc2.add(desc.get(i));
                            tempCosto2.add(costo.get(i));
                        }else{
                            for(int j=0; j<tempCodArt.size(); j++){
                                if(tempCodArt.get(j).equals(codArt.get(i)) && tempIdDoc.get(j).equals(idDoc.get(i))){
                                    int qDaAgg = Integer.parseInt(qta.get(i));
                                    int qPres = Integer.parseInt(tempQta.get(j));
                                    tempQta.set(j, String.valueOf(qDaAgg + qPres));
                                    ce = true;
                                    break;
                                }
                            }
                            if(!ce){
                                tempCodArt.add(codArt.get(i));
                                tempQta.add(qta.get(i));
                                tempIdDoc.add(idDoc.get(i));
                                tempNumDoc2.add(numDoc.get(i));
                                tempDesc2.add(desc.get(i));
                                tempCosto2.add(costo.get(i));
                            }
                        }
                    }

                    List<SpuntaRigaEntity> righe = new ArrayList<>();
                    for(int i=0; i<tempCodArt.size(); i++){
                        SpuntaRigaEntity riga = new SpuntaRigaEntity();
                        riga.idSpuntaDoc = docId;
                        riga.codArt = tempCodArt.get(i);
                        riga.desc = tempDesc2.get(i);
                        riga.qtaDoc = Integer.parseInt(tempQta.get(i));
                        riga.qtaSpunta = 0;
                        riga.diff = -Integer.parseInt(tempQta.get(i));
                        riga.nDoc = tempNumDoc2.get(i);
                        riga.store = "CEDIROMA";
                        riga.costo = tempCosto2.get(i);
                        riga.idDocRemoto = tempIdDoc.get(i);
                        righe.add(riga);
                    }
                    dao.insertRighe(righe);
                } catch(Exception e){
                    e.printStackTrace();
                }
        }

        public void setRows(){
            if(spuntaOrPresa == 0 || spuntaOrPresa == 10){
                if(!isOnline){
                    for(int i=0; i<artDoc.size(); i++){
                        codArt.add(artDoc.get(i).getCodArt());
                        desc.add(artDoc.get(i).getDesc());
                        qta.add(artDoc.get(i).getQtaDoc().toString());
                        idDoc.add(artDoc.get(i).getIdDoc());
                        numDoc.add(artDoc.get(i).getNumDoc());
                    }
                }
                AdapterShowDoc whatever = new AdapterShowDoc(this, codArt, desc, qta);
                listView.setAdapter(whatever);
            }else if(spuntaOrPresa == 1){
                AdapterShowDocPresa adapterShowDocPresa = new AdapterShowDocPresa(this, codArt, desc, qta, ubicaz, subic);
                listView.setAdapter(adapterShowDocPresa);
            }else if(spuntaOrPresa == 2){
                AdapterShowDoc whatever = new AdapterShowDoc(this, codArt, desc, qta);
                listView.setAdapter(whatever);
            }else{
                AdapterShowDocPrintLabel whatever = new AdapterShowDocPrintLabel(this, codArt, desc, alias, qta, esistenza, przL, przP, UDM, conv);
                listView.setAdapter(whatever);
            }
        }

        public class FindRows extends AsyncTask<String,String,String> {
            String z = "";
            Boolean isSuccess = false;
            ResultSet res;

            @Override
            protected void onPreExecute(){
                listView.setVisibility(View.GONE);
                btnSpunta.setVisibility(View.GONE);
                pbShowDoc.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String r) {
                if(isSuccess) {
                    pbShowDoc.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    btnSpunta.setVisibility(View.VISIBLE);
                    setRows();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    Connection con = connectionClass.CONN(thisContext);
                    if (con == null) {
                        z = "Errore di connessione con il server";
                    } else {
                        String query = "select articolo.nome as codiceArticolo, descrizione, quantita, documento.id, documento.serie, documento.numero, rigaDocumento.idMagazzinoDestinazione, " +
                                "cast(prezzoUnitario as decimal(18,2)) as costoUltimo\n" +
                                "from RigaDocumento join Documento on (RigaDocumento.idMaster = Documento.id) " +
                                "join articolo on (articolo.id = RigaDocumento.idArticolo) " +
                                "where Documento.id is not null " + whereID + whereNum + whereSel +
                                " and codiceArticolo not like '.%' and codiceArticolo not like ',%'";
                        Statement stmt = con.createStatement();
                        res = stmt.executeQuery(query);
                        while(res.next()) {

                            if(res.getString("costoUltimo")!=null){
                                costo.add(res.getString("costoUltimo"));
                            }else{
                                costo.add("0.0");
                            }
                            codArt.add(res.getString("codiceArticolo"));
                            desc.add(res.getString("descrizione"));
                            qta.add(res.getString("quantita").substring(0,res.getString("quantita").indexOf(".")));
                            idDoc.add(res.getString("id"));
                            serieDoc.add(res.getString("serie"));
                            numDoc.add(res.getString("numero"));
                        }
                        if (!(codArt.isEmpty())) {
                            //z = "Ordine trovato";
                            isSuccess = true;
                        }
                    }
                }
                catch (Exception ex) {
                    isSuccess = false;
                    z = "Errore";
                }
                return z;
            }
            public boolean sendEmail(String[] to, String from, String subject,
                                     String message, String user, String pass) throws Exception {
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

                return mail.send();
            }

            private void righeDisc(String title,String message){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.cancel();
                            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
                            String email = p.getString("Email", "");
                            String emailPass = p.getString("EmailPass", "");

                            String obj = magazzino + " errore spunta doc " + idXMail;
                            String msg = "Uno o più documenti con id " + idXMail + " presentano righe con magazzino discordante ";
                            String[] to = new String[]{"spunte@bdibimbi.it", email};
                            try {
                                sendEmail(to,email, obj, msg, email, emailPass);
                                Intent retHome = new Intent(context, MainActivity.class);
                                startActivity(retHome);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                                emailError("Errore","E' avvenuto un errore durante l'invio della segnalazione",to, obj, email, emailPass, msg);
                            }
                        });
                android.app.AlertDialog ok = builder.create();
                ok.show();
            }

            private void emailError(String title,String message, String[] to, String obj, String email, String emailPass, String msg){
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton("Riprova", ((dialog, which) -> {
                            try {
                                sendEmail(to,email, obj, msg, email, emailPass);
                                alertDisplayer("Attenzione!", "Segnalazione inviata con successo, verrai riportato alla home");
                            } catch (Exception e) {
                                e.printStackTrace();
                                emailError("Errore","E' avvenuto un errore durante l'invio della segnalazione",to, obj, email, emailPass, msg);
                            }
                        }));
                AlertDialog ok = builder.create();
                ok.show();
            }

            private void alertDisplayer(String title,String message){
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.cancel();
                            Intent retHome = new Intent(context, MainActivity.class);
                            startActivity(retHome);
                            finish();
                        });
                AlertDialog ok = builder.create();
                ok.show();
            }

        }
    }