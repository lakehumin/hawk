package com.cyt.Service;

import java.util.ArrayList;

public class DataAnalyseService {
	//private static byte[] buffer=null;
	public static byte[] List2Array(ArrayList<Byte> array)
	{
		byte[] Buffer=new byte[array.size()];
		for(int i=0;i<array.size();i++)
		{
			Buffer[i]=array.get(i);
		}
		return Buffer;
	}
	
}
