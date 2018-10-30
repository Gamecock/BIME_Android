package com.bime.digitalid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import static com.bime.digitalid.CredentialsHelper.createLoginCredentials;
import static com.bime.digitalid.MainActivity.server;
import static com.bime.digitalid.MainActivity.service;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "Login";
    private String resource = "/api/authentication/authenticate";
    TextView resultTextView ;
    Button signInButton;
    int retrySeconds = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created Login Activity");
        setContentView(R.layout.activity_login);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        signInButton = findViewById(R.id.SignInButton);
        resultTextView.setText("");
        final EditText inputEmail = findViewById(R.id.inputBannedId);
        final EditText inputPassword = findViewById(R.id.inputPassword);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserNamePassword(inputEmail, inputPassword);
            }

        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getUserNamePassword(EditText inputEmail, EditText inputPassword) {
        JSONObject credentials = createLoginCredentials(inputEmail.getText().toString(),
                inputPassword.getText().toString()) ;
        sendCredentials(credentials);
    }

    private void sendCredentials(JSONObject credentialsHelper){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = service+server+resource;
        Log.d(TAG, "Here is where we contact the server with"+ credentialsHelper);
        Log.d(TAG, "Request URL "+url);
        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, credentialsHelper,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log the response string.
                        Log.d(TAG, "Got token: "+response);

                        String token = null;
                        try {
                            token = response.getString("token");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }

                        //Send back to main Activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(MainActivity.LOGIN_TOKEN, token);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int returnCode = error.networkResponse.statusCode;
                if (returnCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                    retrySeconds = 2*retrySeconds;
                    Log.d(TAG, "Unauthorized, increasing delay to "+retrySeconds + " seconds.");
                    signInButton.setClickable(false);
                    resultTextView.setText("Incorrect User Name or Password \nPlease retry in "+retrySeconds + "seconds");
                    resultTextView.setTextSize(12);
//                    try {
//                        Thread.sleep(retrySeconds * 1000);
//                    } catch (Exception e){
//                        Log.e(TAG,  e.getMessage());
//                    }
                    signInButton.setClickable(true);
//                    resultTextView.setText("");
                } else {

                    resultTextView.setText("Server Error, Please Try Later.");
                    Log.d(TAG, "That didn't work! " + error.getMessage());
                    Log.d(TAG, "Response code: " + error.networkResponse.statusCode);
                    Log.d(TAG, "Data is bytes long :" + error.networkResponse.data.length);
                    Log.d(TAG, "Data :" + new String(error.networkResponse.data));
                    Log.d(TAG, "Cause :" + error.getCause());
                }
            }
        }) ;
        // Add the request to the RequestQueue.
        queue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
