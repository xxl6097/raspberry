package com.het.udp.wifi.packet.factory.v42;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.factory.io.IOut;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

import java.nio.ByteBuffer;

/**
 * Created by UUXIA on 2015/6/19.
 * 具体产品类
 * version 42协议版本 数据封装类
 */
public class OutPacket_v42 extends Packet_v42 implements IOut {
    private byte[] data;

    public OutPacket_v42(PacketModel packetModel) {
        super(packetModel);
    }

    /**
     * 封装数据包
     */
    private void fill() {
        if (packetModel != null) {
            UdpDeviceDataBean udpDeviceDataBean = packetModel.getDeviceInfo();
            if (udpDeviceDataBean != null) {
                protocolVersion = udpDeviceDataBean.getProtocolVersion();
                protocolType = udpDeviceDataBean.getProtocolType();
                commandType = udpDeviceDataBean.getCommandType();
                if (udpDeviceDataBean.getDeviceMacToByte() != null) {
                    macAddr = udpDeviceDataBean.getDeviceMacToByte();
                }
                if (udpDeviceDataBean.getNewDeviceType() == null) {
//                    if (LOG.PACKET_VERSION_OFF) Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG,"DeviceType_v42 is null");
                } else {
                    deviceType = udpDeviceDataBean.getNewDeviceType();
                }

                dataStatus = udpDeviceDataBean.getDataStatus();
                wifiStatus = udpDeviceDataBean.getWifiStatus();
                frameSN = udpDeviceDataBean.getFrameSN();

                if (packetModel.getBody() != null) {
                    dataLen = (short) packetModel.getBody().length;
                    frameBody = packetModel.getBody();
                }

                int len = getLength(getDataLen());
                ByteBuffer buf = ByteBuffer.allocate(len);
                fill(buf);
                buf.flip();
                data = buf.array();
                packetModel.setData(data);
            }
        }
    }

    @Override
    public void fill(ByteBuffer buf) {
        putHead(buf);
        putBody(buf);
        putCRC(buf);
    }

    /**
     * 构造数据包
     *
     * @return
     */
    @Override
    public byte[] packetOut() {
        fill();
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"version42 protocol data packetOut finish...");
        return toByte();
    }

    /**
     * 将包头部转化为字节流, 写入指定的ByteBuffer对象.
     *
     * @param buf 写入的ByteBuffer对象.
     */
    @Override
    public void putHead(ByteBuffer buf) {
        buf.put(packetStart);
        buf.put(protocolVersion);
        buf.put(protocolType);
        buf.putShort(commandType);
        buf.put(macAddr);
        buf.put(deviceType);
        buf.put(dataStatus);
        buf.put(wifiStatus);
        buf.putInt(frameSN);
        buf.put(reserved);
        buf.putShort(dataLen);
    }

    /**
     * 初始化包体
     *
     * @param buf ByteBuffer
     */
    @Override
    public void putBody(ByteBuffer buf) {
        if (dataLen > 0 && frameBody != null) {
            buf.put(frameBody);
        }
    }

    /**
     * 初始化CRC16/X25
     *
     * @param buf ByteBuffer
     */
    @Override
    public void putCRC(ByteBuffer buf) {
        //包体CRC
        if (dataLen > 0 && frameBody != null) {
            frameBodyCrc = ByteUtils.CRC16Calc(frameBody, frameBody.length);
        }
        buf.put(frameBodyCrc);

        //整包CRC
        int len = dataLen + Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42 - 3;//协议长度18+N字节,去掉F2 和 CRC两个字节 == 18 - 1 - 2 = 15
        byte[] crc = new byte[len];
        System.arraycopy(buf.array(), 1, crc, 0, len);
        fcs = ByteUtils.CRC16Calc(crc, crc.length);
        buf.put(fcs);
    }

    private byte[] toByte() {
        return data;
    }
}
