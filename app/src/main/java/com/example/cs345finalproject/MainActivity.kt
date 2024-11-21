package com.example.cs345finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun createNewGame(v: View){
        //TODO: when pressed takes you to the game.xml and starts a new game
        
    }
}