package com.example.mymemory

data class Card(
    val id: Int,
    val frontRes: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
