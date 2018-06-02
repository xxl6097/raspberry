package com.het.xml.protocol.coder.parse;

import com.het.xml.protocol.coder.bean.BitDefinition;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.parse.inter.AnalyzeProtocalXml;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.StringWriter;

/**
 * 协议xml解析类
 *
 * @author jake
 */
public class AnalyzeProtocalXmlImpl implements AnalyzeProtocalXml<ProtocolDefinition> {

    public static void main(String[] args) {
        AnalyzeProtocalXmlImpl impl = new AnalyzeProtocalXmlImpl();
//        System.out.println(impl.toXml(new ProtocolDefinition()));
        File file  = new File("C:\\Users\\Administrator\\Desktop\\xml\\aroma1RunData.xml");
        ProtocolDefinition protocolDefinition = impl.parseXMLFile(file);
//        System.out.println(impl.toXml(protocolDefinition));

    }

    public ProtocolDefinition parse(String protocalXmlPath)
            throws XStreamException {
        File xmlFile = new File(protocalXmlPath);
        return parseXMLFile(xmlFile);
    }

    public ProtocolDefinition parseXMLFile(File protocalXmlFile)
            throws XStreamException {
        XStream xstream = configXStream();
        return (ProtocolDefinition) xstream.fromXML(protocalXmlFile);
    }

    @Override
    public ProtocolDefinition paseXML(String xml) throws XStreamException {
        XStream xstream = configXStream();
        return (ProtocolDefinition) xstream.fromXML(xml);
    }

    @Override
    public String toXml(ProtocolDefinition protocolDefinition) throws XStreamException {
        XStream xstream = configXStream();
        StringWriter writer = new StringWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xstream.toXML(protocolDefinition, writer);
        return writer.toString();
    }

    private XStream configXStream() {
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        //设置标签转换关系
        xstream.alias("protocol", ProtocolDefinition.class);
        xstream.aliasField("definitions", ProtocolDefinition.class, "byteDefList");
        xstream.alias("byteDef", ByteDefinition.class);
        xstream.alias("bitDef", BitDefinition.class);
        xstream.aliasField("bitDefList", ByteDefinition.class, "bitDefList");
        //设定byteDef属性转换关系
        xstream.aliasAttribute(ByteDefinition.class, "property", "property");
        xstream.aliasAttribute(ByteDefinition.class, "length", "length");
        xstream.aliasAttribute(ByteDefinition.class, "javaType", "javaType");
        xstream.aliasAttribute(ByteDefinition.class, "ignore", "ignore");
        xstream.aliasAttribute(ByteDefinition.class, "refValue", "refValue");
        xstream.aliasAttribute(ByteDefinition.class, "propertyName", "propertyName");
        xstream.aliasAttribute(ByteDefinition.class, "order", "order");
        xstream.aliasAttribute(ByteDefinition.class, "mulriple", "mulriple");
        xstream.aliasAttribute(ByteDefinition.class, "gap", "gap");
        //设置bitDef属性转换关系
        xstream.aliasAttribute(BitDefinition.class, "property", "property");
        xstream.aliasAttribute(BitDefinition.class, "length", "length");
        xstream.aliasAttribute(BitDefinition.class, "javaType", "javaType");
        xstream.aliasAttribute(BitDefinition.class, "shift", "shift");
        xstream.aliasAttribute(BitDefinition.class, "propertyName", "propertyName");
        xstream.aliasAttribute(BitDefinition.class, "order", "order");
        xstream.aliasAttribute(ByteDefinition.class, "mulriple", "mulriple");
        xstream.aliasAttribute(ByteDefinition.class, "gap", "gap");
        return xstream;
    }
}
