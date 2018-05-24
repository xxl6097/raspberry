package com.java.pi.http.core.http;


import com.java.pi.util.Logc;

import java.io.IOException;

public class HttpServer extends NanoHTTPD {

    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(String hostname, int port) {
        super(hostname, port);
    }

    public HttpServer() {
        super(8888);
    }


    private void https(){
        try {
            this.makeSecure(NanoHTTPD.makeSSLSocketFactory("C:\\Users\\uuxia\\keystore.jks", "password".toCharArray()), null);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException"+ "Couldn't start server:\n" + e.getMessage());
        }
    }


    @Override
    public void start() throws IOException {
        //读取证书,注意这里的密码必须设置
//        KeyStore keyStore = null;
//        try {
//            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, "android".toCharArray());
//            makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null);
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        https();
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
//        super.serve(session);
        /*if (session.getMethod().name().equalsIgnoreCase("POST")) {
            try {
                session.parseBody(new HashMap<String, String>());
                // Logc.e("uu.post " + session.getUri() + " " +
                // session.getParms().toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }
        }*/
         Response response = com.java.pi.http.core.HttpServerManager.getInstance().onResponse(session);
        if (response !=null){
            Logc.d("=========Server.serve.response:" + response.toString());
//            return new Response(Response.Status.OK, "text/plain; charset=UTF-8",response);
            return response;
        }

        return super.serve(session);
    }

}
