package com.het.udp.wifi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SecurityUtils {

    /**
     * 消息摘要.
     */
    private static MessageDigest sDigest;

    static {
        try {
            SecurityUtils.sDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"获取MD5信息摘要失败" + e);
        }
    }

    /**
     * 私有的构造方法.
     */
    private SecurityUtils() {
    }

    /**
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) {
        if (SecurityUtils.sDigest == null) {
            //Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"MD5信息摘要初始化失败");
            return null;
        } else if (ByteUtils.isNull(inStr)) {
            //Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"参数strSource不能为空");
            return null;
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = SecurityUtils.sDigest.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
}
