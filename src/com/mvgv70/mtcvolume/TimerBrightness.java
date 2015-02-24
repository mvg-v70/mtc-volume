package com.mvgv70.mtcvolume;

import java.util.TimerTask;

import android.content.Intent;
import android.util.Log;

public class TimerBrightness extends TimerTask {
	
  private String eventName = "";
  private Settings attSettings = null;
	
  public void setParams(String name, Settings settings)
  {
    eventName = name;
    attSettings = settings;
  }

  @Override
  public void run() 
  {
    int brightness = 0;
    Log.d(Settings.LOG_ID,"run "+eventName+" timer");
    // получим €ркость из настроек
    if (eventName.equals("sunrise"))
      brightness = attSettings.getBrightnessDayLevel();
    else if (eventName.equals("sunset")) 
      brightness = attSettings.getBrightnessNightLevel();
    // установим €ркость с помощью intent
    Intent intent = new Intent(Settings.getContext(),ReceiverBrightness.class);
    intent.putExtra("brightness", brightness);
    Settings.getContext().sendBroadcast(intent);
  }
}