package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * Created by verduranti on 3/20/15.
 */
public class WifiDirectUtility {

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    public void setup(Activity activity)
    {
        WifiP2pManager mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
//            //System.out.println("WIfi Manager set here: " + mManager.toString());
            Channel mChannel = mManager.initialize(activity.getApplicationContext(),
                    activity.getMainLooper(), null);
    }

    public void connect(String address, String pin)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.wps.setup = WpsInfo.KEYPAD;
        config.wps.pin = pin;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("succeeded in connection");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("fail try again");
            }
        });
    }
}
