package com.het.xml.protocol.coder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

//协议定义
@XStreamAlias("definitions")
public class ByteListDefinition {

    @XStreamAsAttribute
    private Boolean checkByteArrayLength;
    @XStreamImplicit
    private List<ByteDefinition> byteDefList;

    @XStreamAlias("loop")
    private ByteLoopDefinition bytesLoop;

    public Boolean isCheckByteArrayLength() {
        return checkByteArrayLength == null ? true : checkByteArrayLength;
    }

    public void setCheckByteArrayLength(Boolean checkByteArrayLength) {
        this.checkByteArrayLength = checkByteArrayLength;
    }

    public List<ByteDefinition> getByteDefList() {
        return byteDefList;
    }

    public void setByteDefList(List<ByteDefinition> byteDefList) {
        this.byteDefList = byteDefList;
    }

    public ByteLoopDefinition getBytesLoop() {
        return bytesLoop;
    }

    public void setBytesLoop(ByteLoopDefinition bytesLoop) {
        this.bytesLoop = bytesLoop;
    }

}
