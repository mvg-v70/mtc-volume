package com.mvgv70.mtcvolume;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;
// import android.content.pm.PackageInfo;
// import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.media.AudioManager;
import android.provider.Settings.System;

import java.util.Calendar;
import java.util.Arrays;
import java.util.Date;
import java.util.List; 
import java.util.ArrayList;

public class Settings {

  // максимальный уровень громкости
  private static final float VOLUME_MAX = 30f;
  public static final String LOG_ID = "MTCVOL";
    
  private ArrayList<Integer> speedValues = new ArrayList<Integer>();
    
  private static Settings instance = null;
  private SharedPreferences prefs;
  private AudioManager am;
  private Resources res;

  private static Context ctx;

  public Settings(Context context)
  {
    ctx = context;
    prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    am = ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE));
    res = context.getResources();
  }

  public static Settings get(Context ctx) 
  {
    if (null == instance)
      instance = new Settings(ctx);
    return instance;
  }
	
  public static Context getContext()
  {
    return ctx;
  }
  
  public String getMcuVersion()
  {
    return am.getParameters("sta_mcu_version=");
  }
	
  /*
   *     всплывающие уведомления toasts
   */
	
  public void showCallsToast(String text)
  {
    if (getBoolean("calls.toast",true))
    {
      Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
    }
  }
	
  public void showSafeVolumeToast(String text)
  {
    if (getBoolean("safevolume.toast",true))
      Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
  }
	
  public void showSpeedToast(String text)
  {
    if (getBoolean("speed.toast",true))
     Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
  }
	
  public void showAppToast(String text)
  {
    if (getBoolean("apps.toast",true))
      Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
  } 
	
  public void showBrightnessToast(String text)
  {
    if (getBoolean("brightness.toast",true))
      Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
  }
	
  public String getVersion() 
  {
	/*
    String version = "?";
    try {
      PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
      version = pInfo.versionName;
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return version;
    */
	return ctx.getString(R.string.app_version);
  } 

  public static void destroy() 
  {
    instance = null;
  }
	
  /*
   *     работа с настройками, базовые функции
   */
	
  private void setBoolean(String name, boolean value)
  {
    Editor editor = prefs.edit();
    editor.putBoolean(name, value);
    editor.commit();
  }
	
  private boolean getBoolean(String name, boolean defaultValue)
  {
    return prefs.getBoolean(name, defaultValue);
  }
	
  public void setInteger(String name, int value)
  {
    Editor editor = prefs.edit();
    editor.putInt(name, value);
    editor.commit();
  }
	
  // числовая настройка, которая хранится как string
  private int getPrefInteger(String name, int defaultValue)
  {
    String value = prefs.getString(name,"");
    if (value.isEmpty())
      return defaultValue;
    else
      return Integer.valueOf(value);
  }
	
  // числовая настройка, которая хранится как number
  public int getInteger(String name, int defaultValue)
  {
    return prefs.getInt(name, defaultValue);
  }
	
  public void setString(String name, String value)
  {
    Editor editor = prefs.edit();
    editor.putString(name, value);
    editor.commit();
  }
	
  private String getString(String name, String defaultValue)
  {
    return prefs.getString(name, defaultValue);
  }
	
  /*
   *     работа с настройками, настройки приложения
   */
	
  public boolean getServiceEnable() 
  {
    return getBoolean("service.enable", true);
  }
	
  public void setServiceEnable(boolean enable) 
  {
    setBoolean("service.enable", enable);
  }
	
  public boolean getCallsEnable()
  {
    return (getServiceEnable() & getBoolean("calls.enable",true)); 
  }
	
  public boolean getSafeVolumeEnable()
  {
    return (getServiceEnable() & getBoolean("safevolume.enable",true)); 
  }
	
  public int getSafeVolume()
  {
    return getPrefInteger("safevolume.level",res.getInteger(R.integer.cfg_def_safevolume_level));
  }
	
  public boolean getSpeedEnable()
  {
    return (getServiceEnable() & getBoolean("speed.enable", true));
  }
	
  public int getSpeedChangeValue()
  {
    return getPrefInteger("speed.speedvol",1);
  }
	
  public boolean getAppsEnable()
  {
    return (getServiceEnable() & getBoolean("apps.enable",true)); 
  }
	
  public int getAppVolumeAdj(String appName)
  {
    return getPrefInteger("apps."+appName+".level",0);
  }
	
  public boolean getNavitelEnable()
  {
    return (getServiceEnable() & getBoolean("navitel.enable",true));
  }
	
  public boolean getBrightnessEnable()
  {
    return (getServiceEnable() & getBoolean("brightness.enable",true));
  }
	
  public boolean getBrightnessToastEnable()
  {
    return (getServiceEnable() & getBoolean("brightness.toast",true));
  }
	
  public int getBrightnessNightLevel()
  {
    return getPrefInteger("brightness.night",res.getInteger(R.integer.cfg_def_brightness_night_level));
  }
	
  public int getBrightnessDayLevel()
  {
    return getPrefInteger("brightness.day",res.getInteger(R.integer.cfg_def_brightness_day_level));
  }
  
  public int getBrightnessCorrection()
  {
    return getPrefInteger("brightness.correction",res.getInteger(R.integer.cfg_def_brightness_correction));
  }
	
  public Date getEventDate(String name)
  {
    // время события
    int eventHour = getInteger(name+".hour",-1);
    int eventMin = getInteger(name+".min",-1);
    // если задано время события
    if ((eventHour > 0) & (eventMin > 0))
    {
      // текущее время
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
      // время события в текущем дне 
      calendar.set(Calendar.HOUR_OF_DAY, eventHour);
      calendar.set(Calendar.MINUTE, eventMin);
      if (calendar.getTimeInMillis() < java.lang.System.currentTimeMillis())
        // если время события уже прошло добавим день
        calendar.add(Calendar.DAY_OF_YEAR, 1);
      // возвращем дату
      return calendar.getTime();
    }
    else
    {
      Log.e(LOG_ID,name+" could not get event time");
      return null;
    }
  }
	
  public void clearSpeedValues()
  {
    speedValues.clear();
  }
    
  public int parseString(String value, int defValue)
  {
    int intValue = defValue;
    try 
    {
      intValue = Integer.valueOf(value);
    } catch (Exception e) 
    {
      intValue = defValue;
    }
    return intValue;
  }
	
  public List<Integer> getSpeedValues() 
  {
    // если строка не разобрана
    if (speedValues == null || speedValues.size() <= 0) 
    {
      // разбираем строку со скоростями
      String spd_cfg = getString("speed.speedrange", ctx.getString(R.string.cfg_def_speed_values));
      // рассчитываем
      List<String> speed_vals_str = Arrays.asList(spd_cfg.split("\\s*,\\s*"));
      StringBuilder speed_vals_clr = new StringBuilder();
      for (String spd_step : speed_vals_str) 
      {
        Integer s = parseString(spd_step,-1);
        // отбрасываем некорректные значения
        if ((s > 0) & (s < 500)) 
        {
          if (speedValues.size() > 0)
            speed_vals_clr.append(", ");
          speed_vals_clr.append(s.toString());
          speedValues.add(s);
        }
      }
      // сохраняем корректные значения
      setString("speed.speedrange", speed_vals_clr.toString());
    }
    return speedValues;
  } 
	
  /*
   *     работа со звуком
   */
	
  // функция из android.microntek.service.MicrontekServer
  private int mtcGetRealVolume(int paramInt) 
  {
    float f1 = 100.0F * paramInt / VOLUME_MAX;
    float f2;
    if (f1 < 20.0F) {
      f2 = f1 * 3.0F / 2.0F;
    } else if (f1 < 50.0F) {
      f2 = f1 + 10.0F;
    } else {
      f2 = 20.0F + f1 * 4.0F / 5.0F;
    }
    return (int) f2;
  }
	
  public int getVolume(Context context) 
  {
    return android.provider.Settings.System.getInt(context.getContentResolver(), "av_volume=", 10);
  }
	  
  public void setVolume(int level) 
  {
    android.provider.Settings.System.putInt(ctx.getContentResolver(), "av_volume=", level);
    am.setParameters("av_volume=" + mtcGetRealVolume(level));
  }
	
  public boolean getMute() 
  {
    return am.getParameters("av_mute=").equals("true");
  }
    
  /*
   *     работа с яркостью экрана
   */
    
  public void setBrightness(int value)
  {
    // проверим граничные значения
    if (value <= 0) value = 10;
    if (value > 100) value = 100;
    //
    Log.d(LOG_ID,"setBrightness("+value+")");
    // рассылаем сообщение
    // Intent intent = new Intent("com.microntek.light");
    // intent.putExtra("keyCode", (int)(value*255/100));
    // ctx.sendBroadcast(intent);
    ContentResolver cResolver = ctx.getContentResolver();
    android.provider.Settings.System.putInt(cResolver, System.SCREEN_BRIGHTNESS_MODE, System.SCREEN_BRIGHTNESS_MODE_MANUAL);    
    android.provider.Settings.System.putInt(cResolver, System.SCREEN_BRIGHTNESS, Math.round(value*255/100));
  }
    
  public int getBrightness()
  {
    return Math.round(100*android.provider.Settings.System.getInt(ctx.getContentResolver(), System.SCREEN_BRIGHTNESS, 255)/255);
  }
    
  public void setSunsetSunrise(int sunriseHour, int sunriseMin, int sunsetHour, int sunsetMin)
  {
    // сохраним
    setInteger("sunrise.hour",sunriseHour);
    setInteger("sunrise.min",sunriseMin);
    setInteger("sunset.hour",sunsetHour);
    setInteger("sunset.min",sunsetMin);
  }
  
  private boolean equalBrightness(int value)
  {
    return (Math.abs(value-getBrightness()) <= 1);
  }
    
  // устанавливает яркость в соответствии с текущим временем
  public boolean setTimeBrightness()
  {
    int sunriseHour = getInteger("sunrise.hour",-1);
    int sunriseMin = getInteger("sunrise.min",-1);
    int sunsetHour = getInteger("sunset.hour",-1);
    int sunsetMin = getInteger("sunset.min",-1);
    boolean result = false;
    // если задано время восхода и захода
    if ((sunriseHour > 0) & (sunriseMin > 0) & (sunsetHour > 0) & (sunsetMin > 0))
    {
      // пересчитаем в минуты после начала суток
      long sunrise = sunriseHour*60 + sunriseMin;
      long sunset = sunsetHour*60 + sunsetMin;
      // текущее время
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(java.lang.System.currentTimeMillis());
      // дневное или вечернее время
      int curHour = calendar.get(Calendar.HOUR_OF_DAY);
      int curMin = calendar.get(Calendar.MINUTE);
      // пересчитаем в минуты текущее время
      long curTime = curHour*60 + curMin;
      // день или ночь
      int brightness;
      if ((curTime > sunrise) & (curTime <= sunset))
        // дневное время
        brightness = getBrightnessDayLevel();
      else
        // вечернее
        brightness = getBrightnessNightLevel();
      // установим яркость
      Log.d(LOG_ID,"set brightness="+brightness+", current="+getBrightness());
      if (!equalBrightness(brightness))
        result = true;
      setBrightness(brightness);
    }
    return result;
  }

}
