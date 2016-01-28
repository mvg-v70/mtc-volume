package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverBackView extends BroadcastReceiver {
	
  private static boolean cameraOn = false;

  @Override
  public void onReceive(Context context, Intent intent)
  {
	String action = intent.getAction();
	Settings settings = Settings.get(context); 
    if (action.equals("com.microntek.videosignalchange"))
    {
      String type = intent.getStringExtra("type");
      if (!cameraOn && type.equals("backview") && settings.getBrightnessCameraEnabled())
      {
        Log.d(Settings.LOG_ID, "backview.begin");
        cameraOn = true;
        // изменим €ркость при входе в режим камеры заднего вида
        Intent brIntent = new Intent(context,ReceiverBrightness.class);
        brIntent.putExtra("brightness", settings.getBrightnessCameraLevel());
        context.sendBroadcast(brIntent);
      }
    }
    else if (action.equals("com.microntek.backviewend"))
    {
      Log.d(Settings.LOG_ID, "backview.end");
      cameraOn = false;
      // изменим €ркость экрана при выходе из режима камеры заднего вида
      context.sendBroadcast(new Intent(context,ReceiverBrightness.class));
    }
  }

}
