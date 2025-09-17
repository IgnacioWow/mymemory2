package com.example.mymemory

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
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
    private lateinit var playerName: String

    private var moves = 0
    private var seconds = 0
    private var lock = false
    private var startAt = 0L
    private var timer: CountDownTimer? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerName = intent.getStringExtra("playerName") ?: "Jugador"
        tvInfo = findViewById(R.id.tvInfo)
        rv = findViewById(R.id.rv)

        // Sonidos
        Sfx.init(this)
        val pairSounds = mapOf(
            R.drawable.knight to R.raw.knight_contact,
            R.drawable.glukhar to R.raw.glukhar_perimeter,
            R.drawable.sanitar to R.raw.sanitar_contact,
            R.drawable.kaban to R.raw.kaban_scream,
            R.drawable.rashala to R.raw.rashala_contact,
            R.drawable.tagilla to R.raw.tagilla_tagilla,
            R.drawable.killa to R.raw.killa_blyat,
            R.drawable.sturman to R.raw.sturman_scream
        )
        Sfx.loadPairSounds(this, pairSounds)
        // 1) Crear juego
        game = MemoryGame(size = 16) // 8 pares -> 4x4
        // 2) RecyclerView
        adapter = CardAdapter(game.cards, this)
        rv.layoutManager = GridLayoutManager(this, 4)
        rv.adapter = adapter

        // 3) Estado y timer
        moves = 0
        seconds = 0
        startAt = System.currentTimeMillis()
        startTimer()
        renderInfo()

        // 4) Revelar 1s y sonido de inicio
        introReveal()
        rv.postDelayed({ Sfx.start() }, 150)
    }

    override fun onCardClicked(position: Int) {
        if (lock) return
        when (val r = game.flip(position)) {
            is FlipResult.First -> {
                adapter.notifyItemChanged(position)
            }
            is FlipResult.Match -> {
                moves++
                val front = game.cards[position].frontRes
                Sfx.match(front)
                adapter.notifyDataSetChanged()
                renderInfo()
                if (game.cards.all { it.isMatched }) onWin()
            }
            is FlipResult.Mismatch -> {
                moves++
                Sfx.mismatch()
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

    private fun introReveal() {
        lock = true
        game.cards.forEach { it.isFaceUp = true }
        adapter.notifyDataSetChanged()
        rv.postDelayed({
            game.cards.forEach { it.isFaceUp = false }
            adapter.notifyDataSetChanged()
            lock = false
        }, 1000)
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
        val ts = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        DbHelper(this).insertScore(playerName, moves, elapsed, ts)
        Sfx.win()
        startActivity(Intent(this, ScoresActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
