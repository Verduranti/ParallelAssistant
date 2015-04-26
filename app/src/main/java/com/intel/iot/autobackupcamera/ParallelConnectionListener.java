package com.intel.iot.autobackupcamera;

/**
 * Created by verduranti on 4/26/15.
 */
public interface ParallelConnectionListener {

    public void connected();

    public void disconnected();

    public void error();

}
