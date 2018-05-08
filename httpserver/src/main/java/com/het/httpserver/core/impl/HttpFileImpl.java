package com.het.httpserver.core.impl;

import com.het.httpserver.bean.ApiResult;
import com.het.httpserver.core.AbstractPostHttpFactory;
import com.het.httpserver.core.http.NanoHTTPD;
import com.het.httpserver.file.HttpFileManager;
import com.het.httpserver.util.BaiduFanyi;
import com.het.httpserver.util.GsonUtil;
import com.het.httpserver.util.Logc;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpFileImpl extends AbstractPostHttpFactory {
    final String FANYI_PATH = "/v1/api/file";
    private NanoHTTPD.Method method;
    private Map<String, String> headers;
    private Map<String, String> parms;

    private NanoHTTPD.TempFileManager fileManager;

    public HttpFileImpl() {
        fileManager = new HttpFileManager();
    }

    private static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX,
            Pattern.CASE_INSENSITIVE);
    private static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";

    private static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern
            .compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);
    private static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";

    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    protected String onMessageReceive(NanoHTTPD.IHTTPSession session) {
        String result = null;
        if (session == null)
            return null;
        Logc.d("=========================HttpFileImpl :" + session.toString());
        this.method = session.getMethod();
        this.headers = session.getHeaders();
        String path = session.getUri();
        parms = session.getParms();
        if (path == null)
            return "path is null";
//        if (path.startsWith(FANYI_PATH)) {
//            String callback = parms.get("callback");
//            String query = parms.get("query");
//            String html = BaiduFanyi.say(query);
//            result = ";" + callback + "(" + html + ");";
//        }


        Map<String, String> files = new HashMap<String, String>();
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("================= start");
        try {
            onParseFile(session,files);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NanoHTTPD.ResponseException e) {
            e.printStackTrace();
        }finally {
            this.fileManager.clear();
        }
        float re = (end - start) / 1000.000f;
        System.out.println("================= end " + re);
        ApiResult<String> ret = new ApiResult<>();
        ret.setCode(0);
        ret.setData(files.toString());
        return GsonUtil.getInstance().toJson(ret);
    }


    private static final int REQUEST_BUFFER_LEN = 512;

    public void onParseFile(NanoHTTPD.IHTTPSession session, Map<String, String> files) throws IOException, NanoHTTPD.ResponseException {
        try {
            InputStream inputStream = session.getInputStream();
            int size = 0;
            int rlen = 0;
            if (this.headers.containsKey("content-length")) {
                size = Integer.parseInt(this.headers.get("content-length"));
            }
            if (this.headers.containsKey("rlen")) {
                rlen = Integer.parseInt(this.headers.get("rlen"));
            }
            ByteBuffer fbuf = ByteBuffer.allocate(size);
            // Read all the body and write it to request_data_output
            byte[] buf = new byte[REQUEST_BUFFER_LEN];
            while (rlen >= 0 && size > 0) {
                rlen = inputStream.read(buf, 0, (int) Math.min(size, REQUEST_BUFFER_LEN));
                size -= rlen;
                if (rlen > 0) {
                    fbuf.put(buf, 0, rlen);
                }
            }
            fbuf.flip();
            // If the method is POST, there may be parameters
            // in data section, too, read it:
            if (NanoHTTPD.Method.POST.equals(this.method)) {
                String contentType = "";
                String contentTypeHeader = this.headers.get("content-type");

                StringTokenizer st = null;
                if (contentTypeHeader != null) {
                    st = new StringTokenizer(contentTypeHeader, ",; ");
                    if (st.hasMoreTokens()) {
                        contentType = st.nextToken();
                    }
                }

                if ("multipart/form-data".equalsIgnoreCase(contentType)) {
                    // Handle multipart/form-data
                    if (!st.hasMoreTokens()) {
                        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST,
                                "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                    }

                    String boundaryStartString = "boundary=";
                    int boundaryContentStart = contentTypeHeader.indexOf(boundaryStartString)
                            + boundaryStartString.length();
                    String boundary = contentTypeHeader.substring(boundaryContentStart, contentTypeHeader.length());
                    if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                        boundary = boundary.substring(1, boundary.length() - 1);
                    }

                    String dir = session.getHeaders().get("mqtt-clientid");
                    decodeMultipartFormData(boundary, fbuf, this.parms, files,dir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void decodeMultipartFormData(String boundary, ByteBuffer fbuf, Map<String, String> parms,
                                         Map<String, String> files,String subDir) throws NanoHTTPD.ResponseException {
        try {
            int[] boundary_idxs = getBoundaryPositions(fbuf, boundary.getBytes());
            if (boundary_idxs.length < 2) {
                throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST,
                        "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
            }

            final int MAX_HEADER_SIZE = 1024;
            byte[] part_header_buff = new byte[MAX_HEADER_SIZE];
            for (int bi = 0; bi < boundary_idxs.length - 1; bi++) {
                fbuf.position(boundary_idxs[bi]);
                int len = (fbuf.remaining() < MAX_HEADER_SIZE) ? fbuf.remaining() : MAX_HEADER_SIZE;
                fbuf.get(part_header_buff, 0, len);
                ByteArrayInputStream bais = new ByteArrayInputStream(part_header_buff, 0, len);
                BufferedReader in = new BufferedReader(new InputStreamReader(bais, Charset.forName("US-ASCII")));

                // First line is boundary string
                String mpline = in.readLine();
                if (!mpline.contains(boundary)) {
                    throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST,
                            "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                }

                String part_name = null, file_name = null, content_type = null;
                // Parse the reset of the header lines
                mpline = in.readLine();
                while (mpline != null && mpline.trim().length() > 0) {
                    Matcher matcher = CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        String attributeString = matcher.group(2);
                        matcher = CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);
                        while (matcher.find()) {
                            String key = matcher.group(1);
                            if (key.equalsIgnoreCase("name")) {
                                part_name = matcher.group(2);
                            } else if (key.equalsIgnoreCase("filename")) {
                                file_name = matcher.group(2);
                            }
                        }
                    }
                    matcher = CONTENT_TYPE_PATTERN.matcher(mpline);
                    if (matcher.matches()) {
                        content_type = matcher.group(2).trim();
                    }
                    mpline = in.readLine();
                }

                // Read the part data
                int part_header_len = len - (int) in.skip(MAX_HEADER_SIZE);
                if (part_header_len >= len - 4) {
                    throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                            "Multipart header size exceeds MAX_HEADER_SIZE.");
                }
                int part_data_start = boundary_idxs[bi] + part_header_len;
                int part_data_end = boundary_idxs[bi + 1] - 4;

                fbuf.position(part_data_start);
                if (content_type == null) {
                    // Read the part into a string
                    byte[] data_bytes = new byte[part_data_end - part_data_start];
                    fbuf.get(data_bytes);
                    parms.put(part_name, new String(data_bytes));
                } else {
                    // Read it into a file
                    String path = saveTmpFile(fbuf, part_data_start, part_data_end - part_data_start, subDir,file_name);
                    if (!files.containsKey(part_name)) {
                        files.put(part_name, path);
                    } else {
                        int count = 2;
                        while (files.containsKey(part_name + count)) {
                            count++;
                        }
                        files.put(part_name + count, path);
                    }
                    parms.put(part_name, file_name);
                }
            }
        } catch (NanoHTTPD.ResponseException re) {
            throw re;
        } catch (Exception e) {
            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, e.toString());
        }
    }


    private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
        int[] res = new int[0];
        if (b.remaining() < boundary.length) {
            return res;
        }

        int search_window_pos = 0;
        byte[] search_window = new byte[4 * 1024 + boundary.length];

        int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
        b.get(search_window, 0, first_fill);
        int new_bytes = first_fill - boundary.length;

        do {
            // Search the search_window
            for (int j = 0; j < new_bytes; j++) {
                for (int i = 0; i < boundary.length; i++) {
                    if (search_window[j + i] != boundary[i])
                        break;
                    if (i == boundary.length - 1) {
                        // Match found, add it to results
                        int[] new_res = new int[res.length + 1];
                        System.arraycopy(res, 0, new_res, 0, res.length);
                        new_res[res.length] = search_window_pos + j;
                        res = new_res;
                    }
                }
            }
            search_window_pos += new_bytes;

            // Copy the end of the buffer to the start
            System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0,
                    boundary.length);

            // Refill search_window
            new_bytes = search_window.length - boundary.length;
            new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
            b.get(search_window, boundary.length, new_bytes);
        } while (new_bytes > 0);
        return res;
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


    private String saveTmpFile(ByteBuffer b, int offset, int len, String dir,String filename_hint) {
        String path = "";
        if (len > 0) {
            FileOutputStream fileOutputStream = null;
            try {
                NanoHTTPD.TempFile tempFile = this.fileManager.createTempFile(dir,filename_hint);
                ByteBuffer src = b.duplicate();
                fileOutputStream = new FileOutputStream(tempFile.getName());
                FileChannel dest = fileOutputStream.getChannel();
                src.position(offset).limit(offset + len);
                dest.write(src.slice());
                path = tempFile.getName();
            } catch (Exception e) { // Catch exception if any
                throw new Error(e); // we won't recover, so throw an error
            } finally {
                safeClose(fileOutputStream);
            }
        }
        return path;
    }
}


