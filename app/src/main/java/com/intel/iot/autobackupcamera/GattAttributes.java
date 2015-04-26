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

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();
    public static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_MEASUREMENT = "00002904-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String ACTIVATE_WIFI = "f8b67f8a-d10e-4c29-bab7-b604f681bf41";
    public static String ACTIVATE_CAMERA = "e3251ee6-922e-44a7-8b35-4438bfc8a5f5";
    public static String SHUTDOWN = "4de865c5-5f02-4891-b9f1-d2f8e4f6f806";
    public static String ADDRESS_DESCRIPTOR = "e3251ee6-922e-44a7-8b35-4438bfc8a5f6";

    static {
        // Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("5b580496-d85f-4cd0-8001-46ff648c706c", "Wake Edison");

        // Characteristics.
        attributes.put(BATTERY_LEVEL, "Battery Level");
        attributes.put(DEVICE_NAME, "Device Name");
        attributes.put(APPEARANCE, "Appearance");
        attributes.put(ACTIVATE_WIFI, "Activate Wifi");
        attributes.put(ACTIVATE_CAMERA, "Activate Camera");
        attributes.put(BATTERY_MEASUREMENT, "Battery Measurement");
        attributes.put(SHUTDOWN, "Shutdown Parallel Assistant");

        //Descriptors.
        attributes.put(ADDRESS_DESCRIPTOR, "Address");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
