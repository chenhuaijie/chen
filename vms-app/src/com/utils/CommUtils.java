package com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;
import android.text.TextUtils;

public class CommUtils {
	public static SimpleDateFormat[] sFormats = {
		new SimpleDateFormat("yyyyMMdd"),
		new SimpleDateFormat("yyyy-MM-dd HH:mm")
	} ;
	
	
	public static String formatDate(Date date,int index ){
		if(date==null)
			return null ;
		return sFormats[index].format(date) ;
	}
	public static Date parseDate(String strDate,int index){
		if(TextUtils.isEmpty(strDate))
			return null;
		try {
			return sFormats[index].parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null ;
		}
	}
}

