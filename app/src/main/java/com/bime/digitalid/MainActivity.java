package com.bime.digitalid;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bime.digitalid.CredentialsHelper.createBannerID;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_TOKEN = "com.bime.digitalid.TOKEN";
    public static final String BANNER_ID = "com.bime.digitalid.BANNER";
    public static final String QR_TEXT = "com.bime.digitalid.QR_TEXT";
    public static final String QR_STATUS = "com.bime.digitalid.QR_STATUS";


    public static final Integer LOGIN_CODE = 123;


    //Primary Service leave active normally
    public static String service = "https://";  //Azure & GCP are secure
    public static String server = "bimewebapi.azurewebsites.net";
    // Comment out above server and uncomment below server for GCP testing
//  public static String server = "bime-0419.appspot.com";
    //Comment out above lines and uncomment below to use localhost and http
//  public static String service = "http://";
//  public static String server = "10.0.2.2:8080";  //this is localhost on laptop when using emulator

    private String TAG = "Main";

    private String token = null;   //TODO: Determine proper way to handle tokens
    private String bannerID = null;

    private  Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created Main Activity");
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMealTicket(bannerID);
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
                bannerID = data.getStringExtra(BANNER_ID);

                //TODO: error handling
            }
        }else {
            Log.e(TAG, "No idea what is request code:"+requestCode);
        }
        getRemainingMeals(bannerID);

    }

    private void getRemainingMeals(String BannerId){
        String mealResource = new String( "/api/meal/getmealcount");
        String url = service+server+mealResource;
        JSONObject bannerId = createBannerID(BannerId);

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, bannerId,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Integer meals = null;
                        try {
                            meals = response.getInt("mealCount");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Log.d(TAG, "Number of meals available:"+meals);
                        button.setText(new StringBuilder("Use Meal, ").append(meals).append(" remaining").toString());
                        if (meals != null & meals > 0){
                            button.setClickable(TRUE);
                        } else {
                            button.setClickable(FALSE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!" +error.toString());
                Log.e(TAG, "Response Code: " +error.networkResponse.statusCode);
                Log.e(TAG, "Error message:" +error.getMessage());
                button.setClickable(FALSE);
                button.setText("No Meals Available - Error:"+error.networkResponse.statusCode);
            }
        }){
            @Override
            public Map<String, String>getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization: Bearer", token);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        Log.d(TAG, "Sending request to server:"+url);
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);

    }
    private void getMealTicket(String BannerId){

        String mealResource = new String( "/api/meal/usemeal");
        String url = service+server+mealResource;
        JSONObject bannerId = createBannerID(BannerId);

        // Request a JSON response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, bannerId,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String content = null;
                        try {
                            content = response.getString("MealTicket");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        generateQR(response.toString(), 200);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!"+error.toString());
                Log.e(TAG, "Error message"+ error.getMessage());
                Log.e(TAG, "Response Code: "+error.networkResponse.statusCode);
                generateQR(error.getMessage(),error.networkResponse.statusCode);
            }
        });

        // Add the request to the RequestQueue.
        Log.d(TAG, "Sending meal token request to server:"+url);
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }

    private void generateQR(String content, int code) {

        Intent intent = new Intent(this, QRActivity.class);
        intent.putExtra(QR_TEXT, content);
        intent.putExtra(QR_STATUS, code);
        startActivity(intent);
    }


}
