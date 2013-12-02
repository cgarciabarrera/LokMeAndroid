package com.cgb.mylocation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;


public class Devices extends Activity
{
	String idPeriodo="0";	
	String IdContacto ="0";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onResume() {
		super.onResume();



		setContentView(R.layout.basedevices);

		CargarDevices();					
	}

	public void CargarDevices(){

		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = null;			

		JSONArray jArray = null;
		try {
			jArray = new JSONArray(Funciones.JsonDevicesCompartidos);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		for (int i=0; i<jArray.length();i++){							
			map = new HashMap<String, String>();
			try
			{
				map.put("usuario",jArray.getJSONObject(i).getString("usuario"));
				map.put("propio",jArray.getJSONObject(i).getString("propio"));
				map.put("devicename",jArray.getJSONObject(i).getString("name"));
				map.put("imei",jArray.getJSONObject(i).getString("imei"));
				mylist.add(map);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
				
		}

		SpecialAdapter adapter = new SpecialAdapter(getApplicationContext(), mylist, R.layout.detalledevice,
				new String[] {"usuario", "devicename", "imei"}, new int[] {R.id.textUsuario, R.id.textDevice, R.id.textIMEI});
		ListView list = (ListView) findViewById(R.id.listView1);
		
		list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				TextView txtImei= (TextView) view.findViewById(R.id.textIMEI);
				
				Funciones.IMEIparametro = txtImei.getText().toString();
				Funciones.dateUltimaSincro= new Date(1990,1,1);
				((MainActivity) getParent()).getTabHost().setCurrentTab(0);
				
			}
		});

		list.setAdapter(adapter);
	}

	public class SpecialAdapter extends SimpleAdapter {
		//private int[] colors = new int[] { 0x30FF0000, 0x300000FF };
		private int[] colors = new int[] { Color.BLACK, Color.BLACK, Color.BLACK};
		
		
		//private int[] colors = new int[] { Color.parseColor("#ecffee"), Color.parseColor("#ffedd7"), Color.parseColor("#ffebeb")};
		public SpecialAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
			super(context, items, resource, from, to);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);			
			int colorPos = position % colors.length;
			
			return view;
		}
	}

}
