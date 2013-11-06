package com.cgb.mylocation;



import com.google.android.gms.maps.GoogleMap;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {

	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.activity_main);
		


		EditText txtIMEI = (EditText) findViewById(R.id.txtIMEI);


        Button btnURL = (Button) findViewById(R.id.btnURL);
        btnURL.setText(Funciones.Dominio);
        
        btnURL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Funciones.Dominio));
				startActivity(browserIntent);
            	
            }
        });
        
        
        if (!Funciones.isServiceRunning)
        {
        	startService(new Intent(MainActivity.this, ServicioLokMe.class));
        }
        
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		txtIMEI.setText(mngr.getDeviceId());
		
		
		Button bAnade = (Button) this.findViewById(R.id.Button01);

		bAnade.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	
				Intent i = new Intent(getApplicationContext(), Mapa.class);
				startActivity(i);
			}
		});
		

		
		

	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

}
