package com.het.protocol;

import com.het.protocol.bean.PacketBean;
import com.het.protocol.coder.bean.ProtocolBean;
import com.het.protocol.coder.decode.AbstractDecoder;
import com.het.protocol.coder.decode.Protocol5ADecoder;
import com.het.protocol.coder.decode.ProtocolF241Decoder;
import com.het.protocol.coder.decode.ProtocolF242Decoder;
import com.het.protocol.coder.encode.AbstractEncoder;
import com.het.protocol.coder.encode.Protocol5AEncoder;
import com.het.protocol.coder.encode.ProtocolF241Encoder;
import com.het.protocol.coder.encode.ProtocolF242Encoder;
import com.het.protocol.coder.util.ProtocolConst;
import com.het.protocol.core.PacketParseException;
import com.het.protocol.core.factory.IPacketIn;
import com.het.protocol.core.factory.manager.impl.PacketManager;
import com.het.protocol.util.ProtoUtils;

public class HeTProtoSDK {
    private static HeTProtoSDK api;
    /**
     * 协议解码器
     */
    private AbstractDecoder decoder;

    /**
     * 设疑编码器
     */
    private AbstractEncoder encoder;

    public static HeTProtoSDK getApi() {
        if (api == null) {
            synchronized (HeTProtoSDK.class) {
                if (null == api) {
                    api = new HeTProtoSDK();
                }
            }
        }
        return api;
    }

    public ProtocolBean decode(byte[] data) throws Exception {
        if (data == null)
            throw new IllegalArgumentException("data is null...");
        if (data.length <= 0)
            throw new IllegalArgumentException("data array is zero");
        byte head = data[0];
        if (head == ProtocolConst.PACKET_5A) {
            decoder = new Protocol5ADecoder();
        } else if (head == ProtocolConst.PACKET_F2) {
            int version = ProtoUtils.getProtocolVersion(data) & 0xFF;
            if (version == ProtocolConst.Verion.PROTOCOL_VERSION_41) {
                decoder = new ProtocolF241Decoder();
            } else if (version == ProtocolConst.Verion.PROTOCOL_VERSION_42) {
                decoder = new ProtocolF242Decoder();
            }
        }

        if (decoder == null)
            throw new IllegalArgumentException("not found decoder protocol");

        return decoder.decode(data);
    }

    public byte[] encode(ProtocolBean protocolBean) throws Exception {
        if (protocolBean == null)
            throw new IllegalArgumentException("protocolBean is null...");
        if (protocolBean.getHead() == ProtocolConst.PACKET_5A) {
            encoder = new Protocol5AEncoder();
        } else if (protocolBean.getHead() == ProtocolConst.PACKET_F2) {
            int version = protocolBean.getProtoVersion() & 0xFF;
            if (version == ProtocolConst.Verion.PROTOCOL_VERSION_41) {
                encoder = new ProtocolF241Encoder();
            } else if (version == ProtocolConst.Verion.PROTOCOL_VERSION_42) {
                encoder = new ProtocolF242Encoder();
            }
        }

        if (encoder == null)
            throw new IllegalArgumentException("not found encoder protocol");
        return encoder.encode(protocolBean);
    }


    /**
     * 解码报文
     */
    public static void decode(PacketBean packet) throws PacketParseException {
        PacketManager packetManager = new PacketManager(packet);
        IPacketIn packetIn = packetManager.createIn();
        packetIn.packetIn();
    }


    /**
     * 编码报文
     */
    public static byte[] encode(PacketBean packet) {
        return new PacketManager(packet).createOut().packetOut();
    }


    public static void main(String[] args) {
        System.out.println("hello world!");
        byte[] runData = new byte[]{(byte) 0x5A, (byte) 0x00, (byte) 0x52,
                (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0x99, (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x01,
                (byte) 0x8C, (byte) 0x18, (byte) 0xD9, (byte) 0xFF, (byte) 0xEB,
                (byte) 0x9D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x05,
                (byte) 0x00, (byte) 0x02, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xFF,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x03,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03,
                (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32, (byte) 0x79};
        try {
            ProtocolBean data = HeTProtoSDK.getApi().decode(runData);
            System.out.println(data.toString());

            byte[] ret = HeTProtoSDK.getApi().encode(data);
            System.out.println(ProtoUtils.toHexString(ret));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
