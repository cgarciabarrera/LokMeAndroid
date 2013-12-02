package com.cgb.mylocation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Login extends Activity {
	//private static final int PICK_CONTACT = 0;
	/** Called when the activity is first created. */
	//public static // SQLiteDatabase Funciones.dbBizz = null;
	//private ProgressDialog m_ProgressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Button btnLogin = (Button) this.findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {	

				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				finish();
			}
		});
	}
}
