package com.example.smartcontact

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SummariesActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var adapter: ItemAdapter
    private lateinit var dataset: MutableList<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summaries)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "SmartContact"
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        //dataset = retrieveList()

        //exemple de test dataset :
        val exampleList: MutableList<String> = mutableListOf(
            "Summary 1",
            "Summary 2",
            "Summary 3",
            "Summary 4",
            "Summary 5"
        )
        dataset = exampleList
        adapter = ItemAdapter(dataset) { summary ->
            val intent = Intent(this, ShowSummaryActivity::class.java)
            intent.putExtra("summary", summary)
            startActivity(intent)
        }

        val list = findViewById<RecyclerView>(R.id.list)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)
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

    private fun retrieveList(): MutableList<String> {
        val list = sharedPreferences.getStringSet("List", mutableSetOf())?.toMutableList()
        return list ?: mutableListOf()
    }

    private fun saveList() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("List", dataset.toMutableSet())
        editor.apply()
    }
}
