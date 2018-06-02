package com.het.xml.protocol.coder.utils;


/**
 * @author Alone
 */
public class EncryptUtils {
    /**
     * 加密
     *
     * @param content
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] content, byte[] key) {
//		System.out.println("加密前的数据：" + StringUtil.byteArrayToHexString(content));
        int j = 0;
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (content[i] ^ key[j]);
            j++;
            if (j > 15) {
                j = 0;
            }
        }
//		System.out.println("加密后的数据：" + StringUtil.byteArrayToHexString(content));
        return content;
    }

    /**
     * 解密
     *
     * @param encryptContent
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] key) {
//		System.out.println("解密前的数据：" + StringUtil.byteArrayToHexString(content));
        int j = 0;
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (content[i] ^ key[j]);
            j++;
            if (j > 15) {
                j = 0;
            }
        }
//		System.out.println("解密key:"+key.toString());
//		System.out.println("解密后的数据：" + StringUtil.byteArrayToHexString(content));
        return content;
    }

    /**
     * 秘钥(16 bytes)产生
     *
     * @param macAddress
     * @param deviceType
     * @param deviceSubType
     * @return
     */
    public static byte[] getKey(String macAddress, Integer deviceType, Integer deviceSubType) {
        deviceSubType = deviceSubType % 1000;
        byte[] key = new byte[16];
        byte[] mac = StringUtil.hexStringToByteArray(macAddress);
        key[0] = deviceType.byteValue();
        key[1] = mac[0];
        key[2] = deviceSubType.byteValue();
        key[3] = mac[5];
        key[4] = mac[3];
        key[5] = mac[2];
        key[6] = mac[4];
        key[7] = mac[1];
        key[8] = (byte) (deviceSubType.byteValue() ^ mac[0]);
        key[9] = (byte) (deviceType.byteValue() ^ mac[5]);
        key[10] = (byte) (mac[4] ^ mac[1]);
        key[11] = (byte) (mac[2] ^ mac[3]);
        key[12] = (byte) (mac[5] ^ mac[0]);
        key[13] = (byte) (mac[2] ^ mac[5]);
        key[14] = (byte) (mac[0] ^ mac[4]);
        key[15] = (byte) (mac[3] ^ mac[1]);
        return key;
    }

    public static void main(String[] args) {
        //accf233c3a0c
        byte[] key = EncryptUtils.getKey("accf233c39fa", 7, 2);
//        System.out.println(StringUtil.byteArrayToHexString(key));
//		byte[] info = StringUtil.hexStringToByteArray("07ac02fb3c2339cfaefdf61f56d995f307ac02fa3c231361");
        // byte[] secretInfo = Encrypt1.encrypt(info, key);
        byte[] secretInfo = StringUtil.hexStringToByteArray("16ad304e1b0d8be19d461508cc4317e4165300602a62bbcfad607508cdbce81be9e4443406128fff9c4f3505c64317e4");
        byte[] decryptInfo = EncryptUtils.decrypt(secretInfo, key);
//        System.out.println("解密后的数据：" + StringUtil.byteArrayToHexString(decryptInfo));
    }
}
