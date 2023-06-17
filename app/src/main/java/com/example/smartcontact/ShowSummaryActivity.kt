package com.example.smartcontact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class ShowSummaryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var summaryTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_summary)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "SmartContact"
        setSupportActionBar(toolbar)

        summaryTextView = findViewById(R.id.Summary)

        val summary = intent.getStringExtra("summary")
        summaryTextView.text = summary
    }

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
