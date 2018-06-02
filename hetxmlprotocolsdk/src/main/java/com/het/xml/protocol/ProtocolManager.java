/*
 * -----------------------------------------------------------------
 * Copyright ©2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: ProtocolManager.java
 * Create: 2015/9/16 13:08
 */
package com.het.xml.protocol;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.het.xml.protocol.coder.decode.SecondLayerProtocolDecoder;
import com.het.xml.protocol.coder.encode.SecondLayerProtocolEncoder;
import com.het.xml.protocol.coder.parse.AnalyzeProtocalXmlImpl;
import com.het.xml.protocol.coder.parse.ProtocolFileLoadManager;
import com.het.xml.protocol.coder.utils.StringUtil;
import com.het.xml.protocol.model.DeviceProBean;
import com.het.xml.protocol.model.PacketDataBean;
import com.het.xml.protocol.model.ProtocolDataModel;
import com.het.xml.protocol.utils.GsonTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 13:08
 * Description:协议管理类
 */
public class ProtocolManager {
    //    private static String url = "file:///android_asset/xml";
    /*是否使用xml解析协议*/
    private boolean bLoad = false;
    /**
     * Singtan instance
     */
    private volatile static ProtocolManager instance = null;
    /**
     * 加载xml管理类
     */
    private ProtocolFileLoadManager protocolFileLoadManager;
    /**
     * 解析xml类
     */
    private AnalyzeProtocalXmlImpl analyzeProtocalXml;

    /**
     * 协议解码器
     */
    private SecondLayerProtocolDecoder decoder;
    /**
     * 存储连接设备信息*
     */
    public static ConcurrentHashMap<String, DeviceProBean> deviceList = new ConcurrentHashMap<String, DeviceProBean>();

    /**
     * 设疑编码器
     */
    private SecondLayerProtocolEncoder encoder;

    private boolean isAutoCalcUpdateFlag = false;

    private Gson gson;

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
//            gson = new GsonBuilder()
//                    .registerTypeAdapter(
//                            new TypeToken<TreeMap<String, Object>>() {
//                            }.getType(),
//                            new JsonDeserializer<TreeMap<String, Object>>() {
//                                @Override
//                                public TreeMap<String, Object> deserialize(
//                                        JsonElement json, Type typeOfT,
//                                        JsonDeserializationContext context) throws JsonParseException {
//
//                                    TreeMap<String, Object> treeMap = new TreeMap<>();
//                                    JsonObject jsonObject = json.getAsJsonObject();
//                                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
//                                    for (Map.Entry<String, JsonElement> entry : entrySet) {
//                                        treeMap.put(entry.getKey(), entry.getValue());
//                                    }
//                                    return treeMap;
//                                }
//                            }).create();
        }
        return gson;
    }

    public static ProtocolManager getInstance() {
        if (instance == null) {
            synchronized (ProtocolManager.class) {
                if (null == instance) {
                    instance = new ProtocolManager();
                }
            }
        }
        return instance;
    }

    public boolean isLoad() {
        return bLoad;
    }

    public boolean isAutoCalcUpdateFlag() {
        return isAutoCalcUpdateFlag;
    }

    public void setAutoCalcUpdateFlag(boolean autoCalcUpdateFlag) {
        isAutoCalcUpdateFlag = autoCalcUpdateFlag;
    }

    public void close() {
        bLoad = false;
    }

    public static void main(String[] args) {
        ProtocolManager.getInstance().loadProtocolXmlPath("C:\\xml", null);
        ProtocolManager.getInstance().isLoad();
    }

    /*public String getRunJson(String deviceCode) {
        if (TextUtils.isEmpty(deviceCode))
            return null;
        if (deviceCode == null)
            return null;
        if (deviceCode.length() != 16)
            return null;
        byte[] ss = StringUtil.hexString2Bytes(deviceCode);
        if (ss.length != 8)
            return null;
        ByteBuffer bb = ByteBuffer.allocate(ss.length);
        bb.put(ss);
        bb.flip();
        int brandId = bb.getInt();
        int deviceType = bb.getShort() & 0xFFFF;
        int deviceSubType = bb.get() & 0xFF;
        int deviceNumber = bb.get() & 0xFF;
        String json = null;
        try {
            json = ProtocolManager.getInstance().getJsonFormat(String.valueOf(deviceType), String.valueOf(deviceSubType), deviceNumber, (short) 0x0105);
        } catch (Exception e) {
        }
        try {
            if (TextUtils.isEmpty(json)) {
                json = ProtocolManager.getInstance().getJsonFormat(String.valueOf(deviceType), String.valueOf(deviceSubType), deviceNumber, (short) 0x0005);
            }
        } catch (Exception e) {
        }
        return json;
    }

    public String getConfigJson(String deviceCode) {
        if (TextUtils.isEmpty(deviceCode))
            return null;
        if (deviceCode == null)
            return null;
        if (deviceCode.length() != 16)
            return null;
        byte[] ss = StringUtil.hexString2Bytes(deviceCode);
        if (ss.length != 8)
            return null;
        ByteBuffer bb = ByteBuffer.allocate(ss.length);
        bb.put(ss);
        bb.flip();
        int brandId = bb.getInt();
        int deviceType = bb.getShort() & 0xFFFF;
        int deviceSubType = bb.get() & 0xFF;
        int deviceNumber = bb.get() & 0xFF;
        String json = null;
        try {
            json = ProtocolManager.getInstance().getJsonFormat(String.valueOf(deviceType), String.valueOf(deviceSubType), deviceNumber, (short) 0x0104);
        } catch (Exception e) {
        }
        try {
            if (TextUtils.isEmpty(json)) {
                json = ProtocolManager.getInstance().getJsonFormat(String.valueOf(deviceType), String.valueOf(deviceSubType), deviceNumber, (short) 0x4007);
            }
        } catch (Exception e) {
        }
        return json;
    }*/

    public String getRunJson(int productId) {
        String json = null;
        try {
            json = ProtocolManager.getInstance().getJsonFormat(productId, (short) 0x0105);
        } catch (Exception e) {
        }
        try {
            if (TextUtils.isEmpty(json)) {
                json = ProtocolManager.getInstance().getJsonFormat(productId, (short) 0x0005);
            }
        } catch (Exception e) {
        }
        return json;
    }

    public String getConfigJson(int productId) {
        String json = null;
        try {
            json = ProtocolManager.getInstance().getJsonFormat(productId, (short) 0x0104);
        } catch (Exception e) {
        }
        try {
            if (TextUtils.isEmpty(json)) {
                json = ProtocolManager.getInstance().getJsonFormat(productId, (short) 0x4007);
            }
        } catch (Exception e) {
        }
        return json;
    }

    /*public String getJsonFormat(String deviceType, String deviceSubType, Integer dataVersion, short cmd) throws Exception {
        if (deviceSubType == null || deviceSubType.equals(""))
            throw new IllegalArgumentException("deviceSubType is null");
        if (deviceType == null || deviceType.equals(""))
            throw new IllegalArgumentException("deviceType is null");
        if (decoder == null) {
            decoder = new SecondLayerProtocolDecoder();
        }
        decoder = new SecondLayerProtocolDecoder();
        decoder.setProtocolXmlManager(protocolFileLoadManager);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deviceType", deviceType);
        map.put("deviceSubType", deviceSubType);
        map.put("command", cmd);
        map.put("dataVersion", dataVersion == null ? 1 : dataVersion);
        map.put("empty", true);
        HashMap config = decoder.decode(map);
        if (gson == null) {
            gson = getGson();
        }
        return gson.toJson(config);
    }*/

    public String getJsonFormat(int productId, short cmd) throws Exception {
        if (decoder == null) {
            decoder = new SecondLayerProtocolDecoder();
        }
        decoder = new SecondLayerProtocolDecoder();
        decoder.setProtocolXmlManager(protocolFileLoadManager);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("command", cmd);
        map.put("productId",productId);
        HashMap config = decoder.decode(map);
        if (gson == null) {
            gson = getGson();
        }
        return gson.toJson(config);
    }

    /**
     * 从设备获取的二进制数据转换成json抛给上层UI
     *
     * @param packet
     * @return
     * @throws Exception
     */
    public String decode(PacketDataBean packet) throws Exception {
        if (protocolFileLoadManager == null)
            return null;
        if (packet == null)
            throw new IllegalArgumentException("Packet is null...");
        if (packet.getBody() == null)
            throw new IllegalArgumentException("body data is null...");
        if (decoder == null) {
            decoder = new SecondLayerProtocolDecoder();
        }
        decoder = new SecondLayerProtocolDecoder();
        decoder.setProtocolXmlManager(protocolFileLoadManager);
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        map.put("deviceType", packet.getDeviceType() + "");
        map.put("deviceSubType", packet.getDeviceSubType() + "");
        map.put("command", packet.getCommand());
        map.put("data", packet.getBody());
        map.put("dataVersion", packet.getDataVersion() == null ? 1 : packet.getDataVersion());
        addParamToMap(map,packet.getDeviceMac());
        HashMap config = decoder.decode(map);
        if (gson == null) {
            gson = getGson();
        }
        return gson.toJson(config);
    }

    /**
     * 将Json数据根据xml协议封装成二进制数据
     *
     * @param packet
     * @return
     * @throws Exception
     */
    public byte[] encode(PacketDataBean packet) throws Exception {
        if (packet == null)
            throw new IllegalArgumentException("Packet is null...");
        if (protocolFileLoadManager == null)
            return null;
        if (packet.getJson() == null)
            throw new IllegalArgumentException("json data is null...");
        if (protocolFileLoadManager == null) {
            throw new IllegalArgumentException("xml is not load...");
        }
        if (encoder == null) {
            encoder = new SecondLayerProtocolEncoder();
        }
        if (gson == null) {
            gson = getGson();
        }
        encoder.setProtocolXmlManager(protocolFileLoadManager);
        Type type = new TypeToken<TreeMap<String, Object>>() {
        }.getType();
        TreeMap<String, Object> map = gson.fromJson(packet.getJson(), type);
        map.put("command", packet.getCommand());
        map.put("macAddress", packet.getDeviceMac());
        map.put("deviceType", packet.getDeviceType());
        map.put("deviceSubType", packet.getDeviceSubType());
        map.put("dataVersion", packet.getDataVersion() == null ? 1 : packet.getDataVersion());
        addParamToMap(map,packet.getDeviceMac());
        byte[] data = encoder.encode(map);
        return data;
    }


    private void addParamToMap(Map<String, Object> map,String mac){
        if (map==null)
            return;
        if (TextUtils.isEmpty(mac))
            return;
        DeviceProBean device = deviceList.get(mac.toUpperCase());
        if (device == null)
            return;
        map.put("productId",device.getProductId());
    }

    /**
     * 从指定路径加载XML，且存数据库
     *
     * @param path    xml路径
     * @param context Android上下文,可为null(null就不存数据库)
     */
    public void loadProtocolXmlPath(String path, Context context) {
        if (protocolFileLoadManager == null) {
            protocolFileLoadManager = new ProtocolFileLoadManager();
        }
        if (analyzeProtocalXml == null) {
            analyzeProtocalXml = new AnalyzeProtocalXmlImpl();
        }
        protocolFileLoadManager.setContext(context);
        protocolFileLoadManager.setParser(analyzeProtocalXml);
        protocolFileLoadManager.load(path);
        bLoad = true;
    }

    /**
     * 加载Json格式的数据协议
     *
     * @param content
     * @param json
     * @return
     */
    public ProtocolDataModel loadFromJson(Context content, String json) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject == null)
                return null;
            if (jsonObject.has("data")) {
                if (gson == null) {
                    gson = getGson();
                }
                String data = jsonObject.getString("data");
                Type type = new TypeToken<ProtocolDataModel>() {
                }.getType();
                ProtocolDataModel proArr = gson.fromJson(data, type);
                if (proArr == null)
                    return null;
                else {
                    loadFromBean(content, proArr);
                    return proArr;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        bLoad = true;
        return null;
    }

    /**
     * 加载解析好的协议
     *
     * @param content
     * @param proArr
     * @return
     */
    public void loadFromBean(Context content, ProtocolDataModel proArr) {
        if (protocolFileLoadManager == null) {
            protocolFileLoadManager = new ProtocolFileLoadManager();
        }
        if (analyzeProtocalXml == null) {
            analyzeProtocalXml = new AnalyzeProtocalXmlImpl();
        }
        protocolFileLoadManager.setContext(content);
        protocolFileLoadManager.setParser(analyzeProtocalXml);
        protocolFileLoadManager.loadXmlString(proArr);
        bLoad = true;
    }

    /**
     * 根据设备产品ID获取该设备所有协议
     *
     * @param context
     * @param productId
     * @return
     */
    public ProtocolDataModel getProtocolByProductId(Context context, int productId) {
        if (protocolFileLoadManager == null) {
            protocolFileLoadManager = new ProtocolFileLoadManager();
        }
        if (analyzeProtocalXml == null) {
            analyzeProtocalXml = new AnalyzeProtocalXmlImpl();
        }
        protocolFileLoadManager.setContext(context);
        protocolFileLoadManager.setParser(analyzeProtocalXml);
        bLoad = true;
        return protocolFileLoadManager.getProtocolByProductId(productId);
    }

    public void load(Context context) {
        if (protocolFileLoadManager == null) {
            protocolFileLoadManager = new ProtocolFileLoadManager();
        }
        if (analyzeProtocalXml == null) {
            analyzeProtocalXml = new AnalyzeProtocalXmlImpl();
        }
        protocolFileLoadManager.setContext(context);
        protocolFileLoadManager.setParser(analyzeProtocalXml);
        protocolFileLoadManager.loadAll();
        bLoad = true;
    }

    /**
     * 获取协议时间，作比较判断是否需要更新
     *
     * @param context
     * @param productId
     * @return
     */
    public String getProtocolDate(Context context, int productId) {
        if (protocolFileLoadManager == null) {
            protocolFileLoadManager = new ProtocolFileLoadManager();
        }
        if (analyzeProtocalXml == null) {
            analyzeProtocalXml = new AnalyzeProtocalXmlImpl();
        }
        protocolFileLoadManager.setContext(context);
        protocolFileLoadManager.setParser(analyzeProtocalXml);
        bLoad = true;
        return protocolFileLoadManager.getProtocolDate(productId);
    }

    public String getProtocolDate(Context context, Object obj) {
        if (obj == null)
            return null;
        String deviceJson = GsonTool.getInstance().toJson(obj);
        if (TextUtils.isEmpty(deviceJson))
            return null;
        DeviceProBean deviceProBean = GsonTool.getInstance().toObject(deviceJson,DeviceProBean.class);
        if (deviceProBean == null) {
            return null;
        }
        if (!TextUtils.isEmpty(deviceProBean.getMacAddress())) {
            deviceList.put(deviceProBean.getMacAddress().toUpperCase(), deviceProBean);
        }
        //return getProtocolDate(context,deviceProBean.getProductId()); //TODO 此处为了兼容设备协议正常解析 add 20180507 by uuxia
        return "0";
    }
}
