package com.het.httpserver.http;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthenticator extends Authenticator {
    String userName;
    String password;

    public BasicAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Called when password authorization is needed.  Subclasses should
     * override the default implementation, which returns null.
     *
     * @return The PasswordAuthentication collected from the
     *         user, or null if none is provided.
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password.toCharArray());
    }

    public static void main(String[] args) {

        String html = null;
        try {

            Map<String, String> headers = new HashMap<>();
            headers.put("username","admin");
            headers.put("password","public");
            html = SimpleHttpUtils.get("https://192.168.1.100:8421/api/v2/nodes/emq@127.0.0.1/clients",headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(html);


    }
}
