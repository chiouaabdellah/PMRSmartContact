package com.example.smartcontact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar

class ProfilActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    private lateinit var toolbar: Toolbar

    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        //Création de toolbar + rennomer
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "SmartContact"
        setSupportActionBar(toolbar)

        //creation de button select Profil
        val button_startconversation = findViewById<Button>(R.id.button_StartConversation)
        button_startconversation.setOnClickListener {
            //Implementer la partie de traduction




            // cette partie va
            val intent = Intent(this, ConversationActivity::class.java)
            startActivity(intent)
        }

    }
    //creation de dropdown menu qui lance SettingsActivity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}