package com.emme.palmarebdibimbi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    private String db = "PassepartoutRetail";
    private String un = "sa";
    private String password = "SaSqlPass*01";
    private String bASardegna = "jdbc:jtds:sqlserver://195.103.42.126/" + db + ";user=" + un + ";password=" + password + ";";
    private String bASardegnaLocale = "jdbc:jtds:sqlserver://192.168.1.41/" + db + ";user=" + un + ";password=" + password + ";";
    @SuppressLint("NewApi")
    public Connection CONN(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
            if(p.getBoolean("Connessione",false)){
                ConnURL = bASardegnaLocale;
            }else{
                ConnURL = bASardegna;
            }
            conn = DriverManager.getConnection(ConnURL);

        }catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }

        return conn;
    }
}