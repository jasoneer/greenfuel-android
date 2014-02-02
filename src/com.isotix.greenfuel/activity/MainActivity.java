package com.isotix.greenfuel.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;
import com.isotix.greenfuel.dialog.DialogStack;
import com.isotix.greenfuel.dialog.LoginDialog;
import com.isotix.greenfuel.dialog.SearchDialog;
import com.isotix.greenfuel.dialog.StationInfoDialog;
import com.isotix.greenfuel.model.Search;
import com.isotix.greenfuel.model.Station;
import com.isotix.greenfuel.utility.PositionListener;

public class MainActivity extends Activity implements View.OnClickListener, PositionListener
{
	private MapView mapView;
    private GoogleMap googleMap;

	private Dialog loginDialog;
	private Dialog searchDialog;

    public GoogleMap getGoogleMap() { return googleMap; }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		App.CurrentActivity = this;

		// Layout and Buttons
		setContentView(R.layout.activity_main);
		//findViewById(R.id.logo).setOnClickListener(this);
		findViewById(R.id.user).setOnClickListener(this);
		findViewById(R.id.search).setOnClickListener(this);
		findViewById(R.id.manage).setOnClickListener(this);
		findViewById(R.id.settings).setOnClickListener(this);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

		// MapView
		mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();

        int mapType = GoogleMap.MAP_TYPE_NORMAL;
        String mapTypePref = App.SharedPrefs.getString("map_view", "Normal");
        if(mapTypePref.equals("Hybrid")) {
            mapType = GoogleMap.MAP_TYPE_HYBRID;
        } else if(mapTypePref.equals("Terrain")) {
            mapType = GoogleMap.MAP_TYPE_TERRAIN;
        } else if(mapTypePref.equals("Satellite")) {
            mapType = GoogleMap.MAP_TYPE_SATELLITE;
        }

        googleMap.setMapType(mapType);
        googleMap.setTrafficEnabled(App.SharedPrefs.getBoolean("map_traffic", false));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);

		// Dialogs
		loginDialog = new LoginDialog(this);
		searchDialog = new SearchDialog(this);

		// Center Map
		App.Position.lastKnownLocation(this);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        DialogStack.Clear();
        mapView.onDestroy();
        App.CurrentActivity = null;
    }
    
	@Override
    public void onPause()
	{
		super.onPause();
        mapView.onPause();
		//App.Position.disable();
	}
    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }

	public void openStationInfoDialog(Station station)
	{
		DialogStack.Show(new StationInfoDialog(this, station));
	}

	// Position Listeners
	public void onStatusEvent(int event)
	{
		/*switch(event)
		{
			case PositionListener.FIRST_FIX:
				Toast.makeText(this, "GPS Fix Obtained", Toast.LENGTH_SHORT).show();
			break;
			case PositionListener.STARTED:
				Toast.makeText(this, "GPS Service Started", Toast.LENGTH_SHORT).show();
			break;
			case PositionListener.STOPPED:
				Toast.makeText(this, "GPS Service Stopped", Toast.LENGTH_SHORT).show();
			break;
		}*/
	}
	public void onPositionFix(double latitude, double longitude)
	{
		if(App.Position.running()) {
			App.Position.disable();
			DialogStack.DismissProgressDialog();
			Search.SearchStations(App.SearchRadius, latitude, longitude);
			//App.Database.getStations(latitude, longitude, App.SearchRadius);
		}
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
	}

	// OnClick Listener
	public void onClick(View view)
	{
		switch(view.getId())
		{
			//case R.id.logo:
			//	startActivity(new Intent(Intent.ACTION_VIEW,
			//							 Uri.parse("http://nufuel.isotix.com/")));
			//break;
			case R.id.user:
				DialogStack.Show(loginDialog);
			break;
			case R.id.search:
				DialogStack.Show(searchDialog);
			break;
			case R.id.manage:
				startActivity(new Intent(MainActivity.this, ManageActivity.class));
			break;
			case R.id.settings:
				startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			break;
		}
	}
	
	// Options Menu
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, Menu.NONE, "UPDATE").setTitleCondensed("Update Station List");
		return true;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case 0:
				Network.UpdateStationList();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
	// End Options Menu
}
