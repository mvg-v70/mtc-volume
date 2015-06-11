package com.mvgv70.mtcvolume;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Switch;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ActivityMain extends Activity implements OnClickListener {
	
  private Switch swService;
	
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //
    swService = (Switch)findViewById(R.id.swServiceEnable);
    swService.setChecked(Settings.get(this).getServiceEnable());
    // нужно ли запускать сервис
    if (Settings.get(this).getServiceEnable() && !ServiceMain.isRunning)
      startService(new Intent(this, ServiceMain.class));
    TextView txtVersion = (TextView)findViewById(R.id.txtVersion);
    txtVersion.setText(getString(R.string.ui_version_title) + " " + Settings.get(this).getVersion());
  }
    
  @Override
  public void onClick(View v) 
  {
    switch (v.getId()) {
    case R.id.swServiceEnable:
      // включение/выключение сервиса
      Settings.get(this).setServiceEnable(swService.isChecked());
      setService(swService.isChecked());
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
    super.onDestroy();
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
