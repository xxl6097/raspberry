package com.java.pi.api;

import com.java.pi.bean.MIState;
import com.java.pi.bean.RaspPiBean;
import com.java.pi.httpserver.util.Util;
import com.java.pi.util.Logc;
import com.java.pi.util.RaspberryConst;

public class MiSocketPlus {
    private static Thread miSocketStateThread = null;
    public static MIState getSocketPlusState(){
        RaspPiBean piBean = RaspBerryApi.getDeviceState(RaspberryConst.ENTITY.MI_SOCKET);
        if (piBean!=null){
            String state = piBean.getState();
            if (!Util.isEmpty(state)){
                if (state.equals(MIState.UNAVAILABLE.value())){
                    //不可用
                    return MIState.UNAVAILABLE;
                }else if (state.equals(MIState.ON.value())){
                    //开启状态
                    return MIState.ON;
                }else{
                    //关闭状态
                    return MIState.OFF;
                }
            }else{
                //可能HomeAssistant还未启动
                return MIState.HBUNAVAILABLE;
            }
        }else{
            //可能HomeAssistant还未启动
            return MIState.HBUNAVAILABLE;
        }
    }

    public static MIState miSocketPlusUsbState(){
        RaspPiBean piBean = RaspBerryApi.getDeviceState(RaspberryConst.ENTITY.MI_SOCKET_USB);
        if (piBean!=null){
            String state = piBean.getState();
            if (Util.isEmpty(state)){
                if (state.equals(MIState.UNAVAILABLE.value())){
                    //不可用
                    return MIState.UNAVAILABLE;
                }else if (state.equals(MIState.ON.value())){
                    //开启状态
                    return MIState.ON;
                }else{
                    //关闭状态
                    return MIState.OFF;
                }
            }else{
                //可能HomeAssistant还未启动
                return MIState.HBUNAVAILABLE;
            }
        }else{
            //可能HomeAssistant还未启动
            return MIState.HBUNAVAILABLE;
        }
    }



    public static void keepMiSocketPlusOn(final long time){
        if (miSocketStateThread != null){
            miSocketStateThread.interrupt();
            miSocketStateThread = null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = "";
                    while (true){
                        MIState state = getSocketPlusState();
                        Logc.i("keepMiSocketPlusOn:"+state.value());
                        if (state == MIState.OFF){
                            result = RaspBerryApi.MiSocketTurnOn();
                            Logc.i("MiSocketTurnOn:"+result);
                            if (!Util.isEmpty(result)){
                                if (result.equals("[]")){
                                    result = RaspBerryApi.HomeAssistantRestart();
                                    Logc.i("HomeAssistantRestart:"+result);
                                }
                            }else{
                            }
                        }else if (state == MIState.ON){
                        }else{
                            result = RaspBerryApi.HomeAssistantRestart();
                            Logc.i("HomeAssistantRestart:"+result);
                        }
                        Thread.sleep(time * 1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
