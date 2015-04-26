package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.util.Log;

//Placeholder right now - fix this

/**
 * Created by verduranti on 3/20/15.
 */
public class WifiDirectUtility {
    private final static String TAG = WifiDirectUtility.class.getSimpleName();

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    public WifiDirectUtility (Activity activity) {
        WifiP2pManager mManager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
//            //System.out.println("WIfi Manager set here: " + mManager.toString());
        Channel mChannel = mManager.initialize(activity.getApplicationContext(),
                activity.getMainLooper(), null);
    }

    private void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Discover Peers Succeeded!");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //connect("fe:c2:de:35:ef:4d"); //sapphire
                        connect("7a:4b:87:ac:9b:de"); //rainier
                    }
                }, 0);
            }
            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "Discover Peers failed with error code: "
                        + reasonCode);
                //if(reasonCode != 2)
                //    return;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //connect("fe:c2:de:35:ef:4d"); //sapphire
                        connect("7a:4b:87:ac:9b:de"); //rainier
                    }
                }, 0);
            }
        });
    }


    public void connect(String address)
    {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Succeeded in connection");
                //Intent i = new Intent(DeviceControlActivity.this, WebViewVideoActivity.class);
                //startActivity(i);

            }

            @Override
            public void onFailure(int reason) {

                Log.e(TAG, "Connection failed try again -> " + reason);
            }
        });
    }

}
