package com.license.number.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.RecoverySystem.ProgressListener;
import android.util.Log;

public class Client {
	private static final String TAG = "Client";

	private String mAddress;
	private String mUrlParam = "";

	public Client(String address) {
		mAddress = address;
	}

	public Client addPathSegment(String segment) {
		if (mAddress == null) {
			mAddress = "";
		}

		if (mAddress != null) {
			mAddress = mAddress.concat("/" + segment);
		}

		return this;
	}

	public Client addPathSegment(int segment) {
		if (mAddress == null) {
			mAddress = "";
		}

		if (mAddress != null) {
			mAddress = mAddress.concat("/" + segment);
		}

		return this;
	}

	public Client addUrlParam(String name, int value) {
		if (mUrlParam.length() > 0) {
			mUrlParam = mUrlParam.concat(",");
		}
		if (name != null && name.length() > 0) {
			mUrlParam = mUrlParam.concat(name + "=" + value);
		}
		return this;
	}

	public Client addUrlParam(String name, String value) {
		if (mUrlParam.length() > 0) {
			mUrlParam = mUrlParam.concat("&");
		}
		if (name != null && name.length() > 0 && value != null
				&& value.length() > 0) {
			mUrlParam = mUrlParam.concat(name + "=" + value);
		}
		return this;
	}

	public String get() throws HttpException, IOException {
		String url;

		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}

		String resp = "";

		Log.v(TAG, "GET " + url);
		HttpGet get = new HttpGet(url);
		HttpResponse httpResponse = newHttpClient().execute(get);
		int code = httpResponse.getStatusLine().getStatusCode();

		// if (code >= 300 && code < 400) {
		// Header locationHeader = httpResponse.getFirstHeader("location");
		// if (locationHeader != null) {
		// ResponseData responseData = new ResponseData();
		// responseData.setRedirectLocation(locationHeader.getValue());
		// return responseData;
		// } else {
		// logger.warn("Invalid redirect location at " + url);
		// }
		// }
		resp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		Log.v(TAG, "RESPONSE " + resp);
		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine(), resp);
		}

		return resp;
	}

	public String put(String request, Header[] headers) throws IOException,
			HttpException {
		String url;

		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}

		String resp = "";
		Log.v(TAG, "PUT " + url);
		Log.v(TAG, "REQUEST " + request);

		HttpPut put = new HttpPut(url);
		if (headers != null) {
			for (Header h : headers) {
				Log.v(TAG, "HEADER " + h.getName() + ":" + h.getValue());
				put.addHeader(h);
			}
		}

		StringEntity se = new StringEntity(request, "UTF-8");
		put.setEntity(se);
		HttpResponse httpResponse = newHttpClient().execute(put);
		int code = httpResponse.getStatusLine().getStatusCode();
		resp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		Log.v(TAG, "RESPONSE " + resp);

		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine(), resp);
		}

		return resp;
	}

	// public String put(InputStream request,String contentType, Header[]
	// headers) throws IOException, HttpException {
	public String put(File request, String contentType, Header[] headers,
			final ProgressListener listener) throws IOException, HttpException {
		String url;

		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}

		String resp = "";
		Log.v(TAG, "PUT " + url);
		Log.v(TAG, "REQUEST " + request);

		HttpPut put = new HttpPut(url);
		if (headers != null) {
			for (Header h : headers) {
				Log.v(TAG, "HEADER " + h.getName() + ":" + h.getValue());
				put.addHeader(h);
			}
		}

		FileEntity fe = new FileEntity(request, contentType) {
			@Override
			public void writeTo(OutputStream outstream) throws IOException {
				if (outstream == null) {
					throw new IllegalArgumentException(
							"Output stream may not be null");
				}

				final InputStream instream = new FileInputStream(this.file);
				try {
					final byte[] tmp = new byte[2048];
					int l;
					long transfteredBytes = 0;
					while ((l = instream.read(tmp)) != -1) {
						// Log.v(TAG, "read " + l + "bytes");
						outstream.write(tmp, 0, l);
						if (listener != null) {
							transfteredBytes += l;
//							listener.onProgress(transfteredBytes);
						}
					}
					outstream.flush();
				} finally {
					instream.close();
				}
			}
		};

		// InputStreamEntity ise= new InputStreamEntity(request, -1);
		put.setEntity(fe);
		HttpResponse httpResponse = newHttpClient().execute(put);
		int code = httpResponse.getStatusLine().getStatusCode();
		resp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		Log.v(TAG, "RESPONSE " + resp);

		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine(), resp);
		}

		return resp;

	}

	public String post(String request, Header[] headers) throws HttpException,
			IOException {
		String url;

		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}

		String resp = "";

		Log.v(TAG, "POST " + url);

		HttpPost post = new HttpPost(url);
		if (headers != null) {
			for (Header h : headers) {
				Log.v(TAG, "HEADER " + h.getName() + ":" + h.getValue());
				post.addHeader(h);
			}
		}

		Log.v(TAG, "REQUEST " + request);
		StringEntity se = new StringEntity(request, "UTF-8");
		post.setEntity(se);
		HttpResponse httpResponse = newHttpClient().execute(post);
		int code = httpResponse.getStatusLine().getStatusCode();
		resp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		Log.v(TAG, "RESPONSE " + resp);
		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine(), resp);
		}

		return resp;

	}
	
	public String post(Header[] headers,HttpEntity httpEntity) throws HttpException,
			IOException {
		String url;
		
		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}
		
		String resp = "";
		
		Log.i(TAG, "POST " + url);
		
		HttpPost post = new HttpPost(url);
		if (headers != null) {
			for (Header h : headers) {
				Log.v(TAG, "HEADER " + h.getName() + ":" + h.getValue());
				post.addHeader(h);
			}
		}
		if(httpEntity!=null)
			post.setEntity(httpEntity) ;
		
		HttpResponse httpResponse = newHttpClient().execute(post);
		int code = httpResponse.getStatusLine().getStatusCode();
		resp = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		Log.i(TAG, "RESPONSE " + resp);
		
		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine(), resp);
		}
		
		return resp;
		
		}
	

	public boolean delete() throws HttpException, IOException {
		String url;

		if (mUrlParam.length() > 0) {
			url = mAddress + "?" + mUrlParam;
		} else {
			url = mAddress;
		}

		Log.v(TAG, "DELETE " + url);
		HttpDelete delete = new HttpDelete(url);
		HttpResponse httpResponse = newHttpClient().execute(delete);
		int code = httpResponse.getStatusLine().getStatusCode();
		Log.v(TAG, "STATUS " + code + " " + httpResponse.getStatusLine());
		if (code >= 400) {
			throw new HttpException(httpResponse.getStatusLine());
		}

		return true;
	}

	public static HttpClient newHttpClient() {
		return new DefaultHttpClient();
		// try {
		// KeyStore trustStore =
		// KeyStore.getInstance(KeyStore.getDefaultType());
		// trustStore.load(null, null);
		//
		// SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
		// sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		//
		// HttpParams params = new BasicHttpParams();
		// HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		//
		// SchemeRegistry registry = new SchemeRegistry();
		// registry.register(new Scheme("http",
		// PlainSocketFactory.getSocketFactory(), 80));
		// registry.register(new Scheme("https", sf, 443));
		//
		// ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
		// registry);
		//
		// return new DefaultHttpClient(ccm, params);
		// } catch (Exception e) {
		// return new DefaultHttpClient();
		// }
	}

	private static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
