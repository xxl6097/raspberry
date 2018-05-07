package com.java.pi.bean;

public enum MIState {
    ON("on"),OFF("off"),UNAVAILABLE("unavailable"),HBUNAVAILABLE("hbunavailable");

    MIState(String string) {
        this.value = string;
    }

    MIState() {
    }

    private String value;

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
