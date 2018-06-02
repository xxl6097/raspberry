package com.het.udp.core.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.RemoteException;

import com.het.log.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.DeviceBindMap;

/**
 * Created by uuxia on 2014-11-27.
 */
public class NetWorkBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,intent.getAction());
        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            try {
                UdpDataManager.getInstance().resetClient();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
//                Logc.i(Logc.HetReportTag.INFO_WIFI,"NetWorkBroadcast.udp. wifi disconnected... ");
                DeviceBindMap.bindDeviceMap.clear();
                DeviceBindMap.runJudgeBindStatus.clear();
            }
            if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"NetWorkBroadcast.udp. wifi connected...");
                sendHFA11();
            }
        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            // WIFI开关
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {// 如果关闭
//                Logc.i(Logc.HetReportTag.INFO_WIFI,"NetWorkBroadcast.udp. wifi closed...");
                DeviceBindMap.bindDeviceMap.clear();
                DeviceBindMap.runJudgeBindStatus.clear();
            }

            if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
//                Logc.i(Logc.HetReportTag.INFO_WIFI,"NetWorkBroadcast.udp. wifi opened...");
                sendHFA11();
            }
        }
    }  //如果无网络连接activeInfo为null

    private void sendHFA11() {
        try {
            UdpDataManager.getInstance().startScan(null);
        } catch (Exception e) {
            e.printStackTrace();
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"NetWorkBroadcast scan lan is err:" + e.getMessage());
        }
    }
}
