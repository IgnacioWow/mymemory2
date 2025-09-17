package com.example.mymemory
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes

object Sfx {
    private var sp: SoundPool? = null
    private val loaded = mutableSetOf<Int>()
    private val pairMap = mutableMapOf<Int, Int>() // frontRes -> soundId

    private var idStart = 0
    private var idMismatch = 0
    private var idDefaultMatch = 0
    private var idWin = 0

    fun init(ctx: Context) {
        if (sp != null) return
        sp = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setMaxStreams(6)
            .build()
        sp?.setOnLoadCompleteListener { _, sid, status -> if (status == 0) loaded += sid }

        idStart        = load(ctx, R.raw.sfx_start)
        idMismatch     = load(ctx, R.raw.sfx_mismatch)
        idDefaultMatch = load(ctx, R.raw.sfx_match)   // respaldo
        idWin          = load(ctx, R.raw.sfx_win)
    }

    // <-- ESTA es la función que faltaba
    private fun load(ctx: Context, @RawRes res: Int): Int =
        try { sp?.load(ctx, res, 1) ?: 0 } catch (_: Exception) { 0 }

    fun loadPairSounds(ctx: Context, mapping: Map<Int, Int>) {
        val pool = sp ?: return
        mapping.forEach { (frontRes, rawRes) ->
            val sid = try { pool.load(ctx, rawRes, 1) } catch (_: Exception) { 0 }
            if (sid != 0) pairMap[frontRes] = sid
        }
    }

    private fun play(id: Int) {
        val pool = sp ?: return
        if (id != 0 && id in loaded) pool.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun start()              = play(idStart)
    fun mismatch()           = play(idMismatch)
    fun match(frontRes: Int?) {
        val id = frontRes?.let { pairMap[it] } ?: idDefaultMatch
        play(id ?: idDefaultMatch)
    }
    fun win()                = play(idWin)
}
