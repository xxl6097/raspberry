package com.het.udp.core.keepalive;

import android.text.TextUtils;

import com.het.udp.core.Utils.DeviceBindMap;

import java.util.HashSet;

/**
 * Created by UUXIA on 2015/6/15.
 */
public abstract class OnDeviceOnlineListener {
    private HashSet<String> mVectorMacAddr = new HashSet<String>();

    public OnDeviceOnlineListener() {
    }

    /**
     * @param mMacAddr
     */
    public OnDeviceOnlineListener(String mMacAddr) {
        if (!TextUtils.isEmpty(mMacAddr)) {
            mVectorMacAddr.add(mMacAddr);
        }
    }

    /**
     * @param mVectorMacAddr
     */
    public OnDeviceOnlineListener(HashSet<String> mVectorMacAddr) {
        this.mVectorMacAddr.addAll(mVectorMacAddr);
    }

    public HashSet<String> getVectorMacAddr() {
        return mVectorMacAddr;
    }

    /**
     * @param nMacAddr
     */
    public void push(String nMacAddr) {
        if (nMacAddr == null)
            return;
        mVectorMacAddr.add(nMacAddr);
    }

    public void remove(String mac){
        if (TextUtils.isEmpty(mac))
            return;
        mVectorMacAddr.remove(mac);
    }

    /**
     * @param macList
     */
    public void push(HashSet macList) {
        if (macList == null)
            return;
        mVectorMacAddr.addAll(macList);
    }

    public void relese() {
        mVectorMacAddr.clear();
        //这个地方只要上层注销了，在线设备也全部干掉，因为不关注小循环了。
        DeviceBindMap.runJudgeBindStatus.clear();
    }

    /**
     * @param online
     */
    public abstract void onLine(boolean online, String nMacAddr);
}
