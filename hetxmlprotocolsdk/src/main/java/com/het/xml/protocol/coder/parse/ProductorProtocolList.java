package com.het.xml.protocol.coder.parse;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 功能：厂商协议列表
 *
 * @author jake
 */
public class ProductorProtocolList {
    //key:协议ID
    private final HashMap<String, Object> mapper = new HashMap<String, Object>(500);
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public <T> T get(String key) {
        ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            return (T) mapper.get(key.toUpperCase());
        } finally {
            readLock.unlock();
        }
    }

    public void put(String key, Object definition) {
        WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            mapper.put(key.toUpperCase(), definition);
        } finally {
            writeLock.unlock();
        }
    }

    public <T> T remove(String key) {
        WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return (T) mapper.remove(key.toUpperCase());
        } finally {
            writeLock.unlock();
        }
    }
}
