package com.het.udp.wifi.core;

import com.het.log.Logc;
import com.het.udp.core.UdpService;
import com.het.udp.wifi.model.PacketBuffer;
import com.het.udp.wifi.packet.factory.vopen.Packet_open;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.LOG;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class UdpClient extends BaseThread {
    public UdpClient(DatagramSocket datagramSocket) {
        super(datagramSocket);
        setName("UdpClient");
    }

    @Override
    public void run() {
        super.run();
        while (runnable) {
            try {
                //若队列为空，则线程阻塞在此处
                PacketBuffer data = outQueue.take();
                send(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"UdpClient.InterruptedException " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"UdpClient.IOException " + e.getMessage());
            }
        }
    }

    private void send(PacketBuffer packetBuffer) throws IOException {
        if (datagramSocket == null) {
            throw new IOException("datagramSocket is null");
        }
        if (datagramSocket.isClosed()){
            throw new IOException("socket is closed");
        }
        if (packetBuffer == null) {
            throw new IOException("packetBuffer is null");
        }
        byte[] bytes = packetBuffer.getData();
        if (bytes == null || bytes.length == 0) {
            throw new IOException("bytes is null or size is zero");
        }
        String ip = packetBuffer.getIp();
        if (ByteUtils.isNull(ip)) {
            throw new IOException("ip is null");
        }
        int port = packetBuffer.getPort();
        if (port == 0) {
            return;
//            port = UdpService.trasPort;
//            throw new IOException("port is zero");
        }
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ip), port);
        datagramSocket.send(dp);
        isHanFengV3Module(bytes);
        if (LOG.UDP_SEND_RECV_OFF) {
            if (bytes[0] != 5 && bytes[bytes.length - 1] != 5 && bytes[bytes.length - 1] != bytes[0]) {
                String cmd = ByteUtils.getCmd(bytes);
                String mac = ByteUtils.getMacAddr(bytes);
                if (bytes[0] == Packet_open.packetStart) {
                    cmd = ByteUtils.getCmdForOPen(bytes);
                }
//                System.err.println("udp.send->" + cmd + " mac=" + mac + " ip=" + ByteUtils.toIp(ip) + ":" +
//                        port +
//                        " " + ByteUtils.toHexString(bytes));
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"udp.send->" + cmd + " mac=" + mac + " ip=" + ByteUtils.toIp(ip) + ":" +
                        port +
                        " " + ByteUtils.toHexString(bytes));
            }
        } else if (LOG.SSID_PASS_OFF) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"udp.ssid.pass" + " ip=" + ip + ":" + port + " data=" + ByteUtils.toHexString(bytes));
        }

    }

    private void isHanFengV3Module(byte[] bytes){
        if (UdpService.getInstance() != null && bytes != null && LOG.HanFengV3) {
            if (bytes[0] == 5 && bytes[bytes.length - 1] == 5 && bytes[bytes.length - 1] == bytes[0]) {
                LOG.HanFengV3 = false;
                UdpService.getInstance().tips("正在广播路由器密码[udpClient.java(90行)]");
            }
        }
    }

    public static void main(String[] args) {
        String ip = "192.168.10.113";
        String ip1 = "192.168.10.255";
        String[] aa = ip.split("\\.");
        int len = aa[3].length();
        if (len == 1) {
            ip += "  ";
        } else if (len == 2) {
            ip += " ";
        }
//        System.out.println(ip + "aaaa\r\n" + ip1);
    }

    public void putData(PacketBuffer packet) {
        //若队列已经满，则等待有空间再继续。
        boolean b = outQueue.offer(packet);
        if (!b) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"this packet offer faile:" + packet.toString());
        }

    }
}
