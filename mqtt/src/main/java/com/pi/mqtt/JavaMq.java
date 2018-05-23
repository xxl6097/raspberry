package com.pi.mqtt;

import com.pi.mqtt.bean.MQBean;
import com.pi.mqtt.bean.MqttConnBean;
import com.pi.mqtt.listener.OnConnectListener;
import com.pi.mqtt.ob.EventManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Timer;
import java.util.TimerTask;

public class JavaMq {
    private MqttConnectOptions conOpt;
    private MqttConnBean connBean;
    private OnConnectListener connectListener;
    private MqttClient client = null;
    public volatile boolean isConnectFlag = false; //是否连接
    private boolean isRelease = false;

    public void subscribe() {
        try {
            if (this.connBean != null) {
                String topic = this.connBean.getTopic();
                int qas = this.connBean.getQos();
                if (topic != null && !topic.equals("")) {
                    this.client.subscribe(topic, qas);
                }
            }
        } catch (MqttException var3) {
        }

    }

    public void subscribe(String topic, int qos) {
        try {
            if (topic != null && !topic.equals("")) {
                this.client.subscribe(topic, qos);
            }
        } catch (MqttException var4) {
        }
    }

    public boolean isConnectFlag() {
        return this.client != null && this.client.isConnected();
    }

    public void publish(byte[] data) {
        this.publish(this.connBean.getTopic(), data);
    }

    public void publish(String topic, byte[] data) {
        if (this.isConnectFlag()) {
            MqttMessage mqttMessage = new MqttMessage();
            try {
                mqttMessage.setPayload(data);
                this.client.publish(topic, mqttMessage);
            } catch (MqttException var5) {
                var5.printStackTrace();
            }
        }
    }

    public void stop() {
        if (client != null) {
            if (client.isConnected()) {
                //注销mqtt相关资源
                try {
                    client.disconnect();
                    client.close();
                    isRelease = true;
                } catch (MqttException e) {
                }

                if (isConnectFlag) {
                    isConnectFlag = false;
                }
            }

        }
    }

    public void start(MqttConnBean bean){
        start(bean,null);
    }

    public void start(MqttConnBean bean, final OnConnectListener listener) {
        connBean = bean;
        // 服务器地址（协议+地址+端口号）
        isConnectFlag = false;
        isRelease = false;
        String uri = connBean.getBrokerUrl();
        if (uri != null && !uri.equals("")) {
            if (client == null) {
                MemoryPersistence persistence = new MemoryPersistence();
                try {
                    client = new MqttClient(uri, connBean.getClientId(), persistence);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                // 设置MQTT监听并且接受消息
                client.setCallback(new MqttCallbackExtended() {
                    public void connectComplete(boolean reconnect, String serverURI) {
                        System.out.println("connect success");
                        //连接成功，需要上传客户端所有的订阅关系
                        if (client != null && client.isConnected() && !isConnectFlag) {
                            isConnectFlag = true;
                        }
                        if (listener != null) {
                            listener.onConnectSucess(reconnect,serverURI);
                        }
                    }

                    public void connectionLost(Throwable throwable) {
                        System.out.println("mqtt connection lost");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                doClientConnection();
                            }
                        }, 5000);
                    }

                    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                        if (listener != null) {
                            listener.onMessageArrived(topic,mqttMessage);
                        }

                        if (topic!=null && mqttMessage != null) {
                            EventManager.getInstance().post(new MQBean(0, topic, mqttMessage));
                        }
                    }

                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                        System.out.println("deliveryComplete:" + iMqttDeliveryToken.getMessageId());
                    }
                });
                conOpt = new MqttConnectOptions();
                // 清除缓存
                conOpt.setCleanSession(true);
                // 设置超时时间，单位：秒
                conOpt.setConnectionTimeout(10);
                // 心跳包发送间隔，单位：秒
                int keepalive = connBean.getKeepAlive().intValue();
                conOpt.setKeepAliveInterval(keepalive);
                // 用户名
                conOpt.setUserName(connBean.getUserName());
                // 密码
                conOpt.setPassword(connBean.getPassword().toCharArray());
                // last will message
                doClientConnection();
            }
        }
    }

    private void doClientConnection() {
        if (client == null)
            return;
        System.out.println("####mqtt doClientConnection " + client.isConnected());
        if (!client.isConnected()/* && NetworkUtil.isConnected(mContext)*/) {
            try {
                client.connect(conOpt);
            } catch (MqttException e) {
                System.out.println("######mqtt failed:" + e.toString());
                if (isConnectFlag) {
                    isConnectFlag = false;
                }
                if (!isRelease) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            doClientConnection();
                        }
                    }, 5000);
                }
            }
        }
    }
}
