package com.het.xml.protocol.coder.decode;


import com.het.log.Logc;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;

/**
 * 第一层协议解码器
 *
 * @author Administrator
 */
public class FirstLayerProtocolDecoder extends AbstractDecoder {

    @Override
    public <T> T decode(Object data) throws Exception {
        byte[] origData = (byte[]) data;
        //版本号
        byte version = 0;
        String header = "";
        int packageStart = origData[0] & 0xFF;
        //如果包以F2开头
        if (packageStart == 0xF2) {
            version = origData[1];
            header = "F2";
            //如果包以5A开头
        } else if (packageStart == 0x5A) {
            version = origData[2];
            header = "5A";
        }
        //获取主版本号
        Integer mainVersion = version >>> 6 & 0x3;
        Integer minorVersion = version & 0x3F;
        String key = mainVersion + "-" + minorVersion + "-" + header + "-D";
        ProtocolDefinition protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        if (protocolDefinition == null) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"can't find the protocol configuration[protocolId:{}]" + key);
            throw new Exception("can't find the protocol configuration[protocolId:" + key + "]");
        }
        protocolDefinition.setClassName(null);
        return this.decode(protocolDefinition, origData);
    }


}
