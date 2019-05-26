package com.s0woo.computervisionterm;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editLocation);
        btn = (Button)findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String str = editText.getText().toString();
                final Handler mHandler = new Handler();

                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try{
                            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+ str + "&key=" + getString(R.string.google_map_key));

                            HttpURLConnection http = (HttpURLConnection) url.openConnection();
                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);
                            http.setDoOutput(true);
                            http.setRequestMethod("POST");
                            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                            BufferedReader reader = new BufferedReader(tmp);
                            StringBuffer buffer = new StringBuffer();
                            String strResult;

                            while ((strResult = reader.readLine()) != null) {
                                System.out.println("in while : " + strResult);
                                if(strResult.contains("lat")) {
                                    buffer.append(strResult);
                                } else if (strResult.contains("lng")) {
                                    buffer.append(strResult);
                                    break;
                                }
                            }

                            String longResult = buffer.toString();

                            if(longResult.length() >1) {
                                String lat = longResult.substring(longResult.indexOf(":")+2, longResult.indexOf(","));
                                String templng = longResult.substring(longResult.indexOf(","));
                                String lng = templng.substring(templng.indexOf(":")+2);

                                Intent intent = new Intent(MainActivity.this, VrPanoramaActivity.class);
                                intent.putExtra("lat", lat);
                                intent.putExtra("lng", lng);
                                startActivity(intent);
                            } else {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "주소를 다시 입력하세요", Toast.LENGTH_LONG).show();
                                    }
                                }, 0);
                            }

                            reader.close();

                        }catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mThread.start();
            }
        });
    }
}
