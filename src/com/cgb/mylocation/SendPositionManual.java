package com.cgb.mylocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class SendPositionManual {

	
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
