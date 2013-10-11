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

import com.cgb.mylocation.MyLocation.LocationResult;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		
		final LocationResult locationResult = new LocationResult(){


			@Override
			public void gotLocation(Location location) {
				// TODO Auto-generated method stub
				String a="";
				
				
				a="pepe!";
				Log.e("Tengo", Double.toString(location.getLatitude()));
				//new SendPosition(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()), Float.toString(location.getAccuracy()), (String)location.getProvider(),  Long.toString(location.getTime() )).execute();
				
			}
		};

        
        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	

        		MyLocation myLocation = new MyLocation();
                myLocation.getLocation(getApplicationContext(), locationResult);
            }
        });

	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public class SendPosition extends AsyncTask<String, String, String>
	{

		String Lat;
		String Lon;
		String Acc;
		String Orig;
		String Hora;
	    public SendPosition(String lat, String lon, String acc, String orig, String hora) {
	        this.Lat =lat;
	        this.Lon = lon;
	        this.Acc = acc;
	        this.Orig =orig;
	        this.Hora =hora;
	        		
	        
	    }

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://agile-forest-7134.herokuapp.com/points/manual");

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
