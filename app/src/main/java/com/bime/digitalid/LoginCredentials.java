package com.bime.digitalid;

import org.json.JSONObject;

public class LoginCredentials extends JSONObject {

    private String BannerId = null;
    private String Password = null;

    public LoginCredentials(String BannerId, String Password){
        this.BannerId = BannerId;
        this.Password = Password;
    }
}
