package com.example.scanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnrating;
    RatingBar ratingBar;
    int myRating = 0;
    private TextView txtqrcode;

    // first String means Url is in string, Void mean nothing, Third String means return type will be in string
    class QRCODE extends AsyncTask<String, Void, String> {
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
        setContentView(R.layout.activity_main);

        btnrating = findViewById(R.id.btnrating);
        ratingBar = findViewById(R.id.ratingBar);

        txtqrcode = findViewById(R.id.txtqrname);
       QRCODENameReader();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                int rating = (int) v;
                String message = null;

                myRating = (int) ratingBar.getRating();
                switch (rating) {
                    case 1:
                        message = "Sorry to hear that \uD83D\uDE29";
                        break;
                    case 2:
                        message = "You Always accept suggestiongs! \uD83D\uDE25";
                        break;
                    case 3:
                        message = "good Enough! \uD83E\uDD7A";
                        break;

                    case 4:
                        message = "Great! Thank You  ☺️";
                        break;
                    case 5:
                        message = "Awesome! You are the Best! \uD83E\uDD29";
                        break;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        btnrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Your Rating is : " + myRating, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void QRCODENameReader() {
        String details;

        QRCODE qrcode = new QRCODE();
        try {

            details = qrcode.execute("http://192.168.0.101:3000/QRscanner").get();
            //First we will check data is retrieve successfully or not
            Log.i("contentData", details);

            JSONArray array  = new JSONArray(details);
            String name = "";
            for (int i = 0; i < array.length(); i++) {
                JSONObject Qrcodepart = array.getJSONObject(i);
                name = Qrcodepart.getString("name");
                txtqrcode.setText(name);
                Log.i("QR data", name);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}