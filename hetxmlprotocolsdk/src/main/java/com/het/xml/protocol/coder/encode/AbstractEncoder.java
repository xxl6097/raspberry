package com.het.xml.protocol.coder.encode;


import com.het.xml.protocol.coder.DataType;
import com.het.xml.protocol.coder.DataTypeDefinition;
import com.het.xml.protocol.coder.bean.BaseDefinition;
import com.het.xml.protocol.coder.bean.BitDefinition;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.encode.inter.Encoder;
import com.het.xml.protocol.coder.exception.EncodeException;
import com.het.xml.protocol.coder.exception.IllegalAttributeValue;
import com.het.xml.protocol.coder.parse.ProtocolFileLoadManager;
import com.het.xml.protocol.coder.utils.BeanUtils;
import com.het.xml.protocol.coder.utils.BinaryConvertUtils;
import com.het.xml.protocol.coder.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据包编码器
 *
 * @author jake
 */
public abstract class AbstractEncoder implements Encoder {
    private static final Map<String, Class> pramamterTypeMap = new HashMap<String, Class>();
    //key:length-数据类别
    private static final Map<String, DataType> dataTypeMap = new HashMap<String, DataType>();

    static {
        pramamterTypeMap.put("1-char", Integer.class);
        pramamterTypeMap.put("1-number", Integer.class);
        pramamterTypeMap.put("2-number", Integer.class);
        pramamterTypeMap.put("4-number", Integer.class);
        pramamterTypeMap.put("4-float", Float.class);
        pramamterTypeMap.put("8-number", Long.class);
        pramamterTypeMap.put("8-float", Double.class);

        dataTypeMap.put("1-char", DataType.CHAR);
        dataTypeMap.put("1-number", DataType.BYTE);
        dataTypeMap.put("2-number", DataType.SHORT);
        dataTypeMap.put("4-number", DataType.INTEGER);
        dataTypeMap.put("4-float", DataType.FLOAT);
        dataTypeMap.put("8-number", DataType.LONG);
        dataTypeMap.put("8-float", DataType.DOUBLE);
    }

    /**
     * 协议管理器
     */
    protected ProtocolFileLoadManager protocolXmlManager;

    public void setProtocolXmlManager(ProtocolFileLoadManager protocolXmlManager) {
        this.protocolXmlManager = protocolXmlManager;
    }

    /**
     * 将对象编码转为byte数组
     *
     * @param data 需要编码的对象
     * @return
     * @throws Exception
     */
    public abstract byte[] encode(Object data) throws Exception;

//    public abstract byte[] encode(Object key, Object data) throws Exception;

    public byte[] encode(ProtocolDefinition protocolDefinition, Object data) throws Exception {
        if (data == null || protocolDefinition == null) {
            throw new IllegalArgumentException("argument can't be null,please check...");
        }
        if (protocolDefinition.getClassName() == null || "".equals(protocolDefinition.getClassName())) {
            protocolDefinition.setClassName("java.util.HashMap");
        }
//        if (!protocolDefinition.getClassName().equals(data.getClass().getName())) {
//            throw new IllegalAttributeValue("[protocolID:" + protocolDefinition.getId() + "]-protocol config object \"className\" value is not correct or the parameter of \"data\" is suspicious");
//        }
        //检查协议定义是否完整
        check(protocolDefinition);
        //Logc.e(Logc.HetReportTag.WIFI_EX_LOG,"[{}-protocolID：{}]-start encoding data package..." + protocolDefinition.getDescription() + protocolDefinition.getId());
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream(50);
        DataOutputStream outputStream = new DataOutputStream(byteArr);
        Object value = null;
        //获取解析规则
        List<ByteDefinition> rule = protocolDefinition.getByteDefList();
        for (ByteDefinition element : rule) {
            //如果定义了字节中的bit运算,则
            if (element.getBitDefList() != null) {
                String propertyValue = "";
                int val = 0;
                int byteValue = 0;
                for (BitDefinition bit : element.getBitDefList()) {
                    if (!bit.isIgnore()) {
                        propertyValue = getValue(data, bit.getProperty()).toString();
                        val = Integer.parseInt(propertyValue);
                        byteValue += writeBitByDefiniton(bit, val);
                    }
                }
                outputStream.writeByte(byteValue);
            } else {//如果没有定义，则做字节处理
                //如果有定义bit运算,默认读取一个字节
                DataType dataType = DataTypeDefinition.getDataType(element.getJavaType());
                if (dataType == null) {
                    throw new IllegalAttributeValue("[protocolID:" + protocolDefinition.getId() + " property:" + element.getProperty() + "]-can't support \"" + element.getJavaType() + "\" data type,please check the \"JavaType\" value");
                }
                try {
                    writeByteByDefinition(element, outputStream, dataType, data);
                } catch (Exception e) {
                    throw new EncodeException("[property:" + element.getProperty() + "]-" + e.getMessage());
                }
            }


        }

        return byteArr.toByteArray();
    }

    /**
     * 按照 ByteDefinition 中定义读取数据
     *
     * @param byteDefinition
     * @param outputStream
     * @param instance       数据储存对象
     * @param dataType       数据类型
     * @return
     */
    private void writeByteByDefinition(ByteDefinition byteDefinition, DataOutputStream outputStream, DataType dataType, Object instance) throws Exception {
        Object value = null;
        //如果refValue引用值不为空，则给length
        if (!StringUtil.isEmpty(byteDefinition.getRefValue())) {
            String proptery;
            Integer length = 0;
            //如果是算数表达式
            if (byteDefinition.getRefValue().matches("^\\w+[+|-]{1}\\d+$")) {
                String[] tempArr;
                if (byteDefinition.getRefValue().contains("+")) {
                    tempArr = byteDefinition.getRefValue().split("\\+");
                    String val = getValue(instance, tempArr[0].trim()).toString();
                    length = Integer.valueOf(val) + Integer.valueOf(tempArr[1]);
                } else if (byteDefinition.getRefValue().contains("-")) {
                    tempArr = byteDefinition.getRefValue().split("-");
                    String val = getValue(instance, tempArr[0].trim()).toString();
                    length = Integer.valueOf(val) - Integer.valueOf(tempArr[1]);
                }
            } else if (byteDefinition.getRefValue().matches("^\\d+$[+|-]{1}\\w+$")) {
                String[] tempArr;
                if (byteDefinition.getRefValue().contains("+")) {
                    tempArr = byteDefinition.getRefValue().split("\\+");
                    String val = getValue(instance, tempArr[1].trim()).toString();
                    length = Integer.valueOf(val) + Integer.valueOf(tempArr[0]);
                } else if (byteDefinition.getRefValue().contains("-")) {
                    tempArr = byteDefinition.getRefValue().split("-");
                    String val = getValue(instance, tempArr[1].trim()).toString();
                    length = Integer.valueOf(val) - Integer.valueOf(tempArr[0]);
                }
            } else {
                String val = getValue(instance, byteDefinition.getRefValue()).toString();
                //如果是小数，就去掉后面的小数
                if (val.matches("\\d+\\.\\d+$")) {
                    val = val.replaceAll("\\.\\d+$", "");
                }
                length = Integer.valueOf(val);
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
        }
        String propertyValue = null;
        //如果处理的不是byte数组
        if (dataType != DataType.BYTE_ARRAY && !byteDefinition.isIgnore()) {
            propertyValue = getValue(instance, byteDefinition.getProperty()).toString();
        }
        DataType realDataType = getRealDataType(byteDefinition, dataType);
        //如果javaType定义的是非基本数据类型，或者number类型，但length取值不规范(比如：3,5,6,7-像这种既不符合int的字节长度也不符合long的字节长度)
        if (realDataType == null) {
            byte temp[];
            byte[] bytes;
            switch (dataType) {
                case INTEGER:
                case LONG:
                    //add start by uuxia 2017/05/23 18:27
                    Integer mulriple = byteDefinition.getMulriple();
                    //add end by uuxia 2017/05/23 18:28
                    if (!byteDefinition.isIgnore()) {
                        //如果是小数，就去掉后面的小数
                        //add start by uuxia 2017/05/23 18:28
                        if (mulriple != null) {
                            Double dValue = Double.parseDouble(propertyValue);
                            if (mulriple == 0) {
                                mulriple = 1;
                            }
                            dValue *= mulriple;
                            long val = dValue.longValue();
                            bytes = BinaryConvertUtils.longToByteArray(val, byteDefinition.getLength());
                            outputStream.write(bytes);
                            //add end by uuxia 2017/05/23 18:37
                        } else {
                            if (propertyValue.matches("\\d+\\.\\d+$")) {
                                propertyValue = propertyValue.replaceAll("\\.\\d+$", "");
                            }
                            long val = Long.parseLong(propertyValue);
                            bytes = BinaryConvertUtils.longToByteArray(val, byteDefinition.getLength());
                            outputStream.write(bytes);
                        }
                    } else {
                        outputStream.write(new byte[byteDefinition.getLength()]);
                    }
                    break;
                case STRING:
                    if (!byteDefinition.isIgnore()) {
                        temp = propertyValue.getBytes("UTF-8");
                        bytes = new byte[byteDefinition.getLength()];
                        //add uuxia bytes.length -- > temp.length
                        int len = bytes.length;
                        if (temp != null && temp.length <= bytes.length) {
                            len = temp.length;
                        }
                        System.arraycopy(temp, 0, bytes, 0, len);
                        outputStream.write(bytes);
                    } else {
                        outputStream.write(new byte[byteDefinition.getLength()]);
                    }
                    break;
                case HEX_STRING:
                    if (!byteDefinition.isIgnore()) {
                        temp = StringUtil.hexStringToByteArray(propertyValue);
                        bytes = new byte[byteDefinition.getLength()];
                        int len = bytes.length;
                        if (temp != null && temp.length <= bytes.length) {
                            len = temp.length;
                        }
                        System.arraycopy(temp, 0, bytes, 0, len);
                        outputStream.write(bytes);
                    } else {
                        outputStream.write(new byte[byteDefinition.getLength()]);
                    }
                    break;
                case BYTE_ARRAY:
                    if (!byteDefinition.isIgnore()) {
                        Object data = getValue(instance, byteDefinition.getProperty());
//                        Method method = instance.getClass().getMethod("get" + StringUtil.capitalize(byteDefinition.getProperty()));
//                        if (method == null) {
//                            method = instance.getClass().getMethod("is" + StringUtil.capitalize(byteDefinition.getProperty()));
//                        }
//                        bytes = (byte[]) method.invoke(instance);
                        if (data != null) {
                            outputStream.write((byte[]) data);
                        }
                    } else {
                        outputStream.write(new byte[byteDefinition.getLength()]);
                    }
                    break;
                default:
                    throw new EncodeException("does't support the \"JavaType\" " + byteDefinition.getJavaType());
            }

        } else {
            if (byteDefinition.getLength() != null && (dataType.getSize() < byteDefinition.getLength())) {
                throw new IllegalAttributeValue("the \"length\" value does't match \"javaType\" value");
            }

            switch (realDataType) {
                case CHAR:
                    if (!byteDefinition.isIgnore()) {
                        outputStream.writeChar(propertyValue.toCharArray()[0]);
                    } else {
                        outputStream.writeChar(0);
                    }
                    break;
                case BYTE:
                    if (!byteDefinition.isIgnore()) {
                        Double dValue = Double.parseDouble(propertyValue);
                        byte bValue = dValue.byteValue();//Integer.valueOf(propertyValue)
                        outputStream.writeByte(bValue);
                    } else {
                        outputStream.writeByte(0);
                    }
                    break;
                case SHORT:
                    if (!byteDefinition.isIgnore()) {
                        Double dValue = Double.parseDouble(propertyValue);
                        short sValue = dValue.shortValue();//Integer.valueOf(propertyValue)
                        outputStream.writeShort(sValue);
                    } else {
                        outputStream.writeShort(0);
                    }
                    break;
                case INTEGER:
                    if (!byteDefinition.isIgnore()) {
                        //add start by uuxia 2017/05/23 18:38
                        Integer mulriple = byteDefinition.getMulriple();
                        if (mulriple != null) {
                            Double dValue = Double.parseDouble(propertyValue);
                            if (mulriple == 0) {
                                mulriple = 1;
                            }
                            dValue *= mulriple;
                            long val = dValue.longValue();
                            byte[] bytes = BinaryConvertUtils.longToByteArray(val, byteDefinition.getLength());
                            outputStream.write(bytes);
                            //add end by uuxia 2017/05/23 18:37
                        } else {
                            Double dValue = Double.parseDouble(propertyValue);
                            long val = dValue.longValue();//Long.parseLong(propertyValue);
                            byte[] bytes = BinaryConvertUtils.longToByteArray(val, byteDefinition.getLength());
                            outputStream.write(bytes);
                        }
                    } else {
                        outputStream.writeInt(0);
                    }
                    break;
                case LONG:
                    if (!byteDefinition.isIgnore()) {
                        //add start by uuxia 2017/05/23 18:42
                        Integer mulriple = byteDefinition.getMulriple();
                        if (mulriple != null) {
                            boolean isInterger = StringUtil.isInteger(propertyValue);
                            Double dValue = Double.parseDouble(propertyValue);
                            if (mulriple == 0) {
                                mulriple = 1;
                            }
                            dValue *= mulriple;
                            long lvalue = dValue.longValue();//Long.valueOf(propertyValue)
                            if (isInterger) {
                                outputStream.writeLong(lvalue);
                            } else {
                                outputStream.writeDouble(dValue);
                            }
                            //add end by uuxia 2017/05/23 18:43
                        } else {
                            boolean isInterger = StringUtil.isInteger(propertyValue);
                            Double dValue = Double.parseDouble(propertyValue);
                            long lvalue = dValue.longValue();//Long.valueOf(propertyValue)
                            if (isInterger) {
                                outputStream.writeLong(lvalue);
                            } else {
                                outputStream.writeDouble(dValue);
                            }
                        }
                    } else {
                        outputStream.writeLong(0);
                    }
                    break;
                case FLOAT:
                    if (!byteDefinition.isIgnore()) {
                        Double dValue = Double.parseDouble(propertyValue);
                        float fValue = dValue.floatValue();//Float.valueOf(propertyValue)
                        outputStream.writeFloat(fValue);
                    } else {
                        outputStream.writeFloat(0);
                    }
                    break;
                case DOUBLE:
                    if (!byteDefinition.isIgnore()) {
                        Double dValue = Double.parseDouble(propertyValue);
                        double dVa = dValue;//Double.valueOf(propertyValue)
                        outputStream.writeDouble(dVa);
                    } else {
                        outputStream.writeDouble(0);
                    }
                    break;
                default:
                    throw new EncodeException("does't support the \"JavaType\" " + byteDefinition.getJavaType());
            }
        }
    }

    /**
     * 根据字节的bit定义读取值
     *
     * @param bitDefinition
     * @param val
     * @return
     * @throws Exception
     */
    private int writeBitByDefiniton(BitDefinition bitDefinition, int val) throws Exception {
        int value = 0;
        Integer length = bitDefinition.getLength();
        if (length == null || length <= 0) {
            throw new IllegalAttributeValue("[property:" + bitDefinition.getProperty() + "]-label \"bitDef\" does't set \"length\" value");
        }
        if (bitDefinition.getShift() == null) {
            throw new IllegalAttributeValue("[property:" + bitDefinition.getProperty() + "]-label \"bitDef\" does't set \"shift\" value");
        }
        value = val << bitDefinition.getShift() & 0xFF;
        return value;
    }


    private Class getParameterType(BaseDefinition definition, DataType dataType) {
        Class parameterType;
        //如果定义了读取长度，则按定义的长度进行
        if (definition.getLength() != null && definition.getLength() > 0) {
            String key = definition.getLength() + "-" + dataType.getCategory();
            parameterType = pramamterTypeMap.get(key);
        } else {//否则，按协议中定义的数据类型
            String key = dataType.getSize() + "-" + dataType.getCategory();
            parameterType = pramamterTypeMap.get(key);
        }
        return parameterType;
    }

    /**
     * 获取合适的数据类型
     *
     * @param definition
     * @param dataType
     * @return
     */
    private DataType getRealDataType(BaseDefinition definition, DataType dataType) {
        DataType type;
//		if(definition.getLength()!=null && definition.getLength()>0){
        //如果定义了读取长度，则按定义的长度进行(2015-6-19号修改)
        if (definition.getLength() != null) {
            String key = definition.getLength() + "-" + dataType.getCategory();
            type = dataTypeMap.get(key);
        } else {//否则，按协议中定义的数据类型
            type = dataType;
        }
        return type;
    }

    private void check(ProtocolDefinition protocolDefinition) throws EncodeException {
        if (protocolDefinition.getId() == null || "".equals(protocolDefinition.getId())) {
            throw new EncodeException("[protocolID:" + protocolDefinition.getId() + "]-the value of label \"id\" can't be null");
        }
        if (protocolDefinition.getClassName() == null || "".equals(protocolDefinition.getClassName())) {
            throw new EncodeException("[protocolID:" + protocolDefinition.getId() + "]-the value of label \"className\" can't be null");
        }
        if (protocolDefinition.getByteDefList() == null || protocolDefinition.getByteDefList().size() == 0) {
            throw new EncodeException("[protocolID:" + protocolDefinition.getId() + "]-the value of label \"definitions\" can't be null");
        }
    }

    private Object getValue(Object obj, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object val = null;
        if (obj instanceof Map) {
            val = ((Map) obj).get(property);
        } else {
            val = BeanUtils.getProperty(obj, property);
        }
        return val == null ? 0 : val;
    }
}
