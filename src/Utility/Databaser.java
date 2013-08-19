package com.isotix.nufuel;

import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databaser extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "afstations";
	
	private static final double RADIUS = 6371d;
	
	public String getLastUpdateTime()
	{
		return null;
	}
	
	public void getStations(double rad, double lat, double lon)
	{
		if(rad == 0.0) return;
	
		double angRad = rad / RADIUS;
		double calc1 = Math.toDegrees(angRad);
		// compensate for longitude getting smaller with increasing latitude
		double calc2 = Math.toDegrees(angRad / Math.cos(Math.toRadians(lat)));
	
		double minLat = lat - calc1;
		double minLon = lon - calc2;
		double maxLat = lat + calc1;	
		double maxLon = lon + calc2;
		
		getStations(minLat, minLon, maxLat, maxLon);
	}
	public void getStations(double minLat, double minLon,
							double maxLat, double maxLon)
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor =
		db.query("stations", null,
			     "(latitude  BETWEEN " + minLat + " AND " + maxLat + ") AND " +
				 "(longitude BETWEEN " + minLon + " AND " + maxLon + ")",
				 null, null, null, null);
		
		if(cursor.getCount() == 0)
			return;
		
		Search.Stations.clear();
		Search.Stations.ensureCapacity(cursor.getCount());

		synchronized(Search.Stations) {
			//cursor.moveToFirst();
			for(int i=0; cursor.moveToNext(); ++i)
				Search.Stations.add(new Station(cursor));
		}
	}
	
	public Station[] getUserStations(String type)
	{
		Station[] stations = new Station[1];
		
		/*stations[0] = new Station(0, 10.23, 5.23, "2011-12-10 12:23:00", "Primary", "First", "Desc");
		stations[1] = new Station(0, 10.23, 5.23, "2011-12-10 12:23:00", "Primary", "First", "Desc");
		stations[2] = new Station(0, 10.23, 5.23, "2011-12-10 12:23:00", "Primary", "First", "Desc");
		stations[3] = new Station(0, 10.23, 5.23, "2011-12-10 12:23:00", "Primary", "First", "Desc");
		stations[4] = new Station(0, 10.23, 5.23, "2011-12-10 12:23:00", "Primary", "First", "Desc");*/
		
		return stations;
	}

	// Station Database Methods
	public void updateStation(Station station)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("UPDATE stations SET latitude = "  + station.latitude()  + ", " + 
									   "longitude = " + station.longitude() + ", " +
									   "type = "      + station.type()      + ", " +
									   "status = "    + station.status()    + ", " +
									   "data = '"     + station.data()      + "', " +
									   "time = '"     + station.time()      + "' " +
								   "WHERE id = " + station.id());
	}
	public void insertStation(Station station)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO stations(id, latitude, longitude, type, status, data, time)" +
				   "VALUES(" + station.id()        + ", "
							 + station.latitude()  + ", "  
							 + station.longitude() + ", "
							 + station.type()      + ", "
							 + station.status()    + ", '"
							 + station.data()      + "', '"
							 + station.time()      + "')");
	}
	
    public Databaser()
	{
        super(App.Current, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
	{
		String createStations = "CREATE TABLE stations" +
								"(id INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
								 "latitude REAL," +
								 "longitude REAL," +
								 "type INTEGER," +
								 "status INTEGER," +
								 "data TEXT," +
								 "time TEXT)";
		db.execSQL(createStations);
		db.execSQL("CREATE INDEX location ON stations(latitude, longitude)");
		db.execSQL("CREATE INDEX category ON stations(type)");
		db.execSQL("CREATE INDEX position ON stations(status)");
		db.execSQL("CREATE INDEX datetime ON stations(time DESC)");
    }
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		
	}
}
