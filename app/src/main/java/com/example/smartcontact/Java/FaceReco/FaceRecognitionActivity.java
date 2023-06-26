package com.example.smartcontact.Java.FaceReco;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartcontact.Kotlin.ProfilActivity;
import com.example.smartcontact.R;
import com.google.mlkit.vision.face.Face;

import org.json.JSONArray;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;


import java.io.IOException;

public class FaceRecognitionActivity extends MLVideoHelperActivity implements FaceRecognitionProcessor.FaceRecognitionCallback {
    private static final Logger logger = Logger.getLogger(Example.class.getName());

    private Interpreter faceNetInterpreter;
    private FaceRecognitionProcessor faceRecognitionProcessor;

    private Face face;
    private Bitmap faceBitmap;
    private float[] faceVector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeAddFaceVisible();
    }

    @Override
    protected VisionBaseProcessor setProcessor() {
        try {
            faceNetInterpreter = new Interpreter(FileUtil.loadMappedFile(this, "mobile_face_net.tflite"), new Interpreter.Options());
        } catch (IOException e) {
            e.printStackTrace();
        }

        faceRecognitionProcessor = new FaceRecognitionProcessor(
                faceNetInterpreter,
                graphicOverlay,
                this
        );
        faceRecognitionProcessor.activity = this;
        return faceRecognitionProcessor;
    }

    public void setTestImage(Bitmap cropToBBox) {
        if (cropToBBox == null) {
            return;
        }
        runOnUiThread(() -> ((ImageView) findViewById(R.id.testImageView)).setImageBitmap(cropToBBox));
    }

    @Override
    public void onFaceDetected(Face face, Bitmap faceBitmap, float[] faceVector) {
        this.face = face;
        this.faceBitmap = faceBitmap;
        this.faceVector = faceVector;
    }

    @Override
    public void onFaceRecognised(Face face, float probability, String name) {

    }

    private void jsonParse() {
        System.out.print("start activity1");

        String url = "https://script.google.com/macros/s/AKfycbyOauyOTtik_WUcjjfHclWSE-0JooIPSM4nxzNIt70LhOcADewzH86vwj1yqNwBQxQ8/exec";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                logger.log("start activity");
                try {
                    JSONArray jsonArray = response.getJSONArray("profilList");
                    for (int i = 0; i > jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        String fullname = user.getString("fullName");
                        String Photo = user.getString("Photo");
                        System.out.println(Photo + fullname);


                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        });
    }

    public void onSelectProfilClicked(View view) {
        Intent i = new Intent(getApplicationContext(), ProfilActivity.class);
        String variableValue = "Hello from Java!";
        i.putExtra("fullName", variableValue);
        i.putExtra("id", variableValue);
        startActivity(i);
    }

    @Override
    public void onAddFaceClicked(View view) {
        super.onAddFaceClicked(view);
        jsonParse();
        Intent i = new Intent(getApplicationContext(), ProfilActivity.class);
        String variableValue = "Hello from Java!";
        i.putExtra("fullName", variableValue);
        i.putExtra("id", variableValue);
        startActivity(i);
    }
}
