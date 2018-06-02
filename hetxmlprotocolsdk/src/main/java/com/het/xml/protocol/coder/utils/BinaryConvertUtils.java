package com.het.xml.protocol.coder.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * byte数组与数字互转
 *
 * @author jake
 */
public class BinaryConvertUtils {
    /**
     * 整数转byte数组(高字节在前)
     *
     * @param val  整数
     * @param size 整数转为多少字节的byte数组（例如：10000转为3个字节的byte数组）
     * @return
     */
    public static byte[] longToByteArray(long val, int size) {
        byte[] result = new byte[size];
        for (int j = 0; j < size; j++) {
            result[j] = (byte) ((val >>> 8 * (size - j - 1)) & 0xFF);
        }
        return result;
    }

    /**
     * float转为byte数组
     *
     * @param val
     * @return
     */
    public static byte[] floatToByteArray(float val) {
        int temVal = Float.floatToIntBits(val);
        int size = Float.SIZE;
        byte[] result = new byte[size];
        for (int j = 0; j < size; j++) {
            result[j] = (byte) ((temVal >>> 8 * (size - j - 1)) & 0xFF);
        }
        return result;
    }

    /**
     * byte数组转为float
     *
     * @param bytes
     * @return
     */
    public static float byteArrayToFloat(byte[] bytes) {
        if (bytes.length != 32) {
            throw new IllegalArgumentException("byte array length must be 32");
        }
        return Float.intBitsToFloat((int) byteArrayToLong(bytes));
    }

    /**
     * byte数组转double
     *
     * @param bytes
     * @return
     */
    public static double byteArrayToDouble(byte[] bytes) {
        if (bytes.length != 64) {
            throw new IllegalArgumentException("byte array length must be 64");
        }
        return Double.longBitsToDouble(byteArrayToLong(bytes));
    }

    /**
     * double 转 byte数组
     *
     * @param val
     * @return
     */
    public static byte[] doubleToByteArray(double val) {
        long temVal = Double.doubleToLongBits(val);
        int size = Double.SIZE;
        byte[] result = new byte[size];
        for (int j = 0; j < size; j++) {
            result[j] = (byte) ((temVal >>> 8 * (size - j - 1)) & 0xFF);
        }
        return result;
    }

    /**
     * byte数组转long
     *
     * @param bytes
     * @return
     */
    public static long byteArrayToLong(byte[] bytes) {
        int size = bytes.length;
        long val = 0;
        for (int j = 0; j < size; j++) {
            val += ((long) (bytes[j] & 255)) << (8 * (size - j - 1));
        }
        return val;
    }


    public static void main(String[] args) {
        byte[] da = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        long lo = byteArrayToLong(da);
//        System.out.println("l=" + lo);
        int i = (int) lo;
//        System.out.println("i=" + i);
        short s = (short) lo;
//        System.out.println("s=" + s);
        for (int j = 0; j < 100; j++) {
            int k = j % 2;
            int v = j / 2;
            if (k == 0) {
//                System.out.println("j=" + j + " k=" + k + " v=" + v);
            } else {
//                System.out.println("j=" + j + " k=" + k);
            }
        }
    }

    public static Number convertNumberToTargetClass(Number number, Class targetClass)
            throws IllegalArgumentException {
        long value;

        if (targetClass.isInstance(number))
            return number;

        if (targetClass.equals(Byte.class)) {
            value = number.longValue();
            if ((value < -128L) || (value > 127L))
                raiseOverflowException(number, targetClass);

            return new Byte(number.byteValue());
        }
        if (targetClass.equals(Short.class)) {
            value = number.longValue();
            if ((value < -32768L) || (value > 32767L))
                raiseOverflowException(number, targetClass);

            return new Short(number.shortValue());
        }
        if (targetClass.equals(Integer.class)) {
            value = number.longValue();
            if ((value < -2147483648L) || (value > 2147483647L))
                raiseOverflowException(number, targetClass);

            return new Integer(number.intValue());
        }
        if (targetClass.equals(Long.class))
            return new Long(number.longValue());

        if (targetClass.equals(BigInteger.class)) {
            if (number instanceof BigDecimal) {
                return ((BigDecimal) number).toBigInteger();
            }

            return BigInteger.valueOf(number.longValue());
        }

        if (targetClass.equals(Float.class))
            return new Float(number.floatValue());

        if (targetClass.equals(Double.class))
            return new Double(number.doubleValue());

        if (targetClass.equals(BigDecimal.class)) {
            return new BigDecimal(number.toString());
        }

        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number
                .getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
    }

    private static void raiseOverflowException(Number number, Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" + number
                .getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
    }
}
