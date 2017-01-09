package com.cyt.Threads;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
public class SendThread extends Thread{

		BufferedOutputStream out;
	    BufferedReader userin;
	    String Message="";
	    public SendThread(BufferedOutputStream out,String Message)
	    { 
	    	this.out = out;
	    	this.Message=Message;
	    }
	    private void send()  
	    {
	    	try {
	    		byte[] send=Message.getBytes();
				out.write(send);
				out.flush();
				System.out.println(" send ok");
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	    @Override
	    public void run() 
	    {
	    	//SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         System.out.println("start SEND THREAD");
	         send();
	         System.out.println("over!");
	    }
	    
	    
	    
	    
	    
	    
	}

