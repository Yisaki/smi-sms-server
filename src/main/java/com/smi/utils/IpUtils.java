package com.smi.utils;

import javax.servlet.http.HttpServletRequest;


public class IpUtils {
		
	public static String getClientIp(HttpServletRequest req) {
		String ip = req.getHeader("x-forwarded-for");
		if(ip != null) {
			return ip.split(",")[0].trim();
		} else {
			return req.getRemoteAddr();
		}
	}
}
