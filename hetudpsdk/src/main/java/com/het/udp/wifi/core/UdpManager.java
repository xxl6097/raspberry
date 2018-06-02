package com.het.udp.wifi.core;

import com.het.log.Logc;
import com.het.udp.wifi.callback.IRecevie;
import com.het.udp.wifi.model.PacketBuffer;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class UdpManager {
    public static final int MINIMUM_DELAY = 0x10;
    //设置缓冲区大小
    private static int SO_BUFSIZE = 8192;
    //端口复用
    private static boolean SO_REUSEADDR = true;
    //设置广播
    private static boolean SO_BROADCAST = true;
    private UdpClient client;
    private UdpServer server;
    private DataIssue udpBiz;
    private DatagramSocket datagramSocket;
    private String broadCasetIp;
    private int trafficType = MINIMUM_DELAY;
    private String ip;
    private int port;

    public UdpManager(String ip, int port) throws SocketException {
        this.ip = ip;
        this.port = port;
        if (createSocket(port)) {
            client = new UdpClient(datagramSocket);
            server = new UdpServer(datagramSocket);
            udpBiz = new DataIssue();
            startUdpService();
        } else {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"uulog.socket create failed...");
            throw new SocketException("socket create failed...");
        }
    }

    private boolean createSocket(int port) throws SocketException {
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setBroadcast(SO_BROADCAST);
        datagramSocket.setReceiveBufferSize(SO_BUFSIZE);
        datagramSocket.setTrafficClass(trafficType);
        datagramSocket.setReuseAddress(SO_REUSEADDR);
        datagramSocket.bind(new InetSocketAddress(port));
        if (datagramSocket != null) {
            Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"udp Socket create sucessss port:"+port);
            return true;
        }
        return false;
    }

    public void close() {
        if (!datagramSocket.isClosed()) {
//            datagramSocket.disconnect();
            datagramSocket.close();
        }
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.close();
        }
        if (udpBiz != null) {
            udpBiz.close();
        }
    }

    public void setCallback(IRecevie callback) {
        if (udpBiz != null) {
            udpBiz.setCallback(callback);
        }
    }

    public void send(byte[] data, String ip, int port) {
        PacketBuffer packet = new PacketBuffer();
        packet.setData(data);
        packet.setPort(port);
        packet.setIp(ip);
        if (data != null) {
            packet.setLength(data.length);
        }
        if (client != null) {
            client.putData(packet);
        }
    }

    /**
     * 开启发送和接收服务线程
     */
    private void startUdpService() {
        if (client == null || server == null || udpBiz == null)
            throw new IllegalArgumentException("client or server instance is null...");
        client.start();
        server.start();
        udpBiz.start();
//        ExecutorService executorService;
    }

    public void setLocalIp(String localIp) {
        if (udpBiz != null) {
            udpBiz.setLocalIp(localIp);
        }
    }

    public void setBroadCasetIp(String broadCasetIp) {
        this.broadCasetIp = broadCasetIp;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
