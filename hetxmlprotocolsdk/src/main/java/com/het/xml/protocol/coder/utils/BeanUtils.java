package com.het.xml.protocol.coder.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by UUXIA on 2015/9/8.
 */
public class BeanUtils {
    public static void setProperty(Object instance, String field, Object value) {
        //��ȡ��
        Class c = instance.getClass();
        StringBuffer sbMethod = new StringBuffer();
        sbMethod.append("set");
        sbMethod.append(StringUtil.toUpperCaseFirstOne(field));
        Method[] methods = c.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(sbMethod.toString())) {
                try {
                    method.invoke(instance, value);
                    return;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String getProperty(Object instance, String field) {
        //��ȡ��
        Class c = instance.getClass();
        StringBuffer sbMethod = new StringBuffer();
        sbMethod.append("get");
        sbMethod.append(StringUtil.toUpperCaseFirstOne(field));
        Method[] methods = c.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(sbMethod.toString())) {
                try {
                    return (String) method.invoke(instance);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
