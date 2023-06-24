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
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.room.jarjarred.org.stringtemplate.v4.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class   FaceRecognitionActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    val REQUEST_CODE = 200
    lateinit var logo: ImageView
    lateinit var menuBtn: ImageButton
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    lateinit var previewView: PreviewView

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)
        OpenCVLoader.initDebug()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        outputDirectory = getOutputDirectory()
        previewView=findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

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
    private fun loadModelFile(): ByteBuffer {
        val fileDescriptor = assets.openFd("model.tflite").fileDescriptor
        val inputStream = FileInputStream(fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val interpreter = Interpreter(loadModelFile())
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(inputWidth * inputHeight * inputChannels * 4) // Adjust based on your model's input requirements
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputWidth * inputHeight)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputWidth) {
            for (j in 0 until inputHeight) {
                val value = intValues[pixel++]
                byteBuffer.putFloat((value shr 16 and 0xFF) / 255.0f)
                byteBuffer.putFloat((value shr 8 and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }
        return byteBuffer
    }
    private fun displayRecognizedName(name: String) {
        runOnUiThread {
            textView.text = name
        }
    }

    private fun detectFaces(image: Mat): List<Rect> {
        val cascadeClassifier = CascadeClassifier(
            CascadeClassifier.CASCADE_FRONTALFACE_ALT2
        )
        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGBA2GRAY)
        cascadeClassifier.detectMultiScale(
            gray, faces, scaleFactor, minNeighbors, 0,
            Size(faceSize.toDouble(), faceSize.toDouble()), Size()
        )
        return faces.toList()
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera initialization failed", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(applicationContext, "Photo saved successfully", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "Failed to save photo", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }



}