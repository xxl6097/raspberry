package com.het.xml.protocol.coder;

import java.util.HashMap;


/**
 * 数据类型定义
 *
 * @author jake
 */
public class DataTypeDefinition {
    private static final HashMap<String, DataType> dataTypeClassMap = new HashMap<String, DataType>();

    static {
        dataTypeClassMap.put("CHAR", DataType.CHAR);
        dataTypeClassMap.put("SHORT", DataType.SHORT);
        dataTypeClassMap.put("INTEGER", DataType.INTEGER);
        dataTypeClassMap.put("STRING", DataType.STRING);
        dataTypeClassMap.put("BYTE", DataType.BYTE);
        dataTypeClassMap.put("LONG", DataType.LONG);
        dataTypeClassMap.put("FLOAT", DataType.FLOAT);
        dataTypeClassMap.put("DOUBLE", DataType.DOUBLE);
        dataTypeClassMap.put("HEXSTRING", DataType.HEX_STRING);
        dataTypeClassMap.put("BYTEARRAY", DataType.BYTE_ARRAY);
    }

    /**
     * 获取数据类型Class对象
     *
     * @param dataType
     * @return
     */
    public static DataType getDataType(String dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Argument can't be null,please check...");
        }
        return dataTypeClassMap.get(dataType.toUpperCase());
    }


}
