package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverSafeVolume extends BroadcastReceiver
{
  // широковещательные события microntek вкл/выкл
  @SuppressWarnings("unused")
private static final String STARTUP_ACTION = "com.microntek.startApp";
  private static final String ACC_ACTION = "com.microntek.acc";
  private static final String ACC_STATE = "accstate";
  private static final String ACC_OFF = "accoff";

  @Override
  public void onReceive(Context context, Intent intent)
  {
    Settings settings = Settings.get(context);
    // безопасный уровень включен в настройках
    if (!settings.getSafeVolumeEnable()) return;
    //
    String action = intent.getAction();
    Log.d(Settings.LOG_ID,"ReceiverSafeVolume.onReceive "+action);
    if (action.equals(ACC_ACTION))
    {
      String accState = intent.getStringExtra(ACC_STATE);
      if (accState.equals(ACC_OFF))
        // выключение магнитолы
        setSafeVolume(accState, context);
    }
    else
      // включение магнитолы
      setSafeVolume("startApp", context);
  }
  
  private void setSafeVolume(String accState, Context context)
  {
    Settings settings = Settings.get(context);
    //
    int volume = settings.getVolume();
    int safeVolume = settings.getSafeVolume();
    if (volume > safeVolume)
    {
      // устанавливаем безопасную громкость
      Log.d(Settings.LOG_ID,"set safevolume="+safeVolume);
      settings.setVolume(safeVolume);
      settings.showSafeVolumeToast(context.getString(R.string.toast_safevolume_set));
    }
  }
}
