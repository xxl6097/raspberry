package com.het.udp.wifi.core;

import com.het.log.Logc;
import com.het.udp.wifi.model.PacketBuffer;
import com.het.udp.wifi.utils.ByteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class UdpServer extends BaseThread {
    protected DatagramPacket datagramPacket;
    protected byte[] buffer = new byte[8192];

    public UdpServer(DatagramSocket datagramSocket) {
        super(datagramSocket);
        setName("UdpServer");
    }

    @Override
    public void run() {
        super.run();
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        while (runnable) {
            try {
                if (datagramSocket.isClosed()) {
                    continue;
                }
                datagramSocket.receive(datagramPacket);
                int localPort = datagramSocket.getLocalPort();
                PacketBuffer packet = new PacketBuffer();
                packet.setLength(datagramPacket.getLength());
                packet.setPort(localPort);
                packet.setIp(datagramPacket.getAddress().getHostAddress().toString());
                //Logc.i(Logc.HetReportTag.INFO_WIFI,"uu 接收队列大小->" + inQueue.size() + " " + ByteUtils.toHexString(packet.getData()));//+""+ ByteUtils.toHexString(datagramPacket.getData()));
                byte[] remo = new byte[datagramPacket.getLength()];
                System.arraycopy(datagramPacket.getData(), 0, remo, 0, datagramPacket.getLength());
                packet.setData(remo);
                boolean b = inQueue.offer(packet);
                if (!b) {
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"this packet is loss:" + packet.toString());
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
//                Logc.e(Logc.HetReportTag.WIFI_EX_LOG,"UdpServer.SocketTimeoutException " + e.getMessage());
            } catch (IOException e) {
//                Logc.e(Logc.HetReportTag.WIFI_EX_LOG,"UdpServer.IOException " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
