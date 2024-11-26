package com.example.cs345finalproject

import android.content.Context
import android.util.Log

class Card(var value: String, var type: String) {

    override fun toString(): String {
        return "$value of $type"
    }

    fun getNumberValue(): Int {
        if ("AceJackQueenKing".contains(value)) { // Ace Jack Queen King
            if (value === "Ace") {
                return 11
            }
            return 10
        }
        else if (value == "one")
            return 1
        else if (value == "two")
            return 2
        else if (value == "three")
            return 3
        else if (value == "four")
            return 4
        else if (value == "five")
            return 5
        else if (value == "six")
            return 6
        else if (value == "seven")
            return 7
        else if (value == "eight")
            return 8
        else if (value == "nine")
            return 9
        else if (value == "ten")
            return 10

        return -1
    }

    fun getImageResource(context: Context): Int{
        val cardString = this.toString()
        val stringFilePath = cardString.replace(" ", "_").lowercase()
        val resourceId = context.resources.getIdentifier(stringFilePath, "drawable", context.packageName)

        if (resourceId == 0) {
            throw IllegalArgumentException("Drawable resource not found for card: $cardString")
        }

        return resourceId
    }

    val isAce: Boolean
        get() = value === "Ace"
}