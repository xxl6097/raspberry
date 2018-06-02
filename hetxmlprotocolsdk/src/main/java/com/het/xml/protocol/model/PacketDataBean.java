package com.het.xml.protocol.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2016, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * <p>
 * <p>描述：</p>
 * 名称: PacketDataBean <br>
 * 作者: uuxia<br>
 * 版本: 1.0<br>
 * 日期: 2016/7/26 17:08<br>
 **/
public class PacketDataBean implements Serializable {
    private byte[] body;
    private short deviceType;
    private byte deviceSubType;
    private short command;
    private Integer dataVersion;
    private String json;
    private String deviceMac;

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public short getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }

    public byte getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(byte deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public short getCommand() {
        return command;
    }

    public void setCommand(short command) {
        this.command = command;
    }

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "PacketDataBean{" +
                "body=" + Arrays.toString(body) +
                ", deviceType='" + deviceType + '\'' +
                ", deviceSubType='" + deviceSubType + '\'' +
                ", command='" + command + '\'' +
                ", dataVersion=" + dataVersion +
                ", json='" + json + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                '}';
    }
}
