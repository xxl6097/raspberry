package com.het.udp.core.smartlink.ti;

import com.het.log.Logc;
import com.het.udp.core.smartlink.ti.callback.SmartConfigListener;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by UUXIA on 2015/6/25.
 */
public class SmartConfig {
    private static final int defaultNumberOfSetups = 4;
    private static final int defaultNumberOfSyncs = 10;
    private static final String defaultSyncLString = "abc";
    private static final String defaultSyncHString = "abcdefghijklmnopqrstuvw";
    private static final String defaultmDnsAckString = "CC3000";
    private static final int mDnsListenPort = 5353;
    private static final int LOCAL_PORT = 15000;
    private static final int FIVE_MINUTE_TIMEOUT_MS = 300000;
    InetSocketAddress sockAddr;
    int localPort;
    int listenPort;
    int waitForAckSocketTimeout;
    Thread sendingThread;
    Thread ackWaitThread;
    private boolean stopSending;
    private String ip;
    private String ssid;
    private byte group;
    private byte[] freeData;
    private String key;
    private String token;
    private String mDnsAckString;
    private int nSetup;
    private String syncLString;
    private String syncHString;
    private byte[] encryptionKey;
    private SmartConfigEncode ftcData;
    private MulticastSocket listenSocket;
    private SmartConfigListener m_listener;
    private boolean isListenSocketGracefullyClosed;

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token) throws Exception {
        this(listener, FreeData, Key, EncryptionKey, Ip, Ssid, Group, Token, "CC3000");
    }

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token, String ackString) throws Exception {
        this(listener, FreeData, Key, EncryptionKey, Ip, Ssid, Group, Token, ackString, 5353);
    }

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token, String ackString, int notifyListenPort) throws Exception {
        this(listener, FreeData, Key, EncryptionKey, Ip, Ssid, Group, Token, ackString, notifyListenPort, 15000);
    }

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token, String ackString, int notifyListenPort, int localPort) throws Exception {
        this(listener, FreeData, Key, EncryptionKey, Ip, Ssid, Group, Token, ackString, notifyListenPort, localPort, 300000);
    }

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token, String ackString, int notifyListenPort, int localPort, int WaitForAckSocketTimeout) throws Exception {
        this(listener, FreeData, Key, EncryptionKey, Ip, Ssid, Group, Token, ackString, notifyListenPort, localPort, WaitForAckSocketTimeout, 4, 10, "abc", "abcdefghijklmnopqrstuvw");
    }

    public SmartConfig(SmartConfigListener listener, byte[] FreeData, String Key, byte[] EncryptionKey, String Ip, String Ssid, byte Group, String Token, String ackString, int notifyListenPort, int LocalPort, int WaitForAckSocketTimeout, int numberOfSetups, int numberOfSyncs, String syncL, String syncH) throws Exception {
        this.m_listener = null;
        boolean AES_LENGTH = true;
        boolean hasEncryption = false;
        this.m_listener = listener;
        this.freeData = FreeData;
        if (EncryptionKey != null && EncryptionKey.length != 0 && EncryptionKey.length != 16) {
            throw new Exception("Encryption key must have 16 characters!");
        } else if (Key.length() > 32) {
            throw new Exception("Password is too long! Maximum length is 32 characters.");
        } else if (Ssid.length() > 32) {
            throw new Exception("Network name (SSID) is too long! Maximum length is 32 characters.");
        } else if (Token.length() > 32) {
            throw new Exception("Token is too long! Maximum length is 32 characters.");
        } else {
            this.group = Group;
            this.stopSending = true;
            this.isListenSocketGracefullyClosed = false;
            this.listenSocket = null;
            this.freeData = FreeData;
            this.ip = Ip;
            this.ssid = Ssid;
            this.key = Key;
            this.token = Token;
            this.nSetup = numberOfSetups;
            this.syncLString = syncL;
            this.syncHString = syncH;
            this.encryptionKey = EncryptionKey;
            this.mDnsAckString = ackString;
            this.createBroadcastUDPSocket(notifyListenPort);
            this.localPort = LocalPort;
            this.listenPort = 5353;
            this.waitForAckSocketTimeout = WaitForAckSocketTimeout;
            this.sockAddr = new InetSocketAddress(this.ip, this.localPort);
            byte[] keyData = new byte[this.key.length()];
            keyData = this.key.getBytes("UTF-8");
            if (this.encryptionKey != null) {
                keyData = this.encryptData(keyData);
                hasEncryption = true;
            }

            this.ftcData = new SmartConfigEncode(this.ssid, keyData, this.freeData, this.token, hasEncryption);
        }
    }

    private void createBroadcastUDPSocket(int port) throws Exception {
        Object wildcardAddr = null;
        InetSocketAddress localAddr = null;
        localAddr = new InetSocketAddress((InetAddress) wildcardAddr, port);
        this.listenSocket = new MulticastSocket((SocketAddress) null);
        this.listenSocket.setReuseAddress(true);
        this.listenSocket.bind(localAddr);
        this.listenSocket.setTimeToLive(255);
        this.listenSocket.joinGroup(InetAddress.getByName("224.0.0.251"));
        this.listenSocket.setBroadcast(true);
    }

    private void send() throws Exception {
        int numberOfPackets = this.ftcData.getmData().size();
        new ArrayList();
        byte[] ftcBuffer = new byte[1600];
        byte[] syncLBuffer = this.syncLString.getBytes();
        byte[] syncHBuffer = this.syncHString.getBytes();
        ftcBuffer = this.makePaddedByteArray(ftcBuffer.length);
        ArrayList packets = this.ftcData.getmData();

        while (!this.stopSending) {
            for (int i = 0; i < this.nSetup; ++i) {
                for (int j = 0; j < numberOfPackets; ++j) {
                    int packsize = ((Integer) packets.get(j)).intValue();
                    if (i % 2 == 0) {
                        this.sendData(new DatagramPacket(syncLBuffer, syncLBuffer.length, this.sockAddr), this.localPort);
                    } else {
                        this.sendData(new DatagramPacket(syncHBuffer, syncHBuffer.length - this.group, this.sockAddr), this.localPort);
                    }

                    this.sendData(new DatagramPacket(ftcBuffer, packsize, this.sockAddr), this.localPort);
                }
            }

            Thread.sleep(100L);
        }

    }

    private void sendData(DatagramPacket packet, int localSendingPort) throws Exception {
        DatagramSocket sock = null;
        sock = new DatagramSocket(localSendingPort);
        sock.send(packet);
        sock.close();
//        System.out.println(localSendingPort+" send Data:"+packet.getSocketAddress().toString() + ":" + Arrays.toString(packet.getData()));
    }

    public void transmitSettings() throws Exception {
        this.stopSending = false;
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI,"Ti 绑定-transmitSettings" );
        this.sendingThread = new Thread(new Runnable() {
            public void run() {
                try {
                    SmartConfig.this.send();
                } catch (Exception var2) {
                    SmartConfig.this.new NotifyThread(SmartConfig.this.m_listener, var2);
                }

            }
        });
        this.sendingThread.start();
        this.ackWaitThread = new Thread(new Runnable() {
            public void run() {
                try {
                    SmartConfig.this.waitForAck();
                } catch (Exception var2) {
                    SmartConfig.this.new NotifyThread(SmartConfig.this.m_listener, var2);
                }

            }
        });
        this.ackWaitThread.start();
    }

    public void stopTransmitting() throws Exception {
        this.isListenSocketGracefullyClosed = true;
        this.listenSocket.close();
        this.stopSending = true;
        if (Thread.currentThread() != this.sendingThread) {
            this.sendingThread.join();
        }

        if (Thread.currentThread() != this.ackWaitThread) {
            this.ackWaitThread.join();
        }

    }

    private void waitForAck() throws Exception {
        boolean RECV_BUFFER_LENGTH = true;
        byte[] recvBuffer = new byte[16384];
        DatagramPacket listenPacket = new DatagramPacket(recvBuffer, 16384);
        int timeout = this.waitForAckSocketTimeout;

        while (!this.stopSending) {
            long start = System.nanoTime();
            this.listenSocket.setSoTimeout(timeout);

            try {
                this.listenSocket.receive(listenPacket);
            } catch (InterruptedIOException var8) {
                if (!this.isListenSocketGracefullyClosed) {
                    new SmartConfig.NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_TIMEOUT);
                }
                break;
            } catch (Exception var9) {
                if (this.isListenSocketGracefullyClosed) {
                    break;
                }

                throw var9;
            }

            if (this.parseMDns(listenPacket.getData())) {
                this.stopTransmitting();
                new SmartConfig.NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_SUCCESS);
                break;
            }

            timeout = (int) ((long) timeout - (System.nanoTime() - start) / 1000000L);
            if (timeout <= 0) {
                new SmartConfig.NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_TIMEOUT);
                break;
            }
        }

    }

    private boolean parseMDns(byte[] data) throws Exception {
        boolean MDNS_HEADER_SIZE = true;
        boolean MDNS_HEADER_SIZE2 = true;
        byte pos = 12;
        if (data.length < pos + 1) {
            return false;
        } else {
            int len = data[pos] & 255;

            int var8;
            for (var8 = pos + 1; len > 0; ++var8) {
                if (data.length < var8 + len) {
                    return false;
                }

                var8 += len;
                if (data.length < var8 + 1) {
                    return false;
                }

                len = data[var8] & 255;
            }

            var8 += 10;
            if (data.length < var8 + 1) {
                return false;
            } else {
                len = data[var8] & 255;
                ++var8;
                if (data.length < var8 + len) {
                    return false;
                } else {
                    String name = new String(data, var8, len);
                    boolean bRet = name.equals(this.mDnsAckString);
                    return bRet;
                }
            }
        }
    }

    private byte[] encryptData(byte[] data) throws Exception {
        byte[] InitializationVector1 = new byte[]{(byte) 1, (byte) 3, (byte) 25, (byte) -46, (byte) -79, (byte) 81, (byte) -14, (byte) 9, (byte) 112, (byte) 97, (byte) -61, (byte) -53, (byte) 48, (byte) 125, (byte) 0, (byte) 1};
        byte[] InitializationVector2 = new byte[]{(byte) 1, (byte) 3, (byte) 25, (byte) -46, (byte) -79, (byte) 81, (byte) -14, (byte) 9, (byte) 112, (byte) 97, (byte) -61, (byte) -53, (byte) 48, (byte) 125, (byte) 0, (byte) 2};
        if (this.encryptionKey != null && this.encryptionKey.length != 0) {
            boolean ZERO_OFFSET = false;
            boolean AES_LENGTH = true;
            boolean DATA_LENGTH = true;
            Cipher c = null;
            Object encryptedData = null;
            Object encryptedData1 = null;
            Object encryptedData2 = null;
            byte[] paddedData = new byte[32];
            byte[] paddedData1 = new byte[16];
            byte[] paddedData2 = new byte[16];
            byte[] aesKey = new byte[16];

            int dataOffset;
            for (dataOffset = 0; dataOffset < 16; ++dataOffset) {
                if (dataOffset < this.encryptionKey.length) {
                    aesKey[dataOffset] = this.encryptionKey[dataOffset];
                } else {
                    aesKey[dataOffset] = 0;
                }
            }

            System.arraycopy(this.encryptionKey, 0, aesKey, 0, 16);
            dataOffset = 0;
            if (data.length < 32) {
                paddedData[dataOffset] = (byte) data.length;
                ++dataOffset;
            }

            System.arraycopy(data, 0, paddedData, dataOffset, data.length);

            for (dataOffset += data.length; dataOffset < 32; ++dataOffset) {
                paddedData[dataOffset] = 0;
            }

            for (int k = 0; k < 16; ++k) {
                paddedData1[k] = paddedData[k];
                paddedData2[k] = paddedData[k + 16];
            }

            c = Cipher.getInstance("AES/OFB/NoPadding");
            SecretKeySpec var22 = new SecretKeySpec(aesKey, "AES");
            byte[] var19 = new byte[32];
            IvParameterSpec ivspec1 = new IvParameterSpec(InitializationVector1);
            IvParameterSpec ivspec2 = new IvParameterSpec(InitializationVector2);
            c.init(1, var22, ivspec1);
            byte[] var20 = c.doFinal(paddedData1);
            c.init(1, var22, ivspec2);
            byte[] var21 = c.doFinal(paddedData2);
            System.arraycopy(var20, 0, var19, 0, 16);
            System.arraycopy(var21, 0, var19, 16, 16);
            return var19;
        } else {
            return data;
        }
    }

    private byte[] makePaddedByteArray(int length) throws Exception {
        byte[] paddedArray = new byte[length];

        for (int x = 0; x < length; ++x) {
            paddedArray[x] = (byte) "1".charAt(0);
        }

        return paddedArray;
    }

    private class NotifyThread implements Runnable {
        private SmartConfigListener m_listener;
        private SmartConfigListener.SmtCfgEvent t_event;
        private Exception t_ex;

        public NotifyThread(SmartConfigListener listener, SmartConfigListener.SmtCfgEvent event) {
            this.m_listener = listener;
            this.t_event = event;
            this.t_ex = null;
            Thread t = new Thread(this);
            t.start();
        }

        public NotifyThread(SmartConfigListener listener, Exception ex) {
            this.m_listener = listener;
            this.t_event = SmartConfigListener.SmtCfgEvent.FTC_ERROR;
            this.t_ex = ex;
            Thread t = new Thread(this);
            t.start();
        }

        public void run() {
            try {
                if (this.m_listener != null) {
                    this.m_listener.onSmartConfigEvent(this.t_event, this.t_ex);
                }
            } catch (Exception var2) {
                ;
            }

        }
    }
}
