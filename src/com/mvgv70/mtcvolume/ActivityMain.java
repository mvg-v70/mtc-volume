package com.mvgv70.mtcvolume;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityMain extends Activity implements OnClickListener {
	
  private static ActivityMain instance;
  private String mcuVersion = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Switch chService = (Switch)findViewById(R.id.swServiceEnable);
    chService.setChecked(Settings.get(this).getServiceEnable());
    // нужно ли запускать сервис
    if (Settings.get(this).getServiceEnable() && !ServiceMain.isRunning)
    {
      startService(new Intent(this, ServiceMain.class));
    } 
    TextView txtVersion = (TextView)findViewById(R.id.txtVersion);
    txtVersion.setText(getString(R.string.ui_version_title) + " " + Settings.get(this).getVersion());
    mcuVersion = Settings.get(this).getMcuVersion();
    // mcu version
    Log.d(Settings.LOG_ID,mcuVersion);
    if (!mcuVersion.startsWith("MTCB-"))
      Log.e(Settings.LOG_ID,"incorrect MCU version "+mcuVersion);
    // OK
    instance = this;
  }
    
  @Override
  public void onClick(View v) 
  {
    switch (v.getId()) {
    case R.id.swServiceEnable:
      // включение/выключение сервиса
      Settings.get(this).setServiceEnable(((Switch)findViewById(R.id.swServiceEnable)).isChecked());
      setService(((Switch)findViewById(R.id.swServiceEnable)).isChecked());
      break;
    case R.id.btnSettings:
      // вызов настроек
      Intent intent = new Intent(this, ActivitySettings.class);
      startActivity(intent); 
      break;
    }
  }
    
  @Override
	public void onDestroy() 
  {
    instance = null;
    super.onDestroy();
  }
    
  public static ActivityMain getInstance() 
  {
    return instance;
  }
    
  public void setService(boolean state)
  {
    // остановка сервиса
    stopService(new Intent(this, ServiceMain.class));		
    if (state)
  	  // старт сервиса
      startService(new Intent(this, ServiceMain.class));
  } 
		
}
