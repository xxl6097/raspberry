package com.het.protocol.coder.decode;

import com.het.protocol.coder.decode.inter.Decoder;
import com.het.protocol.coder.exception.DecodeException;
import com.het.protocol.util.ProtoUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class AbstractDecoder implements Decoder {
    @Override
    public <T> T decode(Object data) throws DecodeException {
        if (data == null)
            return null;
        if (data instanceof byte[]) {
            byte[] bytes = (byte[]) data;
            validate(bytes);
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            byte[] dev = parseData(buffer);
            parseDevice(dev);
            checkCRC(getCRC(), bytes);
            return toBean();
        }
        return null;
    }

    protected String parseDevMacAddr(byte[] data) {
        return ProtoUtils.byteToMac(data);
    }

    protected void checkCRC(byte[] crc1, byte[] data) throws DecodeException {
        int len = getCrcBodySize();
        byte[] cBody = new byte[len];
        System.arraycopy(data, 1, cBody, 0, len);
        byte[] fcs = ProtoUtils.CRC16Calc(cBody, cBody.length);
        boolean isCRC = Arrays.equals(crc1, fcs);
        if (!isCRC) {
            throw new DecodeException("crc error,actually crc is " + ProtoUtils.toHexStrings(crc1) + ",calc crc is " + ProtoUtils.toHexStrings(fcs), DecodeException.ERR.DECODE_CRC_ERROR);
        }
    }

    abstract void validate(byte[] bytes) throws DecodeException;

    abstract byte[] parseData(ByteBuffer buffer) throws DecodeException;

    abstract byte[] getCRC() throws DecodeException;

    abstract int getCrcBodySize() throws DecodeException;


    abstract void parseDevice(byte[] dev) throws DecodeException;

    abstract <T> T toBean();

}
