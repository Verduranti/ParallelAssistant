package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewVideoActivity extends Activity {

    //private EditText field;
    private WebView browser;

    //private MediaCodecWrapper mCodecWrapper; //see example
    private MediaExtractor mExtractor = new MediaExtractor();

    private MenuItem refresh;

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class MyChrome extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i("WebView", consoleMessage.toString());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //field = (EditText)findViewById(R.id.urlField);
        browser = (WebView)findViewById(R.id.webView);
        //browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new MyChrome());
        open();
    }

    public void open(){
        //String url = "http://192.168.1.6/";
        String url = "http://192.168.42.1:8080";
        //String url = "http://192.168.42.1/";
        //browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        //browser.getSettings().setAppCacheEnabled(false);
        //browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        refresh = menu.findItem(R.id.menu_refresh);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                browser.reload();
                return false;
            }
        });

        menu.findItem(R.id.menu_stop).setVisible(false);
        menu.findItem(R.id.menu_scan).setVisible(false);
        refresh.setActionView(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
