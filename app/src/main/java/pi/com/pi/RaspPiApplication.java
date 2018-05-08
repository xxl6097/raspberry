package pi.com.pi;

import android.app.Application;

import com.fsix.mqtt.MqttConnManager;
import com.het.websocket.WsBootstrap;

public class RaspPiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //WSConst.MQTT.HOST = "tcp://192.168.1.100:1883";
        WsBootstrap.init(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        MqttConnManager.getInstances().stop();
        WsBootstrap.destroy(this);
    }
}
