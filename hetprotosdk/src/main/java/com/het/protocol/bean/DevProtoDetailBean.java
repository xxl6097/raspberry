package com.het.protocol.bean;

import java.io.Serializable;

public class DevProtoDetailBean implements Serializable {
    // 设备类型
    private int devType;
    // 设备子类型类型
    private int devSubType;
    // 产品编号
    private int devNum;
    // 设备mac地址
    private String devMacAddr;
    //设备Ip地址
    private String devIpAddr;
}
