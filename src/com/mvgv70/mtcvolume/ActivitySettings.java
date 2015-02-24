package com.mvgv70.mtcvolume;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import android.preference.Preference;
import android.preference.EditTextPreference;
import android.content.Context;

public class ActivitySettings extends PreferenceActivity {
	
  @Override
  protected void onCreate(final Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    getFragmentManager().beginTransaction().replace(android.R.id.content, new preferenceFragment()).commit();
  }
  
  @Override
  public void onDestroy()
  {
    // массив со скорост€ми необходимо пересобрать из настроек
    Settings.get(this).clearSpeedValues();
    // TODO: изменить текущую €ркость
    super.onDestroy();
  }
  
  public class preferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
  {
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.layout.pref);
      // в поле со списком скоростей можно вводить только цифры, зап€тую и пробел
      EditTextPreference speedRange = (EditTextPreference )findPreference("speed.speedrange");
      speedRange.setOnPreferenceChangeListener(this);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
      String str = (String)newValue;
      Context context = preference.getContext();
      // строка может содержать только цифры, зап€тую и пробел
      if (str.matches("[0-9, ]+"))
        return true;
      else
      {
        Toast.makeText(context, context.getString(R.string.ui_adv_settings_incorrect_text)+" "+preference.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
      }
    }
  };
  
}