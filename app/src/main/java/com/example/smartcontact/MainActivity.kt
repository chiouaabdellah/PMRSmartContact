package com.example.smartcontact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    private lateinit var toolbar: Toolbar

    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Création de toolbar + rennomer
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "SmartContact"
        setSupportActionBar(toolbar)

        //Création de button qui lance SummariesActivity
        val button_savedSummaries = findViewById<Button>(R.id.button_Summaries)
        button_savedSummaries.setOnClickListener {
            val intent = Intent(this, SummariesActivity::class.java)
            startActivity(intent)
        }
        val button_startFacerecognition = findViewById<Button>(R.id.button_FaceRecognition)
        button_startFacerecognition.setOnClickListener {
            val intent = Intent(this, FaceRecognitionActivity::class.java)
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