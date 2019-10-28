package com.example.memorialpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SearchWebView extends AppCompatActivity {

    WebView webView;
    WebSettings webSettings;

    String word;

    public void init(){
        webView = findViewById(R.id.sw_webview);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_web_view);

        if(getIntent() != null){
            word = getIntent().getStringExtra("word");
        }

        init();
        setSetting(webView);

        goToSurf(webView, word);
    }

    public void setSetting(WebView view){

        view.setWebViewClient(new WebViewClient());
        webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
    }

    public void goToSurf(WebView view, String word){
        view.loadUrl("https://search.naver.com/search.naver?query=" + word);
    }
}
