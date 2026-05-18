package com.emme.palmarebdibimbi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SpuntaDao {

    // ---- Documento ----

    @Insert
    long insertDocumento(SpuntaDocumentoEntity doc);

    @Update
    void updateDocumento(SpuntaDocumentoEntity doc);

    @Query("SELECT * FROM spunta_documenti WHERE id = :id")
    SpuntaDocumentoEntity getDocumentoById(long id);

    @Query("SELECT * FROM spunta_documenti WHERE fileName = :fileName LIMIT 1")
    SpuntaDocumentoEntity getDocumentoByFileName(String fileName);

    @Query("SELECT * FROM spunta_documenti WHERE completato = 0 ORDER BY dataCreazione DESC")
    List<SpuntaDocumentoEntity> getDocumentiInCorso();

    @Query("SELECT * FROM spunta_documenti WHERE completato = 1 ORDER BY dataCreazione DESC")
    List<SpuntaDocumentoEntity> getDocumentiCompletati();

    @Query("SELECT * FROM spunta_documenti ORDER BY dataCreazione DESC")
    List<SpuntaDocumentoEntity> getTuttiDocumenti();

    @Query("UPDATE spunta_documenti SET completato = 1 WHERE id = :id")
    void completaDocumento(long id);

    @Query("UPDATE spunta_documenti SET emailInviata = 1 WHERE id = :id")
    void marcaEmailInviata(long id);

    @Query("SELECT * FROM spunta_documenti WHERE completato = 1 AND emailInviata = 0 ORDER BY dataCreazione DESC")
    List<SpuntaDocumentoEntity> getDocumentiCompletatiNonInviati();

    @Query("DELETE FROM spunta_documenti WHERE id = :id")
    void deleteDocumento(long id);

    // ---- Righe ----

    @Insert
    long insertRiga(SpuntaRigaEntity riga);

    @Insert
    void insertRighe(List<SpuntaRigaEntity> righe);

    @Update
    void updateRiga(SpuntaRigaEntity riga);

    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc ORDER BY id ASC")
    List<SpuntaRigaEntity> getRigheByDocumento(long idDoc);

    @Query("SELECT * FROM spunta_righe WHERE id = :id")
    SpuntaRigaEntity getRigaById(long id);

    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND codArt = :codArt ORDER BY id ASC")
    List<SpuntaRigaEntity> getRigheByCodArt(long idDoc, String codArt);

    // Ricerca per alias o codice articolo
    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND " +
            "(UPPER(alias) LIKE '%' || UPPER(:query) || '%' OR UPPER(codArt) LIKE '%' || UPPER(:query) || '%') " +
            "ORDER BY id ASC")
    List<SpuntaRigaEntity> cercaRighe(long idDoc, String query);

    // Ricerca prima per alias, poi fallback su codArt
    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND " +
            "UPPER(alias) LIKE '%' || UPPER(:query) || '%' ORDER BY id ASC")
    List<SpuntaRigaEntity> cercaPerAlias(long idDoc, String query);

    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND " +
            "UPPER(codArt) LIKE '%' || UPPER(:query) || '%' ORDER BY id ASC")
    List<SpuntaRigaEntity> cercaPerCodArt(long idDoc, String query);

    // Aggiornamento quantità spuntata
    @Query("UPDATE spunta_righe SET qtaSpunta = :qtaSpunta, alias = :alias, " +
            "timeSp = :timeSp, ubic = :ubic, subic = :subic, diff = qtaSpunta - qtaDoc WHERE id = :id")
    void aggiornaSpunta(long id, int qtaSpunta, String alias, String timeSp, String ubic, String subic);

    // Aggiorna solo qta spunta incrementale
    @Query("UPDATE spunta_righe SET qtaSpunta = qtaSpunta + :qtaDaAggiungere, " +
            "alias = :alias, timeSp = :timeSp, ubic = :ubic, subic = :subic, " +
            "diff = (qtaSpunta + :qtaDaAggiungere) - qtaDoc WHERE id = :id")
    void incrementaSpunta(long id, int qtaDaAggiungere, String alias, String timeSp, String ubic, String subic);

    // Aggiorna colli
    @Query("UPDATE spunta_righe SET colli = colli + :colliDaAggiungere WHERE id = :id")
    void incrementaColli(long id, int colliDaAggiungere);

    // Aggiorna ubicazione per tutte le righe dello stesso articolo nel documento
    @Query("UPDATE spunta_righe SET ubic = :ubic, subic = :subic WHERE idSpuntaDoc = :idDoc AND codArt = :codArt")
    void aggiornaUbicPerArticolo(long idDoc, String codArt, String ubic, String subic);

    // Conteggi
    @Query("SELECT COUNT(*) FROM spunta_righe WHERE idSpuntaDoc = :idDoc")
    int countRighe(long idDoc);

    @Query("SELECT COUNT(*) FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND (qtaSpunta - qtaDoc) != 0")
    int countDiscordanze(long idDoc);

    @Query("SELECT COALESCE(SUM(qtaSpunta), 0) FROM spunta_righe WHERE idSpuntaDoc = :idDoc")
    int sumQtaSpunta(long idDoc);

    @Query("SELECT COALESCE(SUM(colli), 0) FROM spunta_righe WHERE idSpuntaDoc = :idDoc")
    int sumColli(long idDoc);

    // Ricalcola diff per tutte le righe di un documento
    @Query("UPDATE spunta_righe SET diff = qtaSpunta - qtaDoc WHERE idSpuntaDoc = :idDoc")
    void ricalcolaDiff(long idDoc);

    // Segnacollo
    @Query("SELECT * FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND codArt = :codArt AND segnacollo = :segnacollo LIMIT 1")
    SpuntaRigaEntity getRigaPerSegnacollo(long idDoc, String codArt, String segnacollo);

    // Somma quantità per numero segnacollo
    @Query("SELECT COALESCE(SUM(qtaSpunta), 0) FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND segnacollo = :segnacollo")
    int sumQtaPerSegnacollo(long idDoc, String segnacollo);

    // Note per articolo
    @Query("SELECT note FROM spunta_righe WHERE idSpuntaDoc = :idDoc AND codArt = :codArt LIMIT 1")
    String getNotePerArticolo(long idDoc, String codArt);

    @Query("UPDATE spunta_righe SET note = :note WHERE idSpuntaDoc = :idDoc AND codArt = :codArt")
    void aggiornaNotePerArticolo(long idDoc, String codArt, String note);

    // Pulizia
    @Query("DELETE FROM spunta_righe WHERE idSpuntaDoc = :idDoc")
    void deleteRigheByDocumento(long idDoc);

    @Transaction
    default void deleteDocumentoCompleto(long idDoc) {
        deleteRigheByDocumento(idDoc);
        deleteDocumento(idDoc);
    }

    // ---- EAN / Barcode ----

    @Insert
    void insertEan(SpuntaEanEntity ean);

    @Query("SELECT * FROM spunta_ean WHERE idSpuntaDoc = :idDoc ORDER BY id ASC")
    List<SpuntaEanEntity> getEanByDocumento(long idDoc);

    @Query("SELECT * FROM spunta_ean WHERE idSpuntaDoc = :idDoc AND codArt = :codArt ORDER BY id ASC")
    List<SpuntaEanEntity> getEanByCodArt(long idDoc, String codArt);

    @Query("DELETE FROM spunta_ean WHERE idSpuntaDoc = :idDoc")
    void deleteEanByDocumento(long idDoc);

    // ---- Cache articoli per spunta ----

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCacheArticoli(List<SpuntaArticoloCacheEntity> list);

    @Query("SELECT * FROM spunta_art_cache WHERE idSpuntaDoc = :idDoc AND codArt = :codArt LIMIT 1")
    SpuntaArticoloCacheEntity getCacheArticolo(long idDoc, String codArt);

    @Query("DELETE FROM spunta_art_cache WHERE idSpuntaDoc = :idDoc")
    void deleteCacheByDocumento(long idDoc);
}
