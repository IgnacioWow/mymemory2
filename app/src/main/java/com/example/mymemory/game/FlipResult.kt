package com.example.mymemory.game

sealed class FlipResult {
    object First : FlipResult()
    data class Match(val i1: Int, val i2: Int) : FlipResult()
    data class Mismatch(val i1: Int, val i2: Int) : FlipResult()
}
