package com.bime.digitalid;

import android.content.Context;
import androidx.test.InstrumentationRegistry;

import android.util.Log;

import com.android.volley.RequestQueue;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class MainActivityTest {

    String TAG = "Test";
    MockWebServer server;
    RecordedRequest request;
    String path;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule
            = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void buttonClickCallsServer(){
        server = new MockWebServer();
        try {
            server.start();
            server.enqueue(new MockResponse().setBody("{ \"content\" : \"Hello, Brian!\" }"));
            HttpUrl baseURL = server.url("/v1/chat");
            MainActivity.server = baseURL.toString();
            Log.d(TAG, "ServerUrl: "+baseURL);

            //Test Action
            onView(withId(R.id.button)).perform(click());

            request = server.takeRequest();
            path = request.getPath();

        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }

        assertEquals("/greeting?name=Brian", path );

    }

}