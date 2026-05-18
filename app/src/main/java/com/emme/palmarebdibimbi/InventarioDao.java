package com.emme.palmarebdibimbi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InventarioDao {

    @Insert
    long insert(InventarioRowEntity row);

    @Query("""
        SELECT * FROM inventario_rows
        WHERE gondola = :g AND sparata = :s
          AND (UPPER(codArt) LIKE '%' || UPPER(:q) || '%' OR UPPER(alias) LIKE '%' || UPPER(:q) || '%')
        ORDER BY timing DESC
        """)
    List<InventarioRowEntity> searchInZone(String g, String s, String q);

    @Query("""
        SELECT TRIM(gondola) || ' / ' || TRIM(sparata) AS zona
        FROM inventario_rows
        WHERE gondola IS NOT NULL AND TRIM(gondola) <> ''
          AND sparata IS NOT NULL AND TRIM(sparata) <> ''
        GROUP BY TRIM(gondola), TRIM(sparata)
        ORDER BY TRIM(gondola), TRIM(sparata)
        """)
    List<String> getAllZoneKeys();

    @Query("SELECT COUNT(*) FROM inventario_rows")
    int countAllRows();

    @androidx.room.RawQuery
    android.database.Cursor rawQuery(androidx.sqlite.db.SupportSQLiteQuery query);

    @Query("UPDATE inventario_rows SET imported = 1 WHERE imported = 0 AND gondola = :gondola AND sparata = :sparata")
    int markImportedByZone(String gondola, String sparata);

    @Query("SELECT COUNT(*) FROM inventario_rows WHERE gondola = :g AND sparata = :s")
    int countRowsByZone(String g, String s);

    @Query("DELETE FROM inventario_rows WHERE gondola = :g AND sparata = :s")
    int deleteByZone(String g, String s);

    @Query("SELECT COALESCE(SUM(qta), 0) FROM inventario_rows WHERE gondola = :g AND sparata = :s")
    int sumQtaByZone(String g, String s);

    @Query("""
        SELECT COUNT(DISTINCT codArt)
        FROM inventario_rows
        WHERE gondola = :g AND sparata = :s
          AND codArt IS NOT NULL AND TRIM(codArt) <> ''
        """)
    int countDistinctCodArtByZone(String g, String s);

    // --- Conteggi ---
    @Query("SELECT COUNT(*) FROM inventario_rows")
    int countAll();

    @Query("SELECT COUNT(*) FROM inventario_rows WHERE imported = 0")
    int countNotImported();

    // --- Ultime righe ---
    @Query("SELECT * FROM inventario_rows ORDER BY id DESC LIMIT 1")
    InventarioRowEntity getLastOverall();

    @Query("SELECT * FROM inventario_rows WHERE gondola=:g AND sparata=:s ORDER BY timing DESC")
    List<InventarioRowEntity> getRowsByZone(String g, String s);

    @Query("SELECT * FROM inventario_rows WHERE imported = 0 ORDER BY id DESC LIMIT 1")
    InventarioRowEntity getLastOverallNotImported();

    // Ultima riga nella zona corrente (solo non importate)
    @Query("SELECT * FROM inventario_rows " +
            "WHERE imported = 0 AND gondola = :gondola AND sparata = :sparata " +
            "ORDER BY id DESC LIMIT 1")
    InventarioRowEntity getLastByZoneNotImported(String gondola, String sparata);

    @Query("SELECT COUNT(*) FROM inventario_rows WHERE gondola=:g AND sparata=:s AND imported=1")
    int countImportedByZone(String g, String s);

    @Query("SELECT COUNT(*) FROM inventario_rows WHERE gondola=:g AND sparata=:s AND imported=0")
    int countNotImportedByZone(String g, String s);

    @Query("SELECT * FROM inventario_rows WHERE gondola=:g AND sparata=:s AND imported=0 ORDER BY timing ASC")
    List<InventarioRowEntity> getNotImportedByZone(String g, String s);

    // Tutte le righe da inviare (solo non importate)
    @Query("SELECT * FROM inventario_rows WHERE imported = 0 ORDER BY id ASC")
    List<InventarioRowEntity> getAllNotImported();

    // Mark imported in blocco (per sicurezza uso id)
    @Query("UPDATE inventario_rows SET imported = 1 WHERE id IN (:ids)")
    int markImportedByIds(List<Long> ids);

    // (Opzionale) reset per test/debug
    @Query("UPDATE inventario_rows SET imported = 0")
    int resetImportedAll();

    @Query("SELECT * FROM inventario_rows ORDER BY timing ASC")
    List<InventarioRowEntity> getAll();

    @Query("DELETE FROM inventario_rows")
    void deleteAllInventario();

    @Query("SELECT * FROM inventario_rows WHERE imported = 0 AND (" +
            "codArt LIKE '%' || :q || '%' OR store LIKE '%' || :q || '%' OR gondola LIKE '%' || :q || '%' OR sparata LIKE '%' || :q || '%'" +
            ") ORDER BY gondola, sparata, id")
    List<InventarioRowEntity> searchNotImported(String q);

    @androidx.room.Query("SELECT COUNT(*) FROM inventario_rows WHERE imported = 0")
    int countNotImportedRows();

    @androidx.room.Query("SELECT * FROM inventario_rows WHERE imported = 0")
    List<InventarioRowEntity> getNotImportedRows();

    @androidx.room.Query("UPDATE inventario_rows SET imported = 1 WHERE imported = 0")
    int markImportedAllNotImported();

    @Query("UPDATE inventario_rows SET imported = 1")
    int markImportedAll();

    @androidx.room.Query(
            "SELECT COALESCE(SUM(qta), 0) " +
                    "FROM inventario_rows " +
                    "WHERE gondola = :gondola AND sparata = :sparata AND codArt = :codArt AND imported = 0"
    )
    int sumQtaByZoneAndCodArt(String gondola, String sparata, String codArt);
}