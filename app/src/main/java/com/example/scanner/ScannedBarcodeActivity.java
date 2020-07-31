package com.example.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ScannedBarcodeActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";
    boolean isEmail = false;

    // first String means Url is in string, Void mean nothing, Third String means return type will be in string
    class QRCODEREADER extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... address) {
            //String... means multiple address can be send. It acts as array
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //Establish connection with address
                connection.connect();

                //retrieve data from url
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                //Retrieve data and return it as String
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                Log.i("Content", content);
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    if (isEmail)
                        startActivity(new Intent(ScannedBarcodeActivity.this, MainActivity.class));
                    else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    }
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    String details;

                    QRCODEREADER qrcode = new QRCODEREADER();
                    try {

                        details = qrcode.execute("http://192.168.0.101:3000/QRscanner").get();
                        //First we will check data is retrieve successfully or not
                        Log.i("contentData", details);

                        JSONArray array  = new JSONArray(details);
                        String name = "";
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject Qrcodepart = array.getJSONObject(i);
                            name = Qrcodepart.getString("name");
//                            txtBarcodeValue.setText(name);
                            Log.i("QR data", name);

                            if(intentData.equals(name)) {
                                startActivity(new Intent(ScannedBarcodeActivity.this, MainActivity.class));
                            }
                            else{
                                txtBarcodeValue.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isEmail = false;
                                        btnAction.setText("Does Not Match Barcode");
                                        btnAction.setEnabled(false);
                                        intentData = barcodes.valueAt(0).displayValue;
                                        txtBarcodeValue.setText(intentData);
                                    }
                                });
                            }

                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }



//                    txtBarcodeValue.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            if (barcodes.valueAt(0).email != null) {
//                                txtBarcodeValue.removeCallbacks(null);
//                                intentData = barcodes.valueAt(0).email.address;
//                                txtBarcodeValue.setText(intentData);
//                                isEmail = true;
//                            } else {
//                                isEmail = false;
//                                btnAction.setText("Open App");
//                                btnAction.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Intent intent = new Intent(ScannedBarcodeActivity.this, MainActivity.class);
//                                        startActivity(intent);
//                                    }
//                                });
//                                intentData = barcodes.valueAt(0).displayValue;
//                                txtBarcodeValue.setText(intentData);
//
//                            }
//                        }
//                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}

