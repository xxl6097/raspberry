package com.java.pi.httpserver.file;


import com.java.pi.httpserver.core.http.NanoHTTPD;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class HttpFile implements NanoHTTPD.TempFile {
    private final File file;

    private final OutputStream fstream;

    public HttpFile(String tempdir,String filename_hint) throws IOException {
        File tmp = new File(tempdir);
        if (!tmp.exists()){
            tmp.mkdirs();
        }
        this.file = new File(tempdir+File.separator+filename_hint);//File.createTempFile(filename_hint, "", new File(tempdir));
        this.fstream = new FileOutputStream(this.file);
    }

    @Override
    public void delete() throws Exception {
        safeClose(this.fstream);
//        boolean si = this.file.delete();
//        System.out.println("delete "+ file.getAbsolutePath() + " "+si);
//        if (!this.file.delete()) {
//            throw new Exception("could not delete temporary file");
//        }
    }

    @Override
    public String getName() {
        return this.file.getAbsolutePath();
    }

    @Override
    public OutputStream open() throws Exception {
        return this.fstream;
    }

    private static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not close", e);
        }
    }
}
