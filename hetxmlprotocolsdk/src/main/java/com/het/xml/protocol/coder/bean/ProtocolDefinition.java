package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.util.List;

@XStreamAlias("protocol")
public class ProtocolDefinition implements Serializable {

    private static final long serialVersionUID = -2349036864618595131L;
    //协议版本号
    @XStreamAlias("id")
    private String id;
    //目标Class
    @XStreamAlias("className")
    private String className;
    //是否生成crc验证码
    @XStreamAlias("crc")
    private boolean crc = true;
    //协议描述
    @XStreamAlias("description")
    private String description;
    //协议定义
    @XStreamAlias("definitions")
    private List<ByteDefinition> byteDefList;
    //协议定义
//    @XStreamAlias("definitions")
//    private ByteListDefinition bytesDefinition;

    //数据包大小
    private Integer packetSize;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public List<ByteDefinition> getByteDefList() {
        return byteDefList;
    }

    public void setByteDefList(List<ByteDefinition> byteDefList) {
        this.byteDefList = byteDefList;
    }

    public boolean isCrc() {
        return crc;
    }

    public void setCrc(boolean crc) {
        this.crc = crc;
    }

    public Integer getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(Integer packetSize) {
        this.packetSize = packetSize;
    }

    @Override
    public String toString() {
        return "ProtocolDefinition{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", crc=" + crc +
                ", description='" + description + '\'' +
                ", byteDefList=" + byteDefList +
                ", packetSize=" + packetSize +
                '}';
    }
}
