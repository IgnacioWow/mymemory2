package com.example.mymemory.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(ctx: Context) : SQLiteOpenHelper(ctx, "memory.db", null, 4) {

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
        // DEV: esquema estable. Si hay tablas antiguas, las reemplazamos.
        db.execSQL("DROP TABLE IF EXISTS scores")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) { super.onOpen(db); onCreate(db) }

    fun insertScore(name: String, moves: Int, elapsedMs: Long, ts: String) {
        val cv = ContentValues().apply {
            put("name", name); put("moves", moves); put("elapsed_ms", elapsedMs); put("ts", ts)
        }
        writableDatabase.insert("scores", null, cv)
    }
}
