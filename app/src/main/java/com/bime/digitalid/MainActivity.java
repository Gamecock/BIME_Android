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
import com.android.volley.toolbox.JsonObjectRequest;
import android.widget.Button;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_TOKEN = "com.bime.digitalid.TOKEN";
    public static final Integer LOGIN_CODE = 123;

    //Primary Service leave active normally
    public static String service = "https://";  //Azure & GCP are secure
    public static String server = "bimewebapi.azurewebsites.net";
    // Comment out above server and uncomment below server for GCP testing
//    public static String server = "bime-0419.appspot.com";
    //Comment out above lines and uncomment below to use localhost and http
//  public static String service = "http://";
//    public static String server = "10.0.2.2:8080";  //this is localhost on laptop when using emulator

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
                //Commented out pending PR for multiple buttons
//                getQRText("/greeting?name=Brian");
                String BannerId = "B0223456";
                getMealTicket(BannerId);
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
        String BannerId = "B0223456";
        getRemainingMeals(BannerId);

    }

    private void getRemainingMeals(String BannerId){
        String mealResource = new StringBuilder( "/api/").append(BannerId).append("/meals/plan").toString();
        String url = service+server+mealResource;

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Integer meals = null;
                        try {
                            meals = response.getInt("MealCount");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Log.d(TAG, "Number of meals available:"+meals);
                        Button button = findViewById(R.id.button);
                        button.setText(new StringBuilder("Get Meal, ").append(meals).append(" remaining").toString());

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
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);

    }
    private void getMealTicket(String BannerId){

        String mealResource = new StringBuilder( "/api/").append(BannerId).append("/meals/ticket").toString();
        String url = service+server+mealResource;

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String content = null;
                        try {
                            content = response.getString("MealTicket");
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
        Log.d(TAG, "Sending meal token request to server:"+url);
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }


    private void getQRText(String resource){

        String url = service+server+resource;

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String content = null;
                        try {
                            content = response.getString("content");
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
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }

    private void generateQR(String content) {

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
            Log.d(TAG, "Created barcode with content:"+content);
            ImageView imageViewQrCode = findViewById(R.id.qr_view);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() );
        }
    }


}
