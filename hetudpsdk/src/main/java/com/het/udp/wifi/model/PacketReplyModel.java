package com.het.udp.wifi.model;

import java.io.Serializable;

/**
 * Created by uuxia-mac on 2015/8/29.
 */
public class PacketReplyModel implements Serializable {
    private int replyCount;
    private PacketModel packet;
    private long delayTime;

    public PacketReplyModel(int replyCount, PacketModel packet) {
        this.replyCount = replyCount;
        this.packet = packet;
        //默认至少延时1s
        this.delayTime = 1;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public PacketModel getPacket() {
        return packet;
    }

    public void setPacket(PacketModel packet) {
        this.packet = packet;
    }

    @Override
    public String toString() {
        return "PacketReplyModel{" +
                "replyCount=" + replyCount +
                ", packet=" + packet +
                ", delayTime=" + delayTime +
                '}';
    }
}
