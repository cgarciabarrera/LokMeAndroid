package com.cgb.mylocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;


public class Status extends Activity {
	//private static final int PICK_CONTACT = 0;
	/** Called when the activity is first created. */
	//public static // SQLiteDatabase Funciones.dbBizz = null;
	//private ProgressDialog m_ProgressDialog = null;




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		Button bLogout= (Button) findViewById(R.id.logout);

		bLogout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) 
			{	
				Funciones.PreparaConexionBD(getApplicationContext());
				Funciones.dbBizz.execSQL("delete from login");
				Funciones.Username="";
				Funciones.Password="";
				Intent i = new Intent(getApplicationContext(), Login.class);
				startActivity(i);
				finish();
				
				
				//Funciones.dbBizz.execSQL("insert into login (username, password) values (" + Funciones.EC(username) + ", " + Funciones.EC(password) +" )");

			}
		});
		
		final ToggleButton b = (ToggleButton) findViewById(R.id.trackingToggleButton);
		if (Funciones.PuedoEnviarPosiciones==1)
		{
			b.setChecked(true);
		}
		else
		{
			b.setChecked(false);
			
		}
		// attach an OnClickListener
		b.setOnClickListener(new View.OnClickListener()
		{
		    @Override
		    public void onClick(View v)
		    {
		        // your click actions go here
		    	if (b.isChecked())
		    	{
    				Funciones.Trackear(getApplicationContext(),true);
		    	}
		    	else
		    	{
    				Funciones.Trackear(getApplicationContext(),false);
		    	}
		    	
		    }
		});
	}
}
