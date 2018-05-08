package pi.com.pi.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WifiUtils {
    private WifiManager localWifiManager;//提供Wifi管理的各种主要API，主要包含wifi的扫描、建立连接、配置信息等
    //private List<ScanResult> wifiScanList;//ScanResult用来描述已经检测出的接入点，包括接入的地址、名称、身份认证、频率、信号强度等
    private List<WifiConfiguration> wifiConfigList;//WIFIConfiguration描述WIFI的链接信息，包括SSID、SSID隐藏、password等的设置
    private WifiInfo wifiConnectedInfo;//已经建立好网络链接的信息
    private WifiLock wifiLock;//手机锁屏后，阻止WIFI也进入睡眠状态及WIFI的关闭

    // Wifi管理类
    private WifiAdmin mWifiAdmin;

    public WifiUtils(Context context) {
        localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdmin(context);
    }

    //检查WIFI状态
    public int WifiCheckState() {
        return localWifiManager.getWifiState();
    }

    //开启WIFI
    public void WifiOpen() {
        if (!localWifiManager.isWifiEnabled()) {
            localWifiManager.setWifiEnabled(true);
        }
    }

    //关闭WIFI
    public void WifiClose() {
        if (!localWifiManager.isWifiEnabled()) {
            localWifiManager.setWifiEnabled(false);
        }
    }

    public boolean isConnectNoPass(String ssid) {
        boolean iswifi = mWifiAdmin.connectSpecificAP(ssid);
        return iswifi;
    }

    public boolean isConnectNoPass(ScanResult scanResult) {
        boolean iswifi = mWifiAdmin.connectSpecificAP(scanResult);
        return iswifi;
    }


    //扫描wifi
    public void WifiStartScan() {
        localWifiManager.startScan();
//		mWifiAdmin.startScan();
    }


    //得到Scan结果
    public List<ScanResult> getScanResults() {
//		return mWifiAdmin.getWifiList();
        return localWifiManager.getScanResults();//得到扫描结果
    }

    //Scan结果转为Sting
    public List<String> scanResultToString(List<ScanResult> list) {
        List<String> strReturnList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            ScanResult strScan = list.get(i);
            String str = strScan.toString();
            boolean bool = strReturnList.add(str);
            if (!bool) {
                Log.i("scanResultToSting", "Addfail");
            }
        }
        return strReturnList;
    }


    /**
     * 取消保存
     */
    public void removeNetWork(WifiConfiguration wifiConfiguration) {
        if (localWifiManager != null && wifiConfiguration != null) {
            //移除网络
            localWifiManager.removeNetwork(wifiConfiguration.networkId);
            //重新保存配置
            localWifiManager.saveConfiguration();
            localWifiManager.startScan();//重新扫描
        }
    }

    public void removeNetWork(int netId) {
        if (localWifiManager != null && netId != -1) {
            //移除网络
            localWifiManager.removeNetwork(netId);
            //重新保存配置
            localWifiManager.saveConfiguration();
            localWifiManager.startScan();//重新扫描
        }
    }

    /**
     * 根据SSid找出已配置的消息
     *
     * @param SSID
     * @return
     */
    public WifiConfiguration getWifiConfiguration1(String SSID) {
        WifiConfiguration wifiConfiguration = null;
        if (localWifiManager != null) {
            for (WifiConfiguration wcg : localWifiManager.getConfiguredNetworks()) {
                if (wcg.SSID.equals(SSID)) {
                    wifiConfiguration = wcg;
                    break;
                }
            }
        }
        return wifiConfiguration;
    }

    public WifiConfiguration getWifiConfiguration(String SSID) {
        WifiConfiguration result = null;
        if (localWifiManager != null) {
            for (WifiConfiguration s : localWifiManager.getConfiguredNetworks()) {
                if (s.SSID.equals(SSID.replace("\"", ""))) {
                    result = s;
                    break;
                }
            }
        }
        return result;
    }

    public WifiConfiguration getCurrentConfiguration(String ssid) {
        for (int i = 0; i < wifiConfigList.size(); i++) {
            String srcSsid = wifiConfigList.get(i).SSID.replaceAll("\"", "");
            if (!TextUtils.isEmpty(srcSsid) && srcSsid.equalsIgnoreCase(ssid)) {//地址相同
                return wifiConfigList.get(i);
            }
        }
        return null;
    }

    //得到Wifi配置好的信息
    public void getConfiguration() {
        wifiConfigList = localWifiManager.getConfiguredNetworks();//得到配置好的网络信息
//		for(int i =0;i<wifiConfigList.size();i++){
//			Log.i("getConfiguration",wifiConfigList.get(i).SSID);
//			Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
//		}
    }

//    WifiConfiguration wcg = getWifiConfiguration(mWifiManager.getConnectionInfo().getSSID());

    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
    public int IsConfiguration(String SSID) {
        if (wifiConfigList == null) {
            Log.e("IsConfiguration", "uu== ==wifiConfigList is null");
            return -1;
        }
        for (int i = 0; i < wifiConfigList.size(); i++) {
            Log.i(wifiConfigList.get(i).SSID, String.valueOf(wifiConfigList.get(i).networkId));
            if (wifiConfigList.get(i).SSID.equals(SSID)) {//地址相同
                return wifiConfigList.get(i).networkId;
            }
        }
        Log.e("IsConfiguration", "uu== ==networkId not found from wifiConfigList :" + String.valueOf(wifiConfigList.size()));
        return -1;
    }

    //添加指定WIFI的配置信息,原列表不存在此SSID
    public int AddWifiConfig(String ssid, String pwd) {
        int wifiId = -1;
        Log.i("AddWifiConfig", "equals");
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\"" + ssid + "\"";//\"转义字符，代表"
        wifiCong.preSharedKey = "\"" + pwd + "\"";//WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        wifiId = localWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
        System.out.println("uu== ==============返回wifiId:" + wifiId);
//				wifiConfigList.add(wifiId,wifiCong);
//				wifiConfigList.add(wifiCong);
        if (wifiId != -1) {
            return wifiId;
        }
        return wifiId;
    }

    //添加指定WIFI的配置信息,原列表不存在此SSID
    public int AddWifiConfig(List<ScanResult> wifiList, String ssid, String pwd) {
        int wifiId = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult wifi = wifiList.get(i);
            if (wifi.SSID.equals(ssid)) {
                Log.i("AddWifiConfig", "equals");
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\"" + wifi.SSID + "\"";//\"转义字符，代表"
                wifiCong.preSharedKey = "\"" + pwd + "\"";//WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = localWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                System.out.println("uu== ==============返回wifiId:" + wifiId);
//				wifiConfigList.add(wifiId,wifiCong);
//				wifiConfigList.add(wifiCong);
                if (wifiId != -1) {
                    return wifiId;
                }
            }
        }
        return wifiId;
    }

    //连接指定Id的WIFI
    public boolean ConnectWifi(int wifiId) {
        for (int i = 0; i < wifiConfigList.size(); i++) {
            WifiConfiguration wifi = wifiConfigList.get(i);
            if (wifi.networkId == wifiId) {
//                while (!(localWifiManager.enableNetwork(wifiId, true))) {//激活该Id，建立连接
//                    Logc.i("uu ConnectWifi " + String.valueOf(wifiConfigList.get(wifiId).status) + "uu@@@ status:0--已经连接，1--不可连接，2--可以连接");//status:0--已经连接，1--不可连接，2--可以连接
//                }

                // 断开连接
//                localWifiManager.disconnect();
                // 重新连接
                // netID = wifiConfig.networkId;
                // 设置为true,使其他的连接断开
                boolean bRet = localWifiManager.enableNetwork(wifiId, true);
//                localWifiManager.reconnect();
                if (!bRet) {
                    removeNetWork(wifiId);
                }
                return bRet;
            }
        }
        return false;
    }


    //创建一个WIFILock
    public void createWifiLock(String lockName) {
        wifiLock = localWifiManager.createWifiLock(lockName);
    }

    //锁定wifilock
    public void acquireWifiLock() {
        wifiLock.acquire();
    }

    //解锁WIFI
    public void releaseWifiLock() {
        if (wifiLock.isHeld()) {//判定是否锁定
            wifiLock.release();
        }
    }

    //得到建立连接的信息
    public void getConnectedInfo() {
        wifiConnectedInfo = localWifiManager.getConnectionInfo();
    }

    //得到连接的MAC地址
    public String getConnectedMacAddr() {
        return (wifiConnectedInfo == null) ? "NULL" : wifiConnectedInfo.getMacAddress();
    }

    //得到连接的名称SSID
    public String getConnectedSSID() {
        return (wifiConnectedInfo == null) ? "NULL" : wifiConnectedInfo.getSSID();
    }

    //得到连接的IP地址
    public int getConnectedIPAddr() {
        return (wifiConnectedInfo == null) ? 0 : wifiConnectedInfo.getIpAddress();
    }

    //得到连接的ID
    public int getConnectedID() {
        return (wifiConnectedInfo == null) ? 0 : wifiConnectedInfo.getNetworkId();
    }


    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    static int getSecurity(WifiConfiguration config) {
        if (config == null)
            return -1;
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    //连接指定Id的WIFI
    public boolean addNetwork(String ssid, String password) {
        WifiConfiguration wifi = createWifiInfo(ssid, password);
        if (wifi != null) {
            int netID = localWifiManager.addNetwork(wifi);
            wifiConfigList.add(wifi);
            //重新保存配置
            localWifiManager.saveConfiguration();
        }

        return false;
    }

    /**
     * 断开指定ID的网络
     *
     * @param netId wifi的id
     */
    public void disconnect(int netId) {
        // 断开连接
        if (localWifiManager != null) {
            localWifiManager.disableNetwork(netId);
            localWifiManager.disconnect();
        }
    }

    WifiConnect.WifiCipherType getSecurity(ScanResult scanresult) {
        if (scanresult.capabilities.contains("WPA")) {
            return WifiConnect.WifiCipherType.WIFICIPHER_WPA;
        } else if (scanresult.capabilities.contains("WEP")) {
            return WifiConnect.WifiCipherType.WIFICIPHER_WEP;
        } else {
            return WifiConnect.WifiCipherType.WIFICIPHER_NOPASS;
        }
    }

    public boolean connectWiFi(ScanResult scanresult, String ssid, String password) {
        /*WifiConfiguration wifiConfig = createWifiInfo(ssid, password);
        if (wifiConfig == null) {
            Log.v("uu", "uu wifiConfig == null");
            return false;
        }
        if (localWifiManager == null) {
            Log.v("uu", "uu localWifiManager == null");
            return false;
        }
        int netID = localWifiManager.addNetwork(wifiConfig);
        Log.v("uu", "uu netID = " + netID);//连不加密AP总是返回-1
        while (!(localWifiManager.enableNetwork(netID, true))) {//激活该Id，建立连接
            Log.i("uu ConnectWifi ", String.valueOf(wifiConfigList.get(netID).status) + " status:0--已经连接，1--不可连接，2--可以连接");//status:0--已经连接，1--不可连接，2--可以连接
        }
        return true;*/

        return mWifiAdmin.connect(ssid, password, getSecurity(scanresult));
    }

    /***
     * 配置要连接的WIFI热点信息
     *
     * @param SSID
     * @param password
     * @return
     */
    public WifiConfiguration createWifiInfo(String SSID, String password) {
        WifiConfiguration tempConfig = getWifiConfiguration1("\"" + SSID + "\"");
        int type = getSecurity(tempConfig);

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        //增加热点时候 如果已经存在SSID 则将SSID先删除以防止重复SSID出现
        if (tempConfig != null && localWifiManager != null) {
            localWifiManager.removeNetwork(tempConfig.networkId);
        }

        // 分为三种情况：没有密码   用wep加密  用wpa加密
        if (type == SECURITY_NONE || TextUtils.isEmpty(password)) {   // WIFICIPHER_NOPASS
//            config.hiddenSSID = true;
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        } else if (type == SECURITY_WEP) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        } else if (type == SECURITY_PSK) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";//WPA-PSK密码
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    public static String getSSid(Context ctx) {
        WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        // 用来获取当前已连接上的wifi的信息
        if (mWifiManager == null) {
            return "";
        }
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null) {
            return "";
        }
        if (mWifiInfo.getSSID() == null) {
            return "";
        }

        String ssid = mWifiInfo.getSSID();
        // int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        // 16之后的版本 取ssid时会自动带“”. 汉方模块要去掉
        if (android.os.Build.VERSION.SDK_INT > 16) {
            if (!TextUtils.isEmpty(ssid)) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }
        return ssid;
    }


    public static String getIP(Context context) {
        String ip = getLocalIP(context);
        if (TextUtils.isEmpty(ip))
            return null;
        String[] ips = ip.split("\\.");
        String nIp = ips[0] + "." + ips[1] + "." + ips[2] + ".1";
        if (nIp.equalsIgnoreCase("0.0.0.1"))
            return null;
        return ip;
    }


    public static String getLocalIP(Context ctx) {
        WifiManager wm = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        long ip = (long)di.ipAddress;
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int)(ip & 255L)));
        sb.append('.');
        sb.append(String.valueOf((int)(ip >> 8 & 255L)));
        sb.append('.');
        sb.append(String.valueOf((int)(ip >> 16 & 255L)));
        sb.append('.');
        sb.append(String.valueOf((int)(ip >> 24 & 255L)));
        String ipStr = sb.toString();
        if(TextUtils.isEmpty(ipStr) || ipStr.equalsIgnoreCase("0.0.0.0")) {
            String ipStr1 = getLocalIpAddress();
            if(!TextUtils.isEmpty(ipStr1)) {
                return ipStr1;
            }
        }

        return sb.toString();
    }

    public static String getLocalIpAddress() {
        String hostIp = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;

            while(true) {
                while(e.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface)e.nextElement();
                    Enumeration ias = ni.getInetAddresses();

                    while(ias.hasMoreElements()) {
                        ia = (InetAddress)ias.nextElement();
                        if(!(ia instanceof Inet6Address)) {
                            String ip = ia.getHostAddress();
                            if(!"127.0.0.1".equals(ip)) {
                                hostIp = ia.getHostAddress();
                                break;
                            }
                        }
                    }
                }

                return hostIp;
            }
        } catch (SocketException var6) {
            var6.printStackTrace();
            return hostIp;
        }
    }
}
