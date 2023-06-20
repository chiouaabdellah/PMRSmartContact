package com.example.smartcontact

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




//importation des packages de traduction

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


class ConversationActivity : AppCompatActivity() {
    //Toolbar as smartcontract
    private lateinit var toolbar: Toolbar
    private val RQ_SPEECH_REC = 102
    private var recording = false
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var translator : Translator?= null
    private var resultText = ""

    //onCreatefunction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        //Création de toolbar + rennomer
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "SmartContact"
        setSupportActionBar(toolbar)
        // Create an English-Arabic translator:
        val btn_button = findViewById<Button>(R.id.btn_button)
        val tv_text = findViewById<TextView>(R.id.tv_text)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.FRENCH)
            .build()
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
        //creation de button select Profil
        val button_endconversation = findViewById<Button>(R.id.button_EndConversation)
        button_endconversation.setOnClickListener {
            //Implementer la partie envoi de conversation


            // cette partie va
            val intent = Intent(this, ShowSummaryActivity::class.java)
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

    //Function that starts Sppech to text process
    private fun startSpeechToText() {
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
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
}