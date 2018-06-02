package com.het.udp.core.thread;

import android.text.TextUtils;

import com.het.udp.core.UdpDataManager;
import com.het.udp.core.Utils.IpUtils;
import com.het.udp.core.observer.IObserver;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.packet.factory.vopen.GenerateOpenPacket;
import com.het.udp.wifi.utils.ByteUtils;
import com.het.udp.wifi.utils.Contants;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LocalServerManager implements IObserver {
    private static LocalServerManager INSTANCE = null;
    private Set<IDataBack> dataBacks = new HashSet<IDataBack>();

    public LocalServerManager() {
        UdpDataManager.registerObserver(this);
    }

    public static LocalServerManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalServerManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new LocalServerManager();
                }
            }
        }
        return INSTANCE;
    }

    public void destroy() {
        UdpDataManager.unregisterObserver(this);
        dataBacks.clear();
    }

    public synchronized void addListener(IDataBack o) {
        if (o != null) {
            if (!dataBacks.contains(o)) {
                dataBacks.add(o);
            }
        }
    }

    public synchronized void delListener(IObserver o) {
        if (dataBacks.contains(o)) {
            dataBacks.remove(o);
        }
    }


    public int searchLocalServer() {
        int frameNo = ByteUtils.calcFrameNumber();
        GenerateOpenPacket generateOpenPacket = new GenerateOpenPacket();
        generateOpenPacket.setFrameNo(frameNo);
        PacketModel packetModel = generateOpenPacket.generate(Contants.GATEWAY_DISCOVER_RECV);
        PacketUtils.out(packetModel);
        try {
            UdpDataManager.getInstance().send(packetModel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return generateOpenPacket.getFrameNo();
        }
    }


    private void processLocalServerInfo(PacketModel packetModel) {
        String mac = packetModel.getMacAddr();
        if (TextUtils.isEmpty(mac))
            return;
        UdpDeviceDataBean device = packetModel.getDeviceInfo();
        if (device == null)
            return;
        byte[] body = packetModel.getBody();
        if (body == null)
            return;
        ByteBuffer buf = ByteBuffer.wrap(body);
        byte[] ipArr = new byte[4];
        buf.get(ipArr);
        String ip = IpUtils.binaryArray2Ipv4Address(ipArr);
        short port = buf.getShort();
        int productId = buf.getInt();
        device.setLocalServerIp(ip);
        device.setLocalServerPort(port);
        device.setProductId(productId);

        int capacity = buf.capacity();
        int position = buf.position();
        int signKeyLen = capacity - position;
        if (signKeyLen == 32){
            byte[] signKey = new byte[signKeyLen];
            buf.get(signKey);
            device.setGatewaySignKey(signKey);
        }
        notifyObservers(device);
    }

    public synchronized void notifyObservers(UdpDeviceDataBean obj) {
        if (dataBacks == null || dataBacks.size() == 0)
            return;
        Iterator<IDataBack> it = dataBacks.iterator();
        while (it.hasNext()) {
            IDataBack mgr = it.next();
            mgr.onData(obj);
        }
    }

    @Override
    public void receive(PacketModel packetModel) {
        if (packetModel == null)
            return;

        //处理本地服务器信息 9200
        if (Contants.GATEWAY_DISCOVER_SEND == packetModel.getCommand()||Contants.GATEWAY_DISCOVER_SEND1 == packetModel.getCommand()) {
            processLocalServerInfo(packetModel);
        }
    }

    public interface IDataBack<UdpDeviceDataBean> {
        void onData(com.het.udp.wifi.model.UdpDeviceDataBean data);
    }
}
