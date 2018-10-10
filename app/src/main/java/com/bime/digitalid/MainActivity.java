package com.bime.digitalid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.Button;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_TOKEN = "com.bime.digitalid.TOKEN";
    public static final Integer LOGIN_CODE = 123;

    private static String service = "http://";
    private static String server = "10.0.2.2:8080";  //this is localhost on laptop when using emulator

    private String TAG = "Main";

    private String token = null;   //TODO: Determine proper way to handle tokens

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created Main Activity");
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQRText("/greeting?name=Brian");
            }
        });
        if (null == token) {
            // no token so we need to login
            Intent loginIntent = new Intent(this, LoginActivity.class);
            Log.d(TAG, "Starting Activity for result");
            startActivityForResult(loginIntent, LOGIN_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if (requestCode == LOGIN_CODE){
            if (resultCode == Activity.RESULT_OK) {
                token = data.getStringExtra(LOGIN_TOKEN);
                //TODO: error handling
            }
        }else {
            Log.e(TAG, "No idea what is request code:"+requestCode);
        }
    }
    private void getQRText(String resource){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = service+server+resource;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String content = null;
                        try {
                            JSONObject respObject = new JSONObject(response);
                            content = respObject.getString("content");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        generateQR(content);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!"+error.toString());
                //TODO: Display error for user.
            }
        });

        // Add the request to the RequestQueue.
        Log.d(TAG, "Sending request to server:"+url);
        queue.add(stringRequest);
    }

    private void generateQR(String content) {

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = findViewById(R.id.qr_view);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() );
        }
    }


}
