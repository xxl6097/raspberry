package com.het.protocol.bean;

public abstract class Packet {
    abstract byte[] head();
    abstract byte[] body();
    abstract byte[] data();
    abstract DevProtoDetailBean getDev();
}
