package com.bime.digitalid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateQR("Hello World");

                Button button1 = (Button)findViewById(R.id.button1);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    Intent startIntent =  new Intent(getApplicationContext(),secondScreen.class);
                    startIntent.putExtra()
                    startActivity(startIntent);
                    }
                });

            }
        });
    }

    private void generateQR(String content) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = findViewById(R.id.button1);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() );
        }
    }


}
