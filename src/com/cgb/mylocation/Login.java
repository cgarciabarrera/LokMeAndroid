package com.cgb.mylocation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Login extends Activity {
	//private static final int PICK_CONTACT = 0;
	/** Called when the activity is first created. */
	//public static // SQLiteDatabase Funciones.dbBizz = null;
	//private ProgressDialog m_ProgressDialog = null;

	
	ProgressDialog pd = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		Funciones.IMEI=  mngr.getDeviceId();

		setContentView(R.layout.login);
		final TextView txtLogin = (TextView) findViewById(R.id.username);
		final TextView txtPassword = (TextView) findViewById(R.id.password);
		pd = new ProgressDialog(Login.this);

        Funciones.PreparaConexionBD(Login.this);
        
		final String CREATE_TABLE_points = "CREATE TABLE points (idpoint INTEGER PRIMARY KEY AUTOINCREMENT,accuracy double, altitude double, bearing double, longitude double, latitude double, provider string, speed double, timefix double, hasaccuracy boolean, hasaltitude boolean, hasbearing boolean, hasspeed boolean, charging boolean, batterylevel double) ;";
		try {
			Funciones.dbBizz.execSQL(CREATE_TABLE_points);

		} catch (Exception e) {
			e.printStackTrace();
		}

		final String CREATE_TABLE_login = "CREATE TABLE login (username string, password string);";
		try {
			Funciones.dbBizz.execSQL(CREATE_TABLE_login);

		} catch (Exception e) {
			e.printStackTrace();
		}

		final String CREATE_TABLE_enviarposiciones = "CREATE TABLE enviarposiciones (puedo integer);";
		try {
			Funciones.dbBizz.execSQL(CREATE_TABLE_enviarposiciones);
			Funciones.Trackear(Login.this, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		Cursor c = Funciones.dbBizz.rawQuery("SELECT * from login", null);
		if (c != null) 
		{
			if (c.moveToNext()) 
			{
				Funciones.Username= c.getString(c.getColumnIndex("username"));
				Funciones.Password= c.getString(c.getColumnIndex("password"));
				
				txtLogin.setText(Funciones.Username);
				txtPassword.setText(Funciones.Password);
				
				new IntentarLogin(pd, Funciones.Username, Funciones.Password).execute();
			}
			else
			{

			}
		}

		c.close();



		


		Button btnLogin = (Button) this.findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	

				new IntentarLogin(pd, txtLogin.getText().toString(), txtPassword.getText().toString()).execute();
			}
		});
		
        if (!Funciones.isServiceRunning)
        {
                startService(new Intent(Login.this, ServicioLokMe.class));
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //abro la bd y busco el login
        


	}
	
	

	public class IntentarLogin extends AsyncTask<Void, Integer, Integer> {
		private ProgressDialog progress;
		private String Username;
		private String Password;

		public IntentarLogin(ProgressDialog progress, String Username, String Password) {
			this.progress = progress;
			this.Username = Username;
			this.Password= Password;

			//this.price = price;
			//this.c =c;
		}

		public void onPreExecute() {
			//progress.startAnimation(null);
			//this.progress.setVisibility(View.VISIBLE);
			progress.setTitle("Lokme");
			progress.setMessage("Trying to log in");
			progress.show();
			
		}


		public void onPostExecute(Integer result) {
			//this.progress.setVisibility(View.GONE);
			if(result ==1)
			{
				//meterlo en bd y arrancar la siguiente
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				finish();
			}
			else
			{
				
			}
			progress.dismiss();

		}

		@Override
		protected Integer doInBackground(Void... params) {

			if (Funciones.HacerLogin("http://lokme.lextrendlabs.com/api/v1/tokens.json", this.Username, this.Password, getApplicationContext()))
			{
				return 1;

			}
			else
			{
				return 0;
			}
		}
	}


	
}
