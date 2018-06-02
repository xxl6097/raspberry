package com.het.udp.wifi.core;

import com.het.log.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.DeviceBindMap;
import com.het.udp.wifi.callback.OnSendListener;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.PacketReplyModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.factory.v41.Packet_v41;
import com.het.udp.wifi.packet.factory.v42.Packet_v42;
import com.het.udp.wifi.utils.ByteUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by uuxia on 2015/8/29.
 */
public class ReplyManager {
    public static ConcurrentHashMap<Integer, PacketReplyModel> replyMapper = new ConcurrentHashMap<Integer, PacketReplyModel>();
    public static int callAddCount, callRemoveCount;
    public static boolean isWindows = false;
    private static ReplyManager instance;

    static {
        String os = System.getProperty("os.name");
//        System.out.println("current os System is " + os);
        if (os.toLowerCase().contains("win") || os.toLowerCase().contains("mac")) {
            isWindows = true;
        } else {
            isWindows = false;
        }
    }

    public final int keepWaitTime = 2000;
    public com.het.udp.wifi.core.UdpManager udpManager;
    private Thread keepReply,addThread,removeThread;
    private boolean working = true;

    public static ReplyManager getInstance() {
        if (instance == null) {
            synchronized (ReplyManager.class) {
                if (null == instance) {
                    instance = new ReplyManager();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        byte[] testData = new byte[]{4, 5, 6, 7, 8, 8, 9, 5, 3, 4, 4, 5};
        int s = ByteUtils.CRC(testData, testData.length);
//        System.out.println("uuuuuuuxia=" + s);
//        PacketModel packets = new PacketModel();
//        packets.setBody(new byte[]{1,2,3,4,5,6,7,8,9,11,22,33,44,55,66,77,88,99});
//        addPacketReply(packets);
    }

    /**
     * 标记发出数据需要回复
     *
     * @param packet
     */
    public static void makePacketReply(PacketModel packet, String localIp) {
        if (packet == null)
            return;
        int index = 0;
        if (packet.getProtocolVersion() == Packet_v42.PROTOCOL_VERSION){
            index = 32;
        }else{
            index = 33;
        }
        byte[] body = packet.getBody();
        if (body == null || body.length <= index) {
            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"device's data is null.");
            return;
        }
        if (packet.getDeviceInfo() == null) {
            packet.setDeviceInfo(new UdpDeviceDataBean());
        }
//        if (!isWindows && !UdpDataManager.DEBUG) {
//            index = 33;
//        }
        packet.getDeviceInfo().setNeedReply(true);
        //对数据进行回复
        body[index] = 0x40;//0x40 | 0x01;
        //设置本机IP
//        body[index + 1] = IpUtils.getIpLastByte(localIp);

        //此处因为F2的协议数据帧只有了两个字节，所以5A用3个字节
        if (packet.getPacketStart() == Packet_v41.packetStart){
            short frameNo = ByteUtils.calcFrameShort();
            //禁止序号为0;
            if (frameNo == 0)
                frameNo++;
            packet.getDeviceInfo().setFrameSN(frameNo);
            ByteUtils.putShort(frameNo, body, index + 2);
        }else{
            int frameNo = ByteUtils.calcFrameNumber();
            //禁止序号为0;
            if (frameNo == 0)
                frameNo++;
            packet.getDeviceInfo().setFrameSN(frameNo);
            //设置数据帧序号
            putBodyFrameSn(frameNo, body, index + 1);
//            System.out.println(packet.getPacketStart()+"@@@@@@@ frameNo:"+frameNo + " body:"+ ByteUtils.toHexString(body));
        }
    }

    public static void putBodyFrameSn(int sht, byte[] sb, int index) {
        for (int i = 0; i < 3; i++) {
            sb[2 - i + index] = (byte) (sht >> (i * 8) & 0xFF);
        }
    }

    public static void makePacketNoReply(PacketModel packet, String localIp) {
        //Logc.i(Logc.HetReportTag.INFO_WIFI,"uuuuuuuuuuuu", ByteUtils.toHexString(packet.getData()));
        if (packet == null)
            return;
        byte[] body = packet.getBody();
        if (body == null || body.length < 4)
            return;
        if (packet.getDeviceInfo() == null) {
            packet.setDeviceInfo(new UdpDeviceDataBean());
        }
        int index = 0;
//        if (!isWindows && !UdpDataManager.DEBUG){
//            index = 33;
//        }
        packet.getDeviceInfo().setNeedReply(false);
        body[index] &= 0xFE;//使低四位的低一位为0，即表示数据不需要回复
        //设置本机IP
        body[index + 1] = 0;//IpUtils.getIpLastByte(localIp);
    }

    public void startKeepReply(com.het.udp.wifi.core.UdpManager udp) {
        this.udpManager = udp;
        //System.err.println("ppppppp-出-run>startKeepReply");
        if (keepReply == null) {
            keepReply = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (replyMapper) {
                        while (working) {
                            while (replyMapper.size() == 0) {
                                try {
                                    replyMapper.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    replyMapper.notifyAll();
                                }
                            }
                            trigger();
                            Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"~~~~~~~~~~startKeepReply." + replyMapper.toString());
                            //System.err.println("ppppppp-出-run>" + Test.getStringDate()+" trigger");
                            try {
                                replyMapper.wait(keepWaitTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                replyMapper.notifyAll();
                            }
                        }
                    }
                }
            });
            keepReply.setName("keepReply--数据重传机制");
            keepReply.start();
        }

    }

    private void trigger() {
        //replyMapper存储所有补发数据，这里需要轮询的取出来并且发送出去
        Iterator<Integer> it = replyMapper.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            PacketReplyModel value = replyMapper.get(key);
            if (value == null || value.getPacket() == null) {
                it.remove();
                continue;
            }
            //获取当前补发帧已经发送的次数
            int count = value.getReplyCount();
            value.setReplyCount(++count);
            //如果该帧数据已经发了10次，则丢弃该帧数据，表示该帧数据已经失败
            if (count >= 10) {
                it.remove();
                PacketModel packetItem = value.getPacket();
                if (packetItem != null){
                    OnSendListener callback = packetItem.getOnSendListener();
                    if (callback != null){
                        packetItem.setOnSendListener(null);
                        packetItem.setCommand(UdpDataManager.HET_CONFIG_DATA);
                        callback.onSendFailed(callback.getCmd(),packetItem,new Exception("had been sent repet 10 times"));
                    }
                }
                if (UdpDataManager.DEBUG) {
//                    Test.outQueue.poll();
//                    System.out.println("------------------------------------trigger===============" + Test.outQueue.size());
                }
//                System.out.println("==================trigger======================" + key + ":" + count + " " + replyMapper.toString());
                continue;
            }
            if (isWindows && udpManager == null) {
                it.remove();
                continue;
            }
            byte[] data = value.getPacket().getData();
            if (data == null || data.length == 0) {
                it.remove();
                continue;
            }
            String ip = value.getPacket().getIp();
            if (ip == null || "".equals(ip)) {
                it.remove();
                continue;
            }

            //每一帧的数据发送间隔时间要大于一秒
            if (value.getDelayTime() >= 1) {
                long delay = value.getDelayTime();
                delay--;
                value.setDelayTime(delay);
                continue;
            }

            UdpDeviceDataBean dm = value.getPacket().getDeviceInfo();
            if (dm == null) {
                dm = new UdpDeviceDataBean();
                value.getPacket().setDeviceInfo(dm);
            }
            //标记改帧数据需要补发
            dm.setNeedReply(true);
            //标记改帧数据属于补发数据
            dm.setAgainData(true);
            //System.err.println("ppppppp-出start->" + value.toString() + "---------" + value.getPacket().toString());
            try {
                if (isWindows) {
//                    if (udpManager != null) {
//                        udpManager.send(data, ip, port);
//                    }
                } else {
                    UdpDataManager.getInstance().send(value.getPacket());
//                    System.err.println("ppppppp-reply-send>" + value.getPacket().toString());
                }
            } catch (InterruptedException e) {
//                System.err.println("ppppppp-出-excepition1>" + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
//                System.err.println("ppppppp-出-excepition2>" + e.getMessage());
                e.printStackTrace();
            }
            //System.err.println("ppppppp-出-end>" + Thread.currentThread().getName() + " " + value.getPacket().getIp() + " 发送次数:" + count + " 字节数:" + (count * value.getPacket().getData().length) + " " + ByteUtils.toHexString(value.getPacket().getData()));
        }
        replyMapper.notifyAll();
    }

    /**
     * 将待重发数据添加到队列
     *
     * @param packet
     */
    public void addPacketReply(final PacketModel packet) {
        if (packet == null || packet.getDeviceInfo() == null)
            return;
        final int key = packet.getDeviceInfo().getFrameSN();//getvalidateData(packet);
//        System.out.println("uuu 小循环key=addPacketReply==" + key);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (replyMapper != null) {
                    clearMapper();
                    Thread.State state = keepReply.getState();
                    if (state.equals(Thread.State.WAITING)) {
                        synchronized (replyMapper) {
                            if (key != 0) {
                                eliminate(packet);
                                replyMapper.put(key, new PacketReplyModel(0, packet));
                                //System.err.println("ppppppp-出-syn-addPacketReply>" + replyMapper.size());
                            }
                            replyMapper.notifyAll();
                        }
                    } else {
                        if (key != 0) {
                            eliminate(packet);
                            replyMapper.put(key, new PacketReplyModel(0, packet));
                            //System.err.println("ppppppp-出-xxl-addPacketReply>" + replyMapper.size());
                        }
                    }
                    callAddCount++;
                }
            }
        }, "addPacketReply").start();
    }

    private void clearMapper(){
        synchronized (replyMapper) {
            Set<Integer> keySet = replyMapper.keySet();
            if (keySet != null) {
                Iterator<Integer> it = keySet.iterator();
                if (it != null) {
                    while (it.hasNext()) {
                        int key = it.next();
                        PacketReplyModel item = replyMapper.get(key);
                        if (item != null) {
                            PacketModel packet = item.getPacket();
                            if (packet != null) {
                                OnSendListener callback = packet.getOnSendListener();
//                                if (callback != null) {
//                                    packet.setOnSendListener(null);
//                                    packet.setCommand(UdpDataManager.HET_CONFIG_DATA);
//                                    callback.onSendSucess(callback.getCmd(), packet);
//                                }
                            }
                        }
                        it.remove();
                    }
                }
            }
        }
    }

    /**
     * 剔除相同updateFlag的数据包
     *
     * @param packet
     */
    private void eliminate(PacketModel packet) {
        Iterator<Integer> it = replyMapper.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            PacketReplyModel value = replyMapper.get(key);
            if (value != null && value.getPacket() != null && packet != null) {
                byte[] updateFlag1 = value.getPacket().getUpdateFlag();
                byte[] updateFlag2 = packet.getUpdateFlag();
                if (updateFlag2 == null)
                    return;
                if (updateFlag1 == null)
                    continue;
                if (Arrays.equals(updateFlag2, updateFlag1)) {
                    it.remove();
                    //System.out.println("ppppppp-eliminate" + replyMapper.size());
                }
            }
        }
    }

    /**
     * 将回复包从列表中移除
     *
     * @param packet
     */
    public void removeReplyPacket(final PacketModel packet) {
        if (packet == null || packet.getDeviceInfo() == null)
            return;
        final int key = packet.getDeviceInfo().getFrameSN();//getvalidateData(packet);
        if (replyMapper.keySet().contains(key)) {
            PacketReplyModel item = replyMapper.get(key);
//            System.out.println("小循环uuuremoveReplyPacket "+key);
            if (item != null){
                PacketModel packetItem = item.getPacket();
                if (packetItem != null){
                    notifyUdpStatus(packet,item);
//                    OnSendListener callback = packetItem.getOnSendListener();
//                    if (callback != null){
//                        packet.setCommand(UdpDataManager.HET_CONFIG_DATA);
//                        callback.onSendSucess(callback.getCmd(),packet);
//                    }
//
////                    System.out.println("udp.@@removeReplyPacket.key:" + key+" "+packetItem.toString());
//                    if (packetItem.getMacAddr() != null) {
//                        //老设备做特殊处理
//                        if (packetItem.getDeviceInfo() != null && packetItem.getDeviceInfo().isBeUseOldUserKey()) {
//                            DeviceBindMap.specialMacSets.add(packetItem.getMacAddr().toUpperCase());
//                        } else {
//                            DeviceBindMap.normalMacSets.add(packetItem.getMacAddr().toUpperCase());
//                        }
//                    }
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (replyMapper != null) {
                        Thread.State state = keepReply.getState();
                        if (state.equals(Thread.State.WAITING)) {
                            synchronized (replyMapper) {
                                PacketReplyModel pack = replyMapper.remove(key);
//                                notifyUdpStatus(packet,pack);
//                                System.out.println("uuuuuu.synchronized.key:"+key);
                                replyMapper.notifyAll();
                            }
                        } else {
                            synchronized (replyMapper) {
                                //System.err.println("ppppppp-出-removeReplyPacket>" + key);
//                                System.out.println("uuuuuu.NO.synchronized.key:"+key);
                                PacketReplyModel pack = replyMapper.remove(key);
//                                notifyUdpStatus(packet, pack);
                            }

                        }
                        if (UdpDataManager.DEBUG) {
//                            Test.outQueue.poll();
//                            System.out.println("------------------------------------removeReplyPacket===============" + Test.outQueue.size());
                        }
                    }
                    callRemoveCount++;
                }
            }, "removeReplyPacket").start();
        }
    }

    private void notifyUdpStatus(PacketModel recvData, PacketReplyModel item){
//        PacketReplyModel item = replyMapper.get(key);
        if (item != null){
//            System.out.println("uuuremoveReplyPacket "+item.toString());
            PacketModel packetItem = item.getPacket();
            if (packetItem != null){
                OnSendListener callback = packetItem.getOnSendListener();
                if (callback != null){
                    packetItem.setOnSendListener(null);
                    recvData.setCommand(UdpDataManager.HET_CONFIG_DATA);
                    callback.onSendSucess(callback.getCmd(),recvData);
                }

                if (packetItem.getMacAddr() != null) {
                    //老设备做特殊处理
                    if (packetItem.getDeviceInfo() != null && packetItem.getDeviceInfo().isBeUseOldUserKey()) {
                        DeviceBindMap.specialMacSets.add(packetItem.getMacAddr().toUpperCase());
                    } else {
                        DeviceBindMap.normalMacSets.add(packetItem.getMacAddr().toUpperCase());
                    }
                }
            }
        }
    }

    public int getvalidateData(PacketModel packet) {
        if (packet != null) {
            byte[] body = packet.getBody();
            if (body != null && body.length > 0) {
                byte[] key = new byte[body.length - 4];
                System.arraycopy(body, 4, key, 0, key.length);
                if (key != null && key.length > 0) {
                    return ByteUtils.CRC(key, key.length);
                }
            }
        }
        return -1;
    }
}
