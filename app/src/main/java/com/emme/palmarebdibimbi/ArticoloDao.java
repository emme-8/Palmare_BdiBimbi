package com.emme.palmarebdibimbi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


@Dao
public interface ArticoloDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertArticoli(List<ArticoloEntity> articoli);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertEan(List<ArticoloEanEntity> ean);

    @Query("DELETE FROM articolo_ean")
    void clearEan();

    @Query("DELETE FROM articoli")
    void clearArticoli();

    @Transaction
    default void replaceAll(List<ArticoloEntity> articoli, List<ArticoloEanEntity> ean){
        clearEan();
        clearArticoli();
        upsertArticoli(articoli);
        insertEan(ean);
    }

    @Query("SELECT codArt, `desc`, es, NULL AS alias FROM articoli WHERE codArt = :code LIMIT 1")
    ArticoloMini findByCodArtMini(String code);

    @Query("SELECT a.codArt, a.`desc`, a.es, e.ean AS alias " +
            "FROM articoli a INNER JOIN articolo_ean e ON a.codArt = e.codArt " +
            "WHERE e.ean = :ean LIMIT 1")
    ArticoloMini findByEanMini(String ean);

    @Query("SELECT ean FROM articolo_ean WHERE codArt = :codArt LIMIT 1")
    String findFirstEanByCodArt(String codArt);

    public class ArticoloMini {
        public String codArt;
        public String desc;
        public Integer es;
        public String alias; // l'EAN che ha matchato (può essere null se match su codArt)
    }
}
