package com.example.mymemory.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(ctx: Context) : SQLiteOpenHelper(ctx, "memory.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
      CREATE TABLE IF NOT EXISTS scores(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        moves INTEGER NOT NULL,
        elapsed_ms INTEGER NOT NULL,
        ts TEXT NOT NULL
      )
    """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        // Migraciones tolerantes: intenta añadir columnas si faltan
        if (oldV < 2) {
            try { db.execSQL("ALTER TABLE scores ADD COLUMN elapsed_ms INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
            try { db.execSQL("ALTER TABLE scores ADD COLUMN ts TEXT NOT NULL DEFAULT ''") } catch (_: Exception) {}
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Garantiza tabla y columnas, idempotente
        onCreate(db)
        try { db.execSQL("ALTER TABLE scores ADD COLUMN elapsed_ms INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) {}
        try { db.execSQL("ALTER TABLE scores ADD COLUMN ts TEXT NOT NULL DEFAULT ''") } catch (_: Exception) {}
    }

    fun insertScore(name: String, moves: Int, elapsedMs: Long, ts: String) {
        val cv = ContentValues().apply {
            put("name", name)
            put("moves", moves)
            put("elapsed_ms", elapsedMs)
            put("ts", ts)
            // Compat: si tu DB antigua tenía estas columnas, también las rellenamos.
            put("millis", elapsedMs)      // ignorado si no existe
            put("createdAt", ts)          // ignorado si no existe
        }
        writableDatabase.insert("scores", null, cv)
    }
}
