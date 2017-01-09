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
				if (client!=null) {
					byte[] data = StringUtils.readBytes(client.getInputStream());
					String buf2=new String(data);
					String buf=StringUtils.byte2string(data);
					if ("".equals(buf2)||null==buf2)
					{
						rec="";
						continue;
					}
					rec += buf2;
					int length = rec.length();
					//String CHC = rec.substring(length - 4, length);
					String terminal_id=rec.substring(0,3);
					String msg=rec.substring(3,length);
					System.out.println("接收到来自 "+terminal_id+"的消息："+"\t" + msg);
					if (!"".equals(rec)) {
						DataAnalyseService.GPRSDataAnalyse(rec);
						rec="";	
					}
					
//					System.out.println("CHA= " + CHC);
//					if (CHC.equals("1A1B")) {
//						DataAnalyseService.GPRSDataAnalyse(rec); 
//						break;
//					}
					
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
