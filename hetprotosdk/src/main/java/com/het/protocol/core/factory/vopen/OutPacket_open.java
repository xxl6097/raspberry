package com.het.protocol.core.factory.vopen;


import com.het.protocol.bean.DeviceInfoBean;
import com.het.protocol.bean.PacketBean;
import com.het.protocol.core.factory.io.IOut;
import com.het.protocol.util.ProtoUtils;
import com.het.protocol.util.Contants;

import java.nio.ByteBuffer;

/**
 * Created by UUXIA on 2015/6/24.
 */
public class OutPacket_open extends Packet_open implements IOut {
    private byte[] data;

    public OutPacket_open(PacketBean packetBean) {
        super(packetBean);
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
        fcs = ProtoUtils.CRC16Calc(crc, crc.length);
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
        if (packetBean != null) {
            DeviceInfoBean deviceInfoBean = packetBean.getDeviceInfo();
            if (deviceInfoBean != null) {
                protocolVersion = deviceInfoBean.getProtocolVersion();
                protocolType = deviceInfoBean.getProtocolType();
                commandType = deviceInfoBean.getCommandType();
                if (deviceInfoBean.getDeviceMacToByte() != null) {
                    macAddr = deviceInfoBean.getDeviceMacToByte();
                }
//                deviceId = deviceInfoBean.getDeviceId().getBytes();
                deviceId = deviceInfoBean.getDeviceTypeForOpen();
                frameSN = deviceInfoBean.getFrameSN();

                if (packetBean.getBody() != null) {
                    dataLen = (short) packetBean.getBody().length;//(byte) (packetBean.getBody().length + Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - 1);
                    frameBody = packetBean.getBody();
                }

                int len = getLength(getDataLen());
                ByteBuffer buf = ByteBuffer.allocate(len);
                fill(buf);
                buf.flip();
                data = buf.array();
                packetBean.setData(data);
            }
        }
    }
}
