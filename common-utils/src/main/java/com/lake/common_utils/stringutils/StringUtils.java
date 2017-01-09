package com.lake.common_utils.stringutils;
import java.io.*;;
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
   //string 转hexstring
    public static String str2hexstr(String str)
    { 
    	return byte2string(str.getBytes());
    }
  //byte转string
  	 public  static String byte2string(byte[] data){
  		    if(data==null||data.length<=1) return "";
  		    if(data.length>8000000) return "";
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
  		    return ""+sb.toString().toUpperCase();
  		  } 
  	 //hexstring转byte
  	 public static byte[] hexStringToByte(String hex) {   
  		    int len = (hex.length() / 2);   
  		    byte[] result = new byte[len];   
  		    char[] achar = hex.toCharArray();   
  		    for (int i = 0; i < len; i++) {   
  		     int pos = i * 2;   
  		     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));   
  		    }   
  		    return result;    
  		}  
  		  
  		private static byte toByte(char c) {   
  		    byte b = (byte) "0123456789ABCDEF".indexOf(c);   
  		    return b;   
  		}  
  		//读取byte数组
  		public static byte[] readBytes(InputStream in) throws IOException {  
  	        BufferedInputStream bufin = new BufferedInputStream(in);  
  	        int buffSize = 1024; 
  	        ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);  
  	        byte[] temp = new byte[buffSize];  
  	        int size = bufin.read(temp);  
  	        //System.out.println(size);
  	        while (size!=-1) {  
  	            out.write(temp, 0, size);
  	            //size=bufin.read(temp);
  	            break;
  	        }  
  	        //System.out.println("读取完毕！");
  	        byte[] content = out.toByteArray();  
  	        return content;  
  	    }  
}
