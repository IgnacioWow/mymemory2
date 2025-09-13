package com.example.mymemory

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.data.DbHelper
import com.example.mymemory.game.FlipResult
import com.example.mymemory.game.MemoryGame

class PlayerActivity : AppCompatActivity(), CardAdapter.CardClick {

    private lateinit var game: MemoryGame
    private lateinit var adapter: CardAdapter
    private lateinit var tvInfo: TextView
    private lateinit var rv: RecyclerView

    private var moves = 0
    private var seconds = 0
    private var lock = false
    private var startAt = 0L
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        tvInfo = findViewById(R.id.tvInfo)
        rv = findViewById(R.id.rv)

        game = MemoryGame(size = 16)
        adapter = CardAdapter(game.cards, this)   // tu CardAdapter está en este package
        rv.layoutManager = GridLayoutManager(this, 4)
        rv.adapter = adapter

        moves = 0
        seconds = 0
        startAt = System.currentTimeMillis()
        startTimer()
        renderInfo()
    }

    override fun onCardClicked(position: Int) {
        if (lock) return
        when (val r = game.flip(position)) {
            is FlipResult.First -> {
                adapter.notifyItemChanged(position)
            }
            is FlipResult.Match -> {
                moves++
                adapter.notifyDataSetChanged()
                renderInfo()
                if (game.cards.all { it.isMatched }) onWin()
            }
            is FlipResult.Mismatch -> {
                moves++
                adapter.notifyItemChanged(r.i1)
                adapter.notifyItemChanged(r.i2)
                renderInfo()
                lock = true
                rv.postDelayed({
                    game.hide(r.i1, r.i2)
                    adapter.notifyItemChanged(r.i1)
                    adapter.notifyItemChanged(r.i2)
                    lock = false
                }, 600)
            }
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(60_000_000L, 1000L) {
            override fun onTick(ms: Long) { seconds++; renderInfo() }
            override fun onFinish() {}
        }.start()
    }

    private fun renderInfo() {
        tvInfo.text = "Movimientos: $moves | Tiempo: ${seconds}s"
    }

    private fun onWin() {
        timer?.cancel()
        val elapsed = System.currentTimeMillis() - startAt
        val name = intent.getStringExtra("playerName") ?: "Jugador"
        val ts = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        DbHelper(this).insertScore(name, moves, elapsed, ts)
        startActivity(Intent(this, ScoresActivity::class.java))
        finish()
    }

    override fun onDestroy() { super.onDestroy(); timer?.cancel() }
}
