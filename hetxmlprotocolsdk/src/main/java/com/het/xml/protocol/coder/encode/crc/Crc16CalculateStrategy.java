package com.het.xml.protocol.coder.encode.crc;


import com.het.xml.protocol.coder.utils.Crc16Utils;

public class Crc16CalculateStrategy implements CrcCalculateStrategy {

    @Override
    public int calculate(byte[] data) throws Exception {
        int crc = Crc16Utils.computeChecksum(data, data.length);
        return crc;
    }

}
