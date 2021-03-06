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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static com.bime.digitalid.CredentialsHelper.createLoginCredentials;
import static com.bime.digitalid.MainActivity.server;
import static com.bime.digitalid.MainActivity.service;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "Login";
    private String resource = "/api/authentication/authenticate";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created Login Activity");
        setContentView(R.layout.activity_login);
        Button signInButton = findViewById(R.id.SignInButton);
        final EditText inputEmail = findViewById(R.id.inputBannedId);
        final EditText inputPassword = findViewById(R.id.inputPassword);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserNamePassword(inputEmail, inputPassword);

            }
        });

        TextView resultTextView = (TextView) findViewById(R.id.resultTextView);


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
        Log.d(TAG, "Here is where we contact the server with"+ credentials);
        sendCredentials(credentials);
    }

    private void sendCredentials(JSONObject credentialsHelper){

        String url = service+server+resource;

        // Post Credentials to Server.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, credentialsHelper,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log the response string.
                        Log.d(TAG, "Got response: "+response);

                        String token = null;
                        String bannerID = null;
                        try {
                            token = response.getString("token");
                            bannerID = response.getString("bannerID");
                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Log.d(TAG, "Got token: "+token);
                        Log.d(TAG, "Got bannerID: "+bannerID);

                        //Send back to main Activity
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(MainActivity.LOGIN_TOKEN, token);
                        returnIntent.putExtra(MainActivity.BANNER_ID, bannerID);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work! "+error.toString());
                Log.d(TAG, "Response :"+error.networkResponse.statusCode);
                Log.d(TAG, new String(error.networkResponse.data));
            }
        }) ;
        // Add the request to the RequestQueue.
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(request);

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
