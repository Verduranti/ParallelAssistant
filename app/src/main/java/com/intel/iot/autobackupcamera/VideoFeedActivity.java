package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by verduranti on 3/5/15.
 */
public class VideoFeedActivity extends Activity implements ParallelConnectionListener {
//placeholder for something better than WebViewVideoActivity

    //private EditText field;
    private WebView browser;
    private MenuItem refresh;

    //private MediaCodecWrapper mCodecWrapper; //see example
    private MediaExtractor mExtractor = new MediaExtractor();
    //private WifiDirectUtility mWifiUtil = new WifiDirectUtility(this);
    private BLEUtility mBLEUtility = new BLEUtility(this);

    @Override
    public void connected() {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void error() {

    }

    private class MyChrome extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i("WebView", consoleMessage.toString());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void resizeBrowser() {
        int width = browser.getWidth();
        int height = browser.getHeight();
        if(width*3 > height*4) { // Assume landscape
            ViewGroup.LayoutParams lp = browser.getLayoutParams();
            lp.height = height;
            lp.width = height * 4 / 3;
            browser.setLayoutParams(lp);
        }
        else { // Assume portrait
            ViewGroup.LayoutParams lp = browser.getLayoutParams();
            lp.height = width * 3 / 4;
            lp.width = width;
            browser.setLayoutParams(lp);
        }
        System.out.println(width + " x " + height);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBLEUtility.startBLEScan();
        //mWifiUtil.setup(this);

        //Connect to the device via bluetooth. Ask it to wake up the wifi.

        //field = (EditText)findViewById(R.id.urlField);
        browser = (WebView)findViewById(R.id.webView);

        browser.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                resizeBrowser();
            }
        });

        browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new MyChrome());

        open();
    }

    protected void onResume() {
        super.onResume();
        //Restart if not connected
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mBLEUtility.pauseBLEService();
        //mBLEUtility.pauseWifiDirectService();
    }


    public void open(){
        //String url = "http://192.168.1.6/";
        String url = "http://192.168.42.1:8080";
        //String url = "http://192.168.42.1/";
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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
