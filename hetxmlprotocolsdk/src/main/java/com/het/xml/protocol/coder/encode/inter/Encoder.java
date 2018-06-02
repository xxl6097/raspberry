package com.het.xml.protocol.coder.encode.inter;

/**
 * 编码接口
 *
 * @param <E>
 * @author jake
 */
public interface Encoder {
    /**
     * 根据protocolDefinition协议定义解码数据
     *
     * @param data 待编码的java bean
     * @return 编码后的数据
     */
    public abstract byte[] encode(Object data) throws Exception;
}
