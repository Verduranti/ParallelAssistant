package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by verduranti on 2/12/15.
 */
public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 10000; //3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv = (TextView) findViewById(R.id.textSplashTop);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/CenturyGothicStdRegular.otf");
        tv.setTypeface(tf, 1);
        tv = (TextView) findViewById(R.id.textSplashBottom);
        tv.setTypeface(tf);
        tv = (TextView) findViewById(R.id.textSplashBottom2);
        tv.setTypeface(tf);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                //Intent i = new Intent(SplashScreen.this, MainActivity.class);
                Intent i = new Intent(SplashScreenActivity.this, DeviceScanActivity.class);
                //Intent i = new Intent(SplashScreen.this, WifiDirectActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
