package com.example.mymemory

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes

object Sfx {
    private lateinit var sp: SoundPool
    private lateinit var app: Context
    private val cache = mutableMapOf<Int, Int>()
    private var flipId = 0
    private var wrongId = 0

    fun init(ctx: Context) {
        app = ctx.applicationContext
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        sp = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(aa).build()
        flipId = sp.load(app, R.raw.flip, 1)
        wrongId = sp.load(app, R.raw.wrong, 1)
    }

    private fun idFor(@RawRes raw: Int): Int =
        cache.getOrPut(raw) { sp.load(app, raw, 1) }

    fun preload(raws: Collection<Int>) { raws.forEach { idFor(it) } }

    fun flip()  { sp.play(flipId, 1f, 1f, 1, 0, 1f) }
    fun wrong() { sp.play(wrongId, 1f, 1f, 1, 0, 1f) }
    fun playRaw(@RawRes raw: Int) { sp.play(idFor(raw), 1f, 1f, 1, 0, 1f) }
}
