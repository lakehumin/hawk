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
					String CHC=rec.substring(length-4,length);
					//System.out.println("CHC="+CHC);
					//String CHC = rec.substring(length - 4, length);
					if (CHC.equals("FFD9")) {
						DataAnalyseService.GPRSDataAnalyse(rec);
						//System.out.println("Ωÿ»°≥…π¶");
						//System.out.println("rec="+rec);
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
