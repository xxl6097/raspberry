package com.het.udp.wifi.core;

import com.het.log.Logc;
import com.het.udp.wifi.callback.IRecevie;
import com.het.udp.wifi.model.PacketBuffer;
import com.het.udp.wifi.packet.factory.v41.Packet_v41;
import com.het.udp.wifi.packet.factory.v42.Packet_v42;
import com.het.udp.wifi.packet.factory.vopen.Packet_open;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;
import com.het.udp.wifi.utils.LOG;

import java.nio.ByteBuffer;

/**
 * Created by uuxia-mac on 15/8/29.
 */
public class DataIssue extends BaseThread {

    final static byte HEAD_F2 = (byte) 0xF2;
    final static byte HEAD_5A = 0x5A;
    //接收线程
    private static final byte HEAD = (byte) 0xF2;
    ByteBuffer hfBuffer = ByteBuffer.allocate(200);
    // 缓冲BUFF
    private byte[] cashBuffer = new byte[1024 * 1024];
    // 当前BUFFER 的总数byte,（位置指引）
    private int currentSizeNew = 0;
    // 缓冲BUFF
    private byte[] cashBufferNew = new byte[4096];
    private String localIp;
    private IRecevie callback;

    public DataIssue() {
        setName("DataIssue");
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    @Override
    public void run() {
        super.run();
        while (runnable) {
            try {
                //从队列中取数据，如果队列为空，则阻塞在此处
                PacketBuffer packet = inQueue.take();
//              Logc.i(Logc.HetReportTag.INFO_WIFI,"出->" + inQueue.size());
                recv(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void close() {
        runnable = false;
    }

    private void recv(PacketBuffer packet) throws Exception {
        if (packet == null)
            throw new Exception("packet is null...");
        byte[] data = packet.getData();
        if (data == null || data.length == 0)
            throw new Exception("data is null or length is zero...");
        int len = packet.getLength();
        if (data == null || data.length == 0)
            throw new Exception("data length is Invalid...");
        String ip = packet.getIp();
        if (localIp != null && localIp.equals(ip))
            return;
        byte[] recv = checkData(data, len, ip);
        if (callback == null) {
            throw new Exception("please set callback method...");
        }
        if (recv != null) {
            packet.setData(recv);
            callback.onRecevie(packet);
            if (LOG.UDP_SEND_RECV_OFF) {
                String cmd;// = ByteUtils.getCmd(recv);
                String mac;// = ByteUtils.getMacAddr(recv);
                if (recv[0] == Packet_open.packetStart) {
                    cmd = ByteUtils.getCmdForOPen(recv);//getCmd(recv, 31);
                    mac = ByteUtils.getMacAddr(recv, 13);
                } else {
                    cmd = ByteUtils.getCmd(recv, 3);
                    mac = ByteUtils.getMacAddr(recv, 5);
                }
//                System.out.println("udp.recv->" + cmd + " mac=" + mac + " ip=" + ByteUtils.toIp(ip) + ":" +
//                        packet
//                                .getPort() + " " + ByteUtils.toHexString(recv));
                Logc.e(Logc.HetLogRecordTag.INFO_WIFI,"udp.recv->" + cmd + " mac=" + mac + " ip=" + ByteUtils.toIp(ip) + ":" +
                        packet
                                .getPort() + " " + ByteUtils.toHexString(recv));
            }
        }
    }

    public byte[] verifyData_v42(byte[] data, int size, String ip) {
        int i = 0;// 记录，数据头--位置
        if (0 == currentSizeNew) { // currentSize代表数据缓冲区的当前的大小
            // 如果当前缓冲区没数据，先找到数据的头
            while ((i < size) && (data[i] != HEAD)) {//i小于总size，且不等于报头则继续找报头
                //如果接收到得数据不是协议格式数据，可判断是汉风发送的字符串指令
                if (hfBuffer.position() < hfBuffer.capacity()) {
                    hfBuffer.put(data[i]);
                }
                i++;
            }
            // 如果找到了，就截取数据
            if (hfBuffer.position() > 0) {
                hfBuffer.flip();
                issueHfString(hfBuffer, ip);
                hfBuffer.clear();
            }
            //i=0表示报文头完整
            // 如果没找到就不要这帧数据
            if (i == size) {
                return null;
            }
            // 如果找到了，就截取数据
            size -= i;
            System.arraycopy(data, i, cashBufferNew, 0, size);
        } else {
            // 如果缓冲区有数据，直接把数据追加到缓冲区cashBuffer
            System.arraycopy(data, 0, cashBufferNew, currentSizeNew, size);
        }
        currentSizeNew += size;
        do {
            //默认V41版本协议包体长度；
            int whichVersionProtocolLength = getWhichProtocolVersionLength(cashBufferNew);
            // 如果当前数据包长度小于最小包长，说明数据分包了，就直接跳出循环
            if (currentSizeNew < whichVersionProtocolLength) {
                return null;
            }

            // 如果当前数据包长度大于最小包长度，就计算该数据帧的长度
            // 获取有效数据长度 00 0e
            int pos = getWhichProtocolVersionFrameBodyPosition(cashBufferNew);
            int dataLen = ByteUtils.getDataLength(cashBufferNew[pos], cashBufferNew[pos + 1]);
            int pktLen = dataLen + whichVersionProtocolLength;//Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
            // ----------也许会出问题--------------------------------
            if (pktLen > 4000) {
                currentSizeNew = 0;
                return null;
            }
            // 如果包长度大于currentSize，说明数据包补完整，跳出循环
            if (pktLen > currentSizeNew) {
                return null;
            }
            // 如果数据包小于等于currentSize，就拷贝数据到
            byte[] fullpacket = new byte[pktLen];
            System.arraycopy(cashBufferNew, 0, fullpacket, 0, pktLen);
            // 拷贝完数据包后，就开始整理缓冲区---总的数据--
            if (currentSizeNew > pktLen) {// 对多个包进行，数据整理，保证数据开头为0 7E7E （保证准确性）
                // 从剩下的数据中开始找到数据头部
                for (i = pktLen; i < currentSizeNew; i++) {
                    if ((cashBufferNew[i] == HEAD)) {
                        break;// 找到数据头以后就跳出循环
                    }
                    //找不到数据头，则后面接的可能是HF字符串
                    if (hfBuffer.position() < hfBuffer.capacity()) {
                        hfBuffer.put(cashBuffer[i]);
                    }
                }

                if (hfBuffer.position() > 0) {
                    hfBuffer.flip();
                    issueHfString(hfBuffer, ip);
                    hfBuffer.clear();
                }
                // 开始移动数据到缓存区前面
                int j = 0;
                for (; i < currentSizeNew; i++) {
                    cashBufferNew[j++] = cashBufferNew[i];
                }
                // 重置当前的缓冲区大小
                currentSizeNew = j;
            } else {
                currentSizeNew = 0;// 数据全部取走
            }
            if (/*fullpacket[pktLen - 1] == TAIL*/ByteUtils.checkCRC16(fullpacket)) {
                return fullpacket;
            } else {
                if (LOG.MAIN_LOG_OFF)
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY check no pass" + ByteUtils.toHexString(cashBufferNew));
            }
            if (LOG.MAIN_LOG_OFF)
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY this is good packet");

        } while (true);
    }

    public byte[] checkData(byte[] data, int size, String ip) {
        int i = 0;// 记录，数据头--位置
        if (0 == currentSizeNew) { // currentSize代表数据缓冲区的当前的大小
            // 如果当前缓冲区没数据，先找到数据的头
            while ((i < size) && (data[i] != HEAD_F2 && data[i] != HEAD_5A)) {//i小于总size，且不等于报头则继续找报头
                //如果接收到得数据不是协议格式数据，可判断是汉风发送的字符串指令
                if (hfBuffer.position() < hfBuffer.capacity()) {
                    hfBuffer.put(data[i]);
                }
                i++;
            }
            // 如果找到了，就截取数据
            if (hfBuffer.position() > 0) {
                hfBuffer.flip();
                issueHfString(hfBuffer, ip);
                hfBuffer.clear();
            }
            //i=0表示报文头完整
            // 如果没找到就不要这帧数据
            if (i == size) {
                return null;
            }
            // 如果找到了，就截取数据
            size -= i;
            System.arraycopy(data, i, cashBufferNew, 0, size);
        } else {
            // 如果缓冲区有数据，直接把数据追加到缓冲区cashBuffer
            System.arraycopy(data, 0, cashBufferNew, currentSizeNew, size);
        }
        currentSizeNew += size;
        do {
            //默认V41版本协议包体长度；
            int headLength = getHeadLength(cashBufferNew);
            // 如果当前数据包长度小于最小包长，说明数据分包了，就直接跳出循环
            if (currentSizeNew < headLength) {
                return null;
            }

            // 如果当前数据包长度大于最小包长度，就计算该数据帧的长度
            // 获取有效数据长度 00 0e
            int bodyLength = getBodyLength(cashBufferNew);
            //报文体长度要大于等于0
            if (bodyLength < 0)
                return null;
//            int dataLen = ByteUtils.getDataLength(cashBufferNew[pos], cashBufferNew[pos + 1]);
            int pktLen = bodyLength + headLength;//Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
            // ----------也许会出问题--------------------------------
            if (pktLen > 4000) {
                currentSizeNew = 0;
                return null;
            }
            // 如果包长度大于currentSize，说明数据包补完整，跳出循环
            if (pktLen > currentSizeNew) {
                return null;
            }
            // 如果数据包小于等于currentSize，就拷贝数据到
            byte[] fullpacket = new byte[pktLen];
            System.arraycopy(cashBufferNew, 0, fullpacket, 0, pktLen);
            // 拷贝完数据包后，就开始整理缓冲区---总的数据--
            if (currentSizeNew > pktLen) {// 对多个包进行，数据整理，保证数据开头为0 7E7E （保证准确性）
                // 从剩下的数据中开始找到数据头部
                for (i = pktLen; i < currentSizeNew; i++) {
                    if (cashBufferNew[i] == HEAD_F2 || cashBufferNew[i] == HEAD_5A) {
                        break;// 找到数据头以后就跳出循环
                    }
                    //找不到数据头，则后面接的可能是HF字符串
                    if (hfBuffer.position() < hfBuffer.capacity()) {
                        hfBuffer.put(cashBuffer[i]);
                    }
                }

                if (hfBuffer.position() > 0) {
                    hfBuffer.flip();
                    issueHfString(hfBuffer, ip);
                    hfBuffer.clear();
                }
                // 开始移动数据到缓存区前面
                int j = 0;
                for (; i < currentSizeNew; i++) {
                    cashBufferNew[j++] = cashBufferNew[i];
                }
                // 重置当前的缓冲区大小
                currentSizeNew = j;
            } else {
                currentSizeNew = 0;// 数据全部取走
            }
            if (/*fullpacket[pktLen - 1] == TAIL*/ByteUtils.checkCRC16(fullpacket)) {
                return fullpacket;
            } else {
                if (LOG.MAIN_LOG_OFF) {
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY check no pass data:" + ByteUtils.toHexString(fullpacket));
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY check no pass All :" + ByteUtils.toHexString(cashBufferNew));
                }
            }
            if (LOG.MAIN_LOG_OFF)
                Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"YYYYY this is good packet");

        } while (true);
    }

    private void issueHfString(ByteBuffer buf, String ip) {
        int len = buf.limit();
        byte[] hfStr = new byte[len];
        buf.get(hfStr);
        if (callback != null) {
            PacketBuffer packet = new PacketBuffer();
            packet.setIp(ip);
            packet.setData(hfStr);
            callback.onRecevie(packet);
        }
    }

    /**
     * 获取不同版本协议长度
     *
     * @param buffer
     * @return
     */
    private int getHeadLength(byte[] buffer) {
        if (buffer != null && buffer.length >= 2) {
            byte start = buffer[0];
            int version = buffer[1] & 0xFF;
            if (start == Packet_open.packetStart) {
                return Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN;
            } else if (start == Packet_v41.packetStart) {
                if (version == Packet_v41.PROTOCOL_VERSION) {
                    return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
                } else if (version == Packet_v42.PROTOCOL_VERSION) {
                    return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42;
                } else {
                    return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
                }
            }
        }
        return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
    }

    /**
     * 获取数据长度索引号
     *
     * @param buffer
     * @return
     */
    private int getBodyLength(byte[] buffer) {
        if (buffer != null && buffer.length >= 2) {
            byte start = buffer[0];
            int version = buffer[1] & 0xFF;
            if (start == Packet_open.packetStart) {
                if (buffer.length <= Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A) {
                    return -1;
                }
                //5A 协议数据长度=N+33 33不包括起始字节5A
//                return buffer[Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A] - Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A;
                int pos = Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN - Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A;
                return ByteUtils.getDataLength(cashBufferNew[pos], cashBufferNew[pos + 1]) - Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A;
            } else if (start == Packet_v41.packetStart) {
                int pos;
                if (version == Packet_v41.PROTOCOL_VERSION) {
                    pos = Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER - 4;
                } else if (version == Packet_v42.PROTOCOL_VERSION) {
                    pos = Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42 - 6;
                } else {
                    pos = Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER - 4;
                }
                if (buffer.length <= pos) {
                    return -1;
                }
                return ByteUtils.getDataLength(cashBufferNew[pos], cashBufferNew[pos + 1]);
            }
        }
        return -1;
    }

    private int getWhichProtocolVersionLength(byte[] buffer) {
        if (buffer != null && buffer.length >= 2) {
            if ((buffer[1] & 0xFF) == Packet_v41.PROTOCOL_VERSION) {
                return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
            } else if ((buffer[1] & 0xFF) == Packet_v42.PROTOCOL_VERSION) {
                return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_42;
            } else {
                return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
            }
        }
        return Contants.HET_LENGTH_NEW_BASIC_OUT_HEADER;
    }

    private int getWhichProtocolVersionFrameBodyPosition(byte[] buffer) {
        if (buffer != null && buffer.length >= 2) {
            if ((buffer[1] & 0xFF) == Packet_v41.PROTOCOL_VERSION) {
                return 14;
            } else if ((buffer[1] & 0xFF) == Packet_v42.PROTOCOL_VERSION) {
                return Contants.OPEN.HET_LENGTH_NEW_BASIC_OUT_HEADER_V_OPEN_NO_5A;
            } else {
                return 14;
            }
        }
        return 14;
    }

    public void setCallback(IRecevie callback) {
        this.callback = callback;
    }
}
