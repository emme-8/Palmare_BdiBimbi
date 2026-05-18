package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "spunta_documenti",
        indices = {
                @Index("store"),
                @Index("tipoDoc"),
                @Index("completato")
        }
)
public class SpuntaDocumentoEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String fileName = "";
    @NonNull public String docsName = "";
    @NonNull public String tipoDoc = "";
    @NonNull public String store = "";
    @NonNull public String fornitore = "";
    @NonNull public String utente = "";
    @NonNull public String ipNeg = "";
    public int mag = 0;
    public int listino = 0;
    @NonNull public String segnaC = "";
    // 0=spunta, 1=presa, 2=spuntaNeg, 3=spuntaWeb
    public int tipoOperazione = 0;
    public boolean completato = false;
    public boolean emailInviata = false;
    public long dataCreazione = System.currentTimeMillis();
}
