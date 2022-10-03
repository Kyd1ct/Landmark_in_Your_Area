package com.example.geofence_cmp309_1901560;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


public class BrowserActivity extends AppCompatActivity {
    // Simple WebView class which grabs the intent from MapsActivity and opens the landmark website.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);

        WebView webView = findViewById(R.id.browserV);
        webView.setWebChromeClient(new WebChromeClient());
        Intent intent = getIntent();
        String Link = this.getIntent().getStringExtra("Search");

        Uri data = this.getIntent().getData();


        if (intent.hasExtra("Search")){
            webView.loadUrl(Link);
        }
        else {
            webView.loadUrl(String.valueOf(data));
        }
    }

}