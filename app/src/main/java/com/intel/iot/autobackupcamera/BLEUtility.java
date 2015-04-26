package com.intel.iot.autobackupcamera;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by verduranti on 3/20/15.
 */
public class BLEUtility {
    private final static String TAG = BLEUtility.class.getSimpleName();

    private BluetoothLeService mBluetoothLeService;
    private Activity mActivity;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private boolean mConnected = false;
    private String mDeviceAddress;

    private ParallelConnectionListenerManager mListeners;

    //TODO: Move this
    private WifiDirectUtility mWifiUtil;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public BLEUtility(Activity activity) {
        mActivity = activity;
        mHandler = new Handler();
    }

    public boolean checkBluetooth() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mActivity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(mActivity, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void startBLEScan() {
        checkBluetooth();
        mListeners = new ParallelConnectionListenerManager();
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    //If the device is the correct one, go ahead and connect to it
                    if(device.getName() == null)
                    {
                        //Not interested in these
                        return;
                    }
                    Log.i(TAG, "Found " + device.getName());
                    if(device.getName().equals("Parallel Demo S"))
                    {
                        Log.i(TAG, "Connecting to parallel demo");
                        mDeviceAddress = device.getAddress();
                        startBLEService();
                    }
                }
            };


    public void startBLEService() {
        Intent gattServiceIntent = new Intent(mActivity, BluetoothLeService.class);
        mActivity.bindService(gattServiceIntent, mServiceConnection, Activity.BIND_AUTO_CREATE);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            initializeBLEService(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    //Must be called after startBLEService
    private void initializeBLEService(IBinder service) {
        mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
        if (!mBluetoothLeService.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }

        mActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // Automatically connects to the device upon successful start-up initialization.
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mBluetoothLeService.connect(mDeviceAddress);
//
//            }
//        });
        mBluetoothLeService.connect(mDeviceAddress);
    }

    public void pauseBLEService() {
        mActivity.unregisterReceiver(mGattUpdateReceiver);

    }

    //TODO: Move this
    public void pauseWifiDirectService() {
        mWifiUtil.pauseWifiDirect();
    }

    public void addListener(ParallelConnectionListener listener) {
        mListeners.addListener(listener);
    }

    public void removeListener(ParallelConnectionListener listener) {
        mListeners.removeListener(listener);
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
                //invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
                final Map<UUID, BluetoothGattCharacteristic> blecharMap = new HashMap<>();
                for(BluetoothGattService serv : mBluetoothLeService.getSupportedGattServices())
                {
                    Log.i(TAG, serv.getUuid().toString());
                    if(serv.getUuid().equals(UUID.fromString("5b580496-d85f-4cd0-8001-46ff648c706c")))
                    {
                        Log.i(TAG, "Wake Edison Service Found");
                        for(BluetoothGattCharacteristic blechar : serv.getCharacteristics())
                        {
                            blecharMap.put(blechar.getUuid(), blechar);
                        }
                    }
                }

                readCharacteristic(blecharMap, GattAttributes.ACTIVATE_WIFI);

                //TODO: A bad permanent place for this, but allowing it for now
                //Get a WifiDirect connection going

                mWifiUtil = new WifiDirectUtility(mActivity);
                mWifiUtil.addListener(new ParallelConnectionListener() {
                    @Override
                    public void connected() {
                        readCharacteristic(blecharMap, GattAttributes.ACTIVATE_CAMERA);
                    }

                    @Override
                    public void disconnected() {

                    }

                    @Override
                    public void error() {

                    }
                });
                mWifiUtil.initializeWifiDirect();

                mListeners.connected();



            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //What to do with data
                //int test = intent.getIntExtra(BluetoothLeService.EXTRA_NAME, -1);
                //if(test == 0 || test == 1)
                //{
                    //Log.i(TAG,"Try to start Wifi P2P - does nothing right now");
                    //0: This is where we told the app to activate wifi P2P
                    //1: Where we told the board to try and connect with us
                    //-1: anything else
                    //discoverPeers();
                //}
            }
        }
    };

    private void readCharacteristic(final Map<UUID, BluetoothGattCharacteristic> blecharMap,
                                    final String characteristic) {
        final BluetoothGattCharacteristic blechar =
                blecharMap.get(UUID.fromString(characteristic));
        if(blechar != null)
        {
            mBluetoothLeService.readCharacteristic(blechar);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
