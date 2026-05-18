package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "spunta_righe",
        foreignKeys = @ForeignKey(
                entity = SpuntaDocumentoEntity.class,
                parentColumns = "id",
                childColumns = "idSpuntaDoc",
                onDelete = CASCADE
        ),
        indices = {
                @Index("idSpuntaDoc"),
                @Index("codArt"),
                @Index("alias"),
                @Index("nDoc")
        }
)

public class SpuntaRigaEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long idSpuntaDoc;

    @NonNull public String codArt = "";
    @NonNull public String desc = "";
    @NonNull public String alias = "";
    @NonNull public String ubic = "";
    @NonNull public String subic = "";
    public int qtaDoc = 0;
    public int qtaSpunta = 0;
    public int diff = 0;
    @NonNull public String nDoc = "";
    @NonNull public String note = "";
    @NonNull public String store = "";
    @NonNull public String timeSp = "";
    public int colli = 0;
    @NonNull public String costo = "0";
    @NonNull public String idDocRemoto = "";
    @NonNull public String segnacollo = "";

    // Campi extra per presa
    public int esistenza = 0;
    public int impegnati = 0;

    // Campo extra per spuntaNeg/Web
    @NonNull public String rifornimento = "";
}
