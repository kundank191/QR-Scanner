package com.example.kunda.qrscanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    private int FRONT_CAMERA_ID = 1;
    private int REAR_CAMERA_ID = 0;
    private int selectedCameraId = FRONT_CAMERA_ID;
    private Collection<String> selectedScanType = IntentIntegrator.ALL_CODE_TYPES;
    private String scanTitle = "Scan All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the camera to be used from user input
        final RadioGroup cameraType = findViewById(R.id.radio_group_cameras);
        cameraType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_front_camera:
                        selectedCameraId = FRONT_CAMERA_ID;
                        break;
                    case R.id.radio_rear_camera:
                        selectedCameraId = REAR_CAMERA_ID;
                        break;
                }
            }
        });
        // get the scan type from user
        RadioGroup scanType = findViewById(R.id.radio_group_scan_type);
        scanType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_scan_all:
                        selectedScanType = IntentIntegrator.ALL_CODE_TYPES;
                        break;
                    case R.id.radio_scan_qr:
                        selectedScanType = IntentIntegrator.QR_CODE_TYPES;
                        break;
                    case R.id.radio_scan_bar:
                        selectedScanType = IntentIntegrator.ONE_D_CODE_TYPES;
                        break;
                }
            }
        });
        // initiate scan
        (findViewById(R.id.fab_go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(selectedCameraId, selectedScanType, scanTitle);
            }
        });
    }

    /**
     * @param cameraID the camera to be used
     * @param scanType the type of scan to be performed
     * @param title    title of the camera screen
     */
    private void startScan(int cameraID, Collection<String> scanType, String title) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(scanType);
        integrator.setPrompt(title);
        integrator.setCameraId(cameraID);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result of the zxing library
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // null safety
        if (result != null) {
            if (result.getContents() == null) {
                //cancel
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
            } else {
                //Scanned successfully
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                //Extract uri from the result
                String uriData = result.getContents();
                try {
                    Uri uri = Uri.parse(uriData);
                    Intent resultIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(resultIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error reading qr , error : " + uriData, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
