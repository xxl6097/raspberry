package com.het.udp.core.Utils;

/**
 * Created by uuxia-mac on 15/10/21.
 */
public enum DataType {
    HET(18899, "het内部协议"), OPEN(28899, "开放平台协议"), HF(49999, "汉枫广播ssid端口");

    DataType(int port, String procotolType) {
        this.port = port;
        this.procotolType = procotolType;
    }

    public int getPort() {
        return port;
    }

    public static DataType getDataType(int port) {
        if (port == 28899) {
            return OPEN;
        } else {
            return HET;
        }
    }

    public String getProcotolType() {
        return procotolType;
    }

    @Override
    public String toString() {
        return "DataType{" +
                "port=" + port +
                ", procotolType='" + procotolType + '\'' +
                '}';
    }

    private int port;
    private String procotolType;
}

class JavaTst {
    public static void main(String[] args) {
//        System.out.println(DataType.HET.getPort() + " " + DataType.HET.equals(DataType.HET));
    }
}
