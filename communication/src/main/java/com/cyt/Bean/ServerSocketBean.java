package com.cyt.Bean;
import com.cyt.Threads.ServerThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
public class ServerSocketBean extends Thread
{
	private  ServerSocket seversocket=null;
	private  Socket client=null;
	//private final static int port=8080;
	private  OutputStream os=null;
	private  InputStream is=null;
	private int i=0;
	private int port;
	private HashMap<Integer,Socket> clientMap=null;
	public ServerSocketBean(){}
	public ServerSocketBean(int port)
	{
		this.port=port;
		clientMap=new HashMap<Integer,Socket>();
		try  
	    {  
			seversocket = new ServerSocket(port);  
	    }  
	     catch (IOException e)  
	    {  
	        e.printStackTrace();  
	    }
	}
	public boolean CheckCNCT()
	{
		if (!clientMap.isEmpty()) {
			System.out.println("check start");
			System.out.println(clientMap.size());
			for (int i=1;i<clientMap.size();i++) { 
				if (clientMap.get(i).isConnected()) {
					System.out.println("check true");
					return true;
				}
			}
		}
		System.out.println("CHECK false");
		return false;
	}
	public void write(String Message,int num)
	{
		if (clientMap.get(num).isConnected()){
			try {
				BufferedOutputStream out=new BufferedOutputStream(clientMap.get(num).getOutputStream());
				byte[] send=Message.getBytes();
				out.write(send);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public void run()
	{
		 while (true)  
         {  
             System.out.println("Listenning...");  
             try  
             { 
//               每个请求交给一个线程去处理  
            	 i++;
                 Socket client = seversocket.accept();  
                 ServerThread th = new ServerThread(client);  
                 clientMap.put(i, client);
                 th.startRead();
                 System.out.println("start thread");
                 sleep(1000);  
             }  
             catch (Exception e)  
             {  
                 e.printStackTrace();  
             }  
               
         }  
	}
}
