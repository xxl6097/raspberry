package com.het.xml.protocol.coder.encode;



import com.het.log.Logc;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.encode.crc.CrcCalculateStrategy;
import com.het.xml.protocol.coder.utils.Crc16Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Map;

/**
 * 第一层协议编码器
 *
 * @author jake
 */
public class FirstLayerProtocolEncoder extends AbstractEncoder {

    private CrcCalculateStrategy crcCalculate;

    public void setCrcCalculate(CrcCalculateStrategy crcCalculate) {
        this.crcCalculate = crcCalculate;
    }

    @Override
    public byte[] encode(Object data) throws Exception {
        /*PackageDto packageDto = (PackageDto) data;
        //获取包开头标识码
        String header = packageDto.getPackageStart();
        String key = packageDto.getMainVersion() + "-" + packageDto.getMinorVersion() + "-" + header + "-E";*/


        Object mainVersion = null;
        Object minorVersion = null;
        Object packageStart = null;
        if (data instanceof Map) {
            Map dto = (Map) data;
            //获取命令码
            mainVersion = dto.get("mainVersion");
            minorVersion = dto.get("minorVersion");
            packageStart = dto.get("packageStart");
        }
        String key = mainVersion + "-" + minorVersion + "-" + packageStart + "-E";

        ProtocolDefinition protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        if (protocolDefinition == null) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"can't find the protocol configuration[protocolId:{}]" + key);
            throw new Exception("can't find the protocol configuration[protocolId:" + key + "]");
        }
        byte tempData[] = this.encode(protocolDefinition, data);
        //是否生成循环冗余验证码
        if (protocolDefinition.isCrc()) {
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream(tempData.length + 2);
            DataOutputStream out = new DataOutputStream(byteArr);
            out.write(tempData);
            int crc = Crc16Utils.computeChecksum(tempData, tempData.length);//crcCalculate.calculate(tempData);
            out.writeShort(crc);
            return byteArr.toByteArray();
        } else {
            return tempData;
        }
    }



}
