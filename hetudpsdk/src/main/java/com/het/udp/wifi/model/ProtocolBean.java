package com.het.udp.wifi.model;

import java.io.Serializable;

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: ProtocolBean.java
 * Create: 2016/4/12 18:30
 * Author: uuxia
 */
public class ProtocolBean implements Serializable {

    /**
     * productId : 58
     * protocolId : 477
     * developerId : 409
     * productVersion : 1
     * protocolFormat : 1
     * deviceTypeId : 11
     * deviceSubtypeId : 3
     * command : 0401
     * mode : 0
     * content : <?xml version="1.0" encoding="UTF-8"?>
     <protocol>
     <id>1-11-3-0401-D</id>
     <description>香薰机设备信息协议解码</description>
     <definitions>
     <byteDef length="1"  javaType="INTEGER" property="ctrVer" propertyName="控制板固件版本号" order="1"/>
     <byteDef length="1"  javaType="INTEGER" property="dashboardVer" propertyName="显示板固件版本号" order="2"/>
     <byteDef length="1" javaType="INTEGER" property="driverVer" ignore="false" propertyName="驱动板固件版本号" order="5"/>
     <byteDef length="1" javaType="INTEGER" ignore="true" />
     <byteDef length="1" javaType="INTEGER" property="ctrHardWareVer" ignore="false" propertyName="控制板硬件版本号" order="6"/>
     <byteDef length="1" javaType="INTEGER" property="dashboarHardWareVer" ignore="false" propertyName="显示板硬件版本号" order="7"/>
     <byteDef length="1" javaType="INTEGER" property="driverHardWareVer" ignore="false" propertyName="驱动板硬件版本号" order="8"/>
     <byteDef length="2" javaType="INTEGER" ignore="true"/>
     <byteDef length="1" javaType="INTEGER" property="deviceType" ignore="false" propertyName="设备大类" order="10"/>
     <byteDef length="1" javaType="INTEGER" property="deviceSubType" ignore="false" propertyName="设备子类" order="11"/>
     <byteDef length="15" javaType="STRING"  property="identify" ignore="false" propertyName="设备标识" order="14"/>
     <byteDef length="22"  javaType="STRING"  ignore="true" />
     </definitions>
     </protocol>
     * protocolType : 1
     * dataTypeId : 1
     * moduleType : 1
     */

    private int productId;
    private int protocolId;
    private int developerId;
    private int productVersion;
    private int protocolFormat;
    private int deviceTypeId;
    private int deviceSubtypeId;
    private String command;
    private int mode;
    private String content;
    private int protocolType;
    private int dataTypeId;
    private int moduleType;
    private String protocolName;

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }

    public void setDeveloperId(int developerId) {
        this.developerId = developerId;
    }

    public void setProductVersion(int productVersion) {
        this.productVersion = productVersion;
    }

    public void setProtocolFormat(int protocolFormat) {
        this.protocolFormat = protocolFormat;
    }

    public void setDeviceTypeId(int deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public void setDeviceSubtypeId(int deviceSubtypeId) {
        this.deviceSubtypeId = deviceSubtypeId;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public void setDataTypeId(int dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public int getProductId() {
        return productId;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public int getDeveloperId() {
        return developerId;
    }

    public int getProductVersion() {
        return productVersion;
    }

    public int getProtocolFormat() {
        return protocolFormat;
    }

    public int getDeviceTypeId() {
        return deviceTypeId;
    }

    public int getDeviceSubtypeId() {
        return deviceSubtypeId;
    }

    public String getCommand() {
        return command;
    }

    public int getMode() {
        return mode;
    }

    public String getContent() {
        return content;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public int getDataTypeId() {
        return dataTypeId;
    }

    public int getModuleType() {
        return moduleType;
    }

    @Override
    public String toString() {
        return "ProtocolBean{" +
                "productId=" + productId +
                ", protocolId=" + protocolId +
                ", developerId=" + developerId +
                ", productVersion=" + productVersion +
                ", protocolFormat=" + protocolFormat +
                ", deviceTypeId=" + deviceTypeId +
                ", deviceSubtypeId=" + deviceSubtypeId +
                ", command='" + command + '\'' +
                ", mode=" + mode +
                ", content='" + content + '\'' +
                ", protocolType=" + protocolType +
                ", dataTypeId=" + dataTypeId +
                ", moduleType=" + moduleType +
                ", protocolName='" + protocolName + '\'' +
                '}';
    }
}
