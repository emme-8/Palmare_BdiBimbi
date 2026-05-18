package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "articolo_ean",
        primaryKeys = {"codArt", "ean"},
        foreignKeys = @ForeignKey(
                entity = ArticoloEntity.class,
                parentColumns = "codArt",
                childColumns = "codArt",
                onDelete = CASCADE
        ),
        indices = {@Index("codArt"), @Index("ean")}
)
public class ArticoloEanEntity {
    @NonNull public String codArt;
    @NonNull public String ean;

    public ArticoloEanEntity(@NonNull String codArt, @NonNull String ean){
        this.codArt = codArt;
        this.ean = ean;
    }
}
