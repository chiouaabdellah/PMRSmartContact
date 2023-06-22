package com.example.smartcontact.Kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth

class ProfilActivity : AppCompatActivity() {

    lateinit var menuBtn: ImageButton
    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu() }

        //creation de button select Profil
        val button_startconversation = findViewById<Button>(R.id.button_StartConversation)
        button_startconversation.setOnClickListener {
            //Implementer la partie de traduction




            // cette partie va
            val intent = Intent(this, ConversationActivity::class.java)
            startActivity(intent)
        }

    }
    fun showMenu() {
        val popupMenu = PopupMenu(this@ProfilActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.menu.add("Settings")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@ProfilActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                "Settings" -> {
                    startActivity(Intent(this@ProfilActivity, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }





}