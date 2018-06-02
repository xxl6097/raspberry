package com.het.xml.protocol.coder.decode.inter;

/**
 * 数据解码接口
 *
 * @Original jake  @improver uuxia
 */
public interface Decoder {

    /**
     * 根据protocolDefinition协议定义解码数据
     *
     * @param data 待解码的原始数据
     * @return 解码后的数据
     */
    <T> T decode(Object data) throws Exception;
}
