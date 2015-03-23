/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;

public class WifiDirectActivity extends Activity {

    private Channel mChannel;
    private WifiP2pManager mManager;
    private BroadcastReceiver mWifiReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        //System.out.println("WIfi Manager set here: " + mManager.toString());
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mWifiReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //connect("fe:c2:de:35:ef:4d", "90583483");
        //System.out.println("Wifi Broadcast set here: " + mWifiReceiver.toString());
        //mWifiReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel);
        registerReceiver(mWifiReceiver, makeWifiDirectIntentFilter());
    }

    public void connect(String address, String pin)
    {
        System.out.println("Connecting to "+address+" with "+pin);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.wps.setup = WpsInfo.KEYPAD;
        config.wps.pin = pin;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Succeeded in connection");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Connection failed try again -> " + reason);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbind services if any here
    }

    private static IntentFilter makeWifiDirectIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }
}
