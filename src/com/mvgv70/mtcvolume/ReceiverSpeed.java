package com.mvgv70.mtcvolume;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class ReceiverSpeed extends BroadcastReceiver {
  private static double last_speed = 0;
	  
  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (!intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) return;
    Location location = (Location)intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
    Settings settings = Settings.get(context);
    // увеличение громкости включено
    if (!settings.getSpeedEnable()) return;
    // нет скорости в объекте
    if (!location.hasSpeed()) return;
    // включен режим mute
    if (settings.getMute()) return;
    // текущая громкость
    int volume = settings.getVolume(context);
    // нулевая громкость
    if (volume == 0) return;
    // текущая скорость
    double speed = location.getSpeed();
    // перевод m/s в km/h
    speed *= 3.6; 
    if ((int)speed == (int)last_speed) return;
    List<Integer> speed_steps = settings.getSpeedValues();
    // на сколько единиц менять громкость
    int volChange = settings.getSpeedChangeValue();
    // кол-во инкрементов по скорости
    int changeIncr = 0;
    // новая громкость
    int volumeNew = 0;
    // направление смены скорости/громкости
    String volumeDir = "";
    // увеличивается или уменьшается скорость		
    if (speed > last_speed) 
      volumeDir = "+";
    else
      volumeDir = "-";
    // на сколько единиц нужно увеличить громкость
    for (Integer spd_step : speed_steps)
    {
      if (last_speed >= spd_step) 
        changeIncr -= volChange;
      if (speed >= spd_step) 
        changeIncr += volChange;
    }
    // новый уровень громкости
    volumeNew = volume + changeIncr;
    if (volumeNew <= 0) volumeNew = 1;
    // коррекция громкости от скорости
    if (volumeNew != volume)
    {
      Log.d(Settings.LOG_ID,"speed="+(int)speed+", last_speed="+(int)last_speed+", changeIncr="+changeIncr+", volChange="+volChange+", volume="+volume+", volumeNew="+volumeNew);
      // меняем громкость
      settings.setVolume(volumeNew);
      settings.showSpeedToast(context.getString(R.string.toast_volume_speed_set)+" "+volumeDir+" ("+volumeNew+")");
    }
    last_speed = speed;
  }

}
