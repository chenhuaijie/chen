package com.license.number.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.license.number.entity.AppEJB;

public class AppClient {
	private static String LOC_SERVER = "http://10.0.0.173:8080/VMSWEB";

	// 2015-04-22 00:00:00
	public static final String DATE_FOMAT_MS = "yyyy-MM-dd HH:mm:ss";

	public static Random random = new Random();

	public static AppEJB reportPosi(String plate_number) throws Exception {

		Client client = new Client(LOC_SERVER);
		client.addPathSegment("app");
		client.addPathSegment("findCarByPlateNum.html");
		// client.addUrlParam("plate_num", plate_number);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("plate_num", plate_number));

		String resp = client.sendPost(params);
//		 String resp = Test_JSON[random.nextInt(2)];
		Gson gson = new GsonBuilder().setDateFormat(DATE_FOMAT_MS).create();
		AppEJB bResponse = (AppEJB) gson.fromJson(resp, AppEJB.class);
		return bResponse;
	}

	public static final String test_json1 = 
			"{" +
				"\"cars\":[" +
					"{" +
						"\"book\":{\"carid\":\"6cfa4eab-6be3-4324-8b1d-078026d1d3c0\",\"comment\":\"4343\",\"driver\":\"43434\",\"enddate\":\"2015-04-15 00:00:00\",\"idsbook\":\"271aea26-554a-429f-92e5-997ce352cc38\",\"isexpire\":\"N\",\"lease\":\"4343\",\"lessee\":\"4343\",\"startdate\":\"2015-04-07 00:00:00\",\"usedept\":\"4343\",\"userid\":\"133ec7b7-ae09-422c-8147-3baf6abc2a84\"}," +
						"\"car\":{\"carid\":\"6cfa4eab-6be3-4324-8b1d-078026d1d3c0\",\"name\":\"荣威\",\"origin\":\"CT01\",\"plate_num\":\"你-9999\",\"status\":\"Y\",\"type\":\"safas\"}," +
						"\"user\":{\"company\":\"3232\",\"dept\":\"3232\",\"name\":\"nick\"}}," +
					"{\"car\":{\"carid\":\"946b6a84-b523-4d73-ad17-620f22bad3a3\",\"name\":\"奔驰\",\"origin\":\"CT02\",\"plate_num\":\"沪A-9999\",\"status\":\"N\",\"type\":\"SUV\"}}," +
					"{\"car\":{\"carid\":\"a619bbb1-b3b4-4029-8fee-5f3001e55512\",\"name\":\"保时捷\",\"origin\":\"CT02\",\"plate_num\":\"沪A-9988\",\"status\":\"N\",\"type\":\"跑车\"}}," +
					"{\"car\":{\"carid\":\"a7b52c1f-611e-486c-b9bf-cfad976ebfd6\",\"name\":\"玛莎拉蒂\",\"origin\":\"CT02\",\"plate_num\":\"沪T-9999\",\"status\":\"N\",\"type\":\"SUV\"}" +
				"}]," +
				"\"exists\":true" +
			"}";
	public static final String test_json2 = 
			"{" +
				"\"exists\":false" +
			"}";
	public static final  String[] Test_JSON ={test_json1,test_json2};

}
