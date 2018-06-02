package com.het.protocol.core.factory.v41;


import com.het.protocol.bean.DeviceInfoBean;
import com.het.protocol.bean.PacketBean;
import com.het.protocol.core.PacketParseException;
import com.het.protocol.core.factory.io.IIn;
import com.het.protocol.util.ProtoUtils;
import com.het.protocol.util.Contants;

import java.nio.ByteBuffer;

/**
 * Created by uuxia-mac on 15/6/20.
 * version 41协议版本 数据解析类
 */
public class InPacket_v41 extends Packet_v41 implements IIn {

    public InPacket_v41(PacketBean packetBean) {
        super(packetBean);
    }

    /**
     * 校验头部
     *
     * @return true表示头部有效
     */
    @Override
    public boolean validateHeader(ByteBuffer buf) throws PacketParseException {
        if (buf != null && packetStart == buf.get() && buf.capacity() >= Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER) {
            return true;
        } else {
            throw new PacketParseException("包头长度错误.实际长度:" + buf.capacity() + " 理论长度:" + Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER);
        }
    }

    @Override
    public byte[] calcBody(ByteBuffer buf, int length) throws PacketParseException {
        /**得到包体长度*/
        int bodyLen = length - Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
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

    /**
     * 从buf的当前位置解析包头
     *
     * @param buf ByteBuffer
     */
    @Override
    public void parseHeader(ByteBuffer buf) throws PacketParseException {
//        buf.get(packetStart); //在验证报头正确性的时候已经get出来了
        protocolVersion = buf.get();
        protocolType = buf.get();
        commandType = buf.getShort();
        buf.get(macAddr);
        buf.get(deviceType);
        reserved = buf.get();
        dataLen = buf.getShort();
    }

    /**
     * 从buf的当前未知解析包尾
     *
     * @param buf ByteBuffer
     */
    @Override
    public void parseTail(ByteBuffer buf)
            throws PacketParseException {
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
        byte[] type = getDeviceType();
        String mac = ProtoUtils.byteToMac(getMacAddr());
        dm.setProtocolVersion(getProtocolVersion());
        dm.setProtocolType(getProtocolType());
        if (!ProtoUtils.isNull(mac)) {
            dm.setDeviceMac(mac);
        }
        if (type != null && type.length == 2) {
            dm.setDeviceType(type[0]);
            dm.setDeviceSubType(type[1]);
        }
        ////1-绑定成功  0-未绑定
        int bindStatus = ((reserved >>> 7) & 0x01);
        dm.setDeviceBindStatus(bindStatus);
        packetBean.setDeviceInfo(dm);
        packetBean.setBody(getFrameBody());
        return packetBean;
    }

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
//        if (LOG.PACKET_VERSION_OFF) Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"version41 protocol data packetIn finish...");
    }
}
