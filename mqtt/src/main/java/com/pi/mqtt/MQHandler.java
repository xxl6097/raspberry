package com.pi.mqtt;

import com.pi.mqtt.bean.MqttConnBean;
import com.pi.mqtt.listener.OnConnectListener;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQHandler {
    public static String HOST = "tcp://uuxia.cn:1883";
    public static String USERNAME = "admin";
    public static String PASSWORD = "public";
    public static int QOS = 0;
    public static Integer RETAIN = 1;

    private static JavaMq javaMq;

    public static void main(String[] args) {
    }

    public static void startMQ() {
        javaMq = new JavaMq();
        MqttConnBean mqttConnBean = new MqttConnBean();
        mqttConnBean.setBrokerUrl(HOST);
        mqttConnBean.setUserName(USERNAME);
        mqttConnBean.setPassword(PASSWORD);
        mqttConnBean.setQos(QOS);
        mqttConnBean.setRetain(RETAIN);
        javaMq.start(mqttConnBean, new OnConnectListener() {
            @Override
            public void onConnectSucess(boolean reconnect, String serverURI) {
//                javaMq.subscribe("log/R602_f82ce11e3223", 1);
                javaMq.subscribe("socket", 1);
            }

            @Override
            public void onMessageArrived(String topic, MqttMessage mqttMessage) {
                System.out.println("messageArrived:" + topic + "------" + new String(mqttMessage.getPayload()));
            }
        });
    }

    public static void stop() {
        if (javaMq != null) {
            javaMq.stop();
        }
    }


}
