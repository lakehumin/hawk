package com.cyt.Threads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.cyt.Service.DataAnalyseService;
import com.lake.common_utils.stringutils.StringUtils;

public class ServerThread extends Thread
{
	private Socket client=null;
	private BufferedReader in=null;
	private BufferedOutputStream out=null;
	private String th_LocalADDress;
	private String th_localsocketaddress;
	private String th_inetaddress;
	public ServerThread(Socket client)
	{
		this.client=client;
		th_LocalADDress=client.getLocalAddress().toString();
		th_localsocketaddress=client.getLocalSocketAddress().toString();
		th_inetaddress=client.getInetAddress().toString();
		System.out.println("th_LocalADDress= "+th_LocalADDress);
		System.out.println("th_localsocketaddress= "+th_localsocketaddress);
		System.out.println("th_inetaddress= "+th_inetaddress);
		try 
		{
			 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             out=new BufferedOutputStream(client.getOutputStream());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public void startRead()
	{
		if(client.isConnected())
		{
			 ReceiveThread reThread=new ReceiveThread(client,in,out);
			 reThread.start();
		}
	}
	public void write(String Message)
	{
		if(client.isConnected())
		{
			new SendThread(out, Message).start();
		}
	}
	public void close()
	{
		try {
			in.close();
			out.close();
		    if(client != null){
		          client.close();
		        }
		     client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() 
	{
		
	}


}

