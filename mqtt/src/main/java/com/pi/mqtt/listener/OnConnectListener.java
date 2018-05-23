package com.pi.mqtt.listener;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface OnConnectListener {
    void onConnectSucess(boolean reconnect, String serverURI);
    void onMessageArrived(String topic, MqttMessage mqttMessage);
}
