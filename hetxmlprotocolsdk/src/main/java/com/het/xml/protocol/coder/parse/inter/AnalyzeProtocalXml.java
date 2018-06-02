package com.het.xml.protocol.coder.parse.inter;

import com.thoughtworks.xstream.XStreamException;

import java.io.File;


/**
 * 协议配置解析接口
 *
 * @author jake
 */
public interface AnalyzeProtocalXml<T> {

    /**
     * 根据协议xml路径解析xml
     *
     * @param protocalXmlPath
     * @return
     */
    T parse(String protocalXmlPath) throws XStreamException;

    /**
     * 解析协议xml文件
     *
     * @param protocalXmlFile
     * @return
     */
    T parseXMLFile(File protocalXmlFile) throws XStreamException;

    /**
     * 解析xml字符串
     *
     * @param xml
     * @return
     * @throws XStreamException
     */
    T paseXML(String xml) throws XStreamException;


    /***
     * 将对象转换成xml文件
     *
     * @param t
     * @return
     * @throws XStreamException
     */
    String toXml(T t) throws XStreamException;
}
