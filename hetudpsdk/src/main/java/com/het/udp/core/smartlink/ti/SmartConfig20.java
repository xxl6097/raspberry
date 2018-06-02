package com.het.udp.core.smartlink.ti;

import java.util.ArrayList;

/**
 * Created by UUXIA on 2015/6/25.
 */
public class SmartConfig20 {
    private ArrayList<Integer> mData;
    private byte[] mfreeData;
    private String mSsid;
    private String mToken;
    private byte[] mKey;
    private boolean hasEncryption;

    public SmartConfig20() {
    }

    public void encodePackets() throws Exception {
        this.mData = new ArrayList();
        boolean T_START = true;
        boolean T_MID_NO_ENCRYPTION = true;
        boolean T_MID_ENCRYPTION = true;
        boolean T_FREE = true;
        this.mData = new ArrayList();
        this.mData.add(Integer.valueOf(1099));
        this.constructSsid();
        if (this.hasEncryption) {
            this.mData.add(Integer.valueOf(1200));
        } else {
            this.mData.add(Integer.valueOf(1199));
        }

        this.constructKey();
        if (this.mfreeData.length > 1) {
            this.mData.add(Integer.valueOf(1149));
            this.constructFreeData();
        }

    }

    private void constructSsid() throws Exception {
        boolean ConstOffset_1 = true;
        boolean ConstOffset_2 = true;
        int ssidLength = this.mSsid.length();
        int ssidL = ssidLength + 1 + 27;
        this.mData.add(Integer.valueOf(ssidL));
        this.encodeSsidString(this.mSsid);
    }

    private void encodeSsidString(String ssid) throws Exception {
        boolean DataOffset = true;
        byte prevNibble = 0;
        int currentIndex = 0;
        byte[] stringBuffer = new byte[ssid.length()];
        stringBuffer = this.convertStringToBytes(ssid);

        for (int i = 0; i < ssid.length(); ++i) {
            byte currentChar = stringBuffer[i];
            int lowNibble = currentChar & 15;
            int highNibble = currentChar >> 4;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
            prevNibble = (byte) highNibble;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
            prevNibble = (byte) lowNibble;
            currentIndex &= 15;
        }

    }

    private void constructKey() throws Exception {
        boolean ConstOffset_1 = true;
        boolean ConstOffset_2 = true;
        int keyLength = this.mKey.length;
        int keyL = keyLength + 1 + 27;
        this.mData.add(Integer.valueOf(keyL));
        this.encodeKeyString(this.mKey);
    }

    private void encodeKeyString(byte[] key) throws Exception {
        boolean DataOffset = true;
        byte prevNibble = 0;
        int currentIndex = 0;

        for (int i = 0; i < key.length; ++i) {
            int currentChar = this.intToUint8(key[i]);
            int lowNibble = currentChar & 15;
            int highNibble = currentChar >> 4;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
            prevNibble = (byte) highNibble;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
            prevNibble = (byte) lowNibble;
            currentIndex &= 15;
        }

    }

    private void constructFreeData() throws Exception {
        boolean ConstOffset_1 = true;
        boolean ConstOffset_2 = true;
        int freeDataLength = this.mfreeData.length;
        int freeDataL = freeDataLength + 1 + 27;
        this.mData.add(Integer.valueOf(freeDataL));
        this.encodeFreeData(this.mfreeData);
    }

    private void encodeFreeData(byte[] freeData) throws Exception {
        boolean DataOffset = true;
        byte prevNibble = 0;
        int currentIndex = 0;

        for (int i = 0; i < freeData.length; ++i) {
            int currentChar = this.intToUint8(freeData[i]);
            int lowNibble = currentChar & 15;
            int highNibble = currentChar >> 4;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
            prevNibble = (byte) highNibble;
            this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
            prevNibble = (byte) lowNibble;
            currentIndex &= 15;
        }

    }

    private byte[] convertStringToBytes(String string) throws Exception {
        return string.getBytes();
    }

    private int intToUint8(int number) throws Exception {
        return number & 255;
    }

    public ArrayList<Integer> getmData() throws Exception {
        return this.mData;
    }

    public String getmSsid() throws Exception {
        return this.mSsid;
    }

    public void setmSsid(String mSsid) throws Exception {
        this.mSsid = mSsid;
    }

    public byte[] getmKey() throws Exception {
        return this.mKey;
    }

    public void setmKey(byte[] mKey) throws Exception {
        this.mKey = mKey;
    }

    public byte[] getmFreeData() throws Exception {
        return this.mfreeData;
    }

    public void setmFreeData(byte[] mFreeData) throws Exception {
        this.mfreeData = mFreeData;
    }

    public String getmToken() throws Exception {
        return this.mToken;
    }

    public void setmToken(String mToken) throws Exception {
        this.mToken = mToken;
    }

    public void setHasEncryption(boolean hasEncryption) throws Exception {
        this.hasEncryption = hasEncryption;
    }
}

