package com.het.protocol.coder.decode;

import com.het.protocol.coder.exception.DecodeException;

import java.nio.ByteBuffer;

public class ProtocolF241Decoder extends AbstractDecoder{
    @Override
    void validate(byte[] bytes) throws DecodeException {

    }

    @Override
    byte[] parseData(ByteBuffer buffer) throws DecodeException {
        return new byte[0];
    }

    @Override
    byte[] getCRC() throws DecodeException {
        return new byte[0];
    }


    @Override
    int getCrcBodySize() throws DecodeException {
        return 0;
    }

    @Override
    void parseDevice(byte[] dev) throws DecodeException {

    }

    @Override
    <T> T toBean() {
        return null;
    }
}
