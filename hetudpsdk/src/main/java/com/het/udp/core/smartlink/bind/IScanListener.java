package com.het.udp.core.smartlink.bind;


import com.het.udp.wifi.model.UdpDeviceDataBean;

/**
 * Created by uuxia on 2015/4/14.
 */
public interface IScanListener {
    /**
     * 发现设备
     *
     * @param device
     */
    void onFind(UdpDeviceDataBean device);

    /**
     * 扫描进度
     *
     * @param persent
     */
    void onScanProgress(int persent);
}
