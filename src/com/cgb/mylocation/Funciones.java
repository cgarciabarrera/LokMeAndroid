package com.cgb.mylocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Funciones {
	static String IMEI = "";
	public static SQLiteDatabase dbBizz = null; //openOrCreateDatabase(Funciones.nombreBD, // SQLiteDatabase.OPEN_READONLY, null);
	static String nombreBD = "123456";
	static boolean isServiceRunning =false;
	static String Dominio = "http://agile-forest-7134.herokuapp.com/";
	static String PaginaNewPoint =  "points/manual/";
	
	
	public static void PreparaConexionBD(Context c)
	{
		
		nombreBD =  "/data/data/"  + c.getPackageName() + "/databases/LokMe.db";
		if (Funciones.dbBizz==null)
		{
			Funciones.dbBizz = c.openOrCreateDatabase(Funciones.nombreBD, SQLiteDatabase.OPEN_READWRITE , null);
		}
		
	}
}
