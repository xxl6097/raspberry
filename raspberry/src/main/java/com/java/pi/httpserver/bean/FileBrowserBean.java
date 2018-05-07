package com.java.pi.httpserver.bean;

import java.io.Serializable;

public class FileBrowserBean implements Serializable {
    private String fileName;
    private String fileUrl;
    private long fileSize;

    public FileBrowserBean(String fileName, String fileUrl, long fileSize) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }
}
