package com.het.protocol.coder.decode;

import com.het.protocol.coder.bean.ProtocolBean;
import com.het.protocol.coder.exception.DecodeException;

import java.nio.ByteBuffer;

public class Protocol5ADecoder extends AbstractDecoder {
    final int PACKET_SIZE = 35;
    private ProtocolBean bean;

    @Override
    void validate(byte[] bytes) throws DecodeException {
        if (bytes == null) {
            throw new DecodeException("data is null", DecodeException.ERR.DECODE_BODYLEN_ERROR);
        }
        int total = bytes.length;
        if (total < PACKET_SIZE) {
            throw new DecodeException("body less total", DecodeException.ERR.DECODE_BODYLEN_ERROR);
        }
        if (total <= 0) {
            throw new DecodeException("data len is zero", DecodeException.ERR.DECODE_BODYLEN_ERROR);
        }
    }

    @Override
    byte[] parseData(ByteBuffer buffer) throws DecodeException {
        bean = new ProtocolBean();
        bean.setHead(buffer.get());
        bean.setLength((short) (buffer.getShort() - PACKET_SIZE + 1));
        int bodyLen = buffer.capacity() - PACKET_SIZE;
        if (bean.getLength() != bodyLen) {
            throw new DecodeException("read size is " + bean.getLength() + " total is " + buffer.capacity() + ", actually body len is " + bodyLen, DecodeException.ERR.DECODE_DATALEN_ERROR);
        }
        bean.setProtoVersion(buffer.get());
        bean.setProtoType(buffer.get());
        byte[] dev = new byte[8];
        buffer.get(dev);
        byte[] mac = new byte[6];
        buffer.get(mac);
        bean.setDevMacAddr(parseDevMacAddr(mac));
        bean.setFrameSN(buffer.getInt());
        byte[] reserved = new byte[8];
        buffer.get(reserved);
        bean.setReserved(reserved);
        bean.setCommand(buffer.getShort());
        byte[] body = new byte[bean.getLength()];
        buffer.get(body);
        bean.setBody(body);
        byte[] crc = new byte[2];
        buffer.get(crc);
        bean.setFcs(crc);
        return dev;
    }

    @Override
    byte[] getCRC() throws DecodeException {
        return bean.getFcs();
    }

    @Override
    int getCrcBodySize() throws DecodeException {
        if (bean == null)
            throw new DecodeException("get crc body error,data is null", DecodeException.ERR.DECODE_BODYLEN_ERROR);
        int len = PACKET_SIZE + bean.getLength() - 3;
        return len;
    }

    @Override
    void parseDevice(byte[] dev) throws DecodeException {
        if (dev == null)
            throw new DecodeException("data len is zero", DecodeException.ERR.DECODE_DEV_ERROR);
        if (dev.length != 8)
            throw new DecodeException("data len is not 8", DecodeException.ERR.DECODE_DEV_ERROR);
        ByteBuffer b = ByteBuffer.allocate(dev.length);
        b.put(dev);
        b.flip();
        bean.setCustomerId(b.getInt());
        bean.setDevType(b.getShort());
        bean.setDevSubType(b.get());
        bean.setDataVersion(b.get());
    }

    @Override
    ProtocolBean toBean() {
        return bean;
    }
}
