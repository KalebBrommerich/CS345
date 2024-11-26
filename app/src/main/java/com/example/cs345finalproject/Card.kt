package com.example.cs345finalproject

class Card(var value: String, var type: String) {

    override fun toString(): String {
        return "$value of $type"
    }

    fun getValue(): Int {
        if ("AceJackQueenKing".contains(value)) { // Ace Jack Queen King
            if (value === "Ace") {
                return 11
            }
            return 10
        }
        return value.toInt() // 2-10
    }

    val isAce: Boolean
        get() = value === "Ace"
}