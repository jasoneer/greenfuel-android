package com.isotix.nufuel;

import android.database.Cursor;

public class Station
{
	public final static int BIO = 19;
	public final static int ELE = 20;
	public final static int ETH = 21;
	public final static int HYD = 22;
	public final static int CNG = 23;
	public final static int LNG = 24;
	public final static int PRO = 25;

	public final static int PUBLIC = 8;
	public final static int PRIVATE = 9;
	public final static int CLOSED = 10;

	private int _id;
	private double _latitude,
				   _longitude;
	private int _type, _status;
	private String _data, _time;
	
	public Station(int id, double latitude, double longitude,
				   int type, int status,
				   String time, String data)
	{
		_id = id;
		_latitude = latitude;
		_longitude= longitude;
		_type = type;
		_status = status;
		_data = data;
		_time = time;
	}
	
	public Station(JSON json)
	{
		JSON.Object jsonObject = (JSON.Object)json;
		_id = (int)((JSON.Number)jsonObject.get("id")).value();
		_latitude = (double)((JSON.Number)jsonObject.get("lat")).value();
		_longitude = (double)((JSON.Number)jsonObject.get("lon")).value();
		_type = (int)((JSON.Number)jsonObject.get("type")).value();
		_status = (int)((JSON.Number)jsonObject.get("status")).value();
		_data = ((JSON.String)jsonObject.get("data")).value();
		_time = ((JSON.String)jsonObject.get("time")).value();

		/*System.out.println("Station");
		System.out.println(_id);
		System.out.println(_latitude);
		System.out.println(_longitude);
		System.out.println(_type);
		System.out.println(_status);
		System.out.println(_data);
		System.out.println(_time);*/
	}
	
	public Station(Cursor cursor)
	{
		this(cursor.getInt(cursor.getColumnIndex("id")),
			 cursor.getDouble(cursor.getColumnIndex("lat")),
			 cursor.getDouble(cursor.getColumnIndex("lon")),
			 cursor.getInt(cursor.getColumnIndex("type")),
			 cursor.getInt(cursor.getColumnIndex("status")),
			 cursor.getString(cursor.getColumnIndex("data")),
			 cursor.getString(cursor.getColumnIndex("time")));
	}
	
	public int id() { return _id; }
	
	public double latitude() { return _latitude; }
	public double longitude() { return _longitude; }
	public int type() { return _type; }
	public int status() { return _status; }
	public String data() { return _data; }
	public String time() { return _time; }

	public void latitude(double latitude) { _latitude = latitude; }
	public void longitude(double longitude) { _longitude = longitude; }
	public void type(int type) { _type = type; }
	public void status(int status) { _status = status; }
	public void data(String data) { _data = data; }
	public void time(String time) { _time = time; }
}
