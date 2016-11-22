package com.cyt.Bean;
import com.cyt.Bean.ServerThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class ServerSocketBean extends Thread
{
	private  ServerSocket seversocket=null;
	//private final static int port=8080;
	private  OutputStream os=null;
	private  InputStream is=null;
	private  Socket socket=null;
	private int port;
	private ArrayList<ServerThread> sThreads=null;
	public ServerSocketBean(){}
	public ServerSocketBean(int port)
	{
		this.port=port;
		sThreads=new ArrayList<ServerThread>();
		try  
	    {  
			seversocket = new ServerSocket(port);  
	    }  
	     catch (IOException e)  
	    {  
	        e.printStackTrace();  
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
                 socket = seversocket.accept();  
                 ServerThread th = new ServerThread(socket);  
                 sThreads.add(th);
                 th.startRead();
                 th.write("hello");
                 th.write("123");
                 System.out.println("test over");
                 sleep(1000);  
             }  
             catch (Exception e)  
             {  
                 e.printStackTrace();  
             }  
               
         }  
	}
}
