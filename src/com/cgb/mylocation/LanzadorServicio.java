package com.cgb.mylocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cgb.mylocation.ServicioLokMe;

public class LanzadorServicio extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent pushIntent = new Intent(context, ServicioLokMe.class);
			if (!Funciones.isServiceRunning)
			{
				context.startService(pushIntent);
			}
		}
	}
}