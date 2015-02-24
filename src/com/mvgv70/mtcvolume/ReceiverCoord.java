package com.mvgv70.mtcvolume;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
// import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Timer;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class ReceiverCoord extends BroadcastReceiver {
	
  private static Timer timerSunrise = new Timer();
  private static Timer timerSunset = new Timer();
  private static TimerBrightness taskSunrise = new TimerBrightness();
  private static TimerBrightness taskSunset = new TimerBrightness();
  
  // остановка таймеров
  public static void cancelTimers()
  {
    timerSunrise.cancel();
    timerSunset.cancel();
  }
	
  @Override
  public void onReceive(Context context, Intent intent)
  {
    Log.d(Settings.LOG_ID, "ReceiverCoord.onReceive");
    Settings settings = Settings.get(context);
    if (!intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) return;
    Location location = (Location)intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
    // изменение яркости включено
    if (!settings.getBrightnessEnable()) return;
    // определим время рассвета и заката
    com.luckycatlabs.sunrisesunset.dto.Location gpslocation = new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude());
   	SunriseSunsetCalculator calc = new SunriseSunsetCalculator(gpslocation,TimeZone.getDefault());
   	String sunrise = calc.getOfficialSunriseForDate(Calendar.getInstance());
   	String sunset = calc.getOfficialSunsetForDate(Calendar.getInstance());
   	Log.d(Settings.LOG_ID, "sunrise="+sunrise+", sunset="+sunset);
   	// сохраним время рассвета и заката
   	saveSunriseSunset(settings, sunrise, sunset);
   	// изменим текущую яркость
   	settings.setTimeBrightness();
   	// установим таймеры восхода и захода
    Date sunriseDate = settings.getEventDate("sunrise");
    // !!!
    // GregorianCalendar gc = new GregorianCalendar(2015,1,19,17,30); 
    // sunriseDate = gc.getTime();
    // !!!
    if (sunriseDate != null)
    {
      taskSunrise.setParams("sunrise",settings);
      timerSunrise.schedule(taskSunrise, sunriseDate, AlarmManager.INTERVAL_DAY);
      Log.d(Settings.LOG_ID,"set sunrise timer at "+DateFormat.getInstance().format(sunriseDate));
    }
    Date sunsetDate = settings.getEventDate("sunset");
    if (sunsetDate != null)
    {
      taskSunset.setParams("sunset",settings);
      timerSunset.schedule(taskSunset, sunsetDate, AlarmManager.INTERVAL_DAY);
      Log.d(Settings.LOG_ID,"set sunset timer at "+DateFormat.getInstance().format(sunsetDate));
    }
  }
	  
  private void saveSunriseSunset(Settings settings, String sunrise, String sunset)
  {
    // определим время восхода и захода солнца из строк
    int sunriseHour = settings.parseString(sunrise.substring(0,2),-1);
    int sunriseMin = settings.parseString(sunrise.substring(3,5),-1);
    int sunsetHour = settings.parseString(sunset.substring(0,2),-1);
    int sunsetMin = settings.parseString(sunset.substring(3,5),-1);
    // сохраним
    settings.setSunsetSunrise(sunriseHour,sunriseMin,sunsetHour,sunsetMin);
  }

}
