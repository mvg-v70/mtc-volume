package com.mvgv70.mtcvolume;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.util.Log;

public class ReceiverBluetooth extends BroadcastReceiver
{
  // широковещательные события microntek bluetooth
  private static final String BLUETOOTH_ACTION = "com.microntek.bt.report";
  private static final String BLUETOOTH_STATE = "connect_state";
  private static final int BLUETOOTH_CALL_OUT = 2;
  private static final int BLUETOOTH_CALL_IN = 3;
  //широковещательные события ШТАТНОГО плеера
  private static final String MUSIC_PLAYPAUSE_NOTIFY = "com.microntek.playpausemusic";
  private static final String MUSIC_PLAYPAUSE_STATE = "playstate";
  private static final String MUSIC_PLAY_ACTION = "com.microntek.playmusic";
  // PLAY/PAUSE constants
  private static final int MUSIC_NONE = 0;
  private static final int MUSIC_PLAY = 1;
  @SuppressWarnings("unused")
  private static final int MUSIC_PAUSE = 2;
  private static int MusicPlayState = MUSIC_NONE;
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
    // перехват звонков выключен в настройках
    if (!Settings.get(context).getCallsEnable()) return;
    String action = intent.getAction();
    if (action.equals(BLUETOOTH_ACTION)) {
      int state = intent.getIntExtra(BLUETOOTH_STATE,-1);
      if ((state == BLUETOOTH_CALL_IN) | (state == BLUETOOTH_CALL_OUT)) {
        // входящий или исходящий звонок
        if (isPlaying()) {
          // поставим на паузу, если плеер включен
          switchPlayPause(context);
          Settings.get(context).showCallsToast(context.getString(R.string.toast_player_paused));
        }
      }
    }
    else if (action.equals(MUSIC_PLAYPAUSE_NOTIFY)) {
      int playstate = intent.getIntExtra(MUSIC_PLAYPAUSE_STATE,0);
      Log.d(Settings.LOG_ID, "ReceiverMicrontek.playstate = "+playstate);
      // запомним текущее состояние плеера
      setPlayState(playstate);
    }
  } 
  
  public void setPlayState(int newState)
  {
    MusicPlayState = newState;
  }
  
  public boolean isPlaying()
  {
    return (MusicPlayState == MUSIC_PLAY);
  }  
  
  public void switchPlayPause(Context cntx)
  {
    Log.d(Settings.LOG_ID, "ReceiverMicrontek.switchPlayPause");
    Intent intent = new Intent(MUSIC_PLAY_ACTION);
    cntx.sendBroadcast(intent);
  } 

}