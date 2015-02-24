package com.mvgv70.mtcvolume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReceiverToast extends BroadcastReceiver {
	
  @Override
  public void onReceive(Context context, Intent intent)
  {
    String text = intent.getStringExtra("text");
    Toast.makeText(Settings.getContext(), text, Toast.LENGTH_SHORT).show();
  }

}
