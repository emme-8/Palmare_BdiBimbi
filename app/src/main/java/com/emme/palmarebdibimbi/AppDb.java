package com.emme.palmarebdibimbi;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {
                ArticoloEntity.class,
                ArticoloEanEntity.class,
                InventarioRowEntity.class,
                SpuntaDocumentoEntity.class,
                SpuntaRigaEntity.class,
                SpuntaEanEntity.class,
                SpuntaArticoloCacheEntity.class
        },
        version = 11,
        exportSchema = false
)
public abstract class AppDb extends RoomDatabase {

    public abstract ArticoloDao articoloDao();
    public abstract InventarioDao inventarioDao();
    public abstract SpuntaDao spuntaDao();

    private static volatile AppDb INSTANCE;

    // ── Migration 10 → 11 ─────────────────────────────────────────────────────
    // Ricrea le tabelle cancellabili senza toccare i dati di spunta in corso.
    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {

            // 1. Tabelle cancellabili: drop + recreate
            db.execSQL("DROP TABLE IF EXISTS `spunta_art_cache`");
            db.execSQL("DROP TABLE IF EXISTS `articolo_ean`");
            db.execSQL("DROP TABLE IF EXISTS `articoli`");
            db.execSQL("DROP TABLE IF EXISTS `inventario_rows`");

            db.execSQL("CREATE TABLE IF NOT EXISTS `articoli` (" +
                    "`codArt` TEXT NOT NULL, `desc` TEXT, `es` INTEGER, PRIMARY KEY(`codArt`))");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_articoli_codArt` ON `articoli` (`codArt`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_articoli_desc` ON `articoli` (`desc`)");

            db.execSQL("CREATE TABLE IF NOT EXISTS `articolo_ean` (" +
                    "`codArt` TEXT NOT NULL, `ean` TEXT NOT NULL, PRIMARY KEY(`codArt`,`ean`), " +
                    "FOREIGN KEY(`codArt`) REFERENCES `articoli`(`codArt`) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_articolo_ean_codArt` ON `articolo_ean` (`codArt`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_articolo_ean_ean` ON `articolo_ean` (`ean`)");

            db.execSQL("CREATE TABLE IF NOT EXISTS `inventario_rows` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`codArt` TEXT NOT NULL, `gondola` TEXT NOT NULL, `sparata` TEXT NOT NULL, " +
                    "`desc` TEXT NOT NULL, `alias` TEXT NOT NULL, `qta` INTEGER NOT NULL, " +
                    "`timing` INTEGER NOT NULL, `esistenza` INTEGER NOT NULL, `store` TEXT NOT NULL, " +
                    "`sp1` INTEGER NOT NULL, `sp2` INTEGER NOT NULL, `imported` INTEGER NOT NULL)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_codArt` ON `inventario_rows` (`codArt`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_store` ON `inventario_rows` (`store`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_gondola` ON `inventario_rows` (`gondola`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_sparata` ON `inventario_rows` (`sparata`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_timing` ON `inventario_rows` (`timing`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_inventario_rows_imported` ON `inventario_rows` (`imported`)");

            db.execSQL("CREATE TABLE IF NOT EXISTS `spunta_art_cache` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`idSpuntaDoc` INTEGER NOT NULL, `codArt` TEXT NOT NULL, `desc` TEXT NOT NULL, " +
                    "`esistenza` INTEGER NOT NULL, `ordFornitore` INTEGER NOT NULL, `ordCliente` INTEGER NOT NULL, " +
                    "`prz` REAL NOT NULL, `ubic` TEXT NOT NULL, `subic` TEXT NOT NULL, " +
                    "`scorta` INTEGER NOT NULL, `przPromo` TEXT NOT NULL, `inizioPromo` TEXT NOT NULL, " +
                    "`finePromo` TEXT NOT NULL, " +
                    "FOREIGN KEY(`idSpuntaDoc`) REFERENCES `spunta_documenti`(`id`) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_art_cache_idSpuntaDoc` ON `spunta_art_cache` (`idSpuntaDoc`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_art_cache_idSpuntaDoc_codArt` ON `spunta_art_cache` (`idSpuntaDoc`,`codArt`)");

            // 2. Tabelle spunta: crea se mancanti, poi aggiungi colonne nuove
            db.execSQL("CREATE TABLE IF NOT EXISTS `spunta_documenti` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`fileName` TEXT NOT NULL, `docsName` TEXT NOT NULL, `tipoDoc` TEXT NOT NULL, " +
                    "`store` TEXT NOT NULL, `fornitore` TEXT NOT NULL, `utente` TEXT NOT NULL, " +
                    "`ipNeg` TEXT NOT NULL, `mag` INTEGER NOT NULL, `listino` INTEGER NOT NULL, " +
                    "`segnaC` TEXT NOT NULL, `tipoOperazione` INTEGER NOT NULL, " +
                    "`completato` INTEGER NOT NULL, `emailInviata` INTEGER NOT NULL, " +
                    "`dataCreazione` INTEGER NOT NULL)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_documenti_store` ON `spunta_documenti` (`store`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_documenti_tipoDoc` ON `spunta_documenti` (`tipoDoc`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_documenti_completato` ON `spunta_documenti` (`completato`)");

            addColumnIfMissing(db, "spunta_documenti", "tipoOperazione", "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, "spunta_documenti", "emailInviata",   "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, "spunta_documenti", "dataCreazione",  "INTEGER NOT NULL DEFAULT 0");

            db.execSQL("CREATE TABLE IF NOT EXISTS `spunta_righe` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`idSpuntaDoc` INTEGER NOT NULL, `codArt` TEXT NOT NULL, `desc` TEXT NOT NULL, " +
                    "`alias` TEXT NOT NULL, `ubic` TEXT NOT NULL, `subic` TEXT NOT NULL, " +
                    "`qtaDoc` INTEGER NOT NULL, `qtaSpunta` INTEGER NOT NULL, `diff` INTEGER NOT NULL, " +
                    "`nDoc` TEXT NOT NULL, `note` TEXT NOT NULL, `store` TEXT NOT NULL, " +
                    "`timeSp` TEXT NOT NULL, `colli` INTEGER NOT NULL, `costo` TEXT NOT NULL, " +
                    "`idDocRemoto` TEXT NOT NULL, `segnacollo` TEXT NOT NULL, " +
                    "`esistenza` INTEGER NOT NULL, `impegnati` INTEGER NOT NULL, " +
                    "`rifornimento` TEXT NOT NULL, " +
                    "FOREIGN KEY(`idSpuntaDoc`) REFERENCES `spunta_documenti`(`id`) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_righe_idSpuntaDoc` ON `spunta_righe` (`idSpuntaDoc`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_righe_codArt` ON `spunta_righe` (`codArt`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_righe_alias` ON `spunta_righe` (`alias`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_righe_nDoc` ON `spunta_righe` (`nDoc`)");

            addColumnIfMissing(db, "spunta_righe", "ubic",         "TEXT NOT NULL DEFAULT ''");
            addColumnIfMissing(db, "spunta_righe", "subic",        "TEXT NOT NULL DEFAULT ''");
            addColumnIfMissing(db, "spunta_righe", "colli",        "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, "spunta_righe", "costo",        "TEXT NOT NULL DEFAULT '0'");
            addColumnIfMissing(db, "spunta_righe", "idDocRemoto",  "TEXT NOT NULL DEFAULT ''");
            addColumnIfMissing(db, "spunta_righe", "segnacollo",   "TEXT NOT NULL DEFAULT ''");
            addColumnIfMissing(db, "spunta_righe", "esistenza",    "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, "spunta_righe", "impegnati",    "INTEGER NOT NULL DEFAULT 0");
            addColumnIfMissing(db, "spunta_righe", "rifornimento", "TEXT NOT NULL DEFAULT ''");

            db.execSQL("CREATE TABLE IF NOT EXISTS `spunta_ean` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`idSpuntaDoc` INTEGER NOT NULL, `codArt` TEXT NOT NULL, `ean` TEXT NOT NULL, " +
                    "FOREIGN KEY(`idSpuntaDoc`) REFERENCES `spunta_documenti`(`id`) ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_ean_idSpuntaDoc` ON `spunta_ean` (`idSpuntaDoc`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_spunta_ean_codArt` ON `spunta_ean` (`codArt`)");
        }

        private void addColumnIfMissing(SupportSQLiteDatabase db, String table, String column, String definition) {
            try (Cursor c = db.query("PRAGMA table_info(`" + table + "`)")) {
                int nameIdx = c.getColumnIndex("name");
                while (c.moveToNext()) {
                    if (column.equals(c.getString(nameIdx))) return; // già presente
                }
            }
            db.execSQL("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        }
    };

    public static AppDb getInstance(Context context){
        if(INSTANCE == null){
            synchronized (AppDb.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDb.class,
                                    "app.db"
                            )
                            .addMigrations(MIGRATION_10_11)
                            // Versioni molto vecchie (1-9): distruttivo accettabile,
                            // non avevano ancora le tabelle spunta Room.
                            .fallbackToDestructiveMigrationFrom(1, 2, 3, 4, 5, 6, 7, 8, 9)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
