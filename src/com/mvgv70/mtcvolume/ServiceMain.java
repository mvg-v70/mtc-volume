package com.mvgv70.mtcvolume;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

public class ServiceMain extends Service
{
	
  public static boolean isRunning = false;
  private String mcuVersion = "";
  // для определения скорости
  private static Intent si;
  private static PendingIntent spi;
  // для определения координат
  private static Intent li;
  private static PendingIntent lpi;
  // manager
  LocationManager locationManager;

  
  @Override
  public void onCreate() 
  {
    super.onCreate();
    isRunning = false;
  }
  
  @Override
  public IBinder onBind(Intent intent) 
  {
    return null;
  } 
  
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) 
  {
    super.onStartCommand(intent, flags, startId);
    if (isRunning) return START_STICKY;
    // context & settings
    Context context = this;
    Settings settings = Settings.get(context);
    Log.d(Settings.LOG_ID, "ServiceMain: start service");
    // mcu version
    mcuVersion = Settings.get(this).getMcuVersion();
    Log.i(Settings.LOG_ID,mcuVersion);
    if (!mcuVersion.startsWith("MTCB-"))
      Log.e(Settings.LOG_ID,"incorrect MCU version '"+mcuVersion+"'");
    // notification
    Intent ni = new Intent(this, ActivityMain.class);
    PendingIntent ci = PendingIntent.getActivity(this, 0, ni, PendingIntent.FLAG_UPDATE_CURRENT);
    Notification note = new NotificationCompat.Builder(this).setContentTitle(getString(R.string.app_service_title)+" "+settings.getVersion())
    		                                                .setContentText(getString(R.string.app_service_on))
    		                                                .setContentIntent(ci)
    		                                                .setTicker(getString(R.string.app_descr) + " " + getString(R.string.app_starting))
    		                                                .setSmallIcon(R.drawable.ic_launcher).build();
    startForeground(startId,note);
    // Location 
    locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager != null) 
    {
      // определение скорости
      si = new Intent(context, ReceiverSpeed.class);
      spi = PendingIntent.getBroadcast(context, 0, si, PendingIntent.FLAG_UPDATE_CURRENT);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, spi);
      // одноразовое определение координат
      li = new Intent(context, ReceiverCoord.class);
      lpi = PendingIntent.getBroadcast(context, 0, li, PendingIntent.FLAG_UPDATE_CURRENT);
      locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, lpi);
    }
    else
      Log.e(Settings.LOG_ID,"locationManager not found!");
    // создадим таймеры яркости 
    ReceiverCoord.createTimers();
    // изменим текущую яркость
    context.sendBroadcast(new Intent(context,ReceiverBrightness.class));
    // service OK
    isRunning = true;
    return START_STICKY;
  }
  
  @Override
  public void onDestroy() 
  {
    Log.d(Settings.LOG_ID, "ServiceMain: destroy service");
    isRunning = false;
    if (locationManager != null) 
    {
      // удалим изменения позиции
      locationManager.removeUpdates(spi);
      locationManager.removeUpdates(lpi);
    }
    // удалим таймеры яркости 
    ReceiverCoord.cancelTimers();
    super.onDestroy();
  }
  
}