package pi.com.pi.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;


import com.fsix.mqtt.util.Logc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pi.com.pi.bean.WiFiBean;
import pi.com.pi.util.Base64;
import pi.com.pi.util.SpUtil;
import pi.com.pi.wifi.callback.WiFiConnCallback;

public class WiFiTool {
    Context context;

    // Wifi管理类
    private WifiUtils mWifiUtils;

    private Thread threadCheckDone = null,scanThread;
    // 扫描结果列表
    private List<ScanResult> list = new ArrayList<ScanResult>();

    final String KEY = "router";

    private WiFiBean ssidBean;

    public WiFiTool(Context context) {
        mWifiUtils = new WifiUtils(context);
        this.context = context;
        getDefalt();
    }


    public WiFiBean getDefalt(){
        String spStr = SpUtil.getString(context, KEY);
        Object obj = Base64.strBase64Obj(spStr);
        if (obj instanceof WiFiBean){
            ssidBean = (WiFiBean) obj;
        }
        return ssidBean;
    }

    public void connDefalt(WiFiConnCallback wiFiConnCallback){
        getDefalt();
        if (ssidBean!=null) {
            conn(ssidBean.getSsid(),ssidBean.getPassword(),wiFiConnCallback);
        }else{
            wiFiConnCallback.onFailed("ssidBean is null");
        }
    }

    public boolean isConnectedWiFi() {
        String ssid = getSsid();
        System.out.println("============== " + ssid);
        if (TextUtils.isEmpty(ssid))
            return false;
        if (ssid.contains("unknown"))
            return false;
        return true;
    }

    public String getSsid() {
        return mWifiUtils.getSSid(context);
    }

    public void conn(final String ssid, final String password, final WiFiConnCallback wiFiConnCallback) {
        String nSsid = getSsid();
        if (!TextUtils.isEmpty(nSsid)){
            if (nSsid.equals(ssid)) {
                wiFiConnCallback.onWiFiConnected(ssid, password);
                return;
            }
        }
        if (mWifiUtils!=null){
            mWifiUtils.WifiOpen();
        }
        if (threadCheckDone != null) {
            threadCheckDone.interrupt();
            threadCheckDone = null;
        }
        if (threadCheckDone == null) {
            threadCheckDone = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int i = 0, connCout = 0;
                        int wifiState = -1;
                        boolean isconnected = connectWiFi(ssid, password);
                        while (threadCheckDone != null) {
                            String ip = mWifiUtils.getIP(context);
                            boolean internet = connBaiduTest();
                            if (!TextUtils.isEmpty(ip)) {
                                if (wiFiConnCallback != null) {
                                    if (wifiState == -1) {
                                        wifiState = wiFiConnCallback.onWiFiConnected(ssid, password);
                                        if (wifiState == 1) {
                                            ssidBean = new WiFiBean(ssid, password);
                                            SpUtil.putString(context, KEY, Base64.objBase64Str(ssidBean));
                                        }
                                    }
                                    if (internet) {
                                        Logc.e("uu==========互联网畅通====");
                                        if (threadCheckDone != null) {
                                            threadCheckDone.interrupt();
                                            threadCheckDone = null;
                                        }
                                        wiFiConnCallback.onInternetConnected(ip);
                                        break;
                                    }
                                }
                            } else {
                                connCout++;
                                Logc.i("uu=======互联网已断开====connCout:" + connCout);
                            }

                            i++;
                            if (i >= 10) {
                                i = 0;
                                connectWiFi(ssid, password);
                            }
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            threadCheckDone.setName("notifyReturnNetDone");
            threadCheckDone.start();
        }
    }


    String TAG = "uuu";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            NetworkInfo info;
            // 当扫描完成，会发出下面的通知
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.d(TAG, "接收到" + WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                List<ScanResult> mScanResults = mWifiUtils.getScanResults();//manager.getScanResults();
                Log.d(TAG, "mScanResults.size()===" + mScanResults.size());
                List<String> dataList = new ArrayList<>();
                for (int i = 0; i < mScanResults.size(); i++) {
                    dataList.add(mScanResults.get(i).SSID);
                    Log.d(TAG, "ssid : " + mScanResults.get(i).SSID);
                    Log.d(TAG, "id : " + mScanResults.get(i).BSSID);
                }

            }
        }
    };

    public void scan(final IScanWuliWiFi scanWuliWiFi) {
        if (scanThread != null) {
            scanThread.interrupt();
            scanThread = null;
        }
        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mWifiUtils.WifiOpen();//开启WiFI
                mWifiUtils.WifiStartScan();//扫描附近WiFi
                list = mWifiUtils.getScanResults();
                mWifiUtils.getConfiguration();
                scanWuliWiFi.onResult(list);
            }
        });
        scanThread.setName("scanThread-");
        scanThread.start();
    }



    private boolean connectWiFi(final String ssid, final String password) {
        String current = WifiUtils.getSSid(context);
        Logc.e("uu== ====connectWiFi 当前WIFI信息:" + current + " 目标热点:" + ssid + " PASS:" + password);
        if (ssid != null && current != null && current.equalsIgnoreCase(ssid))
            return true;
        /**连接有密码WIFI**/
        if (!TextUtils.isEmpty(password)) {
            int netId = mWifiUtils.AddWifiConfig(ssid, password);
            if (netId != -1) {
                mWifiUtils.getConfiguration();
                //添加了配置信息，要重新得到配置信息
                if (mWifiUtils.ConnectWifi(netId)) {
                    Logc.i("uu====connectWiFi正在连接有密码WIFI:" + ssid + " " + password);
                }
            } else {
                Logc.i("uu====connectWiFi有密码WIFI连接失败:" + ssid + " " + password);
                return false;
            }
        } else {
            mWifiUtils.getConfiguration();
            int wifiItemId = mWifiUtils.IsConfiguration("\"" + ssid + "\"");
            Logc.i( "uu== ====connectWiFi wifiItemId:" + wifiItemId);
            if (wifiItemId != -1) {
                mWifiUtils.addNetwork(ssid, password);
                if (mWifiUtils.ConnectWifi(wifiItemId)) {
                    //连接指定WIFI
                    Logc.i( "uu=====connectWiFi 正在连接指定WIFI:" + ssid + " wifiItemId:" + wifiItemId);
                } else {
                    Logc.i( "uu=====connectWiFi 正在连接指定WIFI失败...:" + ssid + " wifiItemId:" + wifiItemId);
                }
            } else if (mWifiUtils.isConnectNoPass(ssid)) {
                Logc.i( "uu=====connectWiFi 经判断,已经连接WIFI:" + ssid);
                return true;
            } else {
                Logc.i( "uu=====connectWiFi 正在连接的指定WIFI失败:" + ssid + " wifiItemId:" + wifiItemId);
                mWifiUtils.removeNetWork(mWifiUtils.getWifiConfiguration1("\"" + ssid + "\""));
                mWifiUtils.addNetwork(ssid, password);
            }
            return false;
        }

        return true;
    }

    /**
     * 判断是否能够连接上互联网
     */
    public static boolean connBaiduTest() {
        // 个人觉得使用MIUI这个链接有失效的风险
        final String checkUrl = "https://www.baidu.com";
        final int SOCKET_TIMEOUT_MS = 1000;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(checkUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(SOCKET_TIMEOUT_MS);
            connection.setReadTimeout(SOCKET_TIMEOUT_MS);
            connection.setUseCaches(false);
            connection.connect();

            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public interface IScanWuliWiFi{
        void onResult(List<ScanResult> list);
    }
}
