package com.pi.mqtt.bean;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

public class MQBean<T> implements Serializable {
    private int code;
    private T data;
    private String topic;
    private MqttMessage mqttMessage;
    private String message;

    public MQBean() {
    }


    public MQBean(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public MQBean(int code, String topic, MqttMessage message) {
        this.code = code;
        this.topic = topic;
        this.mqttMessage = message;
        this.message = new String(message.getPayload());
    }

    public MQBean(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MQBean{" +
                "code=" + code +
                ", data=" + data +
                ", topic='" + topic + '\'' +
                ", mqttMessage=" + mqttMessage +
                ", message='" + message + '\'' +
                '}';
    }
}
