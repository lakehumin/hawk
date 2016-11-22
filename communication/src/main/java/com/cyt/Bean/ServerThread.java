package com.cyt.Bean;

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
	private InputStream is=null;
	private OutputStream os=null;
	private BufferedReader in=null;
	private BufferedReader userin=null;
	private BufferedOutputStream out=null;
	//private PrintWriter out=null;
	private byte[] Buffer=null;
	private String rec="";
	public ServerThread(Socket client)
	{
		this.client=client;
		try 
		{
			 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             out=new BufferedOutputStream(client.getOutputStream());
             userin = new BufferedReader(new InputStreamReader(System.in));
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
			 ReceiveThread reThread=new ReceiveThread(client,in,out,userin);
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
		    userin.close();
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

