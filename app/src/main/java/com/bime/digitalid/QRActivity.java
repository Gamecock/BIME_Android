package com.bime.digitalid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import static com.bime.digitalid.MainActivity.QR_STATUS;
import static com.bime.digitalid.MainActivity.QR_TEXT;

public class QRActivity extends Activity {

    private final String TAG = "QR";

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        ImageView qrView = findViewById(R.id.qr_view);
        Intent intent = getIntent();
        String text = intent.getStringExtra(QR_TEXT);
        int code = intent.getIntExtra(QR_STATUS, 0);
        if (200 == code) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
                Log.d(TAG, "Created barcode with content:" + text);
                qrView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.e(TAG, "Error response need to know what to show");
        }

    }
}
