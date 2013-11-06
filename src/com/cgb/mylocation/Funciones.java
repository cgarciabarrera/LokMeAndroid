package com.cgb.mylocation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.BatteryManager;
import android.provider.Contacts;

public class Funciones {
	static String IMEI = "";
	public static SQLiteDatabase dbBizz = null; //openOrCreateDatabase(Funciones.nombreBD, // SQLiteDatabase.OPEN_READONLY, null);
	static String nombreBD = "";
	static boolean isServiceRunning =false;
	//static String Dominio = "http://192.168.1.5:3000/";
	static String Dominio = "http://agile-forest-7134.herokuapp.com/";
	static String PaginaNewPoint =  "points/manual/";
	
	
	public static void PreparaConexionBD(Context c)
	{
		
		nombreBD = c.getFilesDir().getPath() +  "/databases/LokMe.db";
		if (Funciones.dbBizz==null)
		{
			
			Funciones.dbBizz = c.openOrCreateDatabase(Funciones.nombreBD, SQLiteDatabase.OPEN_READWRITE , null);
		}
		
		
	}
	
	
	public static void guardarenBDPunto(Context c,Location punto)
	{
		PreparaConexionBD(c);
		
		//preparo la cadena
		String strCadenaSQL="";
		
		strCadenaSQL = "Insert into points (accuracy, altitude, bearing, latitude, longitude, provider, speed, timefix, hasaccuracy, hasaltitude, hasbearing, hasspeed, charging, batterylevel ) values (" + (int)(punto.getAccuracy()) + ", "  + (int)(punto.getAltitude()) + ", "  + (int)(punto.getBearing()) + ", "  + Double.toString(punto.getLatitude()) + ", "  + Double.toString(punto.getLongitude()) + ", "  + EC(punto.getProvider()) + ", "  + Double.toString(punto.getSpeed()) + ", "  + Long.toString(punto.getTime()) + ", "  + BoolToINT(punto.hasAccuracy()) + ", "  + BoolToINT(punto.hasAltitude()) + ", "  + BoolToINT(punto.hasBearing()) + ", "  + BoolToINT(punto.hasSpeed()) + ", "  + BoolToINT(Funciones.getBatteryChargingStatus(c)) + ", " + (int)Funciones.getBatteryLevel(c) + ")";
		Funciones.dbBizz.execSQL(strCadenaSQL);
		String a="";
		
		
		
	}
	
	public static float getBatteryLevel(Context c) {
	    Intent batteryIntent = c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }

	    return ((float)level / (float)scale) * 100.0f; 
	}
	
	public static boolean getBatteryChargingStatus(Context c)
	{
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = c.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		
//		1 = Unknown
//		2 = Charging
//		3 = Discharging
//		4 = Not Charging
//		5 = Full
		
		if (status == 2 || status == 5) 
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	public static String EC(String cadena)
	{
		cadena = DatabaseUtils.sqlEscapeString(cadena);

		return cadena;
	}
	
	public static int BoolToINT(Boolean bool)
	{

		return (bool) ? 1 : 0;
	}

	
	
	
}
