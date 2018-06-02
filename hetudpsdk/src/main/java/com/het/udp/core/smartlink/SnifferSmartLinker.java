package com.het.udp.core.smartlink;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public abstract class SnifferSmartLinker{
	protected Context mContext;
	private String TAG = "HFdebug";

	private String ssid;
	protected String pswd;
	private String broadCastIP;
	private Set<String> successMacSet = new HashSet<String>();
	private int HEADER_COUNT = 200;
	private int HEADER_PACKAGE_DELAY_TIME = 10;
	private int HEADER_CAPACITY = 76;
	private int CONTENT_COUNT = 5;
	private int CONTENT_PACKAGE_DELAY_TIME = 50;
	private int CONTENT_CHECKSUM_BEFORE_DELAY_TIME = 100;
	private int CONTENT_GROUP_DELAY_TIME = 500;
	private final String RET_KEY = "smart_config";

	private int port = 49999;
	protected boolean isConnecting = false;
	
	
	private InetAddress inetAddressbroadcast;
	private DatagramSocket socket;
	private DatagramPacket packetToSendbroadcast;

	public SnifferSmartLinker() {
		isConnecting = false;
	}

	private String getBroadcastAddress(Context ctx)  {
		WifiManager cm = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo myDhcpInfo = cm.getDhcpInfo(); 
		if (myDhcpInfo == null) { 
			return "255.255.255.255";
		} 
//		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask) 
//		| ~myDhcpInfo.netmask; 
		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask) 
				| ~myDhcpInfo.netmask;
		byte[] quads = new byte[4]; 
		for (int k = 0; k < 4; k++) 
		quads[k] = (byte) ((broadcast >> k * 8) & 0xFF); 
		try{
			return InetAddress.getByAddress(quads).getHostAddress(); 
		}catch(Exception e){
			return "255.255.255.255";
		}
	}

	private void connect() {
		Log.e(TAG, "connect");
		int count = 1;
		byte[] header = this.getBytes(HEADER_CAPACITY);
		while (count <= HEADER_COUNT && isConnecting) {
			send(header);
			try {
				Thread.sleep(HEADER_PACKAGE_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		String pwd = pswd;
		int[] content = new int[pwd.length() + 2];

		content[0] = 89;
		int j = 1;
		for (int i = 0; i < pwd.length(); i++) {
			content[j] = pwd.charAt(i) + 76;
			j++;
		}
		content[content.length - 1] = 86;

		count = 1;
		while (count <= CONTENT_COUNT && isConnecting) {
			for (int i = 0; i < content.length; i++) {
				// JCTIP ver2 start end checksum send 3 time;
				int _count = 1;
				if (i == 0 || i == content.length - 1) {
					_count = 3;
				}
				int t = 1;
				while (t <= _count && isConnecting) {
					send(getBytes(content[i]));
					if (i != content.length) {
						try {
							Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					t++;
				}
				// mConfigBroadUdp.send(getBytes(content[i]));

				if (i != content.length) {
					try {
						Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(CONTENT_CHECKSUM_BEFORE_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// int checkLength = pwd.length() * 30 + 76;
			// JCTIP ver1
			int checkLength = pwd.length() + 256 + 76;

			// JCTIP ver2
			int t = 1;
			while (t <= 3 && isConnecting) {
				send(getBytes(checkLength));
				if (t < 3) {
					try {
						Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t++;
			}
			// mConfigBroadUdp.send(getBytes(checkLength));

			try {
				Thread.sleep(CONTENT_GROUP_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		Log.e(TAG, "connect END");
	}

	private byte[] getBytes(int capacity) {
		byte[] data = new byte[capacity];
		for (int i = 0; i < capacity; i++) {
			data[i] = 5;
		}
		return data;
	}

	private void send(byte[] data) {
		packetToSendbroadcast = new DatagramPacket(data, data.length,
				inetAddressbroadcast, port);
		try {
			socket.send(packetToSendbroadcast);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(String password/*, String... ssid*/) throws Exception {
		Log.e(TAG, ssid + ":===============" + password);
//		if (ssid != null && ssid.length > 0) {
//			this.ssid = ssid[0];
//		}else {
//			this.ssid = null;
//		}
		this.pswd = password;
		this.broadCastIP = getBroadcastAddress(mContext);
		socket = new DatagramSocket(port);
		socket.setBroadcast(true);
		inetAddressbroadcast = InetAddress.getByName(broadCastIP);

		Log.e(TAG, "start");
		isConnecting = true;
		successMacSet.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (isConnecting) {
					connect();
				}
				Log.e(TAG, "StopConnet");
				SnifferSmartLinker.this.stop();
			}
		}).start();
//		if(!isfinding){
//			isfinding = true;
//			new Thread(findThread).start();
//		}
	}

	public void stop() {
		isConnecting = false;
		if(socket != null){
			socket.close();
		}
	}
}
