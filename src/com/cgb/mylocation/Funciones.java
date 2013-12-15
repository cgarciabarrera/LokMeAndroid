package com.cgb.mylocation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
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
	
	static String JsonDevicesCompleto="";
	
	static String JsonArrayPuntosDevice="";



	static Date dateUltimaSincro= new Date(1990,1,1);

	static String DeviceContreto="";

	static String ViendoPunto="";

	static String Username="";
	static String Password="";

	static Integer PuedoEnviarPosiciones=1;

	static Integer DeviceIncluidoEnCuenta=0;

	static String URLAGREGADEVICE = "http://lokme.lextrendlabs.com/devices/adddeviceapi.json";

	static LatLng Ultima=null;
	
	static Integer noPreguntarAgregarDevice=0;
	
	static String RegistrationID_GCM="";
	
	static String RegistrarRegidAndroid = "http://lokme.lextrendlabs.com/devices/addregid.json";

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
				//busco el error
				try
				{
					if (map.get("token").length()>1)
					{
						AuthToken = map.get("token").toString();
						// BORRO TABLA LOGIN Y METO TO DO
						//hago valida el apikey realmente.
						success=true;

						// lo grabo en la tabl login de la bd
						Funciones.dbBizz.execSQL("delete from login");
						Funciones.dbBizz.execSQL("insert into login (username, password) values (" + Funciones.EC(username) + ", " + Funciones.EC(password) +" )");


					}
					else
					{
						success=false;

					}
				}
				catch(Exception e)
				{
					success=false;
					e.printStackTrace();

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
		JSONArray ja = null;
		try {
			ja =  concatArray(new JSONArray(JsonDevices), new JSONArray(JsonDevicesCompartidos));
			//JsonDevicesCompleto = ja.toString();
			JsonDevicesCompleto = JsonDevicesCompartidos;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JsonDevicesCompleto =JsonDevicesCompartidos;
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


	public static String AgregaDevice(String url, String Nombre, String token, String IMEIaAgregar, Context ctx)

	{

		boolean success=false;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", token));
		nameValuePairs.add(new BasicNameValuePair("imei", IMEIaAgregar));
		nameValuePairs.add(new BasicNameValuePair("name", Nombre));
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

					GCMRegistrar.checkDevice(ctx);
					GCMRegistrar.checkManifest(ctx);

					Funciones.RegistrationID_GCM = GCMRegistrar.getRegistrationId(ctx);

					if (Funciones.RegistrationID_GCM.equals("")) {
						GCMRegistrar.register(ctx, "4050108544");
						Funciones.RegistrationID_GCM = GCMRegistrar.getRegistrationId(ctx);

					} else {
						Log.v("GCM", "Ya estoy registrado");
					}
					
					//GCMRegistrar.unregister(ctx);

					
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
		if (mMarkerArray.size()>0)
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

	public static void PintaRutaPuntos(GoogleMap googleMap,ArrayList<Marker> mMarkerArray , boolean limpiaMapa)

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


	public static void PintaDevices(GoogleMap googleMap,ArrayList<Marker> mMarkerArray , boolean limpiaMapa)

	{
		int existe=0;
		JSONArray jArray =null;
		try {
			jArray = new JSONArray(Funciones.JsonDevicesCompleto );
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

			float color = 0;
			
			Marker marker=null;
			try {
				if (jArray.getJSONObject(i).getString("propio").equals("1"))
				{
					color = BitmapDescriptorFactory.HUE_GREEN;
				}
				else
				{
					color = BitmapDescriptorFactory.HUE_ROSE;
				}
				marker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude"))))
				.icon(BitmapDescriptorFactory.defaultMarker(color))
				.title(jArray.getJSONObject(i).getString("username") + " " + jArray.getJSONObject(i).getString("name") )
				.snippet( jArray.getJSONObject(i).getString("imei")));
				mMarkerArray.add(marker);

				if (Funciones.IMEI.equals(jArray.getJSONObject(i).getString("imei")))
				{
					existe=1;
				}


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
		Funciones.DeviceIncluidoEnCuenta=existe;

	}


	public static int SegundsosDesde(Date inicial)
	{
		long diffInMs = new Date().getTime() - inicial.getTime();
		return  (int) TimeUnit.MILLISECONDS.toSeconds(diffInMs);

	}

	public static void Trackear(Context c, boolean estado)
	{
		Funciones.PreparaConexionBD(c);
		if (estado)
		{
			Funciones.dbBizz.execSQL("delete from enviarposiciones");
			Funciones.dbBizz.execSQL("insert into enviarposiciones (puedo) values (1) ");
			Funciones.PuedoEnviarPosiciones=1;
		}
		else
		{
			Funciones.dbBizz.execSQL("delete from enviarposiciones");
			Funciones.dbBizz.execSQL("insert into enviarposiciones (puedo) values (0) ");
			Funciones.PuedoEnviarPosiciones=0;
		}
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}


	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	} 

	public static JSONArray concatArray(JSONArray arr1, JSONArray arr2) throws JSONException {
	    JSONArray result = new JSONArray();
	    for (int i = 0; i < arr1.length(); i++) {
	        result.put(arr1.get(i));
	    }
	    for (int i = 0; i < arr2.length(); i++) {
	        result.put(arr2.get(i));
	    }
	    return result;
	}
	
	static void mostrarNotificacion(Context c, String tituloNotificacion, String textoNotificacion, String detalleNotificacion) {

//		tituloNotificacion = "Bizzcall";
//		textoNotificacion = "Bizzcall notifications";
//		detalleNotificacion = "New notifications from your administrator";
		final int MY_NOTIFICATION_ID=1;
		NotificationManager notificationManager;
		Notification myNotification;
		notificationManager =(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
		myNotification = new Notification(R.drawable.ic_launcher,tituloNotificacion, System.currentTimeMillis());
		Context context = c;
		String notificationTitle = textoNotificacion;
		String notificationText = detalleNotificacion;
		Intent myIntent = new Intent(c, Login.class);
		PendingIntent pendingIntent= PendingIntent.getActivity(c,0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
		myNotification.defaults |= Notification.DEFAULT_SOUND;
		myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		myNotification.setLatestEventInfo(context,
				notificationTitle,
				notificationText,
				pendingIntent);
		notificationManager.notify(MY_NOTIFICATION_ID, myNotification);


	}
	
}
