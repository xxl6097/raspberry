package com.het.udp.wifi.packet.factory.vopen;


import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.factory.io.IOut;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

import java.nio.ByteBuffer;

/**
 * Created by UUXIA on 2015/6/24.
 */
public class OutPacket_open extends Packet_open implements IOut {
    private byte[] data;

    public OutPacket_open(PacketModel packetModel) {
        super(packetModel);
    }

    @Override
    public void putHead(ByteBuffer b) {
        b.put(packetStart);
        short bodylen = (short) (dataLen + Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - 1);
        b.putShort(bodylen);
        b.put(protocolVersion);
        b.put(protocolType);
        b.put(deviceId);
        b.put(macAddr);
        b.putInt(frameSN);
        b.put(reserved);
        b.putShort(commandType);
    }

    @Override
    public void putBody(ByteBuffer buf) {
        if (dataLen > 0 && frameBody != null) {
            buf.put(frameBody);
        }
    }

    @Override
    public void putCRC(ByteBuffer buf) {
        //
        int len = Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN + dataLen - 3;//dataLen - 2;//+ Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - 3;//Э�鳤��18+N�ֽ�,ȥ��F2 �� CRC�����ֽ� == 18 - 1 - 2 = 15
        byte[] crc = new byte[len];
        System.arraycopy(buf.array(), 1, crc, 0, len);
        fcs = ByteUtils.CRC16Calc(crc, crc.length);
        buf.put(fcs);
    }

    private byte[] toByte() {
        return data;
    }

    @Override
    public void fill(ByteBuffer buf) {
        putHead(buf);
        putBody(buf);
        putCRC(buf);
    }

    @Override
    public byte[] packetOut() {
        fill();
//        Logc.i(Logc.HetReportTag.INFO_WIFI,"version_open protocol data packetOut finish...");
        return toByte();
    }

    /**
     *
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
//                deviceId = udpDeviceDataBean.getDeviceId().getBytes();
                deviceId = udpDeviceDataBean.getDeviceTypeForOpen();
                frameSN = udpDeviceDataBean.getFrameSN();

                if (packetModel.getBody() != null) {
                    dataLen = (short) packetModel.getBody().length;//(byte) (packetModel.getBody().length + Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - 1);
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
}
