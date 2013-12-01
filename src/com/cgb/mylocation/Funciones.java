package com.cgb.mylocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;

public class Funciones {
	static String IMEI = "";
	
	static String IMEIparametro="";
	public static SQLiteDatabase dbBizz = null; //openOrCreateDatabase(Funciones.nombreBD, // SQLiteDatabase.OPEN_READONLY, null);
	static String nombreBD = "";
	static boolean isServiceRunning =false;
	//static String Dominio = "http://192.168.1.5:3000/";
	static String Dominio = "http://lokme.lextrendlabs.com/";
	static String PaginaNewPoint =  "points/manual/";
	static String AuthToken="";
	static String JsonDevices="";
	
	static String JsonDevicesCompartidos= "";

	static String JsonArrayPuntosDevice="";
	
	static boolean StatusAPP = true;

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


	public static boolean ObtenDevices(String url, String urlCompartidos, String token, Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", token));
		//login sin datos del timer, ignorar.
		HttpResponse JsonResp=null;

		//devices normales
		
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
		
		
		
		//devices incluyendo compartidos
		
		
		try
		{
			JsonResp = HttpUtils.LlamadaHttpPost(urlCompartidos, nameValuePairs,ctx);
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
					Funciones.JsonDevicesCompartidos= map.get("lista");
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


	public static int ObtenPuntosdeDevice(String url, String IMEI, String token, Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", token));
		nameValuePairs.add(new BasicNameValuePair("imei", IMEI));

		//login sin datos del timer, ignorar.
		HttpResponse JsonResp=null;

		try
		{
			JsonResp = HttpUtils.LlamadaHttpPost(url, nameValuePairs,ctx);
		}
		catch (Exception e)
		{
			success=false;
			return 0;
		}

		try {
			//Log.e("resp", Funciones.getResponseBody(JsonResp));


			JsonArrayPuntosDevice= HttpUtils.getResponseBody(JsonResp);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

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

	public static void CentraSobreLatLng(GoogleMap googleMap, ArrayList<LatLng> mLatLngArray)
	{
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng m : mLatLngArray) {
			builder.include(m);
		}

		LatLngBounds bounds = builder.build();

		int padding = 120; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		googleMap.animateCamera(cu);
	}

	public static void CentraSobreMarker(GoogleMap googleMap, ArrayList<Marker> mMarkerArray)
	{
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker m : mMarkerArray) {
			builder.include(m.getPosition());
		}

		LatLngBounds bounds = builder.build();

		int padding = 120; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		googleMap.animateCamera(cu);
	}

	public static void PintaRuta(GoogleMap googleMap, ArrayList<LatLng> mLatLngArray, boolean limpiaMapa)
	{
		JSONArray jArray =null;
		try {
			jArray = new JSONArray(Funciones.JsonArrayPuntosDevice);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();

		LatLng primer=null;

		try {
			primer = new LatLng(Double.parseDouble(jArray.getJSONObject(0).getString("latitude")), Double.parseDouble(jArray.getJSONObject(0).getString("longitude")));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (limpiaMapa)

		{
			googleMap.clear();
		}

		//Metemos el punto 

		googleMap.addMarker(new MarkerOptions()
		.position(primer)
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

		for (int i=0;i< jArray.length();i++)
		{

			Marker marker=null;
			try {

				//marker= new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude")));
				LatLng point = new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude")));
				mLatLngArray.add(point);
				//mMarkerArray.add(point);


				//			CircleOptions circle=new CircleOptions();
				//				circle.strokeWidth(1);
				//				circle.center(point).fillColor(Color.TRANSPARENT).radius(Double.parseDouble(jArray.getJSONObject(i).getString("accuracy")));
				//				googleMap.addCircle(circle); 


			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		PolylineOptions polylineOptions = new PolylineOptions();
		polylineOptions.color(Color.BLUE );
		polylineOptions.width(5);
		polylineOptions.addAll(mLatLngArray);
		googleMap.addPolyline(polylineOptions);

	}
	public static void PintaDevices(GoogleMap googleMap,ArrayList<Marker> mMarkerArray , boolean limpiaMapa)

	{

		JSONArray jArray =null;
		try {
			jArray = new JSONArray(Funciones.JsonDevices);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (limpiaMapa)
		{
			googleMap.clear();
		}
		for (int i=0;i< jArray.length();i++)
		{

			Marker marker=null;
			try {
				marker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude"))))
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
				.title(jArray.getJSONObject(i).getString("username") + " " + jArray.getJSONObject(i).getString("name") )
				.snippet( jArray.getJSONObject(i).getString("imei")));
				mMarkerArray.add(marker);

				LatLng point = new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude")));
				CircleOptions circle=new CircleOptions();
				circle.strokeWidth(1);
				circle.center(point).fillColor(Color.TRANSPARENT).radius(Double.parseDouble(jArray.getJSONObject(i).getString("accuracy")));
				googleMap.addCircle(circle); 


			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

	}




}
