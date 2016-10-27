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
}
