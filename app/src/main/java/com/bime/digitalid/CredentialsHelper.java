package com.bime.digitalid;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public final class CredentialsHelper {
    private CredentialsHelper() { throw new IllegalStateException("No Instances!"); }

    public static JSONObject createLoginCredentials (String BannerId, String Password){
        final JSONObject credentials = new JSONObject();
        try {
            credentials.put("bannerID", BannerId);
            credentials.put("password", Password);
        } catch (JSONException e){
            Log.e("Login: ", e.getMessage() );
        }
        return credentials;
    }
}
