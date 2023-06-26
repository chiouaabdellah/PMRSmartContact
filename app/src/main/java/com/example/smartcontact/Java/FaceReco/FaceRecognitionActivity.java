package com.example.smartcontact.Java.FaceReco;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.EditText;
import com.example.smartcontact.Java.FaceReco.FaceRecognitionProcessor.ImageConverter;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartcontact.Kotlin.ProfilActivity;
import com.example.smartcontact.R;
import com.google.mlkit.vision.face.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class FaceRecognitionActivity extends MLVideoHelperActivity implements FaceRecognitionProcessor.FaceRecognitionCallback {


    private Interpreter faceNetInterpreter;
    private FaceRecognitionProcessor faceRecognitionProcessor;

    private Face face;
    private Bitmap faceBitmap;
    private float[] faceVector;
    private RequestQueue mQueue;


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
        Log.d("FaceRecognition Activity","Hello activity");
        System.out.print("start activity1");


        String url = "https://script.google.com/macros/s/AKfycbyOauyOTtik_WUcjjfHclWSE-0JooIPSM4nxzNIt70LhOcADewzH86vwj1yqNwBQxQ8/exec";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("profilList");
                    //Log.d("FaceRecognition Activity","IDK HELLO " +String.valueOf(jsonArray.length()));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject user = jsonArray.getJSONObject(i);
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(String.valueOf(user), JsonObject.class);
                        String fullName = jsonObject.get("fullName").getAsString();
                        String photo = jsonObject.get("Photo").getAsString();
//                        if (photo.length() > 0) {
//                            url2Float(String.valueOf(photo));
//                        }
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
        mQueue.add(request);
    }
    public void onSelectProfilClicked(View view) {
        //String imageUrl = "https://drive.google.com/uc?id=1oM3HzK_pXXJcLVldUmi4_d61HHy0MqMg";

        //float[] image=ImageConverter.convertImageToFloatArray(imageUrl);
        //faceRecognitionProcessor.ajouterprofile("oussama",image);
        Intent i = new Intent(getApplicationContext(), ProfilActivity.class);
        String variableValue = "Hello from Java!";
        i.putExtra("fullName", variableValue);
        i.putExtra("id", variableValue);
        startActivity(i);
    }

    @Override
    public void onAddFaceClicked(View view) {
        super.onAddFaceClicked(view);

        if (face == null || faceBitmap == null) {
            return;
        }

        Face tempFace = face;
        Bitmap tempBitmap = faceBitmap;
        float[] tempVector = faceVector;

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_face_dialog, null);
        ((ImageView) dialogView.findViewById(R.id.dlg_image)).setImageBitmap(tempBitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editable input  = ((EditText) dialogView.findViewById(R.id.dlg_input)).getEditableText();
                if (input.length() > 0) {
                    faceRecognitionProcessor.registerFace(input, tempVector);
                }
            }
        });
        builder.show();
    }
}
