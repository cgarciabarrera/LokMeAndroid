package com.cgb.mylocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;

public class Funciones {
	static String IMEI = "";
	public static SQLiteDatabase dbBizz = null; //openOrCreateDatabase(Funciones.nombreBD, // SQLiteDatabase.OPEN_READONLY, null);
	static String nombreBD = "";
	static boolean isServiceRunning =false;
	//static String Dominio = "http://192.168.1.5:3000/";
	static String Dominio = "http://agile-forest-7134.herokuapp.com/";
	static String PaginaNewPoint =  "points/manual/";
	static String AuthToken="";
	static String JsonDevices="";

	public static void PreparaConexionBD(Context c)
	{

		nombreBD = c.getFilesDir().getPath() +  "/databases/LokMe.db";
		if (Funciones.dbBizz==null)
		{

			Funciones.dbBizz = c.openOrCreateDatabase(Funciones.nombreBD, SQLiteDatabase.OPEN_READWRITE , null);
		}


	}


	public static void guardarenBDPunto(Context c,Location punto)
	{
		PreparaConexionBD(c);

		//preparo la cadena
		String strCadenaSQL="";

		strCadenaSQL = "Insert into points (accuracy, altitude, bearing, latitude, longitude, provider, speed, timefix, hasaccuracy, hasaltitude, hasbearing, hasspeed, charging, batterylevel ) values (" + (int)(punto.getAccuracy()) + ", "  + (int)(punto.getAltitude()) + ", "  + (int)(punto.getBearing()) + ", "  + Double.toString(punto.getLatitude()) + ", "  + Double.toString(punto.getLongitude()) + ", "  + EC(punto.getProvider()) + ", "  + Double.toString(punto.getSpeed()) + ", "  + Long.toString(punto.getTime()) + ", "  + BoolToINT(punto.hasAccuracy()) + ", "  + BoolToINT(punto.hasAltitude()) + ", "  + BoolToINT(punto.hasBearing()) + ", "  + BoolToINT(punto.hasSpeed()) + ", "  + BoolToINT(Funciones.getBatteryChargingStatus(c)) + ", " + (int)Funciones.getBatteryLevel(c) + ")";
		Funciones.dbBizz.execSQL(strCadenaSQL);
		String a="";



	}

	public static float getBatteryLevel(Context c) {
		Intent batteryIntent = c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


		// Error checking that probably isn't needed but I added just in case.
		if(level == -1 || scale == -1) {
			return 50.0f;
		}

		return ((float)level / (float)scale) * 100.0f; 
	}

	public static boolean getBatteryChargingStatus(Context c)
	{

		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = c.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		//		1 = Unknown
		//		2 = Charging
		//		3 = Discharging
		//		4 = Not Charging
		//		5 = Full

		if (status == 2 || status == 5) 
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public static String EC(String cadena)
	{
		cadena = DatabaseUtils.sqlEscapeString(cadena);

		return cadena;
	}

	public static int BoolToINT(Boolean bool)
	{

		return (bool) ? 1 : 0;
	}


	public static boolean HacerLogin(String url, String username, String password ,Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("email", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		if (! (username.equals("") && password.equals("")))
		{
			//login sin datos del timer, ignorar.
			HttpResponse JsonResp=null;

			try
			{
				JsonResp = HttpUtils.LlamadaHttpPost(url, nameValuePairs,ctx);
			}
			catch (Exception e)
			{
				success=false;
				return success;
			}

			try {
				//Log.e("resp", Funciones.getResponseBody(JsonResp));
				JSONObject jsonObject = new JSONObject(HttpUtils.getResponseBody(JsonResp));
				Iterator<?> keys = jsonObject.keys();
				Map<String, String> map = new HashMap<String, String>();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					map.put(key, jsonObject.getString(key));
				}

				if (map.get("token").length()>1)
				{
					AuthToken = map.get("token").toString();
					// BORRO TABLA LOGIN Y METO TO DO
					//hago valida el apikey realmente.
					success=true;

				}
				else
				{
					success=false;

				}


			} catch (JSONException e) {
				success=false;
				e.printStackTrace();
			}
		}
		else
		{
			success=false;
		}
		return success;

	}


	public static boolean ObtenDevices(String url, String token, Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", token));
		//login sin datos del timer, ignorar.
		HttpResponse JsonResp=null;

		try
		{
			JsonResp = HttpUtils.LlamadaHttpPost(url, nameValuePairs,ctx);
		}
		catch (Exception e)
		{
			success=false;
			return success;
		}

		try {
			//Log.e("resp", Funciones.getResponseBody(JsonResp));
			JSONObject jsonObject = new JSONObject(HttpUtils.getResponseBody(JsonResp));
			Iterator<?> keys = jsonObject.keys();
			Map<String, String> map = new HashMap<String, String>();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, jsonObject.getString(key));
			}


			try
			{
				if (map.get("lista").length()>1)
				{
					Funciones.JsonDevices= map.get("lista");
					success=true;

				}
				else
				{
					success=false;

				}
			}
			catch(Exception e)
			{
				success=false;
			
			}

		} catch (JSONException e) {
			success=false;
			e.printStackTrace();
		}
		return success;

	}

	
	public static String AgregaDevice(String url, String token, String IMEIaAgregar, Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", token));
		nameValuePairs.add(new BasicNameValuePair("imei", IMEIaAgregar));
		//login sin datos del timer, ignorar.
		HttpResponse JsonResp=null;

		try
		{
			JsonResp = HttpUtils.LlamadaHttpPost(url, nameValuePairs,ctx);
		}
		catch (Exception e)
		{
			success=false;
			return "Error";
		}

		try {
			//Log.e("resp", Funciones.getResponseBody(JsonResp));
			JSONObject jsonObject = new JSONObject(HttpUtils.getResponseBody(JsonResp));
			Iterator<?> keys = jsonObject.keys();
			Map<String, String> map = new HashMap<String, String>();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				map.put(key, jsonObject.getString(key));
			}


			try
			{
				if (map.get("respuesta").length()>1)
				{
					success=true;
					return map.get("respuesta");

				}
				else
				{
					success=false;
					return "Error";

				}
			}
			catch(Exception e)
			{
				success=false;
				return "Error";
			
			}

		} catch (JSONException e) {
			success=false;
			
			e.printStackTrace();
			return "Error";
		}
		

	}
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

}
