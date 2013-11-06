package com.cgb.mylocation;


import java.util.ArrayList;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Dialog;

public class Mapa extends FragmentActivity implements OnMyLocationChangeListener {

	
	GoogleMap googleMap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.mapa);
		
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

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
			//googleMap.setOnMyLocationChangeListener(this);		
			

			
        }

        Button b = (Button) findViewById(R.id.button1);
        	
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	
				ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
				
				Marker marker =googleMap.addMarker(new MarkerOptions()
		        .position(new LatLng(40.43, -3.68))
		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.female))
		        .title("Pais: España").snippet("pepe\nJuan\njose"));
				
				mMarkerArray.add(marker);
				
				marker =googleMap.addMarker(new MarkerOptions()
		        .position(new LatLng(40.87, -3.48))
		        .title("Nexus 4").snippet("CArlos García de la Barrera | 50 meter | 5 minutes ago"));
				
				mMarkerArray.add(marker);

				marker =googleMap.addMarker(new MarkerOptions()
		        .position(new LatLng(43.475, -3.7969865))
		        .title("Nexus 4").snippet("CArlos García de la Barrera | 50 meter | 5 minutes ago"));
				
				mMarkerArray.add(marker);
				 googleMap.addCircle(new CircleOptions()
			     .center(new LatLng(40.43, -3.68))
			     .radius(400)
			     .strokeColor(Color.RED)
			     .strokeWidth(2)
			     .fillColor(Color.TRANSPARENT));
				 
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL );
				
				
				
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (Marker m : mMarkerArray) {
				    builder.include(m.getPosition());
				}

				LatLngBounds bounds = builder.build();
				
				int padding = 120; // offset from edges of the map in pixels
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				
				googleMap.animateCamera(cu);
				//googleMap.moveCamera(cu);

			
			}
		});
        
		
		

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


}
