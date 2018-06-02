package com.het.xml.protocol.coder.decode;



import com.het.log.Logc;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.exception.DecodeException;
import com.het.xml.protocol.coder.utils.BinaryConvertUtils;
import com.het.xml.protocol.coder.utils.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * 功能：用于设备数据的解析 第二层协议解码器
 *
 * @Original jake  @improver uuxia
 */
public class SecondLayerProtocolDecoder extends AbstractDecoder {

    @Override
    public <T> T decode(Object data) throws Exception {
        Object result = null;
        if (data instanceof Map) {
            Map map = (Map) data;
            Object dataVersion = map.get("dataVersion");
            Object deviceType = map.get("deviceType");
            Object deviceSubType = map.get("deviceSubType");
            short cmd = (Short) map.get("command");
            Object productId = map.get("productId");
            String command = StringUtil.byteArrayToHexString(BinaryConvertUtils
                    .longToByteArray(cmd, 2));
            StringBuilder keyBuilder = new StringBuilder();
            //协议ID：
            keyBuilder.append(dataVersion).append("-")
                    .append(deviceType).append("-")
                    .append(deviceSubType).append("-")
                    .append(command);//.append("-").append("D");
            String key = keyBuilder.toString();
            byte[] deviceData = (byte[]) map.get("data");
            boolean empty = map.get("empty") == null ? false : (Boolean) map.get("empty");

            ProtocolDefinition protocolDefinition = this.protocolXmlManager
                    .getProtocolDefinition(key);

            if (protocolDefinition == null) {
                key = dataVersion + "-" + command;// + "-D";
                protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
            }
            if (protocolDefinition == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("2").append("-")
                        .append(deviceType).append("-")
                        .append(deviceSubType).append("-")
                        .append(command);
                protocolDefinition = this.protocolXmlManager.getProtocolDefinition(sb.toString());
            }

            if (protocolDefinition == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(productId).append("-")
                        .append(command);
                protocolDefinition = this.protocolXmlManager.getProtocolDefinition(sb.toString());
            }

            if (protocolDefinition == null) {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,
                        "<PROTOCOL_ID:{}>can't find the protocol configuration" + key);
                throw new DecodeException(
                        "<PROTOCOL_ID:" + key + ">can't find the protocol configuration");
            }

            int bodyLen = calcBodyLength(protocolDefinition);
            if (empty) {
                deviceData = new byte[bodyLen];
            }
            if (deviceData == null) {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"deviceData is null. PROTOCOL_ID:" + key);
                throw new DecodeException("deviceData is null. PROTOCOL_ID:" + key);
            }
            try {
                result = this.decode(protocolDefinition, deviceData);
            } catch (Exception e) {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"<PROTOCOL_ID:" + key + ">EXCEPTION" + e);
                throw new DecodeException("<PROTOCOL_ID:" + protocolDefinition.getId() + ">" + e.getMessage());
            }
        }
        return (T) result;
    }

    private int calcBodyLength(ProtocolDefinition protocolDefinition) {
        List<ByteDefinition> byteList = protocolDefinition.getByteDefList();
        int len = 0;
        for (ByteDefinition item : byteList) {
            len += item.getLength() == null ? 1 : item.getLength();
        }
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"bodyLen = " + len);
        return len;
    }

}
