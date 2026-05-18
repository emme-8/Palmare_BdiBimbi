package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "spunta_ean",
        foreignKeys = @ForeignKey(
                entity = SpuntaDocumentoEntity.class,
                parentColumns = "id",
                childColumns = "idSpuntaDoc",
                onDelete = CASCADE
        ),
        indices = {
                @Index("idSpuntaDoc"),
                @Index("codArt")
        }
)
public class SpuntaEanEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long idSpuntaDoc;

    @NonNull public String codArt = "";
    @NonNull public String ean = "";
}
