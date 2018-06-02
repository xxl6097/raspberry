package com.tech.tcp.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static String toHexStrings(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        if (b != null) {
            for (int i = 0; i < b.length; ++i) {
                String s = Integer.toHexString(b[i] & 255);
                if (s.length() == 1) {
                    s = "0" + s;
                }

                buffer.append(s + " ");
            }
        }

        return buffer.toString();
    }


    /**
     * automode(1)+ssid.len(1)+pass.len(1)+ssid+pass
     *
     * @return
     */
    public static byte[] package8100Data(String ssid, String password, int automode) {
        int ssidLen = ssid.getBytes().length;
        int passLen = 0;
        if (password != null) {
            passLen = password.length();
        }
        int bodyLen = ssidLen + passLen + 1 + 1 + 1;
        byte[] body = new byte[bodyLen];
        body[0] = (byte) automode;
        body[1] = (byte) ssidLen;
        body[2] = (byte) passLen;
        System.arraycopy(ssid.getBytes(), 0, body, 1 + 1 + 1, ssidLen);
        if (password != null) {
            System.arraycopy(password.getBytes(), 0, body, ssidLen + 1 + 1 + 1, passLen);
        }
        return body;
    }

    /**
     * 0---不加密（open)
     * 1---WEP
     * 2---WPA
     * 3---WPA2
     * 4---WPA/WPA2
     */
    public static int getSecurity(String cap) {
        if (cap != null) {
            if (cap.toUpperCase().contains("WEP")) {
                return 1;
            } else if (cap.toUpperCase().contains("WPA")) {
                //Security.add("WPA-PSK");
                if (cap.toUpperCase().contains("WPA2"))
                    return 4;
                return 2;
            } else if (cap.toUpperCase().contains("WPA2")) {
                //Security.add("WPA-PSK");
                return 3;
            } else {
                return 0;

            }
        }
        return 0;
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
}
