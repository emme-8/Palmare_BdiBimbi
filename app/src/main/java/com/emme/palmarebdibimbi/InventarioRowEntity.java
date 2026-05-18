package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "inventario_rows",
        indices = {
                @Index("codArt"),
                @Index("store"),
                @Index("gondola"),
                @Index("sparata"),
                @Index("timing"),
                @Index("imported")
        }
)
public class InventarioRowEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String codArt = "";
    @NonNull public String gondola = "";
    @NonNull public String sparata = ""; // es: "1" o "2"

    @NonNull public String desc = "";
    @NonNull public String alias = "";

    public int qta = 0;
    public long timing = 0L;
    public int esistenza = 0;

    @NonNull public String store = "";

    public int sp1 = 0;
    public int sp2 = 0;

    // NEW: sincronizzata su server?
    public boolean imported = false;
}