package com.java.pi.bean;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 2018/5/6.
 */

public class RaspPiBean implements Serializable {

    /**
     * attributes : {"friendly_name":"小米插座增强版","icon":"mdi:power-socket","load_power":700,"model":"chuangmi.plug.v3","temperature":41,"wifi_led":true}
     * entity_id : switch.mi_socket_plus
     * last_changed : 2018-05-05T16:06:52.488426+00:00
     * last_updated : 2018-05-05T16:06:52.488426+00:00
     * state : off
     */

    private AttributesBean attributes;
    private String entity_id;
    private String last_changed;
    private String last_updated;
    private String state;

    public AttributesBean getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesBean attributes) {
        this.attributes = attributes;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getLast_changed() {
        return last_changed;
    }

    public void setLast_changed(String last_changed) {
        this.last_changed = last_changed;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static class AttributesBean {
        /**
         * friendly_name : 小米插座增强版
         * icon : mdi:power-socket
         * load_power : 700
         * model : chuangmi.plug.v3
         * temperature : 41
         * wifi_led : true
         */

        private String friendly_name;
        private String icon;
        private int load_power;
        private String model;
        private int temperature;
        private boolean wifi_led;

        public String getFriendly_name() {
            return friendly_name;
        }

        public void setFriendly_name(String friendly_name) {
            this.friendly_name = friendly_name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getLoad_power() {
            return load_power;
        }

        public void setLoad_power(int load_power) {
            this.load_power = load_power;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public boolean isWifi_led() {
            return wifi_led;
        }

        public void setWifi_led(boolean wifi_led) {
            this.wifi_led = wifi_led;
        }
    }
}
