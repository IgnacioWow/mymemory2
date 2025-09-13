package com.example.mymemory.game

import com.example.mymemory.Card
import com.example.mymemory.R
import kotlin.random.Random

class MemoryGame(private val size: Int) {
    val cards: MutableList<Card>
    private var firstIndex: Int? = null

    init {
        require(size == 16) { "size debe ser 16 (8 pares)" }

        val allFronts = listOf(
            R.drawable.knight, R.drawable.glukhar,
            R.drawable.killa, R.drawable.kaban,
            R.drawable.tagilla, R.drawable.sturman,
            R.drawable.rashala, R.drawable.sanitar
        )
        val rng = Random(System.currentTimeMillis())
        val fronts = allFronts.shuffled(rng).take(size / 2) // 8 únicas

        cards = fronts.flatMapIndexed { i, res ->
            listOf(Card(id = i*2, frontRes = res), Card(id = i*2+1, frontRes = res)) // duplica
        }.shuffled(rng).toMutableList()
    }

    fun flip(pos: Int): FlipResult {
        val c = cards[pos]
        if (c.isMatched || c.isFaceUp) return FlipResult.First
        c.isFaceUp = true
        val f = firstIndex
        return if (f == null) { firstIndex = pos; FlipResult.First }
        else {
            val o = cards[f]
            if (o.frontRes == c.frontRes) {
                o.isMatched = true; c.isMatched = true; firstIndex = null
                FlipResult.Match(f, pos)
            } else FlipResult.Mismatch(f, pos)
        }
    }

    fun hide(i1: Int, i2: Int) {
        cards[i1].isFaceUp = false
        cards[i2].isFaceUp = false
        firstIndex = null
    }
}
