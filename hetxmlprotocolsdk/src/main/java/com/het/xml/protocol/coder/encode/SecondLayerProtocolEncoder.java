package com.het.xml.protocol.coder.encode;


import android.text.TextUtils;

import com.het.log.Logc;
import com.het.xml.protocol.ProtocolManager;
import com.het.xml.protocol.coder.bean.BaseDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.exception.EncodeException;
import com.het.xml.protocol.coder.utils.BinaryConvertUtils;
import com.het.xml.protocol.coder.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SecondLayerProtocolEncoder extends AbstractEncoder {

    @Override
    public byte[] encode(Object data) throws Exception {
        String command = "";
        //第二层协议版本号
        //第一层协议版本号
        Object deviceType = null;
        Object deviceSubType = null;
        Object dataVersion = null;
        Object productId = null;
        if (data instanceof Map) {
            Map dto = (Map) data;
            //获取命令码
            command = StringUtil.byteArrayToHexString(BinaryConvertUtils.longToByteArray(Integer.parseInt(dto.get("command").toString()), 2));
            dataVersion = dto.get("dataVersion");
            deviceType = dto.get("deviceType");
            deviceSubType = dto.get("deviceSubType");
            productId = dto.get("productId");
        } else {
//            BaseDto dto = (BaseDto) data;
//            //获取命令码
//            command = StringUtil.byteArrayToHexString(BinaryConvertUtils.longToByteArray(dto.getCommand(), 2));
//            dataVersion = dto.getVersion();
//            deviceType = dto.getDeviceType();
//            deviceSubType = dto.getDeviceSubType();
        }

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(dataVersion).append("-")
                .append(deviceType).append("-")
                .append(deviceSubType).append("-")
                .append(command);//.append("-").append("E");
        //获取包开头标识码
        String key = keyBuilder.toString();
        ProtocolDefinition protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        if (protocolDefinition == null) {
            key = dataVersion + "";// + "-" + command + "-E";
            protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        }
        if (protocolDefinition == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("2").append("-")
                    .append(deviceType).append("-")
                    .append(deviceSubType).append("-")
                    .append(command);//.append("-").append("E");
            key = sb.toString();
            protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        }

        if (protocolDefinition == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(productId).append("-")
                    .append(command);
            key = sb.toString();
            protocolDefinition = this.protocolXmlManager.getProtocolDefinition(key);
        }
        if (protocolDefinition == null) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "can't find the protocol configuration,protocolId=" + key);
            throw new EncodeException("<PROTOCOL_ID:" + key + "> can't find the protocol configuration");
        }
        byte tempData[] = null;
        if (ProtocolManager.getInstance().isAutoCalcUpdateFlag()) {
            try {
                HashMap<String, BaseDefinition> mapper = this.protocolXmlManager.get0104Mapper(key);
                if (mapper != null) {
                    processUpdateFlag(data, mapper);
                }
            } catch (Exception e) {
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "<PROTOCOL_ID=" + key + ">EXCEPTION=" + e);
            }
        }
        try {
            tempData = this.encode(protocolDefinition, data);
        } catch (Exception e) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "<PROTOCOL_ID=" + key + ">EXCEPTION=" + e);
            throw new EncodeException("PROTOCOL_ID=" + protocolDefinition.getId() + " exception=" + e.getMessage());
        }
        return tempData;
    }


    private void processUpdateFlag(Object data, HashMap<String, BaseDefinition> mapper) {
        if (data instanceof TreeMap) {
            TreeMap<String, Object> dto = (TreeMap<String, Object>) data;
            final String UPDATEFLAG_KAY = "updateFlag";
            BaseDefinition vv = mapper.get(UPDATEFLAG_KAY);
            if (vv != null) {
                int updateFlagLength = vv.getLength();
                Iterator<String> keys = dto.keySet().iterator();
                if (keys != null) {
                    List<BaseDefinition> keyValye = new ArrayList<BaseDefinition>();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (key == null)
                            continue;
                        if (key.equalsIgnoreCase("command") ||
                                key.equalsIgnoreCase("macAddress") ||
                                key.equalsIgnoreCase("deviceType") ||
                                key.equalsIgnoreCase("deviceSubType") ||
                                key.equalsIgnoreCase("dataVersion") ||
                                key.equalsIgnoreCase(UPDATEFLAG_KAY))
                            continue;
                        //处理事务
                        BaseDefinition tmp = mapper.get(key);
                        if (tmp != null) {
                            keyValye.add(tmp);
                        }
                    }
                    String flag = calcUpdateFlag(updateFlagLength, keyValye);
                    if (!TextUtils.isEmpty(flag)) {
                        dto.put(UPDATEFLAG_KAY, flag);
                    }
                }
            }
        }
    }

    private String calcUpdateFlag(int total, List<BaseDefinition> keyValye) {
        if (total > 0 && keyValye != null && keyValye.size() > 0) {
            boolean[] flag = new boolean[total * 8];
            byte[] updateFlag = new byte[total];
            for (int i = 0; i < keyValye.size(); i++) {
                BaseDefinition bd = keyValye.get(i);
                if (bd != null) {
                    int po = calcIndex(total, bd.getIndex());
                    int len = bd.getLength();
                    for (int j = 0; j < len; j++) {
                        flag[po - j] = true;
                    }
                }
            }
            for (int k = 0; k < total; k++) {
                byte des = 0;
                byte tmp = 1;
                for (int m = 0; m < 8; m++) {
                    if (flag[8 * (total - k - 1) + m]) {
                        tmp = 1;
                        tmp <<= m;
                        des |= tmp;
                    }
                }
                updateFlag[k] = des;
            }
            Logc.e("----------------------bb>" + Arrays.toString(flag));
            String result = StringUtil.byteArrayToHexString(updateFlag);
            Logc.e("----------------------uu>" + result);
            return result;
        }
        return null;
    }

    private int calcIndex(int total, int index) {
        int value = index / 8;
        int remain = index % 8;
        int xx = value + (remain == 0 ? 0 : 1);
        int po = 8 * (total - xx) + (remain == 0 ? 8 : remain);
        if (po <= (total * 8)) {
            return po - 1;
        }
        return -1;
    }

}
