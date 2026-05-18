package com.emme.palmarebdibimbi;

import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "articoli",
        indices = {
                @Index(value = {"codArt"}, unique = true),
                @Index(value = {"desc"})
        }
)
public class ArticoloEntity {
    @PrimaryKey @NonNull
    public String codArt;

    public String desc;
    public Integer es;
}
