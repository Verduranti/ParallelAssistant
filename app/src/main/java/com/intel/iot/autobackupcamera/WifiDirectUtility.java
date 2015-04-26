package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;

import java.util.Collection;

/**
 * Created by verduranti on 3/20/15.
 */
public class WifiDirectUtility {
    private final static String TAG = WifiDirectUtility.class.getSimpleName();

    private Activity mActivity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private DirectWifiPeersListener mPeerListListener;
    private DirectWifiGroupInfoListener mGroupInfoListener;
    private boolean mConnected = false;
    private boolean mConnectInProgress = false;

    private ParallelConnectionListenerManager mListeners = new ParallelConnectionListenerManager();

    public WifiDirectUtility (Activity activity) {
        mActivity = activity;
    }

    public void removeListener(ParallelConnectionListener listener) {
        mListeners.removeListener(listener);
    }

    public void addListener(ParallelConnectionListener listener) {
        mListeners.addListener(listener);
    }

    public void initializeWifiDirect() {
        mManager = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mActivity.getApplicationContext(),
                mActivity.getMainLooper(), null);
        mPeerListListener = new DirectWifiPeersListener();
        mGroupInfoListener = new DirectWifiGroupInfoListener();

        //mManager.requestGroupInfo(mChannel, mGroupInfoListener);
        mActivity.registerReceiver(mWifiReceiver, makeWifiDirectIntentFilter());
    }

    public void pauseWifiDirect() {
        mActivity.unregisterReceiver(mWifiReceiver);
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
                        //connect("7a:4b:87:ac:9b:de"); //rainier
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
                        //connect("7a:4b:87:ac:9b:de"); //rainier
                    }
                }, 0);
            }
        });
    }


    synchronized public void connect(String address)
    {
        if(mConnected || mConnectInProgress) {
            return;
            //Don't connect over and over again. Seriously. Stop that.
        }
        Log.i(TAG, "Attempting Wifi P2P connection");
        mConnectInProgress = true;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Succeeded in Wifi P2P connection");
                //pauseWifiDirect();
                mConnected = true;
                mConnectInProgress = false;
                mListeners.connected();
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Connection Failed. Reason Code: " + reason);
                mConnectInProgress = false;
            }
        });
    }



    // Handles various events fired by the Service.
    // WIFI_P2P_STATE_CHANGED_ACTION:
    // WIFI_P2P_CONNECTION_CHANGED_ACTION:
    // WIFI_P2P_PEERS_CHANGED_ACTION:
    // WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
    private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.i(TAG, "Wifi P2P Enabled");
                    //discoverPeers(); // 1
                    mManager.requestGroupInfo(mChannel, mGroupInfoListener);
                } else {
                    Log.i(TAG, "Wifi P2P Disabled!");
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.i(TAG, "Wifi P2P Connection Changed");
                connectionChanged(intent); // 2
                // Respond to new connection or disconnections
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.i(TAG, "Wifi P2P Peers Changed");
                //connect("fe:c2:de:35:ef:4d", "90583483");
                mManager.requestPeers(mChannel, mPeerListListener); // 3
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
                Log.i(TAG, "Android Wifi P2P State Changed");
            }
        }
    };

    private void connectionChanged(Intent intent) {
        if (mManager == null) {
            Log.e(TAG, "WifiP2PManager is missing");
            return;
        }
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo == null) {
            Log.e(TAG, "Connection info is missing");
            return;
        }
        if (networkInfo.isConnected()) {
            //mManager.requestConnectionInfo(mChannel, myConnectionInfoListener);
            Log.i(TAG, "New Connected Network info: " + networkInfo.toString());
        } else {
            // It's a disconnect
            Log.i(TAG, "Network disconnected: " + networkInfo.toString());
        }

    }

    public class DirectWifiPeersListener implements WifiP2pManager.PeerListListener {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            Log.i(TAG,"Found peers");
            for (WifiP2pDevice peer : peers.getDeviceList()) {
                Log.i(TAG, "WifiP2P Address: " + peer.deviceAddress);
                Log.i(TAG, "WifiP2P Name: " + peer.deviceName);
                if(peer.deviceName.equals("sapphire")) {
                    connect(peer.deviceAddress);
                }
            }
        }
    }

    public class DirectWifiGroupInfoListener implements WifiP2pManager.GroupInfoListener {
        // this gets called all the bloody time
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            mConnected = false;
            if(group != null)
            {
                Collection<WifiP2pDevice> groupColl = group.getClientList();
                for(WifiP2pDevice device : groupColl) {
                    Log.i(TAG, device.deviceName);
                    if (device.deviceName.equals("sapphire")) {
                        mConnected = true;
                        mListeners.connected();
                    }
                }
            }
            if(!mConnected)
            {
                discoverPeers();
            }
        }
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
