package com.bime.digitalid;

import android.content.Context;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ApplicationProvider;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.*;

public class MainUnitTests {

        String TAG = "Test";
        MockWebServer server;
        RecordedRequest request;
        String path;

//    private Context context = ApplicationProvider.getApplicationContext();

        @Test
        public void buttonClickCallsServer(){

            // Given a Context object retrieved from Robolectric...
            MainActivity myMainActivity = new MainActivity();
            server = new MockWebServer();
            try {
                server.start();
                server.enqueue(new MockResponse().setBody("{ \"content\" : \"Hello, Brian!\" }"));
                HttpUrl baseURL = server.url("/v1/chat");
//                MainActivity.server = baseURL.toString();
                MainActivity.server = "2.2.2.2:56150";
                System.out.println( "ServerUrl: "+baseURL);

                //Test Action
                myMainActivity.getQRText("/greeting");

                request = server.takeRequest();
                path = request.getPath();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            assertEquals("/greeting?name=Brian", path );

        }

}