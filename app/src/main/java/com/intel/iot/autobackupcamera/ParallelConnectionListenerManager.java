package com.intel.iot.autobackupcamera;

/**
 * Created by verduranti on 4/26/15.
 */

import java.util.HashSet;
import java.util.Set;

public class ParallelConnectionListenerManager {
    private Set<ParallelConnectionListener> listeners = new HashSet<>();

    public void addListener(ParallelConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ParallelConnectionListener listener) {
        listeners.remove(listener);
    }

    public void connected() {
        for(ParallelConnectionListener listener : listeners) {
            listener.connected();
        }
    }

    public void disconnected() {
        for(ParallelConnectionListener listener : listeners) {
            listener.disconnected();
        }
    }

    public void error() {
        for(ParallelConnectionListener listener : listeners) {
            listener.error();
        }
    }
}
