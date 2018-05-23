package com.pi.mqtt.ob;

/**
 * Created by uuxia-mac on 2017/12/30.
 */

public interface INotify<T> {
    void onNotify(T eventData);
}
