package com.lake.common_utils.stringutils;

/**
 * @author LakeHm
 *
 * 2016年10月21日下午12:18:55
 */
public class StringUtils {
    
    public static boolean isEmpty(String s) {
    	if(null == s) return true;
    	return s.isEmpty();
    }
    
    public static boolean isNotEmpty(String s) {
    	return !isEmpty(s);
    }
    	//byte数组到16进制字符串
	  public static String byte2string(byte[] data){
	    if(data==null||data.length<=1) return "0x";
	    if(data.length>200000) return "0x";
	    StringBuffer sb = new StringBuffer();
	    int buf[] = new int[data.length];
	    //byte数组转化成十进制
	    for(int k=0;k<data.length;k++){
	      buf[k] = data[k]<0?(data[k]+256):(data[k]);
	    }
	    //十进制转化成十六进制
	    for(int k=0;k<buf.length;k++){
	      if(buf[k]<16) sb.append("0"+Integer.toHexString(buf[k]));
	      else sb.append(Integer.toHexString(buf[k]));
	    }
	    return "0x"+sb.toString().toUpperCase();
	  } 
}
