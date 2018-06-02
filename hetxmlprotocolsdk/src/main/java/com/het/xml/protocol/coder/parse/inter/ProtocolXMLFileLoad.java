package com.het.xml.protocol.coder.parse.inter;

/**
 * 协议xml文件加载接口
 *
 * @Original jake  @improver uuxia
 */
public interface ProtocolXMLFileLoad<T> {

    /**
     * 协议xml文件加载
     *
     * @param filePathRegex 路径表达式
     */
    void load(String filePathRegex);

    /**
     * 加载Xml字符串
     *
     * @param xmlString
     */
    void loadXmlString(T xmlString);
}
