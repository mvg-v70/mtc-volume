package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverScreenSaver extends BroadcastReceiver
{
  private static boolean ss_flag = false;	
	
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();
    Log.d(Settings.LOG_ID,"ReceiverScreenSaver.onReceive "+action);
    if (action.equals("com.android.music.querystate"))
    {
      ss_flag = true;
      Log.d(Settings.LOG_ID,"screensaver on");
    }
    else if (action.equals("com.microntek.musicclockreset"))
    {
      ss_flag = false;
      Log.d(Settings.LOG_ID,"screensaver off");
    }
    Settings.set_ss_flag(ss_flag);
  }
  
  public static boolean get_ss_flag()
  {
    return ss_flag;
  }
}
