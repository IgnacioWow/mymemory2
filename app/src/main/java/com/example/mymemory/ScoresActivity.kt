package com.example.mymemory

import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.data.DbHelper
import com.example.mymemory.model.Score
import com.example.mymemory.ui.ScoreAdapter

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_scores)

        val rv = findViewById<RecyclerView>(R.id.rvScores)
        rv.layoutManager = LinearLayoutManager(this)

        val db = DbHelper(this).readableDatabase
        val c = db.rawQuery(
            // Toma elapsed_ms si existe, si no usa millis. Igual con ts/createdAt.
            """
  SELECT name,
         moves,
         COALESCE(elapsed_ms, millis)  AS elapsed_val,
         COALESCE(ts, createdAt)       AS ts_val
  FROM scores
  ORDER BY elapsed_val ASC, moves ASC
  LIMIT 100
  """.trimIndent(), null
        )

        val data = buildList {
            val idxName = c.getColumnIndexOrThrow("name")
            val idxMoves = c.getColumnIndexOrThrow("moves")
            val idxElapsed = c.getColumnIndexOrThrow("elapsed_val")
            val idxTs = c.getColumnIndexOrThrow("ts_val")
            while (c.moveToNext()) {
                add(
                    com.example.mymemory.model.Score(
                        name = c.getString(idxName),
                        moves = c.getInt(idxMoves),
                        millis = c.getLong(idxElapsed),
                        createdAt = c.getString(idxTs)
                    )
                )
            }
        }
        c.close(); db.close()

        rv.adapter = ScoreAdapter(data)
    }
}
