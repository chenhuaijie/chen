package com.license.number.network;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.license.number.entity.AppPOJO;
import com.utils.SharedPreferenceHelper;



public class LocClient {
	private static  String LOC_SERVER = "http://192.168.1.104:8080/VMSWEB" ;
	
	
	
	
	public static void init(Context context){
		SharedPreferenceHelper helper = new SharedPreferenceHelper(context);
		String ServerAddress  = helper.getServerAddress();
		
		if(!TextUtils.isEmpty(ServerAddress)){
			LOC_SERVER = ServerAddress+"/VMSWEB";
		}
	}
	
	public static final String DATE_FOMAT_MS = "yyyyMMdd";
	
	public static Random random = new Random();
	
	public static AppPOJO reportPosi(String plate_number) throws HttpException, IOException{
		
		
//    	Client client = new Client(LOC_SERVER) ;
//    	client.addPathSegment("app");
//    	client.addPathSegment("findCarByPlateNum.html");
    	Gson gson = new GsonBuilder().setDateFormat(DATE_FOMAT_MS).create();
//    	client.addUrlParam("plate_num", plate_number);
//		String resp = client.get();
//		AppPOJO bResponse = (AppPOJO) gson.fromJson(resp, AppPOJO.class);
		AppPOJO bResponse = (AppPOJO) gson.fromJson(test_json[random.nextInt(3)], AppPOJO.class);
    	return bResponse ;
    }
	 
	public static final String test_json1 = 
"{\"book\":{\"carid\":\"946b6a84-b523-4d73-ad17-620f22bad3a3\",\"endDateString\":\"20150324\",\"enddate\":1427126400000,\"idsbook\":\"65cd4e5a-afe2-4b86-9c59-11591369993b\",\"isexpire\":\"Y\",\"isexpireString\":\"已过期\",\"lease\":\"11111\",\"lessee\":\"11111\",\"startDateString\":\"20150309\",\"startdate\":1425830400000,\"usedept\":\"1111\",\"userid\":\"1d265e9b-a23a-40db-9b32-1931a54a6dc4\"},"+
"\"car\":{\"carid\":\"946b6a84-b523-4d73-ad17-620f22bad3a3\",\"name\":\"3\",\"origin\":\"3\",\"plate_num\":\"323\",\"status\":\"Y\",\"statusName\":\"已租用\",\"type\":\"3\"},\"exists\":true,\"user\":{\"company\":\"3\",\"dept\":\"3\",\"email\":\"3\",\"name\":\"3\",\"nickname\":\"3\",\"password\":\"123456\",\"phonenumber\":\"3\",\"role\":\"user\",\"userid\":\"1d265e9b-a23a-40db-9b32-1931a54a6dc4\"}}";
	
	public static final String test_json2 = 
		"{\"car\":{\"carid\":\"a619bbb1-b3b4-4029-8fee-5f3001e55512\",\"name\":\"4\",\"origin\":\"4\",\"plate_num\":\"4\",\"status\":\"N\",\"statusName\":\"未租用\",\"type\":\"4\"},\"exists\":true}";
	public static final String test_json3 = 
			"{\"exists\":false}" ;
	
	public static final String[] test_json = {test_json1,test_json2,test_json3} ;
}
