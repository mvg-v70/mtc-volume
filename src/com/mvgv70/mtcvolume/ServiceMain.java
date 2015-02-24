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
  // ��� ����������� ��������
  private static Intent si;
  private static PendingIntent spi;
  // ��� ����������� ���������
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
    // context & settings
    Context context = this;
    Settings settings = Settings.get(context);
    Log.d(Settings.LOG_ID, "ServiceMain: start service");
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
      // ����������� ��������
      si = new Intent(context, ReceiverSpeed.class);
      spi = PendingIntent.getBroadcast(context, 0, si, PendingIntent.FLAG_UPDATE_CURRENT);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, spi);
      // ����������� ����������� ���������
      li = new Intent(context, ReceiverCoord.class);
      lpi = PendingIntent.getBroadcast(context, 0, li, PendingIntent.FLAG_UPDATE_CURRENT);
      locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, lpi);
    }
    else
      Log.e(Settings.LOG_ID,"locationManager not found!");
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
      // ������ ��������� �������
      locationManager.removeUpdates(spi);
      locationManager.removeUpdates(lpi);
    }
    // ������ ������� �������
    ReceiverCoord.cancelTimers();
    super.onDestroy();
  }
  
}