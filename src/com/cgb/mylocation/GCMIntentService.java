package com.cgb.mylocation;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context context, String errorId){
		// Error en el registro: tratamiento del error
		String a="";

	}

	@Override
	protected void onMessage(Context context, Intent intent){
		// Notificación recibida: informo al usuario u otra acción   

		Funciones.mostrarNotificacion(context, intent.getStringExtra("score"), "texto notificacion", "detalle notificacion");
		
	
	}

	@Override
	protected void onRegistered(Context context, String regId){
		// Registro correcto: envío el regId a mi servidor   
		String a=""; 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("auth_token", Funciones.AuthToken));
		nameValuePairs.add(new BasicNameValuePair("imei", Funciones.IMEI));
		nameValuePairs.add(new BasicNameValuePair("regid", regId));
		
		//login sin datos del timer, ignorar.
		HttpResponse JsonResp=null;

		try
		{
			JsonResp = HttpUtils.LlamadaHttpPost(Funciones.RegistrarRegidAndroid, nameValuePairs,context);
			Funciones.RegistrarRegidAndroid = regId;
		}
		catch (Exception e)
		{
			//sacar mensaje
		}



	}

	@Override
	protected void onUnregistered(Context context, String regId){
		// Borrado correcto: informo a mi servidor
		String a="";

	}
}