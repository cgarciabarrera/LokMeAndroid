package com.cgb.mylocation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;


import android.widget.TabHost.TabSpec;


public class MainActivity extends TabActivity {
	//private static final int PICK_CONTACT = 0;
	/** Called when the activity is first created. */
	//public static // SQLiteDatabase Funciones.dbBizz = null;
	//private ProgressDialog m_ProgressDialog = null;

	public static Activity actividad;
	ProgressDialog pd = null;

	public void onDestroy(Bundle savedInstanceState) {
		//super.onDestroy(savedInstanceState);
		pd.dismiss();
	}

	@Override
	public void onResume() {
		super.onResume();





	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost =  getTabHost();

		tabHost.setup();

		Intent intent = new Intent().setClass(this, Mapa.class);
		Bundle b2 = new Bundle();
		b2.putString("favorito", "0"); //Your id
		intent.putExtras(b2); //Put your id to your next Intent

		Intent intent2 = new Intent().setClass(this, Status.class);

		Intent intent3 = new Intent().setClass(this, Alarmas.class);
		
		
		Bundle b = new Bundle();
		b.putString("favorito", "1"); //Your id
		intent3.putExtras(b); //Put your id to your next Intent

		Resources res = getResources();
		// Initialize a TabSpec for each tab and add it to the TabHost
		TabSpec spec = tabHost.newTabSpec("ag1").setIndicator("Status",res.getDrawable(R.drawable.ic_launcher)).setContent(intent);
		tabHost.addTab(spec);
		TabSpec spec2 = tabHost.newTabSpec("ag2").setIndicator("Mapa",res.getDrawable(R.drawable.ic_plusone_medium_off_client)).setContent(intent2);
		tabHost.addTab(spec2);
		TabSpec spec3 = tabHost.newTabSpec("ag3").setIndicator("Alarms",res.getDrawable(R.drawable.ic_launcher)).setContent(intent3);
		tabHost.addTab(spec3);



	}
	
	
	



}