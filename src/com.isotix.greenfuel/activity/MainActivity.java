package com.isotix.greenfuel.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import com.isotix.greenfuel.App;
import com.isotix.greenfuel.R;
import com.isotix.greenfuel.dialog.DialogStack;
import com.isotix.greenfuel.dialog.LoginDialog;
import com.isotix.greenfuel.dialog.SearchDialog;
import com.isotix.greenfuel.dialog.StationInfoDialog;
import com.isotix.greenfuel.model.Search;
import com.isotix.greenfuel.model.Station;
import com.isotix.greenfuel.utility.PositionListener;

public class MainActivity extends MapActivity implements View.OnClickListener, PositionListener
{
	private MapView mapView;
	private MapController mapCtrl;

	private Dialog loginDialog;
	private Dialog searchDialog;

	public Projection getProjection() { return mapView.getProjection(); }
	public MapView getMapView() { return mapView; }

    @Override
    protected void onCreate(Bundle savedInstanceState)
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

		// MapView
		mapView = (MapView)findViewById(R.id.mapview);
//		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(App.SharedPrefs.getString("map_view", "").equals("Satellite") ? true : false);
//		mapView.setStreetView(App.SharedPrefs.getBoolean("map_street", false));
		mapView.setTraffic(App.SharedPrefs.getBoolean("map_traffic", false));
		mapCtrl = mapView.getController();
		mapCtrl.setZoom(15);
		
		mapView.getOverlays().add(App.OverlayRoute);
		mapView.getOverlays().add(App.OverlayStation);

		// Dialogs
		loginDialog = new LoginDialog(this);
		searchDialog = new SearchDialog(this);

		// Center Map
		App.Position.lastKnownLocation(this);
    }
	@Override
    protected boolean isRouteDisplayed() { return false; }
    
	@Override
    protected void onPause()
	{
		super.onPause();
		//App.Position.disable();
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		DialogStack.Clear();
		App.CurrentActivity = null;
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
		mapCtrl.animateTo(new GeoPoint((int)(latitude * 1E6),
								   	   (int)(longitude * 1E6)));
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
