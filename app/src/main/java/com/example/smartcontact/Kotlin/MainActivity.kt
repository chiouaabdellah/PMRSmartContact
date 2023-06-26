package com.example.smartcontact.Kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.smartcontact.Java.ListSummaries
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth
import com.example.smartcontact.Java.FaceReco.FaceRecognitionActivity


class MainActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    class Algo(val imageResourceId: Int, val title: String, val activityClass: Class<*>) {
        // Additional properties and functions can be added here
    }
    val arrayList = ArrayList<Algo>()
    lateinit var menuBtn: ImageButton
    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Création de toolbar + rennomer

        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu() }


        //Création de button qui lance SummariesActivity
        val button_savedSummaries = findViewById<Button>(R.id.button_Summaries)
        button_savedSummaries.setOnClickListener {
            val intent2 = Intent(this, ListSummaries::class.java)
            startActivity(intent2)
        }

        val button_startFacerecognition = findViewById<Button>(R.id.button_FaceRecognition)
        button_startFacerecognition.setOnClickListener {
            val intent = Intent(this, com.example.smartcontact.Java.FaceReco.FaceRecognitionActivity::class.java)
            startActivity(intent)
        }
    }
    //creation de dropdown menu qui lance SettingsActivity

    fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.menu.add("Settings")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                "Settings" -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }



}