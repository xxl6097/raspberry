package com.tech.tcp.core.tcp;


import com.tech.tcp.core.callback.OnDataRecv;
import com.tech.tcp.util.Logc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpClient {
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private InetSocketAddress serverAddr;
    private OnDataRecv onDataRecv;     //此事件用于当接收到数据时向主线程通知接收到的数据
    private int timeout = 3000;
    private Thread reader;
    private Thread conntter;
    private byte[] lock = new byte[0];
    private boolean readrunning = true;
    private boolean connrunning = true;
    private boolean isServerConn = false;
    private String ip;
    private int port;
    private long lastCheckTime;

    public TcpClient() {
        lastCheckTime = System.currentTimeMillis();
    }

    public void connect(String ip, int port, OnDataRecv onDataRecv) {
        this.onDataRecv = onDataRecv;
        this.ip = ip;
        this.port = port;
        connect();
        if (conntter != null) {
            connrunning = false;
            conntter.interrupt();
            conntter = null;
        }
        if (reader != null) {
            readrunning = false;
            reader.interrupt();
            reader = null;
        }
        conntter = new Thread(connRunnable);
        conntter.start();
        reader = new Thread(readRunnable);
        reader.start();
    }


    private void connect() {
        try {
            serverAddr = new InetSocketAddress(ip, port);
            socket = new Socket();
            socket.connect(serverAddr, timeout);
            isServerConn = socket.isConnected();
            if (outputStream == null) {
                outputStream = new DataOutputStream(socket.getOutputStream());
            }
            if (inputStream == null) {
                inputStream = new DataInputStream(socket.getInputStream());
            }
            Logc.i("uu== 正在进行TCP连接" + ip + ":" + port);
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Logc.e("uu== tcp连接失败 " + errMsg);
        }
    }

    public boolean send(byte[] buffer) {     //发送字节流指令
        if (buffer != null && outputStream != null) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                Logc.e("uu= 发送失败" + e.getMessage());
                socketDisconnected();
            }
        }
        return false;
    }

    public void stop() {
        readrunning = false;
        connrunning = false;
        if (reader != null) {
            reader.interrupt();
            reader = null;
        }
        if (conntter != null) {
            conntter.interrupt();
            conntter = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                outputStream = null;
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream = null;
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        Logc.i("stop");
    }


    private boolean isConnected() {
        boolean tcpConn = socket.isConnected();
        return tcpConn && isServerConn;
    }

    private void socketDisconnected() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket = new Socket();
        outputStream = null;
        inputStream = null;
        isServerConn = false;
        if (conntter != null) {
            conntter.interrupt();
        }
        Logc.e("uu== socket is disconnected");
    }

    private Runnable readRunnable = new Runnable() {
        @Override
        public void run() {
            readrunning = true;
            while (readrunning) {
                synchronized (lock) {
                    if (!isConnected()) {
                        socketDisconnected();
                        try {
                            Logc.e("uu== reader thread is sleeped");
                            lock.wait();
                        } catch (InterruptedException e) {
                            Logc.i("uu== 唤醒reader线程");
                        }
                    }
                }
                if (!readrunning)
                    break;
                try {
                    if (inputStream != null && inputStream.available() > 0) {
                        byte[] recvData = new byte[inputStream.available()];
                        inputStream.read(recvData);
//                        Logc.e(" uu== recv:" + Utils.toHexStrings(recvData));
                        if (onDataRecv != null) {
                            onDataRecv.messageReceived(recvData);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (System.currentTimeMillis() - lastCheckTime > 5000) {
                    try {
                        lastCheckTime = System.currentTimeMillis();
                        socket.sendUrgentData(0xFF);
                    } catch (Exception e) {
                        socketDisconnected();
                    }
                }

            }
        }
    };

    private Runnable connRunnable = new Runnable() {
        @Override
        public void run() {
            connrunning = true;
            synchronized (lock) {
                while (connrunning) {
                    connect();
                    if (isConnected()) {
                        if (onDataRecv != null) {
                            onDataRecv.onConnected();
                            Logc.e("uu== tcp连接成功 " + ip + ":" + port);
                        }
                        reader.interrupt();
                        try {
                            Logc.e("uu== connetter thread is sleeped");
                            lock.wait();
                        } catch (InterruptedException e) {
                            Logc.i("uu== 唤醒[连接]线程");
                        }
                    }
                    if (!connrunning)
                        break;
                }
            }
        }
    };
}
