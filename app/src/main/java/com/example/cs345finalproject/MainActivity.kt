package com.example.cs345finalproject

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var deck: MutableList<Card>
    private lateinit var playerHand: MutableList<Card>
    private lateinit var dealerHand: MutableList<Card>
    private var playerScore = 0
    private var dealerScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.DrawerLayout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout, R.string.navOpen, R.string.navClose)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        val navigationView: NavigationView = findViewById(R.id.navigationview)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navigationView.setNavigationItemSelectedListener(this)

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        if(savedInstanceState ==null){
            supportFragmentManager.beginTransaction().replace(R.id.framelayout,Game()).commit()
            navigationView.setCheckedItem(R.id.game)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            true
        }else{
            super.onOptionsItemSelected(item)
        }

    }
    override fun onNavigationItemSelected(item: MenuItem):Boolean{
        when(item.itemId){
            R.id.home -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,Home()).commit()
            }
            R.id.howtoplay -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,HowToPlay()).commit()
            }
            R.id.game -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,Game()).commit()
            }
            R.id.settings -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,Settings()).commit()
            }
            R.id.addMoreChips -> {
                //Intent for google play store?

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun createNewGame(v: View){
        //TODO: when pressed takes you to the game.xml and starts a new game
        playerScore = 0
        dealerScore = 0


        Toast.makeText(this, "new game", Toast.LENGTH_SHORT).show()
        Log.i("INFO", "making fragment")

        supportFragmentManager.beginTransaction().replace(R.id.framelayout, Game()).commitNow()

        findViewById<Button>(R.id.hitBtn).isVisible = true
        findViewById<Button>(R.id.standBtn).isVisible = true
        findViewById<Button>(R.id.doubleDownBtn).isVisible = true
        findViewById<Button>(R.id.newGameAfterGameBtn).isVisible = false

        //create the shuffled deck
        Log.i("INFO", "making deck")
        deck = createDeck()

        //initialize the hands of the user and dealer
        Log.i("INFO", "making hands")
        playerHand = initializeHand()
        findViewById<ImageView>(R.id.playerCard1).setImageResource(playerHand[0].getImageResource(this))
        findViewById<ImageView>(R.id.playerCard2).setImageResource(playerHand[1].getImageResource(this))

        dealerHand = initializeHand()
        findViewById<ImageView>(R.id.dealerCard1).setImageResource(R.drawable.back)
        findViewById<ImageView>(R.id.dealerCard2).setImageResource(dealerHand[1].getImageResource(this))


        for (card in playerHand) {
            Log.i("INFO", card.toString())
            playerScore += card.getNumberValue()
            Log.i("INFO", "player score = $playerScore")
            //below just doesn't work some how only when its uncommented
            //addCardToView(true, card) //add players cards to the view
        }

        //TODO: make it so the first dealer card shows the back
        for (card in dealerHand) {
            Log.i("INFO", card.toString())
            dealerScore += card.getNumberValue()
            Log.i("INFO", "dealer score = $dealerScore")
            //below just doesn't work some how only when its uncommented
            //addCardToView(false, card) //add dealers cards to the view
        }
    }

    fun hit(v: View){
        Toast.makeText(this, "hit", Toast.LENGTH_SHORT).show()
        addCardToView(true, getCard())
    }
    fun stand(v: View){
        Toast.makeText(this, "stand", Toast.LENGTH_SHORT).show()

        val hitButton = findViewById<Button>(R.id.hitBtn)
        hitButton.isEnabled = false

        val doubleDownButton = findViewById<Button>(R.id.doubleDownBtn)
        doubleDownButton.isEnabled = false

        //TODO: Function logic to control the dealer
        findViewById<ImageView>(R.id.dealerCard1).setImageResource(dealerHand[0].getImageResource(this))
        while (dealerScore <17){
            addCardToView(false, getCard())
        }

        //After game has finished
        findViewById<Button>(R.id.hitBtn).isVisible = false
        findViewById<Button>(R.id.standBtn).isVisible = false
        findViewById<Button>(R.id.doubleDownBtn).isVisible = false
        findViewById<Button>(R.id.newGameAfterGameBtn).isVisible = true
    }
    fun doubleDown(v: View){
        Toast.makeText(this, "double down", Toast.LENGTH_SHORT).show()
        addCardToView(true, getCard())

        val hitButton = findViewById<Button>(R.id.hitBtn)
        hitButton.isEnabled = false

        val doubleDownButton = findViewById<Button>(R.id.doubleDownBtn)
        doubleDownButton.isEnabled = false
    }
    fun casinoModeToggled(v: View){
        Toast.makeText(this, "casino mode toggle", Toast.LENGTH_SHORT).show()
    }
    fun textSizeUpdated(v: View){
        Toast.makeText(this, "text size updated", Toast.LENGTH_SHORT).show()
    }

    private fun initializeHand(): MutableList<Card>{
        val list: MutableList<Card> = mutableListOf()
        list.add(deck.removeFirst()) //add the first card from the deck to the hand
        list.add(deck.removeFirst()) //add the second card from the deck to the hand
        return list //return the first two cards
    }

    private fun addCardToView(addToPlayer: Boolean, newCard: Card){
        //Log.i("info", newCard.getImageResource(this).toString())
        lateinit var cards:LinearLayout
        val image = ImageView(this)

        if(addToPlayer) {
            playerHand.add(newCard)
            cards = findViewById<LinearLayout>(R.id.playerCards)
            //TODO make a method to determine what card?
            image.setImageResource(newCard.getImageResource(this))
            playerScore += newCard.getNumberValue()
            Log.i("INFO", "player score = $playerScore")
        }
        else {
            cards = findViewById<LinearLayout>(R.id.dealerCards)
            dealerHand.add(newCard)
            if(cards.childCount ==0){
                image.setImageResource(R.drawable.back)
            }else{
                //TODO make a method to determine what card?
                image.setImageResource(newCard.getImageResource(this))
            }
            dealerScore += newCard.getNumberValue()
            Log.i("INFO", "dealer score = $dealerScore")

        }
        cards.addView(image)
        for (card in cards.children) {
            (card as ImageView).layoutParams.width =
                cards.width / (if (cards.childCount == 0) 1 else cards.childCount)
        }
    }

    private fun createDeck(): MutableList<Card> {
        val suits = listOf("Hearts", "Diamonds", "Clubs", "Spades")
        val ranks = listOf("two", "three", "four", "five", "six", "seven", "eight", "nine",
                            "ten", "Jack", "Queen", "King", "Ace")

        return suits.flatMap { suit ->
            ranks.map { rank ->
                Card(rank, suit)
            }
        }.shuffled().toMutableList()
    }

    private fun getCard():Card {
        return deck.removeFirst()
    }
}