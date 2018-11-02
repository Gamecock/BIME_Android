package com.bime.digitalid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String LOGIN_TOKEN = "com.bime.digitalid.TOKEN";
    public static final Integer LOGIN_CODE = 123;
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
                new Main2Activity();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                token = data.getStringExtra(LOGIN_TOKEN);
                //TODO: error handling
            }
        } else {
            Log.e(TAG, "No idea what is request code:" + requestCode);
        }
    }


    private void generateQR(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = findViewById(R.id.qr_view);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage() );

        }
    }
}


