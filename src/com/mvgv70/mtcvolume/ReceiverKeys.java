package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverKeys extends BroadcastReceiver {
	
  private static final int PAUSEPLAY_KEY = 3;

  @Override
  public void onReceive(Context context, Intent intent)
  {
    Settings settings = Settings.get(context);
    // включение паузы выключено в настройках
    if (!settings.getPauseMuteEnable()) return;
    int keyCode = intent.getIntExtra("keyCode",-1);
    if (keyCode == PAUSEPLAY_KEY)
    {
      // если не запущен плеер
      if (!ReceiverBluetooth.isPlayerRun())
      {
        boolean muteState = settings.getMute();
        // переключаем режим паузы
        muteState = !muteState;
        Log.d(Settings.LOG_ID,"setMute("+muteState+")");
        settings.setMute(muteState);
        if (muteState)
          settings.showPauseMuteToast(context.getString(R.string.toast_mute_on));
        else
          settings.showPauseMuteToast(context.getString(R.string.toast_mute_off));
      }
      else
        Log.d(Settings.LOG_ID,"audio player running");
    }
  }

}