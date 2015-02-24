package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverBoot extends BroadcastReceiver {

  // TODO: нужно ли это ?
  // public ReceiverBoot() {}
	
  @Override
  public void onReceive(Context context, Intent intent) 
  {
    String action = intent.getAction();
    Log.d(Settings.LOG_ID,"ReceiverBoot.onReceive "+action);
    if (Settings.get(context).getServiceEnable() && !ServiceMain.isRunning)
      // стартуем сервис
      context.startService(new Intent(context, ServiceMain.class));
  }
}
