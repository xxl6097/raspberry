package com.java.pi.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;


/**
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2016, by het, Shenzhen, All rights reserved.
 * -----------------------------------------------------------------
 * <p>
 * <p>描述：Log工具，类似android.util.Log。 tag自动产生，格式:
 * TAG:className.methodName(Line:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(Line:lineNumber)。</p>
 * 名称: Log工具 <br>
 * 作者: uuixa<br>
 * 版本: 1.0<br>
 * 日期: 2016/9/30 11:40<br>
 **/
public class Logc {

    public static boolean isAndroid = false;
    /**
     * 日志
     */
    public static final String LINE_BREAK = "\r\n";
    private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter>() {
        protected ReusableFormatter initialValue() {
            return new ReusableFormatter();
        }
    };
    private final static String TAG = ":"; // 自定义Tag的前缀，可以是作者名

    public static boolean DEBUG = true;


    private static void loge(String tag, String content) {
        System.err.println( tag + content);
    }

    private static void logd(String tag, String content) {
        System.out.println(getCurrentTime() + tag + content);
    }

    private static void logw(String tag, String content) {
        System.out.println(getCurrentTime() + tag + content);
    }

    private static void logi(String tag, String content) {
        System.out.println(getCurrentTime() + tag + content);
    }

    private static String hang(){
        return  "["+getFileName()+":"+ getLineNumber()+"]";
    }
    private static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        date += hang();
        return date;
    }

    private static void logv(String tag, String content) {
        System.out.println(tag + content);
    }

    public static void d(String content, Object... objects) {
        if (!DEBUG) {
            return;
        }
        System.out.println(getCurrentTime()+TAG + content + objects);
    }

    public static void i(String content, Object... objects) {
        if (!DEBUG) {
            return;
        }
        System.out.println(getCurrentTime()+TAG + content + objects);
    }

    public static void e(String content, Object... objects) {
        if (!DEBUG) {
            return;
        }
        System.out.println(getCurrentTime()+TAG + content + objects);
    }

    public static void v(String content, Object... objects) {
        if (!DEBUG) {
            return;
        }
        System.out.println(getCurrentTime()+TAG + content + objects);
    }

    public static void v(String content) {
        if (!DEBUG) {
            return;
        }
        logv(TAG, content);
    }

    public static void v(String uTag, String content) {
        if (!DEBUG) {
            return;
        }
        logv(TAG, content);
    }

    public static void d(String content) {
        if (!DEBUG)
            return;

        logd(TAG, content);
    }


    public static void d(String uTag, String content) {
        if (!DEBUG) {
            return;
        }

        logd(TAG, content);
    }

    public static void i(String content) {
        if (!DEBUG)
            return;

        logi(TAG, content);
    }


    public static void i(String uTag, String content) {
        if (!DEBUG) {
            return;
        }

        logi(TAG, content);
    }

    public static void w(String content) {
        if (!DEBUG)
            return;

        logw(TAG, content);
    }

    public static void w(String uTag, String content) {
        if (!DEBUG) {
            return;
        }

        logw(TAG, content);
    }

    public static void e(String content) {
        if (!DEBUG)
            return;
        loge(getCurrentTime() +TAG, content);
    }

    public static void e(String uTag, String content) {
        if (!DEBUG) {
            return;
        }
        loge(TAG, content);
    }

    public static void e(Throwable tr) {
        if (!DEBUG)
            return;

        String content = getThrowable(tr, null);
        loge(TAG, content);
    }


    public static int getLineNumber() {
        try {
            int len = Thread.currentThread().getStackTrace().length;
            String tar = Thread.currentThread().getStackTrace()[len].getClassName();
            if (tar.equals(Thread.class.getName())){
                len --;
            }
            return Thread.currentThread().getStackTrace()[len-1].getLineNumber();
        }catch (Exception e){

        }
        return 0;

    }

    public static String getFileName() {
        try {
            int len = Thread.currentThread().getStackTrace().length;
            String tar = Thread.currentThread().getStackTrace()[len].getClassName();
            if (tar.equals(Thread.class.getName())){
                len --;
            }
            return Thread.currentThread().getStackTrace()[len-1].getFileName();
        }catch (Exception e){

        }
        return "";
    }

    private static StackTraceElement getCallerStackTraceElement() {
        StackTraceElement[] stackTree = Thread.currentThread().getStackTrace();
        if (stackTree == null || stackTree.length < 4)
            return null;
        return Thread.currentThread().getStackTrace()[4];
    }

    public static String format(String msg, Object... args) {
        ReusableFormatter formatter = thread_local_formatter.get();
        return formatter.format(msg, args);
    }

    private static String generateTag(StackTraceElement caller, String uTag) {
        String tag = "(%s:%d).%s"; // 占位符
        if (caller != null) {
            String callerClazzName = caller.getFileName();
            tag = String.format(tag, callerClazzName, caller.getLineNumber(), caller.getMethodName()); // 替换
            //tag = ByteUtils.isNull(TAG) ? tag : TAG + ":" + tag;
            String str = tag;
            if (uTag == null || uTag.equals("")) {
                str = TAG + ":" + tag;
            } else {
                str = TAG + "." + uTag + ":" + tag;
            }
            return str;
        }
        return tag;
    }

    private static String getThrowable(Throwable throwable, String mag) {
        /* 打印异常 */
        StringBuffer sb = new StringBuffer();
        if (throwable != null) {
            sb.append(LINE_BREAK);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            sb.append(stringWriter.toString());
        }
        return sb.toString();
    }

    /**
     * A little trick to reuse a formatter in the same thread
     */
    private static class ReusableFormatter {

        private Formatter formatter;

        private StringBuilder builder;

        public ReusableFormatter() {
            builder = new StringBuilder();
            formatter = new Formatter(builder);
        }

        public String format(String msg, Object... args) {
            formatter.format(msg, args);
            String s = builder.toString();
            builder.setLength(0);
            return s;
        }

    }

}

