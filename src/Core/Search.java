package com.isotix.nufuel;

import android.content.Intent;

import android.widget.Toast;

import java.util.ArrayList;

public class Search
{
	public final static ArrayList<Station> Stations = new ArrayList<Station>();
	public final static ArrayList<double[]> RouteFixes = new ArrayList<double[]>();
	
	public static void SearchStations(double radius, double latitude, double longitude)
	{
		final double _radius = radius;
		final double _lat = latitude;
		final double _lon = longitude;
	
		Intent intent = MainService.GetIntent(new Runnable() {
			@Override
			public void run() {
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading",
													   "Searching for stations..",
													   null);
					}
				});

				int type = Integer.parseInt(App.SharedPrefs.getString("station_type", "0"));
				JSON response = null;
				if(type == 0)
					 response = Network.SendRequest(BuildSearchRadiusRequest(_radius, _lat, _lon));
				else
					response = Network.SendRequest(BuildSearchRadiusRequest(_radius, _lat, _lon,
																			type));
				if(HandleStationInfo(response)) {
					synchronized(RouteFixes) {
						RouteFixes.clear();
					}
					Toast.makeText(App.Current, "Stations Retrieved", Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(App.Current, "Station Search Failed", Toast.LENGTH_SHORT).show();
				
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						App.CurrentActivity.findViewById(R.id.main).invalidate();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}
	
	public static void SearchStations(double radius,
									  double slat, double slon,
									  double elat, double elon)
	{
		final double _radius = radius;
		final double _slat = slat;
		final double _slon = slon;
		final double _elat = elat;
		final double _elon = elon;
	
		Intent intent = MainService.GetIntent(new Runnable() {
			@Override
			public void run() {
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.ShowProgressDialog(App.CurrentActivity,
													   "Loading",
													   "Retrieving Route..",
													   null);
					}
				});

				JSON response = Network.SendRequestURL(BuildRouteRequestURL(_slat, _slon,
																		   _elat, _elon));
				if(response != null &&
				   "OK".equals(((JSON.String)((JSON.Object)response).get("status")).value())) {
					Toast.makeText(App.Current, "Route Retrieved", Toast.LENGTH_SHORT).show();
					System.out.println(response);
					
					/*JSON.Array jsonArray = (JSON.Array)((JSON.Object)response).get("routes");
					jsonArray = (JSON.Array)((JSON.Object)jsonArray.get(0)).get("legs");
					jsonArray = (JSON.Array)((JSON.Object)jsonArray.get(0)).get("steps");
					ArrayList<JSON> jsonSteps = jsonArray.list();
					
					StringBuilder builder = new StringBuilder();
					for(JSON jsonStep : jsonSteps) {
						JSON.Object polyline = (JSON.Object)((JSON.Object)jsonStep).get("polyline");
						JSON.String points = (JSON.String)polyline.get("points");
						builder.append(points.value());
					}
					String polyline = builder.toString();*/

					JSON.Object jsonObject = (JSON.Object)response;
					JSON.Array jsonArray = (JSON.Array)jsonObject.get("routes");
					JSON.Object jsonRoute = (JSON.Object)jsonArray.get(0);
					JSON.Object jsonOverview = (JSON.Object)jsonRoute.get("overview_polyline");
					JSON.String jsonPolyline = (JSON.String)jsonOverview.get("points");

					String polyline = jsonPolyline.value();
					synchronized(RouteFixes) {
						RouteFixes.clear();
						DecodeFixes(polyline);
					}

					int type = Integer.parseInt(App.SharedPrefs.getString("station_type", "0"));
					if(type == 0)
						response = Network.SendRequest(BuildSearchRouteRequest(_radius, polyline));
					else
						response = Network.SendRequest(BuildSearchRouteRequest(_radius, polyline,
																			   type));
					if(HandleStationInfo(response))
						Toast.makeText(App.Current, "Stations Retrieved", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(App.Current, "Station Search Failed", Toast.LENGTH_SHORT).show();
				} else Toast.makeText(App.Current, "Route Search Failed", Toast.LENGTH_SHORT).show();
				
				App.CurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						DialogStack.DismissProgressDialog();
						App.CurrentActivity.findViewById(R.id.main).invalidate();
					}
				});
			}
		});
		App.CurrentActivity.startService(intent);
	}

	private static boolean HandleStationInfo(JSON response)
	{
		if(response == null)
			return false;

		System.out.println(response);
					
		response = Network.SendRequest(BuildStationInfoRequest((JSON.Array)response));
		if(response == null)
			return false;

		System.out.println(response);

		ArrayList<JSON> jsonStations = ((JSON.Array)response).list();
		Stations.clear();
		for(JSON jsonStation : jsonStations)
			Stations.add(new Station(jsonStation));
		App.CurrentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				App.OverlayStation.buildOverlayItems();
			}
		});
		return true;
	}

	private static void DecodeFixes(String polyline)
	{
		System.out.println("Polyline: " + polyline);

		int lat = 0;
		int lon = 0;
		boolean stat = false;

		int ansi = 0;
		int mask = 0;
		int bits = 0;

		int length = polyline.length();
		for(int index = 0; index < length; ++index)
		{
			ansi = polyline.charAt(index) - 63;
			mask |= (ansi & 31) << bits;
			bits += 5;

			if(ansi >= 32) continue;

			int latlon = ((mask & 1) != 0) ? ~(mask >> 1) : mask >> 1;

			if(stat) {
				lon += latlon;
				RouteFixes.add(new double[] { lat * 1.0E-5, lon * 1.0E-5 });
				stat = false;
			} else {
				lat += latlon;
				stat = true;
			}
		
		    mask = 0;
		    bits = 0;
		}
	}

	/*
	 * Request Strings
	 */
	private static String BuildRouteRequestURL(double slat, double slon, double elat, double elon)
	{
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("http://maps.googleapis.com/maps/api/directions/json?sensor=false&region=us&");
		builder.append("origin=").append(slat).append(',').append(slon).append('&');
		builder.append("destination=").append(elat).append(',').append(elon);
		
		return builder.toString();
	}
	
	private static String BuildSearchRouteRequest(double radius, String route)
	{
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_searchRoute").append('&');
		builder.append("radius=").append(radius).append('&');
		builder.append("route=").append(Network.Encode(route));
		
		return builder.toString();
	}
	private static String BuildSearchRouteRequest(double radius, String route, int type)
	{
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_searchRoute").append('&');
		builder.append("radius=").append(radius).append('&');
		builder.append("type=").append(type).append('&');
		builder.append("route=").append(Network.Encode(route));
		
		return builder.toString();
	}
	
	private static String BuildSearchRadiusRequest(double radius, double lat, double lon)
	{
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_searchRadius").append('&');
		builder.append("rad=").append(radius).append('&');
		builder.append("lat=").append(lat).append('&');
		builder.append("lon=").append(lon);
		
		return builder.toString();
	}
	private static String BuildSearchRadiusRequest(double radius, double lat, double lon, int type)
	{
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_searchRadius").append('&');
		builder.append("rad=").append(radius).append('&');
		builder.append("lat=").append(lat).append('&');
		builder.append("lon=").append(lon).append('&');
		builder.append("type=").append(type);
		
		return builder.toString();
	}
	
	private static String BuildStationInfoRequest(JSON.Array idList)
	{
		if(idList == null) return null;
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_info").append('&');
		builder.append("ids=").append(Network.Encode(idList.toString()));
		
		return builder.toString();
	}
	/*private static String BuildStationInfoRequest(int[] idList)
	{ geo fix -121.740517 38.544907
		if(idList == null) return null;
		StringBuilder builder = Network.GetStringBuilder();
		
		builder.append("cmd=station_info").append('&');
		builder.append("ids=").append(Network.Encode("["));
		for(int id : idList)
			builder.append(Network.Encode(Integer.toString(id))).append(Network.Encode(","));
		builder.deleteCharAt(builder.length()-Network.Encode(",").length());
		builder.append(Network.Encode("]"));
		
		return builder.toString();
	}*/
}
