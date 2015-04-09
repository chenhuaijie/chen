package com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper {
	private static final String TAG = "SharedPreferenceHelper";
	private static final String SERVER_ADDR_KEY = "server_addr";
	private Context context;

	public SharedPreferenceHelper(Context context) {
		this.context = context;
	}

	public void putServerAddress(String server_addr) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		editor.putString(SERVER_ADDR_KEY, server_addr);
		editor.commit();
	}

	public String getServerAddress() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String server_addr = prefs.getString(SERVER_ADDR_KEY, "");
		return server_addr;
	}

	public void removeServerAddress() {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		editor.remove(SERVER_ADDR_KEY);
		editor.commit();
	}
}
