package com.intel.iot.autobackupcamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Handler;


public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private Channel mChannel;
    private DirectWifiPeersListener myPeerListListener;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
        super();

        this.mManager = manager;
        this.mChannel = channel;
        this.myPeerListListener = new DirectWifiPeersListener();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                System.out.println("WIFI P2P Enabled");
                discoverPeers(); // 1
            } else {
                System.out.println("Wifi P2P disabled!");
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            connectionChanged(intent); // 2
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //connect("fe:c2:de:35:ef:4d", "90583483");
            mManager.requestPeers(mChannel, myPeerListListener); // 3
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    private void discoverPeers() {
        System.out.println("Test: " + mChannel.toString());
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Discover Peers Succeeded!");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        connect("fe:c2:de:35:ef:4d", "14168314");

                    }
                }, 0);
            }

            @Override
            public void onFailure(int reasonCode) {
                System.out.println("Discover Peers failed with error code: "
                        + reasonCode);
            }
        });

    }

    private void connectionChanged(Intent intent) {
        if (mManager == null) {
            return;
        }
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo == null) {
            System.out.println("connection info is null");
            return;
        }
        if (networkInfo.isConnected()) {
            //mManager.requestConnectionInfo(mChannel, myConnectionInfoListener);
            System.out.println(networkInfo.toString());
        } else {
            // It's a disconnect
            System.out.println(networkInfo.toString());
        }

    }

    public void listPeers() {
        mManager.requestPeers(mChannel, myPeerListListener);
    }

    public class DirectWifiPeersListener implements PeerListListener {


        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
            boolean foundOwner = false;
            System.out.println("Found peers");
            //Toast.makeText(mActivity, "found peers", Toast.LENGTH_LONG).show();
            for (WifiP2pDevice peer : peers.getDeviceList()) {
                System.out.println(peer.deviceAddress);
//                WifiP2pConfig config = new WifiP2pConfig();
//                config.deviceAddress = peer.deviceAddress;
//                config.wps.setup = WpsInfo.KEYPAD;
//                config.wps.pin = "90583483";
//                mManager.connect(mChannel, config, new ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        System.out.println("succeeded in connection");
//                    }
//
//                    @Override
//                    public void onFailure(int reason) {
//                        System.out.println("fail try again");
//                    }
//                });
                connect(peer.deviceAddress, "14168314");
            }
        }


    }

    public void connect(String address, String pin)
    {
        System.out.println("Connecting to "+address+" with "+pin);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = address;
        //config.wps.setup = WpsInfo.KEYPAD;
        config.wps.setup = WpsInfo.LABEL;
        config.wps.pin = pin;
        mManager.connect(mChannel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("Succeeded in connection");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("Connection failed try again -> "+reason);
            }
        });
    }
}