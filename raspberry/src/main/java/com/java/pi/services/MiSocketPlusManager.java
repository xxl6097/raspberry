package com.java.pi.services;

import com.java.pi.api.MiSocketPlus;
import com.java.pi.api.RaspBerryApi;
import com.java.pi.bean.MIState;
import com.java.pi.http.util.Util;
import com.java.pi.util.Logc;

public class MiSocketPlusManager {
    private static MiSocketPlusManager instance = null;
    //唤醒线程
    private Thread wake = null;
    private Thread miSocketStateThread = null;
    //线程标识
    private boolean running = true;
    //暂停标识
    private boolean pause = false;
    //线程锁
    private byte[] lock = new byte[0];
    public static MiSocketPlusManager getInstance() {
        if (instance == null){
            synchronized (MiSocketPlusManager.class){
                if (instance == null){
                    instance = new MiSocketPlusManager();
                }
            }
        }
        return instance;
    }

    public void keepMiSocketPlusHangon(final long time){
        if (miSocketStateThread != null){
            miSocketStateThread.interrupt();
            miSocketStateThread = null;
        }
        miSocketStateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        String result = "";
                        while (running) {
                            if (pause) {
                                lock.wait();
                            }
                            MIState state = MiSocketPlus.getSocketPlusState();
                            Logc.i("keepMiSocketPlusOn:" + state.value());
                            if (state == MIState.OFF) {
                                result = RaspBerryApi.MiSocketTurnOn();
                                Logc.i("MiSocketTurnOn:" + result);
                                if (!Util.isEmpty(result)) {
                                    if (result.equals("[]")) {
                                        result = RaspBerryApi.HomeAssistantRestart();
                                        Logc.i("HomeAssistantRestart:" + result);
                                    }
                                } else {
                                }
                            } else if (state == MIState.ON) {
                            } else {
                                result = RaspBerryApi.HomeAssistantRestart();
                                Logc.i("HomeAssistantRestart:" + result);
                            }
                            lock.wait(time * 1000);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        miSocketStateThread.start();
    }

    public void switchMode(boolean ispause) {
        this.pause = ispause;
        if (wake != null) {
            wake.interrupt();
            return;
        }
        wake = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (true) {
                        lock.notifyAll();
                        Logc.d("#########switchMode# " );
                        if (!running){
                            break;
                        }

                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "switchMode-" );
        wake.start();
    }
}
