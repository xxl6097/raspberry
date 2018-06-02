package com.het.xml.protocol.model;

import java.io.Serializable;
import java.util.List;

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: ProtocolDataModel.java
 * Create: 2016/4/29 13:53
 * Author: uuxia
 */
public class ProtocolDataModel implements Serializable {

    private static final long serialVersionUID = -5956024401835501248L;
    /**
     * protocolDate : 1458095456000
     * list : [{"productId":136,"protocolId":1055,"developerId":1,"productVersion":1,"protocolFormat":1,"deviceTypeId":6,"deviceSubtypeId":1,"command":"A031","mode":0,"content":"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<protocol>\r\n\t<id>1-6-1-A031-D<\/id>\r\n\t<description>床垫设备信息协议解码<\/description>\r\n\t<definitions>\t\r\n\t\t<byteDef length=\"1\"  javaType=\"INTEGER\" property=\"breathRate\" ignore=\"false\" propertyName=\"心率\" />\r\n\t\t<byteDef length=\"1\"  javaType=\"INTEGER\" property=\"breathRate\" ignore=\"false\" propertyName=\"呼吸率\" />\r\n\t\t<byteDef>\r\n\t\t\t<bitDefList>\r\n\t\t\t\t<bitDef length=\"1\" shift=\"0\" javaType=\"BYTE\" property=\"snoreTimes\" propertyName=\"打呼次数\"/>\r\n\t\t\t\t<bitDef length=\"1\" shift=\"1\" javaType=\"BYTE\" property=\"hasAnybody\" propertyName=\"是否有人\"/>\r\n\t\t\t<\/bitDefList>\r\n\t\t<\/byteDef>\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"turnOverTimes\" ignore=\"false\" propertyName=\"翻身次数\"/>\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"timeZone\" ignore=\"false\" propertyName=\"时区\"/>\r\n\t\t<byteDef length=\"2\" javaType=\"INTEGER\" property=\"year\" ignore=\"false\" propertyName=\"当前时间（年）\"/>\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"month\" ignore=\"false\" propertyName=\"当前时间（月）\"/>\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"day\" ignore=\"false\" propertyName=\"当前时间（日）\" />\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"hour\" ignore=\"false\" propertyName=\"当前时间（时）\" />\r\n\t\t<byteDef length=\"1\" javaType=\"INTEGER\" property=\"minute\" ignore=\"false\" propertyName=\"当前时间（分）\" />\r\n\t<\/definitions>\r\n<\/protocol>","protocolType":3,"dataTypeId":8,"moduleType":2}]
     */

    private long protocolDate;

    private int productId;

    private List<ProtocolBean> list;

    public void setProtocolDate(long protocolDate) {
        this.protocolDate = protocolDate;
    }

    public void setList(List<ProtocolBean> list) {
        this.list = list;
    }

    public long getProtocolDate() {
        return protocolDate;
    }

    public List<ProtocolBean> getList() {
        return list;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "ProtocolDataModel{" +
                "protocolDate=" + protocolDate +
                ", productId=" + productId +
                ", list=" + list +
                '}';
    }
}
