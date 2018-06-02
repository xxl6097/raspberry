package com.het.udp.core.smartlink;

import com.het.log.Logc;
import com.het.udp.core.UdpDataManager;
import com.het.udp.core.observer.IObserver;
import com.het.udp.core.smartlink.bind.IBindListener;
import com.het.udp.core.smartlink.bind.IScanListener;
import com.het.udp.core.smartlink.callback.OnDiffComplayEvents;
import com.het.udp.core.smartlink.ti.TiManager;
import com.het.udp.wifi.model.UdpDeviceDataBean;
import com.het.udp.wifi.model.PacketModel;
import com.het.udp.wifi.packet.PacketUtils;
import com.het.udp.wifi.utils.Contants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by uuxia on 2015/3/30.
 */
public abstract class SmartLinkBindBase extends SnifferSmartLinker implements IObserver {
    protected OnDiffComplayEvents diffComplayEvents;

    //设置与设备通讯超时时间 默认25秒
    protected final static float BINDTIMEOUT = 25;
    //////////////////////////
    protected final static String TAG = "smartlink";
    /////////TI绑定////////////
    protected TiManager tiManager;
    protected Set<String> findDeviceSet = new HashSet<String>();
    protected Hashtable<String, UdpDeviceDataBean> findDeviceTable = new Hashtable<String, UdpDeviceDataBean>();
    protected Set<String> deviceSet = new HashSet<String>();
    protected HashMap<String, UdpDeviceDataBean> selectSets = new HashMap<String, UdpDeviceDataBean>();

    protected HashSet<PacketModel> multiVersionServerData = new HashSet<PacketModel>();
    //设置绑定超时时间 默认100秒
    protected int scanTimeOut = 100;
    protected volatile boolean bIsBinding = false;
    protected boolean bScanning = true;

    protected int trasPort = UdpDataManager.mPort;
    //    protected String broadCastIP = "255.255.255.255";
    protected String ssid_pwd;
    protected int braodPort = 49999;

    protected IScanListener onScanListener;
    protected IBindListener onBindListener;
    /**
     * 搜索连上wifi的设备，并作40s超时
     */
    private float mPersent = 0;

    public SmartLinkBindBase() {
        super();
    }

    /**
     * 设置扫描超时时间  最小超时需要30s
     *
     * @param scanTimeOut
     */
    public void setScanTimeOut(int scanTimeOut) {
        if (scanTimeOut < 30) {
            scanTimeOut = 30;
        }
        this.scanTimeOut = scanTimeOut;
    }

    public void stopScan() {
        isConnecting = false;
        bScanning = false;
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "停止扫描...");
    }

    protected void startConfig(){
        if (diffComplayEvents != null) {
            diffComplayEvents.onConfigure(diffComplayEvents.getType());
            if (diffComplayEvents.getType() == OnDiffComplayEvents.ACCESS_ROUTER_STYLE_REALTEK) {
                //科中龙
            } else if (diffComplayEvents.getType() == OnDiffComplayEvents.ACCESS_ROUTER_STYLE_TI) {
                if (tiManager != null) {
                    tiManager.startSmartConfig();
                }
            } else if (diffComplayEvents.getType() == OnDiffComplayEvents.ACCESS_ROUTER_STYLE_SMARTLINK){
                try {
                    start(ssid_pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            /////////TI绑定////////////
            if (tiManager != null) {
                tiManager.startSmartConfig();
            } else {
                try {
                    start(ssid_pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /////////TI绑定////////////
        }

        searchDevice();
    }

    private void searchDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPersent = 0;
                for (int i = 0; i < scanTimeOut; i++) {
                    if (!isConnecting) {
                        break;
                    }
                    if (onScanListener != null) {
                        int per = (int) ((++mPersent / scanTimeOut) * 100);
                        onScanListener.onScanProgress(per);
                        if (per >= 100) {
                            stopScan();
                        }
                    }
                    try {
                        Thread.sleep(/*10 * */1000);
                    } catch (InterruptedException e) {
                    }
                }
                mPersent = 0;
            }
        }).start();
    }



    protected void send4020() throws Exception {
        PacketModel p = new PacketModel();
        UdpDeviceDataBean udpDeviceDataBean = new UdpDeviceDataBean();
        udpDeviceDataBean.setCommandType(Contants.HET_SMARTLINK_SEND_EXIT_ROUTER);
        p.setDeviceInfo(udpDeviceDataBean);
        PacketUtils.out(p);
        UdpDataManager.getInstance().send(p);
    }
}
