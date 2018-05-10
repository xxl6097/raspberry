package com.java.pi.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static String detectMimeType(String fileName) {
        if (fileName!=null&&!fileName.equals("")) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }
    public static byte[] loadContent(String fileName) throws IOException {
        InputStream input = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            File file = new File(fileName);
            input = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            try {
                if (null != input) {
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
