package pi.com.pi.bean;

import java.io.Serializable;

public class WiFiBean implements Serializable {
    private String ssid;
    private String password;

    public WiFiBean() {
    }

    public WiFiBean(String ssid, String password) {
        this.ssid = ssid;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "WiFiBean{" +
                "ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
