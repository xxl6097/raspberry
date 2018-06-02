package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.util.List;


/**
 * 解析协议中，字节定义
 *
 * @Original jake  @improver uuxia
 */
@XStreamAlias("byteDef")
public class ByteDefinition extends BaseDefinition {
    private static final long serialVersionUID = -7296782848736073447L;
    //引用目标Class属性值
    @XStreamAlias("refValue")
    @XStreamAsAttribute
    private String refValue;
    //bit定义集合
    @XStreamAlias("bitDefList")
    private List<BitDefinition> bitDefList;

    public String getRefValue() {
        return refValue;
    }

    public void setRefValue(String refValue) {
        this.refValue = refValue == null ? null : refValue.trim();
    }

    public List<BitDefinition> getBitDefList() {
        return bitDefList;
    }

    public void setBitDefList(List<BitDefinition> bitDefList) {
        this.bitDefList = bitDefList;
    }


}
