package pi.com.pi.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

public class WiFiConnnect {
    private static String TAG = WiFiConnnect.class.getName();

    private WifiManager wifiManager;
    private WifiConnectListenner listenner;
    private Thread connThread = null;

    public interface WifiConnectListenner {
        void connectFail();

        void connectSuc();
    }

    public void setListenner(WifiConnectListenner listener) {
        this.listenner = listener;
    }

    // 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    // 构造函数
    public WiFiConnnect(Context context) {
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    // 提供一个外部接口，传入要连接的无线网
    public void connect(String ssid, String password, WifiCipherType type) {
        if (connThread != null){
            connThread.interrupt();
            connThread = null;
        }
        connThread = new Thread(new ConnectRunnable(ssid, password, type));
        connThread.start();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {//modify 20161219加上屏蔽的两行会导致返回netId为-1
//            config.wepKeys[0] = "";
            config.hiddenSSID = true;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;

            System.out.println("==uu== 无密配置");
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }

    // 打开wifi功能
    private boolean openWifi() {
        boolean bRet = true;
        if (!wifiManager.isWifiEnabled()) {
            bRet = wifiManager.setWifiEnabled(true);
            System.out.println("openWifi  bRet: " + bRet);
        }
        return bRet;
    }

    class ConnectRunnable implements Runnable {
        private String ssid;

        private String password;

        private WifiCipherType type;

        public ConnectRunnable(String ssid, String password, WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;

            System.out.println("==uu== ssid: " + ssid + " password: " + password + " type: " + type);
        }

        @Override
        public void run() {
            try {
                processConn(ssid, password, type);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processConn(String ssid, String password, WifiCipherType type) throws InterruptedException {
        // 打开wifi
        openWifi();
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒检测……
                Thread.sleep(100);
                System.out.println("==uu==wifi已经打开");
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
        System.out.println("==uu==wifiConfig： " + wifiConfig);

        if (wifiConfig == null) {
            return;
        }
        WifiConfiguration tempConfig = isExsits(ssid);

        if (tempConfig != null) {
            System.out.println("==uu==配置过ssid");
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        int netID = wifiManager.addNetwork(wifiConfig);

        System.out.println("==uu==netID： " + netID);

            /*
             * DisableOthers 为true的时候就表示在连接networkId的时候，拒绝连接其他的wifiap，
             * 也就是说只连接当前的wifiap，如果连不上也不会去连接其他的wifiap
			 * 而为false的时候就表示连接当前wifiap，如果连不上，就去连接其他的wifiap
             * */

        if (-1 != netID) {// 表示可以分配新的ID成功
            Method connectMethod = connectWifiByReflectMethod(netID);  //解决部分手机不能连接热点的情况不能单纯的使用enableNetwork方法
            if (connectMethod == null) {
                System.out.println("==uu==connect wifi by enableNetwork method");
                // 通用API
                boolean b = wifiManager.enableNetwork(netID, true);

//                if (b) {// 链接成功
//                    if (listenner != null) {
//                        listenner.connectSuc();
//                    }
//                    System.out.println("==uu==成功连接热点");
//                } else {
//                    if (listenner != null) {
//                        listenner.connectFail();
//                    }
//                }
//            } else {
//                if (listenner != null) {
//                    listenner.connectSuc();
//                }
            }

        } else {
            if (listenner != null) {
                listenner.connectFail();
            }
        }

        boolean conned = checkConnState(ssid);
        if (conned) {
            if (listenner != null) {
                listenner.connectSuc();
            }
        }
    }

    private boolean checkConnState(String target) throws InterruptedException {
        while (true) {
            String ssid = getSsid();
            if (TextUtils.isEmpty(ssid))
                continue;
            if (target.equals(ssid)) {
                return true;
            }
            Thread.sleep(1000);
        }
    }

    /**
     * 通过反射出不同版本的connect方法来连接Wifi
     *
     * @param netId
     * @return
     * @author jiangping.li
     * @since MT 1.0
     */
    private Method connectWifiByReflectMethod(int netId) {
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            System.out.println("==uu==connectWifiByReflectMethod road 1");
            // 反射方法： connect(int, listener) , 4.2 <= phone‘s android version
            for (Method methodSub : wifiManager.getClass().getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(wifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("==uu== connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");

                    return null;
                }
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            // 反射方法: connect(Channel c, int networkId, ActionListener listener)
            // 暂时不处理4.1的情况 , 4.1 == phone‘s android version
            System.out.println("==uu== connectWifiByReflectMethod road 2");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            System.out.println("==uu== connectWifiByReflectMethod road 3");
            // 反射方法：connectNetwork(int networkId) ,
            // 4.0 <= phone‘s android version < 4.1
            for (Method methodSub : wifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connectNetwork".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(wifiManager, netId);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("connectWifiByReflectMethod Android "
                            + Build.VERSION.SDK_INT + " error!");
                    return null;
                }
            }
        } else {
            // < android 4.0
            return null;
        }
        return connectMethod;
    }

    public String getSsid() {
        try {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {  //wifi已经打开
                WifiInfo info = wifiManager.getConnectionInfo();
                String wifiId = info != null ? info.getSSID() : null;

                return wifiId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
