package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;


public class BaseDefinition implements Serializable {

    private static final long serialVersionUID = -6402956161850508576L;
    //字节长度
    @XStreamAlias("length")
    @XStreamAsAttribute
    private Integer length;
    //目标class的属性数据类型
    @XStreamAlias("javaType")
    @XStreamAsAttribute
    private String javaType = "BYTE";
    //目标class的属性名
    @XStreamAlias("property")
    @XStreamAsAttribute
    private String property;
    //是否忽略该字节(对于协议中的保留字，设置该属性为true，则忽略。不会将此字节存入目标class实例中保存)
    @XStreamAlias("ignore")
    @XStreamAsAttribute
    private boolean ignore = false;
    //属性名称描述
    @XStreamAlias("propertyName")
    @XStreamAsAttribute
    private String propertyName;
    @XStreamAlias("order")
    @XStreamAsAttribute
    private Integer order;

    @XStreamAlias("gap")
    @XStreamAsAttribute
    private Integer gap;

    @XStreamAlias("mulriple")
    @XStreamAsAttribute
    private Integer mulriple;

    //索引号
    private int index;

    //存储数据字段
    private Object value;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType == null ? null : javaType.trim();
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property == null ? null : property.trim();
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName == null ? null : propertyName.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getGap() {
        return gap;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    public Integer getMulriple() {
        return mulriple;
    }

    public void setMulriple(Integer mulriple) {
        this.mulriple = mulriple;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
