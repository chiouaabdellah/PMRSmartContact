package com.example.smartcontact.Kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.preference.PreferenceFragmentCompat

import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    lateinit var menuBtn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.preference_screen, MyPreferenceFragment())
            .commit()


        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu()}

    }
    fun showMenu() {
        val popupMenu = PopupMenu(this@SettingsActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    class MyPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }


}