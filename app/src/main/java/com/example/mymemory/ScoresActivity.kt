package com.example.mymemory

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.data.DbHelper
import com.example.mymemory.model.Score
import com.example.mymemory.ui.ScoreAdapter
import android.widget.Button
import android.view.MenuItem
class ScoresActivity : AppCompatActivity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_scores)
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
        val rv = findViewById<RecyclerView>(R.id.rvScores)
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        rv.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val db = DbHelper(this).readableDatabase
        val data = db.rawQuery(
            """
      SELECT id,
             name          AS name_val,
             moves         AS moves_val,
             elapsed_ms    AS elapsed_val,
             ts            AS ts_val
      FROM scores
      ORDER BY id DESC
      LIMIT 100
      """.trimIndent(), null
        ).use { c ->
            val out = ArrayList<Score>()
            val iN = c.getColumnIndexOrThrow("name_val")
            val iM = c.getColumnIndexOrThrow("moves_val")
            val iE = c.getColumnIndexOrThrow("elapsed_val")
            val iT = c.getColumnIndexOrThrow("ts_val")
            while (c.moveToNext()) {
                out += Score(
                    name = c.getString(iN),
                    moves = c.getInt(iM),
                    millis = c.getLong(iE),
                    createdAt = c.getString(iT)
                )
            }
            out
        }
        db.close()

        if (data.isEmpty()) tvEmpty.visibility = View.VISIBLE
        rv.adapter = ScoreAdapter(data)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
