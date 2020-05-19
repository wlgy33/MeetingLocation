package com.example.meetinglocation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.GeoApiContext;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Centroid extends AppCompatActivity{
    private static final String TAG = "result";
    TextView centroid;
    String stringUrl = "https://us-central1-meetinglocation-492f2.cloudfunctions.net/MeetingLocationCentroid?location=";
    String data = "{";
    private GeoApiContext mGeoApiContext = null;
    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.centroid_result);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        centroid = findViewById(R.id.centroid);
        Intent intent = getIntent();
        ArrayList<AddressItem> addresses = (ArrayList<AddressItem>) intent.getSerializableExtra("locations");
        for (int i = 0; i < addresses.size(); i++) {
            data += addresses.get(i).latlng;
            if (i!=addresses.size()-1)
                data += ",";
        }
        stringUrl = stringUrl  + data+"}";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(stringUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    Centroid.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            centroid.setText(myResponse);

                        }
                    });
                }
            }
        });
        String stringCentroid = (String) centroid.getText();
        Intent pushIntent = new Intent(Centroid.this, MainActivity.class);
        pushIntent.putExtra("centroid",stringCentroid);
    }

}

