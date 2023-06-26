package com.example.smartcontact.Kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth

class ProfilActivity : AppCompatActivity() {
    lateinit var readProgressLayout: RelativeLayout
    lateinit var readProgressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdapter: ReadRecyclerAdapter
    private val TAG = "ProfilActivity"

    // il faut adapter cette partie avec le ID reconnue avec faceRecognition
    private val id = "ID1" // Replace "123" with the desired ID

    lateinit var menuBtn: ImageButton
    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu() }

        //creation de button select Profil

        // Use the variableValue as needed
        //cette partie permet l'affichage de profil avec des requettes Volley :
        readProgressLayout = findViewById(R.id.readProgressLayout)
        readProgressBar = findViewById(R.id.readProgressBar)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this)

        val profilList = arrayListOf<Profil>()

        val queue = Volley.newRequestQueue(this)
        val url = "https://script.google.com/macros/s/AKfycbyOauyOTtik_WUcjjfHclWSE-0JooIPSM4nxzNIt70LhOcADewzH86vwj1yqNwBQxQ8/exec"
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener {
                readProgressLayout.visibility = View.GONE
                readProgressBar.visibility = View.GONE
                val data = it.getJSONArray("profilList")
                for (i in 0 until data.length()) {
                    val profilJasonObject = data.getJSONObject(i)
                    val profileId = profilJasonObject.getString("ID")
                    if (profileId == id) {
                        val profilObject = Profil(
                            profileId,
                            profilJasonObject.getString("fullName"),
                            profilJasonObject.getString("Competence"),
                            profilJasonObject.getString("Company"),
                            profilJasonObject.getString("Position"),
                            profilJasonObject.getString("Experience")
                        )
                        profilList.add(profilObject)
                    }
                }
                recyclerAdapter = ReadRecyclerAdapter(this, profilList)
                recyclerView.adapter = recyclerAdapter
                recyclerView.layoutManager = layoutManager
            }, Response.ErrorListener {
                readProgressLayout.visibility = View.GONE
                readProgressBar.visibility = View.GONE
                Toast.makeText(this@ProfilActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return super.getHeaders()
            }
        }
        queue.add(jsonObjectRequest)
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