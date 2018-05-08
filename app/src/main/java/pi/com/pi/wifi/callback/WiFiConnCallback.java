package pi.com.pi.wifi.callback;

public interface WiFiConnCallback {
    //0:不用再回调，1：存储ssid和密码
    int onWiFiConnected(String ssid, String password);
    boolean onInternetConnected(String ip);
    void onFailed(String msg);
}
