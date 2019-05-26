package com.s0woo.computervisionterm;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VrPanoramaActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private VrPanoramaView panoramaView;
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();

    public boolean loadImageSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        panoramaView.setEventListener(new ActivityEventListener());

        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;

        final String lat = getIntent().getStringExtra("lat");
        final String lng = getIntent().getStringExtra("lng");

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try{
                    URL url = new URL("https://maps.googleapis.com/maps/api/streetview?size=4000x570&location=" + lat + "," + lng + "&key=" + getString(R.string.google_map_key));

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start();

        try {
            mThread.join();
            panoramaView.loadImageFromBitmap(bitmap, panoOptions);
            panoramaView.setDisplayMode(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private class ActivityEventListener extends VrPanoramaEventListener {

        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    VrPanoramaActivity.this, "파노라마 생성 에러 : " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
