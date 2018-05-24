package com.ws.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/log")
public class LogWebSocketHandle {
	
	private Process process;
	private InputStream inputStream;
	public static Map<String, Session> clients = new ConcurrentHashMap<String, Session>();

	/**
	 * 新的WebSocket请求开启
	 */
	@OnOpen
	public void onOpen(Session session) {
		try {
			// 执行tail -f命令
			process = Runtime.getRuntime().exec("tail -f /var/log/syslog");
			inputStream = process.getInputStream();
			
			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(inputStream, session);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		System.out.println("进入："+myWebsocket);
//		clients.put(myWebsocket, session);
	}
	
	/**
	 * WebSocket请求关闭
	 */
	@OnClose
	public void onClose() {
		try {
			if(inputStream != null)
				inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(process != null)
			process.destroy();
	}
	
	@OnError
	public void onError(Throwable thr) {
		thr.printStackTrace();
	}


	/**
	 * 将数据传回客户端
	 * 异步的方式
	 * @param myWebsocket
	 * @param message
	 */
	public static void broadcast(String myWebsocket, String message) {
		if (clients.containsKey(myWebsocket)) {
			clients.get(myWebsocket).getAsyncRemote().sendText(message);
		} else {
			throw new NullPointerException("[" + myWebsocket +"]Connection does not exist");
		}
	}
}