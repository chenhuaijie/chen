package com.license.number.network;

import org.apache.http.StatusLine;

public class HttpException extends Exception {
	private StatusLine statusLine;
	
	public HttpException(StatusLine sl) {
		statusLine = sl;
	}
	
	public HttpException(StatusLine sl, String content) {
		super(content);
		statusLine = sl;
	}
	
	public StatusLine getStatusLine() {
		return statusLine;
	}
}
