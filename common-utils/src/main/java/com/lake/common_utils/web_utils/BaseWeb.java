package com.lake.common_utils.web_utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

/**
 * @author LakeHm
 *
 * 2016年11月15日下午2:53:42
 */
public class BaseWeb {

	public void print(HttpServletResponse response, Object o) throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");		//解决跨域问题
		PrintWriter pw = response.getWriter();
		pw.write(JSON.toJSONString(o));
	}
}
