package com.cgb.mylocation;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

public class HttpUtils {
	private static int TimeOutConexion = 5000;
	
	private static HttpParams ParametrosHTTP()
	{
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection =  TimeOutConexion;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket =  TimeOutConexion;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		//httpParameters.setParameter("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		return httpParameters;
	}

	public static HttpResponse LlamadaHttpPut(String url,List<NameValuePair> nameValuePairs, Context ctx) throws Exception
	{
		HttpClient httpclient = new DefaultHttpClient(ParametrosHTTP());		
		HttpPut httppost = new HttpPut(url);
		HttpResponse response= null;
		if (hayInternet(ctx)){
			try {			
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				// Execute HTTP Post Request
				httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
				response = httpclient.execute(httppost);
	
				return response;
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();

			}				
		}
		else{			

		}
		return response;	
	}

	public static HttpResponse LlamadaHttpDelete(String url, Context ctx) throws Exception
	
	{
		HttpClient httpclient = new DefaultHttpClient(ParametrosHTTP());	
		HttpDelete httppost = new HttpDelete(url);
		HttpResponse response= null;
		
		if (hayInternet(ctx)){
			try {
				response = httpclient.execute(httppost);	
				return response;	
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();

			}		
		}
		else{			

		}
		return response;	
	}
	
	public static HttpResponse LlamadaHttpPost(String url,List<NameValuePair> nameValuePairs, Context ctx)
	
	{
		HttpClient httpclient = new DefaultHttpClient(ParametrosHTTP());		
		HttpPost httppost = new HttpPost(url);
		HttpResponse response= null;
		
		if (hayInternet(ctx)){			
			try {				
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));	
				// Execute HTTP Post Request
				httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
				response = httpclient.execute(httppost);
	
				return response;
			} 			
			catch (Exception ex)
			{
				ex.printStackTrace();
				
			}			
		}
		else{			
			
		}
		return response;				
	}

	public static HttpResponse LlamadaHttpGet(String url,List<NameValuePair> nameValuePairs, Context ctx) throws Exception
	{			
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 5000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		HttpClient httpclient = new DefaultHttpClient(ParametrosHTTP());	
		
		
		HttpGet httppost = new HttpGet(url);
		HttpResponse response= null;
		
		if (hayInternet(ctx)){	
			try {
				httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
				response = httpclient.execute(httppost);
	
				return response;	
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
				
			}
		}
		else{
			
		}
		return response;		
	}
	
	public static String getResponseBody(HttpResponse response)
	{
		String responseText="";
		try {
			//responseText =  EntityUtils.toString(response.getEntity(),  "ISO-8859-1");
			responseText =  EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (org.apache.http.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseText;
	}
	
//	public static boolean hayInternet() 
//	{
//		Context c = Utils.CoreLib.Context();
//		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//		return activeNetworkInfo != null;
//	}

	public static boolean hayInternet(Context contexto) {
		boolean lRet = false;
		//Context contexto = Utils.CoreLib.Context();

		try{
			ConnectivityManager conMgr =  (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info= conMgr.getActiveNetworkInfo();  
			if(info != null && info.isConnected()) {  
				lRet = true;								
			}
			else{  
				lRet = false;
			}
		}catch (Exception e) {
			Log.d("Connection Error", e.toString());
			lRet = false ;
		}
		return lRet;
	}

	private static boolean isAirplaneModeOn(Context context) {

		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;

	}
}
