package com.isotix.greenfuel.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_settings);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key)
    {
        if(key.equals("map_view")) {
            String mapType = sharedPrefs.getString(key, "");
            if(mapType.equals("Street")) {
            		App.CurrentActivity.getMapView().setSatellite(false);
            } else if(mapType.equals("Satellite")) {
            		App.CurrentActivity.getMapView().setSatellite(true);
            }
        } else if(key.equals("map_traffic")) {
            App.CurrentActivity.getMapView().setTraffic(sharedPrefs.getBoolean(key, false));
        }
//        else if(key.equals("map_street")) {
//            App.CurrentActivity.getMapView().setStreetView(sharedPrefs.getBoolean(key, false));
//        }
    }

	@Override
    protected void onPause()
    {
        super.onPause();          
        App.SharedPrefs.unregisterOnSharedPreferenceChangeListener(this);    
    }
    @Override
    protected void onResume()
    {
        super.onResume();           
        App.SharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }
}
