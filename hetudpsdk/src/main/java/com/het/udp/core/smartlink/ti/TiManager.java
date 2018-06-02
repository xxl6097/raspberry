package com.het.udp.core.smartlink.ti;

import android.content.Context;

import com.het.log.Logc;
import com.het.udp.core.smartlink.ti.callback.SmartConfigConstants;
import com.het.udp.core.smartlink.ti.callback.SmartConfigListener;
import com.het.udp.core.smartlink.ti.utils.NetworkUtil;

/**
 * Created by UUXIA on 2015/6/25.
 */
public class TiManager {
    private String ssid;
    private String passKey;
    private String deviceName;
    private String devicePass;
    private Context mContext;

    private SmartConfig smartConfig;
    private SmartConfigListener smartConfigListener;

    public TiManager(Context mContext) {
        this.mContext = mContext;
        ssid = NetworkUtil.getWifiName(mContext);
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public void startSmartConfig() {
        byte[] freeData;
        byte[] paddedEncryptionKey;
        String gateway = NetworkUtil.getGateway(mContext);
        if (devicePass != null && devicePass.length() > 0) {
            paddedEncryptionKey = (devicePass.toString() + SmartConfigConstants.ZERO_PADDING_16).substring(0, 16).trim().getBytes();
        } else {
            paddedEncryptionKey = null;
        }
        if (deviceName != null && deviceName.length() > 0) { // device name isn't empty
            byte[] freeDataChars = new byte[deviceName.length() + 2];
            freeDataChars[0] = 0x03;
            freeDataChars[1] = (byte) deviceName.length();
            for (int i = 0; i < deviceName.length(); i++) {
                freeDataChars[i + 2] = (byte) deviceName.charAt(i);
            }
            freeData = freeDataChars;
        } else {
            freeData = new byte[1];
            freeData[0] = 0x03;
        }
        smartConfig = null;
        smartConfigListener = new SmartConfigListener() {
            @Override
            public void onSmartConfigEvent(SmtCfgEvent event, Exception e) {

            }
        };
        try {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"网关:" + gateway + " ssid:" + ssid + " 密码:" + passKey);
            smartConfig = new SmartConfig(smartConfigListener, freeData, passKey, paddedEncryptionKey, gateway, ssid, (byte) 0, "");
            smartConfig.transmitSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSmartConfig() {
        try {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"关闭Ti扫描:" + " ssid:" + ssid + " 密码:" + passKey);
            smartConfig.stopTransmitting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
