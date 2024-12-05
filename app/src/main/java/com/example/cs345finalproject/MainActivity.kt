package com.example.cs345finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    private lateinit var deck: MutableList<Card>
    private lateinit var playerHand: MutableList<Card>
    private lateinit var dealerHand: MutableList<Card>
    private var casinoMode = false;
    private var playerAceCount = 0
    private var dealerAceCount = 0
    private var playerScore = 0
    private var dealerScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setup the toolbar so it can have a textView
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //find the drawerLayout and navigationView
        drawerLayout = findViewById(R.id.DrawerLayout)
        navigationView = findViewById(R.id.navigationview)

        //setup the drawer
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navOpen, R.string.navClose
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //set the navigation view
        navigationView.setNavigationItemSelectedListener(this)

        //load the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.framelayout, Game()).commit()
            navigationView.setCheckedItem(R.id.game)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
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
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,Game()).commitNow()
                //make a way to restore the game if nav away? viewmodel?
            }
            R.id.settings -> {
                supportFragmentManager.beginTransaction().replace(R.id.framelayout,Settings()).commitNow()
                findViewById<CheckBox>(R.id.casinoModeCheckbox).isChecked = casinoMode
            }
            R.id.addMoreChips -> {
                //Intent for google play store?
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/")))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun createNewGame(v: View){
        //TODO: when pressed takes you to the game.xml and starts a new game
        playerScore = 0
        playerAceCount = 0
        dealerScore = 0
        dealerAceCount = 0

        Toast.makeText(this, "new game", Toast.LENGTH_SHORT).show()
        Log.i("INFO", "making fragment")

        supportFragmentManager.beginTransaction().replace(R.id.framelayout, Game()).commitNow()

        findViewById<Button>(R.id.hitBtn).isVisible = true
        findViewById<Button>(R.id.standBtn).isVisible = true
        findViewById<Button>(R.id.doubleDownBtn).isVisible = true
        findViewById<Button>(R.id.newGameAfterGameBtn).isVisible = false

        //TODO: make deck and the hands reset if they have greater than 0 elements
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

        //initialize the player score
        for (card in playerHand) {
            playerAceCount += if (card.isAce) 1 else 0 //increase the counter of Ace's in the player hand
            playerScore += card.getNumberValue()
        }
        Log.i("INFO", "initial player score = $playerScore")

        //initialize the dealer score
        for (card in dealerHand) {
            dealerAceCount += if (card.isAce) 1 else 0 //increase the counter of Ace's in the dealer hand
            dealerScore += card.getNumberValue()
        }

        //TODO: display the card if they do have 21
        //end game if dealer has 21 right away
        if(dealerScore == 21){
            endGame()
        }
        Log.i("INFO", "initial dealer score = $dealerScore")
    }

    fun hit(v: View){
        //Toast.makeText(this, "hit", Toast.LENGTH_SHORT).show()
        addCardToView(true, getCard())

        val doubleDownButton = findViewById<Button>(R.id.doubleDownBtn)
        doubleDownButton.isEnabled = false

        //check if the player score is above 21 to disable the hit button
        if (reducePlayerAce() >= 21) {
            val hitButton = findViewById<Button>(R.id.hitBtn)
            hitButton.isEnabled = false
        }

    }
    fun stand(v: View){
        //Toast.makeText(this, "stand", Toast.LENGTH_SHORT).show()

        val hitButton = findViewById<Button>(R.id.hitBtn)
        hitButton.isEnabled = false

        val doubleDownButton = findViewById<Button>(R.id.doubleDownBtn)
        doubleDownButton.isEnabled = false

        findViewById<ImageView>(R.id.dealerCard1).setImageResource(dealerHand[0].getImageResource(this))

        //dealer gets cards if the player hasn't busted and dealer is below 17
        if(casinoMode){
            //give the player a "realistic" game that happens in a casino
            if(reducePlayerAce() > reduceDealerAce()){
             if(reduceDealerAce() >= 17){
                 //dealer has 17 or more, adjust hidden card to "give the house and edge"
                 dealerScore -= dealerHand[0].getNumberValue()
                 dealerHand[0] = getCard()
                 dealerScore += dealerHand[0].getNumberValue()
             }else{
                 //dealer can still hit,

             }
            }//don't need to do anything
        }else{
            //don't rig the game
            while (reduceDealerAce() < 17 && reducePlayerAce() <= 21){
                addCardToView(false, getCard())
            }
        }
        endGame() //determines who won the game

        //After game has finished
        hitButton.isVisible = false
        findViewById<Button>(R.id.standBtn).isVisible = false
        doubleDownButton.isVisible = false
        findViewById<Button>(R.id.newGameAfterGameBtn).isVisible = true
    }

    fun doubleDown(v: View){
        Toast.makeText(this, "double down", Toast.LENGTH_SHORT).show()
        addCardToView(true, getCard())

        //disable the hit button
        val hitButton = findViewById<Button>(R.id.hitBtn)
        hitButton.isEnabled = false

        //disable the double down button
        val doubleDownButton = findViewById<Button>(R.id.doubleDownBtn)
        doubleDownButton.isEnabled = false
    }

    fun casinoModeToggled(v: View){
        Toast.makeText(this, "casino mode toggle", Toast.LENGTH_SHORT).show()
        casinoMode = findViewById<CheckBox>(R.id.casinoModeCheckbox).isChecked
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

    // method to reduce the player's score if they go over 21 and have an ace in their hand
    private fun reducePlayerAce(): Int {
        while (playerScore > 21 && playerAceCount > 0) {
            playerScore -= 10
            playerAceCount -= 1
        }
        return playerScore
    }

    // method to reduce the dealer's score if they go over 21 and have an ace in their hand
    private fun reduceDealerAce(): Int {
        while (dealerScore > 21 && dealerAceCount > 0) {
            dealerScore -= 10
            dealerAceCount -= 1
        }
        return dealerScore
    }

    private fun addCardToView(addToPlayer: Boolean, newCard: Card){
        lateinit var cards:LinearLayout
        val image = ImageView(this)

        if(addToPlayer) {
            playerHand.add(newCard) //add card to the player hand
            playerAceCount += if (newCard.isAce) 1 else 0 //increase the counter of Ace's in the player hand
            playerScore += newCard.getNumberValue() //update the players score
            Log.i("INFO", "new player score = $playerScore")
            cards = findViewById<LinearLayout>(R.id.playerCards)
            image.setImageResource(newCard.getImageResource(this))
        }
        else {
            dealerHand.add(newCard) //add card to the dealer hand
            dealerAceCount += if (newCard.isAce) 1 else 0 //increase the counter of Ace's in the dealer hand
            dealerScore += newCard.getNumberValue() //update the dealers score
            Log.i("INFO", "dealer score = $dealerScore")
            cards = findViewById<LinearLayout>(R.id.dealerCards)
            if(cards.childCount == 0){
                image.setImageResource(R.drawable.back)
            }else{
                image.setImageResource(newCard.getImageResource(this))
            }
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

    //TODO: implement end game function: check who won and maybe update some text saying they won
    private fun endGame() {
        //both player and dealer have blackjack
        if(playerScore == 21 && dealerScore == 21){
            Toast.makeText(this, "Pushed", Toast.LENGTH_LONG).show()
            //raisePlayerChips(playerBet, true, true);
            //return  "Pushed! " + playerBet + " has been returned";
        } //player has blackjack and the dealer does not
        else if(playerScore == 21 && dealerScore != 21) {
            //raisePlayerChips(playerBet, true, false);
            //return  "You have Blackjack! You won " + (playerBet + (playerBet*3)/2) + " chips";
            Toast.makeText(this, "You have blackjack", Toast.LENGTH_LONG).show()
        } //dealer has blackjack and the player does not
        else if(dealerScore == 21 && playerScore != 21) {
            Toast.makeText(this, "Dealer has blackjack. You Lost", Toast.LENGTH_LONG).show()
            //return  ("Dealer has Blackjack! You lost " + playerBet + " chips");
        } //player busts
        else if(playerScore > 21) {
            Toast.makeText(this, "You busted. You Lost", Toast.LENGTH_LONG).show()
            //return  "You busted! You lost " + playerBet + " chips";
        } //dealer busts
        else if(dealerScore > 21) {
            Toast.makeText(this, "Dealer busted. You Won", Toast.LENGTH_LONG).show()
            //raisePlayerChips(playerBet, false, false);
            //return  "Dealer busted! You won " + playerBet + " chips";
        }//player and dealer have the same sum
        else if(playerScore == dealerScore) {
            Toast.makeText(this, "Pushed", Toast.LENGTH_LONG).show()
            //raisePlayerChips(playerBet, false, true);
            //return  "Pushed! " + playerBet + " has been returned";
        } //player has a greater hand than the dealer
        else if(playerScore > dealerScore) {
            Toast.makeText(this, "You won", Toast.LENGTH_LONG).show()
            //raisePlayerChips(playerBet, false, false);
            //return  "You won " +  playerBet + " chips";
        } //dealer has a greater hand than the dealer
        else if(dealerScore > playerScore) {
            Toast.makeText(this, "You Lost", Toast.LENGTH_LONG).show()
            //return  "You lost " + playerBet + " chips";
        }
    }
}