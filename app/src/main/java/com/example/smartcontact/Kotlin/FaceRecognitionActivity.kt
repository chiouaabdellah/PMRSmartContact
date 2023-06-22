package com.example.smartcontact.Kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth
import android.hardware.Camera


class   FaceRecognitionActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    val REQUEST_CODE = 200
    lateinit var logo: ImageView
    lateinit var menuBtn: ImageButton
    private var camera: Camera? = null
    private var surfaceHolder: SurfaceHolder? = null

    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)
        surfaceHolder = (findViewById(R.id.cameraPreview) as SurfaceView).holder
        surfaceHolder?.addCallback(this)
        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu() }


        //creation de button select Profil
        val button_selectProfil = findViewById<Button>(R.id.button_SelectProfil)
        button_selectProfil.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        }
        if (ContextCompat.checkSelfPermission(
                this@FaceRecognitionActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@FaceRecognitionActivity,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@FaceRecognitionActivity,
                    arrayOf(Manifest.permission.CAMERA), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@FaceRecognitionActivity,
                    arrayOf(Manifest.permission.CAMERA), 1
                )
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            logo = findViewById(R.id.iv)
            logo.setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }
    fun showMenu() {
        val popupMenu = PopupMenu(this@FaceRecognitionActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.menu.add("Settings")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@FaceRecognitionActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                "Settings" -> {
                    startActivity(Intent(this@FaceRecognitionActivity, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    fun surfaceCreated(holder: SurfaceHolder) {
        camera = Camera.open()
        camera?.setDisplayOrientation(90)
    }

    fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        camera?.setPreviewDisplay(holder)
        camera?.startPreview()
    }

    fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

}

private fun SurfaceHolder.addCallback(faceRecognitionActivity: FaceRecognitionActivity) {

}
