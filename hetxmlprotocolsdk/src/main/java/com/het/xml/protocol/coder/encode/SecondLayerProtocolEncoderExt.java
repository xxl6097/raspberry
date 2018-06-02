package com.het.xml.protocol.coder.encode;



import com.het.log.Logc;
import com.het.xml.protocol.coder.ProtocolMode;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.encode.crc.CrcCalculateStrategy;
import com.het.xml.protocol.coder.exception.EncodeException;
import com.het.xml.protocol.coder.parse.ProductorProtocolManager;

import java.util.Map;

/**
 * 厂商协议编码
 *
 * @author jake
 */
public class SecondLayerProtocolEncoderExt extends AbstractEncoder {
    //循环冗余计算接口
    private CrcCalculateStrategy crcCalculate;

    //协议管理器
    private ProductorProtocolManager protocolManager;

    public void setProtocolManager(ProductorProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void setCrcCalculate(CrcCalculateStrategy crcCalculate) {
        this.crcCalculate = crcCalculate;
    }

    @Override
    public byte[] encode(Object data) throws Exception {
        Map dto = (Map) data;
        //获取命令码
        String command = dto.get("command").toString();
        //设备数据协议版本号
        Object dataVersion = dto.get("dataVersion");
        Object deviceType = dto.get("deviceType");
        Object deviceSubType = dto.get("deviceSubType");

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(dataVersion).append("-")
                .append(deviceType).append("-")
                .append(deviceSubType).append("-")
                .append(command).append("-").append("E");
        //获取包开头标识码
        String developerID = dto.get("developerID").toString();
        String protocolID = keyBuilder.toString();
        //获取协议生产模式协议
        ProtocolDefinition protocolDefinition = protocolManager.get(developerID, protocolID, ProtocolMode.PRODUCT_MODE);
        if (protocolDefinition == null) {
            //获取协议开发模式协议
            protocolDefinition = protocolManager.get(developerID, protocolID, ProtocolMode.DEVELOP_MODE);
        }
        if (protocolDefinition == null) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[DEVELOPER_ID:{} PROTOCOL_ID:{}]-can't find the protocol configuration" + developerID + protocolID);
            throw new EncodeException("[PROTOCOL_ID:" + protocolID + "]-can't find the protocol configuration");
        }
        byte tempData[] = null;
        try {
            tempData = this.encode(protocolDefinition, data);
        } catch (Exception e) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[PROTOCOL_ID:" + protocolDefinition.getId() + "]-EXCEPTION" + e);
            throw new EncodeException("[PROTOCOL_ID:" + protocolDefinition.getId() + "]-" + e.getMessage());
        }

        return tempData;
//		if(protocolDefinition.isCrc()){
//			ByteArrayOutputStream byteArr = new ByteArrayOutputStream(tempData.length+2);
//			DataOutputStream out = new DataOutputStream(byteArr);
//			out.write(tempData);
//			int crc = crcCalculate.calculate(tempData);
//			out.writeShort(crc);
//			return byteArr.toByteArray();
//		}else{
//			return tempData;
//		}
    }


}
