package com.tech.tcp.core.base;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseQueue<T> extends Thread {
    protected boolean running = true;
    protected BlockingQueue<T> queue = new LinkedBlockingQueue<T>();

    @Override
    public void run() {
        super.run();
        execute();
    }

    protected abstract void execute();

    public abstract boolean send(T data);

    public abstract void close();

}
