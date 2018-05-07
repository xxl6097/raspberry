package com.java.pi.httpserver.file;

import com.java.pi.httpserver.core.http.NanoHTTPD;
import com.java.pi.util.HttpConst;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HttpFileManager implements NanoHTTPD.TempFileManager {
    private final String tmpdir;

    private final List<NanoHTTPD.TempFile> tempFiles;

    public HttpFileManager() {
        this.tmpdir = HttpConst.FILE_PATH;
        this.tempFiles = new ArrayList<NanoHTTPD.TempFile>();
    }

    @Override
    public void clear() {
        for (NanoHTTPD.TempFile file : this.tempFiles) {
            try {
                file.delete();
            } catch (Exception ignored) {
                NanoHTTPD.LOG.log(Level.WARNING, "could not delete file ", ignored);
            }
        }
        this.tempFiles.clear();
    }

    @Override
    public NanoHTTPD.TempFile createTempFile(String filename_hint) throws Exception {
        HttpFile tempFile = new HttpFile(this.tmpdir,filename_hint);
        this.tempFiles.add(tempFile);
        return tempFile;
    }


    @Override
    public NanoHTTPD.TempFile createTempFile(String subDir, String filename_hint) throws Exception{
        if (subDir==null||subDir.equals("")) {
            HttpFile tempFile = new HttpFile(this.tmpdir, filename_hint);
            this.tempFiles.add(tempFile);
            return tempFile;
        }else{
            HttpFile tempFile = new HttpFile(this.tmpdir + subDir, filename_hint);
            this.tempFiles.add(tempFile);
            return tempFile;
        }
    }
}
