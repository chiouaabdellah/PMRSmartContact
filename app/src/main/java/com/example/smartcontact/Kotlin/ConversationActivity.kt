package com.example.smartcontact.Kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.content.ContextCompat


import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.widget.PopupMenu
import com.example.smartcontact.Java.ListSummaries
import com.example.smartcontact.Java.LoginActivity
import com.example.smartcontact.R
import com.google.firebase.auth.FirebaseAuth


//importation des packages de traduction

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


class ConversationActivity : AppCompatActivity() {

    var options = setOptionsForLanguage("English")

    private val RQ_SPEECH_REC = 102
    private var recording = false
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var translator : Translator?= null
    private var resultText = ""
    lateinit var menuBtn: ImageButton

    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        val languageSpinner: Spinner = findViewById(R.id.language_spinner)
        val languages = arrayOf("English", "French", "Spanish", "German")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = parent.getItemAtPosition(position).toString()
                options = setOptionsForLanguage(selectedLanguage)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                val selectedLanguage = "English"
                options = setOptionsForLanguage(selectedLanguage)
                // Handle the case where no language is selected
            }
        })


        //Création de toolbar + rennomer

        menuBtn = findViewById<ImageButton>(R.id.menu_btn)
        menuBtn.setOnClickListener { v: View? -> showMenu() }

        // Create an English-Arabic translator:
        val btn_button = findViewById<Button>(R.id.btn_button)
        val tv_text = findViewById<TextView>(R.id.tv_text)

        translator = Translation.getClient(options)

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        tv_text.text = "..."
        translator!!.downloadModelIfNeeded(conditions)
            .addOnFailureListener { exception ->
                tv_text.text = "Model download error!"
            }

        checkAudioPermission()

        btn_button.setOnClickListener {
            if (!recording) {
                resultText = ""
                tv_text.text = ""
                startSpeechToText()
            } else {
                speechRecognizer.stopListening()
            }

            recording = !recording
            btn_button.text = if (recording) "Stop Translation" else "Start Translation"
        }

        //Creation de button end conversation
        val button_endconversation = findViewById<Button>(R.id.button_EndConversation)
        button_endconversation.setOnClickListener {
            //Implementer la partie envoi de conversation


            // cette partie va
            val intent = Intent(this, ListSummaries::class.java)
            startActivity(intent)
        }

    }
    //creation de dropdown menu qui lance SettingsActivity


    //Function that starts Sppech to text process
    private fun startSpeechToText() {
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 15);

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {
                if (recording)
                    speechRecognizer.startListening(speechRecognizerIntent)
            }

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    resultText += " \n" + result[0]
                    var output_text = resultText

                    translator!!.translate(output_text)
                        .addOnSuccessListener { translatedText ->
                            var words = translatedText.split(" ");
                            val tv_text = findViewById<TextView>(R.id.tv_text)
                            tv_text.text = words.takeLast(25).joinToString(" ")
                        }
                        .addOnFailureListener { exception ->
                            output_text = "Translation error!"
                        }

                }
                if (recording)
                    speechRecognizer.startListening(speechRecognizerIntent)
            }

            override fun onPartialResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    var output_text = resultText + "\n" + result[0] + "..."

                    translator!!.translate(output_text)
                        .addOnSuccessListener { translatedText ->
                            var words = translatedText.split(" ");
                            val tv_text = findViewById<TextView>(R.id.tv_text)
                            tv_text.text = words.takeLast(25).joinToString(" ")
                        }
                        .addOnFailureListener { exception ->
                            output_text = "Translation error!"
                        }
                }
            }
            override fun onEvent(i: Int, bundle: Bundle?) {

            }
        })
        // starts listening ...
        speechRecognizer.startListening(speechRecognizerIntent)
    }



    private fun checkAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // M = 23
            if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.programmingtech.offlinespeechtotext"))
                startActivity(intent)
                Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showMenu() {
        val popupMenu = PopupMenu(this@ConversationActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.menu.add("Settings")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Logout" -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@ConversationActivity, LoginActivity::class.java))
                    finish()
                    true
                }
                "Settings" -> {
                    startActivity(Intent(this@ConversationActivity, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }



    private fun setOptionsForLanguage(language: String): TranslatorOptions {
        val sourceLanguage: String
        val targetLanguage: String

        when (language) {
            "English" -> {
                sourceLanguage = TranslateLanguage.ENGLISH
                targetLanguage = TranslateLanguage.FRENCH
            }
            "French" -> {
                sourceLanguage = TranslateLanguage.FRENCH
                targetLanguage = TranslateLanguage.FRENCH
            }
            "Spanish" -> {
                sourceLanguage = TranslateLanguage.SPANISH
                targetLanguage = TranslateLanguage.FRENCH
            }
            "German" -> {
                sourceLanguage = TranslateLanguage.GERMAN
                targetLanguage = TranslateLanguage.FRENCH
            }
            else -> {

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.FRENCH)
                    .build()
                // Handle the case where an unsupported language is selected
                return options
            }
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()

        return options

        // Use the options in your logic or perform actions accordingly
    }

}