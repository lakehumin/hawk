package com.cyt.Threads;

import java.io.*;
import java.net.*;

import com.cyt.Service.DataAnalyseService;
import com.lake.common_utils.stringutils.StringUtils;

public class ReceiveThread extends Thread {
	BufferedReader in;
	ServerSocket server;
	BufferedOutputStream out;
	Socket client;
	String rec;

	public ReceiveThread(ServerSocket server, BufferedReader in,
			BufferedOutputStream out, Socket client) {
		this.in = in;
		this.server = server;
		this.client = client;
		this.out = out;
		rec = "";
	}

	public ReceiveThread(Socket client, BufferedReader in,
			BufferedOutputStream out) {
		this.in = in;
		this.client = client;
		this.out = out;
		rec = "";
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (client.isConnected()) {
					byte[] data = StringUtils.readBytes(client.getInputStream());
					rec += StringUtils.byte2string(data);
					if (rec.startsWith("0x1")) {
						System.out.println("当前客户端已经断开连接。。。");
						break;
					}
					int length = rec.length();
					String CHC = rec.substring(length - 4, length);
					System.out.println("rec= " + rec);
					System.out.println("CHA= " + CHC);
					if (CHC.equals("1A1B")) {
						DataAnalyseService.GPRSDataAnalyse(rec); 
						break;
					}
					
				}
				else {
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				if (client != null) {
					client.close();
				}
				//server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
