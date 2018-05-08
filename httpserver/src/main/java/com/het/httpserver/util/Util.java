package com.het.httpserver.util;

import com.het.httpserver.http.SimpleHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static final String sGetAddrUrl = "http://ip-api.com/json/";
    public static boolean isEmpty(CharSequence str){
        if (str==null||str.equals(""))
            return true;
        return false;
    }

    public static List<File> getTxTFileList(String strPath) {
        List<File> filelist = new ArrayList<>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getTxTFileList(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileName.endsWith("txt")) { // 判断文件名是否以.avi结尾
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    filelist.add(files[i]);
                } else {
                    continue;
                }
            }
        }
        return filelist;
    }

    public static String locateCityName(String foreignIPString) {
        String cityName = null;
        try {
            String requestStr = Util.sGetAddrUrl;
            if (foreignIPString != null) {
                requestStr += foreignIPString;
            }
            cityName = SimpleHttpUtils.get(requestStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityName;
    }

}
