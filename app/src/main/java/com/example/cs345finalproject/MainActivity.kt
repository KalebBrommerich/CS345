package com.example.cs345finalproject

import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    lateinit var drawerLayout: DrawerLayout
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

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
            supportFragmentManager.beginTransaction().replace(R.id.framelayout,Home()).commit()
            navigationView.setCheckedItem(R.id.home)
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
        Toast.makeText(this, "new game", Toast.LENGTH_SHORT).show()
        supportFragmentManager.beginTransaction().replace(R.id.framelayout,Game()).commitNow()
        //cards exist in the layout, need to refresh somehow?
        repeat(2){
            addCardToView(true)
            addCardToView(false)
        }

    }
    fun hit(v: View){
        Toast.makeText(this, "hit", Toast.LENGTH_SHORT).show()
        addCardToView(true)
    }
    fun stand(v: View){
        Toast.makeText(this, "stand", Toast.LENGTH_SHORT).show()
    }
    fun doubleDown(v: View){
        Toast.makeText(this, "double down", Toast.LENGTH_SHORT).show()
    }
    fun casinoModeToggled(v: View){
        Toast.makeText(this, "casino mode toggle", Toast.LENGTH_SHORT).show()
    }
    fun textSizeUpdated(v: View){
        Toast.makeText(this, "text size updated", Toast.LENGTH_SHORT).show()
    }
    private fun addCardToView(addToPlayer: Boolean){
        lateinit var cards:LinearLayout
        val image = ImageView(this)
        image.adjustViewBounds = true

        if(addToPlayer) {
            cards = findViewById<LinearLayout>(R.id.playerCards)
            //TODO make a method to determine what card?
            image.setImageResource(R.drawable.ace_of_clubs)
        }
        else {
            cards = findViewById<LinearLayout>(R.id.dealerCards)
            if(cards.childCount ==0){
                image.setImageResource(R.drawable.back)
            }else{
                //TODO make a method to determine what card?
                image.setImageResource(R.drawable.ace_of_clubs)
            }
        }
        cards.addView(image)
        for (card in cards.children) {
            (card as ImageView).layoutParams.width =
                cards.width / (if (cards.childCount == 0) 1 else cards.childCount)
        }

    }
}