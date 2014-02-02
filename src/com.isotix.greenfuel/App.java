package com.isotix.greenfuel;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.isotix.greenfuel.activity.MainActivity;
import com.isotix.greenfuel.model.User;
import com.isotix.greenfuel.utility.Databaser;
import com.isotix.greenfuel.utility.Positioner;

public class App extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Current = this;
		Database = new Databaser();
		Position = new Positioner();

		SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		startService(MainService.GetIntent(null));
	}

	public static App Current;
	public static MainService CurrentService;
	public static MainActivity CurrentActivity;
	
	public static User CurrentUser;
	
	public static Databaser  Database;
	public static Positioner Position;

	public static SharedPreferences SharedPrefs;
	
	public static double SearchRadius;
}
