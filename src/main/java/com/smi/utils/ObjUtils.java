package com.smi.utils;

public class ObjUtils {
	public static String getNotNullString(Object obj){
		if(obj==null)
			return "";
		return obj.toString();
	}
}
