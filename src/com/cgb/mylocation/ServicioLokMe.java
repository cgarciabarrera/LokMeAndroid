package com.cgb.mylocation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.cgb.mylocation.MyLocation.LocationResult;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;



public class ServicioLokMe extends Service {

	private static Timer timer = new Timer(); 

	//private Context ctx=null;


	//public static // SQLiteDatabase Funciones.dbBizz = null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	String TAG="carlos3";
	@Override
	public void onCreate() {

		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		Funciones.IMEI=  mngr.getDeviceId();

		Funciones.PreparaConexionBD(getApplicationContext());

		final String CREATE_TABLE_points = "CREATE TABLE points (idpoint INTEGER PRIMARY KEY AUTOINCREMENT,accuracy double, altitude double, bearing double, longitude double, latitude double, provider string, speed double, timefix double, hasaccuracy boolean, hasaltitude boolean, hasbearing boolean, hasspeed boolean, charging boolean, batterylevel double) ;";
		try {
			Funciones.dbBizz.execSQL(CREATE_TABLE_points);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onReceive(Context context, Intent intent)
	{
		//this.ctx=context;

		if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
		{
			Intent serviceLauncher = new Intent(context, ServicioLokMe.class);
			context.startService(serviceLauncher);
			//Log.v("TEST", "Service loaded at start");
		}
	}


	@Override
	public void onDestroy() {
		//Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		//Log.d(TAG, "onDestroy");
		timer.cancel();
		Log.e("Servicio","Me han parado");
		Funciones.isServiceRunning=false;

		//		player.stop();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		//		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		//Log.d(TAG, "onStart");
		//		player.start();
		timer.scheduleAtFixedRate(new mainTask(), 10000, 60000);
		Funciones.isServiceRunning=true;

		//timer.scheduleAtFixedRate(new mainTask(), 60000, 30000);
	}
	private class mainTask extends TimerTask
	{ 

		final LocationResult locationResult = new LocationResult(){


			@Override
			public void gotLocation(Location location) {
				// TODO Auto-generated method stub

				//meterlo en BD lo primero, luego intentar mandarlo

				Funciones.guardarenBDPunto(getApplicationContext(), location);


				//leer de la bd todos los puntos e ir mandandolos y eliminando si ok.

				Cursor c = Funciones.dbBizz.rawQuery("select * from points order by idpoint desc", null);

				try
				{
					if (c != null) {						
						while (c.moveToNext()) { 			

							new SendPosition(Funciones.Dominio + Funciones.PaginaNewPoint, Integer.toString(c.getInt(c.getColumnIndex("idpoint"))) , Long.toString(c.getLong(c.getColumnIndex("timefix"))) , Integer.toString(c.getInt(c.getColumnIndex("batterylevel"))) , Integer.toString(c.getInt(c.getColumnIndex("charging"))),  Integer.toString(c.getInt(c.getColumnIndex("hasaltitude"))) , Integer.toString(c.getInt(c.getColumnIndex("hasaccuracy"))) , Integer.toString(c.getInt(c.getColumnIndex("hasbearing"))) , Integer.toString(c.getInt(c.getColumnIndex("hasspeed"))), Double.toString(location.getLatitude()), Double.toString(location.getLongitude()), Float.toString(location.getAccuracy()), (String)location.getProvider(),  Long.toString(location.getTime()), Float.toString(location.getSpeed()),Double.toString(location.getAltitude()), Double.toString(location.getBearing()) ).execute();
							Funciones.dbBizz.execSQL("delete from points where idpoint = " + c.getInt(c.getColumnIndex("idpoint")));
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				c.close();


			}
		};


		private Handler updateUI = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				MyLocation myLocation = new MyLocation();

				myLocation.getLocation(getApplicationContext(), locationResult);
			}
		};
		public void run() { 
			try {
				updateUI.sendEmptyMessage(0);
			} catch (Exception e) {e.printStackTrace(); }
		}


		//		public void run() 
		//		{
		//
		//
		//			String a="";
		//			Log.e("run", "service");
		//
		//			
		//			
		//			MyLocation myLocation = new MyLocation();
		//			Context c = getApplicationContext();
		//			myLocation.getLocation(getApplicationContext(), this.locationResult);
		//
		//
		//
		//		}



	}

	public class SendPosition extends AsyncTask<String, String, String> 
	{

		String Lat;
		String Lon;
		String Acc;
		String Orig;
		String Hora;
		String URLEnvio;
		String Speed;
		String Height;
		String Course;
		String BatteryLevel;
		String isCharging;
		String hasAltitude;
		String hasAccuracy;
		String hasBearing;
		String hasSpeed;
		String TimeFix;
		

		public SendPosition(String URLEnvio, String idPoint, String timefix, String batterylevel, String ischarging, String hasaltitude, String hasaccuracy, String hasbearing, String hasspeed, String lat, String lon, String acc, String orig, String hora, String Speed, String Height, String Course)  throws Exception {
			this.Lat =lat;
			this.Lon = lon;
			this.Acc = acc;
			this.Orig =orig;
			this.Hora =hora;
			this.URLEnvio = URLEnvio;
			this.Speed = Speed;
			this.Height=Height;
			this.Course=Course;
			this.BatteryLevel= batterylevel;
			this.isCharging = ischarging;
			this.hasAltitude = hasaltitude;
			this.hasAccuracy = hasaccuracy;
			this.hasBearing = hasbearing;
			this.hasSpeed = hasspeed;
			this.TimeFix = timefix;

		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URLEnvio);

			try {
				// Add your data

				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("latitude", Lat));
				nameValuePairs.add(new BasicNameValuePair("longitude", Lon));
				nameValuePairs.add(new BasicNameValuePair("imei", Funciones.IMEI));
				nameValuePairs.add(new BasicNameValuePair("accuracy", Acc));
				nameValuePairs.add(new BasicNameValuePair("provider", Orig));
				nameValuePairs.add(new BasicNameValuePair("speed", Speed));
				nameValuePairs.add(new BasicNameValuePair("height", Height));
				nameValuePairs.add(new BasicNameValuePair("course", Course));
				nameValuePairs.add(new BasicNameValuePair("batterylevel", BatteryLevel ));
				nameValuePairs.add(new BasicNameValuePair("ischarging", isCharging));
				nameValuePairs.add(new BasicNameValuePair("hasaltitude", hasAltitude));
				nameValuePairs.add(new BasicNameValuePair("hasaccuracy", hasAccuracy));
				nameValuePairs.add(new BasicNameValuePair("hasbearing", hasBearing));
				nameValuePairs.add(new BasicNameValuePair("hasspeed", hasSpeed));
				nameValuePairs.add(new BasicNameValuePair("timefix", TimeFix));
				
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}



	}





}