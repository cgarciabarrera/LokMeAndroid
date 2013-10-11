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
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;



public class ServicioLokMe extends Service {

	private static Timer timer = new Timer(); 




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
		
		Funciones.nombreBD =  "/data/data/"  + getApplicationContext().getPackageName() + "/databases/LokMe.db";
		if (Funciones.dbBizz==null)
		{
			Funciones.dbBizz = openOrCreateDatabase(Funciones.nombreBD, SQLiteDatabase.OPEN_READWRITE , null);
		}
		try {
			//android.os.Debug.waitForDebugger();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onReceive(Context context, Intent intent)
	{
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
		timer.scheduleAtFixedRate(new mainTask(), 10000, 120000);
		Funciones.isServiceRunning=true;

		//timer.scheduleAtFixedRate(new mainTask(), 60000, 30000);
	}
	private class mainTask extends TimerTask
	{ 

		final LocationResult locationResult = new LocationResult(){


			@Override
			public void gotLocation(Location location) {
				// TODO Auto-generated method stub

				new SendPosition(Funciones.Dominio + Funciones.PaginaNewPoint, Double.toString(location.getLatitude()),Double.toString(location.getLongitude()), Float.toString(location.getAccuracy()), (String)location.getProvider(),  Long.toString(location.getTime() )).execute();

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
		public SendPosition(String URLEnvio, String lat, String lon, String acc, String orig, String hora) {
			this.Lat =lat;
			this.Lon = lon;
			this.Acc = acc;
			this.Orig =orig;
			this.Hora =hora;
			this.URLEnvio = URLEnvio;
			


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