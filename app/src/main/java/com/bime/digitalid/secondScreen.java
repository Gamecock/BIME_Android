package com.bime.digitalid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class secondScreen extends AppCompatActivity {
    private String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

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
