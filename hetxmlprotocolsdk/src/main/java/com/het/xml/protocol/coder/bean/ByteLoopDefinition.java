package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public class ByteLoopDefinition {

    @XStreamAsAttribute
    private Integer times;
    @XStreamAsAttribute
    private String refValue;
    @XStreamAsAttribute
    private String property;
    //属性名称描述
    @XStreamAsAttribute
    private String propertyName;
    @XStreamAsAttribute
    private String className;

    @XStreamImplicit
    private List<ByteDefinition> byteDefList;

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public List<ByteDefinition> getByteDefList() {
        return byteDefList;
    }

    public void setByteDefList(List<ByteDefinition> byteDefList) {
        this.byteDefList = byteDefList;
    }

    public String getRefValue() {
        return refValue;
    }

    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
