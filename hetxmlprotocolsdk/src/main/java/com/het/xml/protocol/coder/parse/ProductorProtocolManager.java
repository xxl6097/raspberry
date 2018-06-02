package com.het.xml.protocol.coder.parse;


import com.het.log.Logc;
import com.het.xml.protocol.coder.DataTypeDefinition;
import com.het.xml.protocol.coder.ProtocolMode;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.thoughtworks.xstream.XStreamException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 厂商自定义协议管理
 *
 * @author jake
 */
public class ProductorProtocolManager extends ProtocolFileLoadManager {

    //生成模式下的协议(key:developerID)
    private final HashMap<String, ProductorProtocolList> productModelMapper = new HashMap<String, ProductorProtocolList>(50);
    //开发模式下的协议(key:developerID)
    private final HashMap<String, ProductorProtocolList> developModelMapper = new HashMap<String, ProductorProtocolList>(50);

    private ReentrantReadWriteLock productLock = new ReentrantReadWriteLock();

    private ReentrantReadWriteLock developLock = new ReentrantReadWriteLock();

    /**
     * 获取协议
     *
     * @param developerID
     * @param protocolID
     * @param mode        0开发模式 1 生产模式
     * @return
     */
    public ProtocolDefinition get(String developerID, String protocolID, int mode) {
        if (developerID == null || protocolID == null) {
            throw new IllegalArgumentException("developerID或者protocolID参数不能为NULL");
        }
        ProtocolDefinition definition = null;
        ProductorProtocolList protocolList = null;
        if (mode == ProtocolMode.DEVELOP_MODE) {
            protocolList = getFromDevelopModelList(developerID);
            if (protocolList != null) {
                definition = protocolList.get(protocolID);
            }
        } else if (mode == ProtocolMode.PRODUCT_MODE) {
            protocolList = getFromProductModelList(developerID);
            if (protocolList != null) {
                definition = protocolList.get(protocolID);
            }
        }
        return definition;
    }


    private ProductorProtocolList getFromDevelopModelList(String developerID) {
        ReadLock readLock = developLock.readLock();
        readLock.lock();
        try {
            return developModelMapper.get(developerID.toUpperCase());
        } finally {
            readLock.unlock();
        }
    }

    private ProductorProtocolList getFromProductModelList(String developerID) {
        ReadLock readLock = productLock.readLock();
        readLock.lock();
        try {
            return productModelMapper.get(developerID.toUpperCase());
        } finally {
            readLock.unlock();
        }
    }

    /**
     * @param developerID 开发者ID
     * @param definition  设备协议
     * @param model       协议模式：0开发模式 ,1生产模式
     */
    private void put(String developerID, String protocolID, Object definition, int model) {
        if (developerID == null || definition == null) {
            throw new IllegalArgumentException("参数不能为NULL");
        }
        if (model == ProtocolMode.DEVELOP_MODE) {
            saveInDevelopModelList(developerID, protocolID, definition);
        } else if (model == ProtocolMode.PRODUCT_MODE) {
            saveInProductModelList(developerID, protocolID, definition);
        }
    }

    /**
     * 保存协议到开发模式列表中
     *
     * @param developerID
     * @param definition
     */
    private void saveInDevelopModelList(String developerID, String protocolID, Object definition) {
        if (developerID == null || definition == null) {
            throw new IllegalArgumentException("参数不能为NULL");
        }
        WriteLock writeLock = developLock.writeLock();
        writeLock.lock();
        try {
            developerID = developerID.toUpperCase();
            ProductorProtocolList protocolList = developModelMapper.get(developerID);
            if (protocolList == null) {
                protocolList = new ProductorProtocolList();
                developModelMapper.put(developerID, protocolList);
            }
            protocolList.put(protocolID, definition);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 保存协议至生成模式列表中
     *
     * @param developerID
     * @param definition
     */
    private void saveInProductModelList(String developerID, String protocolID, Object definition) {
        if (developerID == null || definition == null) {
            throw new IllegalArgumentException("参数不能为NULL");
        }
        WriteLock writeLock = productLock.writeLock();
        writeLock.lock();
        try {
            developerID = developerID.toUpperCase();
            ProductorProtocolList protocolList = productModelMapper.get(developerID);
            if (protocolList == null) {
                protocolList = new ProductorProtocolList();
                productModelMapper.put(developerID, protocolList);
            }
            protocolList.put(protocolID, definition);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 加载厂商协议
     *
     * @param xml
     */
    public ProtocolDefinition load(String xml, String developerID, String protocolID, int mode) {
        ProtocolDefinition definition = null;
        try {
            definition = (ProtocolDefinition) getParser().paseXML(xml);
            int packetSize = calculate(definition);
            definition.setPacketSize(packetSize);
            put(developerID, protocolID, definition, mode);
        } catch (XStreamException e) {
            e.printStackTrace();
        }
        Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[开发者ID：{}   协议ID：{}]-{}协议加载成功" + developerID + protocolID + (mode == 0 ? "开发模式" : "生产模式"));
        return definition;
    }

    /**
     * 计算协议定义中数据包总字节数
     *
     * @param protocolDenifition
     * @return
     */
    private int calculate(ProtocolDefinition protocolDenifition) {
        int size = 0;
        if (protocolDenifition != null) {
            List<ByteDefinition> byteList = protocolDenifition.getByteDefList();
            if (byteList != null) {
                for (ByteDefinition byteDef : byteList) {
                    if (byteDef.getBitDefList() != null) {
                        size += 1;
                    } else {
                        size += (byteDef.getLength() != null ? byteDef.getLength() : DataTypeDefinition.getDataType(byteDef.getJavaType()).getSize());
                    }
                }
            }
        }
        return size;
    }

    /**
     * 删除
     *
     * @param developerID
     * @param protocolID
     */
    public void remove(String developerID, String protocolID) {
        try {
            Object obj = removeFromDevelopModelList(developerID, protocolID);
            if (obj == null) {
                obj = removeFromProductModelList(developerID, protocolID);
            }
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[开发者ID:{} 协议ID:{}]-移除开发者协议{}" + developerID + protocolID + (obj == null ? "失败" : "成功"));
        } catch (Exception e) {
        }

    }

    /**
     * 删除
     *
     * @param developerID
     * @param protocolID
     */
    public void remove(String developerID, String protocolID, int mode) {
        try {
            Object obj = null;
            if (mode == 0) {
                obj = removeFromDevelopModelList(developerID, protocolID);
            } else if (mode == 1) {
                obj = removeFromProductModelList(developerID, protocolID);
            }
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[开发者ID:{} 协议ID:{}]-移除{}协议{}" + developerID + protocolID + (mode == 0 ? "开发模式" : "生产模式") + (obj == null ? "失败" : "成功"));
        } catch (Exception e) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"[开发者ID:{} 协议ID:{}]-异常：移除开发者协议失败(失败原因：{})" + new Object[]{developerID, protocolID, e.getMessage()});
        }

    }

    /**
     * 移除开发模式协议列表中的协议
     *
     * @param developerID
     * @param protocolID
     */
    private Object removeFromDevelopModelList(String developerID, String protocolID) {
        Object definition = null;
        ProductorProtocolList protocolList = null;
        try {
            protocolList = getFromDevelopModelList(developerID);
            if (protocolList != null) {
                definition = protocolList.remove(protocolID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }

    /**
     * 从生产模式下的协议列表中移除协议
     *
     * @param developerID
     * @param protocolID
     */
    private Object removeFromProductModelList(String developerID, String protocolID) {
        Object definition = null;
        ProductorProtocolList protocolList = null;
        try {
            protocolList = getFromProductModelList(developerID);
            if (protocolList != null) {
                definition = protocolList.remove(protocolID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }
}
