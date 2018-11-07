package com.bime.digitalid;

import android.content.Context;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.robolectric.shadows.ShadowLog;

import androidx.test.core.app.ApplicationProvider;
import android.support.test.rule.ActivityTestRule;
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

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }

        @Test
        public void buttonClickCallsServer(){

            // Given a Context object retrieved from Robolectric...
            MainActivity myMainActivity = new MainActivity();
            server = new MockWebServer();
            try {
                server.start();
                server.enqueue(new MockResponse().setBody("{ \"content\" : \"Hello, Brian!\" }"));
            } catch (Exception e) {
                System.out.println("Exception:"+e.getMessage());
            }

            HttpUrl baseURL = server.url("/v1/chat");
                int port = baseURL.port();
                String testServer = new StringBuilder("localhost:").append(port).toString();
                MainActivity.server = testServer;
                System.out.println( "ServerUrl: "+baseURL);
                System.out.println("TestURL:" +testServer);

                //Test Action
                myMainActivity.getQRText("/greeting");
                System.out.println("Function called:");
            try{
                request = server.takeRequest();
                path = request.getPath();

            } catch (Exception e) {
                System.out.println("Exception:"+e.getMessage());
            }

            assertEquals("/greeting?name=Brian", path );

        }

}