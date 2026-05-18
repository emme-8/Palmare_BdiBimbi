package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Cache locale dei dati di dettaglio degli articoli per un documento di spunta.
 * Viene popolata da PreloadArticoli all'avvio della spunta con una singola query SQL
 * (IN clause su tutti i codici del documento), poi FindArt legge da qui senza più
 * aprire connessioni al server.
 */
@Entity(
        tableName = "spunta_art_cache",
        foreignKeys = @ForeignKey(
                entity = SpuntaDocumentoEntity.class,
                parentColumns = "id",
                childColumns = "idSpuntaDoc",
                onDelete = CASCADE
        ),
        indices = {
                @Index("idSpuntaDoc"),
                @Index({"idSpuntaDoc", "codArt"})
        }
)
public class SpuntaArticoloCacheEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long idSpuntaDoc;

    @NonNull public String codArt = "";
    @NonNull public String desc = "";
    public int esistenza = 0;
    public int ordFornitore = 0;
    public int ordCliente = 0;
    public double prz = 0.0;
    @NonNull public String ubic = "";
    @NonNull public String subic = "";
    public int scorta = 0;
    @NonNull public String przPromo = "";
    @NonNull public String inizioPromo = "";
    @NonNull public String finePromo = "";
}
