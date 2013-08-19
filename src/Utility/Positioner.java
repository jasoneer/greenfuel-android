package com.isotix.nufuel;

import java.util.List;

import android.os.Bundle;

import android.content.Intent;
import android.content.Context;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.location.Address;
import android.location.Geocoder;

import android.widget.Toast;

public class Positioner implements LocationListener,
								   GpsStatus.Listener
{
	private LocationManager manager;
	private PositionListener _listener;

	private Geocoder geocoder;

	private boolean running;
	public boolean running() { return running; }
	
	public Positioner()
	{
		manager = (LocationManager)App.Current.getSystemService(Context.LOCATION_SERVICE);
		geocoder = new Geocoder(App.Current);
	}

	public void startGeocoding(String address, PositionListener listener)
	{
		final String addressText = address;
		final PositionListener posListener = listener;
		Intent intent = MainService.GetIntent(new Runnable() {
			private double[] geoFix;
			@Override
			public void run()
			{
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading", "Geocoding..", null);
					}
				});
				try {
					List<Address> addressList = geocoder.getFromLocationName(addressText, 1);
					if(addressList != null && !addressList.isEmpty())
						geoFix = new double[] { addressList.get(0).getLatitude(),
												addressList.get(0).getLongitude() };
					else geoFix = null;
				} catch(Exception ex) { ex.printStackTrace(); geoFix = null; }
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						if(geoFix != null) posListener.onPositionFix(geoFix[0], geoFix[1]);
						else Toast.makeText(App.Current,
											"Geocoding Failed",
											Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}
	public void startGeocoding(String begAddress, String endAddress, PositionListener listener)
	{
		final String begAddressText = begAddress;
		final String endAddressText = endAddress;
		final PositionListener posListener = listener;
		Intent intent = MainService.GetIntent(new Runnable() {
			private double[] bGeoFix, eGeoFix;
			@Override
			public void run()
			{
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading", "Geocoding..", null);
					}
				});
				try {
					List<Address> begAddressList = geocoder.getFromLocationName(begAddressText, 1);
					List<Address> endAddressList = geocoder.getFromLocationName(endAddressText, 1);
					if(begAddressList != null && !begAddressList.isEmpty() &&
					   endAddressList != null && !endAddressList.isEmpty()) {
						bGeoFix = new double[] { begAddressList.get(0).getLatitude(),
												 begAddressList.get(0).getLongitude() };
						eGeoFix = new double[] { endAddressList.get(0).getLatitude(),
												 endAddressList.get(0).getLongitude() };
					} else bGeoFix = eGeoFix = null;
				} catch(Exception ex) { ex.printStackTrace(); bGeoFix = eGeoFix = null; }
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						if(bGeoFix != null && eGeoFix != null) {
							posListener.onPositionFix(bGeoFix[0], bGeoFix[1]);
							posListener.onPositionFix(eGeoFix[0], eGeoFix[1]);
						} else Toast.makeText(App.Current,
											  "Geocoding Failed",
											  Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}
	
	public void enable(PositionListener listener)
	{
		running = true;
		_listener = listener;
		manager.addGpsStatusListener(this);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	public void disable()
	{
		running = false;
		_listener = null;
		manager.removeGpsStatusListener(this);
		manager.removeUpdates(this);
	}
	
	public void lastKnownLocation(PositionListener listener)
	{
		if(listener == null) return;
		
		// LocationManager.PASSIVE_PROVIDER
		Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(location != null) listener.onPositionFix(location.getLatitude(),
													location.getLongitude());
		listener = null;
	}
	
	public void onLocationChanged(Location location)
	{
		if(_listener != null)
			_listener.onPositionFix(location.getLatitude(),
									location.getLongitude());
	}
	public void onGpsStatusChanged(int event)
	{
		if(_listener != null)
			switch(event)
			{
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					_listener.onStatusEvent(PositionListener.FIRST_FIX);
				break;
				case GpsStatus.GPS_EVENT_STARTED:
					_listener.onStatusEvent(PositionListener.STARTED);
				break;
				case GpsStatus.GPS_EVENT_STOPPED:
					_listener.onStatusEvent(PositionListener.STOPPED);
				break;
			}
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	public void onProviderEnabled(String provider) {}
	public void onProviderDisabled(String provider) {}
}
