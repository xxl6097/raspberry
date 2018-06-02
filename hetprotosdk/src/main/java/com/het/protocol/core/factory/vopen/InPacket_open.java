package com.het.protocol.core.factory.vopen;



import com.het.protocol.bean.DeviceInfoBean;
import com.het.protocol.bean.PacketBean;
import com.het.protocol.core.PacketParseException;
import com.het.protocol.core.factory.io.IIn;
import com.het.protocol.util.ProtoUtils;
import com.het.protocol.util.Contants;

import java.nio.ByteBuffer;

/**
 * Created by UUXIA on 2015/6/24.
 * 开放平台数据协议
 */
public class InPacket_open extends Packet_open implements IIn {
    public InPacket_open(PacketBean packetBean) {
        super(packetBean);
    }

    @Override
    public boolean validateHeader(ByteBuffer buf) throws PacketParseException {
        if (buf != null && packetStart == buf.get() && buf.capacity() >= Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN) {
            return true;
        } else {
            throw new PacketParseException("包头长度错误.实际长度:" + buf.capacity() + " 理论长度:" + Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN);
        }
    }

    @Override
    public byte[] calcBody(ByteBuffer buf, int length) throws PacketParseException {
        /**得到包体长度*/
        int bodyLen = length - Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN;
        if (bodyLen == dataLen) {
            /**得到包体内容*/
            byte[] body = new byte[bodyLen];
            if (bodyLen > 0) {
                buf.get(body);
            }
            return body;
        } else {
            throw new PacketParseException("包体长度错误.实际长度:" + dataLen + " 理论长度:" + bodyLen);
        }
    }

    @Override
    public void parseHeader(ByteBuffer b) throws PacketParseException {
        dataLen = b.getShort();
        dataLen = (short) (dataLen - Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN + 1);
        protocolVersion = b.get();
        protocolType = b.get();
        b.get(deviceId);
        b.get(macAddr);
        frameSN = b.getInt();
        b.get(reserved);
        commandType = b.getShort();
    }

    @Override
    public void parseTail(ByteBuffer buf) throws PacketParseException {
        buf.get(fcs);
    }

    @Override
    public Object toPacketModel() {
        if (packetBean == null)
            packetBean = new PacketBean();
        DeviceInfoBean dm = packetBean.getDeviceInfo();
        if (dm == null) {
            dm = new DeviceInfoBean();
        }
        dm.setNewDeviceTypeForOpen(deviceId);
        String mac = ProtoUtils.byteToMac(getMacAddr());
        dm.setOpenProtocol(true);
        dm.setProtocolVersion(getProtocolVersion());
        dm.setProtocolType(getProtocolType());
        dm.setFrameSN(getFrameSN());
        if (!ProtoUtils.isNull(mac)) {
            dm.setDeviceMac(mac);
        }
        packetBean.setDeviceInfo(dm);
        packetBean.setBody(getFrameBody());
        return packetBean;
    }

    @Override
    public void packetIn() throws PacketParseException {
        if (packetBean != null && packetBean.getData() != null) {
            int length = packetBean.getData().length;
            ByteBuffer buf = ByteBuffer.allocate(length);
            buf.put(packetBean.getData());
            buf.flip();
            validateHeader(buf);
            parseHeader(buf);
            /**得到包体*/
            if (dataLen > 0) {
                setFrameBody(calcBody(buf, length));
            }
            parseTail(buf);
            toPacketModel();
        } else {
            throw new PacketParseException("协议包头错误");
        }
//        Logc.i(Logc.HetReportTag.INFO_WIFI,"version_open protocol data packetIn finish...");
    }
}
