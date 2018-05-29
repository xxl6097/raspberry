package com.example.test;

import java.io.IOException;

public class MyClass {
    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("reboot");//注销
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
