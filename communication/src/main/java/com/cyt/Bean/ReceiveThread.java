package com.cyt.Bean;

import java.io.*;
import java.net.*;

import com.lake.common_utils.stringutils.StringUtils;

public class ReceiveThread extends Thread {
	BufferedReader in;
	ServerSocket server;
	BufferedOutputStream out;
	BufferedReader userin;
	Socket client;
	String rec;

	public ReceiveThread(ServerSocket server, BufferedReader in,
			BufferedOutputStream out, BufferedReader userin, Socket client) {
		this.in = in;
		this.server = server;
		this.client = client;
		this.out = out;
		this.userin = userin;
		rec = "";
	}

	public ReceiveThread(Socket client, BufferedReader in,
			BufferedOutputStream out, BufferedReader userin) {
		this.in = in;
		this.client = client;
		this.out = out;
		this.userin = userin;
		rec = "";
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (client.isConnected()) {
					byte[] data = StringUtils.readBytes(client.getInputStream());
					rec += StringUtils.byte2string(data);
					int length = rec.length();
					String CHA = rec.substring(length - 4, length);
					System.out.println("rec= " + rec);
					System.out.println("CHA= " + CHA);
					if (CHA.equals("1A1B")) {
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
				userin.close();
				if (client != null) {
					client.close();
				}
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
