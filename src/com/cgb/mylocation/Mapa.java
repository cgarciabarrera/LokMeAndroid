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

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

public class Mapa extends FragmentActivity implements OnMyLocationChangeListener {

	
	GoogleMap googleMap;
	
	ProgressDialog pd =null;
	
	@Override
	protected void onResume() {
		super.onResume();
		
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        pd = new ProgressDialog(Mapa.this);
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available        	
        	int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            
        }else {	// Google Play Services are available	
		
			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			
			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();
			
			// Enabling MyLocation Layer of Google Map
			//googleMap.setMyLocationEnabled(true);			
			
			// Setting event handler for location change
			// googleMap.setOnMyLocationChangeListener(this);	
			
			

			

			
        }

        Button b = (Button) findViewById(R.id.refresh);
        	
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setTitle("Lokme");
				pd.setMessage("Buscando devices");
				pd.setIcon(R.drawable.ic_launcher);
				pd.setCancelable(false);
				new LlenaPuntos(pd).execute();
			
			}
		});
        
		
		
        Button b2= (Button) findViewById(R.id.mapaVista);
    	
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) 
			{	
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		});

		
        Button b3= (Button) findViewById(R.id.satellite1);
    	
		b3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) 
			{	
				googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}
		});
		
		
		 googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	            @Override
	            public void onInfoWindowClick(Marker marker) {
	            	String a = marker.getTitle();


	            }
	        });
		
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle("Lokme");
		pd.setMessage("Buscando devices");
		pd.setIcon(R.drawable.ic_launcher);
		pd.setCancelable(false);
		new LlenaPuntos(pd).execute();



		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


		setContentView(R.layout.mapa);
		

	}

	@Override
	public void onMyLocationChange(Location location) {
		// TODO Auto-generated method stub
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		
		// Getting longitude of the current location
		double longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		// Showing the current location in Google Map
		//googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		//googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		
		// Setting latitude and longitude in the TextView tv_location
		tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
	}

	
	public class LlenaPuntos extends AsyncTask<Void, Integer, Integer> {
		private ProgressDialog progress;
		//private TextView price;
		private HttpResponse JsonResp;
		//private Context c;

		public LlenaPuntos(ProgressDialog progress) {
			this.progress = progress;
			//this.price = price;
			//this.c =c;
		}

		public void onPreExecute() {
			//progress.startAnimation(null);
			//this.progress.setVisibility(View.VISIBLE);
			progress.show();
		}


		public void onPostExecute(Integer result) {
			//this.progress.setVisibility(View.GONE);
			if(result ==1)
			{
				//procesar JsonDevices y pintarlo
				JSONArray jArray =null;
				try {
					jArray = new JSONArray(Funciones.JsonDevices);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
				googleMap.clear();
				for (int i=0;i< jArray.length();i++)
				{
					
					Marker marker=null;
					try {
						marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(Double.parseDouble(jArray.getJSONObject(i).getString("latitude")), Double.parseDouble(jArray.getJSONObject(i).getString("longitude"))))
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
						.title(jArray.getJSONObject(i).getString("imei"))
						.snippet("pepe\nJuan\njose"));
						mMarkerArray.add(marker);
						
						
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Marker m : mMarkerArray) {
				    builder.include(m.getPosition());
				}

				LatLngBounds bounds = builder.build();
				
				int padding = 120; // offset from edges of the map in pixels
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				
				googleMap.animateCamera(cu);

			}
			progress.dismiss();

		}

		@Override
		protected Integer doInBackground(Void... params) {

			if (Funciones.HacerLogin("http://lokme.lextrendlabs.com/api/v1/tokens.json", "cgarciabarrera@gmail.com", "Carlos01", getApplicationContext()))
			{
				
			}
			
			if(Funciones.ObtenDevices("http://lokme.lextrendlabs.com/devices/listamisdevices.json", Funciones.AuthToken, getApplicationContext()))
			{
			}
			
			return 1;
		}
	}

}
