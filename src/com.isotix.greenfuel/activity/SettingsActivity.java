package com.isotix.greenfuel.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.google.android.gms.maps.GoogleMap;
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
            String mapType = sharedPrefs.getString(key, "Normal");
            if(mapType.equals("Normal")) {
                App.CurrentActivity.getGoogleMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if(mapType.equals("Hybrid")) {
                App.CurrentActivity.getGoogleMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if(mapType.equals("Terrain")) {
                App.CurrentActivity.getGoogleMap().setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            } else if(mapType.equals("Satellite")) {
                App.CurrentActivity.getGoogleMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        } else if(key.equals("map_traffic")) {
            App.CurrentActivity.getGoogleMap().setTrafficEnabled(sharedPrefs.getBoolean(key, false));
        }
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
