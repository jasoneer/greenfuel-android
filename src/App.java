package com.isotix.nufuel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class App extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		Current = this;
		Database = new Databaser();
		Position = new Positioner();

		OverlayRoute = new RouteOverlay();
		OverlayStation = new StationOverlay();

		SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		startService(MainService.GetIntent(null));
	}

	public static App Current;
	public static MainService CurrentService;
	public static MainActivity CurrentActivity;
	
	public static User CurrentUser;
	
	public static Databaser  Database;
	public static Positioner Position;

	public static RouteOverlay OverlayRoute;
	public static StationOverlay OverlayStation;

	public static SharedPreferences SharedPrefs;
	
	public static double SearchRadius;
}
