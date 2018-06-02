package com.het.xml.protocol.coder.decode;

import com.het.log.Logc;
import com.het.xml.protocol.coder.DataType;
import com.het.xml.protocol.coder.DataTypeDefinition;
import com.het.xml.protocol.coder.bean.BaseDefinition;
import com.het.xml.protocol.coder.bean.BitDefinition;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.decode.inter.Decoder;
import com.het.xml.protocol.coder.exception.DecodeException;
import com.het.xml.protocol.coder.exception.IllegalAttributeValue;
import com.het.xml.protocol.coder.parse.ProtocolFileLoadManager;
import com.het.xml.protocol.coder.utils.BeanUtils;
import com.het.xml.protocol.coder.utils.BinaryConvertUtils;
import com.het.xml.protocol.coder.utils.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据包解码器
 *
 * @Original jake  @improver uuxia
 */
public abstract class AbstractDecoder implements Decoder {
    private static final Map<String, String> methodNameMap = new HashMap<String, String>();

    static {
        methodNameMap.put("1-char", "Char");
        methodNameMap.put("1-number", "UnsignedByte");
        methodNameMap.put("2-number", "UnsignedShort");
        methodNameMap.put("4-number", "Int");
        methodNameMap.put("4-float", "Float");
        methodNameMap.put("8-number", "Long");
        methodNameMap.put("8-float", "Double");
    }

    /**
     * 协议管理器
     */
    protected ProtocolFileLoadManager protocolXmlManager;

    public void setProtocolXmlManager(ProtocolFileLoadManager protocolXmlManager) {
        this.protocolXmlManager = protocolXmlManager;
    }

    public abstract <T> T decode(Object data) throws Exception;

    public <T> T decode(ProtocolDefinition protocolDefinition, byte[] data) throws Exception {
        if (data == null || protocolDefinition == null) {
            throw new IllegalArgumentException("argument can't be null,please check...");
        }
        //检查协议定义是否完整
        check(protocolDefinition);
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "[{}-protocolID:{}]-start decoding data package..." + protocolDefinition.getDescription() + " " + protocolDefinition.getId());
        Class instanceClass = null;
        if (protocolDefinition.getClassName() == null || "".equals(protocolDefinition.getClassName())) {
            instanceClass = Class.forName("java.util.HashMap");
        } else {
            try {
                instanceClass = Class.forName(protocolDefinition.getClassName());
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("[protocolID:" + protocolDefinition.getId() + "]-can't find class\"" + protocolDefinition.getClassName() + "\"");
            }
        }
        //数据储存对象
        Object instance = null;
        try {
            instance = instanceClass.newInstance();
        } catch (Exception e) {
            throw new DecodeException("[protocolID:" + protocolDefinition.getId() + "]-can't instance\"" + protocolDefinition.getClassName() + "\"");
        }

        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));

        Object value = null;
        //获取解析规则
        List<ByteDefinition> rule = protocolDefinition.getByteDefList();
        int count = 0;
        for (ByteDefinition element : rule) {
            //如果有定义bit运算,默认读取一个字节
            if (element.getBitDefList() != null) {
                element.setJavaType("BYTE");
                element.setIgnore(true);
            }
            DataType dataType = DataTypeDefinition.getDataType(element.getJavaType());
            if (dataType == null) {
                throw new IllegalAttributeValue("[protocolID:" + protocolDefinition.getId() + " property:" + element.getProperty() + "]-can't support \"" + element.getJavaType() + "\" data type,please check the \"JavaType\" value");
            }
            switch (dataType) {
                //add start by uuxia 2017-05-23 19:03
                case LONG:
                case INTEGER:
                    try {
                        value = readByteByDefinition(element, inputStream, dataType, instance);
                    } catch (Exception e1) {
                        throw new DecodeException("[property:" + element.getProperty() + "]-" + e1.getMessage());
                    }
                    Integer mulriple = element.getMulriple();
                    if (mulriple != null) {
                        Double dValue = Double.parseDouble(value.toString());
                        if (dValue != null) {
                            dValue /= mulriple;
                            int num = mulriple / 10;
                            String f = String.valueOf(num);
                            int len = f.length();
                            if (num > 0) {
                                value = String.format("%." + len + "f", dValue);
                            }
                        }
                    }
                    setValue(instance, value, element);
                    break;
                //add end by uuxia 2017-05-23 19:04
                case CHAR:
                case BYTE:
                case SHORT:
                case FLOAT:
                case DOUBLE:
                    try {
                        value = readByteByDefinition(element, inputStream, dataType, instance);
                    } catch (Exception e1) {
                        throw new DecodeException("[property:" + element.getProperty() + "]-" + e1.getMessage());
                    }
                    setValue(instance, value, element);
                    break;
                case STRING:
                    try {
                        value = readByteByDefinition(element, inputStream, dataType, instance);
                    } catch (Exception e1) {
                        throw new DecodeException("[property:" + element.getProperty() + "]-" + e1.getMessage());
                    }
                    String strValue = new String((byte[]) value, "UTF-8");
                    setValue(instance, strValue.trim(), element);
                    break;
                case HEX_STRING:
                    try {
                        value = (byte[]) readByteByDefinition(element, inputStream, dataType, instance);
                    } catch (Exception e1) {
                        throw new DecodeException("[property:" + element.getProperty() + "]-" + e1.getMessage());
                    }
                    setValue(instance, StringUtil.byteArrayToHexString((byte[]) value), element);
                    break;
                case BYTE_ARRAY:
                    try {
                        value = readByteByDefinition(element, inputStream, dataType, instance);
                    } catch (Exception e1) {
                        throw new DecodeException("[property:" + element.getProperty() + "]-" + e1.getMessage());
                    }
                    setValue(instance, value, element);
                    break;
                default:
                    Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "[protocolID:" + protocolDefinition.getId() + " property:" + element.getProperty() + "]-does't support the \"JavaType\" {}" + element.getJavaType());
                    break;
            }
            Object bitVal = null;
            if (element.getBitDefList() != null) {
                for (BitDefinition bitDef : element.getBitDefList()) {
                    try {
                        bitVal = readBitByDefiniton(bitDef, (Integer) BinaryConvertUtils.convertNumberToTargetClass((Number) value, Integer.class));
                    } catch (Exception e) {
                        throw new DecodeException("[property:" + bitDef.getProperty() + "]-" + e.getMessage());
                    }
                    setValue(instance, bitVal, bitDef);
                }
            }
            count++;
        }


        return (T) instance;
    }


    private void setValue(Object instance, Object value, BaseDefinition definition) throws IllegalAccessException, InvocationTargetException {
        if (!definition.isIgnore()) {//是否忽略该属性
            if (instance instanceof Map) {
                definition.setValue(value);
                ((Map) instance).put(definition.getProperty(), value);
            } else {
                BeanUtils.setProperty(instance, definition.getProperty(), value);
            }
        }

    }

    /**
     * 按照 ByteDefinition 中定义读取数据
     *
     * @param byteDefinition
     * @param inputStream
     * @param instance       数据储存对象
     * @param dataType       数据类型
     * @return
     */
    private Object readByteByDefinition(ByteDefinition byteDefinition, DataInputStream inputStream, DataType dataType, Object instance) throws Exception {
        Object value = null;
        String methodName = "read";
        //如果refValue引用值不为空，则给length
        if (!StringUtil.isEmpty(byteDefinition.getRefValue())) {
            String proptery;
            Integer length = 0;
            //如果是算数表达式
            //只能任何字类字符加‘+’或者‘-’开始且只能一位，以数字结束
            if (byteDefinition.getRefValue().matches("^\\w+[+|-]{1}\\d+$")) {
                String[] tempArr;
                if (byteDefinition.getRefValue().contains("+")) {
                    tempArr = byteDefinition.getRefValue().split("\\+");
                    String val = BeanUtils.getProperty(instance, tempArr[0].trim());
                    length = Integer.valueOf(val) + Integer.valueOf(tempArr[1]);
                } else if (byteDefinition.getRefValue().contains("-")) {
                    tempArr = byteDefinition.getRefValue().split("-");
                    String val = BeanUtils.getProperty(instance, tempArr[0].trim());
                    length = Integer.valueOf(val) - Integer.valueOf(tempArr[1]);
                }
            } else if (byteDefinition.getRefValue().matches("^\\d+[+|-]{1}\\w+$")) {
                //只能数字结束且只能一位，以数字结束
                String[] tempArr;
                if (byteDefinition.getRefValue().contains("+")) {
                    tempArr = byteDefinition.getRefValue().split("\\+");
                    String val = BeanUtils.getProperty(instance, tempArr[1].trim());
                    length = Integer.valueOf(val) + Integer.valueOf(tempArr[0]);
                } else if (byteDefinition.getRefValue().contains("-")) {
                    tempArr = byteDefinition.getRefValue().split("-");
                    String val = BeanUtils.getProperty(instance, tempArr[1].trim());
                    length = Integer.valueOf(val) - Integer.valueOf(tempArr[0]);
                }
            } else {
//                String val = BeanUtils.getProperty(instance, byteDefinition.getRefValue());
//                length = Integer.valueOf(val);
                short dataLen = (Short) ((Map) instance).get(byteDefinition.getRefValue());
                length = (int) dataLen;
            }
            byteDefinition.setLength(length);
        }
        if (StringUtil.isEmpty(byteDefinition.getJavaType())) {
            throw new IllegalAttributeValue("the \"JavaType\" value can't be null");
        }

        //如果不是基本数据
        if (dataType.getSize() == 0) {
            if (byteDefinition.getLength() == null) {
                throw new IllegalAttributeValue("if the \"javaType\" value is not primitive data type, \"length\" value can't be null or zero");
            }
            byte[] bytes = new byte[byteDefinition.getLength()];
            DataInputStream.class.getMethod(methodName, byte[].class).invoke(inputStream, bytes);
            value = bytes;
        } else {//处理基本数据类型
            if (byteDefinition.getLength() != null && (dataType.getSize() < byteDefinition.getLength())) {
                throw new IllegalAttributeValue("the \"length\" value does't match \"javaType\" value");
            }
            methodName = getMethodName(byteDefinition, dataType);
            //如果javaType定义的是number类型，但length取值不规范(比如：3,5,6,7-像这种既不符合int的字节长度也不符合long的字节长度)
            if ("read".equals(methodName)) {
                int len = byteDefinition.getLength();
                byte[] bytes = new byte[len];
                DataInputStream.class.getMethod(methodName, byte[].class).invoke(inputStream, bytes);
                long lo = BinaryConvertUtils.byteArrayToLong(bytes);
                value = lo;
                if (len % 2 == 0) {
                    if (len == 2) {
                        short sh = (short) lo;
                        value = sh;
                    } else if (len == 4) {
                        int in = (int) lo;
                        value = in;
                    } else {
                        value = lo;
                    }
                }
            } else {
                value = DataInputStream.class.getMethod(methodName).invoke(inputStream);
            }
        }
        return value;
    }

    /**
     * 根据字节的bit定义读取值
     *
     * @param bitDefinition
     * @param val
     * @return
     * @throws Exception
     */
    private Object readBitByDefiniton(BitDefinition bitDefinition, int val) throws Exception {
        Object value = null;
        int hexArr[] = new int[]{0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F, 0xFF};
        Integer length = bitDefinition.getLength();
        if (length == null || length <= 0) {
            throw new IllegalAttributeValue("label \"bitDef\" does't set \"length\" value");
        }
        if (bitDefinition.getShift() == null) {
            throw new IllegalAttributeValue("label \"bitDef\" does't set \"shift\" value");
        }
        value = (val >>> bitDefinition.getShift()) & hexArr[length - 1];
        return value;
    }

    /**
     * 获取数据流读取方法名
     *
     * @param definition
     * @param dataType
     * @return
     */
    private String getMethodName(BaseDefinition definition, DataType dataType) {
        String methodName = null;
        //key:length-数据类别
        String type = null;
        //如果定义了读取长度，则按定义的长度进行
        if (definition.getLength() != null && definition.getLength() > 0) {
            String key = definition.getLength() + "-" + dataType.getCategory();
            if ("number".equals(dataType.getCategory())) {
                if (dataType.getSize() == definition.getLength()) {
                    type = methodNameMap.get(key);
                }
            } else {
                type = methodNameMap.get(key);
            }

        } else {//否则，按协议中定义的数据类型
            type = dataType.toString();
        }
        methodName = "read" + (type == null ? "" : type);
        return methodName;
    }

    private void check(ProtocolDefinition protocolDefinition) throws DecodeException {
        if (protocolDefinition.getId() == null || "".equals(protocolDefinition.getId())) {
            throw new DecodeException("[protocolID:" + protocolDefinition.getId() + "]-the value of label \"id\" can't be null");
        }
        if (protocolDefinition.getByteDefList() == null || protocolDefinition.getByteDefList().size() == 0) {
            throw new DecodeException("[protocolID:" + protocolDefinition.getId() + "]-the value of label \"definitions\" can't be null");
        }
    }

}
